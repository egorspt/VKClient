package com.app.tinkoff_fintech.ui.views.activities

import android.annotation.SuppressLint
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
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.detail.DetailActivity
import com.app.tinkoff_fintech.detail.DetailActivity.Companion.ARG_URL_IMAGE
import com.app.tinkoff_fintech.mainActivity.FragmentInteractor
import com.app.tinkoff_fintech.mainActivity.ViewPagerAdapter
import com.app.tinkoff_fintech.ui.views.fragments.AllPostsFragment
import com.app.tinkoff_fintech.ui.views.fragments.FavoritePostsFragment
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.ui.views.fragments.ProfileFragment
import com.app.tinkoff_fintech.states.TokenState
import com.app.tinkoff_fintech.utils.AccessToken
import com.app.tinkoff_fintech.utils.PreferencesService
import com.app.tinkoff_fintech.viewmodels.SharedViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(),
    FragmentInteractor {

    companion object {
        const val VK_ACCESS_TOKEN = "vkAccessToken"
        const val LAST_REFRESH_TOKEN = "lastRefreshToken"
        const val TAB1 = "Новости"
        const val TAB2 = "Избранное"
        const val TAB3 = "Профиль"
    }

    @Inject
    lateinit var connectivityManager: com.app.tinkoff_fintech.utils.ConnectivityManager

    private val model: SharedViewModel by viewModels()
    private val tabs = mutableListOf(
        TAB1,
        TAB2,
        TAB3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent.inject(this)
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkAccessToken()

        connectivityManager.listOfAvailableListener.add{ connectivityOnAvailableListener() }
        connectivityManager.listOfLostListener.add{ connectivityOnLostListener() }
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
        } else if (tabLayout.getTabAt(1)?.text == TAB2)
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
            putExtra(ARG_URL_IMAGE, post.id)
        }, options.toBundle())
    }

    override fun changeLikes(itemId: Int, ownerId: Int, isLikes: Int) {
        val vkService = NetworkService.create()

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

    @SuppressLint("CheckResult")
    private fun checkAccessToken() {
        AccessToken.accessToken = PreferencesService(
            this
        ).getString(VK_ACCESS_TOKEN)
        NetworkService.createForSecure()
            .serviceKey()
            .subscribeOn(Schedulers.io())
            .flatMap {
                NetworkService.createWithoutInterceptor()
                    .checkToken(AccessToken.accessToken, it.access_token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map<TokenState> { result -> TokenState.Success(result) }
                    .onErrorReturn { e -> TokenState.Error(e) }
            }
            .onErrorReturn { e -> TokenState.Error(e) }
            .subscribe(::renderToken)
    }

    private fun renderToken(state: TokenState) {
        when (state) {
            is TokenState.Success -> {
                if (state.response.error != null) {
                    vkLogin()
                    return
                }
                if (state.response.response.success == 0)
                    vkLogin()
                else initApp()
            }
            is TokenState.Error -> { showError(state.error.message) }
        }
    }


    private fun showError(message: String?) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.errorText, message))
            .show()
    }

    private fun vkLogin() {
        VK.login(this, arrayListOf(VKScope.WALL, VKScope.FRIENDS, VKScope.PHOTOS, VKScope.DOCS))
    }

    private fun initApp() {
        AccessToken.accessToken = PreferencesService(
            this
        ).getString(VK_ACCESS_TOKEN)
        val viewPagerAdapter =
            ViewPagerAdapter(
                supportFragmentManager, lifecycle, listOf(
                    AllPostsFragment(),
                    FavoritePostsFragment(),
                    ProfileFragment()
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
                R.id.navigation_profile -> viewPager.currentItem = 2
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
                    PreferencesService(this@MainActivity)
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

    override fun reLogin() {
        vkLogin()
    }

    private fun connectivityOnLostListener() {

    }

    private fun connectivityOnAvailableListener() {

    }
}

