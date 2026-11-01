package com.example.auth

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.CyanTextMuted
import com.example.ui.theme.CyanTextPrimary
import com.example.ui.theme.CyanTextSecondary
import com.example.ui.theme.DarkGraphiteCard
import com.example.ui.theme.DarkGraphiteCardBorder
import com.example.ui.theme.DarkGraphiteSurface
import com.example.ui.theme.EsportsBlack
import com.example.ui.theme.EsportsError
import com.example.ui.theme.EsportsErrorContainer
import com.example.ui.theme.EsportsSuccess
import com.example.ui.theme.EsportsSuccessContainer

@Composable
fun AuthRootScreen(
  viewModel: AuthViewModel,
  uiState: AuthUiState,
  onGoogleSignInClicked: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(EsportsBlack),
    contentAlignment = Alignment.TopCenter,
  ) {
    // Subtle background neon ambient glow
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(280.dp)
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(0x2500F0FF),
              Color(0x100066FF),
              Color.Transparent,
            )
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 560.dp)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Esports Branding Header
      LaxBrandingHeader()

      Spacer(modifier = Modifier.height(24.dp))

      // Status Banners (Error / Success)
      AnimatedVisibility(
        visible = uiState.errorMessage != null,
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        uiState.errorMessage?.let { message ->
          ErrorBanner(message = message, modifier = Modifier.fillMaxWidth())
          Spacer(modifier = Modifier.height(16.dp))
        }
      }

      AnimatedVisibility(
        visible = uiState.successMessage != null,
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        uiState.successMessage?.let { message ->
          SuccessBanner(message = message, modifier = Modifier.fillMaxWidth())
          Spacer(modifier = Modifier.height(16.dp))
        }
      }

      // Active Screen Selection
      when (uiState.screen) {
        AuthScreen.SIGN_IN -> {
          SignInContent(
            uiState = uiState,
            viewModel = viewModel,
            onGoogleSignInClicked = onGoogleSignInClicked,
            modifier = Modifier.fillMaxWidth(),
          )
        }
        AuthScreen.CREATE_ACCOUNT -> {
          BackHandler { viewModel.navigateTo(AuthScreen.SIGN_IN) }
          CreateAccountContent(
            uiState = uiState,
            viewModel = viewModel,
            onGoogleSignInClicked = onGoogleSignInClicked,
            modifier = Modifier.fillMaxWidth(),
          )
        }
        AuthScreen.FORGOT_PASSWORD -> {
          BackHandler { viewModel.navigateTo(AuthScreen.SIGN_IN) }
          ForgotPasswordContent(
            uiState = uiState,
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth(),
          )
        }
        AuthScreen.DASHBOARD -> {
          AuthenticatedDashboardContent(
            uiState = uiState,
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }
    }
  }
}

@Composable
fun LaxBrandingHeader(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // Glowing Emblem Badge
    Box(
      modifier = Modifier
        .size(72.dp)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(
            listOf(Color(0xFF003840), Color(0xFF001F26))
          )
        )
        .border(1.5.dp, CyanPrimary, CircleShape),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = Icons.Default.Videocam,
        contentDescription = "LAX Recorder Emblem",
        tint = CyanPrimary,
        modifier = Modifier.size(38.dp),
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Studio Name
    Text(
      text = "LAX GPC RECORDER",
      style = MaterialTheme.typography.headlineMedium.copy(
        fontWeight = FontWeight.Black,
        letterSpacing = 1.8.sp,
        fontFamily = FontFamily.SansSerif,
      ),
      color = CyanTextPrimary,
      textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(4.dp))

    // Tagline / Esports Brand
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
    ) {
      Text(
        text = "LAX ESPORTS",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp,
        ),
        color = CyanPrimary,
      )
      Text(
        text = " • ",
        style = MaterialTheme.typography.labelSmall,
        color = CyanTextMuted,
      )
      Text(
        text = "STUDIO AUTH",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Medium,
          letterSpacing = 1.sp,
        ),
        color = CyanTextSecondary,
      )
    }
  }
}

