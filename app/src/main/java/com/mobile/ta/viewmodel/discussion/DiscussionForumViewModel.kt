package com.mobile.ta.viewmodel.discussion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mobile.ta.data.DiscussionData
import com.mobile.ta.model.discussion.DiscussionForum
import com.mobile.ta.utils.now
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscussionForumViewModel @Inject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val DISCUSSION_FORUM = "DISCUSSION_FORUM"
    }

    private var _discussionForums: MutableLiveData<ArrayList<DiscussionForum>>
    val discussionForums: LiveData<ArrayList<DiscussionForum>>
        get() = _discussionForums

    init {
        _discussionForums = savedStateHandle.getLiveData(DISCUSSION_FORUM, arrayListOf())
    }

    fun createNewDiscussion(title: String, question: String) {
        val today = now()
        val discussionForum =
            DiscussionForum(today.toString(), title, question, today, "NEW", "user_id", "username")
        addDiscussionForum(discussionForum)
    }

    fun fetchDiscussionForums() {
        val discussionForumsData = DiscussionData.discussionForumsData
        setDiscussionForums(discussionForumsData)
    }

    private fun addDiscussionForum(discussionForums: DiscussionForum) {
        DiscussionData.addForum(discussionForums)
        fetchDiscussionForums()
    }

    private fun setDiscussionForums(discussionForums: ArrayList<DiscussionForum>) {
        _discussionForums.value = discussionForums
    }
}