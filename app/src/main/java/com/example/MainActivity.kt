package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.auth.AuthRootScreen
import com.example.auth.AuthViewModel
import com.example.auth.GoogleSignInHelper
import com.example.ui.theme.EsportsBlack
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  private val authViewModel: AuthViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val googleSignInHelper = remember { GoogleSignInHelper(this@MainActivity) }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = EsportsBlack,
        ) { innerPadding ->
          AuthRootScreen(
            viewModel = authViewModel,
            uiState = uiState,
            onGoogleSignInClicked = {
              coroutineScope.launch {
                val result = googleSignInHelper.launchGoogleSignIn()
                result.onSuccess { idToken ->
                  authViewModel.signInWithGoogle(idToken)
                }.onFailure { error ->
                  if (error is GetCredentialCancellationException) {
                    authViewModel.onGoogleSignInCancelled()
                  } else {
                    authViewModel.onGoogleSignInError(
                      error.localizedMessage ?: "Google Sign-In failed. Please try again."
                    )
                  }
                }
              }
            },
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding),
          )
        }
      }
    }
  }
}
