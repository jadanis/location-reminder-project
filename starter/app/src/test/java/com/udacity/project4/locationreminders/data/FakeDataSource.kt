package com.udacity.project4.locationreminders.data

import androidx.lifecycle.MutableLiveData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private var shouldReturnError: Boolean = false

//    Create a fake data source to act as a double to the real data source
    var reminderServiceData: LinkedHashMap<String,ReminderDTO> = LinkedHashMap()
    //private val observableReminders = MutableLiveData<List<ReminderDTO>>()

    constructor(list: List<ReminderDTO> = listOf()){
        for(r in list){
            reminderServiceData[r.id] = r
        }
    }

    fun setReturnError(value: Boolean){
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if(shouldReturnError){
            return Result.Error("Test exception")
        }
        return Result.Success(reminderServiceData.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderServiceData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if(shouldReturnError){
            return Result.Error("Test exception")
        }
        reminderServiceData[id]?.let{
            return Result.Success(it)
        }
        return Result.Error("Could not find reminder")
    }

    override suspend fun deleteAllReminders() {
        reminderServiceData.clear()
    }


}