package com.example.spendwise.UI.fragments

import AuthRepository
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spendwise.Auth.SupabaseClientProvider
import com.example.spendwise.R
import com.example.spendwise.UI.MainActivity
import com.example.spendwise.databinding.FragmentOnboardingBinding
import io.github.jan.supabase.auth.auth


class OnboardingFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingBinding
    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        authRepository = AuthRepository(requireContext())

        val isUserLoggedIn = SupabaseClientProvider.supabase.auth.currentUserOrNull() != null
        val isSignupDone = authRepository.isSignupCompleted()

        if (isUserLoggedIn || isSignupDone) {

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.getStartedButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame, SignupFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.loginTxt.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame, loginFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}


