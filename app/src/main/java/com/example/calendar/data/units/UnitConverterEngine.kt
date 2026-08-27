package com.example.calendar.data.units

import java.text.DecimalFormat

enum class UnitCategory(val title: String, val iconEmoji: String) {
    WEIGHT("وزن و جرم", "⚖️"),
    LENGTH("طول و مسافت", "📏"),
    VOLUME("حجم و مایعات", "🧪"),
    TEMPERATURE("دما", "🌡️"),
    SPEED("سرعت", "🚗"),
    AREA("مساحت", "📐"),
    DATA("حجم حافظه", "💾"),
    TIME("زمان", "⏱️"),
    PRESSURE("فشار", "💨")
}

data class UnitItem(
    val id: String,
    val namePersian: String,
    val symbol: String,
    val toBaseFactor: Double // factor to convert 1 unit to base unit
)

object UnitConverterEngine {

    val weightUnits = listOf(
        UnitItem("kg", "کیلوگرم", "kg", 1.0),
        UnitItem("g", "گرم", "g", 0.001),
        UnitItem("mg", "میلی‌گرم", "mg", 0.000001),
        UnitItem("lb", "پوند", "lb", 0.45359237),
        UnitItem("oz", "اونس", "oz", 0.02834952),
        UnitItem("ton", "تن متریک", "t", 1000.0),
        UnitItem("mesghal", "مثقال شرعی", "مثقال", 0.004608),
        UnitItem("seer", "سیر", "سیر", 0.075),
        UnitItem("carat", "قیراط", "ct", 0.0002)
    )

    val lengthUnits = listOf(
        UnitItem("m", "متر", "m", 1.0),
        UnitItem("cm", "سانتی‌متر", "cm", 0.01),
        UnitItem("mm", "میلی‌متر", "mm", 0.001),
        UnitItem("km", "کیلومتر", "km", 1000.0),
        UnitItem("inch", "اینچ", "in", 0.0254),
        UnitItem("foot", "فوت / پا", "ft", 0.3048),
        UnitItem("yard", "یارد", "yd", 0.9144),
        UnitItem("mile", "مایل", "mi", 1609.344),
        UnitItem("nmi", "مایل دریایی", "NM", 1852.0)
    )

    val volumeUnits = listOf(
        UnitItem("L", "لیتر", "L", 1.0),
        UnitItem("mL", "میلی‌لیتر (cc)", "mL/cc", 0.001),
        UnitItem("m3", "متر مکعب", "m³", 1000.0),
        UnitItem("cm3", "سانتی‌متر مکعب", "cm³", 0.001),
        UnitItem("gal_us", "گالن (آمریکا)", "gal", 3.78541),
        UnitItem("gal_uk", "گالن (بریتانیا)", "gal UK", 4.54609),
        UnitItem("cup", "فنجان / لیوان آشپزی", "cup", 0.24),
        UnitItem("tbsp", "قاشق غذاخوری", "tbsp", 0.015),
        UnitItem("tsp", "قاشق چایخوری", "tsp", 0.005)
    )

    val speedUnits = listOf(
        UnitItem("kmh", "کیلومتر بر ساعت", "km/h", 1.0),
        UnitItem("ms", "متر بر ثانیه", "m/s", 3.6),
        UnitItem("mph", "مایل بر ساعت", "mph", 1.609344),
        UnitItem("knot", "نات (دریانوردی)", "kn", 1.852),
        UnitItem("mach", "ماخ (سرعت صوت)", "Mach", 1234.8)
    )

    val areaUnits = listOf(
        UnitItem("m2", "متر مربع", "m²", 1.0),
        UnitItem("cm2", "سانتی‌متر مربع", "cm²", 0.0001),
        UnitItem("km2", "کیلومتر مربع", "km²", 1000000.0),
        UnitItem("ha", "هکتار", "ha", 10000.0),
        UnitItem("acre", "جریب (آکر)", "acre", 4046.856),
        UnitItem("ft2", "فوت مربع", "ft²", 0.092903),
        UnitItem("in2", "اینچ مربع", "in²", 0.00064516)
    )

