package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.BetMode
import com.example.model.BetStatus
import com.example.model.PlacedBet
import com.example.model.SportsMatch
import com.example.ui.components.formatCoins
import com.example.ui.components.formatOdds
import com.example.ui.components.sportIcon
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalPredictPlayColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyBetsScreen(
    bets: List<PlacedBet>,
    matches: List<SportsMatch>,
    onBrowseMatches: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = LocalPredictPlayColors.current
    var selectedTab by rememberSaveable { mutableStateOf(0) } // 0 = Active, 1 = Settled

    val activeBets = bets.filter { it.status == BetStatus.ACTIVE }
    val settledBets = bets.filter { it.status != BetStatus.ACTIVE }
    val displayed = if (selectedTab == 0) activeBets else settledBets
    val matchMap = matches.associateBy { it.id }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "My Virtual Bets",
                style = MaterialTheme.typography.headlineSmall,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Active (${activeBets.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mybets_tab_active"),
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Settled (${settledBets.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mybets_tab_settled"),
                )
            }
        }

        if (displayed.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = ext.textMuted,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (selectedTab == 0) "No active virtual bets" else "No settled bets yet",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (selectedTab == 0) {
                        "Tap any 1X2 odds on Home or Live to place a simulated prediction."
                    } else {
                        "Active bets settle automatically as soon as their simulated matches finish."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = ext.textSecondary,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBrowseMatches) {
                    Text("Browse Matches")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(displayed, key = { it.id }) { bet ->
                    BetRecordCard(bet = bet, matchMap = matchMap)
                }
            }
        }
    }
}

@Composable
private fun BetRecordCard(
    bet: PlacedBet,
    matchMap: Map<String, SportsMatch>,
) {
    val ext = LocalPredictPlayColors.current
    val dateFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)

    val statusColor = when (bet.status) {
        BetStatus.ACTIVE -> ext.coinAmber
        BetStatus.WON -> ext.oddsUp
        BetStatus.LOST -> ext.oddsDown
    }
    val statusBg = when (bet.status) {
        BetStatus.ACTIVE -> ext.coinAmberContainer
        BetStatus.WON -> MaterialTheme.colorScheme.primaryContainer
        BetStatus.LOST -> ext.liveCrimsonContainer
    }
    val statusLabel = when (bet.status) {
        BetStatus.ACTIVE -> "IN PLAY"
        BetStatus.WON -> "WON +${formatCoins(bet.payout)} VC"
        BetStatus.LOST -> "LOST"
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, ext.borderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bet_card_${bet.id}"),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ext.surfaceElevated,
                    ) {
                        Text(
                            text = if (bet.mode == BetMode.ACCUMULATOR) {
                                "ACCA • ${bet.legs.size} LEGS"
                            } else {
                                "SINGLE"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                    Text(
                        text = dateFormatter.format(Date(bet.placedAtMillis)),
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily),
                        color = ext.textMuted,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusBg,
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                bet.legs.forEach { leg ->
                    val liveMatch = matchMap[leg.matchId]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(
                                imageVector = sportIcon(leg.sport),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp),
                            )
                            Column {
                                Text(
                                    text = "${leg.pick.label} (${leg.pick.shortCode}) • ${leg.matchTitle}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                val subtitle = when {
                                    leg.isMatchFinished && leg.finalScoreText != null -> {
                                        val res = if (leg.isLegWon == true) "Correct" else "Missed"
                                        "Final ${leg.finalScoreText} • $res"
                                    }
                                    liveMatch != null && liveMatch.isLive -> {
                                        val rem = (liveMatch.totalSimulatedSeconds - liveMatch.elapsedSeconds).coerceAtLeast(0)
                                        "${liveMatch.startTimeLabel} (${liveMatch.homeScore}-${liveMatch.awayScore}) • Settles in ${rem}s"
                                    }
                                    liveMatch != null -> liveMatch.startTimeLabel
                                    else -> "Simulating..."
                                }
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when (leg.isLegWon) {
                                        true -> ext.oddsUp
                                        false -> ext.oddsDown
                                        null -> ext.textSecondary
                                    },
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = "${formatOdds(leg.odds)}x",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Icon(
                                imageVector = if (leg.isMatchFinished) Icons.Filled.CheckCircle else Icons.Filled.HourglassTop,
                                contentDescription = null,
                                tint = when (leg.isLegWon) {
                                    true -> ext.oddsUp
                                    false -> ext.oddsDown
                                    null -> ext.textMuted
                                },
                                modifier = Modifier.size(15.dp),
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = ext.borderSubtle, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Stake", style = MaterialTheme.typography.labelSmall, color = ext.textMuted)
                    Text(
                        text = "${formatCoins(bet.stake)} VC",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Odds", style = MaterialTheme.typography.labelSmall, color = ext.textMuted)
                    Text(
                        text = "${formatOdds(bet.totalOdds)}x",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (bet.status == BetStatus.WON) "Payout" else "Potential Win",
                        style = MaterialTheme.typography.labelSmall,
                        color = ext.textMuted,
                    )
                    Text(
                        text = "${formatCoins(if (bet.status == BetStatus.WON) bet.payout else bet.potentialWin)} VC",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (bet.status == BetStatus.LOST) ext.textMuted else MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
