package com.offpay.wallet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offpay.wallet.ui.common.OffpayNavTab
import com.offpay.wallet.ui.common.WarmBottomNavBar
import com.offpay.wallet.ui.common.WarmOffpayTopBar
import com.offpay.wallet.ui.receive.ReceiveActivity
import com.offpay.wallet.ui.send.SendActivity
import com.offpay.wallet.ui.theme.OffpayTheme
import com.offpay.wallet.ui.theme.PlayfairDisplayFamily
import com.offpay.wallet.ui.theme.SageSecondary
import com.offpay.wallet.ui.theme.SageTextOnWash
import com.offpay.wallet.ui.theme.SageWash
import com.offpay.wallet.ui.theme.SpaceMonoFamily
import com.offpay.wallet.ui.theme.TerracottaHover
import com.offpay.wallet.ui.theme.TerracottaPrimary
import com.offpay.wallet.ui.theme.TerracottaWash
import com.offpay.wallet.ui.theme.WarmBackground
import com.offpay.wallet.ui.theme.WarmCardBorder
import com.offpay.wallet.ui.theme.WarmCardSurface
import com.offpay.wallet.ui.theme.WarmDivider
import com.offpay.wallet.ui.theme.WarmInfo
import com.offpay.wallet.ui.theme.WarmInfoWash
import com.offpay.wallet.ui.theme.WarmRecessedBorder
import com.offpay.wallet.ui.theme.WarmRecessedSurface
import com.offpay.wallet.ui.theme.WarmTextMuted
import com.offpay.wallet.ui.theme.WarmTextPrimary
import com.offpay.wallet.ui.theme.WarmTextSecondary
import com.offpay.wallet.ui.theme.monoTypography

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OffpayTheme {
                HomeScreen(
                    onSendClick = {
                        startActivity(Intent(this, SendActivity::class.java))
                    },
                    onReceiveClick = {
                        startActivity(Intent(this, ReceiveActivity::class.java))
                    },
                    onTabSelected = { tab ->
                        when (tab) {
                            OffpayNavTab.SEND -> startActivity(Intent(this, SendActivity::class.java))
                            OffpayNavTab.RECEIVE -> startActivity(Intent(this, ReceiveActivity::class.java))
                            OffpayNavTab.ACTIVITY -> Toast.makeText(this, "All activity records synced", Toast.LENGTH_SHORT).show()
                            OffpayNavTab.VAULT -> {}
                        }
                    }
                )
            }
        }
    }
}

data class TransactionItem(
    val id: String,
    val merchant: String,
    val method: String,
    val timestamp: String,
    val amount: String,
    val isDebit: Boolean,
    val status: String
)

private val fakeWarmTransactions = listOf(
    TransactionItem(
        id = "tx-1",
        merchant = "Chai Stall #04",
        method = "NFC Tap",
        timestamp = "10m ago",
        amount = "$0.50",
        isDebit = true,
        status = "Settled"
    ),
    TransactionItem(
        id = "tx-2",
        merchant = "Alice (P2P Receive)",
        method = "BLE Mesh",
        timestamp = "2h ago",
        amount = "$1.20",
        isDebit = false,
        status = "Settled"
    ),
    TransactionItem(
        id = "tx-3",
        merchant = "Metro Rail Terminal",
        method = "NFC Tap",
        timestamp = "Yesterday",
        amount = "$2.00",
        isDebit = true,
        status = "Settled"
    )
)