    val dataUnits = listOf(
        UnitItem("B", "بایت", "B", 1.0),
        UnitItem("KB", "کیلوبایت", "KB", 1024.0),
        UnitItem("MB", "مگابایت", "MB", 1024.0 * 1024.0),
        UnitItem("GB", "گیگابایت", "GB", 1024.0 * 1024.0 * 1024.0),
        UnitItem("TB", "ترابایت", "TB", 1024.0 * 1024.0 * 1024.0 * 1024.0),
        UnitItem("bit", "بیت", "bit", 0.125)
    )

    val pressureUnits = listOf(
        UnitItem("bar", "بار", "bar", 1.0),
        UnitItem("atm", "اتمسفر", "atm", 1.01325),
        UnitItem("kpa", "کیلو پاسکال", "kPa", 0.01),
        UnitItem("pa", "پاسکال", "Pa", 0.00001),
        UnitItem("psi", "پوند بر اینچ مربع (PSI)", "psi", 0.0689476),
        UnitItem("mmhg", "میلی‌متر جیوه (mmHg)", "mmHg", 0.00133322)
    )

    val timeUnits = listOf(
        UnitItem("s", "ثانیه", "s", 1.0),
        UnitItem("min", "دقیقه", "min", 60.0),
        UnitItem("h", "ساعت", "h", 3600.0),
        UnitItem("d", "روز", "day", 86400.0),
        UnitItem("w", "هفته", "week", 604800.0),
        UnitItem("m", "ماه (۳۰ روزه)", "month", 2592000.0),
        UnitItem("y", "سال (۳۶۵ روزه)", "year", 31536000.0),
        UnitItem("ms", "میلی‌ثانیه", "ms", 0.001)
    )

    fun getUnitsForCategory(category: UnitCategory): List<UnitItem> {
        return when (category) {
            UnitCategory.WEIGHT -> weightUnits
            UnitCategory.LENGTH -> lengthUnits
            UnitCategory.VOLUME -> volumeUnits
            UnitCategory.SPEED -> speedUnits
            UnitCategory.AREA -> areaUnits
            UnitCategory.DATA -> dataUnits
            UnitCategory.PRESSURE -> pressureUnits
            UnitCategory.TIME -> timeUnits
            UnitCategory.TEMPERATURE -> listOf(
                UnitItem("C", "سلسیوس (سانتی‌گراد)", "°C", 1.0),
                UnitItem("F", "فارنهایت", "°F", 1.0),
                UnitItem("K", "کلوین", "K", 1.0)
            )
        }
    }

    fun convert(category: UnitCategory, value: Double, fromUnitId: String, toUnitId: String): Double {
        if (fromUnitId == toUnitId) return value

        if (category == UnitCategory.TEMPERATURE) {
            // Convert from fromUnit to Celsius first
            val tempInCelsius = when (fromUnitId) {
                "C" -> value
                "F" -> (value - 32.0) * 5.0 / 9.0
                "K" -> value - 273.15
                else -> value
            }
            // Convert from Celsius to toUnit
            return when (toUnitId) {
                "C" -> tempInCelsius
                "F" -> (tempInCelsius * 9.0 / 5.0) + 32.0
                "K" -> tempInCelsius + 273.15
                else -> tempInCelsius
            }
        }

        val units = getUnitsForCategory(category)
        val fromUnit = units.find { it.id == fromUnitId } ?: return value
        val toUnit = units.find { it.id == toUnitId } ?: return value

        val baseValue = value * fromUnit.toBaseFactor
        return baseValue / toUnit.toBaseFactor
    }

    fun formatResult(value: Double): String {
        return if (value == 0.0) {
            "0"
        } else if (Math.abs(value) >= 1_000_000 || (Math.abs(value) < 0.0001 && Math.abs(value) > 0)) {
            val df = DecimalFormat("0.####E0")
            df.format(value)
        } else if (value == value.toLong().toDouble()) {
            DecimalFormat("#,###").format(value.toLong())
        } else {
            val df = DecimalFormat("#,###.####")
            df.format(value)
        }
    }
}
