package com.offpay.wallet.ui.send

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offpay.wallet.HomeActivity
import com.offpay.wallet.ui.common.OffpayNavTab
import com.offpay.wallet.ui.common.WarmBottomNavBar
import com.offpay.wallet.ui.common.WarmOffpayTopBar
import com.offpay.wallet.ui.receive.ReceiveActivity
import com.offpay.wallet.ui.settled.SettledActivity
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
import com.offpay.wallet.ui.theme.WarmRecessedBorder
import com.offpay.wallet.ui.theme.WarmRecessedSurface
import com.offpay.wallet.ui.theme.WarmTextMuted
import com.offpay.wallet.ui.theme.WarmTextPrimary
import com.offpay.wallet.ui.theme.WarmTextSecondary
import com.offpay.wallet.ui.theme.monoTypography
import kotlinx.coroutines.delay

class SendActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val amountParam = intent.getStringExtra(EXTRA_AMOUNT) ?: "0.50"
        val merchantParam = intent.getStringExtra(EXTRA_MERCHANT_ADDRESS) 
            ?: "0x89205A3A3b2A55318F128695f24F44dC29a43a06"

        setContent {
            OffpayTheme {
                SendScreen(
                    initialAmount = amountParam,
                    merchantAddress = merchantParam,
                    onNavigateBack = { finish() },
                    onPaymentSettled = { amount, address ->
                        val intent = SettledActivity.createIntent(this, amount, address)
                        startActivity(intent)
                        finish()
                    },
                    onTabSelected = { tab ->
                        when (tab) {
                            OffpayNavTab.VAULT -> {
                                val intent = Intent(this, HomeActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                }
                                startActivity(intent)
                                finish()
                            }
                            OffpayNavTab.RECEIVE -> {
                                startActivity(Intent(this, ReceiveActivity::class.java))
                                finish()
                            }
                            OffpayNavTab.ACTIVITY -> {
                                Toast.makeText(this, "Recent transactions synced", Toast.LENGTH_SHORT).show()
                            }
                            OffpayNavTab.SEND -> {}
                        }
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_AMOUNT = "extra_amount"
        const val EXTRA_MERCHANT_ADDRESS = "extra_merchant_address"

        fun createIntent(context: Context, amount: String, merchantAddress: String): Intent {
            return Intent(context, SendActivity::class.java).apply {
                putExtra(EXTRA_AMOUNT, amount)
                putExtra(EXTRA_MERCHANT_ADDRESS, merchantAddress)
            }
        }
    }
}

@Composable
fun SendScreen(
    initialAmount: String = "0.50",
    merchantAddress: String = "0x89205A3A3b2A55318F128695f24F44dC29a43a06",
    onNavigateBack: () -> Unit = {},
    onPaymentSettled: (amount: String, address: String) -> Unit = { _, _ -> },
    onTabSelected: (OffpayNavTab) -> Unit = {}
) {
    var selectedAmount by remember { mutableStateOf(initialAmount) }
    var isArmed by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // 2-second simulation timer when payment is armed
    LaunchedEffect(isArmed) {
        if (isArmed) {
            delay(2000L)
            onPaymentSettled(selectedAmount, merchantAddress)
        }
    }

    Scaffold(
        containerColor = WarmBackground,
        topBar = {
            WarmOffpayTopBar()
        },
        bottomBar = {
            WarmBottomNavBar(
                selectedTab = OffpayNavTab.SEND,
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
                // Headline & Subtitle
                SendHeaderSection(onBackClick = onNavigateBack)
            }

            item {
                // White Amount Card with 4 Quick-Select Chips
                SendAmountCard(
                    selectedAmount = selectedAmount,
                    onAmountSelect = { selectedAmount = it }
                )
            }

            item {
                // Recipient Card: Chai Merchant
                RecipientMerchantCard(
                    merchantAddress = merchantAddress,
                    onCopy = {
                        clipboardManager.setText(AnnotatedString(merchantAddress))
                        Toast.makeText(context, "Address copied", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                // Pulsing Amber / Terracotta Ring Animation during simulated tap
                AnimatedVisibility(
                    visible = isArmed,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    WarmPulsingNfcSection()
                }
            }

            item {
                // Full-width Terracotta Arm Payment Button
                Button(
                    onClick = { if (!isArmed) isArmed = true },
                    enabled = !isArmed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TerracottaPrimary,
                        contentColor = Color.White,
                        disabledContainerColor = TerracottaPrimary.copy(alpha = 0.6f),
                        disabledContentColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isArmed) "⚡ Broadcasting Voucher..." else "⚡ Arm Payment ($$selectedAmount)",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Helper Muted Text
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hold near merchant's phone or reader",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Signed locally on-device • No internet needed",
                        style = MaterialTheme.typography.bodySmall,
                        color = SageTextOnWash,
                        fontSize = 11.sp
                    )
                }
            }

            item {
                // Cryptographic Envelope Card
                CryptographicEnvelopeCard()
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SendHeaderSection(onBackClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(WarmRecessedSurface)
                    .border(1.dp, WarmRecessedBorder, RoundedCornerShape(4.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = WarmTextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Send Payment",
                fontFamily = PlayfairDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = WarmTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "Prepare an offline payment voucher",
            style = MaterialTheme.typography.bodyMedium,
            color = WarmTextSecondary,
            modifier = Modifier.padding(start = 38.dp)
        )
    }
}

@Composable
private fun SendAmountCard(
    selectedAmount: String,
    onAmountSelect: (String) -> Unit
) {
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
            Text(
                text = "AMOUNT TO PAY",
                style = MaterialTheme.typography.labelSmall,
                color = WarmTextMuted,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Huge Amount in Space Mono
            Text(
                text = "$$selectedAmount",
                style = MaterialTheme.monoTypography.amountHuge,
                color = WarmTextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Quick-Select Amount Chips ($0.25, $0.50, $1.00, $2.00 Max)
            val chipOptions = listOf("0.25", "0.50", "1.00", "2.00")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chipOptions.forEach { amount ->
                    val isSelected = selectedAmount == amount
                    val label = if (amount == "2.00") "$2.00 Max" else "$$amount"

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onAmountSelect(amount) },
                        shape = RoundedCornerShape(4.dp),
                        color = if (isSelected) TerracottaPrimary else WarmRecessedSurface,
                        border = BorderStroke(1.dp, if (isSelected) TerracottaPrimary else WarmRecessedBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color.White else WarmTextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(thickness = 1.dp, color = WarmDivider)
            Spacer(modifier = Modifier.height(10.dp))

            // Offline vault reserve
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Offline vault reserve: $18.50",
                    style = MaterialTheme.monoTypography.voucherId,
                    color = WarmTextSecondary
                )
                Text(
                    text = "Single-tap OK",
                    style = MaterialTheme.typography.bodySmall,
                    color = SageTextOnWash,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun RecipientMerchantCard(
    merchantAddress: String,
    onCopy: () -> Unit
) {
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
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(TerracottaWash),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = TerracottaPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Chai Merchant",
                                style = MaterialTheme.typography.titleMedium,
                                color = WarmTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SageWash
                            ) {
                                Text(
                                    text = "Verified",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SageTextOnWash,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "0x8920...3a06",
                            style = MaterialTheme.monoTypography.walletAddressCompact,
                            color = WarmTextMuted
                        )
                    }
                }

                IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = WarmTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Locked Note
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
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = TerracottaPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Bound to recipient • Cannot be redirected",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun WarmPulsingNfcSection() {
    val infiniteTransition = rememberInfiniteTransition(label = "nfcPulseWarm")

    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringScale"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier.size(90.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .scale(ringScale)
                    .border(2.dp, TerracottaPrimary.copy(alpha = ringAlpha), CircleShape)
            )

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(TerracottaWash)
                    .border(1.5.dp, TerracottaPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = "Pulsing NFC",
                    tint = TerracottaPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Holding near merchant phone...",
            style = MaterialTheme.typography.bodySmall,
            color = TerracottaPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CryptographicEnvelopeCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = WarmRecessedSurface,
        border = BorderStroke(1.dp, WarmRecessedBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "CRYPTOGRAPHIC ENVELOPE",
                style = MaterialTheme.typography.labelSmall,
                color = WarmTextMuted,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Voucher ID",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmTextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = "#VCH-7741-OFF",
                    style = MaterialTheme.monoTypography.voucherId,
                    color = WarmTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Nonce Proof",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmTextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = "0x3f...e829",
                    style = MaterialTheme.monoTypography.hashOrProof,
                    color = WarmTextMuted
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SendScreenPreview() {
    OffpayTheme {
        SendScreen()
    }
}
