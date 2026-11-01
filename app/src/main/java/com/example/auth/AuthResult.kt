package com.example.auth

import com.google.firebase.auth.FirebaseUser

/**
 * Result representing the outcome of a Firebase Authentication operation.
 */
sealed interface AuthResult {
  data class Success(
    val user: FirebaseUser? = null,
    val message: String? = null,
  ) : AuthResult

  data class Error(
    val message: String,
    val errorCode: String? = null,
  ) : AuthResult
}