@Composable
fun SignInContent(
  uiState: AuthUiState,
  viewModel: AuthViewModel,
  onGoogleSignInClicked: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkGraphiteSurface),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(
      listOf(DarkGraphiteCardBorder, Color(0xFF162335))
    )),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
    ) {
      Text(
        text = "Sign In",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = CyanTextPrimary,
      )
      Text(
        text = "Access your verified esports gameplay records and cloud settings",
        style = MaterialTheme.typography.bodySmall,
        color = CyanTextSecondary,
        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
      )

      // Email Field
      OutlinedTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChange(it) },
        label = { Text("Email Address") },
        placeholder = { Text("name@example.com") },
        leadingIcon = {
          Icon(Icons.Default.Email, contentDescription = "Email", tint = CyanPrimary)
        },
        isError = uiState.emailError != null,
        supportingText = {
          uiState.emailError?.let { Text(it, color = EsportsError) }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Email,
          imeAction = ImeAction.Next,
        ),
        colors = authTextFieldColors(),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("email_input"),
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Password Field
      OutlinedTextField(
        value = uiState.password,
        onValueChange = { viewModel.onPasswordChange(it) },
        label = { Text("Password") },
        placeholder = { Text("Enter your password") },
        leadingIcon = {
          Icon(Icons.Default.Lock, contentDescription = "Password", tint = CyanPrimary)
        },
        trailingIcon = {
          IconButton(
            onClick = { viewModel.togglePasswordVisibility() },
            modifier = Modifier.size(48.dp),
          ) {
            Icon(
              imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
              contentDescription = if (uiState.isPasswordVisible) "Hide password" else "Show password",
              tint = CyanTextSecondary,
            )
          }
        },
        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = uiState.passwordError != null,
        supportingText = {
          uiState.passwordError?.let { Text(it, color = EsportsError) }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Password,
          imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
          onDone = { viewModel.signIn() }
        ),
        colors = authTextFieldColors(),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("password_input"),
      )

      // Forgot Password Link
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 2.dp, bottom = 12.dp),
        contentAlignment = Alignment.CenterEnd,
      ) {
        TextButton(
          onClick = { viewModel.navigateTo(AuthScreen.FORGOT_PASSWORD) },
          modifier = Modifier.testTag("forgot_password_link"),
        ) {
          Text(
            text = "Forgot Password?",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = CyanPrimary,
          )
        }
      }

      // Email Sign In Button
      Button(
        onClick = { viewModel.signIn() },
        enabled = !uiState.isLoading,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = CyanPrimary,
          contentColor = Color.Black,
          disabledContainerColor = Color(0xFF004D54),
          disabledContentColor = Color(0xFF80A3A6),
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("signin_button"),
      ) {
        if (uiState.isLoading) {
          CircularProgressIndicator(
            color = Color.Black,
            strokeWidth = 2.5.dp,
            modifier = Modifier.size(22.dp),
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Authenticating...",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          )
        } else {
          Text(
            text = "SIGN IN WITH EMAIL",
            style = MaterialTheme.typography.labelLarge.copy(
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp,
            ),
          )
        }
      }

      // Divider
      OrDivider()

      // Google Sign-In Button
      GoogleAuthButton(
        text = "SIGN IN WITH GOOGLE",
        onClick = onGoogleSignInClicked,
        enabled = !uiState.isLoading,
        testTag = "google_signin_button",
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Switch to Register
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Don't have an account?",
          style = MaterialTheme.typography.bodySmall,
          color = CyanTextSecondary,
        )
        TextButton(
          onClick = { viewModel.navigateTo(AuthScreen.CREATE_ACCOUNT) },
          modifier = Modifier.testTag("create_account_link"),
        ) {
          Text(
            text = "Create Account",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = CyanPrimary,
          )
        }
      }
    }
  }
}

