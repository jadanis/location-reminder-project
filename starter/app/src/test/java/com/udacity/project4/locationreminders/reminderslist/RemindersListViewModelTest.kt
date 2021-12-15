package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

//    @Before
//    fun setupViewModel(){
//        dataSource = FakeDataSource()
//        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),dataSource)
//    }

    @After
    fun cleanUp(){
        stopKoin()
    }

    @Test
    fun loadReminders_NotEmpty(){
        mainCoroutineRule.pauseDispatcher()
        //GIVEN
        val reminder1 = ReminderDataItem("title1","desc","location",0.0,0.0)
        val reminder2 = ReminderDataItem("title2","desc","location",0.0,0.0)
        val reminderList = listOf(
            ReminderDTO(reminder1.title,reminder1.description,reminder1.location,reminder1.latitude,reminder1.longitude,reminder1.id),
            ReminderDTO(reminder2.title,reminder2.description,reminder2.location,reminder2.latitude,reminder2.longitude,reminder2.id)
        )
        dataSource = FakeDataSource(reminderList)
        viewModel= RemindersListViewModel(ApplicationProvider.getApplicationContext(),dataSource)
        //WHEN
        viewModel.loadReminders()

        //THEN
        assertThat(viewModel.showLoading.value,`is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.remindersList.value,`is`(listOf(reminder1,reminder2)))
        assertThat(viewModel.showLoading.value,`is`(false))
        assertThat(viewModel.showNoData.value,`is`(false))

    }

    @Test
    fun loadReminders_Empty(){
        //GIVEN
        dataSource = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),dataSource)
        //WHEN
        viewModel.loadReminders()
        //THEN
        assertThat(viewModel.remindersList.value,`is`(listOf()))
        assertThat(viewModel.showNoData.value,`is`(true))
    }

    @Test
    fun loadReminders_Error(){
        //GIVEN
        dataSource = FakeDataSource()
        dataSource.setReturnError(true)
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),dataSource)
        //WHEN
        viewModel.loadReminders()
        //THEN
        assertThat(viewModel.showSnackBar.value,`is`("Test exception"))
    }

    //provide testing to the RemindersListViewModel and its live data objects

}