package com.rodrigoloq.chatapp.auth.forgotpassword

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rodrigoloq.chatapp.R
import com.rodrigoloq.chatapp.auth.forgotpassword.viewmodel.ForgotPasswordUIState
import com.rodrigoloq.chatapp.auth.forgotpassword.viewmodel.ForgotPasswordViewModel
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme
import com.rodrigoloq.chatapp.ui.theme.ProgressBackground
import kotlin.text.trim

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordViewPreview() {
    ChatAppTheme() {
        ForgotPasswordViewContent(
            state = ForgotPasswordUIState(),
            modifier = Modifier,
            navController = rememberNavController(),
            sendInstructions = { }
        )
    }
}

@Composable
fun ForgotPasswordViewContent(
    state: ForgotPasswordUIState,
    modifier: Modifier = Modifier,
    navController: NavController,
    sendInstructions: (String) -> Unit
) {
    val context = LocalContext.current
    val focus = LocalFocusManager.current

    LaunchedEffect(state.successSend) {
        when (state.successSend) {
            true -> {
                Toast.makeText(
                    context,
                    "Se han enviado las instrucciones, revise su correo",
                    Toast.LENGTH_LONG
                ).show()
                navController.popBackStack()
            }

            false -> {
                Toast.makeText(
                    context,
                    state.errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }

            null -> Unit
        }
    }

    val emailFocusRequester = remember { FocusRequester() }
    var emailTextValue by remember { mutableStateOf("") }
    var blankEmailError by remember { mutableStateOf(false) }
    var wrongEmailFormatError by remember { mutableStateOf(false) }

    val sendInstructions = {
        focus.clearFocus()

        if (emailTextValue.trim().isEmpty() || wrongEmailFormatError) {
            blankEmailError = true
            emailFocusRequester.requestFocus()
        } else {
            sendInstructions(emailTextValue)
        }
    }

    Scaffold() { it ->
        Column(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            //navController.navigate("email_login")
                            navController.popBackStack()
                        })
                Text(
                    text = "Olvide la contraseña",
                    modifier = Modifier.align(Alignment.Center),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            HorizontalDivider(thickness = 1.dp)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.icono_password),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .size(70.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            Text(
                text = "Ingrese su email para el envio de las instrucciones de recuperacion",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                    .focusRequester(emailFocusRequester),
                singleLine = true,
                value = emailTextValue,
                onValueChange = {
                    blankEmailError = it.trim().isEmpty()
                    wrongEmailFormatError = !Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    emailTextValue = it
                },
                supportingText = {
                    if (blankEmailError)
                        Text(
                            text = "Ingrese correo electronico",
                            color = MaterialTheme.colorScheme.error
                        )
                    else if (wrongEmailFormatError)
                        Text(
                            text = "Formato invalido",
                            color = MaterialTheme.colorScheme.error
                        )
                },
                isError = blankEmailError || wrongEmailFormatError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                label = {
                    Text("Correo electronico")
                })
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 10.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                        .align(Alignment.BottomCenter),
                    onClick = {
                        sendInstructions()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "ENVIAR")
                }
            }
        }
        if (state.inProgress) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(ProgressBackground)
                    .clickable(interactionSource = null, indication = null) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ForgotPasswordView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ForgotPasswordViewContent(
        state = uiState,
        modifier = modifier,
        navController = navController,
        sendInstructions = { email ->
            viewModel.sendInstructions(email)
        }
    )

}