@Composable
fun CreateAccountContent(
  uiState: AuthUiState,
  viewModel: AuthViewModel,
  onGoogleSignInClicked: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkGraphiteSurface),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(
      listOf(DarkGraphiteCardBorder, Color(0xFF162335))
    )),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 6.dp),
      ) {
        IconButton(
          onClick = { viewModel.navigateTo(AuthScreen.SIGN_IN) },
          modifier = Modifier.size(40.dp),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to Sign In",
            tint = CyanPrimary,
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Create Account",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = CyanTextPrimary,
        )
      }

      Text(
        text = "Register with LAX GPC RECORDER for synchronized profile & secure cloud uploads",
        style = MaterialTheme.typography.bodySmall,
        color = CyanTextSecondary,
        modifier = Modifier.padding(bottom = 20.dp),
      )

      // Email Field
      OutlinedTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChange(it) },
        label = { Text("Email Address") },
        placeholder = { Text("player@domain.com") },
        leadingIcon = {
          Icon(Icons.Default.Email, contentDescription = "Email", tint = CyanPrimary)
        },
        isError = uiState.emailError != null,
        supportingText = {
          uiState.emailError?.let { Text(it, color = EsportsError) }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Email,
          imeAction = ImeAction.Next,
        ),
        colors = authTextFieldColors(),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("create_email_input"),
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Password Field
      OutlinedTextField(
        value = uiState.password,
        onValueChange = { viewModel.onPasswordChange(it) },
        label = { Text("Password (min 6 characters)") },
        placeholder = { Text("Choose a secure password") },
        leadingIcon = {
          Icon(Icons.Default.Lock, contentDescription = "Password", tint = CyanPrimary)
        },
        trailingIcon = {
          IconButton(
            onClick = { viewModel.togglePasswordVisibility() },
            modifier = Modifier.size(48.dp),
          ) {
            Icon(
              imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
              contentDescription = if (uiState.isPasswordVisible) "Hide password" else "Show password",
              tint = CyanTextSecondary,
            )
          }
        },
        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = uiState.passwordError != null,
        supportingText = {
          uiState.passwordError?.let { Text(it, color = EsportsError) }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Password,
          imeAction = ImeAction.Next,
        ),
        colors = authTextFieldColors(),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("create_password_input"),
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Confirm Password Field
      OutlinedTextField(
        value = uiState.confirmPassword,
        onValueChange = { viewModel.onConfirmPasswordChange(it) },
        label = { Text("Confirm Password") },
        placeholder = { Text("Re-enter password") },
        leadingIcon = {
          Icon(Icons.Default.Lock, contentDescription = "Confirm Password", tint = CyanSecondary)
        },
        trailingIcon = {
          IconButton(
            onClick = { viewModel.toggleConfirmPasswordVisibility() },
            modifier = Modifier.size(48.dp),
          ) {
            Icon(
              imageVector = if (uiState.isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
              contentDescription = if (uiState.isConfirmPasswordVisible) "Hide password" else "Show password",
              tint = CyanTextSecondary,
            )
          }
        },
        visualTransformation = if (uiState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = uiState.confirmPasswordError != null,
        supportingText = {
          uiState.confirmPasswordError?.let { Text(it, color = EsportsError) }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Password,
          imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
          onDone = { viewModel.createAccount() }
        ),
        colors = authTextFieldColors(),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("confirm_password_input"),
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Submit Create Account
      Button(
        onClick = { viewModel.createAccount() },
        enabled = !uiState.isLoading,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = CyanPrimary,
          contentColor = Color.Black,
          disabledContainerColor = Color(0xFF004D54),
          disabledContentColor = Color(0xFF80A3A6),
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("create_account_button"),
      ) {
        if (uiState.isLoading) {
          CircularProgressIndicator(
            color = Color.Black,
            strokeWidth = 2.5.dp,
            modifier = Modifier.size(22.dp),
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Registering Account...",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          )
        } else {
          Text(
            text = "CREATE ACCOUNT WITH EMAIL",
            style = MaterialTheme.typography.labelLarge.copy(
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp,
            ),
          )
        }
      }

      // Divider
      OrDivider()

      // Google Sign-Up Button
      GoogleAuthButton(
        text = "SIGN UP WITH GOOGLE",
        onClick = onGoogleSignInClicked,
        enabled = !uiState.isLoading,
        testTag = "google_signup_button",
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Back to Sign In
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Already have an account?",
          style = MaterialTheme.typography.bodySmall,
          color = CyanTextSecondary,
        )
        TextButton(
          onClick = { viewModel.navigateTo(AuthScreen.SIGN_IN) },
          modifier = Modifier.testTag("back_to_signin_link"),
        ) {
          Text(
            text = "Sign In",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = CyanPrimary,
          )
        }
      }
    }
  }
}

@Composable
fun ForgotPasswordContent(
  uiState: AuthUiState,
  viewModel: AuthViewModel,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkGraphiteSurface),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(
      listOf(DarkGraphiteCardBorder, Color(0xFF162335))
    )),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 6.dp),
      ) {
        IconButton(
          onClick = { viewModel.navigateTo(AuthScreen.SIGN_IN) },
          modifier = Modifier.size(40.dp),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to Sign In",
            tint = CyanPrimary,
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Password Recovery",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = CyanTextPrimary,
        )
      }

      Text(
        text = "Enter your registered email address. We'll send an official Firebase password reset link to your inbox.",
        style = MaterialTheme.typography.bodySmall,
        color = CyanTextSecondary,
        modifier = Modifier.padding(bottom = 20.dp),
      )

      // Email Field
      OutlinedTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChange(it) },
        label = { Text("Registered Email Address") },
        placeholder = { Text("name@example.com") },
        leadingIcon = {
          Icon(Icons.Default.Email, contentDescription = "Email", tint = CyanPrimary)
        },
        isError = uiState.emailError != null,
        supportingText = {
          uiState.emailError?.let { Text(it, color = EsportsError) }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Email,
          imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
          onDone = { viewModel.sendPasswordReset() }
        ),
        colors = authTextFieldColors(),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("reset_email_input"),
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Send Reset Link Button
      Button(
        onClick = { viewModel.sendPasswordReset() },
        enabled = !uiState.isLoading,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = CyanPrimary,
          contentColor = Color.Black,
          disabledContainerColor = Color(0xFF004D54),
          disabledContentColor = Color(0xFF80A3A6),
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("send_reset_button"),
      ) {
        if (uiState.isLoading) {
          CircularProgressIndicator(
            color = Color.Black,
            strokeWidth = 2.5.dp,
            modifier = Modifier.size(22.dp),
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Sending Link...",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          )
        } else {
          Text(
            text = "SEND RESET EMAIL",
            style = MaterialTheme.typography.labelLarge.copy(
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp,
            ),
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Return to Sign In
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
      ) {
        OutlinedButton(
          onClick = { viewModel.navigateTo(AuthScreen.SIGN_IN) },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanTextPrimary),
          modifier = Modifier.testTag("back_from_reset_button"),
        ) {
          Text("Return to Sign In")
        }
      }
    }
  }
}

