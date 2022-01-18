package com.udacity.project4

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Rule
import androidx.test.espresso.action.Press

import androidx.test.espresso.action.CoordinatesProvider

import androidx.test.espresso.action.Tap

import androidx.test.espresso.action.GeneralClickAction

import androidx.test.espresso.ViewAction




//@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var activityRule = ActivityTestRule(RemindersActivity::class.java)

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Before
    fun registerIdlingResource(){
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource(){
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun saveReminder() = runBlockingTest {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //No data shown initially
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
        //Add reminder
        onView(withId(R.id.addReminderFAB)).perform(click())
        //Title is blank
        onView(withId(R.id.reminderTitle)).check(matches(withHint("Reminder Title")))
        onView(withId(R.id.reminderTitle)).check(matches(withText("")))
        //Save without title
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withId(R.id.snackbar_text)).check(matches(withText(R.string.err_enter_title)))
        Thread.sleep(3000)
        //Enter title save without location
        onView(withId(R.id.reminderTitle)).perform(typeText("Title 1"), closeSoftKeyboard())
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withId(R.id.snackbar_text)).check(matches(withText(R.string.err_select_location)))
        //Enter Description
        onView(withId(R.id.reminderDescription)).perform(typeText("Desc"), closeSoftKeyboard())
        //Select location
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.map)).perform(clickXY(50,50))
        onView(withId(R.id.save_button)).perform(click())
        //Check is populated
        onView(withId(R.id.selectedLocation)).check(matches(not(withText(""))))
        //Save
        onView(withId(R.id.saveReminder)).perform(click())
        //Check no data view is hidden
        Thread.sleep(3000)
        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))

        activityScenario.close()
    }

    // ViewAction to click on Map
    //solution from https://stackoverflow.com/questions/22177590/click-by-bounds-coordinates/22798043#22798043
    private fun clickXY(x: Int, y: Int): ViewAction? {
        return GeneralClickAction(
            Tap.SINGLE,
            CoordinatesProvider { view ->
                val screenPos = IntArray(2)
                view.getLocationOnScreen(screenPos)
                val screenX = (screenPos[0] + x).toFloat()
                val screenY = (screenPos[1] + y).toFloat()
                floatArrayOf(screenX, screenY)
            },
            Press.FINGER
        )
    }

//    add End to End testing to the app

}
