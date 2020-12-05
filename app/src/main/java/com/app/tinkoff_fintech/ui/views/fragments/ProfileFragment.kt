package com.app.tinkoff_fintech.ui.views.fragments

import android.app.Activity.RESULT_OK
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.recycler.adapters.ProfileAdapter
import com.app.tinkoff_fintech.paging.wall.WallListViewModel
import com.app.tinkoff_fintech.ui.contracts.ProfileContractInterface
import com.app.tinkoff_fintech.ui.presenters.ProfilePresenter
import com.app.tinkoff_fintech.ui.views.activities.DetailActivity
import com.app.tinkoff_fintech.ui.views.activities.DetailActivity.Companion.ARG_OWNER_ID
import com.app.tinkoff_fintech.ui.views.activities.DetailActivity.Companion.ARG_POST_ID
import com.app.tinkoff_fintech.ui.views.activities.DetailActivity.Companion.FROM_ACTIVITY
import com.app.tinkoff_fintech.ui.views.activities.DetailActivity.Companion.FROM_PROFILE
import com.app.tinkoff_fintech.ui.views.activities.NewPostActivity
import com.app.tinkoff_fintech.ui.views.activities.NewPostActivity.Companion.OWNER_NAME
import com.app.tinkoff_fintech.ui.views.activities.NewPostActivity.Companion.OWNER_PHOTO
import com.app.tinkoff_fintech.ui.views.activities.NewPostActivity.Companion.PICK_PHOTO
import com.app.tinkoff_fintech.utils.State
import com.app.tinkoff_fintech.network.models.news.ProfileInformation
import kotlinx.android.synthetic.main.fragment_profile.progressBar
import kotlinx.android.synthetic.main.fragment_profile.recyclerView
import javax.inject.Inject

class ProfileFragment : Fragment(),
    ProfileContractInterface.View {

    companion object {
        private const val REQUEST_CODE = 42
    }

    private val viewModel: WallListViewModel by activityViewModels()

    @Inject
    lateinit var presenter: ProfilePresenter

    @Inject
    lateinit var profileAdapter: ProfileAdapter

    private var ownerId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity?.applicationContext as App).addProfileComponent(this)
        (activity?.applicationContext as App).profileComponent?.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        (activity?.applicationContext as App).clearProfileComponent()
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.attachView(this)
    }

    override fun init() {
        if (!viewModel.isInitialized()) {
            (activity?.application as App).profileComponent?.inject(viewModel)
            viewModel.init()
        }
        initObservers()
        initAdapter()
        presenter.getProfileInformation()
    }

    private fun initAdapter() {
        val dividerItemDecoration = DividerItemDecoration(activity, RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.divider_post_space,
                null
            )!!
        )

        with(profileAdapter) {
            postClickListener = { id -> startDetailActivity(id) }
            retry = {
                viewModel.retry()
                presenter.getProfileInformation()
            }
            newPostClickListener = { ownerPhoto, ownerName, pickPhoto ->
                startNewPostActivity(
                    ownerPhoto,
                    ownerName,
                    pickPhoto
                )
            }
            changeLikesListener = { postId, postOwnerId, isLikes -> changeLike(postId, postOwnerId, isLikes) }
        }

        with(recyclerView) {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = profileAdapter
            addItemDecoration(dividerItemDecoration)
        }
    }


    private fun initObservers() {
        viewModel.getState().observe(requireActivity(), Observer { state ->
            progressBar.visibility =
                if (viewModel.listIsEmpty() && state == State.LOADING) View.VISIBLE else View.GONE
            if (!viewModel.listIsEmpty() || state == State.ERROR)
                profileAdapter.setStateAdapter(state ?: State.DONE)
        })

        viewModel.newsList.observe(requireActivity(), Observer<PagedList<Post>> {
            profileAdapter.submitList(it)
        })
    }

    override fun updateProfileInformation(profileInformation: ProfileInformation) {
        ownerId = profileInformation.id
        profileAdapter.profileInformation = profileInformation
        profileAdapter.notifyItemChanged(0)
        profileAdapter.notifyItemChanged(1)
    }

    private fun startNewPostActivity(ownerPhoto: String, ownerName: String, pickPhoto: Boolean) {
        val intent = Intent(requireActivity(), NewPostActivity::class.java).apply {
            putExtra(OWNER_PHOTO, ownerPhoto)
            putExtra(OWNER_NAME, ownerName)
            putExtra(PICK_PHOTO, pickPhoto)
        }
        val bundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
        startActivityForResult(intent, 42, bundle)
    }

    private fun startDetailActivity(postId: Int) {
        val intent = Intent(activity, DetailActivity::class.java).apply {
            putExtra(FROM_ACTIVITY, FROM_PROFILE)
            putExtra(ARG_OWNER_ID, ownerId)
            putExtra(ARG_POST_ID, postId)
        }
        val bundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
        startActivity(intent, bundle)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
            viewModel.invalidate()
    }

    private fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean) {
        presenter.changeLike(postId, ownerId, isLikes)
    }
}