package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.TransactionType
import com.example.model.WalletTransaction
import com.example.ui.components.formatCoins
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalPredictPlayColors
import com.example.viewmodel.PredictPlayViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    coinBalance: Int,
    lastDailyClaimMillis: Long,
    currentTimeMillis: Long,
    transactions: List<WalletTransaction>,
    onClaimDailyCoins: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = LocalPredictPlayColors.current
    val elapsedSinceClaim = currentTimeMillis - lastDailyClaimMillis
    val cooldownRemainingMillis = if (lastDailyClaimMillis == 0L) {
        0L
    } else {
        (PredictPlayViewModel.DAILY_COOLDOWN_MILLIS - elapsedSinceClaim).coerceAtLeast(0L)
    }
    val canClaimDaily = cooldownRemainingMillis == 0L

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                    MaterialTheme.colorScheme.surface,
                                ),
                            ),
                        )
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
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
                                imageVector = Icons.Filled.AccountBalanceWallet,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = "Virtual Coin Wallet",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = ext.coinAmberContainer,
                        ) {
                            Text(
                                text = "NO CASH VALUE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ext.coinAmber,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MonetizationOn,
                            contentDescription = null,
                            tint = ext.coinAmber,
                            modifier = Modifier.size(34.dp),
                        )
                        Text(
                            text = "${formatCoins(coinBalance)} VC",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    Text(
                        text = stringResource(R.string.disclaimer_text),
                        style = MaterialTheme.typography.bodySmall,
                        color = ext.textSecondary,
                    )

                    Button(
                        onClick = onClaimDailyCoins,
                        enabled = canClaimDaily,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = ext.surfaceHighest,
                            disabledContentColor = ext.textSecondary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("claim_daily_coins_button"),
                    ) {
                        Icon(
                            imageVector = if (canClaimDaily) Icons.Filled.CardGiftcard else Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (canClaimDaily) {
                                "Daily Free Coins (+${PredictPlayViewModel.DAILY_REWARD_COINS} VC)"
                            } else {
                                "Next Claim in ${formatMillisCountdown(cooldownRemainingMillis)}"
                            },
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Transaction History",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "${transactions.size} entries",
                    style = MaterialTheme.typography.labelMedium,
                    color = ext.textSecondary,
                )
            }
        }

        items(transactions, key = { it.id }) { tx ->
            TransactionRow(tx = tx)
        }
    }
}

@Composable
private fun TransactionRow(tx: WalletTransaction) {
    val ext = LocalPredictPlayColors.current
    val timeFormatter = SimpleDateFormat("MMM d, HH:mm", Locale.US)
    val isPositive = tx.amountDelta >= 0

    val icon = when (tx.type) {
        TransactionType.INITIAL_GRANT -> Icons.Filled.MonetizationOn
        TransactionType.DAILY_BONUS -> Icons.Filled.CardGiftcard
        TransactionType.BET_PLACED -> Icons.Filled.Receipt
        TransactionType.BET_WON -> Icons.Filled.TrendingUp
        TransactionType.RESET -> Icons.Filled.Refresh
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, ext.borderSubtle),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPositive) MaterialTheme.colorScheme.primaryContainer else ext.surfaceHighest,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isPositive) MaterialTheme.colorScheme.primary else ext.textSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Column {
                    Text(
                        text = tx.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${tx.subtitle} • ${timeFormatter.format(Date(tx.timestampMillis))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ext.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val sign = if (isPositive) "+" else ""
                Text(
                    text = "$sign${formatCoins(tx.amountDelta)} VC",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isPositive) ext.oddsUp else ext.oddsDown,
                )
                Text(
                    text = "Bal ${formatCoins(tx.balanceAfter)}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily),
                    color = ext.textMuted,
                )
            }
        }
    }
}

private fun formatMillisCountdown(millis: Long): String {
    val totalSeconds = (millis / 1000L).coerceAtLeast(0L)
    val hrs = totalSeconds / 3600L
    val mins = (totalSeconds % 3600L) / 60L
    val secs = totalSeconds % 60L
    return String.format(Locale.US, "%02dh %02dm %02ds", hrs, mins, secs)
}
