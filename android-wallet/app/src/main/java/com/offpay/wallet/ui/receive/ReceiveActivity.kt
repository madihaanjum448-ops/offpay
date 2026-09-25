package com.offpay.wallet.ui.receive

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offpay.wallet.HomeActivity
import com.offpay.wallet.ui.common.OffpayNavTab
import com.offpay.wallet.ui.common.WarmBottomNavBar
import com.offpay.wallet.ui.common.WarmOffpayTopBar
import com.offpay.wallet.ui.send.SendActivity
import com.offpay.wallet.ui.theme.OffpayTheme
import com.offpay.wallet.ui.theme.PlayfairDisplayFamily
import com.offpay.wallet.ui.theme.SageSecondary
import com.offpay.wallet.ui.theme.SageTextOnWash
import com.offpay.wallet.ui.theme.SageWash
import com.offpay.wallet.ui.theme.SpaceMonoFamily
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

class ReceiveActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OffpayTheme {
                ReceiveScreen(
                    onTabSelected = { tab ->
                        when (tab) {
                            OffpayNavTab.VAULT -> {
                                val intent = Intent(this, HomeActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                }
                                startActivity(intent)
                                finish()
                            }
                            OffpayNavTab.SEND -> {
                                startActivity(Intent(this, SendActivity::class.java))
                                finish()
                            }
                            OffpayNavTab.ACTIVITY -> {
                                Toast.makeText(this, "Intake ledger synced", Toast.LENGTH_SHORT).show()
                            }
                            OffpayNavTab.RECEIVE -> {}
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ReceiveScreen(
    onTabSelected: (OffpayNavTab) -> Unit = {}
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = WarmBackground,
        topBar = {
            WarmOffpayTopBar()
        },
        bottomBar = {
            WarmBottomNavBar(
                selectedTab = OffpayNavTab.RECEIVE,
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
                // Mode header: "TERMINAL / RECEIVE MODE" + "NFC Armed & Ready" sage badge
                ReceiveModeHeaderRow()
            }

            item {
                // Large Card with Concentric Soft Sage Rings + Terracotta NFC Icon + "Waiting for tap..."
                ReceiveWaitingForTapCard(
                    onChangeAmount = {
                        Toast.makeText(context, "Configurable terminal amount prompt", Toast.LENGTH_SHORT).show()
                    },
                    onShowQr = {
                        Toast.makeText(context, "Offline Dynamic QR Code active", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                // TODAY'S OFFLINE INTAKE Card
                TodayOfflineIntakeCard()
            }

            item {
                // Two Small Stat Cards: Last Handshake & Hardware Enclave
                TwoStatCardsRow()
            }

            item {
                Spacer(modifier = Modifier.height(2.dp))
                // Muted Troubleshooting Link
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Toast.makeText(context, "NFC antenna optimal placement: top 20mm of device", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Terminal tap troubleshooting guide",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ReceiveModeHeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "TERMINAL / RECEIVE MODE",
            style = MaterialTheme.typography.labelSmall,
            color = WarmTextMuted,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.SemiBold
        )

        Surface(
            shape = RoundedCornerShape(4.dp),
            color = SageWash,
            border = BorderStroke(1.dp, SageSecondary.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(SageSecondary, CircleShape)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "NFC Armed & Ready",
                    style = MaterialTheme.typography.labelSmall,
                    color = SageTextOnWash,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun ReceiveWaitingForTapCard(
    onChangeAmount: () -> Unit,
    onShowQr: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sageRings")

    val ringScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringScale1"
    )
    val ringAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringAlpha1"
    )

    val ringScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, delayMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringScale2"
    )
    val ringAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, delayMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringAlpha2"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(8.dp), ambientColor = Color(0x0A1C1B19)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = WarmCardSurface),
        border = BorderStroke(1.dp, WarmCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Concentric soft sage rings around centered terracotta NFC icon chip
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Ring 2
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .scale(ringScale2)
                        .border(1.5.dp, SageSecondary.copy(alpha = ringAlpha2), CircleShape)
                )
                // Ring 1
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .scale(ringScale1)
                        .border(1.5.dp, SageSecondary.copy(alpha = ringAlpha1), CircleShape)
                )

                // Center Terracotta NFC Chip
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TerracottaWash)
                        .border(1.dp, TerracottaPrimary.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Nfc,
                        contentDescription = null,
                        tint = TerracottaPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // "Waiting for tap..." (Playfair Display Serif)
            Text(
                text = "Waiting for tap...",
                fontFamily = PlayfairDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = WarmTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Instructions
            Text(
                text = "Hold the customer's phone or card steadily against the top back of this terminal",
                style = MaterialTheme.typography.bodySmall,
                color = WarmTextSecondary,
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pill: "Phone-to-Phone NFC & Smart Cards"
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = WarmRecessedSurface,
                border = BorderStroke(1.dp, WarmRecessedBorder)
            ) {
                Text(
                    text = "Phone-to-Phone NFC & Smart Cards",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmTextMuted,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Two buttons: Change Amount & Show QR (4px rounded)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onChangeAmount() },
                    shape = RoundedCornerShape(4.dp),
                    color = WarmRecessedSurface,
                    border = BorderStroke(1.dp, WarmRecessedBorder)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Change Amount",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onShowQr() },
                    shape = RoundedCornerShape(4.dp),
                    color = WarmRecessedSurface,
                    border = BorderStroke(1.dp, WarmRecessedBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = null,
                            tint = WarmTextPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Show QR",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayOfflineIntakeCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(8.dp), ambientColor = Color(0x0A1C1B19)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = WarmCardSurface),
        border = BorderStroke(1.dp, WarmCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: TODAY'S OFFLINE INTAKE + VAULT OK
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S OFFLINE INTAKE",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmTextMuted,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = SageWash,
                    border = BorderStroke(1.dp, SageSecondary.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = "VAULT OK",
                        style = MaterialTheme.typography.labelSmall,
                        color = SageTextOnWash,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Large Space Mono: $4.20 USD + 8 Vouchers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$4.20 USD",
                    style = MaterialTheme.monoTypography.amountLarge,
                    color = WarmTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "8 Vouchers",
                    style = MaterialTheme.monoTypography.voucherId,
                    color = WarmTextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar: Offline buffer capacity 8/50
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Offline buffer capacity",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmTextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = "8/50",
                    style = MaterialTheme.monoTypography.voucherId,
                    color = TerracottaPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { 8f / 50f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = TerracottaPrimary,
                trackColor = WarmRecessedBorder
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Secured in local Hardware Keystore. Vouchers will auto-settle upon next gateway handshake.",
                style = MaterialTheme.typography.bodySmall,
                color = WarmTextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun TwoStatCardsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Last Handshake
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            color = WarmRecessedSurface,
            border = BorderStroke(1.dp, WarmRecessedBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Last Handshake",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmTextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "2m ago (Local)",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        // Card 2: Hardware Enclave
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            color = WarmRecessedSurface,
            border = BorderStroke(1.dp, WarmRecessedBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Hardware Enclave",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmTextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(SageSecondary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Active & Locked",
                        style = MaterialTheme.typography.bodySmall,
                        color = SageTextOnWash,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiveScreenPreview() {
    OffpayTheme {
        ReceiveScreen()
    }
}
