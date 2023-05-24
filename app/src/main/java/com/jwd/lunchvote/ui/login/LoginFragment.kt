package com.jwd.lunchvote.ui.login

import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jwd.lunchvote.core.ui.base.BaseFragment
import com.jwd.lunchvote.core.ui.util.setOnTextChangedListener
import com.jwd.lunchvote.core.ui.util.viewBindings
import com.jwd.lunchvote.databinding.FragmentLoginBinding
import com.jwd.lunchvote.ui.login.LoginContract.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(){
    private val viewModel : LoginViewModel by viewModels()
    override val binding: FragmentLoginBinding by viewBindings(FragmentLoginBinding::inflate)

    override fun initView() {
        with(binding){
            // 뷰모델을 xml에 데이터바인딩
            loginViewModel = viewModel

            btnLoginRegister.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionDestLoginToDestRegisterEmail())
            }

            tilLoginEmail.editText?.apply {
                // 이메일 입력 후 엔터 누르면 비밀번호 입력으로 이동
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                        tilLoginPwd.editText?.requestFocus()
                        true
                    } else {
                        false
                    }
                }

                // 이메일 입력이 변경되면 뷰모델에 전달
                this.setOnTextChangedListener {
                    viewModel.sendEvent(LoginEvent.SetEmail(it))
                }
            }

            // 비밀번호 입력이 변경되면 뷰모델에 전달
            tilLoginPwd.editText?.setOnTextChangedListener {
                viewModel.sendEvent(LoginEvent.SetPwd(it))
            }
        }
    }
}