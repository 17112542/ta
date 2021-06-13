package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.config.CollectionConstants.CHAPTER_COLLECTION
import com.mobile.ta.config.CollectionConstants.COURSE_COLLECTION
import com.mobile.ta.config.CollectionConstants.USER_COLLECTION
import com.mobile.ta.model.user.course.chapter.UserChapter
import com.mobile.ta.repository.UserChapterRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.UserChapterMapper
import com.mobile.ta.utils.mapper.UserChapterMapper.FINISHED_FIELD
import com.mobile.ta.utils.wrapper.status.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class UserChapterRepositoryImpl @Inject constructor(database: FirebaseFirestore) :
    UserChapterRepository {
    private val userCollection = database.collection(USER_COLLECTION)
    override suspend fun getFinishedUserChapters(
        userId: String,
        courseId: String
    ): Status<MutableList<UserChapter>> {
        return userCollection.document(userId).collection(COURSE_COLLECTION).document(courseId)
            .collection(
                CHAPTER_COLLECTION
            ).whereEqualTo(FINISHED_FIELD, true).fetchData(UserChapterMapper::mapToUserChapters)
    }

    override suspend fun addUserChapter(
        userId: String,
        courseId: String,
        chapterId: String,
        data: HashMap<String, Any>
    ): Status<Boolean> {
        return userCollection.document(userId).collection(COURSE_COLLECTION).document(courseId)
            .collection(
                CHAPTER_COLLECTION
            ).document(chapterId).set(data).fetchData()
    }
}