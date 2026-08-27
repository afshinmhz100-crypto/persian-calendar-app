package com.example.calendar.core

import java.util.Calendar
import java.util.Date
import kotlin.math.floor

object PersianCalendarHelper {

    private val PERSIAN_MONTH_NAMES = arrayOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    private val GREGORIAN_MONTH_NAMES = arrayOf(
        "January", "February", "March",
        "April", "May", "June",
        "July", "August", "September",
        "October", "November", "December"
    )

    private val HIJRI_MONTH_NAMES = arrayOf(
        "محرم", "صفر", "ربیع‌الاول",
        "ربیع‌الثانی", "جمادی‌الاول", "جمادی‌الثانی",
        "رجب", "شعبان", "رمضان",
        "شوال", "ذی‌القعده", "ذی‌الحجه"
    )

    private val YEAR_ANIMALS = arrayOf(
        Pair("موش", "🐭"),
        Pair("گاو", "🐂"),
        Pair("پلنگ", "🐅"),
        Pair("خرگوش", "🐇"),
        Pair("نهنگ (اژدها)", "🐉"),
        Pair("مار", "🐍"),
        Pair("اسب", "🐎"),
        Pair("گوسفند", "🐑"),
        Pair("میمون", "🐒"),
        Pair("مرغ (خروس)", "🐓"),
        Pair("سگ", "🐕"),
        Pair("خوک", "🐖")
    )

