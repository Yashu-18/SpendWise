package com.example.spendwise.Auth

import AuthRepository
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendwise.Auth.SupabaseClientProvider.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(private val context: Context) : ViewModel() {

    private val _authResult = MutableStateFlow<Result<Unit>?>(null)
    val authResult: StateFlow<Result<Unit>?> = _authResult

    private val authRepository = AuthRepository(context)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = try {
                authRepository.login(email, password)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun signup(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = try {
                authRepository.signup(firstName, lastName, email, password)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _authResult.value = try {
                authRepository.signOut()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    // AuthViewModel.kt
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    fun checkUserLoggedIn() {
        viewModelScope.launch {
            _isLoggedIn.value = supabase.auth.currentSessionOrNull() != null
        }
    }

    fun resetAuthResult() {
        _authResult.value = null
    }
}
