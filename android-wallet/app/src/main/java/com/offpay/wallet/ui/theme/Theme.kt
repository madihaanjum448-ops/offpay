package com.offpay.wallet.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = OffpayPrimaryAccent,
    onPrimary = OffpayBackground,
    primaryContainer = OffpayPrimaryAccentDim,
    onPrimaryContainer = OffpayTextPrimary,
    secondary = OffpaySecondaryAccent,
    onSecondary = OffpayBackground,
    secondaryContainer = OffpaySecondaryAccentDim,
    onSecondaryContainer = OffpayTextPrimary,
    background = OffpayBackground,
    onBackground = OffpayTextPrimary,
    surface = OffpaySurface,
    onSurface = OffpayTextPrimary,
    surfaceVariant = OffpaySurfaceElevated,
    onSurfaceVariant = OffpayTextMuted,
    outline = OffpaySurfaceBorder,
    outlineVariant = OffpayTextDisabled,
    error = OffpayError,
    onError = OffpayBackground,
    errorContainer = OffpayErrorDim,
    onErrorContainer = OffpayTextPrimary
)

@Composable
fun OffpayTheme(
    darkTheme: Boolean = true, // Offpay defaults to dark crypto/wallet theme
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val extendedColors = OffpayExtendedColors()
    val monoTypography = OffpayMonoTypography()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(
        LocalOffpayColors provides extendedColors,
        LocalOffpayTypography provides monoTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

/**
 * Access object for custom Offpay design system tokens.
 */
object OffpayTheme {
    val colors: OffpayExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalOffpayColors.current

    val monoTypography: OffpayMonoTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalOffpayTypography.current
}

/**
 * Extension properties on [MaterialTheme] for convenient access.
 */
val MaterialTheme.offpayColors: OffpayExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalOffpayColors.current

val MaterialTheme.monoTypography: OffpayMonoTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalOffpayTypography.current
