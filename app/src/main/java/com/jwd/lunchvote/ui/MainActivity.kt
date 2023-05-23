package com.jwd.lunchvote.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.jwd.lunchvote.R
import com.jwd.lunchvote.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment).navController
    }

    // Todo : 나중에 로그인 여부 처리할거임
    private val beforeLogin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (beforeLogin) {
            navController.setGraph(R.navigation.nav_login)
        } else {
            navController.setGraph(R.navigation.nav_main)
        }
    }
}