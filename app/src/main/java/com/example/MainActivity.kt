package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calendar.data.ThemeMode
import com.example.calendar.ui.CalendarViewModel
import com.example.calendar.ui.MainScreen
import com.example.calendar.widget.PersianCalendarWidgetProvider
import com.example.ui.theme.PersianCalendarTheme

class MainActivity : ComponentActivity() {

    private val calendarViewModel: CalendarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Update widget on app start and update status bar daily notification
        try {
            PersianCalendarWidgetProvider.sendUpdateBroadcast(this)
            com.example.calendar.notifications.ReminderNotificationManager(this).showDailyCalendarNotification()
        } catch (e: Exception) {
            // Widget / notification safeguard
        }

        setContent {
            val userSettings by calendarViewModel.userSettings.collectAsStateWithLifecycle()

            val isDark = when (userSettings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            PersianCalendarTheme(
                themeStyle = userSettings.theme,
                darkTheme = isDark
            ) {
                val currentDensity = LocalDensity.current
                val scale = userSettings.fontSizeScale.coerceIn(0.75f, 1.4f)
                val customDensity = Density(
                    density = currentDensity.density,
                    fontScale = currentDensity.fontScale * scale
                )

                // Persian is an RTL language - provide RTL layout direction and font scale
                CompositionLocalProvider(
                    LocalLayoutDirection provides LayoutDirection.Rtl,
                    LocalDensity provides customDensity
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        MainScreen(viewModel = calendarViewModel)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            PersianCalendarWidgetProvider.sendUpdateBroadcast(this)
            com.example.calendar.notifications.ReminderNotificationManager(this).showDailyCalendarNotification()
        } catch (e: Exception) {
            // Widget / notification update safeguard
        }
    }
}

