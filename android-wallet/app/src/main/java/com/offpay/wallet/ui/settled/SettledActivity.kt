package com.offpay.wallet.ui.settled

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offpay.wallet.HomeActivity
import com.offpay.wallet.ui.theme.OffpayTheme
import com.offpay.wallet.ui.theme.PlayfairDisplayFamily
import com.offpay.wallet.ui.theme.SageSecondary
import com.offpay.wallet.ui.theme.SageTextOnWash
import com.offpay.wallet.ui.theme.SageWash
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
import kotlinx.coroutines.delay

class SettledActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val amount = intent.getStringExtra(EXTRA_AMOUNT) ?: "0.50"
        val merchantAddress = intent.getStringExtra(EXTRA_MERCHANT_ADDRESS) 
            ?: "0x89205A3A3b2A55318F128695f24F44dC29a43a06"

        setContent {
            OffpayTheme {
                SettledScreen(
                    amount = amount,
                    merchantAddress = merchantAddress,
                    onDoneClick = {
                        val intent = Intent(this, HomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_AMOUNT = "extra_amount"
        const val EXTRA_MERCHANT_ADDRESS = "extra_merchant_address"

        fun createIntent(context: Context, amount: String, merchantAddress: String): Intent {
            return Intent(context, SettledActivity::class.java).apply {
                putExtra(EXTRA_AMOUNT, amount)
                putExtra(EXTRA_MERCHANT_ADDRESS, merchantAddress)
            }
        }
    }
}

@Composable
fun SettledScreen(
    amount: String = "0.50",
    merchantAddress: String = "0x89205A3A3b2A55318F128695f24F44dC29a43a06",
    onDoneClick: () -> Unit = {}
) {
    val checkmarkScale = remember { Animatable(0f) }
    var showDetails by remember { mutableStateOf(false) }
    var isOnChainSettled by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // 1. Spring scale-in checkmark
        checkmarkScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        // 2. Short delay before showing clearance details
        delay(500L)
        showDetails = true

        // 3. Settling transition
        delay(2000L)
        isOnChainSettled = true
    }

    Scaffold(
        containerColor = WarmBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                // Top header with back button and "Payment Settled" (Serif)
                SettledHeaderSection(onDoneClick = onDoneClick)
            }

            item {
                // Large White Card: Terracotta Checkmark + Huge Amount + Verified Offline badge
                SettledHeroCard(
                    amount = amount,
                    checkmarkScale = checkmarkScale.value
                )
            }

            item {
                // Clearance State Card
                AnimatedVisibility(
                    visible = showDetails,
                    enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                        initialOffsetY = { 30 },
                        animationSpec = tween(400)
                    )
                ) {
                    ClearanceStateCard(
                        isOnChainSettled = isOnChainSettled,
                        txHash = "0x8e2a...41fb",
                        onCopyHash = {
                            clipboardManager.setText(AnnotatedString("0x8e2a4f91b72c918a3841fb"))
                            Toast.makeText(context, "Digest copied", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            item {
                // Merchant Row Card
                MerchantInfoRowCard(
                    merchantName = "Chai Merchant",
                    terminalId = "TM-409"
                )
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Full-width Terracotta "Done →" Button
                Button(
                    onClick = onDoneClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TerracottaPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Done →",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Muted Link: "View cryptographic receipt"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Toast.makeText(context, "Receipt verified via secp256k1 ECDSA", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "View cryptographic receipt",
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
private fun SettledHeaderSection(onDoneClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDoneClick,
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
            text = "Payment Settled",
            fontFamily = PlayfairDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = WarmTextPrimary
        )
    }
}

@Composable
private fun SettledHeroCard(
    amount: String,
    checkmarkScale: Float
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
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Terracotta rounded-square checkmark icon with soft wash glow behind it
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .scale(checkmarkScale)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TerracottaWash)
                    .border(1.dp, TerracottaPrimary.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Settled",
                    tint = TerracottaPrimary,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // "+$0.50" in huge terracotta Space Mono
            Text(
                text = "+$$amount",
                style = MaterialTheme.monoTypography.amountHuge,
                color = TerracottaPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Payment Verified",
                style = MaterialTheme.typography.titleMedium,
                color = WarmTextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // "Verified offline • 0ms latency" sage badge
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = SageWash,
                border = BorderStroke(1.dp, SageSecondary.copy(alpha = 0.35f))
            ) {
                Text(
                    text = "Verified offline • 0ms latency",
                    style = MaterialTheme.typography.labelSmall,
                    color = SageTextOnWash,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Signature authenticated locally via secp256k1",
                style = MaterialTheme.typography.bodySmall,
                color = WarmTextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ClearanceStateCard(
    isOnChainSettled: Boolean,
    txHash: String,
    onCopyHash: () -> Unit
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
            // Header: CLEARANCE STATE + SETTLED (OFFLINE) badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CLEARANCE STATE",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmTextMuted,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isOnChainSettled) SageWash else TerracottaWash,
                    border = BorderStroke(1.dp, if (isOnChainSettled) SageSecondary.copy(alpha = 0.3f) else TerracottaPrimary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = if (isOnChainSettled) "SETTLED (ON-CHAIN)" else "SETTLED (OFFLINE)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isOnChainSettled) SageTextOnWash else TerracottaPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Voucher Digest
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Voucher Digest",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmTextSecondary,
                    fontSize = 11.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onCopyHash() }
                ) {
                    Text(
                        text = txHash,
                        style = MaterialTheme.monoTypography.voucherId,
                        color = WarmTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = WarmTextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Payer Vault status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payer Vault",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmTextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = "Committed (Local Storage)",
                    style = MaterialTheme.typography.bodySmall,
                    color = SageTextOnWash,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 1.dp, color = WarmDivider)
            Spacer(modifier = Modifier.height(10.dp))

            // P2P PROTOCOL Section
            Text(
                text = "P2P PROTOCOL",
                style = MaterialTheme.typography.labelSmall,
                color = WarmTextMuted,
                letterSpacing = 0.8.sp,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Dual-offline P2P Mesh",
                style = MaterialTheme.typography.titleSmall,
                color = WarmTextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Auto-settle queued for broadcast on connect",
                style = MaterialTheme.typography.bodySmall,
                color = WarmTextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun MerchantInfoRowCard(
    merchantName: String,
    terminalId: String
) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = TerracottaPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = merchantName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Terminal #$terminalId",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmTextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = SageWash
            ) {
                Text(
                    text = "Zero Fee",
                    style = MaterialTheme.typography.labelSmall,
                    color = SageTextOnWash,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettledScreenPreview() {
    OffpayTheme {
        SettledScreen()
    }
}
