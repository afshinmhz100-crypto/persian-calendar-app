package com.example.calendar.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(val title: String, val description: String) {
    SYSTEM("خودکار (پیروی از سیستم)", "تنظیم خودکار با تم تیره یا روشن گوشی"),
    LIGHT("حالت همیشه روشن ☀️", "تم روشن فعال حتی در صورت فعال بودن دارک‌مود سیستم"),
    DARK("حالت همیشه تاریک 🌙", "تم تیره و چشم‌نواز با پس‌زمینه تیره")
}

enum class AppThemeStyle(val title: String, val description: String) {
    TURQUOISE("فیروزه‌ای اصیل", "رنگ‌بندی فیروزه‌ای و طلایی ایرانی"),
    DARK_MINIMAL("تاریک مینیمال", "زمینه مشکی خالص OLED با کنتراست بالا"),
    ROSE_GOLD("رز مدرن", "ترکیب ملایم رز و مرجانی"),
    WARM_AMBER("کهربایی گرم", "الهام گرفته از کویر و پاییز"),
    LIGHT_MINIMAL("روشن مینیمال", "زمینه تمیز، روشن و پرنور")
}

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val theme: AppThemeStyle = AppThemeStyle.TURQUOISE,
    val showGregorianInGrid: Boolean = true,
    val enableDailyCalendarNotification: Boolean = true,
    val showWeatherInNotification: Boolean = true,
    val showSubDatesInNotification: Boolean = true,
    val showOccasionsInNotification: Boolean = true,
    val enableBirthdayNotifications: Boolean = true,
    val enableEventReminders: Boolean = true,
    val enableHolidayNotifications: Boolean = false,
    val autoUpdateNews: Boolean = true,
    val lastCloudSyncTimestamp: Long = 0L,
    val fontSizeScale: Float = 1.0f
)

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("persian_calendar_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<UserSettings> = _settingsFlow.asStateFlow()

    private fun loadSettings(): UserSettings {
        val themeModeName = prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val themeMode = try {
            ThemeMode.valueOf(themeModeName)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }

        val themeName = prefs.getString("theme_style", AppThemeStyle.TURQUOISE.name) ?: AppThemeStyle.TURQUOISE.name
        val theme = try {
            AppThemeStyle.valueOf(themeName)
        } catch (e: Exception) {
            AppThemeStyle.TURQUOISE
        }

        return UserSettings(
            themeMode = themeMode,
            theme = theme,
            showGregorianInGrid = prefs.getBoolean("show_gregorian_grid", true),
            enableDailyCalendarNotification = prefs.getBoolean("enable_daily_cal_notif", true),
            showWeatherInNotification = prefs.getBoolean("show_weather_notif", true),
            showSubDatesInNotification = prefs.getBoolean("show_subdates_notif", true),
            showOccasionsInNotification = prefs.getBoolean("show_occasions_notif", true),
            enableBirthdayNotifications = prefs.getBoolean("enable_birthday_notif", true),
            enableEventReminders = prefs.getBoolean("enable_event_reminders", true),
            enableHolidayNotifications = prefs.getBoolean("enable_holiday_notif", false),
            autoUpdateNews = prefs.getBoolean("auto_update_news", true),
            lastCloudSyncTimestamp = prefs.getLong("last_cloud_sync_time", 0L),
            fontSizeScale = prefs.getFloat("font_size_scale", 1.0f)
        )
    }

    fun updateDailyCalendarNotification(enable: Boolean) {
        prefs.edit().putBoolean("enable_daily_cal_notif", enable).apply()
        _settingsFlow.value = _settingsFlow.value.copy(enableDailyCalendarNotification = enable)
    }

    fun updateShowWeatherInNotification(show: Boolean) {
        prefs.edit().putBoolean("show_weather_notif", show).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showWeatherInNotification = show)
    }

    fun updateShowSubDatesInNotification(show: Boolean) {
        prefs.edit().putBoolean("show_subdates_notif", show).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showSubDatesInNotification = show)
    }

    fun updateShowOccasionsInNotification(show: Boolean) {
        prefs.edit().putBoolean("show_occasions_notif", show).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showOccasionsInNotification = show)
    }

    fun updateThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _settingsFlow.value = _settingsFlow.value.copy(themeMode = mode)
    }

    fun updateTheme(theme: AppThemeStyle) {
        prefs.edit().putString("theme_style", theme.name).apply()
        _settingsFlow.value = _settingsFlow.value.copy(theme = theme)
    }

    fun updateShowGregorianInGrid(show: Boolean) {
        prefs.edit().putBoolean("show_gregorian_grid", show).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showGregorianInGrid = show)
    }

    fun updateBirthdayNotifications(enable: Boolean) {
        prefs.edit().putBoolean("enable_birthday_notif", enable).apply()
        _settingsFlow.value = _settingsFlow.value.copy(enableBirthdayNotifications = enable)
    }

    fun updateEventReminders(enable: Boolean) {
        prefs.edit().putBoolean("enable_event_reminders", enable).apply()
        _settingsFlow.value = _settingsFlow.value.copy(enableEventReminders = enable)
    }

    fun updateHolidayNotifications(enable: Boolean) {
        prefs.edit().putBoolean("enable_holiday_notif", enable).apply()
        _settingsFlow.value = _settingsFlow.value.copy(enableHolidayNotifications = enable)
    }

    fun updateAutoUpdateNews(auto: Boolean) {
        prefs.edit().putBoolean("auto_update_news", auto).apply()
        _settingsFlow.value = _settingsFlow.value.copy(autoUpdateNews = auto)
    }

    fun updateLastCloudSyncTimestamp(timestamp: Long) {
        prefs.edit().putLong("last_cloud_sync_time", timestamp).apply()
        _settingsFlow.value = _settingsFlow.value.copy(lastCloudSyncTimestamp = timestamp)
    }

    fun updateFontSizeScale(scale: Float) {
        prefs.edit().putFloat("font_size_scale", scale).apply()
        _settingsFlow.value = _settingsFlow.value.copy(fontSizeScale = scale)
    }
}
