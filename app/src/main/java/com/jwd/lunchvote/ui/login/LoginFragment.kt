package com.jwd.lunchvote.ui.login

import androidx.navigation.fragment.findNavController
import com.jwd.lunchvote.base.BaseFragment
import com.jwd.lunchvote.databinding.FragmentLoginBinding
import com.jwd.lunchvote.util.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(){
    override val binding: FragmentLoginBinding by viewBindings(FragmentLoginBinding::inflate)

    override fun initView() {
        with(binding){
            btnHome.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionDestLoginToDestHome())
            }
        }
    }
}