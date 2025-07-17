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
import com.example.spendwise.R
import com.example.spendwise.UI.MainActivity
import com.example.spendwise.databinding.FragmentSignupBinding
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val factory = AuthViewModelFactory(requireContext())
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        binding.login.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame, loginFragment())
                .addToBackStack(null)
                .commit()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.authResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        showToast("Signup successful")
                        authViewModel.resetAuthResult()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    } else {
                        showToast(it.exceptionOrNull()?.message ?: "Unknown error")
                        authViewModel.resetAuthResult()
                    }
                }
            }
        }

        binding.signup.setOnClickListener {
            val firstName = binding.firstname.text.toString()
            val lastName = binding.lastname.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.conpassword.text.toString()

            if (validateInput(firstName, lastName, email, password, confirmPassword)) {
                authViewModel.signup(firstName, lastName, email, password)
            }
        }
    }

    private fun validateInput(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return when {
            firstName.isEmpty() -> { showToast("First name is required"); false }
            lastName.isEmpty() -> { showToast("Last name is required"); false }
            email.isEmpty() -> { showToast("Email is required"); false }
            password.isEmpty() -> { showToast("Password is required"); false }
            confirmPassword.isEmpty() -> { showToast("Confirm password is required"); false }
            password != confirmPassword -> { showToast("Passwords do not match"); false }
            else -> true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
