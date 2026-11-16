package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.BetSlipSelection
import com.example.model.PickOutcome
import com.example.model.SportCategory
import com.example.model.SportsMatch
import com.example.ui.components.LivePulsingBadge
import com.example.ui.components.MatchCard
import com.example.ui.theme.LocalPredictPlayColors

@Composable
fun LiveScreen(
    matches: List<SportsMatch>,
    liveFilterSport: SportCategory?,
    betSlip: List<BetSlipSelection>,
    onSelectFilter: (SportCategory?) -> Unit,
    onSelectOutcome: (SportsMatch, PickOutcome) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = LocalPredictPlayColors.current
    val allLive = matches.filter { it.isLive && !it.isFinished }
    val filteredLive = if (liveFilterSport == null) allLive else allLive.filter { it.sport == liveFilterSport }
    val slipMap = betSlip.associateBy { it.matchId }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Surface(
                shape = MaterialTheme.shapes.medium,
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
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Live Match Center",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Text(
                            text = "Real-time simulated scores & dynamic odds",
                            style = MaterialTheme.typography.bodySmall,
                            color = ext.textSecondary,
                        )
                    }
                    LivePulsingBadge(label = "${allLive.size} LIVE")
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = liveFilterSport == null,
                    onClick = { onSelectFilter(null) },
                    label = { Text("All (${allLive.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    modifier = Modifier.testTag("live_filter_all"),
                )
                SportCategory.entries.forEach { sport ->
                    val count = allLive.count { it.sport == sport }
                    FilterChip(
                        selected = liveFilterSport == sport,
                        onClick = { onSelectFilter(sport) },
                        label = { Text("${sport.displayName} ($count)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                        modifier = Modifier.testTag("live_filter_${sport.name.lowercase()}"),
                    )
                }
            }
        }

        if (filteredLive.isEmpty()) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, ext.borderSubtle),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Sensors,
                            contentDescription = null,
                            tint = ext.textMuted,
                            modifier = Modifier.size(36.dp),
                        )
                        Text(
                            text = "No live matches in this category right now",
                            style = MaterialTheme.typography.titleSmall,
                            color = ext.textSecondary,
                        )
                        Text(
                            text = "Upcoming matches kick off automatically every few seconds.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ext.textMuted,
                        )
                    }
                }
            }
        } else {
            items(filteredLive, key = { it.id }) { match ->
                MatchCard(
                    match = match,
                    selectedOutcome = slipMap[match.id]?.outcome,
                    onSelectOutcome = { outcome -> onSelectOutcome(match, outcome) },
                )
            }
        }
    }
}
