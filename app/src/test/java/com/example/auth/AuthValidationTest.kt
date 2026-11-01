package com.example.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuthValidationTest {

  @Test
  fun `validateEmail returns null for valid emails`() {
    assertNull(AuthValidation.validateEmail("gamer@lax.com"))
    assertNull(AuthValidation.validateEmail("esports.pro@domain.gg"))
    assertNull(AuthValidation.validateEmail("player+recorder@sample.co"))
  }

  @Test
  fun `validateEmail returns error for invalid or empty emails`() {
    assertNotNull(AuthValidation.validateEmail(""))
    assertNotNull(AuthValidation.validateEmail("   "))
    assertNotNull(AuthValidation.validateEmail("plainaddress"))
    assertNotNull(AuthValidation.validateEmail("@missingusername.com"))
    assertNotNull(AuthValidation.validateEmail("user@.com"))
  }

  @Test
  fun `validatePassword returns null when password is 6 or more characters`() {
    assertNull(AuthValidation.validatePassword("123456"))
    assertNull(AuthValidation.validatePassword("esportsSecurePass!"))
  }

  @Test
  fun `validatePassword returns error when password is empty or under 6 characters`() {
    assertNotNull(AuthValidation.validatePassword(""))
    assertNotNull(AuthValidation.validatePassword("12345"))
  }

  @Test
  fun `validateConfirmPassword validates equality correctly`() {
    assertNull(AuthValidation.validateConfirmPassword("mysecret123", "mysecret123"))
    assertNotNull(AuthValidation.validateConfirmPassword("mysecret123", "differentPass"))
    assertNotNull(AuthValidation.validateConfirmPassword("mysecret123", ""))
  }

  @Test
  fun `mapFirebaseExceptionToMessage maps common error codes to friendly text`() {
    val invalidEmailEx = FirebaseAuthException("ERROR_INVALID_EMAIL", "Bad email")
    assertEquals(
      "The email address is badly formatted.",
      FirebaseAuthRepository.mapFirebaseExceptionToMessage(invalidEmailEx),
    )

    val wrongPasswordEx = FirebaseAuthException("ERROR_WRONG_PASSWORD", "Wrong password")
    assertEquals(
      "Incorrect password. Please verify and try again.",
      FirebaseAuthRepository.mapFirebaseExceptionToMessage(wrongPasswordEx),
    )

    val userNotFoundEx = FirebaseAuthException("ERROR_USER_NOT_FOUND", "No user")
    assertEquals(
      "No user found with this email. Please check your email or create an account.",
      FirebaseAuthRepository.mapFirebaseExceptionToMessage(userNotFoundEx),
    )

    val emailInUseEx = FirebaseAuthException("ERROR_EMAIL_ALREADY_IN_USE", "Email used")
    assertEquals(
      "The email address is already in use by another account. Please sign in.",
      FirebaseAuthRepository.mapFirebaseExceptionToMessage(emailInUseEx),
    )

    val notAllowedEx = FirebaseAuthException("ERROR_OPERATION_NOT_ALLOWED", "Disabled")
    assertTrue(
      FirebaseAuthRepository.mapFirebaseExceptionToMessage(notAllowedEx)
        .contains("sign-in is not enabled"),
    )

    val networkEx = FirebaseNetworkException("Network error")
    assertTrue(
      FirebaseAuthRepository.mapFirebaseExceptionToMessage(networkEx)
        .contains("Network connection error"),
    )

    val tooManyReqEx = FirebaseTooManyRequestsException("Too many attempts")
    assertTrue(
      FirebaseAuthRepository.mapFirebaseExceptionToMessage(tooManyReqEx)
        .contains("unusual activity or too many failed attempts"),
    )
  }
}
