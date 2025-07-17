package com.example.spendwise.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.spendwise.R
import com.example.spendwise.UI.fragments.OnboardingFragment
import com.example.spendwise.databinding.ActivityOnboardingScreenBinding

class OnboardingScreen : AppCompatActivity() {

    private lateinit var binding : ActivityOnboardingScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding =  DataBindingUtil.setContentView(this,R.layout.activity_onboarding_screen)

        loadfragment(OnboardingFragment())

    }

    private fun loadfragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
    }

}

