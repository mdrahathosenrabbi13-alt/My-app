package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.BetMode
import com.example.model.BetSlipSelection
import com.example.model.OddsTrend
import com.example.model.PickOutcome
import com.example.model.SportCategory
import com.example.model.SportsMatch
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalPredictPlayColors
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.roundToInt

fun sportIcon(sport: SportCategory): ImageVector = when (sport) {
    SportCategory.FOOTBALL -> Icons.Filled.SportsSoccer
    SportCategory.CRICKET -> Icons.Filled.SportsCricket
    SportCategory.BASKETBALL -> Icons.Filled.SportsBasketball
    SportCategory.TENNIS -> Icons.Filled.SportsTennis
}

fun formatDurationTimer(totalSeconds: Int): String {
    val hrs = totalSeconds / 3600
    val mins = (totalSeconds % 3600) / 60
    val secs = totalSeconds % 60
    return if (hrs > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format(Locale.US, "%02d:%02d", mins, secs)
    }
}

fun formatOdds(odds: Double): String = String.format(Locale.US, "%.2f", odds)

fun formatCoins(amount: Int): String = String.format(Locale.US, "%,d", amount)

@Composable
fun TopAppBarHeader(
    coinBalance: Int,
    sessionSeconds: Int,
    onWalletClick: () -> Unit,
    onSessionTimerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = LocalPredictPlayColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer,
                                ),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "VIRTUAL SIMULATOR",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = ext.surfaceElevated,
                    border = BorderStroke(1.dp, ext.borderSubtle),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onSessionTimerClick)
                        .testTag("header_session_timer"),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = "Session timer",
                            tint = ext.textSecondary,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = formatDurationTimer(sessionSeconds),
                            style = MaterialTheme.typography.labelMedium,
                            color = ext.textSecondary,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onWalletClick)
                        .testTag("header_wallet_pill"),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MonetizationOn,
                            contentDescription = "Virtual coins balance",
                            tint = ext.coinAmber,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "${formatCoins(coinBalance)} VC",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }

        // Subtle required disclaimer strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ext.surfaceElevated.copy(alpha = 0.7f))
                .padding(horizontal = 16.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = ext.textMuted,
                modifier = Modifier.size(12.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.disclaimer_text),
                style = MaterialTheme.typography.labelSmall,
                color = ext.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        HorizontalDivider(color = ext.borderSubtle, thickness = 1.dp)
    }
}

@Composable
fun LivePulsingBadge(
    label: String,
    modifier: Modifier = Modifier,
) {
    val ext = LocalPredictPlayColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 750),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dot_alpha",
    )

    Surface(
        shape = RoundedCornerShape(50),
        color = ext.liveCrimsonContainer,
        border = BorderStroke(1.dp, ext.liveCrimson.copy(alpha = 0.5f)),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(ext.liveCrimson),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                ),
                color = ext.liveCrimson,
            )
        }
    }
}

