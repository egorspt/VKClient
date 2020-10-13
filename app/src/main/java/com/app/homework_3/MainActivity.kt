package com.app.homework_3

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.news_fragment.*


class MainActivity : AppCompatActivity(), FragmentInteractor {

    private val model: SharedViewModel by viewModels()
    private val tabs = mutableListOf("Новости", "Избранное")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter
        viewPager.registerOnPageChangeCallback(viewPagerListener)
        viewPagerAdapter.update(
            listOf(
                AllPostsFragment(),
                FavoritePostsFragment()
            )
        )

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

    override fun onOpenDetail(
        sharedImageView: ImageView?,
        groupName: String,
        contentImage: String?,
        contentText: String
    ) {
        val options = sharedImageView?.let {
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                it,
                ViewCompat.getTransitionName(sharedImageView)!!
            )
        }
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(ARG_GROUP_NAME, groupName)
            putExtra(ARG_CONTENT_IMAGE, contentImage)
            putExtra(ARG_CONTENT_TEXT, contentText)
        }, options?.toBundle())
    }
}

