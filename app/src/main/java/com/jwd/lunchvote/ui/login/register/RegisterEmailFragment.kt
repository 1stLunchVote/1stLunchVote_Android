package com.jwd.lunchvote.ui.login.register

import com.jwd.lunchvote.core.ui.base.BaseFragment
import com.jwd.lunchvote.core.ui.util.viewBindings
import com.jwd.lunchvote.databinding.FragmentRegisterEmailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterEmailFragment : BaseFragment<FragmentRegisterEmailBinding>() {
    override val binding: FragmentRegisterEmailBinding by viewBindings(FragmentRegisterEmailBinding::inflate)

    override fun initView() {
        with(binding) {

        }
    }
}