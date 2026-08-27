package com.example.calendar.data.weather

import com.example.calendar.core.PersianCalendarHelper
import com.example.calendar.core.PersianDate
import kotlin.math.roundToInt

data class WeatherData(
    val cityName: String,
    val provinceName: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int,
    val conditionText: String,
    val conditionEmoji: String,
    val tempMax: Double,
    val tempMin: Double,
    val dailyForecast: List<DailyForecastItem> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun getFormattedTemp(): String = "${PersianCalendarHelper.toPersianDigits(temperature.roundToInt())}°"
    fun getFormattedFeelsLike(): String = "${PersianCalendarHelper.toPersianDigits(feelsLike.roundToInt())}°"
    fun getFormattedMax(): String = "${PersianCalendarHelper.toPersianDigits(tempMax.roundToInt())}°"
    fun getFormattedMin(): String = "${PersianCalendarHelper.toPersianDigits(tempMin.roundToInt())}°"
    fun getFormattedHumidity(): String = "${PersianCalendarHelper.toPersianDigits(humidity)}٪"
    fun getFormattedWind(): String = "${PersianCalendarHelper.toPersianDigits(windSpeed.roundToInt())} km/h"
}

data class DailyForecastItem(
    val dayOfWeekName: String,
    val persianDateStr: String,
    val tempMax: Double,
    val tempMin: Double,
    val weatherCode: Int,
    val conditionText: String,
    val conditionEmoji: String
)

object WeatherCodeMapper {
    fun mapCode(code: Int): Pair<String, String> {
        return when (code) {
            0 -> Pair("صاف و آفتابی", "☀️")
            1 -> Pair("عمدتاً صاف", "🌤️")
            2 -> Pair("نیمه‌ابری", "⛅")
            3 -> Pair("تمام ابری", "☁️")
            45, 48 -> Pair("مه‌آلود", "🌫️")
            51, 53, 55 -> Pair("نم‌نم باران", "🌦️")
            56, 57 -> Pair("باران یخی ملایم", "🌨️")
            61 -> Pair("باران ملایم", "🌧️")
            63 -> Pair("باران متوسط", "🌧️")
            65 -> Pair("بارش شدید باران", "⛈️")
            66, 67 -> Pair("باران شدید یخ‌زده", "🌨️")
            71 -> Pair("بارش برف ملایم", "🌨️")
            73 -> Pair("بارش برف متوسط", "❄️")
            75 -> Pair("بارش برف سنگین", "❄️")
            77 -> Pair("دانه‌های برف", "❄️")
            80, 81 -> Pair("رگبار باران", "🌦️")
            82 -> Pair("رگبار شدید باران", "⛈️")
            85, 86 -> Pair("رگبار برف", "🌨️")
            95 -> Pair("رعد و برق", "⛈️")
            96, 99 -> Pair("طوفان و تگرگ", "⛈️")
            else -> Pair("هوای معتدل", "🌤️")
        }
    }
}
