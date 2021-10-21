package com.example.bact.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bact.getOrAwaitValue
import com.example.bact.ui.home.HomeFragmentViewModel
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityViewModelTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var myActivityViewModel: HomeFragmentViewModel

    @Before
    fun setupViewModel(){
        myActivityViewModel = HomeFragmentViewModel()
    }

    @Test
    fun setScale_scale_is_4(){
        myActivityViewModel.setScale(4)
        val value = myActivityViewModel.scale.getOrAwaitValue()
        assertThat(value,`is`(4))
    }

    @Test
    fun setNoiseGrade_noiseGrade_is_2(){
        myActivityViewModel.setNoiseGrade(2)
        val value = myActivityViewModel.noiseGrade.getOrAwaitValue()
        assertThat(value,`is`(2))
    }

}