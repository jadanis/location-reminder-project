package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var localDataSource: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Unconfined
            )
    }

    @Before
    fun registerIdlingResource(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun insertAndGetById() = runBlocking {
        // GIVEN
        val reminder = ReminderDTO(
            "title",
            "description",
            "location",
            0.0,
            0.0
        )
        localDataSource.saveReminder(reminder)

        // WHEN
        val result = localDataSource.getReminder(reminder.id) as Result.Success

        // THEN
        assertThat(result.data, `is`(reminder))
    }

    //Per Submission feedback
    @Test
    fun getByIdError() = runBlocking {
        //GIVEN
        val reminder = ReminderDTO(
            "title",
            "description",
            "location",
            0.0,
            0.0
        )
        //WHEN
        val result = localDataSource.getReminder(reminder.id) as Result.Error
        //THEN
        assertThat(result.message,`is`("Reminder not found!"))

    }

    //TODO: fix error: IllegalStateException This Job has not completed
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
        localDataSource.saveReminder(reminder1)
        val reminder2 = ReminderDTO(
            "title2",
            "descriptiokn2",
            "location",
            0.0,
            0.0
        )
        localDataSource.saveReminder(reminder2)
        //WHEN
        val result = localDataSource.getReminders() as Result.Success
        //THEN
        assertThat(result.data.size,`is`(2))
        assertThat(result.data,`is`(listOf(reminder1,reminder2)))

    }

    //TODO: fix error, same as above
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
        localDataSource.saveReminder(reminder1)
        val reminder2 = ReminderDTO(
            "title2",
            "descriptiokn2",
            "location",
            0.0,
            0.0
        )
        localDataSource.saveReminder(reminder2)
        //WHEN
        localDataSource.deleteAllReminders()
        val result = localDataSource.getReminders() as Result.Success
        //THEN
        assertThat(result.data.size,`is`(0))

    }

//   Add testing implementation to the RemindersLocalRepository.kt

}