package com.offpay.wallet.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Core Palette Tokens
val OffpayBackground = Color(0xFF0B0F0E)
val OffpaySurface = Color(0xFF161B1A)
val OffpaySurfaceElevated = Color(0xFF1E2523)
val OffpaySurfaceBorder = Color(0xFF27312E)

// Primary Accent - Green (Settled / Success / Online)
val OffpayPrimaryAccent = Color(0xFF39FF88)
val OffpayPrimaryAccentDim = Color(0xFF1F8044)
val OffpayPrimaryAccentGlow = Color(0x3339FF88)

// Secondary Accent - Amber (Pending / Offline / Armed)
val OffpaySecondaryAccent = Color(0xFFFFB020)
val OffpaySecondaryAccentDim = Color(0xFF8A5E11)
val OffpaySecondaryAccentGlow = Color(0x33FFB020)

// Text Tokens
val OffpayTextPrimary = Color(0xFFF2F5F4)
val OffpayTextMuted = Color(0xFF8A928F)
val OffpayTextDisabled = Color(0xFF525957)

// Error / Alert Tokens
val OffpayError = Color(0xFFFF5C5C)
val OffpayErrorDim = Color(0xFF802E2E)
val OffpayErrorGlow = Color(0x33FF5C5C)

// Semantic State Colors
val OffpaySettled = OffpayPrimaryAccent
val OffpayOnline = OffpayPrimaryAccent
val OffpaySuccess = OffpayPrimaryAccent

val OffpayPending = OffpaySecondaryAccent
val OffpayOffline = OffpaySecondaryAccent
val OffpayArmed = OffpaySecondaryAccent

/**
 * Extended color tokens for custom Offpay domain-specific UI states.
 */
@Immutable
data class OffpayExtendedColors(
    val background: Color = OffpayBackground,
    val surface: Color = OffpaySurface,
    val surfaceElevated: Color = OffpaySurfaceElevated,
    val surfaceBorder: Color = OffpaySurfaceBorder,
    val primaryAccent: Color = OffpayPrimaryAccent,
    val primaryAccentDim: Color = OffpayPrimaryAccentDim,
    val secondaryAccent: Color = OffpaySecondaryAccent,
    val secondaryAccentDim: Color = OffpaySecondaryAccentDim,
    val textPrimary: Color = OffpayTextPrimary,
    val textMuted: Color = OffpayTextMuted,
    val textDisabled: Color = OffpayTextDisabled,
    val error: Color = OffpayError,
    val settled: Color = OffpaySettled,
    val online: Color = OffpayOnline,
    val success: Color = OffpaySuccess,
    val pending: Color = OffpayPending,
    val offline: Color = OffpayOffline,
    val armed: Color = OffpayArmed
)

val LocalOffpayColors = staticCompositionLocalOf { OffpayExtendedColors() }
