package com.offpay.wallet.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================================================
// Warm Editorial Tactile Color Palette
// ============================================================================

// Canvas & Surface
val WarmBackground = Color(0xFFF7F5F0)
val WarmCardSurface = Color(0xFFFFFFFF)
val WarmCardBorder = Color(0xFFE5E1D8)

// Recessed / Well Containers (inputs, ledger trays, envelopes)
val WarmRecessedSurface = Color(0xFFFAF8F5)
val WarmRecessedBorder = Color(0xFFEFEBE3)

// Text Tokens
val WarmTextPrimary = Color(0xFF1C1B19)
val WarmTextSecondary = Color(0xFF6B6862)
val WarmTextMuted = Color(0xFF6B6862)

// Dividers & Hairlines
val WarmDivider = Color(0xFFD8D3C8)

// Primary Accent - Terracotta
val TerracottaPrimary = Color(0xFFC1652F)
val TerracottaHover = Color(0xFFA85324)
val TerracottaWash = Color(0xFFFAF0EA)
val TerracottaGlow = Color(0x33C1652F)

// Secondary Accent - Sage Green
val SageSecondary = Color(0xFF7A8B6F)
val SageWash = Color(0xFFEFF3ED)
val SageTextOnWash = Color(0xFF536549)

// Error & Alert
val WarmError = Color(0xFFA8382B)
val WarmErrorWash = Color(0xFFFDF1F0)

// Info / Metadata
val WarmInfo = Color(0xFF465B66)
val WarmInfoWash = Color(0xFFEFF4F7)

// Semantic State Mappings
val WarmSettled = SageSecondary
val WarmOnline = SageSecondary
val WarmArmed = TerracottaPrimary
val WarmPending = TerracottaPrimary

/**
 * Extended color tokens for custom Warm Editorial Tactile UI states.
 */
@Immutable
data class OffpayExtendedColors(
    val background: Color = WarmBackground,
    val cardSurface: Color = WarmCardSurface,
    val cardBorder: Color = WarmCardBorder,
    val recessedSurface: Color = WarmRecessedSurface,
    val recessedBorder: Color = WarmRecessedBorder,
    val textPrimary: Color = WarmTextPrimary,
    val textSecondary: Color = WarmTextSecondary,
    val textMuted: Color = WarmTextMuted,
    val divider: Color = WarmDivider,
    val primaryTerracotta: Color = TerracottaPrimary,
    val primaryTerracottaHover: Color = TerracottaHover,
    val primaryTerracottaWash: Color = TerracottaWash,
    val secondarySage: Color = SageSecondary,
    val secondarySageWash: Color = SageWash,
    val secondarySageText: Color = SageTextOnWash,
    val error: Color = WarmError,
    val errorWash: Color = WarmErrorWash,
    val info: Color = WarmInfo,
    val infoWash: Color = WarmInfoWash
)

val LocalOffpayColors = staticCompositionLocalOf { OffpayExtendedColors() }
