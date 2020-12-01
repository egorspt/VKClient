package com.app.tinkoff_fintech.ui.views.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
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
import com.app.tinkoff_fintech.ui.views.activities.ImageActivity
import com.app.tinkoff_fintech.ui.views.activities.NewPostActivity
import com.app.tinkoff_fintech.ui.views.activities.NewPostActivity.Companion.OWNER_NAME
import com.app.tinkoff_fintech.ui.views.activities.NewPostActivity.Companion.OWNER_PHOTO
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.detail.DetailActivity
import com.app.tinkoff_fintech.paging.wall.ProfileAdapter
import com.app.tinkoff_fintech.paging.wall.WallListViewModel
import com.app.tinkoff_fintech.ui.contracts.ProfileContractInterface
import com.app.tinkoff_fintech.ui.presenters.ProfilePresenter
import com.app.tinkoff_fintech.ui.views.activities.NewPostActivity.Companion.PICK_PHOTO
import com.app.tinkoff_fintech.utils.State
import com.app.tinkoff_fintech.vk.ProfileInformation
import kotlinx.android.synthetic.main.fragment_profile.*
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
        initState()
        initAdapter()
        presenter.getProfileInformation()
    }

    private fun clickImage(url: String) {
        requireActivity().startActivity(Intent(activity, ImageActivity::class.java).apply {
            putExtra(DetailActivity.ARG_URL_IMAGE, url)
        })
    }

    private fun initAdapter() {
        val dividerItemDecoration = DividerItemDecoration(activity, RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.divider_post_space,null)!!)
        recyclerView.addItemDecoration(dividerItemDecoration)

        profileAdapter.clickImage = { url -> clickImage(url) }
        profileAdapter.retry = { viewModel.retry() }
        profileAdapter.newPostClickListener = { ownerPhoto, ownerName, pickPhoto -> startNewPostActivity(ownerPhoto, ownerName, pickPhoto) }
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = profileAdapter
        viewModel.newsList.observe(requireActivity(), Observer<PagedList<Post>> {
            profileAdapter.submitList(it)
        })
    }

    private fun initState() {
        textError.setOnClickListener { viewModel.retry() }
        viewModel.getState().observe(requireActivity(), Observer { state ->
            progressBar.visibility =
                if (viewModel.listIsEmpty() && state == State.LOADING) View.VISIBLE else View.GONE
            textError.visibility =
                if (viewModel.listIsEmpty() && state == State.ERROR) View.VISIBLE else View.GONE
            if (!viewModel.listIsEmpty()) {
                profileAdapter.setState(state ?: State.DONE)
            }
        })
    }

    override fun showError(error: String?) {
        AlertDialog.Builder(activity)
            .setMessage(error)
            .show()
    }

    override fun updateProfileInformation(profileInformation: ProfileInformation) {
        profileAdapter.profileInformation = profileInformation
        profileAdapter.notifyItemChanged(0)
    }

    private fun startNewPostActivity(ownerPhoto: String, ownerName: String, pickPhoto: Boolean) {
        startActivityForResult(Intent(requireActivity(), NewPostActivity::class.java).apply {
            putExtra(OWNER_PHOTO, ownerPhoto)
            putExtra(OWNER_NAME, ownerName)
            putExtra(PICK_PHOTO, pickPhoto)
        }, 42)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
            viewModel.invalidate()
    }
}