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
import com.example.spendwise.UI.MainActivity
import com.example.spendwise.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class loginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val factory = AuthViewModelFactory(requireContext())
        authViewModel = ViewModelProvider(requireActivity(), factory)[AuthViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.authResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        showToast("Login successful")
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

        binding.Login.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (validateInput(email, password)) {
                authViewModel.login(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> { showToast("Email is required"); false }
            password.isEmpty() -> { showToast("Password is required"); false }
            else -> true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