    val WEEK_DAYS_PERSIAN = arrayOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه"
    )

    val WEEK_DAYS_SHORT = arrayOf(
        "ش", "ی", "د", "س", "چ", "پ", "ج"
    )

    fun getMonthName(month: Int): String {
        return if (month in 1..12) PERSIAN_MONTH_NAMES[month - 1] else ""
    }

    fun getGregorianMonthName(month: Int): String {
        return if (month in 1..12) GREGORIAN_MONTH_NAMES[month - 1] else ""
    }

    fun getHijriMonthName(month: Int): String {
        return if (month in 1..12) HIJRI_MONTH_NAMES[month - 1] else ""
    }

    /**
     * Calculates the animal of the Persian year (نماد حیوانی سال).
     */
    fun getYearAnimal(year: Int): Pair<String, String> {
        val index = ((year - 7) % 12 + 12) % 12
        return YEAR_ANIMALS[index]
    }

    fun getYearAnimalFull(year: Int): String {
        val (name, emoji) = getYearAnimal(year)
        return "سال $name $emoji"
    }

    /**
     * Converts English digits to Persian digits (0-9 -> ۰-۹).
     */
    fun toPersianDigits(text: Any): String {
        val str = text.toString()
        val persianChars = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val sb = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(persianChars[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Converts Persian digits to English digits (۰-۹ -> 0-9).
     */
    fun toEnglishDigits(text: String): String {
        val persianChars = "۰۱۲۳۴۵۶۷۸۹"
        val sb = StringBuilder()
        for (ch in text) {
            val idx = persianChars.indexOf(ch)
            if (idx != -1) {
                sb.append(idx)
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Checks whether a Persian (Jalali) year is a leap year (سال کبیسه).
     */
    fun isPersianLeapYear(year: Int): Boolean {
        val a = year - 474
        val b = (a % 2820) + 474
        val c = (b + 38) * 682
        val d = c % 2816
        return d < 682
    }

    /**
     * Returns the number of days in a given Persian month of a specific year.
     */
    fun getDaysInPersianMonth(year: Int, month: Int): Int {
        return when {
            month in 1..6 -> 31
            month in 7..11 -> 30
            month == 12 -> if (isPersianLeapYear(year)) 30 else 29
            else -> 30
        }
    }

    /**
     * Converts Gregorian Date (Year, Month, Day) to Persian Date.
     */
    fun gregorianToPersian(gYear: Int, gMonth: Int, gDay: Int): PersianDate {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy = gYear - 1600
        val gm = gMonth - 1
        val gd = gDay - 1

        var gDayNo = 365 * gy + ((gy + 3) / 4) - ((gy + 99) / 100) + ((gy + 399) / 400)

        for (i in 0 until gm) {
            gDayNo += gDaysInMonth[i]
        }
        if (gm > 1 && ((gYear % 4 == 0 && gYear % 100 != 0) || (gYear % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gd

        var jDayNo = gDayNo - 79

        val jNp = floor(jDayNo.toDouble() / 12053.0).toInt()
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += ((jDayNo - 1) / 365)
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        while (jm < 11 && jDayNo >= jDaysInMonth[jm]) {
            jDayNo -= jDaysInMonth[jm]
            jm++
        }

        val jd = jDayNo + 1
        return PersianDate(jy, jm + 1, jd)
    }

    /**
     * Converts Persian Date (Year, Month, Day) to Gregorian Date.
     */
    fun persianToGregorian(jYear: Int, jMonth: Int, jDay: Int): GregorianDate {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val jy = jYear - 979
        val jm = jMonth - 1
        val jd = jDay - 1

        var jDayNo = 365 * jy + (jy / 33) * 8 + ((jy % 33 + 3) / 4)
        for (i in 0 until jm) {
            jDayNo += jDaysInMonth[i]
        }
        jDayNo += jd

        var gDayNo = jDayNo + 79

        var gy = 1600 + 400 * (gDayNo / 146097)
        gDayNo %= 146097

        var leap = true
        if (gDayNo >= 36525) {
            gDayNo--
            gy += 100 * (gDayNo / 36524)
            gDayNo %= 36524

            if (gDayNo >= 365) {
                gDayNo++
            } else {
                leap = false
            }
        }

        gy += 4 * (gDayNo / 1461)
        gDayNo %= 1461

        if (gDayNo >= 366) {
            leap = false
            gDayNo--
            gy += gDayNo / 365
            gDayNo %= 365
        }

        var gm = 0
        while (true) {
            val daysInMonth = if (gm == 1 && leap) 29 else gDaysInMonth[gm]
            if (gDayNo >= daysInMonth) {
                gDayNo -= daysInMonth
                gm++
            } else {
                break
            }
        }

        val gd = gDayNo + 1
        return GregorianDate(gy, gm + 1, gd)
    }

    /**
     * Gets today's Persian Date based on current system time.
     */
    fun getTodayPersian(): PersianDate {
        val calendar = Calendar.getInstance()
        val gYear = calendar.get(Calendar.YEAR)
        val gMonth = calendar.get(Calendar.MONTH) + 1
        val gDay = calendar.get(Calendar.DAY_OF_MONTH)
        return gregorianToPersian(gYear, gMonth, gDay)
    }

    /**
     * Gets today's Gregorian Date.
     */
    fun getTodayGregorian(): GregorianDate {
        val calendar = Calendar.getInstance()
        return GregorianDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    /**
     * Returns the day of week index for a Persian date:
     * 0 = شنبه (Saturday)
     * 1 = یکشنبه (Sunday)
     * ...
     * 6 = جمعه (Friday)
     */
    fun getDayOfWeekIndex(persianDate: PersianDate): Int {
        val gDate = persianToGregorian(persianDate.year, persianDate.month, persianDate.day)
        val calendar = Calendar.getInstance()
        calendar.set(gDate.year, gDate.month - 1, gDate.day)
        val javaDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
        return when (javaDayOfWeek) {
            Calendar.SATURDAY -> 0
            Calendar.SUNDAY -> 1
            Calendar.MONDAY -> 2
            Calendar.TUESDAY -> 3
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 5
            Calendar.FRIDAY -> 6
            else -> 0
        }
    }

    fun getDayOfWeekName(persianDate: PersianDate): String {
        val idx = getDayOfWeekIndex(persianDate)
        return WEEK_DAYS_PERSIAN[idx]
    }

    /**
     * Calculates days passed and remaining in current Persian year.
     */
    fun getYearProgressInfo(persianDate: PersianDate): Pair<Int, Int> {
        var daysPassed = 0
        for (m in 1 until persianDate.month) {
            daysPassed += getDaysInPersianMonth(persianDate.year, m)
        }
        daysPassed += persianDate.day

        val totalDaysInYear = if (isPersianLeapYear(persianDate.year)) 366 else 365
        val daysRemaining = totalDaysInYear - daysPassed
        return Pair(daysPassed, daysRemaining)
    }

    /**
     * Returns Persian Zodiac / برج فلکی
     */
    fun getZodiacSign(month: Int): String {
        return when (month) {
            1 -> "برج حمل ♈"
            2 -> "برج ثور ♉"
            3 -> "برج جوزا ♊"
            4 -> "برج سرطان ♋"
            5 -> "برج اسد ♌"
            6 -> "برج سنبله ♍"
            7 -> "برج میزان ♎"
            8 -> "برج عقرب ♏"
            9 -> "برج قوس ♐"
            10 -> "برج جدی ♑"
            11 -> "برج دلو ♒"
            12 -> "برج حوت ♓"
            else -> ""
        }
    }

    /**
     * Converts Gregorian Date to Islamic / Hijri Lunar (هجری قمری) Date.
     */
    fun gregorianToHijri(gYear: Int, gMonth: Int, gDay: Int): HijriDate {
        var y = gYear
        var m = gMonth
        val d = gDay

        if (m < 3) {
            y -= 1
            m += 12
        }

        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + d + b - 1524.5

        val z = floor(jd + 0.5)
        val l = (z - 1948440 + 10632).toLong()
        val n = ((l - 1) / 10631).toInt()
        val l1 = l - 10631 * n + 354
        val j = (((10985 - l1) / 5316).toInt()) * (((50 * l1) / 17719).toInt()) +
                ((l1 / 5670).toInt()) * (((43 * l1) / 15238).toInt())
        val l2 = l1 - (((30 - j) / 15).toInt()) * (((17719 * j) / 50).toInt()) -
                ((j / 16).toInt()) * (((15238 * j) / 43).toInt()) + 29
        val hMonth = ((24 * l2) / 709).toInt()
        val hDay = (l2 - ((709 * hMonth) / 24)).toInt()
        val hYear = (30 * n + j - 30).toInt()

        return HijriDate(hYear, hMonth.coerceIn(1, 12), hDay.coerceIn(1, 30))
    }

    /**
     * Converts Persian Date to Islamic / Hijri Lunar (هجری قمری) Date.
     */
    fun persianToHijri(jYear: Int, jMonth: Int, jDay: Int): HijriDate {
        val gDate = persianToGregorian(jYear, jMonth, jDay)
        return gregorianToHijri(gDate.year, gDate.month, gDate.day)
    }

    /**
     * Calculates Age in completed years, months, days and the birthday cake candle number to blow out.
     */
    fun calculateAgeAndCandle(
        birthYear: Int,
        birthMonth: Int,
        birthDay: Int,
        currentDate: PersianDate = getTodayPersian()
    ): BirthdayAgeInfo {
        if (birthYear > currentDate.year ||
            (birthYear == currentDate.year && (birthMonth > currentDate.month || (birthMonth == currentDate.month && birthDay > currentDate.day)))
        ) {
            return BirthdayAgeInfo(
                ageYears = 0,
                ageMonths = 0,
                ageDays = 0,
                candleNumber = 1,
                daysUntilNextBirthday = 0,
                isBirthdayToday = false
            )
        }

        var years = currentDate.year - birthYear
        var months = currentDate.month - birthMonth
        var days = currentDate.day - birthDay

        if (days < 0) {
            months--
            val prevMonth = if (currentDate.month == 1) 12 else currentDate.month - 1
            val prevYear = if (currentDate.month == 1) currentDate.year - 1 else currentDate.year
            days += getDaysInPersianMonth(prevYear, prevMonth)
        }
        if (months < 0) {
            years--
            months += 12
        }
        if (years < 0) years = 0

        // Candle number to blow out on current / next turning birthday:
        val isBirthdayToday = (currentDate.month == birthMonth && currentDate.day == birthDay)
        val candleNumber = if (isBirthdayToday) years else years + 1

        // Days until next birthday
        val nextBdayYear = if (currentDate.month > birthMonth || (currentDate.month == birthMonth && currentDate.day > birthDay)) {
            currentDate.year + 1
        } else {
            currentDate.year
        }

        // Approximate days remaining to next birthday
        var daysUntilNext = 0
        if (!isBirthdayToday) {
            val (currentPassed, _) = getYearProgressInfo(currentDate)
            val (nextBdayPassed, _) = getYearProgressInfo(PersianDate(nextBdayYear, birthMonth, birthDay))
            daysUntilNext = if (nextBdayYear == currentDate.year) {
                (nextBdayPassed - currentPassed).coerceAtLeast(0)
            } else {
                val totalDaysCurrentYear = if (isPersianLeapYear(currentDate.year)) 366 else 365
                (totalDaysCurrentYear - currentPassed + nextBdayPassed).coerceAtLeast(0)
            }
        }

        return BirthdayAgeInfo(
            ageYears = years,
            ageMonths = months,
            ageDays = days,
            candleNumber = candleNumber,
            daysUntilNextBirthday = daysUntilNext,
            isBirthdayToday = isBirthdayToday
        )
    }

    /**
     * Comprehensive database of Iranian official holidays and global events.
     */
    fun getOccasionsForPersianDate(year: Int, month: Int, day: Int): List<CalendarOccasion> {
        val list = mutableListOf<CalendarOccasion>()
        val key = "$month/$day"

        // Persian Fixed / Iranian Solar Holidays & Celebrations
        when (key) {
            "1/1" -> list.add(CalendarOccasion("عید نوروز - آغاز سال نو خورشیدی", isOfficialHoliday = true, isIranian = true))
            "1/2" -> list.add(CalendarOccasion("عید نوروز", isOfficialHoliday = true, isIranian = true))
            "1/3" -> list.add(CalendarOccasion("عید نوروز", isOfficialHoliday = true, isIranian = true))
            "1/4" -> list.add(CalendarOccasion("عید نوروز", isOfficialHoliday = true, isIranian = true))
            "1/6" -> list.add(CalendarOccasion("زادروز زرتشت (جشن فروردین‌گان)", isOfficialHoliday = false, isIranian = true))
            "1/12" -> list.add(CalendarOccasion("روز جمهوری اسلامی ایران", isOfficialHoliday = true, isIranian = true))
            "1/13" -> list.add(CalendarOccasion("روز طبیعت (سیزده‌به‌در)", isOfficialHoliday = true, isIranian = true))
            "1/25" -> list.add(CalendarOccasion("روز بزرگداشت عطار نیشابوری", isOfficialHoliday = false, isIranian = true))
            "1/29" -> list.add(CalendarOccasion("روز ارتش جمهوری اسلامی ایران", isOfficialHoliday = false, isIranian = true))
            "2/1" -> list.add(CalendarOccasion("روز بزرگداشت سعدی شیرازی", isOfficialHoliday = false, isIranian = true))
            "2/10" -> list.add(CalendarOccasion("روز ملی خلیج فارس", isOfficialHoliday = false, isIranian = true))
            "2/12" -> list.add(CalendarOccasion("روز معلم و شهادت استاد مطهری", isOfficialHoliday = false, isIranian = true))
            "2/15" -> list.add(CalendarOccasion("جشن بهاربد / روز شیراز", isOfficialHoliday = false, isIranian = true))
            "2/25" -> list.add(CalendarOccasion("روز بزرگداشت حکیم ابوالقاسم فردوسی", isOfficialHoliday = false, isIranian = true))
            "2/28" -> list.add(CalendarOccasion("روز بزرگداشت حکیم عمر خیام", isOfficialHoliday = false, isIranian = true))
            "3/1" -> list.add(CalendarOccasion("روز بزرگداشت ملاصدرا", isOfficialHoliday = false, isIranian = true))
            "3/3" -> list.add(CalendarOccasion("فتح خرمشهر در عملیات بیت‌المقدس", isOfficialHoliday = false, isIranian = true))
            "3/14" -> list.add(CalendarOccasion("رحلت امام خمینی (ره)", isOfficialHoliday = true, isIranian = true))
            "3/15" -> list.add(CalendarOccasion("قیام ۱۵ خرداد", isOfficialHoliday = true, isIranian = true))
            "4/1" -> list.add(CalendarOccasion("جشن آب‌پاشونک (آغاز تابستان)", isOfficialHoliday = false, isIranian = true))
            "4/7" -> list.add(CalendarOccasion("شهادت آیت‌الله بهشتی و ۷۲ تن از یارانش", isOfficialHoliday = false, isIranian = true))
            "4/14" -> list.add(CalendarOccasion("روز قلم", isOfficialHoliday = false, isIranian = true))
            "5/8" -> list.add(CalendarOccasion("روز بزرگداشت شیخ شهاب‌الدین سهروردی", isOfficialHoliday = false, isIranian = true))
            "5/14" -> list.add(CalendarOccasion("صدور فرمان مشروطیت", isOfficialHoliday = false, isIranian = true))
            "5/28" -> list.add(CalendarOccasion("کودتای ۲۸ مرداد ۱۳۳۲", isOfficialHoliday = false, isIranian = true))
            "6/1" -> list.add(CalendarOccasion("روز پزشک (بزرگداشت ابوعلی سینا)", isOfficialHoliday = false, isIranian = true))
            "6/5" -> list.add(CalendarOccasion("روز داروسازی (بزرگداشت زکریای رازی)", isOfficialHoliday = false, isIranian = true))
            "6/8" -> list.add(CalendarOccasion("روز مبارزه با تروریسم (شهادت رجایی و باهنر)", isOfficialHoliday = false, isIranian = true))
            "6/27" -> list.add(CalendarOccasion("روز شعر و ادب فارسی (بزرگداشت استاد شهریار)", isOfficialHoliday = false, isIranian = true))
            "6/31" -> list.add(CalendarOccasion("آغاز هفته دفاع مقدس", isOfficialHoliday = false, isIranian = true))
            "7/1" -> list.add(CalendarOccasion("جشن پاییزه / آغاز سال تحصیلی", isOfficialHoliday = false, isIranian = true))
            "7/8" -> list.add(CalendarOccasion("روز بزرگداشت مولوی (مولانا)", isOfficialHoliday = false, isIranian = true))
            "7/20" -> list.add(CalendarOccasion("روز بزرگداشت حافظ شیرازی", isOfficialHoliday = false, isIranian = true))
            "7/26" -> list.add(CalendarOccasion("روز تربیت بدنی و ورزش", isOfficialHoliday = false, isIranian = true))
            "8/7" -> list.add(CalendarOccasion("روز بزرگداشت کوروش بزرگ", isOfficialHoliday = false, isIranian = true))
            "8/13" -> list.add(CalendarOccasion("روز دانش‌آموز", isOfficialHoliday = false, isIranian = true))
            "8/24" -> list.add(CalendarOccasion("روز کتاب و کتابخوانی", isOfficialHoliday = false, isIranian = true))
            "9/16" -> list.add(CalendarOccasion("روز دانشجو", isOfficialHoliday = false, isIranian = true))
            "9/30" -> list.add(CalendarOccasion("شب یلدا (جشن چله)", isOfficialHoliday = false, isIranian = true))
            "10/1" -> list.add(CalendarOccasion("جشن خرم‌روز (نخستین روز زمستان)", isOfficialHoliday = false, isIranian = true))
            "10/10" -> list.add(CalendarOccasion("جشن دی‌گان", isOfficialHoliday = false, isIranian = true))
            "11/12" -> list.add(CalendarOccasion("بازگشت امام خمینی به ایران (آغاز دهه فجر)", isOfficialHoliday = false, isIranian = true))
            "11/22" -> list.add(CalendarOccasion("پیروزی انقلاب اسلامی ایران", isOfficialHoliday = true, isIranian = true))
            "11/29" -> list.add(CalendarOccasion("جشن سپندارمذگان (روز مهر و مادر ایرانی)", isOfficialHoliday = false, isIranian = true))
            "12/5" -> list.add(CalendarOccasion("روز مهندس (بزرگداشت خواجه نصیرالدین طوسی)", isOfficialHoliday = false, isIranian = true))
            "12/15" -> list.add(CalendarOccasion("روز درختکاری", isOfficialHoliday = false, isIranian = true))
            "12/29" -> list.add(CalendarOccasion("روز ملی شدن صنعت نفت ایران", isOfficialHoliday = true, isIranian = true))
        }

        // Islamic Occasions matched through Hijri calculation
        val hDate = persianToHijri(year, month, day)
        val hKey = "${hDate.month}/${hDate.day}"

        when (hKey) {
            "1/9" -> list.add(CalendarOccasion("تاسوعای حسینی", isOfficialHoliday = true, isIranian = true))
            "1/10" -> list.add(CalendarOccasion("عاشورای حسینی (شهادت امام حسین علیه‌السلام)", isOfficialHoliday = true, isIranian = true))
            "1/12" -> list.add(CalendarOccasion("شهادت امام سجاد علیه‌السلام", isOfficialHoliday = false, isIranian = true))
            "2/20" -> list.add(CalendarOccasion("اربعین حسینی", isOfficialHoliday = true, isIranian = true))
            "2/28" -> list.add(CalendarOccasion("رحلت پیامبر اکرم (ص) و شهادت امام حسن مجتبی (ع)", isOfficialHoliday = true, isIranian = true))
            "2/29", "2/30" -> list.add(CalendarOccasion("شهادت امام رضا علیه‌السلام", isOfficialHoliday = true, isIranian = true))
            "3/1" -> list.add(CalendarOccasion("هجرت پیامبر اکرم از مکه به مدینه (آغاز سال قمری)", isOfficialHoliday = false, isIranian = true))
            "3/8" -> list.add(CalendarOccasion("شهادت امام حسن عسکری (ع)", isOfficialHoliday = true, isIranian = true))
            "3/17" -> list.add(CalendarOccasion("ولادت حضرت رسول اکرم (ص) و ولادت امام جعفر صادق (ع)", isOfficialHoliday = true, isIranian = true))
            "5/5" -> list.add(CalendarOccasion("ولادت حضرت زینب (س) و روز پرستار", isOfficialHoliday = false, isIranian = true))
            "6/3" -> list.add(CalendarOccasion("شهادت حضرت فاطمه زهرا (س)", isOfficialHoliday = true, isIranian = true))
            "7/13" -> list.add(CalendarOccasion("ولادت امام علی علیه‌السلام و روز پدر", isOfficialHoliday = true, isIranian = true))
            "7/27" -> list.add(CalendarOccasion("عید سعید مبعث رسول اکرم (ص)", isOfficialHoliday = true, isIranian = true))
            "8/3" -> list.add(CalendarOccasion("ولادت امام حسین (ع) و روز پاسدار", isOfficialHoliday = false, isIranian = true))
            "8/4" -> list.add(CalendarOccasion("ولادت حضرت ابوالفضل العباس (ع) و روز جانباز", isOfficialHoliday = false, isIranian = true))
            "8/5" -> list.add(CalendarOccasion("ولادت امام سجاد (ع)", isOfficialHoliday = false, isIranian = true))
            "8/15" -> list.add(CalendarOccasion("ولادت حضرت قائم (عج) و جشن نیمه شعبان", isOfficialHoliday = true, isIranian = true))
            "9/1" -> list.add(CalendarOccasion("آغاز ماه مبارک رمضان", isOfficialHoliday = false, isIranian = true))
            "9/15" -> list.add(CalendarOccasion("ولادت امام حسن مجتبی (ع)", isOfficialHoliday = false, isIranian = true))
            "9/19" -> list.add(CalendarOccasion("شب قدر و ضربت خوردن حضرت علی (ع)", isOfficialHoliday = false, isIranian = true))
            "9/21" -> list.add(CalendarOccasion("شهادت حضرت علی علیه‌السلام", isOfficialHoliday = true, isIranian = true))
            "9/23" -> list.add(CalendarOccasion("شب قدر", isOfficialHoliday = false, isIranian = true))
            "10/1" -> list.add(CalendarOccasion("عید سعید فطر", isOfficialHoliday = true, isIranian = true))
            "10/2" -> list.add(CalendarOccasion("تعطیلی به مناسبت عید سعید فطر", isOfficialHoliday = true, isIranian = true))
            "10/25" -> list.add(CalendarOccasion("شهادت امام جعفر صادق علیه‌السلام", isOfficialHoliday = true, isIranian = true))
            "11/1" -> list.add(CalendarOccasion("ولادت حضرت معصومه (س) و روز دختر", isOfficialHoliday = false, isIranian = true))
            "11/11" -> list.add(CalendarOccasion("ولادت امام رضا علیه‌السلام", isOfficialHoliday = false, isIranian = true))
            "12/1" -> list.add(CalendarOccasion("سالروز ازدواج حضرت علی (ع) و حضرت فاطمه (س)", isOfficialHoliday = false, isIranian = true))
            "12/9" -> list.add(CalendarOccasion("روز عرفه (نیایش)", isOfficialHoliday = false, isIranian = true))
            "12/10" -> list.add(CalendarOccasion("عید سعید قربان", isOfficialHoliday = true, isIranian = true))
            "12/15" -> list.add(CalendarOccasion("ولادت امام علی النقی الهادی (ع)", isOfficialHoliday = false, isIranian = true))
            "12/18" -> list.add(CalendarOccasion("عید سعید غدیر خم", isOfficialHoliday = true, isIranian = true))
        }

        // Global / International Occasions matched through Gregorian calculation
        val gDate = persianToGregorian(year, month, day)
        val gKey = "${gDate.month}/${gDate.day}"

        when (gKey) {
            "1/1" -> list.add(CalendarOccasion("آغاز سال نو میلادی (New Year)", isOfficialHoliday = false, isIranian = false))
            "1/24" -> list.add(CalendarOccasion("روز جهانی آموزش (International Day of Education)", isOfficialHoliday = false, isIranian = false))
            "2/4" -> list.add(CalendarOccasion("روز جهانی سرطان (World Cancer Day)", isOfficialHoliday = false, isIranian = false))
            "2/11" -> list.add(CalendarOccasion("روز جهانی زنان و دختران در علم", isOfficialHoliday = false, isIranian = false))
            "2/14" -> list.add(CalendarOccasion("روز جهانی ولنتاین (Valentine's Day)", isOfficialHoliday = false, isIranian = false))
            "2/20" -> list.add(CalendarOccasion("روز جهانی عدالت اجتماعی (World Day of Social Justice)", isOfficialHoliday = false, isIranian = false))
            "2/21" -> list.add(CalendarOccasion("روز جهانی زبان مادری (Mother Language Day)", isOfficialHoliday = false, isIranian = false))
            "3/1" -> list.add(CalendarOccasion("روز جهانی تبعیض صفر (Zero Discrimination Day)", isOfficialHoliday = false, isIranian = false))
            "3/3" -> list.add(CalendarOccasion("روز جهانی حیات وحش (World Wildlife Day)", isOfficialHoliday = false, isIranian = false))
            "3/8" -> list.add(CalendarOccasion("روز جهانی زن (International Women's Day)", isOfficialHoliday = false, isIranian = false))
            "3/14" -> list.add(CalendarOccasion("روز جهانی عدد پی و ریاضیات (Pi Day)", isOfficialHoliday = false, isIranian = false))
            "3/20" -> list.add(CalendarOccasion("روز جهانی شادی (International Day of Happiness)", isOfficialHoliday = false, isIranian = false))
            "3/21" -> list.add(CalendarOccasion("روز جهانی شعر و روز بین‌المللی نوروز (World Poetry Day)", isOfficialHoliday = false, isIranian = false))
            "3/22" -> list.add(CalendarOccasion("روز جهانی آب (World Water Day)", isOfficialHoliday = false, isIranian = false))
            "3/23" -> list.add(CalendarOccasion("روز جهانی هواشناسی (World Meteorological Day)", isOfficialHoliday = false, isIranian = false))
            "4/7" -> list.add(CalendarOccasion("روز جهانی بهداشت و سلامتی (World Health Day)", isOfficialHoliday = false, isIranian = false))
            "4/22" -> list.add(CalendarOccasion("روز جهانی زمین پاک (Earth Day)", isOfficialHoliday = false, isIranian = false))
            "4/23" -> list.add(CalendarOccasion("روز جهانی کتاب و حق نشر (World Book Day)", isOfficialHoliday = false, isIranian = false))
            "5/1" -> list.add(CalendarOccasion("روز جهانی کار و کارگر (Workers' Day)", isOfficialHoliday = false, isIranian = false))
            "5/3" -> list.add(CalendarOccasion("روز جهانی آزادی مطبوعات (World Press Freedom Day)", isOfficialHoliday = false, isIranian = false))
            "5/8" -> list.add(CalendarOccasion("روز جهانی صلیب سرخ و هلال احمر (Red Cross Day)", isOfficialHoliday = false, isIranian = false))
            "5/15" -> list.add(CalendarOccasion("روز جهانی خانواده (International Day of Families)", isOfficialHoliday = false, isIranian = false))
            "5/31" -> list.add(CalendarOccasion("روز جهانی بدون دخانیات (World No Tobacco Day)", isOfficialHoliday = false, isIranian = false))
            "6/1" -> list.add(CalendarOccasion("روز جهانی والدین و کودکان", isOfficialHoliday = false, isIranian = false))
            "6/5" -> list.add(CalendarOccasion("روز جهانی محیط زیست (World Environment Day)", isOfficialHoliday = false, isIranian = false))
            "6/8" -> list.add(CalendarOccasion("روز جهانی اقیانوس‌ها (World Oceans Day)", isOfficialHoliday = false, isIranian = false))
            "6/20" -> list.add(CalendarOccasion("روز جهانی پناهندگان (World Refugee Day)", isOfficialHoliday = false, isIranian = false))
            "6/21" -> list.add(CalendarOccasion("روز جهانی موسیقی و یوگا", isOfficialHoliday = false, isIranian = false))
            "7/11" -> list.add(CalendarOccasion("روز جهانی جمعیت (World Population Day)", isOfficialHoliday = false, isIranian = false))
            "7/30" -> list.add(CalendarOccasion("روز بین‌المللی دوستی (International Friendship Day)", isOfficialHoliday = false, isIranian = false))
            "8/12" -> list.add(CalendarOccasion("روز بین‌المللی جوانان (International Youth Day)", isOfficialHoliday = false, isIranian = false))
            "8/19" -> list.add(CalendarOccasion("روز جهانی عکاسی و بشردوستی (World Photography Day)", isOfficialHoliday = false, isIranian = false))
            "9/8" -> list.add(CalendarOccasion("روز بین‌المللی سوادآموزی (International Literacy Day)", isOfficialHoliday = false, isIranian = false))
            "9/21" -> list.add(CalendarOccasion("روز جهانی صلح (International Day of Peace)", isOfficialHoliday = false, isIranian = false))
            "9/27" -> list.add(CalendarOccasion("روز جهانی گردشگری (World Tourism Day)", isOfficialHoliday = false, isIranian = false))
            "10/1" -> list.add(CalendarOccasion("روز جهانی سالمندان و موسیقی (International Day of Older Persons)", isOfficialHoliday = false, isIranian = false))
            "10/5" -> list.add(CalendarOccasion("روز جهانی معلم (World Teachers' Day)", isOfficialHoliday = false, isIranian = false))
            "10/10" -> list.add(CalendarOccasion("روز جهانی سلامت روان (World Mental Health Day)", isOfficialHoliday = false, isIranian = false))
            "10/16" -> list.add(CalendarOccasion("روز جهانی غذا (World Food Day)", isOfficialHoliday = false, isIranian = false))
            "10/24" -> list.add(CalendarOccasion("روز سازمان ملل متحد (United Nations Day)", isOfficialHoliday = false, isIranian = false))
            "11/10" -> list.add(CalendarOccasion("روز جهانی علم در خدمت صلح و توسعه", isOfficialHoliday = false, isIranian = false))
            "11/14" -> list.add(CalendarOccasion("روز جهانی دیابت (World Diabetes Day)", isOfficialHoliday = false, isIranian = false))
            "11/20" -> list.add(CalendarOccasion("روز جهانی کودک (Universal Children's Day)", isOfficialHoliday = false, isIranian = false))
            "11/25" -> list.add(CalendarOccasion("روز بین‌المللی مبارزه با خشونت علیه زنان", isOfficialHoliday = false, isIranian = false))
            "12/1" -> list.add(CalendarOccasion("روز جهانی ایدز (World AIDS Day)", isOfficialHoliday = false, isIranian = false))
            "12/3" -> list.add(CalendarOccasion("روز جهانی معلولان (International Day of Persons with Disabilities)", isOfficialHoliday = false, isIranian = false))
            "12/5" -> list.add(CalendarOccasion("روز جهانی داوطلبان و روز جهانی خاک", isOfficialHoliday = false, isIranian = false))
            "12/10" -> list.add(CalendarOccasion("روز جهانی حقوق بشر (Human Rights Day)", isOfficialHoliday = false, isIranian = false))
            "12/25" -> list.add(CalendarOccasion("جشن میلاد حضرت عیسی مسیح (کریسمس)", isOfficialHoliday = false, isIranian = false))
        }

        return list
    }
}

data class BirthdayAgeInfo(
    val ageYears: Int,
    val ageMonths: Int,
    val ageDays: Int,
    val candleNumber: Int,
    val daysUntilNextBirthday: Int,
    val isBirthdayToday: Boolean
)
