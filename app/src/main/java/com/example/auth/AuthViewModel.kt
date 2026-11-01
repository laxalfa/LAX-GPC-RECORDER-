package com.example.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthScreen {
  SIGN_IN,
  CREATE_ACCOUNT,
  FORGOT_PASSWORD,
  DASHBOARD,
}

data class AuthUiState(
  val screen: AuthScreen = AuthScreen.SIGN_IN,
  val email: String = "",
  val password: String = "",
  val confirmPassword: String = "",
  val isPasswordVisible: Boolean = false,
  val isConfirmPasswordVisible: Boolean = false,
  val emailError: String? = null,
  val passwordError: String? = null,
  val confirmPasswordError: String? = null,
  val isLoading: Boolean = false,
  val errorMessage: String? = null,
  val successMessage: String? = null,
  val currentUser: FirebaseUser? = null,
)

class AuthViewModel(
  private val repository: AuthRepository = FirebaseAuthRepository(),
) : ViewModel() {

  private val _uiState = MutableStateFlow(AuthUiState())
  val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

  init {
    // Monitor real Firebase authentication state and session restoration
    viewModelScope.launch {
      repository.authStateFlow.collect { user ->
        _uiState.update { current ->
          current.copy(
            currentUser = user,
            screen = if (user != null) AuthScreen.DASHBOARD else {
              if (current.screen == AuthScreen.DASHBOARD) AuthScreen.SIGN_IN else current.screen
            },
            isLoading = false,
          )
        }
      }
    }
  }

  fun onEmailChange(newEmail: String) {
    _uiState.update {
      it.copy(
        email = newEmail,
        emailError = null,
        errorMessage = null,
      )
    }
  }

  fun onPasswordChange(newPassword: String) {
    _uiState.update {
      it.copy(
        password = newPassword,
        passwordError = null,
        errorMessage = null,
      )
    }
  }

  fun onConfirmPasswordChange(newConfirm: String) {
    _uiState.update {
      it.copy(
        confirmPassword = newConfirm,
        confirmPasswordError = null,
        errorMessage = null,
      )
    }
  }

  fun togglePasswordVisibility() {
    _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
  }

  fun toggleConfirmPasswordVisibility() {
    _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
  }

  fun navigateTo(screen: AuthScreen) {
    _uiState.update {
      it.copy(
        screen = screen,
        emailError = null,
        passwordError = null,
        confirmPasswordError = null,
        errorMessage = null,
        successMessage = null,
      )
    }
  }

  fun dismissMessages() {
    _uiState.update {
      it.copy(errorMessage = null, successMessage = null)
    }
  }

  fun signIn() {
    val state = _uiState.value
    if (state.isLoading) return

    val emailError = AuthValidation.validateEmail(state.email)
    val passwordError = AuthValidation.validatePassword(state.password)

    if (emailError != null || passwordError != null) {
      _uiState.update {
        it.copy(
          emailError = emailError,
          passwordError = passwordError,
          errorMessage = "Please resolve the highlighted errors before submitting.",
        )
      }
      return
    }

    _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

    viewModelScope.launch {
      when (val result = repository.signIn(state.email, state.password)) {
        is AuthResult.Success -> {
          _uiState.update {
            it.copy(
              isLoading = false,
              password = "",
              confirmPassword = "",
              errorMessage = null,
              successMessage = null,
            )
          }
        }
        is AuthResult.Error -> {
          _uiState.update {
            it.copy(
              isLoading = false,
              errorMessage = result.message,
            )
          }
        }
      }
    }
  }

  fun createAccount() {
    val state = _uiState.value
    if (state.isLoading) return

    val emailError = AuthValidation.validateEmail(state.email)
    val passwordError = AuthValidation.validatePassword(state.password)
    val confirmError = AuthValidation.validateConfirmPassword(state.password, state.confirmPassword)

    if (emailError != null || passwordError != null || confirmError != null) {
      _uiState.update {
        it.copy(
          emailError = emailError,
          passwordError = passwordError,
          confirmPasswordError = confirmError,
          errorMessage = "Please resolve the highlighted errors before submitting.",
        )
      }
      return
    }

    _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

    viewModelScope.launch {
      when (val result = repository.register(state.email, state.password)) {
        is AuthResult.Success -> {
          _uiState.update {
            it.copy(
              isLoading = false,
              password = "",
              confirmPassword = "",
              errorMessage = null,
              successMessage = "Account registered successfully!",
            )
          }
        }
        is AuthResult.Error -> {
          _uiState.update {
            it.copy(
              isLoading = false,
              errorMessage = result.message,
            )
          }
        }
      }
    }
  }

  fun sendPasswordReset() {
    val state = _uiState.value
    if (state.isLoading) return

    val emailError = AuthValidation.validateEmail(state.email)
    if (emailError != null) {
      _uiState.update {
        it.copy(
          emailError = emailError,
          errorMessage = "Please enter a valid email address.",
        )
      }
      return
    }

    _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

    viewModelScope.launch {
      when (val result = repository.sendPasswordReset(state.email)) {
        is AuthResult.Success -> {
          _uiState.update {
            it.copy(
              isLoading = false,
              errorMessage = null,
              successMessage = result.message,
            )
          }
        }
        is AuthResult.Error -> {
          _uiState.update {
            it.copy(
              isLoading = false,
              errorMessage = result.message,
            )
          }
        }
      }
    }
  }

  fun signInWithGoogle(idToken: String) {
    if (_uiState.value.isLoading) return
    _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

    viewModelScope.launch {
      when (val result = repository.signInWithGoogle(idToken)) {
        is AuthResult.Success -> {
          _uiState.update {
            it.copy(
              isLoading = false,
              password = "",
              confirmPassword = "",
              errorMessage = null,
              successMessage = null,
            )
          }
        }
        is AuthResult.Error -> {
          _uiState.update {
            it.copy(
              isLoading = false,
              errorMessage = result.message,
            )
          }
        }
      }
    }
  }

  fun onGoogleSignInCancelled() {
    _uiState.update {
      it.copy(
        isLoading = false,
        errorMessage = null,
      )
    }
  }

  fun onGoogleSignInError(message: String) {
    _uiState.update {
      it.copy(
        isLoading = false,
        errorMessage = message,
      )
    }
  }

  fun signOut() {
    _uiState.update { it.copy(isLoading = true) }
    repository.signOut()
    _uiState.update {
      it.copy(
        password = "",
        confirmPassword = "",
        isLoading = false,
        errorMessage = null,
        successMessage = "Signed out successfully.",
      )
    }
  }
}
