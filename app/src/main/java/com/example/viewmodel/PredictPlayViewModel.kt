package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LocalPreferencesRepository
import com.example.data.MockMatchesProvider
import com.example.model.BetLeg
import com.example.model.BetMode
import com.example.model.BetSlipSelection
import com.example.model.BetStatus
import com.example.model.NavDestination
import com.example.model.OddsTrend
import com.example.model.PickOutcome
import com.example.model.PlacedBet
import com.example.model.PredictPlayUiState
import com.example.model.SportCategory
import com.example.model.SportsMatch
import com.example.model.TransactionType
import com.example.model.WalletTransaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

class PredictPlayViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = LocalPreferencesRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(
        PredictPlayUiState(
            matches = MockMatchesProvider.createInitialMatches(),
            transactions = listOf(
                WalletTransaction(
                    id = "tx_init",
                    title = "Welcome Bonus",
                    subtitle = "Starting virtual simulator balance",
                    amountDelta = 1000,
                    balanceAfter = 1000,
                    type = TransactionType.INITIAL_GRANT,
                    timestampMillis = System.currentTimeMillis(),
                ),
            ),
        ),
    )
    val uiState: StateFlow<PredictPlayUiState> = _uiState.asStateFlow()

    companion object {
        const val DAILY_COOLDOWN_MILLIS = 24L * 60L * 60L * 1000L
        const val DAILY_REWARD_COINS = 250
        const val REMINDER_INTERVAL_SECONDS = 30 * 60 // 30 minutes
    }

    init {
        loadSavedLocalPrefs()
        startSessionAndMatchTicker()
        startOddsFluctuationTicker()
    }

    private fun loadSavedLocalPrefs() {
        viewModelScope.launch {
            val saved = prefsRepo.prefsFlow.first()
            _uiState.update { state ->
                val updatedTransactions = if (saved.coinBalance != 1000 && state.transactions.size == 1) {
                    listOf(
                        WalletTransaction(
                            id = "tx_restored",
                            title = "Local Wallet Restored",
                            subtitle = "Saved virtual coin balance",
                            amountDelta = saved.coinBalance,
                            balanceAfter = saved.coinBalance,
                            type = TransactionType.INITIAL_GRANT,
                            timestampMillis = System.currentTimeMillis(),
                        ),
                    )
                } else {
                    state.transactions
                }
                state.copy(
                    username = saved.username,
                    coinBalance = saved.coinBalance,
                    lastDailyClaimMillis = saved.lastDailyClaimMillis,
                    lossLimit = saved.lossLimit,
                    transactions = updatedTransactions,
                )
            }
        }
    }

    private fun startSessionAndMatchTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                var newlyCreditedCoins = 0
                val now = System.currentTimeMillis()

                _uiState.update { state ->
                    val nextSessionSeconds = state.sessionSeconds + 1
                    val triggerReminder = nextSessionSeconds > 0 &&
                        nextSessionSeconds % REMINDER_INTERVAL_SECONDS == 0

                    val activeLegMatchIds = state.bets
                        .filter { it.status == BetStatus.ACTIVE }
                        .flatMap { it.legs }
                        .map { it.matchId }
                        .toSet()

                    val newlyFinishedMatches = mutableMapOf<String, Triple<PickOutcome, Int, Int>>()

                    val updatedMatches = state.matches.map { match ->
                        if (match.isFinished) {
                            match
                        } else if (!match.isLive) {
                            // Speed up kickoff slightly if user placed a bet on it so they see auto-settlement!
                            val step = if (match.id in activeLegMatchIds) 4 else 1
                            val nextCountdown = (match.kickoffCountdownSeconds - step).coerceAtLeast(0)
                            if (nextCountdown == 0) {
                                match.copy(
                                    isLive = true,
                                    kickoffCountdownSeconds = 0,
                                    elapsedSeconds = 1,
                                    startTimeLabel = formatLiveLabel(match.sport, 1, match.totalSimulatedSeconds),
                                    liveDetail = formatLiveDetail(match.sport, 1, match.totalSimulatedSeconds),
                                )
                            } else {
                                val minsLabel = if (match.id in activeLegMatchIds) {
                                    "Starting (${nextCountdown}s)"
                                } else {
                                    match.startTimeLabel
                                }
                                match.copy(
                                    kickoffCountdownSeconds = nextCountdown,
                                    startTimeLabel = minsLabel,
                                )
                            }
                        } else {
                            val nextElapsed = match.elapsedSeconds + 1
                            if (nextElapsed >= match.totalSimulatedSeconds) {
                                val resolution = MockMatchesProvider.resolveOutcomeForFinishedMatch(match)
                                newlyFinishedMatches[match.id] = resolution
                                match.copy(
                                    isLive = false,
                                    isFinished = true,
                                    elapsedSeconds = match.totalSimulatedSeconds,
                                    homeScore = resolution.second,
                                    awayScore = resolution.third,
                                    settledOutcome = resolution.first,
                                    startTimeLabel = "FT",
                                    liveDetail = "Final",
                                    homeTrend = OddsTrend.NONE,
                                    drawTrend = OddsTrend.NONE,
                                    awayTrend = OddsTrend.NONE,
                                )
                            } else {
                                val (nextHome, nextAway) = simulateLiveScoring(match)
                                match.copy(
                                    elapsedSeconds = nextElapsed,
                                    homeScore = nextHome,
                                    awayScore = nextAway,
                                    startTimeLabel = formatLiveLabel(match.sport, nextElapsed, match.totalSimulatedSeconds),
                                    liveDetail = formatLiveDetail(match.sport, nextElapsed, match.totalSimulatedSeconds),
                                )
                            }
                        }
                    }

                    // Settle bets whose match legs finished
                    val newTransactions = state.transactions.toMutableList()
                    var runningBalance = state.coinBalance
                    var runningNetDelta = state.sessionNetDelta

                    val updatedBets = state.bets.map { bet ->
                        if (bet.status != BetStatus.ACTIVE) {
                            bet
                        } else {
                            val updatedLegs = bet.legs.map { leg ->
                                val finishedRes = newlyFinishedMatches[leg.matchId]
                                val existingFinishedMatch = updatedMatches.find { it.id == leg.matchId && it.isFinished }
                                when {
                                    finishedRes != null -> leg.copy(
                                        isMatchFinished = true,
                                        actualOutcome = finishedRes.first,
                                        finalScoreText = "${finishedRes.second} - ${finishedRes.third}",
                                    )
                                    existingFinishedMatch != null && existingFinishedMatch.settledOutcome != null -> leg.copy(
                                        isMatchFinished = true,
                                        actualOutcome = existingFinishedMatch.settledOutcome,
                                        finalScoreText = "${existingFinishedMatch.homeScore} - ${existingFinishedMatch.awayScore}",
                                    )
                                    else -> leg
                                }
                            }

                            val allLegsFinished = updatedLegs.all { it.isMatchFinished }
                            if (!allLegsFinished) {
                                bet.copy(legs = updatedLegs)
                            } else {
                                val allWon = updatedLegs.all { it.isLegWon == true }
                                if (allWon) {
                                    val payout = bet.potentialWin
                                    runningBalance += payout
                                    runningNetDelta += payout
                                    newlyCreditedCoins += payout
                                    newTransactions.add(
                                        0,
                                        WalletTransaction(
                                            id = "tx_win_${bet.id}",
                                            title = if (bet.mode == BetMode.ACCUMULATOR) "Acca Bet Won" else "Bet Won",
                                            subtitle = updatedLegs.joinToString(", ") { it.matchTitle },
                                            amountDelta = payout,
                                            balanceAfter = runningBalance,
                                            type = TransactionType.BET_WON,
                                            timestampMillis = now,
                                        ),
                                    )
                                    bet.copy(
                                        legs = updatedLegs,
                                        status = BetStatus.WON,
                                        settledAtMillis = now,
                                        payout = payout,
                                    )
                                } else {
                                    bet.copy(
                                        legs = updatedLegs,
                                        status = BetStatus.LOST,
                                        settledAtMillis = now,
                                        payout = 0,
                                    )
                                }
                            }
                        }
                    }

                    // Keep board fresh: replace finished matches that have no active slip/bet wait after a cycle,
                    // keeping 20 active/upcoming/live matches on the board
                    val activeBetMatchIds = updatedBets
                        .filter { it.status == BetStatus.ACTIVE }
                        .flatMap { it.legs }
                        .map { it.matchId }
                        .toSet()

                    val refreshedMatches = updatedMatches.map { match ->
                        if (match.isFinished && match.id !in activeBetMatchIds && Random.nextDouble() < 0.08) {
                            MockMatchesProvider.createReplacementMatch(match.sport)
                        } else {
                            match
                        }
                    }

                    // Remove finished matches from betSlip automatically
                    val finishedIds = refreshedMatches.filter { it.isFinished }.map { it.id }.toSet()
                    val cleanedSlip = state.betSlip.filterNot { it.matchId in finishedIds }

                    state.copy(
                        matches = refreshedMatches,
                        bets = updatedBets,
                        betSlip = cleanedSlip,
                        coinBalance = runningBalance,
                        transactions = newTransactions,
                        sessionSeconds = nextSessionSeconds,
                        sessionNetDelta = runningNetDelta,
                        currentTimeMillis = now,
                        showThirtyMinReminder = state.showThirtyMinReminder || triggerReminder,
                    )
                }

                if (newlyCreditedCoins > 0) {
                    prefsRepo.saveBalance(_uiState.value.coinBalance)
                }
            }
        }
    }

    private fun startOddsFluctuationTicker() {
        viewModelScope.launch {
            while (true) {
                delay(2800L)
                _uiState.update { state ->
                    val updatedMatches = state.matches.map { match ->
                        if (!match.isLive || match.isFinished) {
                            match.copy(
                                homeTrend = OddsTrend.NONE,
                                drawTrend = OddsTrend.NONE,
                                awayTrend = OddsTrend.NONE,
                            )
                        } else if (Random.nextDouble() < 0.72) {
                            val (hOdds, hTrend) = MockMatchesProvider.fluctuateOdds(match.homeOdds)
                            val (dOdds, dTrend) = MockMatchesProvider.fluctuateOdds(match.drawOdds)
                            val (aOdds, aTrend) = MockMatchesProvider.fluctuateOdds(match.awayOdds)
                            match.copy(
                                homeOdds = hOdds,
                                drawOdds = dOdds,
                                awayOdds = aOdds,
                                homeTrend = hTrend,
                                drawTrend = dTrend,
                                awayTrend = aTrend,
                            )
                        } else {
                            match.copy(
                                homeTrend = OddsTrend.NONE,
                                drawTrend = OddsTrend.NONE,
                                awayTrend = OddsTrend.NONE,
                            )
                        }
                    }

                    val matchMap = updatedMatches.associateBy { it.id }
                    val updatedSlip = state.betSlip.map { selection ->
                        val latestMatch = matchMap[selection.matchId]
                        if (latestMatch != null) {
                            selection.copy(
                                odds = latestMatch.oddsFor(selection.outcome),
                                trend = latestMatch.trendFor(selection.outcome),
                            )
                        } else {
                            selection
                        }
                    }

                    state.copy(
                        matches = updatedMatches,
                        betSlip = updatedSlip,
                    )
                }
            }
        }
    }

    private fun simulateLiveScoring(match: SportsMatch): Pair<Int, Int> {
        var h = match.homeScore
        var a = match.awayScore
        when (match.sport) {
            SportCategory.FOOTBALL -> {
                val roll = Random.nextDouble()
                if (roll < 0.045) h++ else if (roll < 0.085) a++
            }
            SportCategory.CRICKET -> {
                val roll = Random.nextDouble()
                if (roll < 0.45) h += listOf(1, 2, 4, 6).random()
                if (roll > 0.55) a += listOf(1, 2, 4, 6).random()
            }
            SportCategory.BASKETBALL -> {
                if (Random.nextDouble() < 0.42) h += listOf(2, 3).random()
                if (Random.nextDouble() < 0.42) a += listOf(2, 3).random()
            }
            SportCategory.TENNIS -> {
                if (Random.nextDouble() < 0.06 && h < 2) h++
                else if (Random.nextDouble() < 0.06 && a < 2) a++
            }
        }
        return h to a
    }

    private fun formatLiveLabel(sport: SportCategory, elapsed: Int, total: Int): String {
        val ratio = (elapsed.toDouble() / total.coerceAtLeast(1)).coerceIn(0.0, 1.0)
        return when (sport) {
            SportCategory.FOOTBALL -> "LIVE ${(ratio * 90).roundToInt().coerceIn(1, 90)}'"
            SportCategory.CRICKET -> {
                val overs = (ratio * 20).toInt().coerceIn(0, 19)
                val balls = ((ratio * 120).toInt() % 6)
                "LIVE $overs.$balls Ov"
            }
            SportCategory.BASKETBALL -> {
                val quarter = (ratio * 4).toInt().coerceIn(0, 3) + 1
                "LIVE Q$quarter"
            }
            SportCategory.TENNIS -> {
                val setNum = if (ratio < 0.45) 1 else if (ratio < 0.8) 2 else 3
                "LIVE Set $setNum"
            }
        }
    }

    private fun formatLiveDetail(sport: SportCategory, elapsed: Int, total: Int): String {
        val remain = (total - elapsed).coerceAtLeast(0)
        return when (sport) {
            SportCategory.FOOTBALL -> if (elapsed < total / 2) "1st Half • ${remain}s left" else "2nd Half • ${remain}s left"
            SportCategory.CRICKET -> "T20 Live • ${remain}s left"
            SportCategory.BASKETBALL -> "Pro Clock • ${remain}s left"
            SportCategory.TENNIS -> "Best of 3 • ${remain}s left"
        }
    }

    fun selectTab(tab: NavDestination) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectSportTab(sport: SportCategory) {
        _uiState.update { it.copy(selectedSportTab = sport) }
    }

    fun selectLiveFilter(sport: SportCategory?) {
        _uiState.update { it.copy(liveFilterSport = sport) }
    }

    fun toggleSelection(match: SportsMatch, outcome: PickOutcome) {
        if (match.isFinished) return
        _uiState.update { state ->
            val existing = state.betSlip.find { it.matchId == match.id }
            val updatedSlip = when {
                existing != null && existing.outcome == outcome -> {
                    state.betSlip.filterNot { it.matchId == match.id }
                }
                existing != null -> {
                    state.betSlip.map {
                        if (it.matchId == match.id) {
                            it.copy(
                                outcome = outcome,
                                odds = match.oddsFor(outcome),
                                trend = match.trendFor(outcome),
                            )
                        } else {
                            it
                        }
                    }
                }
                else -> {
                    state.betSlip + BetSlipSelection(
                        matchId = match.id,
                        matchTitle = match.matchTitle,
                        sport = match.sport,
                        outcome = outcome,
                        odds = match.oddsFor(outcome),
                        trend = match.trendFor(outcome),
                    )
                }
            }
            val nextMode = if (updatedSlip.size <= 1) BetMode.SINGLE else state.betMode
            state.copy(
                betSlip = updatedSlip,
                betMode = nextMode,
            )
        }
    }

    fun removeSelection(matchId: String) {
        _uiState.update { state ->
            val updated = state.betSlip.filterNot { it.matchId == matchId }
            state.copy(
                betSlip = updated,
                betMode = if (updated.size <= 1) BetMode.SINGLE else state.betMode,
                isBetSlipOpen = if (updated.isEmpty()) false else state.isBetSlipOpen,
            )
        }
    }

    fun clearBetSlip() {
        _uiState.update {
            it.copy(
                betSlip = emptyList(),
                isBetSlipOpen = false,
                betMode = BetMode.SINGLE,
            )
        }
    }

    fun setBetSlipOpen(open: Boolean) {
        _uiState.update { it.copy(isBetSlipOpen = open) }
    }

    fun setBetMode(mode: BetMode) {
        _uiState.update { state ->
            if (mode == BetMode.ACCUMULATOR && state.betSlip.size < 2) state
            else state.copy(betMode = mode)
        }
    }

    fun updateStakeInput(raw: String) {
        val digitsOnly = raw.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(stakeInput = digitsOnly) }
    }

    fun setQuickStake(amount: Int) {
        _uiState.update { it.copy(stakeInput = amount.coerceAtLeast(1).toString()) }
    }

    fun getEffectiveStakeTotal(state: PredictPlayUiState = _uiState.value): Int {
        val unitStake = state.stakeInput.toIntOrNull() ?: 0
        if (unitStake <= 0 || state.betSlip.isEmpty()) return 0
        return when (state.betMode) {
            BetMode.SINGLE -> unitStake * state.betSlip.size
            BetMode.ACCUMULATOR -> unitStake
        }
    }

    fun isLossLimitExceeded(totalStake: Int, state: PredictPlayUiState = _uiState.value): Boolean {
        if (state.lossLimit <= 0) return false
        val projectedSessionLoss = (-state.sessionNetDelta).coerceAtLeast(0) + totalStake
        return projectedSessionLoss > state.lossLimit
    }

    fun placeBet() {
        val state = _uiState.value
        val unitStake = state.stakeInput.toIntOrNull() ?: 0
        if (state.betSlip.isEmpty() || unitStake <= 0) {
            showStatusBanner("Enter a valid virtual stake amount.")
            return
        }
        val totalStake = getEffectiveStakeTotal(state)
        if (totalStake > state.coinBalance) {
            showStatusBanner("Insufficient virtual coins. Claim daily coins or lower your stake.")
            return
        }
        if (isLossLimitExceeded(totalStake, state)) {
            showStatusBanner("Blocked by your virtual loss limit (${state.lossLimit} VC). Adjust in Profile.")
            return
        }

        val now = System.currentTimeMillis()
        val newBalance = state.coinBalance - totalStake
        val placedBets = mutableListOf<PlacedBet>()
        val newTxList = state.transactions.toMutableList()

        if (state.betMode == BetMode.ACCUMULATOR && state.betSlip.size >= 2) {
            val combinedOdds = MockMatchesProvider.roundOdds(
                state.betSlip.fold(1.0) { acc, item -> acc * item.odds },
            )
            val potentialWin = (unitStake * combinedOdds).roundToInt()
            val betId = "bet_${now}_acca"
            val legs = state.betSlip.map {
                BetLeg(
                    matchId = it.matchId,
                    matchTitle = it.matchTitle,
                    sport = it.sport,
                    pick = it.outcome,
                    odds = it.odds,
                )
            }
            placedBets.add(
                PlacedBet(
                    id = betId,
                    mode = BetMode.ACCUMULATOR,
                    legs = legs,
                    stake = unitStake,
                    totalOdds = combinedOdds,
                    potentialWin = potentialWin,
                    status = BetStatus.ACTIVE,
                    placedAtMillis = now,
                ),
            )
            newTxList.add(
                0,
                WalletTransaction(
                    id = "tx_$betId",
                    title = "Accumulator Bet (${legs.size} Legs)",
                    subtitle = "${combinedOdds}x combined odds",
                    amountDelta = -unitStake,
                    balanceAfter = newBalance,
                    type = TransactionType.BET_PLACED,
                    timestampMillis = now,
                ),
            )
        } else {
            var runningBal = state.coinBalance
            state.betSlip.forEachIndexed { idx, sel ->
                runningBal -= unitStake
                val betId = "bet_${now}_$idx"
                val potentialWin = (unitStake * sel.odds).roundToInt()
                placedBets.add(
                    PlacedBet(
                        id = betId,
                        mode = BetMode.SINGLE,
                        legs = listOf(
                            BetLeg(
                                matchId = sel.matchId,
                                matchTitle = sel.matchTitle,
                                sport = sel.sport,
                                pick = sel.outcome,
                                odds = sel.odds,
                            ),
                        ),
                        stake = unitStake,
                        totalOdds = sel.odds,
                        potentialWin = potentialWin,
                        status = BetStatus.ACTIVE,
                        placedAtMillis = now,
                    ),
                )
                newTxList.add(
                    0,
                    WalletTransaction(
                        id = "tx_$betId",
                        title = "Single Bet • ${sel.outcome.label}",
                        subtitle = "${sel.matchTitle} @ ${sel.odds}x",
                        amountDelta = -unitStake,
                        balanceAfter = runningBal,
                        type = TransactionType.BET_PLACED,
                        timestampMillis = now,
                    ),
                )
            }
        }

        _uiState.update {
            it.copy(
                coinBalance = newBalance,
                bets = placedBets + it.bets,
                transactions = newTxList,
                betSlip = emptyList(),
                isBetSlipOpen = false,
                betMode = BetMode.SINGLE,
                sessionNetDelta = it.sessionNetDelta - totalStake,
                sessionBetsCount = it.sessionBetsCount + placedBets.size,
                statusBannerMessage = "Placed ${placedBets.size} virtual bet(s) for $totalStake VC!",
            )
        }
        viewModelScope.launch {
            prefsRepo.saveBalance(newBalance)
        }
    }

    fun claimDailyCoins() {
        val state = _uiState.value
        val now = System.currentTimeMillis()
        val elapsed = now - state.lastDailyClaimMillis
        if (state.lastDailyClaimMillis > 0L && elapsed < DAILY_COOLDOWN_MILLIS) {
            showStatusBanner("Daily free coins already claimed. Come back later!")
            return
        }
        val newBalance = state.coinBalance + DAILY_REWARD_COINS
        val tx = WalletTransaction(
            id = "tx_daily_$now",
            title = "Daily Free Coins",
            subtitle = "24-hour simulator reward",
            amountDelta = DAILY_REWARD_COINS,
            balanceAfter = newBalance,
            type = TransactionType.DAILY_BONUS,
            timestampMillis = now,
        )
        _uiState.update {
            it.copy(
                coinBalance = newBalance,
                lastDailyClaimMillis = now,
                transactions = listOf(tx) + it.transactions,
                statusBannerMessage = "+$DAILY_REWARD_COINS VC added to your virtual wallet!",
            )
        }
        viewModelScope.launch {
            prefsRepo.saveDailyClaim(now, newBalance)
        }
    }

    fun updateUsername(newName: String) {
        val sanitized = newName.trim().take(20).ifEmpty { "PredictorOne" }
        _uiState.update {
            it.copy(
                username = sanitized,
                statusBannerMessage = "Username updated to $sanitized",
            )
        }
        viewModelScope.launch {
            prefsRepo.saveUsername(sanitized)
        }
    }

    fun updateLossLimit(limit: Int) {
        _uiState.update {
            it.copy(
                lossLimit = limit,
                statusBannerMessage = if (limit == 0) {
                    "Virtual loss limit turned off"
                } else {
                    "Virtual loss limit set to $limit VC"
                },
            )
        }
        viewModelScope.launch {
            prefsRepo.saveLossLimit(limit)
        }
    }

    fun dismissThirtyMinReminder(takeBreak: Boolean) {
        _uiState.update {
            it.copy(
                showThirtyMinReminder = false,
                statusBannerMessage = if (takeBreak) {
                    "Good call! Taking a breather keeps simulation fun."
                } else {
                    null
                },
            )
        }
    }

    fun triggerReminderPreview() {
        _uiState.update { it.copy(showThirtyMinReminder = true) }
    }

    fun resetSimulator() {
        val now = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                coinBalance = 1000,
                lastDailyClaimMillis = 0L,
                bets = emptyList(),
                betSlip = emptyList(),
                isBetSlipOpen = false,
                sessionNetDelta = 0,
                sessionBetsCount = 0,
                lossLimit = 500,
                matches = MockMatchesProvider.createInitialMatches(),
                transactions = listOf(
                    WalletTransaction(
                        id = "tx_reset_$now",
                        title = "Simulator Reset",
                        subtitle = "Fresh 1,000 VC virtual balance",
                        amountDelta = 1000,
                        balanceAfter = 1000,
                        type = TransactionType.RESET,
                        timestampMillis = now,
                    ),
                ),
                statusBannerMessage = "Simulator reset to 1,000 VC.",
            )
        }
        viewModelScope.launch {
            prefsRepo.resetAll()
        }
    }

    fun clearStatusBanner() {
        _uiState.update { it.copy(statusBannerMessage = null) }
    }

    private fun showStatusBanner(msg: String) {
        _uiState.update { it.copy(statusBannerMessage = msg) }
    }
}
