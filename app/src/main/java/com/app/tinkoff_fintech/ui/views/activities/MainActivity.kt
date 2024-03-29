package com.app.tinkoff_fintech.ui.views.activities

import android.os.Bundle
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
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
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

    private val authLauncher = VK.login(this) { result : VKAuthenticationResult ->
        when (result) {
            is VKAuthenticationResult.Success -> {
                preferencesService.put(MainActivity.VK_ACCESS_TOKEN, result.token.accessToken)
                preferencesService.put(MainActivity.LAST_REFRESH_TOKEN, Calendar.getInstance().time.time)
                initApp()
            }
            is VKAuthenticationResult.Failed -> {
                vkLogin()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent.inject(this)
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.attachView(this)
        vkLogin()
    }

    override fun init() {
//        checkAccessToken()
    }

    private val viewPagerListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            navView.menu.getItem(position).isChecked = true
        }
    }

    private fun onChangeFavorites(isFavorites: Boolean) {
        navView.menu.getItem(1).isVisible = isFavorites
        if (!isFavorites)
            navView.selectedItemId = R.id.navigation_news
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
        authLauncher.launch(arrayListOf(VKScope.WALL, VKScope.FRIENDS, VKScope.PHOTOS, VKScope.DOCS))
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

        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_news -> viewPager.currentItem = 0
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
}

