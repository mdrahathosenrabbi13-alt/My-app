package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.BetStatus
import com.example.model.NavDestination
import com.example.ui.components.BetSlipBottomSheet
import com.example.ui.components.FloatingBetSlipBar
import com.example.ui.components.ResponsiblePlayReminderDialog
import com.example.ui.components.TopAppBarHeader
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveScreen
import com.example.ui.screens.MyBetsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.LocalPredictPlayColors
import com.example.ui.theme.PredictPlayTheme
import com.example.viewmodel.PredictPlayViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
        setContent {
            PredictPlayTheme {
                PredictPlayApp()
            }
        }
    }
}

@Composable
fun PredictPlayApp(
    viewModel: PredictPlayViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val ext = LocalPredictPlayColors.current

    val liveCount = state.matches.count { it.isLive && !it.isFinished }
    val activeBetsCount = state.bets.count { it.status == BetStatus.ACTIVE }

    LaunchedEffect(state.statusBannerMessage) {
        val message = state.statusBannerMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearStatusBanner()
        }
    }

    BackHandler(enabled = state.currentTab != NavDestination.HOME || state.isBetSlipOpen) {
        if (state.isBetSlipOpen) {
            viewModel.setBetSlipOpen(false)
        } else {
            viewModel.selectTab(NavDestination.HOME)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBarHeader(
                    coinBalance = state.coinBalance,
                    sessionSeconds = state.sessionSeconds,
                    onWalletClick = { viewModel.selectTab(NavDestination.WALLET) },
                    onSessionTimerClick = { viewModel.selectTab(NavDestination.PROFILE) },
                    modifier = Modifier.statusBarsPadding(),
                )
            },
            bottomBar = {
                if (!useRail) {
                    PredictPlayBottomBar(
                        currentTab = state.currentTab,
                        liveCount = liveCount,
                        activeBetsCount = activeBetsCount,
                        onSelectTab = viewModel::selectTab,
                    )
                }
            },
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                if (useRail) {
                    PredictPlayNavigationRail(
                        currentTab = state.currentTab,
                        liveCount = liveCount,
                        activeBetsCount = activeBetsCount,
                        onSelectTab = viewModel::selectTab,
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    val contentModifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 720.dp)
                        .align(Alignment.TopCenter)

                    when (state.currentTab) {
                        NavDestination.HOME -> HomeScreen(
                            selectedSport = state.selectedSportTab,
                            matches = state.matches,
                            betSlip = state.betSlip,
                            onSelectSport = viewModel::selectSportTab,
                            onSelectOutcome = viewModel::toggleSelection,
                            onNavigateToLive = { viewModel.selectTab(NavDestination.LIVE) },
                            modifier = contentModifier,
                        )

                        NavDestination.LIVE -> LiveScreen(
                            matches = state.matches,
                            liveFilterSport = state.liveFilterSport,
                            betSlip = state.betSlip,
                            onSelectFilter = viewModel::selectLiveFilter,
                            onSelectOutcome = viewModel::toggleSelection,
                            modifier = contentModifier,
                        )

                        NavDestination.MY_BETS -> MyBetsScreen(
                            bets = state.bets,
                            matches = state.matches,
                            onBrowseMatches = { viewModel.selectTab(NavDestination.HOME) },
                            modifier = contentModifier,
                        )

                        NavDestination.WALLET -> WalletScreen(
                            coinBalance = state.coinBalance,
                            lastDailyClaimMillis = state.lastDailyClaimMillis,
                            currentTimeMillis = state.currentTimeMillis,
                            transactions = state.transactions,
                            onClaimDailyCoins = viewModel::claimDailyCoins,
                            modifier = contentModifier,
                        )

                        NavDestination.PROFILE -> ProfileScreen(
                            username = state.username,
                            bets = state.bets,
                            sessionSeconds = state.sessionSeconds,
                            sessionNetDelta = state.sessionNetDelta,
                            lossLimit = state.lossLimit,
                            onUpdateUsername = viewModel::updateUsername,
                            onUpdateLossLimit = viewModel::updateLossLimit,
                            onPreviewReminder = viewModel::triggerReminderPreview,
                            onResetSimulator = viewModel::resetSimulator,
                            modifier = contentModifier,
                        )
                    }

                    FloatingBetSlipBar(
                        selections = state.betSlip,
                        betMode = state.betMode,
                        onOpenSlip = { viewModel.setBetSlipOpen(true) },
                        onClearSlip = viewModel::clearBetSlip,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .widthIn(max = 600.dp),
                    )
                }
            }
        }
    }

    if (state.isBetSlipOpen && state.betSlip.isNotEmpty()) {
        val totalStake = viewModel.getEffectiveStakeTotal(state)
        val limitExceeded = viewModel.isLossLimitExceeded(totalStake, state)
        BetSlipBottomSheet(
            selections = state.betSlip,
            betMode = state.betMode,
            stakeInput = state.stakeInput,
            coinBalance = state.coinBalance,
            lossLimit = state.lossLimit,
            effectiveTotalStake = totalStake,
            isLossLimitExceeded = limitExceeded,
            onDismiss = { viewModel.setBetSlipOpen(false) },
            onSelectMode = viewModel::setBetMode,
            onRemoveSelection = viewModel::removeSelection,
            onStakeChange = viewModel::updateStakeInput,
            onQuickStake = viewModel::setQuickStake,
            onPlaceBet = viewModel::placeBet,
        )
    }

    if (state.showThirtyMinReminder) {
        ResponsiblePlayReminderDialog(
            sessionSeconds = state.sessionSeconds,
            sessionBetsCount = state.sessionBetsCount,
            sessionNetDelta = state.sessionNetDelta,
            onTakeBreak = { viewModel.dismissThirtyMinReminder(takeBreak = true) },
            onContinue = { viewModel.dismissThirtyMinReminder(takeBreak = false) },
        )
    }
}

