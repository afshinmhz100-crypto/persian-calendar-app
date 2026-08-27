package com.example.calendar.data.weather

import android.content.Context
import android.content.SharedPreferences
import com.example.calendar.core.PersianCalendarHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class WeatherRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val _selectedCity = MutableStateFlow(loadSavedCity())
    val selectedCity: StateFlow<City> = _selectedCity.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherData?>(loadCachedWeather())
    val weatherState: StateFlow<WeatherData?> = _weatherState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private fun loadSavedCity(): City {
        val cityName = prefs.getString("city_name", IranCitiesData.defaultCity.name) ?: IranCitiesData.defaultCity.name
        return IranCitiesData.findCityByName(cityName)
    }

    private fun loadCachedWeather(): WeatherData {
        val city = loadSavedCity()
        val temp = prefs.getFloat("cached_temp", 26.0f).toDouble()
        val feelsLike = prefs.getFloat("cached_feels_like", 26.0f).toDouble()
        val humidity = prefs.getInt("cached_humidity", 35)
        val wind = prefs.getFloat("cached_wind", 12.0f).toDouble()
        val code = prefs.getInt("cached_code", 0)
        val (condition, emoji) = WeatherCodeMapper.mapCode(code)
        val maxT = prefs.getFloat("cached_max", 30.0f).toDouble()
        val minT = prefs.getFloat("cached_min", 18.0f).toDouble()

        return WeatherData(
            cityName = city.name,
            provinceName = city.province,
            temperature = temp,
            feelsLike = feelsLike,
            humidity = humidity,
            windSpeed = wind,
            weatherCode = code,
            conditionText = condition,
            conditionEmoji = emoji,
            tempMax = maxT,
            tempMin = minT,
            dailyForecast = generateFallbackDailyForecast(temp, code),
            lastUpdated = prefs.getLong("cached_time", System.currentTimeMillis())
        )
    }

    suspend fun setCity(city: City) {
        _selectedCity.value = city
        prefs.edit()
            .putString("city_name", city.name)
            .putString("city_province", city.province)
            .apply()
        fetchWeather(city)
    }

    suspend fun refreshWeather() {
        fetchWeather(_selectedCity.value)
    }

    suspend fun fetchWeather(city: City = _selectedCity.value) = withContext(Dispatchers.IO) {
        _isLoading.value = true
        _errorMessage.value = null
        try {
            val url = "https://api.open-meteo.com/v1/forecast?" +
                    "latitude=${city.latitude}&longitude=${city.longitude}" +
                    "&current=temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m" +
                    "&daily=weather_code,temperature_2m_max,temperature_2m_min" +
                    "&timezone=Asia%2FTehran"

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonStr = response.body?.string()
                if (jsonStr != null) {
                    val root = JSONObject(jsonStr)
                    val current = root.getJSONObject("current")
                    val temp = current.getDouble("temperature_2m")
                    val feelsLike = current.optDouble("apparent_temperature", temp)
                    val humidity = current.optInt("relative_humidity_2m", 40)
                    val windSpeed = current.optDouble("wind_speed_10m", 10.0)
                    val weatherCode = current.optInt("weather_code", 0)

                    val (conditionText, conditionEmoji) = WeatherCodeMapper.mapCode(weatherCode)

                    val daily = root.optJSONObject("daily")
                    val dailyList = mutableListOf<DailyForecastItem>()
                    var maxTemp = temp + 4
                    var minTemp = temp - 6

                    if (daily != null) {
                        val timeArray = daily.optJSONArray("time")
                        val maxArray = daily.optJSONArray("temperature_2m_max")
                        val minArray = daily.optJSONArray("temperature_2m_min")
                        val codeArray = daily.optJSONArray("weather_code")

                        if (timeArray != null && maxArray != null && minArray != null && codeArray != null) {
                            val count = minOf(timeArray.length(), 7)
                            val todayPersian = PersianCalendarHelper.getTodayPersian()

                            for (i in 0 until count) {
                                val dMax = maxArray.optDouble(i, temp + 3)
                                val dMin = minArray.optDouble(i, temp - 5)
                                val dCode = codeArray.optInt(i, 0)
                                val (dCondition, dEmoji) = WeatherCodeMapper.mapCode(dCode)

                                if (i == 0) {
                                    maxTemp = dMax
                                    minTemp = dMin
                                }

                                // Calculate day name for forecast
                                val forecastDayPersian = when (i) {
                                    0 -> "امروز"
                                    1 -> "فردا"
                                    2 -> "پس‌فردا"
                                    else -> {
                                        val dayIdx = (PersianCalendarHelper.getDayOfWeekIndex(todayPersian) + i) % 7
                                        PersianCalendarHelper.WEEK_DAYS_PERSIAN[dayIdx]
                                    }
                                }

                                val dateStr = "${PersianCalendarHelper.toPersianDigits(todayPersian.day + i)} ${PersianCalendarHelper.getMonthName(todayPersian.month)}"

                                dailyList.add(
                                    DailyForecastItem(
                                        dayOfWeekName = forecastDayPersian,
                                        persianDateStr = dateStr,
                                        tempMax = dMax,
                                        tempMin = dMin,
                                        weatherCode = dCode,
                                        conditionText = dCondition,
                                        conditionEmoji = dEmoji
                                    )
                                )
                            }
                        }
                    }

                    if (dailyList.isEmpty()) {
                        dailyList.addAll(generateFallbackDailyForecast(temp, weatherCode))
                    }

                    val weatherData = WeatherData(
                        cityName = city.name,
                        provinceName = city.province,
                        temperature = temp,
                        feelsLike = feelsLike,
                        humidity = humidity,
                        windSpeed = windSpeed,
                        weatherCode = weatherCode,
                        conditionText = conditionText,
                        conditionEmoji = conditionEmoji,
                        tempMax = maxTemp,
                        tempMin = minTemp,
                        dailyForecast = dailyList,
                        lastUpdated = System.currentTimeMillis()
                    )

                    // Cache in prefs
                    prefs.edit()
                        .putFloat("cached_temp", temp.toFloat())
                        .putFloat("cached_feels_like", feelsLike.toFloat())
                        .putInt("cached_humidity", humidity)
                        .putFloat("cached_wind", windSpeed.toFloat())
                        .putInt("cached_code", weatherCode)
                        .putFloat("cached_max", maxTemp.toFloat())
                        .putFloat("cached_min", minTemp.toFloat())
                        .putLong("cached_time", System.currentTimeMillis())
                        .apply()

                    _weatherState.value = weatherData
                }
            } else {
                _errorMessage.value = "خطا در دریافت وضعیت هوا"
            }
        } catch (e: Exception) {
            _errorMessage.value = "عدم دسترسی به اینترنت یا سرور هواشناسی"
            if (_weatherState.value == null) {
                _weatherState.value = loadCachedWeather()
            }
        } finally {
            _isLoading.value = false
        }
    }

    private fun generateFallbackDailyForecast(baseTemp: Double, baseCode: Int): List<DailyForecastItem> {
        val list = mutableListOf<DailyForecastItem>()
        val todayPersian = PersianCalendarHelper.getTodayPersian()
        for (i in 0 until 5) {
            val dName = when (i) {
                0 -> "امروز"
                1 -> "فردا"
                2 -> "پس‌فردا"
                else -> {
                    val dayIdx = (PersianCalendarHelper.getDayOfWeekIndex(todayPersian) + i) % 7
                    PersianCalendarHelper.WEEK_DAYS_PERSIAN[dayIdx]
                }
            }
            val (cond, emoji) = WeatherCodeMapper.mapCode(baseCode)
            list.add(
                DailyForecastItem(
                    dayOfWeekName = dName,
                    persianDateStr = "${PersianCalendarHelper.toPersianDigits(todayPersian.day + i)} ${PersianCalendarHelper.getMonthName(todayPersian.month)}",
                    tempMax = baseTemp + 3,
                    tempMin = baseTemp - 5,
                    weatherCode = baseCode,
                    conditionText = cond,
                    conditionEmoji = emoji
                )
            )
        }
        return list
    }

    companion object {
        @Volatile
        private var INSTANCE: WeatherRepository? = null

        fun getInstance(context: Context): WeatherRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
