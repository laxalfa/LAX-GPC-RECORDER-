package com.example.auth

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FakeAuthRepository : AuthRepository {
  private val _authState = MutableStateFlow<FirebaseUser?>(null)
  override val authStateFlow: Flow<FirebaseUser?> = _authState.asStateFlow()
  override val currentFirebaseUser: FirebaseUser? get() = _authState.value

  var googleSignInResult: AuthResult = AuthResult.Success(null, "Success")

  override suspend fun signIn(email: String, password: String): AuthResult =
    AuthResult.Success(null)

  override suspend fun register(email: String, password: String): AuthResult =
    AuthResult.Success(null)

  override suspend fun signInWithGoogle(idToken: String): AuthResult =
    googleSignInResult

  override suspend fun sendPasswordReset(email: String): AuthResult =
    AuthResult.Success(null)

  override fun signOut() {
    _authState.value = null
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
  private val testDispatcher = StandardTestDispatcher()
  private lateinit var fakeRepository: FakeAuthRepository
  private lateinit var viewModel: AuthViewModel

  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    fakeRepository = FakeAuthRepository()
    viewModel = AuthViewModel(fakeRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `signInWithGoogle success updates state and resets error`() = runTest {
    fakeRepository.googleSignInResult = AuthResult.Success(null, "Google Success")
    viewModel.signInWithGoogle("fake-id-token")
    advanceUntilIdle()

    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertNull(state.errorMessage)
  }

  @Test
  fun `signInWithGoogle error updates errorMessage`() = runTest {
    fakeRepository.googleSignInResult = AuthResult.Error("Google auth failed")
    viewModel.signInWithGoogle("fake-id-token")
    advanceUntilIdle()

    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertEquals("Google auth failed", state.errorMessage)
  }

  @Test
  fun `onGoogleSignInCancelled resets loading without error message`() = runTest {
    viewModel.onGoogleSignInCancelled()
    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertNull(state.errorMessage)
  }
}
