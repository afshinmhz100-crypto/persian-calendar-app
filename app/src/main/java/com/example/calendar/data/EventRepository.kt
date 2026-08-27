package com.example.calendar.data

import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao) {

    val allEvents: Flow<List<CalendarEventEntity>> = eventDao.getAllEvents()

    fun getEventsForDate(year: Int, month: Int, day: Int): Flow<List<CalendarEventEntity>> {
        return eventDao.getEventsForDate(year, month, day)
    }

    fun getEventsForMonth(year: Int, month: Int): Flow<List<CalendarEventEntity>> {
        return eventDao.getEventsForMonth(year, month)
    }

    fun getBirthdays(): Flow<List<CalendarEventEntity>> {
        return eventDao.getBirthdays()
    }

    fun searchEvents(query: String): Flow<List<CalendarEventEntity>> {
        return eventDao.searchEvents(query)
    }

    suspend fun insertEvent(event: CalendarEventEntity): Long {
        return eventDao.insertEvent(event)
    }

    suspend fun insertAll(events: List<CalendarEventEntity>) {
        eventDao.insertAll(events)
    }

    suspend fun updateEvent(event: CalendarEventEntity) {
        eventDao.updateEvent(event)
    }

    suspend fun deleteEvent(event: CalendarEventEntity) {
        eventDao.deleteEvent(event)
    }

    suspend fun deleteById(id: Long) {
        eventDao.deleteById(id)
    }

    suspend fun getAllEventsList(): List<CalendarEventEntity> {
        return eventDao.getAllEventsList()
    }

    suspend fun clearAll() {
        eventDao.clearAll()
    }
}