@Composable
private fun PredictPlayBottomBar(
    currentTab: NavDestination,
    liveCount: Int,
    activeBetsCount: Int,
    onSelectTab: (NavDestination) -> Unit,
) {
    val ext = LocalPredictPlayColors.current
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        NavDestination.entries.forEach { dest ->
            val selected = currentTab == dest
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTab(dest) },
                icon = {
                    NavDestinationIcon(
                        dest = dest,
                        selected = selected,
                        liveCount = liveCount,
                        activeBetsCount = activeBetsCount,
                    )
                },
                label = {
                    Text(
                        text = dest.label,
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = ext.textSecondary,
                    unselectedTextColor = ext.textSecondary,
                ),
                modifier = Modifier.testTag("nav_${dest.name.lowercase()}"),
            )
        }
    }
}

@Composable
private fun PredictPlayNavigationRail(
    currentTab: NavDestination,
    liveCount: Int,
    activeBetsCount: Int,
    onSelectTab: (NavDestination) -> Unit,
) {
    val ext = LocalPredictPlayColors.current
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        NavDestination.entries.forEach { dest ->
            val selected = currentTab == dest
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectTab(dest) },
                icon = {
                    NavDestinationIcon(
                        dest = dest,
                        selected = selected,
                        liveCount = liveCount,
                        activeBetsCount = activeBetsCount,
                    )
                },
                label = {
                    Text(
                        text = dest.label,
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = ext.textSecondary,
                    unselectedTextColor = ext.textSecondary,
                ),
                modifier = Modifier.testTag("rail_${dest.name.lowercase()}"),
            )
        }
    }
}

@Composable
private fun NavDestinationIcon(
    dest: NavDestination,
    selected: Boolean,
    liveCount: Int,
    activeBetsCount: Int,
) {
    val ext = LocalPredictPlayColors.current
    val icon = when (dest) {
        NavDestination.HOME -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
        NavDestination.LIVE -> if (selected) Icons.Filled.Sensors else Icons.Outlined.Sensors
        NavDestination.MY_BETS -> if (selected) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong
        NavDestination.WALLET -> if (selected) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet
        NavDestination.PROFILE -> if (selected) Icons.Filled.Person else Icons.Outlined.Person
    }

    val badgeCount = when (dest) {
        NavDestination.LIVE -> liveCount
        NavDestination.MY_BETS -> activeBetsCount
        else -> 0
    }

    if (badgeCount > 0) {
        BadgedBox(
            badge = {
                Badge(
                    containerColor = if (dest == NavDestination.LIVE) ext.liveCrimson else MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Text(badgeCount.toString())
                }
            },
        ) {
            Icon(imageVector = icon, contentDescription = dest.label)
        }
    } else {
        Icon(imageVector = icon, contentDescription = dest.label)
    }
}
