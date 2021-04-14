package com.mobile.ta.repository

import com.google.firebase.firestore.DocumentReference
import com.mobile.ta.model.discussion.DiscussionForum
import com.mobile.ta.model.discussion.DiscussionForumAnswer
import com.mobile.ta.model.status.Status

interface DiscussionRepository {

    suspend fun getDiscussionForums(): Status<MutableList<DiscussionForum>>

    suspend fun getDiscussionForumById(id: String): Status<DiscussionForum>

    suspend fun getDiscussionForumAnswers(
        discussionId: String
    ): Status<MutableList<DiscussionForumAnswer>>

    suspend fun addDiscussionForum(data: HashMap<String, Any?>): Status<DocumentReference>

    suspend fun addDiscussionForumAnswer(
        discussionId: String,
        data: HashMap<String, Any>
    ): Status<DocumentReference>

    suspend fun markAsAcceptedAnswer(discussionId: String, answerId: String): Status<Void>
}