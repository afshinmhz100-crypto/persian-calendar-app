package com.example.calendar.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendar.backup.BackupManager
import com.example.calendar.backup.BackupRestoreResult
import com.example.calendar.core.BirthdayAgeInfo
import com.example.calendar.core.CalendarDayModel
import com.example.calendar.core.GregorianDate
import com.example.calendar.core.HijriDate
import com.example.calendar.core.PersianCalendarHelper
import com.example.calendar.core.PersianDate
import com.example.calendar.data.AppDatabase
import com.example.calendar.data.AppThemeStyle
import com.example.calendar.data.CalendarEventEntity
import com.example.calendar.data.EventRepository
import com.example.calendar.data.PreferencesManager
import com.example.calendar.data.UserSettings
import com.example.calendar.data.weather.City
import com.example.calendar.data.weather.WeatherData
import com.example.calendar.data.weather.WeatherRepository
import com.example.calendar.fortune.FaalHafezRepository
import com.example.calendar.fortune.FaalItem
import com.example.calendar.news.NewsRepository
import com.example.calendar.news.NewsState
import com.example.calendar.notifications.ReminderNotificationManager
import com.example.calendar.widget.PersianCalendarWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val route: String) {
    CALENDAR("تقویم", "calendar"),
    FORTUNE("فال روزانه", "fortune"),
    CONVERTER("ابزارها", "tools"),
    NEWS("اخبار زنده", "news"),
    SETTINGS("تنظیمات", "settings")
}

