package com.example.calendar.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.calendar.core.PersianCalendarHelper
import com.example.calendar.data.AppThemeStyle
import com.example.calendar.data.CalendarEventEntity
import com.example.calendar.data.EventRepository
import com.example.calendar.data.PreferencesManager
import com.example.calendar.data.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

data class BackupRestoreResult(
    val success: Boolean,
    val message: String,
    val restoredEventCount: Int = 0
)

class BackupManager(
    private val context: Context,
    private val eventRepository: EventRepository,
    private val preferencesManager: PreferencesManager
) {

    /**
     * Generates a complete JSON backup of all settings, notes, birthdays and reminders.
     */
    suspend fun createBackupJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "PersianCalendar")
        root.put("version", 1)
        root.put("exportTimestamp", System.currentTimeMillis())

        val today = PersianCalendarHelper.getTodayPersian()
        root.put("persianDate", today.toFormattedString())

        // User Settings
        val settings = preferencesManager.settingsFlow.value
        val settingsObj = JSONObject()
        settingsObj.put("theme", settings.theme.name)
        settingsObj.put("showGregorianInGrid", settings.showGregorianInGrid)
        settingsObj.put("enableDailyCalendarNotification", settings.enableDailyCalendarNotification)
        settingsObj.put("showWeatherInNotification", settings.showWeatherInNotification)
        settingsObj.put("showSubDatesInNotification", settings.showSubDatesInNotification)
        settingsObj.put("showOccasionsInNotification", settings.showOccasionsInNotification)
        settingsObj.put("enableBirthdayNotifications", settings.enableBirthdayNotifications)
        settingsObj.put("enableEventReminders", settings.enableEventReminders)
        settingsObj.put("enableHolidayNotifications", settings.enableHolidayNotifications)
        settingsObj.put("autoUpdateNews", settings.autoUpdateNews)
        settingsObj.put("fontSizeScale", settings.fontSizeScale.toDouble())
        settingsObj.put("lastCloudSyncTimestamp", settings.lastCloudSyncTimestamp)
        root.put("settings", settingsObj)

        // Events & Notes
        val events = eventRepository.getAllEventsList()
        val eventsArray = JSONArray()
        for (event in events) {
            val eventObj = JSONObject()
            eventObj.put("title", event.title)
            eventObj.put("description", event.description)
            eventObj.put("persianYear", event.persianYear)
            eventObj.put("persianMonth", event.persianMonth)
            eventObj.put("persianDay", event.persianDay)
            eventObj.put("category", event.category)
            eventObj.put("timeHour", event.timeHour)
            eventObj.put("timeMinute", event.timeMinute)
            eventObj.put("isReminderEnabled", event.isReminderEnabled)
            eventObj.put("priority", event.priority)
            eventObj.put("colorTag", event.colorTag)
            eventObj.put("isCompleted", event.isCompleted)
            eventObj.put("createdAtTimestamp", event.createdAtTimestamp)
            eventsArray.put(eventObj)
        }
        root.put("events", eventsArray)

        root.toString(2)
    }

    /**
     * Saves backup to a temporary cache file and shares it (Google Drive, Telegram, Files, etc.).
     */
    suspend fun exportAndShareBackupFile(): Intent = withContext(Dispatchers.IO) {
        val jsonString = createBackupJson()
        val today = PersianCalendarHelper.getTodayPersian()
        val fileName = "calendar_backup_${today.year}_${today.month}_${today.day}.json"

        val backupDir = File(context.cacheDir, "backups")
        if (!backupDir.exists()) backupDir.mkdirs()

        val file = File(backupDir, fileName)
        FileOutputStream(file).use { out ->
            out.write(jsonString.toByteArray(Charsets.UTF_8))
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "فایل پشتیبان تقویم خورشیدی ($fileName)")
            putExtra(Intent.EXTRA_TEXT, "فایل پشتیبان کامل یادداشت‌ها، رویدادها و تنظیمات تقویم خورشیدی")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**
     * Initiates Google Drive cloud sync intent / upload.
     */
    suspend fun performGoogleDriveSync(): Intent = withContext(Dispatchers.IO) {
        val intent = exportAndShareBackupFile()
        intent.setPackage("com.google.android.apps.docs") // Google Drive target if available
        preferencesManager.updateLastCloudSyncTimestamp(System.currentTimeMillis())
        intent
    }

    /**
     * Restores settings and events from a JSON backup string.
     */
    suspend fun restoreFromJson(jsonString: String, clearExisting: Boolean = false): BackupRestoreResult = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (!root.has("app") || !root.has("settings") || !root.has("events")) {
                return@withContext BackupRestoreResult(
                    success = false,
                    message = "ساختار فایل پشتیبان نامعتبر است."
                )
            }

            // Restore Settings
            val settingsObj = root.getJSONObject("settings")
            val themeName = settingsObj.optString("theme", AppThemeStyle.TURQUOISE.name)
            val theme = try { AppThemeStyle.valueOf(themeName) } catch (e: Exception) { AppThemeStyle.TURQUOISE }
            preferencesManager.updateTheme(theme)
            preferencesManager.updateShowGregorianInGrid(settingsObj.optBoolean("showGregorianInGrid", true))
            preferencesManager.updateDailyCalendarNotification(settingsObj.optBoolean("enableDailyCalendarNotification", true))
            preferencesManager.updateShowWeatherInNotification(settingsObj.optBoolean("showWeatherInNotification", true))
            preferencesManager.updateShowSubDatesInNotification(settingsObj.optBoolean("showSubDatesInNotification", true))
            preferencesManager.updateShowOccasionsInNotification(settingsObj.optBoolean("showOccasionsInNotification", true))
            preferencesManager.updateBirthdayNotifications(settingsObj.optBoolean("enableBirthdayNotifications", true))
            preferencesManager.updateEventReminders(settingsObj.optBoolean("enableEventReminders", true))
            preferencesManager.updateHolidayNotifications(settingsObj.optBoolean("enableHolidayNotifications", false))
            preferencesManager.updateAutoUpdateNews(settingsObj.optBoolean("autoUpdateNews", true))
            preferencesManager.updateFontSizeScale(settingsObj.optDouble("fontSizeScale", 1.0).toFloat())

            // Restore Events
            if (clearExisting) {
                eventRepository.clearAll()
            }

            val eventsArray = root.getJSONArray("events")
            val restoredEvents = mutableListOf<CalendarEventEntity>()

            for (i in 0 until eventsArray.length()) {
                val obj = eventsArray.getJSONObject(i)
                restoredEvents.add(
                    CalendarEventEntity(
                        title = obj.getString("title"),
                        description = obj.optString("description", ""),
                        persianYear = obj.getInt("persianYear"),
                        persianMonth = obj.getInt("persianMonth"),
                        persianDay = obj.getInt("persianDay"),
                        category = obj.optString("category", "یادداشت"),
                        timeHour = obj.optInt("timeHour", -1),
                        timeMinute = obj.optInt("timeMinute", -1),
                        isReminderEnabled = obj.optBoolean("isReminderEnabled", false),
                        priority = obj.optString("priority", "متوسط"),
                        colorTag = obj.optString("colorTag", "#0A9396"),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        createdAtTimestamp = obj.optLong("createdAtTimestamp", System.currentTimeMillis())
                    )
                )
            }

            if (restoredEvents.isNotEmpty()) {
                eventRepository.insertAll(restoredEvents)
            }

            BackupRestoreResult(
                success = true,
                message = "بازیابی اطلاعات با موفقیت انجام شد.",
                restoredEventCount = restoredEvents.size
            )
        } catch (e: Exception) {
            BackupRestoreResult(
                success = false,
                message = "خطا در خواندن فایل پشتیبان: ${e.localizedMessage}"
            )
        }
    }
}
