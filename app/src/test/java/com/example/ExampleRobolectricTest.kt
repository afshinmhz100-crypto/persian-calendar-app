package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.calendar.core.PersianCalendarHelper
import com.example.calendar.core.PersianDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("تقویم پارسی 1405", appName)
    }

    @Test
    fun `test persian date conversion`() {
        // 1 Farvardin 1404 -> 21 March 2025
        val gDate = PersianCalendarHelper.persianToGregorian(1404, 1, 1)
        assertEquals(2025, gDate.year)
        assertEquals(3, gDate.month)
        assertEquals(21, gDate.day)

        val pDate = PersianCalendarHelper.gregorianToPersian(2025, 3, 21)
        assertEquals(1404, pDate.year)
        assertEquals(1, pDate.month)
        assertEquals(1, pDate.day)
    }

    @Test
    fun `test persian holidays detection`() {
        val nowruzOccasions = PersianCalendarHelper.getOccasionsForPersianDate(1404, 1, 1)
        assertTrue(nowruzOccasions.any { it.isOfficialHoliday })
    }
}
