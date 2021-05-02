package com.mobile.ta.ui.profile

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.R
import com.mobile.ta.adapter.ProfileFeedbackAdapter
import com.mobile.ta.adapter.diff.ProfileFeedbackDiffCallback
import com.mobile.ta.databinding.FragmentProfileFeedbackTabBinding
import com.mobile.ta.ui.BaseFragment
import com.mobile.ta.viewmodel.profile.ProfileFeedbackTabViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFeedbackTabFragment :
    BaseFragment<FragmentProfileFeedbackTabBinding>(FragmentProfileFeedbackTabBinding::inflate) {

    private val viewModel: ProfileFeedbackTabViewModel by viewModels()

    private val feedbackAdapter by lazy {
        ProfileFeedbackAdapter(ProfileFeedbackDiffCallback())
    }

    override fun runOnCreateView() {
        binding.profileTabRecyclerView.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = feedbackAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                ).apply {
                    setDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.drawable_space_item_decoration,
                            null
                        )!!
                    )
                })
        }
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.feedbacks.observe(viewLifecycleOwner, {
            feedbackAdapter.submitList(it)
            showEmptyState(it.isEmpty())
        })
    }

    private fun showEmptyState(show: Boolean) {
        binding.apply {
            profileTabNoData.visibility = if (show) View.VISIBLE else View.GONE
            profileTabRecyclerView.visibility = if (show.not()) View.VISIBLE else View.GONE
        }
    }
}