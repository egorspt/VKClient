package com.app.tinkoff_fintech.ui.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.recycler.adapters.ViewPagerAdapter
import com.app.tinkoff_fintech.states.TokenState
import com.app.tinkoff_fintech.ui.contracts.MainContractInterface
import com.app.tinkoff_fintech.ui.presenters.MainPresenter
import com.app.tinkoff_fintech.ui.views.fragments.FavoritesFragment
import com.app.tinkoff_fintech.ui.views.fragments.NewsFragment
import com.app.tinkoff_fintech.ui.views.fragments.ProfileFragment
import com.app.tinkoff_fintech.utils.AccessToken
import com.app.tinkoff_fintech.utils.PreferencesService
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.android.material.transition.platform.MaterialFade
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainContractInterface.View {

    companion object {
        const val VK_ACCESS_TOKEN = "vkAccessToken"
        const val LAST_REFRESH_TOKEN = "lastRefreshToken"
        const val TAB1 = "Новости"
        const val TAB2 = "Избранное"
        const val TAB3 = "Профиль"
    }

    @Inject
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var preferencesService: PreferencesService

    private val tabs = mutableListOf(TAB1, TAB2, TAB3)

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent.inject(this)
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.attachView(this)
    }

    override fun init() {
        checkAccessToken()
    }

    private val viewPagerListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            navView.menu.getItem(position).isChecked = true
        }
    }

    private fun onChangeFavorites(isFavorites: Boolean) {
        navView.menu.getItem(1).isVisible = isFavorites
        val tabFavorites = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(1)
        if (isFavorites)
            tabFavorites.visibility = VISIBLE
        else {
            tabFavorites.visibility = GONE
            tabLayout.getTabAt(0)?.select()
        }
    }

    private fun checkAccessToken() {
        presenter.checkAccessToken()
    }

    override fun renderToken(state: TokenState) {
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
            is TokenState.Error -> {
                initApp()
            }
        }
    }

    private fun vkLogin() {
        VK.login(this, arrayListOf(VKScope.WALL, VKScope.FRIENDS, VKScope.PHOTOS, VKScope.DOCS))
    }

    private fun initApp() {
        AccessToken.accessToken = preferencesService.getString(VK_ACCESS_TOKEN)
        val viewPagerAdapter =
            ViewPagerAdapter(
                supportFragmentManager, lifecycle, listOf(
                    NewsFragment(),
                    FavoritesFragment(),
                    ProfileFragment()
                )
            )

        with(viewPager) {
            adapter = viewPagerAdapter
            registerOnPageChangeCallback(viewPagerListener)
            isUserInputEnabled = false
            offscreenPageLimit = 1
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

        presenter.getFavorites().observe(this, Observer<List<Post>> { posts ->
            if (posts.isEmpty())
                onChangeFavorites(false)
            else onChangeFavorites(true)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                preferencesService.put(VK_ACCESS_TOKEN, token.accessToken)
                preferencesService.put(LAST_REFRESH_TOKEN, Calendar.getInstance().time.time)
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

