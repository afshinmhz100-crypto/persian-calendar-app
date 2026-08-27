package com.example.calendar.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.calendar.core.PersianCalendarHelper
import com.example.calendar.data.AppDatabase
import com.example.calendar.data.weather.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersianCalendarWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == Intent.ACTION_DATE_CHANGED ||
            intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED ||
            intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE
        ) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, PersianCalendarWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(component)
            onUpdate(context, appWidgetManager, ids)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_persian_calendar)

            val todayPersian = PersianCalendarHelper.getTodayPersian()
            val todayGregorian = PersianCalendarHelper.getTodayGregorian()
            val todayHijri = PersianCalendarHelper.persianToHijri(
                todayPersian.year,
                todayPersian.month,
                todayPersian.day
            )

            val dayOfWeekName = PersianCalendarHelper.getDayOfWeekName(todayPersian)
            val (animalName, emoji) = PersianCalendarHelper.getYearAnimal(todayPersian.year)
            val occasions = PersianCalendarHelper.getOccasionsForPersianDate(
                todayPersian.year,
                todayPersian.month,
                todayPersian.day
            )

            // Day of week
            views.setTextViewText(R.id.widget_day_of_week, dayOfWeekName)

            // Zodiac Animal of the year
            views.setTextViewText(R.id.widget_zodiac_animal, "سال $animalName $emoji")

            // Persian Day Number
            views.setTextViewText(
                R.id.widget_persian_day,
                PersianCalendarHelper.toPersianDigits(todayPersian.day)
            )

            // Persian Month & Year
            val monthName = PersianCalendarHelper.getMonthName(todayPersian.month)
            val yearDigits = PersianCalendarHelper.toPersianDigits(todayPersian.year)
            views.setTextViewText(R.id.widget_persian_month_year, "$monthName $yearDigits")

            // Hijri Date (pure number and month, without redundant letters)
            views.setTextViewText(
                R.id.widget_hijri_date,
                todayHijri.toHijriFormattedString()
            )

            // Gregorian Date
            views.setTextViewText(
                R.id.widget_gregorian_date,
                "${todayGregorian.day} ${todayGregorian.monthName} ${todayGregorian.year}"
            )

            // Weather for full-width / wide widget view
            val weatherRepo = WeatherRepository.getInstance(context)
            val currentWeather = weatherRepo.weatherState.value
            if (currentWeather != null) {
                val weatherString = "📍 ${currentWeather.cityName}: ${currentWeather.getFormattedTemp()} ${currentWeather.conditionEmoji} ${currentWeather.conditionText}"
                views.setTextViewText(R.id.widget_weather_text, weatherString)
                views.setViewVisibility(R.id.widget_weather_text, View.VISIBLE)
            } else {
                views.setViewVisibility(R.id.widget_weather_text, View.GONE)
            }

            // Occasion / Holiday text
            if (occasions.isNotEmpty()) {
                val firstOccasion = occasions.first()
                val prefix = if (firstOccasion.isOfficialHoliday) "🔴 تعطیل: " else "📌 "
                views.setTextViewText(R.id.widget_occasion, prefix + firstOccasion.title)
                views.setViewVisibility(R.id.widget_occasion, View.VISIBLE)
            } else {
                views.setTextViewText(
                    R.id.widget_occasion,
                    PersianCalendarHelper.getZodiacSign(todayPersian.month)
                )
                views.setViewVisibility(R.id.widget_occasion, View.VISIBLE)
            }

            // Click Intent to open App
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)

            // Check today's events / reminders asynchronously and update widget reminder banner
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val todayEvents = db.eventDao().getEventsForDateDirect(
                        todayPersian.year,
                        todayPersian.month,
                        todayPersian.day
                    )
                    if (todayEvents.isNotEmpty()) {
                        val firstEvent = todayEvents.first()
                        val reminderText = if (todayEvents.size == 1) {
                            "⏰ یادداشت: ${firstEvent.title}"
                        } else {
                            "⏰ ${PersianCalendarHelper.toPersianDigits(todayEvents.size)} یادآوری و برنامه برای امروز"
                        }
                        views.setTextViewText(R.id.widget_reminder, reminderText)
                        views.setViewVisibility(R.id.widget_reminder, View.VISIBLE)
                    } else {
                        views.setViewVisibility(R.id.widget_reminder, View.GONE)
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    // Fail gracefully
                }
            }
        }

        fun sendUpdateBroadcast(context: Context) {
            try {
                val intent = Intent(context, PersianCalendarWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val component = ComponentName(context, PersianCalendarWidgetProvider::class.java)
                    val ids = appWidgetManager.getAppWidgetIds(component)
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
                context.sendBroadcast(intent)
            } catch (e: Throwable) {
                // Widget broadcast safeguard
            }
        }
    }
}
