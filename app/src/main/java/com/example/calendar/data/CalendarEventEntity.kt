package com.example.calendar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for storing notes, birthdays, reminders and events.
 */
@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val persianYear: Int,
    val persianMonth: Int,
    val persianDay: Int,
    val category: String, // "یادداشت", "تولد", "رویداد مهم", "جلسه", "کاری"
    val timeHour: Int = -1, // -1 means all-day
    val timeMinute: Int = -1,
    val isReminderEnabled: Boolean = false,
    val priority: String = "متوسط", // "کم", "متوسط", "زیاد"
    val colorTag: String = "#0A9396",
    val isCompleted: Boolean = false,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
