package com.example.calendar.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.MainActivity
import com.example.R
import com.example.calendar.core.PersianCalendarHelper
import com.example.calendar.data.CalendarEventEntity
import com.example.calendar.data.weather.WeatherRepository

class ReminderNotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_EVENTS = "calendar_events_channel"
        const val CHANNEL_BIRTHDAYS = "calendar_birthdays_channel"
        const val CHANNEL_UPDATES = "calendar_updates_channel"
        const val CHANNEL_DAILY_CALENDAR = "calendar_daily_status_channel"
        const val NOTIFICATION_ID_DAILY_CALENDAR = 1001
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val eventChannel = NotificationChannel(
                CHANNEL_EVENTS,
                "یادآوری رویدادها و یادداشت‌ها",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "اعلان‌های مربوط به یادداشت‌ها و جلسات تقویم"
            }

            val birthdayChannel = NotificationChannel(
                CHANNEL_BIRTHDAYS,
                "یادآوری تولدها و سالگردها",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "اعلان تبریک و یادآوری سالروز تولدها"
            }

            val updatesChannel = NotificationChannel(
                CHANNEL_UPDATES,
                "بروزرسانی‌ها و اخبار",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "اعلان اخبار جدید"
            }

            val dailyCalendarChannel = NotificationChannel(
                CHANNEL_DAILY_CALENDAR,
                "تقویم و تاریخ روز در نوار وضعیت",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "نمایش عدد روز در نوار اعلان و تاریخ کامل و مناسبت در نوار کشویی"
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(eventChannel)
            notificationManager.createNotificationChannel(birthdayChannel)
            notificationManager.createNotificationChannel(updatesChannel)
            notificationManager.createNotificationChannel(dailyCalendarChannel)
        }
    }

    /**
     * Creates an alpha-mask transparent bitmap showing a clean calendar badge with the Persian day number
     * for the Android status bar icon, preventing any solid white square rendering issues.
     */
    private fun createDayNumberBitmap(dayNumber: Int): Bitmap {
        val size = 96
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        bitmap.density = context.resources.displayMetrics.densityDpi
        val canvas = Canvas(bitmap)

        // Fully transparent background
        canvas.drawColor(Color.TRANSPARENT)

        // 1. Calendar Header loops
        val loopPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 4f
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawLine(28f, 6f, 28f, 20f, loopPaint)
        canvas.drawLine(68f, 6f, 68f, 20f, loopPaint)

        // 2. Calendar Card Outline
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 4.5f
        }
        val cardRect = RectF(10f, 14f, size - 10f, size - 10f)
        canvas.drawRoundRect(cardRect, 14f, 14f, cardPaint)

        // Top bar separator inside calendar
        canvas.drawLine(12f, 32f, size - 12f, 32f, cardPaint)

        // 3. Persian Day Number in White Bold
        val text = PersianCalendarHelper.toPersianDigits(dayNumber)
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = if (text.length > 1) 40f else 46f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val bounds = android.graphics.Rect()
        textPaint.getTextBounds(text, 0, text.length, bounds)
        val xPos = size / 2f
        // Center text in the lower 2/3 of the calendar
        val calendarBodyCenterY = (32f + (size - 10f)) / 2f
        val yPos = calendarBodyCenterY + (bounds.height() / 2f) - bounds.bottom

        canvas.drawText(text, xPos, yPos, textPaint)

        return bitmap
    }

    /**
     * Cancels the daily status bar calendar notification.
     */
    fun cancelDailyCalendarNotification() {
        try {
            notificationManager.cancel(NOTIFICATION_ID_DAILY_CALENDAR)
        } catch (e: Exception) {
            // Ignored
        }
    }

    /**
     * Shows or updates the ongoing daily calendar notification in the status bar
     * using custom RemoteViews layouts: Day number inside right box, day of week & month & year,
     * small Gregorian and Hijri underneath, weather opposite, and occasion at bottom.
     * No zodiac, no duplicate solar line.
     */
    fun showDailyCalendarNotification() {
        val userSettings = try {
            com.example.calendar.data.PreferencesManager(context).settingsFlow.value
        } catch (e: Exception) {
            com.example.calendar.data.UserSettings()
        }

        if (!userSettings.enableDailyCalendarNotification) {
            cancelDailyCalendarNotification()
            return
        }

        val todayPersian = PersianCalendarHelper.getTodayPersian()
        val todayGregorian = PersianCalendarHelper.getTodayGregorian()
        val todayHijri = PersianCalendarHelper.persianToHijri(
            todayPersian.year,
            todayPersian.month,
            todayPersian.day
        )
        val dayOfWeek = PersianCalendarHelper.getDayOfWeekName(todayPersian)
        val monthName = PersianCalendarHelper.getMonthName(todayPersian.month)
        val occasions = PersianCalendarHelper.getOccasionsForPersianDate(
            todayPersian.year,
            todayPersian.month,
            todayPersian.day
        )

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_DAILY_CALENDAR,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val dayStr = PersianCalendarHelper.toPersianDigits(todayPersian.day)
        val dayBitmap = try {
            createDayNumberBitmap(todayPersian.day)
        } catch (e: Throwable) {
            null
        }

        val persianDateTitle = "$dayOfWeek $dayStr $monthName ${PersianCalendarHelper.toPersianDigits(todayPersian.year)}"
        val gregorianText = "${todayGregorian.day} ${todayGregorian.monthName} ${todayGregorian.year}"
        val hijriText = todayHijri.toHijriFormattedString()
        val subDates = if (userSettings.showSubDatesInNotification) "$gregorianText • $hijriText" else ""

        // Fetch current weather
        val weather = try {
            WeatherRepository.getInstance(context).weatherState.value
        } catch (e: Throwable) {
            null
        }
        val weatherTemp = if (weather != null) "${weather.conditionEmoji} ${weather.getFormattedTemp()}" else "🌤️ ۲۸°"
        val weatherDesc = if (weather != null) "${weather.cityName} • ${weather.conditionText}" else "تهران • صاف"

        // Collapsed Custom View
        val collapsedView = RemoteViews(context.packageName, R.layout.notification_daily_calendar)
        collapsedView.setTextViewText(R.id.notif_day_number, dayStr)
        collapsedView.setTextViewText(R.id.notif_persian_date, persianDateTitle)
        
        if (userSettings.showSubDatesInNotification && subDates.isNotEmpty()) {
            collapsedView.setTextViewText(R.id.notif_sub_dates, subDates)
            collapsedView.setViewVisibility(R.id.notif_sub_dates, View.VISIBLE)
        } else {
            collapsedView.setViewVisibility(R.id.notif_sub_dates, View.GONE)
        }

        if (userSettings.showWeatherInNotification) {
            collapsedView.setTextViewText(R.id.notif_weather_temp, weatherTemp)
            collapsedView.setTextViewText(R.id.notif_weather_desc, weatherDesc)
            collapsedView.setViewVisibility(R.id.notif_weather_container, View.VISIBLE)
        } else {
            collapsedView.setViewVisibility(R.id.notif_weather_container, View.GONE)
        }

        // Expanded Custom View
        val expandedView = RemoteViews(context.packageName, R.layout.notification_daily_calendar_expanded)
        expandedView.setTextViewText(R.id.notif_day_number_exp, dayStr)
        expandedView.setTextViewText(R.id.notif_persian_date_exp, persianDateTitle)
        
        if (userSettings.showSubDatesInNotification && subDates.isNotEmpty()) {
            expandedView.setTextViewText(R.id.notif_sub_dates_exp, subDates)
            expandedView.setViewVisibility(R.id.notif_sub_dates_exp, View.VISIBLE)
        } else {
            expandedView.setViewVisibility(R.id.notif_sub_dates_exp, View.GONE)
        }

        if (userSettings.showWeatherInNotification) {
            expandedView.setTextViewText(R.id.notif_weather_temp_exp, weatherTemp)
            expandedView.setTextViewText(R.id.notif_weather_desc_exp, weatherDesc)
            expandedView.setViewVisibility(R.id.notif_weather_container_exp, View.VISIBLE)
        } else {
            expandedView.setViewVisibility(R.id.notif_weather_container_exp, View.GONE)
        }

        if (userSettings.showOccasionsInNotification && occasions.isNotEmpty()) {
            val occ = occasions.first()
            val occText = if (occ.isOfficialHoliday) "🔴 تعطیل رسمی: ${occ.title}" else "📌 مناسبت امروز: ${occ.title}"
            expandedView.setTextViewText(R.id.notif_occasion_text, occText)
            expandedView.setViewVisibility(R.id.notif_occasion_text, View.VISIBLE)
        } else {
            expandedView.setViewVisibility(R.id.notif_occasion_text, View.GONE)
        }

        try {
            val builder = NotificationCompat.Builder(context, CHANNEL_DAILY_CALENDAR)
                .setSmallIcon(R.drawable.ic_stat_calendar)
                .setSubText("امروز $dayStr")
                .setContentTitle(persianDateTitle)
                .setContentText(if (subDates.isNotEmpty()) "$subDates • $weatherTemp" else weatherTemp)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)

            if (dayBitmap != null) {
                builder.setLargeIcon(dayBitmap)
            }

            notificationManager.notify(NOTIFICATION_ID_DAILY_CALENDAR, builder.build())
        } catch (e: Throwable) {
            // Notification safeguard
        }
    }

    fun showEventNotification(event: CalendarEventEntity) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = if (event.category == "تولد") CHANNEL_BIRTHDAYS else CHANNEL_EVENTS
        val title = if (event.category == "تولد") "🎉 یادآوری تولد: ${event.title}" else "⏰ یادآوری: ${event.title}"

        val dateStr = "${PersianCalendarHelper.toPersianDigits(event.persianDay)} ${PersianCalendarHelper.getMonthName(event.persianMonth)}"
        val timeStr = if (event.timeHour >= 0) "ساعت ${PersianCalendarHelper.toPersianDigits(event.timeHour)}:${PersianCalendarHelper.toPersianDigits(event.timeMinute.toString().padStart(2, '0'))}" else ""
        val contentText = listOf(event.description, dateStr, timeStr).filter { it.isNotBlank() }.joinToString(" • ")

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_calendar)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(event.id.toInt(), notification)
    }

    fun showTestNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            999,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_EVENTS)
            .setSmallIcon(R.drawable.ic_stat_calendar)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(999, notification)
    }
}
