package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.BetStatus
import com.example.model.PlacedBet
import com.example.ui.components.formatCoins
import com.example.ui.components.formatDurationTimer
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalPredictPlayColors
import java.util.Locale
import kotlin.math.abs

@Composable
fun ProfileScreen(
    username: String,
    bets: List<PlacedBet>,
    sessionSeconds: Int,
    sessionNetDelta: Int,
    lossLimit: Int,
    onUpdateUsername: (String) -> Unit,
    onUpdateLossLimit: (Int) -> Unit,
    onPreviewReminder: () -> Unit,
    onResetSimulator: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = LocalPredictPlayColors.current
    var showEditNameDialog by remember { mutableStateOf(false) }

    val totalBets = bets.size
    val settledBets = bets.filter { it.status != BetStatus.ACTIVE }
    val wonBets = settledBets.count { it.status == BetStatus.WON }
    val winRatePercent = if (settledBets.isEmpty()) 0 else ((wonBets * 100.0) / settledBets.size).toInt()
    val bestWin = bets.filter { it.status == BetStatus.WON }.maxOfOrNull { it.payout } ?: 0

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Profile Header Card
        item {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, ext.borderSubtle),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_profile_avatar),
                            contentDescription = "Profile avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = username,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Text(
                                text = "Virtual Sports Analyst",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    IconButton(
                        onClick = { showEditNameDialog = true },
                        modifier = Modifier.testTag("edit_username_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit username",
                            tint = ext.textSecondary,
                        )
                    }
                }
            }
        }

        // KPI Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatMetricCard(
                    label = "Total Bets",
                    value = totalBets.toString(),
                    accent = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                StatMetricCard(
                    label = "Win Rate",
                    value = "$winRatePercent%",
                    accent = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
                StatMetricCard(
                    label = "Best Win",
                    value = "${formatCoins(bestWin)} VC",
                    accent = ext.coinAmber,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Performance Chart Card
        item {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, ext.borderSubtle),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BarChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = "Recent Prediction Performance",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        Text(
                            text = "${settledBets.size} settled",
                            style = MaterialTheme.typography.labelSmall,
                            color = ext.textSecondary,
                        )
                    }

                    PredictionStatsChart(
                        settledBets = settledBets.take(10).reversed(),
                        winColor = ext.oddsUp,
                        lossColor = ext.oddsDown,
                        trackColor = ext.surfaceElevated,
                        baselineColor = ext.borderSubtle,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        LegendDot(color = ext.oddsUp, label = "Won ($wonBets)")
                        LegendDot(color = ext.oddsDown, label = "Lost (${settledBets.size - wonBets})")
                    }
                }
            }
        }

        // Responsible Play Card
        item {
            val sessionLoss = (-sessionNetDelta).coerceAtLeast(0)
            val limitRatio = if (lossLimit <= 0) 0f else (sessionLoss.toFloat() / lossLimit).coerceIn(0f, 1f)

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Responsible Play Controls",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }

                    // Session Timer Row
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ext.surfaceElevated,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp),
                                )
                                Column {
                                    Text(
                                        text = "Active Session Timer",
                                        style = MaterialTheme.typography.titleSmall,
                                    )
                                    Text(
                                        text = "Auto check-in popup every 30 minutes",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ext.textSecondary,
                                    )
                                }
                            }
                            Text(
                                text = formatDurationTimer(sessionSeconds),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    // Virtual Loss Limit Selector
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "Session Virtual Loss Limit",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = if (lossLimit == 0) "Off" else "${formatCoins(sessionLoss)} / ${formatCoins(lossLimit)} VC",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (limitRatio >= 0.85f && lossLimit > 0) ext.oddsDown else ext.textSecondary,
                            )
                        }

                        if (lossLimit > 0) {
                            LinearProgressIndicator(
                                progress = { limitRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = if (limitRatio >= 0.85f) ext.oddsDown else MaterialTheme.colorScheme.primary,
                                trackColor = ext.surfaceHighest,
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            listOf(250, 500, 1000, 2000, 0).forEach { limitOption ->
                                val selected = lossLimit == limitOption
                                FilterChip(
                                    selected = selected,
                                    onClick = { onUpdateLossLimit(limitOption) },
                                    label = {
                                        Text(
                                            text = if (limitOption == 0) "Off" else "$limitOption",
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("loss_limit_$limitOption"),
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedButton(
                            onClick = onPreviewReminder,
                            border = BorderStroke(1.dp, ext.borderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("preview_reminder_button"),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("30m Check-In", style = MaterialTheme.typography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = onResetSimulator,
                            border = BorderStroke(1.dp, ext.borderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reset_simulator_button"),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Coins", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    Text(
                        text = stringResource(R.string.disclaimer_text),
                        style = MaterialTheme.typography.labelSmall,
                        color = ext.textMuted,
                    )
                }
            }
        }
    }

    if (showEditNameDialog) {
        var draftName by remember { mutableStateOf(username) }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Edit Username") },
            text = {
                OutlinedTextField(
                    value = draftName,
                    onValueChange = { if (it.length <= 20) draftName = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateUsername(draftName)
                        showEditNameDialog = false
                    },
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun StatMetricCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val ext = LocalPredictPlayColors.current
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, ext.borderSubtle),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = ext.textSecondary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                ),
                color = accent,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun PredictionStatsChart(
    settledBets: List<PlacedBet>,
    winColor: Color,
    lossColor: Color,
    trackColor: Color,
    baselineColor: Color,
) {
    val ext = LocalPredictPlayColors.current
    if (settledBets.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(trackColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Place and settle bets to see your win/loss bar chart",
                style = MaterialTheme.typography.bodySmall,
                color = ext.textMuted,
            )
        }
        return
    }

    val deltas = settledBets.map { bet ->
        if (bet.status == BetStatus.WON) (bet.payout - bet.stake).coerceAtLeast(10) else -bet.stake
    }
    val maxMagnitude = deltas.maxOf { abs(it) }.coerceAtLeast(50)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(trackColor)
            .padding(horizontal = 12.dp, vertical = 14.dp),
    ) {
        val midY = size.height / 2f
        drawLine(
            color = baselineColor,
            start = Offset(0f, midY),
            end = Offset(size.width, midY),
            strokeWidth = 2f,
        )

        val count = deltas.size
        val slotWidth = size.width / count.coerceAtLeast(1)
        val barWidth = (slotWidth * 0.55f).coerceIn(12f, 36f)

        deltas.forEachIndexed { index, delta ->
            val ratio = (abs(delta).toFloat() / maxMagnitude).coerceIn(0.15f, 1f)
            val barHeight = (size.height / 2f - 6f) * ratio
            val x = index * slotWidth + (slotWidth - barWidth) / 2f
            val isWin = delta >= 0
            val topY = if (isWin) midY - barHeight else midY
            drawRoundRect(
                color = if (isWin) winColor else lossColor,
                topLeft = Offset(x, topY),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(6f, 6f),
            )
        }
    }
}

@Composable
private fun LegendDot(
    color: Color,
    label: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = LocalPredictPlayColors.current.textSecondary,
        )
    }
}
