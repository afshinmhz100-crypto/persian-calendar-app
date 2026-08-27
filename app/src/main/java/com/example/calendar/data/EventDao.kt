package com.example.calendar.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM calendar_events ORDER BY persianYear ASC, persianMonth ASC, persianDay ASC, timeHour ASC")
    fun getAllEvents(): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events WHERE persianYear = :year AND persianMonth = :month AND persianDay = :day ORDER BY timeHour ASC")
    fun getEventsForDate(year: Int, month: Int, day: Int): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events WHERE persianYear = :year AND persianMonth = :month AND persianDay = :day ORDER BY timeHour ASC")
    suspend fun getEventsForDateDirect(year: Int, month: Int, day: Int): List<CalendarEventEntity>

    @Query("SELECT * FROM calendar_events WHERE persianYear = :year AND persianMonth = :month")
    fun getEventsForMonth(year: Int, month: Int): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events WHERE category = 'تولد' ORDER BY persianMonth ASC, persianDay ASC")
    fun getBirthdays(): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchEvents(query: String): Flow<List<CalendarEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<CalendarEventEntity>)

    @Update
    suspend fun updateEvent(event: CalendarEventEntity)

    @Delete
    suspend fun deleteEvent(event: CalendarEventEntity)

    @Query("DELETE FROM calendar_events WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM calendar_events")
    suspend fun clearAll()

    @Query("SELECT * FROM calendar_events")
    suspend fun getAllEventsList(): List<CalendarEventEntity>
}
