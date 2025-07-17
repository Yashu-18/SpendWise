package com.example.spendwise.UI



import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.spendwise.Auth.AuthViewModel
import com.example.spendwise.Auth.AuthViewModelFactory
import com.example.spendwise.R
import com.example.spendwise.databinding.ActivityMainBinding
import com.example.spendwise.UI.fragments.AddTransactionFragment
import com.example.spendwise.UI.fragments.ChartFragment
import com.example.spendwise.UI.fragments.HomeFragment
import com.example.spendwise.UI.fragments.ProfileFragment
import com.example.spendwise.UI.fragments.ReccuringFragment



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Initialize AuthViewModel
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(applicationContext))
            .get(AuthViewModel::class.java)



        binding.bottomNavigationView.background = null
        loadFragment(HomeFragment())

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.home -> HomeFragment()
                R.id.chart -> ChartFragment()
                R.id.profile -> ProfileFragment()
                R.id.wallet -> ReccuringFragment()
                else -> null
            }
            loadFragment(selectedFragment)
        }

        binding.fab.setOnClickListener {
            AddTransactionFragment().show(supportFragmentManager, null)
        }
    }


    private fun loadFragment(fragment: Fragment?): Boolean {
        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, it)
                .commit()
            return true
        }
        return false
    }
}