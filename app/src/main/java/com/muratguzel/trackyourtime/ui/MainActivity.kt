package com.muratguzel.trackyourtime.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.databinding.ActivityMainBinding
import com.muratguzel.trackyourtime.ui.fragment.CalendarFragment
import com.muratguzel.trackyourtime.ui.fragment.CountDownFragment
import com.muratguzel.trackyourtime.ui.fragment.ProfileFragment
import com.muratguzel.trackyourtime.ui.fragment.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleBackPress()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        window.statusBarColor = resources.getColor(R.color.statusBarColor)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.homeContainer, CountDownFragment())
                .commit()
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.ic_home -> CountDownFragment()
                R.id.ic_profile -> ProfileFragment()
                R.id.ic_calender ->CalendarFragment()
                else -> null
            }

            fragment?.let {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.homeContainer)


                if (currentFragment?.javaClass != fragment.javaClass) {
                    supportFragmentManager.popBackStack()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homeContainer, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
            true
        }
    }
    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.homeContainer)

                if (currentFragment is CountDownFragment) {
                    finish()
                } else {
                    window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.white)
                    binding.bottomNavigationView.selectedItemId = R.id.ic_home


                }
            }
        })
    }
}
