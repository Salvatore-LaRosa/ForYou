package com.example.foryou.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import com.example.foryou.viewmodel.AuthViewModel

/**
 * A screen for setting up a new PIN.
 * This screen is shown after registration or if the user has not set a PIN yet.
 *
 * @param navController The NavController for navigation.
 * @param viewModel The ViewModel for authentication.
 */
@Composable
fun SetupPinScreen(navController: NavController, viewModel: AuthViewModel) {
    var pin by remember { mutableStateOf("") }
    val email = viewModel.getSavedEmail() ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crea un PIN",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        ContinuousPinFieldSetup(
            pin = pin,
            onPinChange = { newPin ->
                if (newPin.length <= 4 && newPin.all(Char::isDigit)) pin = newPin
            }
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (pin.length == 4) {
                    viewModel.saveUserPin(email, pin)
                    navController.navigate("mainPage/$email") {
                        popUpTo("setup_pin") { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Conferma", style = MaterialTheme.typography.titleMedium)
        }
    }
}

/**
 * PIN input field styled like the login screen version.
 */
@Composable
private fun ContinuousPinFieldSetup(
    pin: String,
    onPinChange: (String) -> Unit
) {
    val focusRequesters = List(4) { FocusRequester() }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            val char = pin.getOrNull(index)?.toString() ?: ""
            var isFocused by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .size(65.dp)
                    .border(
                        width = 2.dp,
                        color = if (isFocused)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.medium
                    )
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = TextFieldValue(
                        text = char,
                        selection = TextRange(char.length)
                    ),
                    onValueChange = { newText ->
                        if (newText.text.length <= 1 && newText.text.isDigitsOnly()) {
                            val newPin = buildString {
                                append(pin.take(index))
                                append(newText.text)
                                append(pin.drop(index + 1))
                            }
                            onPinChange(newPin)
                            if (newText.text.isNotEmpty() && index < 3) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    },
                    textStyle = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .focusRequester(focusRequesters[index])
                        .onFocusChanged { isFocused = it.isFocused }
                        .onKeyEvent { event ->
                            if (event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                                char.isEmpty() && index > 0
                            ) {
                                focusRequesters[index - 1].requestFocus()
                                onPinChange(pin.dropLast(1))
                            }
                            false
                        },
                    visualTransformation = PasswordVisualTransformation(),
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.Center) {
                            if (char.isEmpty()) {
                                Text(
                                    text = "-",
                                    fontSize = 28.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            inner()
                        }
                    }
                )
            }
        }
    }
}
