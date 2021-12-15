package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var dataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    //main coroutine rule

    @Before
    fun setupViewModel(){
        dataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),dataSource)
    }

    @After
    fun cleanUp(){
        stopKoin()
    }

    @Test
    fun saveReminder_AddedToDataSourceAndLiveDataUpdated(){
        mainCoroutineRule.pauseDispatcher()
        //Given
        val reminder = ReminderDataItem("title","desc","location",0.0,0.0)

        //When
        viewModel.saveReminder(reminder)

        //then
        assertThat(viewModel.showLoading.getOrAwaitValue(),`is`(true))
        mainCoroutineRule.resumeDispatcher()

//        val result = dataSource.getReminder(reminder.id) as Result.Success
//        val reminder2 =
//            ReminderDataItem(
//                result.data.title,
//                result.data.description,
//                result.data.location,
//                result.data.latitude,
//                result.data.longitude,
//                result.data.id
//            )

        //assertThat(reminder2,`is`(reminder))
        assertThat(viewModel.showLoading.getOrAwaitValue(),`is`(false))
        assertThat(viewModel.showToast.getOrAwaitValue(),`is`("Reminder Saved !"))
        //assertThat(viewModel.navigationCommand.getOrAwaitValue(),`is`(NavigationCommand.Back))

    }

    @Test
    fun validateEnteredData_noTitle_returnsFalse(){
        //GIVEN
        val reminder = ReminderDataItem(null,"desc","location",0.0,0.0)
        //WHEN
        val result = viewModel.validateEnteredData(reminder)
        //THEN
        assertThat(result,`is`(false))
    }

    @Test
    fun validateEnteredData_noLocation_returnsFalse(){
        //GIVEN
        val reminder = ReminderDataItem("title","desc",null,0.0,0.0)
        //WHEN
        val result = viewModel.validateEnteredData(reminder)
        //THEN
        assertThat(result,`is`(false))
    }

    //provide testing to the SaveReminderView and its live data objects


}