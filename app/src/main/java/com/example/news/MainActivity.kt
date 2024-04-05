package com.example.news

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.news.databinding.ActivityMainBinding
import com.example.news.databinding.ReusableFormBinding
import com.example.news.dto.ParcelableArticle
import com.example.news.utils.ThemePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val themePreferences: ThemePreferences by lazy {
        ThemePreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        themePreferences.loadTheme()

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                handleSplashScreen()
            }
        } else {
            binding.apply {
                motionLayout.visibility = View.GONE
                splash.visibility = View.GONE
                logo.visibility = View.GONE
                navHostFragment.visibility = View.VISIBLE
                btmNav.visibility = View.VISIBLE
            }
        }

        val bottomNav = binding.btmNav
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment

        NavigationUI.setupWithNavController(bottomNav, navHostFragment.navController)

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.e("NAV", "Hiding bottom nav ${destination.id}")
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    bottomNav.visibility = View.GONE
                }

                else -> {
                    bottomNav.visibility = View.VISIBLE
                }
            }
        }

        ThemePreferences(this).loadTheme()

    }

    private suspend fun handleSplashScreen() {
        withContext(Dispatchers.Default) {
            delay(1950)
        }
        withContext(Dispatchers.Main) {
            Log.d(
                "SplashScreen",
                "Before hiding: Logo visibility = ${binding.logo.visibility}, NavHostFragment visibility = ${binding.navHostFragment.visibility}"
            )

            binding.apply {
                motionLayout.visibility = View.GONE
                splash.visibility = View.GONE
                logo.visibility = View.GONE
                navHostFragment.visibility = View.VISIBLE
                btmNav.visibility = View.VISIBLE
            }
            Log.d(
                "SplashScreen",
                "After hiding: Logo visibility = ${binding.logo.visibility}, NavHostFragment visibility = ${binding.navHostFragment.visibility}"
            )

        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            if (intent.hasExtra("navigateTo")
                &&
                "SingleArticleFragment" == intent.getStringExtra("navigateTo")
            ) {
                val navController = binding.navHostFragment.findNavController()
                val bundle = Bundle()
                bundle.putParcelable(
                    "article",
                    intent.getParcelableExtra("article", ParcelableArticle::class.java)
                )
                navController.navigate(R.id.singleArticleFragment, bundle)
            }
        }
    }


}