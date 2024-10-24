package com.muratguzel.trackyourtime.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.databinding.ActivitySplashBinding
import com.muratguzel.trackyourtime.ui.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)

        Thread.sleep(3000)
        installSplashScreen()
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.statusBarColor)
        checkUserState()
    }

    private fun checkUserState() {
        authViewModel.currentUserNavigateState.observe(this) { currentUserRedirectionState ->
            if (currentUserRedirectionState) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
