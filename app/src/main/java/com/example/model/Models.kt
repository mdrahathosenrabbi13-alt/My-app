package com.example.model

import androidx.compose.runtime.Immutable

enum class SportCategory(val displayName: String) {
    FOOTBALL("Football"),
    CRICKET("Cricket"),
    BASKETBALL("Basketball"),
    TENNIS("Tennis"),
}

enum class PickOutcome(val shortCode: String, val label: String) {
    HOME("1", "Home"),
    DRAW("X", "Draw"),
    AWAY("2", "Away"),
}

enum class OddsTrend {
    NONE,
    UP,
    DOWN,
}

enum class NavDestination(val label: String) {
    HOME("Home"),
    LIVE("Live"),
    MY_BETS("My Bets"),
    WALLET("Wallet"),
    PROFILE("Profile"),
}

enum class BetMode(val label: String) {
    SINGLE("Single"),
    ACCUMULATOR("Accumulator"),
}

enum class BetStatus {
    ACTIVE,
    WON,
    LOST,
}

enum class TransactionType {
    INITIAL_GRANT,
    DAILY_BONUS,
    BET_PLACED,
    BET_WON,
    RESET,
}

@Immutable
data class SportsMatch(
    val id: String,
    val sport: SportCategory,
    val league: String,
    val homeTeam: String,
    val awayTeam: String,
    val startTimeLabel: String,
    val kickoffCountdownSeconds: Int,
    val isLive: Boolean,
    val isFinished: Boolean = false,
    val elapsedSeconds: Int = 0,
    val totalSimulatedSeconds: Int = 45,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val liveDetail: String = "",
    val homeOdds: Double,
    val drawOdds: Double,
    val awayOdds: Double,
    val homeTrend: OddsTrend = OddsTrend.NONE,
    val drawTrend: OddsTrend = OddsTrend.NONE,
    val awayTrend: OddsTrend = OddsTrend.NONE,
    val settledOutcome: PickOutcome? = null,
) {
    fun oddsFor(outcome: PickOutcome): Double = when (outcome) {
        PickOutcome.HOME -> homeOdds
        PickOutcome.DRAW -> drawOdds
        PickOutcome.AWAY -> awayOdds
    }

    fun trendFor(outcome: PickOutcome): OddsTrend = when (outcome) {
        PickOutcome.HOME -> homeTrend
        PickOutcome.DRAW -> drawTrend
        PickOutcome.AWAY -> awayTrend
    }

    val matchTitle: String get() = "$homeTeam vs $awayTeam"
}

@Immutable
data class BetSlipSelection(
    val matchId: String,
    val matchTitle: String,
    val sport: SportCategory,
    val outcome: PickOutcome,
    val odds: Double,
    val trend: OddsTrend = OddsTrend.NONE,
)

@Immutable
data class BetLeg(
    val matchId: String,
    val matchTitle: String,
    val sport: SportCategory,
    val pick: PickOutcome,
    val odds: Double,
    val isMatchFinished: Boolean = false,
    val actualOutcome: PickOutcome? = null,
    val finalScoreText: String? = null,
) {
    val isLegWon: Boolean?
        get() = if (!isMatchFinished || actualOutcome == null) null else actualOutcome == pick
}

@Immutable
data class PlacedBet(
    val id: String,
    val mode: BetMode,
    val legs: List<BetLeg>,
    val stake: Int,
    val totalOdds: Double,
    val potentialWin: Int,
    val status: BetStatus,
    val placedAtMillis: Long,
    val settledAtMillis: Long? = null,
    val payout: Int = 0,
)

@Immutable
data class WalletTransaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amountDelta: Int,
    val balanceAfter: Int,
    val type: TransactionType,
    val timestampMillis: Long,
)

@Immutable
data class PredictPlayUiState(
    val currentTab: NavDestination = NavDestination.HOME,
    val selectedSportTab: SportCategory = SportCategory.FOOTBALL,
    val liveFilterSport: SportCategory? = null,
    val matches: List<SportsMatch> = emptyList(),
    val betSlip: List<BetSlipSelection> = emptyList(),
    val isBetSlipOpen: Boolean = false,
    val betMode: BetMode = BetMode.SINGLE,
    val stakeInput: String = "50",
    val bets: List<PlacedBet> = emptyList(),
    val coinBalance: Int = 1000,
    val transactions: List<WalletTransaction> = emptyList(),
    val lastDailyClaimMillis: Long = 0L,
    val currentTimeMillis: Long = System.currentTimeMillis(),
    val username: String = "PredictorOne",
    val sessionSeconds: Int = 0,
    val sessionNetDelta: Int = 0,
    val sessionBetsCount: Int = 0,
    val lossLimit: Int = 500,
    val showThirtyMinReminder: Boolean = false,
    val statusBannerMessage: String? = null,
)