@Composable
fun AuthenticatedDashboardContent(
  uiState: AuthUiState,
  viewModel: AuthViewModel,
  modifier: Modifier = Modifier,
) {
  val user = uiState.currentUser

  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkGraphiteSurface),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.verticalGradient(
        listOf(CyanPrimary, DarkGraphiteCardBorder)
      )
    ),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Verified Esports Badge
      Box(
        modifier = Modifier
          .size(68.dp)
          .clip(CircleShape)
          .background(Color(0xFF003840))
          .border(2.dp, CyanPrimary, CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = "User Avatar",
          tint = CyanPrimary,
          modifier = Modifier.size(36.dp),
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "AUTHENTICATED SESSION",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Black,
          letterSpacing = 1.5.sp,
        ),
        color = CyanPrimary,
      )

      Text(
        text = user?.email ?: user?.displayName ?: "Verified Player",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = CyanTextPrimary,
        modifier = Modifier
          .padding(top = 4.dp, bottom = 12.dp)
          .testTag("user_email_display"),
      )

      // Verified Status Chip
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = EsportsSuccessContainer,
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(
          listOf(EsportsSuccess, Color(0xFF00BFA5))
        )),
        modifier = Modifier.padding(bottom = 20.dp),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Verified status",
            tint = EsportsSuccess,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Firebase Session Active & Verified",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = EsportsSuccess,
          )
        }
      }

      // Security & Metadata Details Card
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkGraphiteCard,
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 20.dp),
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = "Security details",
              tint = CyanPrimary,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Firebase Project Security",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = CyanTextPrimary,
            )
          }

          val provider = user?.providerData?.firstOrNull { it.providerId.isNotEmpty() }?.providerId ?: "firebase"
          val providerLabel = when (provider) {
            "google.com" -> "Google Sign-In (google.com)"
            "password" -> "Email / Password (password)"
            else -> provider
          }

          MetadataRow(label = "Project ID", value = "lax-gpc-recorder")
          MetadataRow(label = "Package Name", value = "com.lax.recorder")
          MetadataRow(label = "Auth Provider", value = providerLabel)
          MetadataRow(label = "Firebase UID", value = user?.uid?.take(16)?.plus("...") ?: "N/A")
          MetadataRow(
            label = "Email Verified",
            value = if (user?.isEmailVerified == true) "Yes" else "No",
          )
        }
      }

      // Sign Out Action Button
      Button(
        onClick = { viewModel.signOut() },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF261219),
          contentColor = EsportsError,
        ),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(
          listOf(EsportsError, Color(0xFF991B1B))
        )),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("sign_out_button"),
      ) {
        Icon(
          imageVector = Icons.Default.Logout,
          contentDescription = "Sign Out",
          tint = EsportsError,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "SIGN OUT OF FIREBASE",
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
          ),
        )
      }
    }
  }
}

