package com.offpay.wallet.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offpay.wallet.ui.theme.PlayfairDisplayFamily
import com.offpay.wallet.ui.theme.SageSecondary
import com.offpay.wallet.ui.theme.SageTextOnWash
import com.offpay.wallet.ui.theme.SageWash
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

/**
 * Top Bar: "OFFPAY" wordmark (serif) + "OFFLINE READY" pill badge (sage) + circular profile icon (terracotta)
 */
@Composable
fun WarmOffpayTopBar(
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "OFFPAY" Wordmark (Serif)
        Text(
            text = "OFFPAY",
            fontFamily = PlayfairDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            letterSpacing = 1.sp,
            color = WarmTextPrimary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // "OFFLINE READY" Pill Badge (Sage)
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
                        text = "OFFLINE READY",
                        style = MaterialTheme.typography.labelSmall,
                        color = SageTextOnWash,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                }
            }

            // Circular Profile Icon (Terracotta)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(TerracottaWash)
                    .border(1.dp, TerracottaPrimary.copy(alpha = 0.3f), CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = TerracottaPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

enum class OffpayNavTab {
    VAULT,
    SEND,
    RECEIVE,
    ACTIVITY
}

/**
 * 4-Tab Bottom Nav Bar (Vault, Send, Receive, Activity) with active tab in terracotta.
 */
@Composable
fun WarmBottomNavBar(
    selectedTab: OffpayNavTab,
    onTabSelected: (OffpayNavTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, ambientColor = Color(0x0A1C1B19)),
        color = WarmCardSurface,
        border = BorderStroke(1.dp, WarmCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabItem(
                label = "Vault",
                icon = Icons.Outlined.AccountBalanceWallet,
                isSelected = selectedTab == OffpayNavTab.VAULT,
                onClick = { onTabSelected(OffpayNavTab.VAULT) }
            )
            NavTabItem(
                label = "Send",
                icon = Icons.Outlined.ArrowOutward,
                isSelected = selectedTab == OffpayNavTab.SEND,
                onClick = { onTabSelected(OffpayNavTab.SEND) }
            )
            NavTabItem(
                label = "Receive",
                icon = Icons.Outlined.Sensors,
                isSelected = selectedTab == OffpayNavTab.RECEIVE,
                onClick = { onTabSelected(OffpayNavTab.RECEIVE) }
            )
            NavTabItem(
                label = "Activity",
                icon = Icons.Outlined.History,
                isSelected = selectedTab == OffpayNavTab.ACTIVITY,
                onClick = { onTabSelected(OffpayNavTab.ACTIVITY) }
            )
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) TerracottaPrimary else WarmTextSecondary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 10.sp
        )
    }
}
