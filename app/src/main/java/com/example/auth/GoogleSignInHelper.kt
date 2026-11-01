package com.example.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleSignInHelper(
  private val context: Context,
) {
  private val credentialManager = CredentialManager.create(context)

  suspend fun launchGoogleSignIn(): Result<String> {
    return try {
      val serverClientId = context.getString(R.string.default_web_client_id)

      // Exactly one option per request to keep bottom sheet responsive on modern Android
      val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
        .build()

      val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

      val response = credentialManager.getCredential(
        request = request,
        context = context,
      )

      val credential = response.credential
      if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        Result.success(googleIdTokenCredential.idToken)
      } else {
        Result.failure(IllegalStateException("Unrecognized credential type returned."))
      }
    } catch (e: GetCredentialCancellationException) {
      // Diagnostic logging: user cancelled flow or dismissed dialog
      Log.w(TAG, "Google Sign-In interactive flow was cancelled.", e)
      Result.failure(e)
    } catch (e: NoCredentialException) {
      Log.w(TAG, "No Google credentials available on device.", e)
      Result.failure(e)
    } catch (e: GetCredentialException) {
      Log.e(TAG, "GetCredentialException occurred: ${e.type} - ${e.message}", e)
      Result.failure(e)
    } catch (e: Exception) {
      Log.e(TAG, "Google Sign-In failed unexpectedly: ${e.message}", e)
      Result.failure(e)
    }
  }

  companion object {
    private const val TAG = "GoogleSignInHelper"
  }
}
