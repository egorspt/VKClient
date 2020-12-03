package com.app.tinkoff_fintech.ui.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.mainActivity.ViewPagerAdapter
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.states.TokenState
import com.app.tinkoff_fintech.ui.contracts.MainContractInterface
import com.app.tinkoff_fintech.ui.presenters.MainPresenter
import com.app.tinkoff_fintech.ui.views.fragments.AllPostsFragment
import com.app.tinkoff_fintech.ui.views.fragments.FavoritePostsFragment
import com.app.tinkoff_fintech.ui.views.fragments.ProfileFragment
import com.app.tinkoff_fintech.utils.AccessToken
import com.app.tinkoff_fintech.utils.PreferencesService
import com.app.tinkoff_fintech.utils.State
import com.app.tinkoff_fintech.viewmodels.SharedViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
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
    lateinit var connectivityManager: com.app.tinkoff_fintech.utils.ConnectivityManager
    @Inject
    lateinit var preferencesService: PreferencesService

    private val model: SharedViewModel by viewModels()
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
        connectivityManager.listOfAvailableListener.add { connectivityOnAvailableListener() }
        connectivityManager.listOfLostListener.add { connectivityOnLostListener() }
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
                showError(state.error.message)
            }
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
        AccessToken.accessToken = preferencesService.getString(VK_ACCESS_TOKEN)
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

    private fun connectivityOnLostListener() {

    }

    private fun connectivityOnAvailableListener() {

    }
}

