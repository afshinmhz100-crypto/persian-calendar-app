package com.example.calendar.data.health

import com.example.calendar.core.PersianCalendarHelper
import java.text.DecimalFormat

enum class Gender(val title: String) {
    MALE("آقا 👨"),
    FEMALE("خانم 👩")
}

enum class ActivityLevel(val title: String, val multiplier: Double, val description: String) {
    SEDENTARY("بدون فعالیت / پشت‌میزنشین", 1.2, "فعالیت ورزشی کم یا هیچ"),
    LIGHT("فعالیت سبک (۱ تا ۳ روز در هفته)", 1.375, "پیاده‌روی یا ورزش ملایم"),
    MODERATE("فعالیت متوسط (۳ تا ۵ روز در هفته)", 1.55, "ورزش و تمرین بدنی منظم"),
    VERY_ACTIVE("فعالیت شدید (۶ تا ۷ روز در هفته)", 1.725, "تمرینات پرفشار روزانه"),
    EXTRA_ACTIVE("ورزشکار حرفه‌ای / کار سنگین بدنی", 1.9, "دو نوبت تمرین یا کار یدی سخت")
}

data class HealthAssessmentResult(
    val bmi: Double,
    val bmiCategory: String,
    val bmiStatusColorHex: Long, // e.g. 0xFF4CAF50
    val bmiEmoji: String,
    val idealWeightMinKg: Double,
    val idealWeightMaxKg: Double,
    val idealWeightDevineKg: Double,
    val weightDifferenceKg: Double, // positive means overweight, negative means underweight
    val bmrCalories: Int,
    val tdeeCalories: Int,
    val dailyWaterLiters: Double,
    val bodyFatPercentage: Double,
    val healthAdvice: String
)

object HealthCalculator {

    fun calculate(
        heightCm: Double,
        weightKg: Double,
        age: Int,
        gender: Gender,
        activityLevel: ActivityLevel
    ): HealthAssessmentResult {
        val heightM = heightCm / 100.0
        val heightInches = heightCm / 2.54

        // BMI
        val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 0.0

        val (category, colorHex, emoji, advice) = when {
            bmi < 18.5 -> Quadruple(
                "کمبود وزن (لاغر)",
                0xFF29B6F6, // Blue
                "🥗",
                "شاخص توده بدنی شما کمتر از حد نرمال است. توصیه می‌شود رژیم غذایی غنی از پروتئین، کربوهیدرات‌های پیچیده و چربی‌های مفید همراه با تمرینات مقاومتی داشته باشید."
            )
            bmi in 18.5..24.9 -> Quadruple(
                "وزن نرمال و ایده‌آل",
                0xFF4CAF50, // Green
                "✨",
                "تبریک! وضعیت بدنی شما در محدوده سلامت و استاندارد قرار دارد. با حفظ تغذیه متعادل و ورزش مستمر، این تعادل را نگه دارید."
            )
            bmi in 25.0..29.9 -> Quadruple(
                "اضافه وزن",
                0xFFFFA726, // Orange
                "⚠️",
                "وزن شما کمی بالاتر از حد ایده‌آل است. کاهش مختصر مصرف قندهای ساده و افزایش فعالیت هوازی روزانه (۳۰ دقیقه پیاده‌روی سریع) بسیار مؤثر خواهد بود."
            )
            bmi in 30.0..34.9 -> Quadruple(
                "چاقی درجه ۱",
                0xFFFF7043, // Deep orange
                "🛑",
                "وضعیت بدنی در محدوده چاقی خفیف قرار دارد. پیشنهاد می‌شود برنامه تمرینی منظم و رژیم کالری‌شماری اصولی زیر نظر متخصص تنظیم کنید."
            )
            bmi in 35.0..39.9 -> Quadruple(
                "چاقی درجه ۲",
                0xFFE53935, // Red
                "🚨",
                "شاخص بدنی نشان‌دهنده چاقی متوسط است که می‌تواند خطر بیماری‌های قلبی و دیابت را افزایش دهد. اصلاح سبک زندگی و مشورت پزشکی ضروری است."
            )
            else -> Quadruple(
                "چاقی مفرط (درجه ۳)",
                0xFFB71C1C, // Dark red
                "⚠️",
                "نیاز فوری به پیگیری پزشکی و برنامه جامع کاهش وزن جهت بهبود سلامت عمومی و مفاصل."
            )
        }

        // Ideal weight range based on normal BMI (18.5 - 24.9)
        val minIdealWeight = 18.5 * (heightM * heightM)
        val maxIdealWeight = 24.9 * (heightM * heightM)

        // Devine formula
        val inchesOver5Feet = (heightInches - 60.0).coerceAtLeast(0.0)
        val devineWeight = if (gender == Gender.MALE) {
            50.0 + (2.3 * inchesOver5Feet)
        } else {
            45.5 + (2.3 * inchesOver5Feet)
        }

        val weightDiff = weightKg - ((minIdealWeight + maxIdealWeight) / 2.0)

        // BMR (Mifflin-St Jeor)
        val bmr = if (gender == Gender.MALE) {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5
        } else {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161
        }

        // TDEE
        val tdee = (bmr * activityLevel.multiplier).toInt()

        // Daily water intake: 35ml per kg
        val waterLiters = (weightKg * 0.035).coerceIn(1.5, 5.0)

        // Body fat approx (Deurenberg)
        val genderFactor = if (gender == Gender.MALE) 1 else 0
        val bodyFat = ((1.20 * bmi) + (0.23 * age) - (10.8 * genderFactor) - 5.4).coerceIn(4.0, 65.0)

        return HealthAssessmentResult(
            bmi = Math.round(bmi * 10.0) / 10.0,
            bmiCategory = category,
            bmiStatusColorHex = colorHex,
            bmiEmoji = emoji,
            idealWeightMinKg = Math.round(minIdealWeight * 10.0) / 10.0,
            idealWeightMaxKg = Math.round(maxIdealWeight * 10.0) / 10.0,
            idealWeightDevineKg = Math.round(devineWeight * 10.0) / 10.0,
            weightDifferenceKg = Math.round(weightDiff * 10.0) / 10.0,
            bmrCalories = bmr.toInt(),
            tdeeCalories = tdee,
            dailyWaterLiters = Math.round(waterLiters * 10.0) / 10.0,
            bodyFatPercentage = Math.round(bodyFat * 10.0) / 10.0,
            healthAdvice = advice
        )
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
