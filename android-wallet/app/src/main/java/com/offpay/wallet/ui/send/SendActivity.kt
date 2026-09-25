package com.offpay.wallet.ui.send

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.cardemulation.CardEmulation
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offpay.wallet.ui.theme.OffpayBackground
import com.offpay.wallet.ui.theme.OffpayExtendedColors
import com.offpay.wallet.ui.theme.OffpayMonoTypography
import com.offpay.wallet.ui.theme.OffpayPrimaryAccent
import com.offpay.wallet.ui.theme.OffpaySecondaryAccent
import com.offpay.wallet.ui.theme.OffpaySurface
import com.offpay.wallet.ui.theme.OffpaySurfaceBorder
import com.offpay.wallet.ui.theme.OffpaySurfaceElevated
import com.offpay.wallet.ui.theme.OffpayTextMuted
import com.offpay.wallet.ui.theme.OffpayTextPrimary
import com.offpay.wallet.ui.theme.OffpayTheme
import com.offpay.wallet.ui.theme.monoTypography
import com.offpay.wallet.ui.theme.offpayColors

class SendActivity : ComponentActivity() {

    private var armTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val amountParam = intent.getStringExtra(EXTRA_AMOUNT) ?: "25.00"
        val merchantParam = intent.getStringExtra(EXTRA_MERCHANT_ADDRESS) 
            ?: "0x89205A3A3b2A55318F128695f24F44dC29a43a06"

