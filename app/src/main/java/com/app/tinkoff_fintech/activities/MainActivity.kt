package com.app.tinkoff_fintech.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.app.tinkoff_fintech.*
import com.app.tinkoff_fintech.activities.DetailActivity.Companion.ARG_ID_POST
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.fragments.AllPostsFragment
import com.app.tinkoff_fintech.fragments.FavoritePostsFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(),
    FragmentInteractor {

    companion object {
        const val VK_ACCESS_TOKEN = "vkAccessToken"
        const val LAST_REFRESH_TOKEN = "lastRefreshToken"
    }

    private val model: SharedViewModel by viewModels()
    private val tabs = mutableListOf("Новости", "Избранное")

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkAccessToken()
    }

    private val viewPagerListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            navView.menu.getItem(position).isChecked = true
        }
    }

    private fun onChangeFavorites(isFavorites: Boolean) {
        navView.menu.getItem(1).isVisible = isFavorites
        if (isFavorites) {
            if (tabLayout.tabCount == 1)
                tabLayout.addTab(tabLayout.newTab().apply { text = getString(R.string.nameTab2) })
        } else if (tabLayout.tabCount != 1)
            tabLayout.removeTabAt(1)
    }

    override fun onOpenDetail(sharedTextView: TextView, sharedImageView: ImageView?, post: Post) {
        val arrayPairs = if (sharedImageView == null)
            arrayOf(Pair.create(sharedTextView as View, getString(R.string.transitionNameText)))
        else arrayOf(
            Pair.create(sharedImageView as View, getString(R.string.transitionNameImage)),
            Pair.create(sharedTextView as View, getString(R.string.transitionNameText))
        )
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *arrayPairs)
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(ARG_ID_POST, post.id)
        }, options.toBundle())
    }

    override fun changeLikes(itemId: Int, ownerId: Int, isLikes: Int) {
        val vkService = NetworkService().create()

        if (isLikes == 1)
            vkService.addLike(itemId, ownerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        else
            vkService.deleteLike(itemId, ownerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun checkAccessToken() {
        AccessToken.accessToken = PreferencesService(this).getString(VK_ACCESS_TOKEN)
        NetworkService().createForSecure()
            .serviceKey()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    showError(it.message)
                },
                onSuccess = {
                    NetworkService().createWithoutInterceptor()
                        .checkToken(AccessToken.accessToken, it.access_token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                            onError = {error ->
                                showError(error.message)
                            },
                            onSuccess = {checkToken ->
                                if (checkToken.error != null) {
                                    showError(checkToken.error.error_msg)
                                    return@subscribeBy
                                }
                                if (checkToken.response.success == 0)
                                    vkLogin()
                                else initApp()
                            }
                        )
                }
            )
    }

    private fun showError(message: String?) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.errorText, message))
            .show()
    }

    private fun vkLogin() {
        VK.login(this, arrayListOf(VKScope.WALL, VKScope.FRIENDS))
    }

    private fun initApp() {
        AccessToken.accessToken = PreferencesService(this).getString(VK_ACCESS_TOKEN)
        val viewPagerAdapter = ViewPagerAdapter(
            supportFragmentManager, lifecycle, listOf(
                AllPostsFragment(),
                FavoritePostsFragment()
            )
        )
        with(viewPager) {
            adapter = viewPagerAdapter
            registerOnPageChangeCallback(viewPagerListener)
            isUserInputEnabled = false
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()

        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_newsline -> viewPager.currentItem = 0
                R.id.navigation_favorites -> viewPager.currentItem = 1
            }
            return@setOnNavigationItemSelectedListener true
        }

        model.favorites.observe(this, Observer<List<Post>> { posts ->
            if (posts.isEmpty())
                onChangeFavorites(false)
            else onChangeFavorites(true)
        })
        onChangeFavorites(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                val preferences = PreferencesService(this@MainActivity)
                preferences.put(VK_ACCESS_TOKEN, token.accessToken)
                preferences.put(LAST_REFRESH_TOKEN, Calendar.getInstance().time.time)
                initApp()
            }

            override fun onLoginFailed(errorCode: Int) {
                vkLogin()
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}