@Composable
fun OddsButton(
    outcome: PickOutcome,
    odds: Double,
    trend: OddsTrend,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
) {
    val ext = LocalPredictPlayColors.current
    var activeFlash by remember { mutableStateOf(OddsTrend.NONE) }

    LaunchedEffect(odds, trend) {
        if (trend != OddsTrend.NONE) {
            activeFlash = trend
            delay(1100L)
            activeFlash = OddsTrend.NONE
        }
    }

    val targetContainer = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        activeFlash == OddsTrend.UP -> ext.oddsUpFlashBg
        activeFlash == OddsTrend.DOWN -> ext.oddsDownFlashBg
        else -> ext.surfaceElevated
    }
    val targetBorder = when {
        isSelected -> MaterialTheme.colorScheme.primary
        activeFlash == OddsTrend.UP -> ext.oddsUp
        activeFlash == OddsTrend.DOWN -> ext.oddsDown
        else -> ext.borderSubtle
    }
    val targetTextColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        activeFlash == OddsTrend.UP -> ext.oddsUp
        activeFlash == OddsTrend.DOWN -> ext.oddsDown
        else -> MaterialTheme.colorScheme.onSurface
    }

    val animatedBg by animateColorAsState(targetValue = targetContainer, animationSpec = tween(250), label = "odds_bg")
    val animatedBorder by animateColorAsState(targetValue = targetBorder, animationSpec = tween(250), label = "odds_border")
    val animatedText by animateColorAsState(targetValue = targetTextColor, animationSpec = tween(250), label = "odds_text")

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = animatedBg,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, animatedBorder),
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testTag),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = outcome.shortCode,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else ext.textSecondary,
                )
                if (activeFlash == OddsTrend.UP) {
                    Text(
                        text = "▲",
                        style = MaterialTheme.typography.labelSmall,
                        color = ext.oddsUp,
                    )
                } else if (activeFlash == OddsTrend.DOWN) {
                    Text(
                        text = "▼",
                        style = MaterialTheme.typography.labelSmall,
                        color = ext.oddsDown,
                    )
                }
            }
            Text(
                text = formatOdds(odds),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = animatedText,
            )
        }
    }
}

@Composable
fun TeamInitialBadge(
    teamName: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val initials = remember(teamName) {
        teamName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
            .ifEmpty { "PP" }
    }
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(accent.copy(alpha = 0.16f))
            .border(1.dp, accent.copy(alpha = 0.45f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = accent,
        )
    }
}

