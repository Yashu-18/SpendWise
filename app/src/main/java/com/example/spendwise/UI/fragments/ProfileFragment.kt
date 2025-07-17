package com.example.spendwise.UI.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.spendwise.Auth.AuthViewModel
import com.example.spendwise.Auth.AuthViewModelFactory
import com.example.spendwise.UI.OnboardingScreen
import com.example.spendwise.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = AuthViewModelFactory(requireContext())
        authViewModel = ViewModelProvider(requireActivity(), factory)[AuthViewModel::class.java]

        binding.logout.setOnClickListener {
            lifecycleScope.launch {
                authViewModel.signOut()
                authViewModel.authResult.collect { result ->
                    result?.let {
                        if (it.isSuccess) {
                            // Navigate to OnboardingScreen and clear back stack
                            val intent = Intent(requireContext(), OnboardingScreen::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            requireActivity().finishAffinity() // Ensures all activities in the task are closed
                        } else {
                            val errorMessage = it.exceptionOrNull()?.message ?: "Sign-out failed"
                            showToast(errorMessage)
                        }
                    }
                }
            }
        }
    }

    // Show a Toast message
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
