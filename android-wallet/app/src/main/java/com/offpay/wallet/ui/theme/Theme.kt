package com.offpay.wallet.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Subtle rounding only: 4px on chips/buttons/inputs, 8px on cards
val WarmEditorialShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(8.dp)
)

private val WarmEditorialColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = WarmCardSurface,
    primaryContainer = TerracottaWash,
    onPrimaryContainer = TerracottaPrimary,
    secondary = SageSecondary,
    onSecondary = WarmCardSurface,
    secondaryContainer = SageWash,
    onSecondaryContainer = SageTextOnWash,
    tertiary = WarmInfo,
    onTertiary = WarmCardSurface,
    tertiaryContainer = WarmInfoWash,
    onTertiaryContainer = WarmInfo,
    background = WarmBackground,
    onBackground = WarmTextPrimary,
    surface = WarmCardSurface,
    onSurface = WarmTextPrimary,
    surfaceVariant = WarmRecessedSurface,
    onSurfaceVariant = WarmTextSecondary,
    outline = WarmCardBorder,
    outlineVariant = WarmRecessedBorder,
    error = WarmError,
    onError = WarmCardSurface,
    errorContainer = WarmErrorWash,
    onErrorContainer = WarmError
)

@Composable
fun OffpayTheme(
    darkTheme: Boolean = false, // Pure Warm Editorial Tactile light theme
    content: @Composable () -> Unit
) {
    val colorScheme = WarmEditorialColorScheme
    val extendedColors = OffpayExtendedColors()
    val monoTypography = OffpayMonoTypography()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                // Enable dark icons on light warm background
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
            }
        }
    }

    CompositionLocalProvider(
        LocalOffpayColors provides extendedColors,
        LocalOffpayTypography provides monoTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = WarmEditorialTypography,
            shapes = WarmEditorialShapes,
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
