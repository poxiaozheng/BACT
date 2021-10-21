package com.example.bact.ui.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.example.bact.BACTApplication
import com.example.bact.R
import com.example.bact.adapter.FragmentAdapter
import com.example.bact.databinding.ActivityMainBinding
import com.example.bact.ui.activity.BaseActivity
import com.example.bact.ui.history.HistoryFragmentViewModel
import com.example.bact.ui.history.HistoryFragmentViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : BaseActivity() {

    private val tabTitleList = listOf("Home", "History")

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initStatusBar(ContextCompat.getColor(this, R.color.white))
        init()
    }

    private fun init() {

        val adapter = FragmentAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()

        binding.tabLayout.setSelectedTabIndicator(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}