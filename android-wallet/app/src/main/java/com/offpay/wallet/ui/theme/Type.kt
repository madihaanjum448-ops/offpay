package com.offpay.wallet.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

// ============================================================================
// Google Font Provider Setup
// ============================================================================

val GoogleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = 0
)

// 1. Playfair Display (Serif) for Headings & Display
val PlayfairDisplayFont = GoogleFont("Playfair Display")
val PlayfairDisplayFamily = FontFamily(
    Font(googleFont = PlayfairDisplayFont, fontProvider = GoogleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = PlayfairDisplayFont, fontProvider = GoogleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = PlayfairDisplayFont, fontProvider = GoogleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = PlayfairDisplayFont, fontProvider = GoogleFontProvider, weight = FontWeight.Bold)
)

// 2. Plus Jakarta Sans for Body Text, Buttons, Labels
val PlusJakartaSansFont = GoogleFont("Plus Jakarta Sans")
val PlusJakartaSansFamily = FontFamily(
    Font(googleFont = PlusJakartaSansFont, fontProvider = GoogleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = PlusJakartaSansFont, fontProvider = GoogleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = PlusJakartaSansFont, fontProvider = GoogleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = PlusJakartaSansFont, fontProvider = GoogleFontProvider, weight = FontWeight.Bold)
)

// 3. Space Mono for Amounts, Addresses, Voucher IDs, Hashes, Nonces
val SpaceMonoFont = GoogleFont("Space Mono")
val SpaceMonoFamily = FontFamily(
    Font(googleFont = SpaceMonoFont, fontProvider = GoogleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = SpaceMonoFont, fontProvider = GoogleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = SpaceMonoFont, fontProvider = GoogleFontProvider, weight = FontWeight.Bold)
)

// ============================================================================
// Material 3 Typography - Warm Editorial
// ============================================================================

val WarmEditorialTypography = Typography(
    // Display styles (Serif - Playfair Display)
    displayLarge = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 54.sp,
        letterSpacing = (-0.5).sp,
        color = WarmTextPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 38.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp,
        color = WarmTextPrimary
    ),
    displaySmall = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        color = WarmTextPrimary
    ),

    // Headline styles (Serif - Playfair Display)
    headlineLarge = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        color = WarmTextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        color = WarmTextPrimary
    ),
    headlineSmall = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        color = WarmTextPrimary
    ),

    // Title styles (Serif for titles, Plus Jakarta Sans for sub-titles)
    titleLarge = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = WarmTextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        color = WarmTextPrimary
    ),
    titleSmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = WarmTextPrimary
    ),

    // Body styles (Plus Jakarta Sans)
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = WarmTextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = WarmTextSecondary
    ),
    bodySmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        color = WarmTextMuted
    ),

    // Label styles (Plus Jakarta Sans)
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp,
        color = WarmTextPrimary
    ),
    labelMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.4.sp,
        color = WarmTextSecondary
    ),
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 9.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.8.sp,
        color = WarmTextMuted
    )
)

// Legacy alias to maintain full backward compatibility
val AppTypography = WarmEditorialTypography

/**
 * Monospace Typography tokens for amounts, addresses, voucher IDs, and cryptographic hashes (Space Mono).
 */
@Immutable
data class OffpayMonoTypography(
    val amountHuge: TextStyle = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 48.sp,
        letterSpacing = (-1).sp,
        color = WarmTextPrimary
    ),
    val amountLarge: TextStyle = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.5).sp,
        color = WarmTextPrimary
    ),
    val amountMedium: TextStyle = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        color = WarmTextPrimary
    ),
    val amountSmall: TextStyle = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        color = WarmTextPrimary
    ),
    val voucherId: TextStyle = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        color = WarmTextMuted
    ),
    val walletAddress: TextStyle = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = WarmTextSecondary
    ),
    val walletAddressCompact: TextStyle = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        color = WarmTextSecondary
    ),
    val hashOrProof: TextStyle = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        color = WarmTextMuted
    )
)

val LocalOffpayTypography = staticCompositionLocalOf { OffpayMonoTypography() }