@Composable
fun OrDivider(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    HorizontalDivider(modifier = Modifier.weight(1f), color = DarkGraphiteCardBorder)
    Text(
      text = "OR",
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
      color = CyanTextMuted,
      modifier = Modifier.padding(horizontal = 14.dp),
    )
    HorizontalDivider(modifier = Modifier.weight(1f), color = DarkGraphiteCardBorder)
  }
}

@Composable
fun GoogleAuthButton(
  text: String,
  onClick: () -> Unit,
  enabled: Boolean,
  testTag: String,
  modifier: Modifier = Modifier,
) {
  OutlinedButton(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(10.dp),
    colors = ButtonDefaults.outlinedButtonColors(
      containerColor = DarkGraphiteCard,
      contentColor = CyanTextPrimary,
      disabledContainerColor = Color(0xFF0F1520),
      disabledContentColor = Color(0xFF55657E),
    ),
    border = BorderStroke(
      1.2.dp,
      Brush.horizontalGradient(
        listOf(Color(0xFF4285F4), Color(0xFF00F0FF))
      )
    ),
    modifier = modifier
      .fillMaxWidth()
      .height(50.dp)
      .testTag(testTag),
  ) {
    GoogleLogo(modifier = Modifier.size(20.dp))
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
      ),
      color = CyanTextPrimary,
    )
  }
}

@Composable
fun GoogleLogo(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height
    val strokeWidth = w * 0.22f
    val center = Offset(w / 2f, h / 2f)
    val radius = (w - strokeWidth) / 2f

    // Top Red arc
    drawArc(
      color = Color(0xFFEA4335),
      startAngle = 200f,
      sweepAngle = 100f,
      useCenter = false,
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )
    // Left Yellow arc
    drawArc(
      color = Color(0xFFFBBC05),
      startAngle = 110f,
      sweepAngle = 90f,
      useCenter = false,
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )
    // Bottom Green arc
    drawArc(
      color = Color(0xFF34A853),
      startAngle = 30f,
      sweepAngle = 90f,
      useCenter = false,
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )
    // Right Blue arc
    drawArc(
      color = Color(0xFF4285F4),
      startAngle = -30f,
      sweepAngle = 60f,
      useCenter = false,
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )
    // Blue horizontal bar
    drawLine(
      color = Color(0xFF4285F4),
      start = Offset(center.x, center.y),
      end = Offset(center.x + radius + strokeWidth * 0.3f, center.y),
      strokeWidth = strokeWidth,
      cap = StrokeCap.Square,
    )
  }
}

@Composable
fun MetadataRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = CyanTextMuted,
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
      color = CyanTextSecondary,
    )
  }
}

@Composable
fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = EsportsErrorContainer,
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.horizontalGradient(listOf(EsportsError, Color(0xFFB91C1C)))
    ),
    modifier = modifier,
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = Icons.Default.ErrorOutline,
        contentDescription = "Error notification",
        tint = EsportsError,
        modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(12.dp))
      Text(
        text = message,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        color = Color(0xFFFFD1D8),
      )
    }
  }
}

@Composable
fun SuccessBanner(message: String, modifier: Modifier = Modifier) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = EsportsSuccessContainer,
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.horizontalGradient(listOf(EsportsSuccess, Color(0xFF059669)))
    ),
    modifier = modifier,
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "Success notification",
        tint = EsportsSuccess,
        modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(12.dp))
      Text(
        text = message,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        color = Color(0xFFD1FAE5),
      )
    }
  }
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedTextColor = CyanTextPrimary,
  unfocusedTextColor = CyanTextPrimary,
  focusedContainerColor = DarkGraphiteCard,
  unfocusedContainerColor = DarkGraphiteCard,
  cursorColor = CyanPrimary,
  focusedBorderColor = CyanPrimary,
  unfocusedBorderColor = DarkGraphiteCardBorder,
  focusedLabelColor = CyanPrimary,
  unfocusedLabelColor = CyanTextSecondary,
  focusedPlaceholderColor = CyanTextMuted,
  unfocusedPlaceholderColor = CyanTextMuted,
  errorBorderColor = EsportsError,
  errorLabelColor = EsportsError,
  errorLeadingIconColor = EsportsError,
  errorSupportingTextColor = EsportsError,
)