@Composable
fun HomeScreen(
    balance: String = "$5.00",
    voucherToken: String = "#089",
    onSendClick: () -> Unit = {},
    onReceiveClick: () -> Unit = {},
    onTabSelected: (OffpayNavTab) -> Unit = {}
) {
    Scaffold(
        containerColor = WarmBackground,
        topBar = {
            WarmOffpayTopBar()
        },
        bottomBar = {
            WarmBottomNavBar(
                selectedTab = OffpayNavTab.VAULT,
                onTabSelected = onTabSelected
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
                // Status Pill: "Ready for offline tap" + "NFC · BLE" indicator
                OfflineStatusHeaderRow()
            }

            item {
                // Large White Card: AVAILABLE VAULT BALANCE
                VaultBalanceCard(
                    balance = balance,
                    voucherToken = voucherToken
                )
            }

            item {
                // Side-by-side action cards: Send (terracotta) and Receive (white outline)
                ActionCardsSection(
                    onSendClick = onSendClick,
                    onReceiveClick = onReceiveClick
                )
            }

            item {
                // Auto-refill Reservoir Row
                AutoRefillReservoirRow()
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                // Recent Activity Header with "SYNCED" badge
                RecentActivityHeader()
            }

            // Transaction rows
            items(fakeWarmTransactions) { tx ->
                WarmTransactionRow(transaction = tx)
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun OfflineStatusHeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "Ready for offline tap" status pill
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = WarmRecessedSurface,
            border = BorderStroke(1.dp, WarmRecessedBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(SageSecondary, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ready for offline tap",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // "NFC · BLE" indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Nfc,
                contentDescription = null,
                tint = SageSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "NFC · BLE",
                style = MaterialTheme.typography.labelSmall,
                color = WarmTextMuted,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun VaultBalanceCard(
    balance: String,
    voucherToken: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Color(0x0A1C1B19),
                spotColor = Color(0x0A1C1B19)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = WarmCardSurface),
        border = BorderStroke(1.dp, WarmCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Label Row: "AVAILABLE VAULT BALANCE" + "USDC" Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AVAILABLE VAULT BALANCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmTextMuted,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = WarmInfoWash,
                    border = BorderStroke(1.dp, WarmInfo.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "USDC",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarmInfo,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Huge Space Mono Balance "$5.00"
            Text(
                text = balance,
                style = MaterialTheme.monoTypography.amountHuge,
                color = WarmTextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "USD VALUE",
                style = MaterialTheme.typography.labelSmall,
                color = WarmTextMuted,
                letterSpacing = 0.8.sp,
                fontSize = 9.sp
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(thickness = 1.dp, color = WarmDivider)
            Spacer(modifier = Modifier.height(12.dp))

            // Voucher token #089
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Voucher token $voucherToken",
                    style = MaterialTheme.monoTypography.voucherId,
                    color = WarmTextSecondary
                )
                Text(
                    text = "Secured Enclave",
                    style = MaterialTheme.typography.bodySmall,
                    color = SageTextOnWash,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Collateral verified on-chain • $2.00 per-tap limit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(WarmRecessedSurface)
                    .border(1.dp, WarmRecessedBorder, RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = SageSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Collateral verified on-chain • $2.00 per-tap limit",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun ActionCardsSection(
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Send Card: Filled Terracotta
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(96.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSendClick() },
            shape = RoundedCornerShape(8.dp),
            color = TerracottaPrimary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Send",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "INSTANT",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Authorize tap",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowOutward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Receive Card: White Outline Card
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(96.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onReceiveClick() },
            shape = RoundedCornerShape(8.dp),
            color = WarmCardSurface,
            border = BorderStroke(1.dp, WarmCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Receive",
                        style = MaterialTheme.typography.titleMedium,
                        color = WarmTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = SageWash
                    ) {
                        Text(
                            text = "LISTENING",
                            style = MaterialTheme.typography.labelSmall,
                            color = SageTextOnWash,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Accept payment",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmTextSecondary,
                        fontSize = 11.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = SageSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AutoRefillReservoirRow() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = WarmRecessedSurface,
        border = BorderStroke(1.dp, WarmRecessedBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Auto-refill Reservoir",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$20.00 queued when online",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmTextMuted,
                    fontSize = 11.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = WarmCardSurface,
                border = BorderStroke(1.dp, WarmCardBorder),
                modifier = Modifier.clickable { }
            ) {
                Text(
                    text = "Adjust",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun RecentActivityHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recent Activity",
            fontFamily = PlayfairDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = WarmTextPrimary
        )

        Surface(
            shape = RoundedCornerShape(4.dp),
            color = SageWash,
            border = BorderStroke(1.dp, SageSecondary.copy(alpha = 0.25f))
        ) {
            Text(
                text = "SYNCED",
                style = MaterialTheme.typography.labelSmall,
                color = SageTextOnWash,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun WarmTransactionRow(
    transaction: TransactionItem
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = WarmCardSurface,
        border = BorderStroke(1.dp, WarmCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (transaction.isDebit) TerracottaWash else SageWash),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (transaction.isDebit) Icons.Default.ArrowOutward else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (transaction.isDebit) TerracottaPrimary else SageSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = transaction.merchant,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${transaction.timestamp} · ${transaction.method}",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (transaction.isDebit) "-${transaction.amount}" else "+${transaction.amount}",
                    style = MaterialTheme.monoTypography.amountSmall,
                    color = if (transaction.isDebit) WarmTextPrimary else SageSecondary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = SageTextOnWash,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    OffpayTheme {
        HomeScreen()
    }
}
