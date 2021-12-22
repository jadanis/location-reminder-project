package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataBase: RemindersDatabase

    @Before
    fun initDb(){
        dataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = dataBase.close()

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        //GIVEN
        val reminder = ReminderDTO(
            "title",
            "descriptiokn",
            "location",
            0.0,
            0.0
        )
        dataBase.reminderDao().saveReminder(reminder)
        //WHEN
        val loaded = dataBase.reminderDao().getReminderById(reminder.id)
        //THEN
        assertThat(loaded as ReminderDTO, notNullValue())
        assertThat(loaded,`is`(reminder))
    }

    @Test
    fun getAllReminders() = runBlockingTest {
        //GIVEN
        val reminder1 = ReminderDTO(
            "title1",
            "descriptiokn1",
            "location",
            0.0,
            0.0
        )
        dataBase.reminderDao().saveReminder(reminder1)
        val reminder2 = ReminderDTO(
            "title2",
            "descriptiokn2",
            "location",
            0.0,
            0.0
        )
        dataBase.reminderDao().saveReminder(reminder2)
        //WHEN
        val reminderList = dataBase.reminderDao().getReminders()
        //THEN
        assertThat(reminderList.size,`is`(2))
        assertThat(reminderList,`is`(listOf(reminder1,reminder2)))

    }

    @Test
    fun deleteAllReminders() = runBlockingTest {
        //GIVEN
        val reminder1 = ReminderDTO(
            "title1",
            "descriptiokn1",
            "location",
            0.0,
            0.0
        )
        dataBase.reminderDao().saveReminder(reminder1)
        val reminder2 = ReminderDTO(
            "title2",
            "descriptiokn2",
            "location",
            0.0,
            0.0
        )
        dataBase.reminderDao().saveReminder(reminder2)
        //WHEN
        dataBase.reminderDao().deleteAllReminders()
        val reminderList = dataBase.reminderDao().getReminders()
        //THEN
        assertThat(reminderList.size,`is`(0))

    }


//    Add testing implementation to the RemindersDao.kt

}