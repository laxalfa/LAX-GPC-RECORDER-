package com.example.auth

/**
 * Pure validation utility for authentication fields.
 * Decoupled from Android runtime to ensure deterministic unit testing.
 */
object AuthValidation {

  private val EMAIL_REGEX = Regex(
    "[a-zA-Z0-9+._%\\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
  )

  /**
   * Validates an email address.
   * Returns null if valid, or an error message if invalid.
   */
  fun validateEmail(email: String): String? {
    val trimmed = email.trim()
    return when {
      trimmed.isEmpty() -> "Email address cannot be empty."
      !EMAIL_REGEX.matches(trimmed) -> "Please enter a valid email address (e.g. user@domain.com)."
      else -> null
    }
  }

  /**
   * Validates a password.
   * Returns null if valid, or an error message if invalid.
   */
  fun validatePassword(password: String): String? {
    return when {
      password.isEmpty() -> "Password cannot be empty."
      password.length < 6 -> "Password must be at least 6 characters long."
      else -> null
    }
  }

  /**
   * Validates confirmation password against the original password.
   */
  fun validateConfirmPassword(password: String, confirmPassword: String): String? {
    return when {
      confirmPassword.isEmpty() -> "Please confirm your password."
      password != confirmPassword -> "Passwords do not match."
      else -> null
    }
  }
}
