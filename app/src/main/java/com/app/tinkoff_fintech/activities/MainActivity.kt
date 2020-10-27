package com.app.tinkoff_fintech.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.app.tinkoff_fintech.*
import com.app.tinkoff_fintech.activities.DetailActivity.Companion.ARG_POST
import com.app.tinkoff_fintech.fragments.AllPostsFragment
import com.app.tinkoff_fintech.fragments.FavoritePostsFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(),
    FragmentInteractor {

    private val model: SharedViewModel by viewModels()
    private val tabs = mutableListOf("Новости", "Избранное")

    override fun onCreate(savedInstanceState: Bundle?) {
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
                tabLayout.addTab(tabLayout.newTab().apply { text = "Избранное" })
        } else if (tabLayout.tabCount != 1)
            tabLayout.removeTabAt(1)
    }

    override fun onOpenDetail(sharedTextView: TextView, sharedImageView: ImageView?, post: Post) {
        val options = sharedImageView?.let {
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                *arrayOf(
                    Pair.create(sharedImageView as View, getString(R.string.transitionNameImage)),
                    Pair.create(sharedTextView as View, getString(R.string.transitionNameText))
                )
            )
        }
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(ARG_POST, post)
        }, options?.toBundle())
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
        val lastTimeLongRefreshToken =
            getSharedPreferences(getString(R.string.appPreferences), Context.MODE_PRIVATE)
                .getLong(getString(R.string.lastRefreshToken), 0L)
        if (lastTimeLongRefreshToken == 0L) {
            vkLogin()
        } else {
            val lastTimeDayRefreshToken =
                (Calendar.getInstance().time.time.toDouble() - lastTimeLongRefreshToken.toDouble()) / 1000 / 60 / 60 / 24
            if (lastTimeDayRefreshToken > 1)
                vkLogin()
            else initApp()
        }
    }

    private fun vkLogin() {
        VK.login(this, arrayListOf(VKScope.WALL, VKScope.FRIENDS))
    }

    private fun initApp() {
        AccessToken.value = getSharedPreferences(
            getString(R.string.appPreferences),
            Context.MODE_PRIVATE
        ).getString(getString(R.string.vkAccessToken), "")!!
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
                val preferences =
                    getSharedPreferences(getString(R.string.appPreferences), Context.MODE_PRIVATE)
                preferences.edit()
                    .putString(getString(R.string.vkAccessToken), token.accessToken)
                    .apply()
                preferences.edit()
                    .putLong(getString(R.string.lastRefreshToken), Calendar.getInstance().time.time)
                    .apply()
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


