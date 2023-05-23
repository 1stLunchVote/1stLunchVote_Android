package com.jwd.lunchvote.ui.home

import com.jwd.lunchvote.base.BaseFragment
import com.jwd.lunchvote.databinding.FragmentHomeBinding
import com.jwd.lunchvote.util.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val binding: FragmentHomeBinding by viewBindings(FragmentHomeBinding::inflate)

    override fun initView() {

    }
}