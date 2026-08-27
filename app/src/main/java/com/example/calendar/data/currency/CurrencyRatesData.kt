package com.example.calendar.data.currency

import android.util.Log
import com.example.calendar.core.PersianCalendarHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

data class CurrencyItem(
    val code: String,
    val namePersian: String,
    val symbol: String,
    val flagEmoji: String,
    var priceInToman: Double,
    var changePercent: Double = 0.0,
    val category: CurrencyCategory = CurrencyCategory.FIAT,
    var lastUpdate: String = ""
) {
    fun getFormattedPrice(): String {
        val formatter = DecimalFormat("#,###")
        return "${PersianCalendarHelper.toPersianDigits(formatter.format(priceInToman.toLong()))} تومان"
    }
}

enum class CurrencyCategory(val title: String) {
    FIAT("ارزهای بین‌المللی"),
    GOLD("طلا و مسکوکات"),
    CRYPTO("ارزهای دیجیتال")
}

object CurrencyRatesData {
    private const val TAG = "NavasanRates"
    private const val NAVASAN_URL = "https://www.navasan.net/"

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    var lastFetchedTime: String = ""
        private set

    var isLiveFromNavasan: Boolean = false
        private set

    // Standard baseline prices in Tomans (regularly refreshed / updated from Navasan)
    val defaultCurrencies: MutableList<CurrencyItem> = mutableListOf(
        // Fiat Currencies
        CurrencyItem("USD", "دلار آمریکا", "$", "🇺🇸", 93500.0, 0.35, CurrencyCategory.FIAT),
        CurrencyItem("EUR", "یورو اروپا", "€", "🇪🇺", 101800.0, 0.22, CurrencyCategory.FIAT),
        CurrencyItem("AED", "درهم امارات", "AED", "🇦🇪", 25500.0, 0.15, CurrencyCategory.FIAT),
        CurrencyItem("GBP", "پوند انگلیس", "£", "🇬🇧", 119800.0, 0.40, CurrencyCategory.FIAT),
        CurrencyItem("TRY", "لیر ترکیه", "₺", "🇹🇷", 2740.0, -0.10, CurrencyCategory.FIAT),
        CurrencyItem("CNY", "یوان چین", "¥", "🇨🇳", 12900.0, 0.18, CurrencyCategory.FIAT),
        CurrencyItem("CAD", "دلار کانادا", "C$", "🇨🇦", 67800.0, 0.25, CurrencyCategory.FIAT),
        CurrencyItem("IQD", "صد دینار عراق", "IQD", "🇮🇶", 7150.0, 0.05, CurrencyCategory.FIAT),
        CurrencyItem("OMR", "ریال عمان", "OMR", "🇴🇲", 243000.0, 0.30, CurrencyCategory.FIAT),
        CurrencyItem("AUD", "دلار استرالیا", "A$", "🇦🇺", 61200.0, 0.12, CurrencyCategory.FIAT),
        CurrencyItem("KWD", "دینار کویت", "KWD", "🇰🇼", 304000.0, 0.10, CurrencyCategory.FIAT),
        CurrencyItem("CHF", "فرانک سوییس", "CHF", "🇨🇭", 104500.0, 0.15, CurrencyCategory.FIAT),
        CurrencyItem("RUB", "روبل روسیه", "RUB", "🇷🇺", 980.0, -0.05, CurrencyCategory.FIAT),
        CurrencyItem("AZN", "منات آذربایجان", "AZN", "🇦🇿", 55000.0, 0.20, CurrencyCategory.FIAT),

        // Gold and Coins
        CurrencyItem("GOLD_18K", "گرم طلای ۱۸ عیار", "طلا", "🪙", 4680000.0, 0.45, CurrencyCategory.GOLD),
        CurrencyItem("GOLD_24K", "گرم طلای ۲۴ عیار", "طلا", "🥇", 6240000.0, 0.45, CurrencyCategory.GOLD),
        CurrencyItem("COIN_EMAMI", "سکه تمام امامی", "سکه", "🟡", 54200000.0, 0.80, CurrencyCategory.GOLD),
        CurrencyItem("COIN_BAHAR", "سکه بهار آزادی", "سکه", "🟡", 49800000.0, 0.75, CurrencyCategory.GOLD),
        CurrencyItem("COIN_HALF", "نیم سکه بهار آزادی", "سکه", "🌗", 29500000.0, 0.50, CurrencyCategory.GOLD),
        CurrencyItem("COIN_QUARTER", "ربع سکه بهار آزادی", "سکه", "🌘", 19200000.0, 0.60, CurrencyCategory.GOLD),
        CurrencyItem("COIN_GRAM", "سکه یک گرمی", "سکه", "⚪", 8900000.0, 0.20, CurrencyCategory.GOLD),
        CurrencyItem("GOLD_ABSHODEH", "مثقال طلا (آبشده)", "طلا", "🏺", 20280000.0, 0.45, CurrencyCategory.GOLD),
        CurrencyItem("GOLD_OUNCE", "انس جهانی طلا ($)", "$", "🌐", 2780.0, 0.15, CurrencyCategory.GOLD),

        // Crypto
        CurrencyItem("USDT", "تتر (دلار دیجیتال)", "USDT", "🟢", 93600.0, 0.30, CurrencyCategory.CRYPTO),
        CurrencyItem("BTC", "بیت‌کوین (دلار)", "$", "₿", 96500.0, 1.20, CurrencyCategory.CRYPTO),
        CurrencyItem("ETH", "اتریوم (دلار)", "$", "⟠", 2750.0, 0.85, CurrencyCategory.CRYPTO)
    )

