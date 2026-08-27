package com.example.calendar.core

import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Data class representing a Persian (Jalali / Solar Hijri) date.
 */
data class PersianDate(
    val year: Int,
    val month: Int, // 1..12
    val day: Int    // 1..31
) : Comparable<PersianDate> {

    val monthName: String
        get() = PersianCalendarHelper.getMonthName(month)

    val isLeapYear: Boolean
        get() = PersianCalendarHelper.isPersianLeapYear(year)

    fun toFormattedString(): String {
        return "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
    }

    fun toPersianFormattedString(): String {
        return "${PersianCalendarHelper.toPersianDigits(day)} $monthName ${PersianCalendarHelper.toPersianDigits(year)}"
    }

    override fun compareTo(other: PersianDate): Int {
        if (this.year != other.year) return this.year.compareTo(other.year)
        if (this.month != other.month) return this.month.compareTo(other.month)
        return this.day.compareTo(other.day)
    }
}

/**
 * Data class representing a Gregorian (Miladi) date.
 */
data class GregorianDate(
    val year: Int,
    val month: Int, // 1..12
    val day: Int    // 1..31
) {
    val monthName: String
        get() = PersianCalendarHelper.getGregorianMonthName(month)

    fun toFormattedString(): String {
        return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }
}

/**
 * Data class representing an Islamic (Hijri Lunar / هجری قمری) date.
 */
data class HijriDate(
    val year: Int,
    val month: Int, // 1..12
    val day: Int    // 1..30
) {
    val monthName: String
        get() = PersianCalendarHelper.getHijriMonthName(month)

    fun toFormattedString(): String {
        return "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
    }

    fun toPersianFormattedString(): String {
        return "${PersianCalendarHelper.toPersianDigits(day)} $monthName ${PersianCalendarHelper.toPersianDigits(year)}"
    }

    fun toHijriFormattedString(): String {
        return "${PersianCalendarHelper.toPersianDigits(day)} $monthName ${PersianCalendarHelper.toPersianDigits(year)}"
    }
}

/**
 * Occasion Type for color coding (Birthdays/Celebrations = Green, Deaths/Martyrdom = Red, Global = Blue, etc.)
 */
enum class OccasionType {
    MARTYRDOM,        // شهادت، رحلت، وفات، تاسوعا، عاشورا، اربعین (قرمز)
    CELEBRATION,      // ولادت، میلاد، عید، روز مادر، روز پدر، جشن (سبز)
    OFFICIAL_HOLIDAY, // تعطیل رسمی عمومی
    NATIONAL,         // مناسبت ملی و باستانی
    GLOBAL            // مناسبت جهانی و بین‌المللی
}

/**
 * Holiday / Occasion model.
 */
data class CalendarOccasion(
    val title: String,
    val isOfficialHoliday: Boolean, // True = تعطیل رسمی
    val isIranian: Boolean = true,  // True = مناسبت ایرانی / ملی / مذهبی, False = مناسبت جهانی
    val description: String = "",
    val occasionType: OccasionType = detectOccasionType(title, isOfficialHoliday, isIranian)
) {
    companion object {
        fun detectOccasionType(title: String, isHoliday: Boolean, isIranian: Boolean): OccasionType {
            val t = title.lowercase()
            return when {
                t.contains("شهادت") || t.contains("رحلت") || t.contains("وفات") ||
                t.contains("عاشورا") || t.contains("تاسوعا") || t.contains("اربعین") ||
                t.contains("ضربت خوردن") -> OccasionType.MARTYRDOM

                t.contains("ولادت") || t.contains("میلاد") || t.contains("عید") ||
                t.contains("جشن") || t.contains("نوروز") || t.contains("مبعث") ||
                t.contains("روز زن") || t.contains("روز مادر") || t.contains("روز پدر") ||
                t.contains("روز دختر") || t.contains("نیمه شعبان") || t.contains("سپندارمذگان") ||
                t.contains("یلدا") || t.contains("کریسمس") -> OccasionType.CELEBRATION

                !isIranian -> OccasionType.GLOBAL
                isHoliday -> OccasionType.OFFICIAL_HOLIDAY
                else -> OccasionType.NATIONAL
            }
        }
    }
}

/**
 * Day cell data for calendar month grid.
 */
data class CalendarDayModel(
    val persianDate: PersianDate,
    val gregorianDate: GregorianDate,
    val hijriDate: HijriDate = PersianCalendarHelper.persianToHijri(persianDate.year, persianDate.month, persianDate.day),
    val dayOfWeekName: String,
    val dayOfWeekIndex: Int, // 0 = شنبه, 6 = جمعه
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val occasions: List<CalendarOccasion> = emptyList(),
    val eventCount: Int = 0
) {
    val isHoliday: Boolean
        get() = occasions.any { it.isOfficialHoliday } || dayOfWeekIndex == 6 // جمعه تعطیل است
}
