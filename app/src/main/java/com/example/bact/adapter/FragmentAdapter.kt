package com.example.bact.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bact.ui.history.HistoryFragment
import com.example.bact.ui.home.HomeFragment

class FragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    companion object {
        const val PAGE_COUNT = 2
        const val PAGE_HOME = 0
        const val PAGE_HISTORY = 1
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            PAGE_HOME -> {
                HomeFragment.newInstance()
            }
            PAGE_HISTORY -> {
                HistoryFragment.newInstance()
            }
            else -> {
                HomeFragment.newInstance()
            }
        }
    }

    override fun getItemCount(): Int {
        return PAGE_COUNT
    }
}