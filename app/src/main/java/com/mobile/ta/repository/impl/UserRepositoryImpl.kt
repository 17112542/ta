package com.mobile.ta.repository.impl

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.user.User
import com.mobile.ta.model.user.course.chapter.assignment.UserAssignmentAnswer
import com.mobile.ta.model.user.course.chapter.assignment.UserSubmittedAssignment
import com.mobile.ta.model.user.course.chapter.assignment.mapToFirebaseData
import com.mobile.ta.model.user.feedback.Feedback
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.utils.exists
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.fetchDataWithResult
import com.mobile.ta.utils.mapper.UserMapper
import com.mobile.ta.utils.mapper.UserSubmittedAssignmentMapper
import com.mobile.ta.utils.wrapper.status.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    database: FirebaseFirestore,
    storage: FirebaseStorage
) : UserRepository {

    private val userCollection by lazy {
        database.collection(CollectionConstants.USER_COLLECTION)
    }

    private val storageReference by lazy {
        storage.reference
    }

    override suspend fun submitQuestionResult(
        userId: String,
        userAssignmentAnswer: UserAssignmentAnswer,
        courseId: String,
        chapterId: String
    ): Status<Boolean> {
        return userCollection.document(userId)
            .collection(CollectionConstants.COURSE_COLLECTION).document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .collection(CollectionConstants.QUESTION_COLLECTION).document(userAssignmentAnswer.id)
            .set(userAssignmentAnswer)
            .fetchData()
    }

    override suspend fun updateCorrectAnswerCount(
        userId: String,
        userSubmittedAssignment: UserSubmittedAssignment,
        courseId: String,
        chapterId: String
    ): Status<Boolean> {
        return userCollection.document(userId)
            .collection(CollectionConstants.COURSE_COLLECTION).document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .update(userSubmittedAssignment.mapToFirebaseData())
            .fetchData()
    }

    override suspend fun resetSubmittedChapter(
        userId: String,
        courseId: String,
        chapterId: String
    ): Status<Boolean> {
        return userCollection.document(userId)
            .collection(CollectionConstants.COURSE_COLLECTION).document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .delete()
            .fetchData()
    }

    override suspend fun getSubmittedChapter(
        userId: String,
        courseId: String,
        chapterId: String
    ): Status<UserSubmittedAssignment> {
        return userCollection.document(userId)
            .collection(CollectionConstants.COURSE_COLLECTION).document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .fetchData(UserSubmittedAssignmentMapper::mapToUserSubmittedAssignment)
    }

    override suspend fun getIfSubmittedBefore(
        userId: String,
        courseId: String,
        chapterId: String
    ): Status<Boolean> {
        return userCollection.document(userId)
            .collection(CollectionConstants.COURSE_COLLECTION).document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .exists()
    }

    override suspend fun createNewSubmittedAssignment(
        userId: String,
        courseId: String,
        chapterId: String
    ): Status<Boolean> {
        return userCollection.document(userId)
            .collection(CollectionConstants.COURSE_COLLECTION).document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .set(
                mapOf(
                    "exists" to true
                )
            )
            .fetchData()
    }

    override suspend fun addUserFeedback(id: String, data: HashMap<String, Any?>): Status<Boolean> {
        return userCollection.document(id)
            .collection(CollectionConstants.FEEDBACK_COLLECTION)
            .add(data)
            .fetchData()
    }

    override suspend fun getUser(): Status<User> {
        return auth.currentUser?.let {
            userCollection.document(it.uid).fetchData(UserMapper::mapToUser)
        } ?: Status.error(null, null)
    }

    override suspend fun getUserFeedbacks(id: String): Status<MutableList<Feedback>> {
        return userCollection.document(id)
            .collection(CollectionConstants.FEEDBACK_COLLECTION)
            .orderBy(UserMapper.CREATED_AT, Query.Direction.DESCENDING)
            .fetchData(UserMapper::mapToUserFeedbacks)
    }

    override suspend fun getUserImageUrl(userId: String, imageUri: Uri): Status<Uri> {
        return storageReference
            .child("${CollectionConstants.IMAGES_USERS_STORAGE_PATH}/$userId/${imageUri.lastPathSegment}")
            .downloadUrl.fetchDataWithResult()
    }

    override suspend fun updateUser(user: User): Status<Boolean> {
        return userCollection.document(user.id).update(
            mapOf(
                UserMapper.NAME to user.name,
                UserMapper.BIRTH_DATE to user.birthDate,
                UserMapper.PHOTO to user.photo,
                UserMapper.PHONE_NUMBER to user.phoneNumber,
                UserMapper.BIO to user.bio
            )
        ).fetchData()
    }

    override suspend fun uploadUserImage(userId: String, imageUri: Uri): Status<Boolean> {
        return storageReference
            .child("${CollectionConstants.IMAGES_USERS_STORAGE_PATH}/$userId/${imageUri.lastPathSegment}")
            .putFile(imageUri).fetchData()
    }
}