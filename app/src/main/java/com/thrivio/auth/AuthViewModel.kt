package com.thrivio.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrivio.network.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.Google
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    data object Loading : AuthUiState()
    data object SignedOut : AuthUiState()
    data object SignedIn : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isSignUp = MutableStateFlow(false)
    val isSignUp: StateFlow<Boolean> = _isSignUp.asStateFlow()

    init {
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            val session = try {
                SupabaseClient.client.auth.currentSessionOrNull()
            } catch (_: Exception) {
                null
            }
            _uiState.value = if (session != null) AuthUiState.SignedIn else AuthUiState.SignedOut
        }
    }

    fun toggleSignUpMode() {
        _isSignUp.value = !_isSignUp.value
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                SupabaseClient.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                _uiState.value = AuthUiState.SignedIn
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Sign in failed")
            }
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                _uiState.value = AuthUiState.SignedIn
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                SupabaseClient.client.auth.signInWith(Google)
                _uiState.value = AuthUiState.SignedIn
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Google sign in failed")
            }
        }
    }

    fun clearError() {
        _uiState.value = AuthUiState.SignedOut
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
            } catch (_: Exception) { }
            _uiState.value = AuthUiState.SignedOut
        }
    }
}
