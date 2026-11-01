package com.example.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

/**
 * Interface defining all Firebase Authentication operations.
 */
interface AuthRepository {
  val authStateFlow: Flow<FirebaseUser?>
  val currentFirebaseUser: FirebaseUser?
  suspend fun signIn(email: String, password: String): AuthResult
  suspend fun register(email: String, password: String): AuthResult
  suspend fun signInWithGoogle(idToken: String): AuthResult
  suspend fun sendPasswordReset(email: String): AuthResult
  fun signOut()
}

/**
 * Await extension for Google Play Services Tasks without requiring external play-services coroutines dependency.
 */
suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
  addOnSuccessListener { result ->
    continuation.resume(result) {}
  }
  addOnFailureListener { exception ->
    continuation.resumeWithException(exception)
  }
  addOnCanceledListener {
    continuation.cancel()
  }
}

/**
 * Production implementation of [AuthRepository] backed by Firebase Authentication.
 */
class FirebaseAuthRepository(
  private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) : AuthRepository {

  override val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
      trySend(firebaseAuth.currentUser)
    }
    auth.addAuthStateListener(listener)
    // Emit initial state
    trySend(auth.currentUser)
    awaitClose {
      auth.removeAuthStateListener(listener)
    }
  }

  override val currentFirebaseUser: FirebaseUser?
    get() = auth.currentUser

  override suspend fun signIn(email: String, password: String): AuthResult {
    return try {
      val result = auth.signInWithEmailAndPassword(email.trim(), password).awaitTask()
      AuthResult.Success(user = result.user, message = "Signed in successfully.")
    } catch (e: Exception) {
      AuthResult.Error(
        message = mapFirebaseExceptionToMessage(e),
        errorCode = (e as? FirebaseAuthException)?.errorCode,
      )
    }
  }

  override suspend fun register(email: String, password: String): AuthResult {
    return try {
      val result = auth.createUserWithEmailAndPassword(email.trim(), password).awaitTask()
      AuthResult.Success(user = result.user, message = "Account created successfully.")
    } catch (e: Exception) {
      AuthResult.Error(
        message = mapFirebaseExceptionToMessage(e),
        errorCode = (e as? FirebaseAuthException)?.errorCode,
      )
    }
  }

  override suspend fun signInWithGoogle(idToken: String): AuthResult {
    return try {
      val credential = GoogleAuthProvider.getCredential(idToken, null)
      val result = auth.signInWithCredential(credential).awaitTask()
      AuthResult.Success(user = result.user, message = "Google Sign-In successful.")
    } catch (e: Exception) {
      AuthResult.Error(
        message = mapFirebaseExceptionToMessage(e),
        errorCode = (e as? FirebaseAuthException)?.errorCode,
      )
    }
  }

  override suspend fun sendPasswordReset(email: String): AuthResult {
    return try {
      auth.sendPasswordResetEmail(email.trim()).awaitTask()
      AuthResult.Success(message = "Password reset link sent to ${email.trim()}. Please check your inbox.")
    } catch (e: Exception) {
      AuthResult.Error(
        message = mapFirebaseExceptionToMessage(e),
        errorCode = (e as? FirebaseAuthException)?.errorCode,
      )
    }
  }

  override fun signOut() {
    auth.signOut()
  }

  companion object {
    /**
     * Translates Firebase error codes and exceptions into actionable, user-friendly messages.
     */
    fun mapFirebaseExceptionToMessage(e: Throwable): String {
      return when (e) {
        is FirebaseNetworkException -> {
          "Network connection error. Please verify your internet connection and try again."
        }
        is FirebaseTooManyRequestsException -> {
          "Access blocked due to unusual activity or too many failed attempts. Please try again later."
        }
        is FirebaseAuthException -> {
          when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "The email address is badly formatted."
            "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please verify and try again."
            "ERROR_USER_NOT_FOUND" -> "No user found with this email. Please check your email or create an account."
            "ERROR_USER_DISABLED" -> "This user account has been disabled."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Access is temporarily suspended. Try again later."
            "ERROR_OPERATION_NOT_ALLOWED" -> "Google or Email sign-in is not enabled in Firebase Console. Go to Build > Authentication > Sign-in method to enable it."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "The email address is already in use by another account. Please sign in."
            "ERROR_WEAK_PASSWORD" -> "The password is too weak. Please use at least 6 characters."
            "ERROR_INVALID_CREDENTIAL" -> "Invalid credentials. Please try again."
            else -> e.localizedMessage ?: "Authentication failed (${e.errorCode})."
          }
        }
        else -> e.localizedMessage ?: "An unexpected authentication error occurred."
      }
    }
  }
}
