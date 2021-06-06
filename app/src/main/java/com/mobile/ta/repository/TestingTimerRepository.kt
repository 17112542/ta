package com.mobile.ta.repository

import com.mobile.ta.model.testing.TimeSpent
import com.mobile.ta.utils.wrapper.status.Status
import java.sql.Timestamp

interface TestingTimerRepository {
    suspend fun saveTimeSpent(
        userId: String,
        courseId: String,
        chapterId: String,
        timeSpent: TimeSpent
    ): Status<Boolean>
}