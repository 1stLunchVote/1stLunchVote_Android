package com.jwd.lunchvote.ui.home

import com.jwd.lunchvote.core.ui.base.BaseFragment
import com.jwd.lunchvote.core.ui.util.viewBindings
import com.jwd.lunchvote.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val binding: FragmentHomeBinding by viewBindings(FragmentHomeBinding::inflate)

    override fun initView() {

    }
}