@Composable
fun MatchCard(
    match: SportsMatch,
    selectedOutcome: PickOutcome?,
    onSelectOutcome: (PickOutcome) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = LocalPredictPlayColors.current
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (match.isLive) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f) else ext.borderSubtle,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Top Meta Row: Sport icon + League + Time/Live Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = sportIcon(match.sport),
                        contentDescription = match.sport.displayName,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = match.league,
                        style = MaterialTheme.typography.labelSmall,
                        color = ext.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                when {
                    match.isLive -> LivePulsingBadge(label = match.startTimeLabel)
                    match.isFinished -> Surface(
                        shape = RoundedCornerShape(50),
                        color = ext.surfaceHighest,
                    ) {
                        Text(
                            text = "FT • FINAL",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily),
                            color = ext.textSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                    else -> Text(
                        text = match.startTimeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily),
                        color = ext.textSecondary,
                    )
                }
            }

            // Teams & Score Row
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        TeamInitialBadge(
                            teamName = match.homeTeam,
                            accent = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = match.homeTeam,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (match.isLive || match.isFinished) {
                        Text(
                            text = match.homeScore.toString(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        TeamInitialBadge(
                            teamName = match.awayTeam,
                            accent = ext.coinAmber,
                        )
                        Text(
                            text = match.awayTeam,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (match.isLive || match.isFinished) {
                        Text(
                            text = match.awayScore.toString(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            if (match.isLive) {
                val progress = (match.elapsedSeconds.toFloat() / match.totalSimulatedSeconds.coerceAtLeast(1)).coerceIn(0f, 1f)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = match.liveDetail,
                            style = MaterialTheme.typography.labelSmall,
                            color = ext.textMuted,
                        )
                        Text(
                            text = "Auto-settles at FT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = ext.surfaceHighest,
                    )
                }
            }

            // 1X2 Odds Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PickOutcome.entries.forEach { outcome ->
                    OddsButton(
                        outcome = outcome,
                        odds = match.oddsFor(outcome),
                        trend = match.trendFor(outcome),
                        isSelected = selectedOutcome == outcome,
                        enabled = !match.isFinished,
                        onClick = { onSelectOutcome(outcome) },
                        testTag = "odds_${match.id}_${outcome.shortCode}",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingBetSlipBar(
    selections: List<BetSlipSelection>,
    betMode: BetMode,
    onOpenSlip: () -> Unit,
    onClearSlip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = LocalPredictPlayColors.current
    AnimatedVisibility(
        visible = selections.isNotEmpty(),
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier,
    ) {
        val combinedOdds = if (selections.size >= 2 && betMode == BetMode.ACCUMULATOR) {
            selections.fold(1.0) { acc, s -> acc * s.odds }
        } else {
            selections.sumOf { it.odds }
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onOpenSlip)
                .testTag("floating_bet_slip_bar"),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = selections.size.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    Column {
                        Text(
                            text = if (selections.size >= 2 && betMode == BetMode.ACCUMULATOR) {
                                "Accumulator Slip"
                            } else {
                                "Bet Slip (${selections.size} Pick${if (selections.size > 1) "s" else ""})"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            text = if (selections.size >= 2 && betMode == BetMode.ACCUMULATOR) {
                                "Combined ${formatOdds(combinedOdds)}x • Tap to stake"
                            } else {
                                "Tap to enter virtual stake"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = ext.textSecondary,
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onClearSlip,
                        modifier = Modifier.testTag("clear_bet_slip_bar"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteSweep,
                            contentDescription = "Clear bet slip",
                            tint = ext.textSecondary,
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                    ) {
                        Text(
                            text = "Open",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetSlipBottomSheet(
    selections: List<BetSlipSelection>,
    betMode: BetMode,
    stakeInput: String,
    coinBalance: Int,
    lossLimit: Int,
    effectiveTotalStake: Int,
    isLossLimitExceeded: Boolean,
    onDismiss: () -> Unit,
    onSelectMode: (BetMode) -> Unit,
    onRemoveSelection: (String) -> Unit,
    onStakeChange: (String) -> Unit,
    onQuickStake: (Int) -> Unit,
    onPlaceBet: () -> Unit,
) {
    val ext = LocalPredictPlayColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val unitStake = stakeInput.toIntOrNull() ?: 0

    val combinedAccaOdds = selections.fold(1.0) { acc, item -> acc * item.odds }
    val potentialWin = when {
        unitStake <= 0 || selections.isEmpty() -> 0
        betMode == BetMode.ACCUMULATOR && selections.size >= 2 -> (unitStake * combinedAccaOdds).roundToInt()
        else -> selections.sumOf { (unitStake * it.odds).roundToInt() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Virtual Bet Slip",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = "Balance: ${formatCoins(coinBalance)} VC",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close bet slip",
                    )
                }
            }

            // Single / Accumulator switcher when 2+ selections
            if (selections.size >= 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    BetMode.entries.forEach { mode ->
                        val selected = betMode == mode
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectMode(mode) },
                            label = {
                                Text(
                                    text = if (mode == BetMode.ACCUMULATOR) {
                                        "Accumulator (${formatOdds(combinedAccaOdds)}x)"
                                    } else {
                                        "Single (${selections.size} bets)"
                                    },
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("bet_mode_${mode.name.lowercase()}"),
                        )
                    }
                }
            }

            // Selected Picks List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((selections.size.coerceAtMost(3) * 74).dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp),
            ) {
                items(selections, key = { it.matchId }) { item ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ext.surfaceElevated,
                        border = BorderStroke(1.dp, ext.borderSubtle),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    imageVector = sportIcon(item.sport),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp),
                                )
                                Column {
                                    Text(
                                        text = "${item.outcome.label} (${item.outcome.shortCode})",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Text(
                                        text = item.matchTitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ext.textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${formatOdds(item.odds)}x",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                IconButton(
                                    onClick = { onRemoveSelection(item.matchId) },
                                    modifier = Modifier.size(36.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Remove pick",
                                        tint = ext.textMuted,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Stake Input + Quick Stake Chips
            OutlinedTextField(
                value = stakeInput,
                onValueChange = onStakeChange,
                label = {
                    Text(
                        text = if (betMode == BetMode.SINGLE && selections.size > 1) {
                            "Virtual Stake per Bet (VC)"
                        } else {
                            "Virtual Stake (VC)"
                        },
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Text(
                        text = "VC",
                        style = MaterialTheme.typography.labelMedium,
                        color = ext.coinAmber,
                        modifier = Modifier.padding(end = 12.dp),
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = ext.borderSubtle,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bet_slip_stake_input"),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(25, 50, 100, 250).forEach { preset ->
                    OutlinedButton(
                        onClick = { onQuickStake(preset) },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        border = BorderStroke(1.dp, ext.borderSubtle),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "$preset",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
                OutlinedButton(
                    onClick = {
                        val maxPerBet = if (betMode == BetMode.SINGLE && selections.size > 1) {
                            (coinBalance / selections.size).coerceAtLeast(1)
                        } else {
                            coinBalance.coerceAtLeast(1)
                        }
                        onQuickStake(maxPerBet)
                    },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "MAX",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            // Summary Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ext.surfaceElevated,
                border = BorderStroke(1.dp, ext.borderSubtle),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Total Stake", style = MaterialTheme.typography.bodyMedium, color = ext.textSecondary)
                        Text(
                            text = "${formatCoins(effectiveTotalStake)} VC",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Potential Virtual Win", style = MaterialTheme.typography.bodyMedium, color = ext.textSecondary)
                        Text(
                            text = "${formatCoins(potentialWin)} VC",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            if (isLossLimitExceeded) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ext.liveCrimsonContainer,
                    border = BorderStroke(1.dp, ext.liveCrimson.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WarningAmber,
                            contentDescription = null,
                            tint = ext.liveCrimson,
                        )
                        Text(
                            text = "This stake exceeds your Responsible Play virtual loss limit (${formatCoins(lossLimit)} VC). Lower stake or adjust in Profile.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }

            val canPlace = unitStake > 0 && effectiveTotalStake in 1..coinBalance && !isLossLimitExceeded
            Button(
                onClick = onPlaceBet,
                enabled = canPlace,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("place_virtual_bet_button"),
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (effectiveTotalStake > coinBalance) {
                        "Insufficient Virtual Coins"
                    } else {
                        "Place Virtual Bet • ${formatCoins(effectiveTotalStake)} VC"
                    },
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Text(
                text = stringResource(R.string.disclaimer_text),
                style = MaterialTheme.typography.labelSmall,
                color = ext.textMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
fun ResponsiblePlayReminderDialog(
    sessionSeconds: Int,
    sessionBetsCount: Int,
    sessionNetDelta: Int,
    onTakeBreak: () -> Unit,
    onContinue: () -> Unit,
) {
    val ext = LocalPredictPlayColors.current
    AlertDialog(
        onDismissRequest = onContinue,
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = "Responsible Play Check-in",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
        title = {
            Text(
                text = "30-Minute Session Check-In",
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "You've been simulating predictions for ${formatDurationTimer(sessionSeconds)}. Take a quick moment to review your session.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ext.textSecondary,
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ext.surfaceElevated,
                    border = BorderStroke(1.dp, ext.borderSubtle),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Session Bets", style = MaterialTheme.typography.bodySmall, color = ext.textSecondary)
                            Text(
                                text = sessionBetsCount.toString(),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Net Virtual Coins", style = MaterialTheme.typography.bodySmall, color = ext.textSecondary)
                            val prefix = if (sessionNetDelta >= 0) "+" else ""
                            Text(
                                text = "$prefix${formatCoins(sessionNetDelta)} VC",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (sessionNetDelta >= 0) ext.oddsUp else ext.oddsDown,
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.disclaimer_text),
                    style = MaterialTheme.typography.labelSmall,
                    color = ext.textMuted,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onContinue,
                modifier = Modifier.testTag("reminder_continue_button"),
            ) {
                Text("Continue Playing")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onTakeBreak,
                modifier = Modifier.testTag("reminder_break_button"),
            ) {
                Text("Take a Break", color = ext.textSecondary)
            }
        },
    )
}
