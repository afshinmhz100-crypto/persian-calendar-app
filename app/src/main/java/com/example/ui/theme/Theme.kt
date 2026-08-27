package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.calendar.data.AppThemeStyle

fun getThemeColorScheme(theme: AppThemeStyle, isDark: Boolean): ColorScheme {
    return when (theme) {
        AppThemeStyle.TURQUOISE -> {
            if (isDark) {
                darkColorScheme(
                    primary = TurquoiseDarkPrimary,
                    onPrimary = TurquoiseDarkOnPrimary,
                    primaryContainer = TurquoiseDarkPrimaryContainer,
                    onPrimaryContainer = Color(0xFFC7F1F5),
                    secondary = TurquoiseDarkSecondary,
                    onSecondary = Color(0xFF3E2000),
                    secondaryContainer = Color(0xFF5A3100),
                    onSecondaryContainer = Color(0xFFFFDDB3),
                    background = TurquoiseDarkBackground,
                    onBackground = Color(0xFFE1E3E3),
                    surface = TurquoiseDarkSurface,
                    onSurface = Color(0xFFE1E3E3),
                    surfaceVariant = TurquoiseDarkSurfaceVariant,
                    onSurfaceVariant = Color(0xFFBFC8CA),
                    outline = Color(0xFF899294)
                )
            } else {
                lightColorScheme(
                    primary = TurquoisePrimary,
                    onPrimary = TurquoiseOnPrimary,
                    primaryContainer = TurquoisePrimaryContainer,
                    onPrimaryContainer = TurquoiseOnPrimaryContainer,
                    secondary = TurquoiseSecondary,
                    onSecondary = Color.White,
                    secondaryContainer = TurquoiseSecondaryContainer,
                    onSecondaryContainer = Color(0xFF3D1E00),
                    background = TurquoiseBackground,
                    onBackground = Color(0xFF191C1D),
                    surface = TurquoiseSurface,
                    onSurface = Color(0xFF191C1D),
                    surfaceVariant = TurquoiseSurfaceVariant,
                    onSurfaceVariant = Color(0xFF3F484A),
                    outline = Color(0xFF6F797A)
                )
            }
        }
        AppThemeStyle.DARK_MINIMAL -> {
            if (isDark) {
                darkColorScheme(
                    primary = DarkOledPrimary,
                    onPrimary = Color.Black,
                    primaryContainer = Color(0xFF004D56),
                    onPrimaryContainer = Color(0xFF80F2FF),
                    secondary = DarkOledSecondary,
                    onSecondary = Color.Black,
                    secondaryContainer = Color(0xFF005043),
                    onSecondaryContainer = Color(0xFF7BFBE2),
                    background = DarkOledBackground,
                    onBackground = Color(0xFFE6E6E6),
                    surface = DarkOledSurface,
                    onSurface = Color(0xFFE6E6E6),
                    surfaceVariant = DarkOledSurfaceVariant,
                    onSurfaceVariant = Color(0xFFB0B7C3),
                    outline = Color(0xFF8A92A0)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF006874),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFF9EEFFD),
                    onPrimaryContainer = Color(0xFF001F24),
                    secondary = Color(0xFF006A5A),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFF8CF4DD),
                    onSecondaryContainer = Color(0xFF00201A),
                    background = Color(0xFFFBFDFD),
                    onBackground = Color(0xFF191C1D),
                    surface = Color(0xFFFFFFFF),
                    onSurface = Color(0xFF191C1D),
                    surfaceVariant = Color(0xFFDBE4E6),
                    onSurfaceVariant = Color(0xFF3F484A),
                    outline = Color(0xFF70797B)
                )
            }
        }
        AppThemeStyle.ROSE_GOLD -> {
            if (isDark) {
                darkColorScheme(
                    primary = Color(0xFFFF8A80),
                    onPrimary = Color(0xFF5C1400),
                    primaryContainer = Color(0xFF7D2200),
                    onPrimaryContainer = Color(0xFFFFDBCF),
                    secondary = Color(0xFFFFD180),
                    onSecondary = Color(0xFF452B00),
                    secondaryContainer = Color(0xFF623F00),
                    onSecondaryContainer = Color(0xFFFFDEAC),
                    background = Color(0xFF1E1412),
                    onBackground = Color(0xFFEDE0DD),
                    surface = Color(0xFF2D1E1B),
                    onSurface = Color(0xFFEDE0DD),
                    surfaceVariant = Color(0xFF3E2C28),
                    onSurfaceVariant = Color(0xFFD8C2BC),
                    outline = Color(0xFFA08C87)
                )
            } else {
                lightColorScheme(
                    primary = RosePrimary,
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFFFDAD3),
                    onPrimaryContainer = Color(0xFF3E0600),
                    secondary = RoseSecondary,
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFFFDDB4),
                    onSecondaryContainer = Color(0xFF2B1700),
                    background = RoseBackground,
                    onBackground = Color(0xFF221A18),
                    surface = RoseSurface,
                    onSurface = Color(0xFF221A18),
                    surfaceVariant = RoseSurfaceVariant,
                    onSurfaceVariant = Color(0xFF524340),
                    outline = Color(0xFF85736F)
                )
            }
        }
        AppThemeStyle.WARM_AMBER -> {
            if (isDark) {
                darkColorScheme(
                    primary = Color(0xFFFFB74D),
                    onPrimary = Color(0xFF482900),
                    primaryContainer = Color(0xFF673D00),
                    onPrimaryContainer = Color(0xFFFFDDB7),
                    secondary = Color(0xFFFFE082),
                    onSecondary = Color(0xFF402E00),
                    secondaryContainer = Color(0xFF5C4300),
                    onSecondaryContainer = Color(0xFFFFE08B),
                    background = Color(0xFF1A1610),
                    onBackground = Color(0xFFECE1D7),
                    surface = Color(0xFF262016),
                    onSurface = Color(0xFFECE1D7),
                    surfaceVariant = Color(0xFF3B3222),
                    onSurfaceVariant = Color(0xFFD4C4B5),
                    outline = Color(0xFF9D8E81)
                )
            } else {
                lightColorScheme(
                    primary = AmberPrimary,
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFFFDDB8),
                    onPrimaryContainer = Color(0xFF2B1700),
                    secondary = AmberSecondary,
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFFFE08B),
                    onSecondaryContainer = Color(0xFF241A00),
                    background = AmberBackground,
                    onBackground = Color(0xFF201B13),
                    surface = AmberSurface,
                    onSurface = Color(0xFF201B13),
                    surfaceVariant = AmberSurfaceVariant,
                    onSurfaceVariant = Color(0xFF4F4539),
                    outline = Color(0xFF817567)
                )
            }
        }
        AppThemeStyle.LIGHT_MINIMAL -> {
            if (isDark) {
                darkColorScheme(
                    primary = Color(0xFF94A3B8),
                    onPrimary = Color(0xFF0F172A),
                    primaryContainer = Color(0xFF334155),
                    onPrimaryContainer = Color(0xFFE2E8F0),
                    secondary = Color(0xFFCBD5E1),
                    onSecondary = Color(0xFF1E293B),
                    secondaryContainer = Color(0xFF475569),
                    onSecondaryContainer = Color(0xFFF1F5F9),
                    background = Color(0xFF0F172A),
                    onBackground = Color(0xFFF8FAFC),
                    surface = Color(0xFF1E293B),
                    onSurface = Color(0xFFF8FAFC),
                    surfaceVariant = Color(0xFF334155),
                    onSurfaceVariant = Color(0xFFCBD5E1),
                    outline = Color(0xFF64748B)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF0F172A),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFE2E8F0),
                    onPrimaryContainer = Color(0xFF0F172A),
                    secondary = Color(0xFF334155),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFF1F5F9),
                    onSecondaryContainer = Color(0xFF1E293B),
                    background = Color(0xFFF8FAFC),
                    onBackground = Color(0xFF0F172A),
                    surface = Color(0xFFFFFFFF),
                    onSurface = Color(0xFF0F172A),
                    surfaceVariant = Color(0xFFE2E8F0),
                    onSurfaceVariant = Color(0xFF475569),
                    outline = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
fun PersianCalendarTheme(
    themeStyle: AppThemeStyle = AppThemeStyle.TURQUOISE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = getThemeColorScheme(themeStyle, darkTheme)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