data class DateConverterState(
    val persianYear: Int = 1404,
    val persianMonth: Int = 1,
    val persianDay: Int = 1,
    val convertedGregorian: GregorianDate? = null,
    val convertedHijri: HijriDate? = null,
    val gYear: Int = 2025,
    val gMonth: Int = 3,
    val gDay: Int = 21,
    val convertedPersian: PersianDate? = null,
    val calculatedDayOfWeek: String = "",
    val isLeapYear: Boolean = false,
    val daysPassedInYear: Int = 0,
    val daysRemainingInYear: Int = 0,
    val yearAnimalName: String = "",
    val yearAnimalEmoji: String = "",
    val birthdayInfo: BirthdayAgeInfo? = null
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val eventRepository = EventRepository(db.eventDao())
    val preferencesManager = PreferencesManager(application)
    val newsRepository = NewsRepository(application)
    val backupManager = BackupManager(application, eventRepository, preferencesManager)
    val notificationManager = ReminderNotificationManager(application)
    val weatherRepository = WeatherRepository.getInstance(application)

    val userSettings: StateFlow<UserSettings> = preferencesManager.settingsFlow

    // Weather States
    val selectedCity: StateFlow<City> = weatherRepository.selectedCity
    val weatherState: StateFlow<WeatherData?> = weatherRepository.weatherState
    val isWeatherLoading: StateFlow<Boolean> = weatherRepository.isLoading
    val weatherErrorMessage: StateFlow<String?> = weatherRepository.errorMessage

    // Current selected tab
    private val _selectedTab = MutableStateFlow(MainTab.CALENDAR)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    // Calendar Navigation State
    private val today = PersianCalendarHelper.getTodayPersian()
    private val _currentYear = MutableStateFlow(today.year)
    val currentYear: StateFlow<Int> = _currentYear.asStateFlow()

    private val _currentMonth = MutableStateFlow(today.month)
    val currentMonth: StateFlow<Int> = _currentMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow(today)
    val selectedDate: StateFlow<PersianDate> = _selectedDate.asStateFlow()

    // All events flow
    val allEvents: StateFlow<List<CalendarEventEntity>> = eventRepository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Month Grid Data derived from current year/month and events
    val monthDays: StateFlow<List<CalendarDayModel>> = combine(
        _currentYear,
        _currentMonth,
        allEvents
    ) { year, month, events ->
        generateMonthDays(year, month, events)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected day events
    val selectedDayEvents: StateFlow<List<CalendarEventEntity>> = combine(
        _selectedDate,
        allEvents
    ) { date, events ->
        events.filter { it.persianYear == date.year && it.persianMonth == date.month && it.persianDay == date.day }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Fortune / Faal State
    private val _hasTakenFaal = MutableStateFlow(false)
    val hasTakenFaal: StateFlow<Boolean> = _hasTakenFaal.asStateFlow()

    private val _currentFaal = MutableStateFlow(FaalHafezRepository.getDailyFortune())
    val currentFaal: StateFlow<FaalItem> = _currentFaal.asStateFlow()

    private val _isShufflingFaal = MutableStateFlow(false)
    val isShufflingFaal: StateFlow<Boolean> = _isShufflingFaal.asStateFlow()

    // Date Converter State
    private val _converterState = MutableStateFlow(
        DateConverterState(
            persianYear = today.year,
            persianMonth = today.month,
            persianDay = today.day,
            convertedGregorian = PersianCalendarHelper.persianToGregorian(today.year, today.month, today.day),
            gYear = PersianCalendarHelper.getTodayGregorian().year,
            gMonth = PersianCalendarHelper.getTodayGregorian().month,
            gDay = PersianCalendarHelper.getTodayGregorian().day,
            convertedPersian = today,
            calculatedDayOfWeek = PersianCalendarHelper.getDayOfWeekName(today),
            isLeapYear = today.isLeapYear
        )
    )
    val converterState: StateFlow<DateConverterState> = _converterState.asStateFlow()

    // News State
    val newsState: StateFlow<NewsState> = newsRepository.newsState

    // Support / ZarinPal Modal
    private val _showZarinPalModal = MutableStateFlow(false)
    val showZarinPalModal: StateFlow<Boolean> = _showZarinPalModal.asStateFlow()

    // Backup & Restore Status Message
    private val _backupStatus = MutableStateFlow<String?>(null)
    val backupStatus: StateFlow<String?> = _backupStatus.asStateFlow()

    init {
        // Initial news load
        viewModelScope.launch {
            newsRepository.refreshNews(force = false)
        }
        // Initial weather load
        viewModelScope.launch {
            weatherRepository.refreshWeather()
        }
        recalculateConverterState()
    }

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
    }

    fun nextMonth() {
        if (_currentMonth.value == 12) {
            _currentYear.value += 1
            _currentMonth.value = 1
        } else {
            _currentMonth.value += 1
        }
    }

    fun prevMonth() {
        if (_currentMonth.value == 1) {
            _currentYear.value -= 1
            _currentMonth.value = 12
        } else {
            _currentMonth.value -= 1
        }
    }

    fun jumpToToday() {
        val today = PersianCalendarHelper.getTodayPersian()
        _currentYear.value = today.year
        _currentMonth.value = today.month
        _selectedDate.value = today
    }

    fun selectDate(date: PersianDate) {
        _selectedDate.value = date
        _currentYear.value = date.year
        _currentMonth.value = date.month
    }

    private fun generateMonthDays(year: Int, month: Int, events: List<CalendarEventEntity>): List<CalendarDayModel> {
        val daysInMonth = PersianCalendarHelper.getDaysInPersianMonth(year, month)
        val firstDayOfMonth = PersianDate(year, month, 1)
        val firstDayOfWeekIndex = PersianCalendarHelper.getDayOfWeekIndex(firstDayOfMonth) // 0 = شنبه

        val list = mutableListOf<CalendarDayModel>()
        val today = PersianCalendarHelper.getTodayPersian()

        // Days from previous month to fill the first row
        if (firstDayOfWeekIndex > 0) {
            val prevMonth = if (month == 1) 12 else month - 1
            val prevYear = if (month == 1) year - 1 else year
            val prevMonthDays = PersianCalendarHelper.getDaysInPersianMonth(prevYear, prevMonth)
            val startDay = prevMonthDays - firstDayOfWeekIndex + 1
            for (d in startDay..prevMonthDays) {
                val pDate = PersianDate(prevYear, prevMonth, d)
                val gDate = PersianCalendarHelper.persianToGregorian(prevYear, prevMonth, d)
                val hDate = PersianCalendarHelper.persianToHijri(prevYear, prevMonth, d)
                val dayOfWeekIdx = PersianCalendarHelper.getDayOfWeekIndex(pDate)
                list.add(
                    CalendarDayModel(
                        persianDate = pDate,
                        gregorianDate = gDate,
                        hijriDate = hDate,
                        dayOfWeekName = PersianCalendarHelper.WEEK_DAYS_PERSIAN[dayOfWeekIdx],
                        dayOfWeekIndex = dayOfWeekIdx,
                        isCurrentMonth = false,
                        isToday = (pDate == today),
                        occasions = PersianCalendarHelper.getOccasionsForPersianDate(prevYear, prevMonth, d),
                        eventCount = events.count { it.persianYear == prevYear && it.persianMonth == prevMonth && it.persianDay == d }
                    )
                )
            }
        }

        // Days in current month
        for (d in 1..daysInMonth) {
            val pDate = PersianDate(year, month, d)
            val gDate = PersianCalendarHelper.persianToGregorian(year, month, d)
            val hDate = PersianCalendarHelper.persianToHijri(year, month, d)
            val dayOfWeekIdx = PersianCalendarHelper.getDayOfWeekIndex(pDate)
            list.add(
                CalendarDayModel(
                    persianDate = pDate,
                    gregorianDate = gDate,
                    hijriDate = hDate,
                    dayOfWeekName = PersianCalendarHelper.WEEK_DAYS_PERSIAN[dayOfWeekIdx],
                    dayOfWeekIndex = dayOfWeekIdx,
                    isCurrentMonth = true,
                    isToday = (pDate == today),
                    occasions = PersianCalendarHelper.getOccasionsForPersianDate(year, month, d),
                    eventCount = events.count { it.persianYear == year && it.persianMonth == month && it.persianDay == d }
                )
            )
        }

        // Fill trailing row to complete full grid (multiple of 7)
        val remaining = 7 - (list.size % 7)
        if (remaining in 1..6) {
            val nextMonth = if (month == 12) 1 else month + 1
            val nextYear = if (month == 12) year + 1 else year
            for (d in 1..remaining) {
                val pDate = PersianDate(nextYear, nextMonth, d)
                val gDate = PersianCalendarHelper.persianToGregorian(nextYear, nextMonth, d)
                val hDate = PersianCalendarHelper.persianToHijri(nextYear, nextMonth, d)
                val dayOfWeekIdx = PersianCalendarHelper.getDayOfWeekIndex(pDate)
                list.add(
                    CalendarDayModel(
                        persianDate = pDate,
                        gregorianDate = gDate,
                        hijriDate = hDate,
                        dayOfWeekName = PersianCalendarHelper.WEEK_DAYS_PERSIAN[dayOfWeekIdx],
                        dayOfWeekIndex = dayOfWeekIdx,
                        isCurrentMonth = false,
                        isToday = (pDate == today),
                        occasions = PersianCalendarHelper.getOccasionsForPersianDate(nextYear, nextMonth, d),
                        eventCount = events.count { it.persianYear == nextYear && it.persianMonth == nextMonth && it.persianDay == d }
                    )
                )
            }
        }

        return list
    }

    // Fortune Actions
    fun takeNewDivination() {
        _isShufflingFaal.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(650)
            _currentFaal.value = FaalHafezRepository.getRandomFortune(excludeId = _currentFaal.value.id)
            _hasTakenFaal.value = true
            _isShufflingFaal.value = false
        }
    }

    // Weather Actions
    fun setWeatherCity(city: City) {
        viewModelScope.launch {
            weatherRepository.setCity(city)
            PersianCalendarWidgetProvider.sendUpdateBroadcast(getApplication())
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            weatherRepository.refreshWeather()
            PersianCalendarWidgetProvider.sendUpdateBroadcast(getApplication())
        }
    }

    // Event Management Actions
    fun addEvent(
        title: String,
        description: String,
        persianDate: PersianDate,
        category: String,
        timeHour: Int = -1,
        timeMinute: Int = -1,
        isReminderEnabled: Boolean = false,
        priority: String = "متوسط",
        colorTag: String = "#0A9396"
    ) {
        viewModelScope.launch {
            val entity = CalendarEventEntity(
                title = title,
                description = description,
                persianYear = persianDate.year,
                persianMonth = persianDate.month,
                persianDay = persianDate.day,
                category = category,
                timeHour = timeHour,
                timeMinute = timeMinute,
                isReminderEnabled = isReminderEnabled,
                priority = priority,
                colorTag = colorTag
            )
            val id = eventRepository.insertEvent(entity)
            if (isReminderEnabled) {
                notificationManager.showEventNotification(entity.copy(id = id))
            }
        }
    }

    fun deleteEvent(event: CalendarEventEntity) {
        viewModelScope.launch {
            eventRepository.deleteEvent(event)
        }
    }

    fun toggleEventCompletion(event: CalendarEventEntity) {
        viewModelScope.launch {
            eventRepository.updateEvent(event.copy(isCompleted = !event.isCompleted))
        }
    }

    // Date Converter Actions
    fun updateConverterPersianDate(year: Int, month: Int, day: Int) {
        val maxDays = PersianCalendarHelper.getDaysInPersianMonth(year, month)
        val validDay = day.coerceIn(1, maxDays)
        val gDate = PersianCalendarHelper.persianToGregorian(year, month, validDay)
        val hDate = PersianCalendarHelper.persianToHijri(year, month, validDay)
        val pDate = PersianDate(year, month, validDay)
        val (passed, remaining) = PersianCalendarHelper.getYearProgressInfo(pDate)
        val (animalName, animalEmoji) = PersianCalendarHelper.getYearAnimal(year)
        val bdayInfo = PersianCalendarHelper.calculateAgeAndCandle(year, month, validDay)

        _converterState.value = _converterState.value.copy(
            persianYear = year,
            persianMonth = month,
            persianDay = validDay,
            convertedGregorian = gDate,
            convertedHijri = hDate,
            calculatedDayOfWeek = PersianCalendarHelper.getDayOfWeekName(pDate),
            isLeapYear = PersianCalendarHelper.isPersianLeapYear(year),
            daysPassedInYear = passed,
            daysRemainingInYear = remaining,
            yearAnimalName = animalName,
            yearAnimalEmoji = animalEmoji,
            birthdayInfo = bdayInfo
        )
    }

    fun updateConverterGregorianDate(gYear: Int, gMonth: Int, gDay: Int) {
        val pDate = PersianCalendarHelper.gregorianToPersian(gYear, gMonth, gDay)
        val hDate = PersianCalendarHelper.gregorianToHijri(gYear, gMonth, gDay)
        val (passed, remaining) = PersianCalendarHelper.getYearProgressInfo(pDate)
        val (animalName, animalEmoji) = PersianCalendarHelper.getYearAnimal(pDate.year)
        val bdayInfo = PersianCalendarHelper.calculateAgeAndCandle(pDate.year, pDate.month, pDate.day)

        _converterState.value = _converterState.value.copy(
            gYear = gYear,
            gMonth = gMonth,
            gDay = gDay,
            convertedPersian = pDate,
            convertedHijri = hDate,
            calculatedDayOfWeek = PersianCalendarHelper.getDayOfWeekName(pDate),
            isLeapYear = PersianCalendarHelper.isPersianLeapYear(pDate.year),
            daysPassedInYear = passed,
            daysRemainingInYear = remaining,
            yearAnimalName = animalName,
            yearAnimalEmoji = animalEmoji,
            birthdayInfo = bdayInfo
        )
    }

    private fun recalculateConverterState() {
        val state = _converterState.value
        updateConverterPersianDate(state.persianYear, state.persianMonth, state.persianDay)
    }

    // News Actions
    fun refreshNews() {
        viewModelScope.launch {
            newsRepository.refreshNews(force = true)
        }
    }

    // Settings & Theme
    fun setThemeMode(mode: com.example.calendar.data.ThemeMode) {
        preferencesManager.updateThemeMode(mode)
    }

    fun setAppTheme(theme: AppThemeStyle) {
        preferencesManager.updateTheme(theme)
    }

    fun toggleDailyCalendarNotification(enable: Boolean) {
        preferencesManager.updateDailyCalendarNotification(enable)
        val notifManager = com.example.calendar.notifications.ReminderNotificationManager(getApplication())
        if (enable) {
            notifManager.showDailyCalendarNotification()
        } else {
            notifManager.cancelDailyCalendarNotification()
        }
    }

    fun toggleShowWeatherInNotification(show: Boolean) {
        preferencesManager.updateShowWeatherInNotification(show)
        if (userSettings.value.enableDailyCalendarNotification) {
            com.example.calendar.notifications.ReminderNotificationManager(getApplication()).showDailyCalendarNotification()
        }
    }

    fun toggleShowSubDatesInNotification(show: Boolean) {
        preferencesManager.updateShowSubDatesInNotification(show)
        if (userSettings.value.enableDailyCalendarNotification) {
            com.example.calendar.notifications.ReminderNotificationManager(getApplication()).showDailyCalendarNotification()
        }
    }

    fun toggleShowOccasionsInNotification(show: Boolean) {
        preferencesManager.updateShowOccasionsInNotification(show)
        if (userSettings.value.enableDailyCalendarNotification) {
            com.example.calendar.notifications.ReminderNotificationManager(getApplication()).showDailyCalendarNotification()
        }
    }

    fun refreshCalendarNotification() {
        val notifManager = com.example.calendar.notifications.ReminderNotificationManager(getApplication())
        notifManager.showDailyCalendarNotification()
    }

    fun toggleShowGregorianInGrid(show: Boolean) {
        preferencesManager.updateShowGregorianInGrid(show)
    }

    fun toggleBirthdayNotifications(enable: Boolean) {
        preferencesManager.updateBirthdayNotifications(enable)
    }

    fun toggleEventReminders(enable: Boolean) {
        preferencesManager.updateEventReminders(enable)
    }

    fun toggleHolidayNotifications(enable: Boolean) {
        preferencesManager.updateHolidayNotifications(enable)
    }

    fun toggleAutoUpdateNews(auto: Boolean) {
        preferencesManager.updateAutoUpdateNews(auto)
    }

    fun setFontSizeScale(scale: Float) {
        preferencesManager.updateFontSizeScale(scale)
    }

    // ZarinPal Modal
    fun setZarinPalModalVisible(visible: Boolean) {
        _showZarinPalModal.value = visible
    }

    // Backup & Restore
    fun restoreBackup(jsonString: String, onResult: (BackupRestoreResult) -> Unit) {
        viewModelScope.launch {
            val result = backupManager.restoreFromJson(jsonString, clearExisting = false)
            _backupStatus.value = result.message
            onResult(result)
        }
    }

    fun clearBackupStatus() {
        _backupStatus.value = null
    }
}