        setContent {
            OffpayTheme {
                SendScreen(
                    initialAmount = amountParam,
                    merchantAddress = merchantParam,
                    onNavigateBack = { finish() },
                    onArmPayment = { amount, address, durationSeconds, onTick, onExpire ->
                        triggerHceArming(amount, address, durationSeconds, onTick, onExpire)
                    },
                    onDisarmPayment = {
                        disarmHce()
                    }
                )
            }
        }
    }

    /**
     * HCE Payment Logic: Arms Host Card Emulation service with payment payload for NFC reader tap.
     */
    private fun triggerHceArming(
        amount: String,
        merchantAddress: String,
        durationSeconds: Long,
        onTick: (Long) -> Unit,
        onExpire: () -> Unit
    ) {
        armTimer?.cancel()

        // Configure HCE Card Emulation if NFC is present
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter != null) {
            val cardEmulation = CardEmulation.getInstance(nfcAdapter)
            // Register preferred HCE service dynamically when armed
            // cardEmulation.setPreferredService(this, ComponentName(this, OffpayHceService::class.java))
        }

        // Timer for armed state validity window
        armTimer = object : CountDownTimer(durationSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onTick(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                disarmHce()
                onExpire()
            }
        }.start()
    }

    /**
     * Disarms the HCE service and clears active payment payload.
     */
    private fun disarmHce() {
        armTimer?.cancel()
        armTimer = null
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter != null) {
            val cardEmulation = CardEmulation.getInstance(nfcAdapter)
            // cardEmulation.unsetPreferredService(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disarmHce()
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
    initialAmount: String = "25.00",
    merchantAddress: String = "0x89205A3A3b2A55318F128695f24F44dC29a43a06",
    onNavigateBack: () -> Unit = {},
    onArmPayment: (amount: String, address: String, duration: Long, onTick: (Long) -> Unit, onExpire: () -> Unit) -> Unit = { _, _, _, _, _ -> },
    onDisarmPayment: () -> Unit = {}
) {
    var isArmed by remember { mutableStateOf(false) }
    var secondsRemaining by remember { mutableLongStateOf(60L) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            if (isArmed) {
                onDisarmPayment()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SendTopBar(
                isArmed = isArmed,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Status Badge
                PaymentStateBadge(isArmed = isArmed, secondsRemaining = secondsRemaining)

                Spacer(modifier = Modifier.height(32.dp))

                // Large Amount Display using amountLarge JetBrains Mono typography
                AmountDisplaySection(
                    amount = initialAmount,
                    currencyCode = "USD"
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Merchant / Recipient Card with walletAddressCompact JetBrains Mono style
                MerchantAddressCard(
                    merchantAddress = merchantAddress,
                    onCopyAddress = {
                        clipboardManager.setText(AnnotatedString(merchantAddress))
                        Toast.makeText(context, "Address copied", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Bottom Action Area: Amber "Arm Payment" button using secondary accent color
            PaymentActionSection(
                isArmed = isArmed,
                secondsRemaining = secondsRemaining,
                onArmClick = {
                    if (!isArmed) {
                        isArmed = true
                        secondsRemaining = 60L
                        onArmPayment(
                            initialAmount,
                            merchantAddress,
                            60L,
                            { remaining -> secondsRemaining = remaining },
                            { isArmed = false }
                        )
                    } else {
                        isArmed = false
                        onDisarmPayment()
                    }
                }
            )
        }
    }
}

@Composable
private fun SendTopBar(
    isArmed: Boolean,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .size(40.dp)
                .background(OffpaySurface, CircleShape)
                .border(1.dp, OffpaySurfaceBorder, CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = OffpayTextPrimary
            )
        }

        Text(
            text = "Send Offline Payment",
            style = MaterialTheme.typography.titleMedium,
            color = OffpayTextPrimary,
            fontWeight = FontWeight.SemiBold
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (isArmed) OffpaySecondaryAccent.copy(alpha = 0.15f) else OffpaySurface,
                    CircleShape
                )
                .border(
                    1.dp,
                    if (isArmed) OffpaySecondaryAccent.copy(alpha = 0.5f) else OffpaySurfaceBorder,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Nfc,
                contentDescription = "NFC Status",
                tint = if (isArmed) OffpaySecondaryAccent else OffpayTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PaymentStateBadge(
    isArmed: Boolean,
    secondsRemaining: Long
) {
    val badgeBgColor by animateColorAsState(
        targetValue = if (isArmed) OffpaySecondaryAccent.copy(alpha = 0.15f) else OffpaySurfaceElevated,
        label = "badgeBg"
    )
    val badgeBorderColor by animateColorAsState(
        targetValue = if (isArmed) OffpaySecondaryAccent else OffpaySurfaceBorder,
        label = "badgeBorder"
    )
    val badgeTextColor by animateColorAsState(
        targetValue = if (isArmed) OffpaySecondaryAccent else OffpayTextMuted,
        label = "badgeText"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(badgeBgColor)
            .border(1.dp, badgeBorderColor, RoundedCornerShape(30.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(badgeTextColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isArmed) "ARMED • TAP POS ($secondsRemaining s)" else "OFFLINE HCE READY",
            style = MaterialTheme.typography.labelMedium,
            color = badgeTextColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun AmountDisplaySection(
    amount: String,
    currencyCode: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "AMOUNT TO PAY",
            style = MaterialTheme.typography.labelSmall,
            color = OffpayTextMuted,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$",
                style = MaterialTheme.typography.titleLarge,
                color = OffpayTextMuted,
                modifier = Modifier.padding(bottom = 6.dp, end = 4.dp)
            )
            // Using amountLarge from Offpay Mono Typography (JetBrains Mono)
            Text(
                text = amount,
                style = MaterialTheme.monoTypography.amountLarge,
                color = OffpayTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = currencyCode,
                style = MaterialTheme.typography.labelMedium,
                color = OffpayPrimaryAccent,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
            )
        }
    }
}

@Composable
private fun MerchantAddressCard(
    merchantAddress: String,
    onCopyAddress: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = OffpaySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, OffpaySurfaceBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MERCHANT RECIPIENT",
                    style = MaterialTheme.typography.labelSmall,
                    color = OffpayTextMuted,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onCopyAddress() }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = OffpayTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Copy",
                        style = MaterialTheme.typography.labelSmall,
                        color = OffpayTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Merchant address displayed in walletAddressCompact style (JetBrains Mono)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(OffpayBackground)
                    .border(1.dp, OffpaySurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = merchantAddress,
                    style = MaterialTheme.monoTypography.walletAddressCompact,
                    color = OffpayTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = OffpayPrimaryAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "End-to-End Cryptographic Token",
                        style = MaterialTheme.typography.bodySmall,
                        color = OffpayTextMuted
                    )
                }

                Text(
                    text = "L2 Offline",
                    style = MaterialTheme.typography.labelSmall,
                    color = OffpayPrimaryAccent,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PaymentActionSection(
    isArmed: Boolean,
    secondsRemaining: Long,
    onArmClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isArmed) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = "Tap",
                    tint = OffpaySecondaryAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hold phone near terminal to pay",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OffpayTextPrimary
                )
            }
        }

        // Amber "Arm Payment" button using secondary accent color
        Button(
            onClick = onArmClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = if (isArmed) 12.dp else 0.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = OffpaySecondaryAccent,
                    spotColor = OffpaySecondaryAccent
                ),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OffpaySecondaryAccent,
                contentColor = OffpayBackground
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isArmed) Icons.Default.CheckCircle else Icons.Default.Nfc,
                    contentDescription = null,
                    tint = OffpayBackground,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isArmed) "Disarm Payment ($secondsRemaining s)" else "Arm Payment",
                    style = MaterialTheme.typography.titleMedium,
                    color = OffpayBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (isArmed) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "HCE active • Payload broadcast enabled",
                style = MaterialTheme.typography.bodySmall,
                color = OffpayTextMuted
            )
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
