package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.BetSlipSelection
import com.example.model.PickOutcome
import com.example.model.SportCategory
import com.example.model.SportsMatch
import com.example.ui.components.LivePulsingBadge
import com.example.ui.components.MatchCard
import com.example.ui.components.sportIcon
import com.example.ui.theme.LocalPredictPlayColors

@Composable
fun HomeScreen(
    selectedSport: SportCategory,
    matches: List<SportsMatch>,
    betSlip: List<BetSlipSelection>,
    onSelectSport: (SportCategory) -> Unit,
    onSelectOutcome: (SportsMatch, PickOutcome) -> Unit,
    onNavigateToLive: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = LocalPredictPlayColors.current
    val filteredMatches = matches.filter { it.sport == selectedSport && !it.isFinished }
    val liveCount = matches.count { it.isLive && !it.isFinished }
    val slipMap = betSlip.associateBy { it.matchId }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Hero Arena Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(152.dp)
                    .clip(MaterialTheme.shapes.large)
                    .clickable(onClick = onNavigateToLive)
                    .testTag("home_hero_banner"),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_arena_banner),
                    contentDescription = "PredictPlay Arena",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xE6090D0B),
                                    Color(0x99090D0B),
                                    Color(0x33090D0B),
                                ),
                            ),
                        ),
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        LivePulsingBadge(label = "$liveCount LIVE NOW")
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                        ) {
                            Text(
                                text = "VIRTUAL COINS ONLY",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Matchday Arena",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Sensors,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = "Tap any 1X2 odds to build Singles or Accumulators",
                                style = MaterialTheme.typography.bodySmall,
                                color = ext.textSecondary,
                            )
                        }
                    }
                }
            }
        }

        // Sport Category Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SportCategory.entries.forEach { sport ->
                    val isSelected = sport == selectedSport
                    val count = matches.count { it.sport == sport && !it.isFinished }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else ext.borderSubtle,
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectSport(sport) }
                            .testTag("sport_tab_${sport.name.lowercase()}"),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                        ) {
                            Icon(
                                imageVector = sportIcon(sport),
                                contentDescription = sport.displayName,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = "${sport.displayName} ($count)",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }

        // Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${selectedSport.displayName} Fixtures",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "${filteredMatches.size} Active",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        if (filteredMatches.isEmpty()) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, ext.borderSubtle),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = sportIcon(selectedSport),
                            contentDescription = null,
                            tint = ext.textMuted,
                            modifier = Modifier.size(32.dp),
                        )
                        Text(
                            text = "Refreshing ${selectedSport.displayName} fixtures...",
                            style = MaterialTheme.typography.titleSmall,
                            color = ext.textSecondary,
                        )
                    }
                }
            }
        } else {
            items(filteredMatches, key = { it.id }) { match ->
                MatchCard(
                    match = match,
                    selectedOutcome = slipMap[match.id]?.outcome,
                    onSelectOutcome = { outcome -> onSelectOutcome(match, outcome) },
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