    /**
     * Fetches live rates directly from https://www.navasan.net/ and updates cache
     */
    suspend fun fetchLiveRatesFromNavasan(): Result<List<CurrencyItem>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(NAVASAN_URL)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Mobile Safari/537.36")
                .header("Accept-Language", "fa,en;q=0.9")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }

            val html = response.body?.string().orEmpty()
            if (html.isEmpty()) {
                return@withContext Result.failure(Exception("پاسخی از سرور دریافت نشد"))
            }

            // Parse rows from navasan.net
            val rowRegex = Regex("""<tr[^>]*data-code="([^"]+)"[^>]*>([\s\S]*?)</tr>""", RegexOption.IGNORE_CASE)
            val priceRegex = Regex("""class="price"[^>]*>([\d\s.,]+)</td>""", RegexOption.IGNORE_CASE)
            val changeRegex = Regex("""class="change"[^>]*>([-\d\s.,]+)</td>""", RegexOption.IGNORE_CASE)
            val timeRegex = Regex("""class="time"[^>]*>([^<]+)</td>""", RegexOption.IGNORE_CASE)

            var parsedCount = 0
            var latestTime = ""

            for (match in rowRegex.findAll(html)) {
                val dataCode = match.groupValues[1].lowercase().trim()
                val innerContent = match.groupValues[2]

                val rawPriceStr = priceRegex.find(innerContent)?.groupValues?.get(1)?.replace(",", "")?.replace(" ", "")?.trim()
                val rawChangeStr = changeRegex.find(innerContent)?.groupValues?.get(1)?.replace(",", "")?.replace(" ", "")?.trim()
                val timeStr = timeRegex.find(innerContent)?.groupValues?.get(1)?.trim().orEmpty()

                if (timeStr.isNotEmpty()) latestTime = timeStr
                val parsedPrice = rawPriceStr?.toDoubleOrNull() ?: continue
                val parsedChange = rawChangeStr?.toDoubleOrNull() ?: 0.0

                // Map data-code to our CurrencyItem code
                val mappedCode = when (dataCode) {
                    "usd" -> "USD"
                    "eur" -> "EUR"
                    "aed", "aed_note" -> "AED"
                    "gbp" -> "GBP"
                    "try" -> "TRY"
                    "cny" -> "CNY"
                    "cad" -> "CAD"
                    "iqd" -> "IQD"
                    "omr" -> "OMR"
                    "aud" -> "AUD"
                    "kwd" -> "KWD"
                    "chf" -> "CHF"
                    "rub" -> "RUB"
                    "azn" -> "AZN"
                    "18ayar" -> "GOLD_18K"
                    "sekkeh" -> "COIN_EMAMI"
                    "bahar" -> "COIN_BAHAR"
                    "nim" -> "COIN_HALF"
                    "rob" -> "COIN_QUARTER"
                    "gerami" -> "COIN_GRAM"
                    "abshodeh" -> "GOLD_ABSHODEH"
                    "usd_xau" -> "GOLD_OUNCE"
                    "usd_usdt" -> "USDT"
                    "usd_btc" -> "BTC"
                    "usd_eth" -> "ETH"
                    else -> null
                }

                if (mappedCode != null) {
                    val item = defaultCurrencies.find { it.code == mappedCode }
                    if (item != null) {
                        // Adjust pricing if needed (navasan reports some items in Tomans, some in single units)
                        val finalPrice = when (mappedCode) {
                            "GOLD_18K" -> if (parsedPrice < 1000000) parsedPrice * 10 else parsedPrice
                            "GOLD_ABSHODEH" -> if (parsedPrice < 1000000) parsedPrice * 10 else parsedPrice
                            "COIN_EMAMI", "COIN_BAHAR", "COIN_HALF", "COIN_QUARTER", "COIN_GRAM" -> {
                                if (parsedPrice < 100000) parsedPrice * 1000 else parsedPrice
                            }
                            else -> parsedPrice
                        }

                        item.priceInToman = finalPrice
                        item.changePercent = parsedChange
                        item.lastUpdate = timeStr
                        parsedCount++
                    }
                }
            }

            if (parsedCount > 0) {
                isLiveFromNavasan = true
                lastFetchedTime = if (latestTime.isNotEmpty()) latestTime else "لحظه‌ای"
                Log.d(TAG, "Successfully parsed $parsedCount items from navasan.net")
                Result.success(defaultCurrencies.toList())
            } else {
                Result.failure(Exception("داده‌ای یافت نشد"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching from navasan.net", e)
            Result.failure(e)
        }
    }

    fun convertAmount(
        amount: Double,
        fromCode: String,
        toCode: String
    ): Double {
        if (fromCode == toCode) return amount
        
        // Value of 1 unit of fromCode in Toman
        val fromRateInToman = if (fromCode == "IRT") 1.0 else (defaultCurrencies.find { it.code == fromCode }?.priceInToman ?: 1.0)
        // Value of 1 unit of toCode in Toman
        val toRateInToman = if (toCode == "IRT") 1.0 else (defaultCurrencies.find { it.code == toCode }?.priceInToman ?: 1.0)

        val totalTomans = amount * fromRateInToman
        return totalTomans / toRateInToman
    }
}
