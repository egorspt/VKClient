package com.app.tinkoff_fintech.recycler.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, list: List<Fragment>) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private var fragments = list

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}