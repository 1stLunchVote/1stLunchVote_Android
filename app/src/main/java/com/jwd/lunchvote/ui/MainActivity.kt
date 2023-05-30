package com.jwd.lunchvote.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.databinding.ActivityMainBinding
import com.jwd.lunchvote.navigation.LunchVoteNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(){
    @Inject lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LunchVoteTheme {
                // Todo : 나중에 로그인 여부 처리할거임
                LunchVoteNavHost(beforeLogin = firebaseAuth.currentUser == null)
            }
        }
    }
}
//class MainActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityMainBinding
//
//    private val navController by lazy {
//        (supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment).navController
//    }
//
//    private val beforeLogin = true
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//
//        if (beforeLogin) {
//            navController.setGraph(R.navigation.nav_login)
//        } else {
//            navController.setGraph(R.navigation.nav_main)
//        }
//    }
//}