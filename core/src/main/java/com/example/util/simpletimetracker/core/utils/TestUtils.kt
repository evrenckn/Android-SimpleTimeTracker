package com.example.util.simpletimetracker.core.utils

import com.example.util.simpletimetracker.domain.interactor.PrefsInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordTypeInteractor
import com.example.util.simpletimetracker.domain.interactor.RunningRecordInteractor
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class TestUtils @Inject constructor(
    private val recordTypeInteractor: RecordTypeInteractor,
    private val recordInteractor: RecordInteractor,
    private val runningRecordInteractor: RunningRecordInteractor,
    private val prefsInteractor: PrefsInteractor
) {

    fun clearDatabase() = runBlocking {
        recordTypeInteractor.clear()
        recordInteractor.clear()
        runningRecordInteractor.clear()
    }

    fun clearPrefs() = runBlocking {
        prefsInteractor.clear()
    }
}