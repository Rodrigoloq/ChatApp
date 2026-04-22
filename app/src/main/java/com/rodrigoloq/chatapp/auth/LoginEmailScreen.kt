package com.rodrigoloq.chatapp.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rodrigoloq.chatapp.R
import com.rodrigoloq.chatapp.auth.viewmodel.LoginEmailViewModel
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme
import com.rodrigoloq.chatapp.ui.theme.ProgressBackground
import kotlin.text.trim

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginEmailViewPreview(){
    ChatAppTheme() {
        LoginEmailView(navController = rememberNavController())
    }
}

@Composable
fun LoginEmailView(modifier: Modifier = Modifier,
                   navController: NavController,
                   viewModel: LoginEmailViewModel = viewModel()){
    val context = LocalContext.current
    val focus = LocalFocusManager.current

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    var emailTextValue by remember { mutableStateOf("") }
    var blankEmailError by remember { mutableStateOf(false) }
    var wrongEmailFormatError by remember { mutableStateOf(false) }

    var passwordTextValue by remember { mutableStateOf("") }
    var blankPasswordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val inProgress = viewModel.inProgress

    val login = {
        focus.clearFocus()
        if(emailTextValue.trim().isEmpty() || wrongEmailFormatError){
            blankEmailError = true
            emailFocusRequester.requestFocus()
        } else if(passwordTextValue.trim().isEmpty()){
            blankPasswordError = true
            passwordFocusRequester.requestFocus()
        } else {
            viewModel.loginUser(emailTextValue,
                passwordTextValue){onLogin, errorMsg ->
                if(!onLogin){
                    Toast.makeText(context,errorMsg, Toast.LENGTH_SHORT).show()
                } else {
                    navController.navigate("main"){
                        popUpTo("auth") { inclusive = true }
                    }
                }
            }
        }
    }

    Scaffold() { it ->
        Column(modifier = modifier
            .fillMaxSize()
            .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                text = "LOGIN")
            Image(modifier = Modifier.width(100.dp)
                .padding(top = 15.dp),
                painter = painterResource(R.drawable.login_usuario),
                contentDescription = null)
            OutlinedTextField(modifier = Modifier.fillMaxWidth()
                .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                .focusRequester(emailFocusRequester),
                singleLine = true,
                value = emailTextValue,
                onValueChange = {
                    blankEmailError = it.trim().isEmpty()
                    wrongEmailFormatError = !Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    emailTextValue = it },
                supportingText = {
                    if(blankEmailError)
                        Text(text = "Ingrese correo electronico",
                            color = MaterialTheme.colorScheme.error)
                    else if(wrongEmailFormatError)
                        Text(text = "Formato invalido",
                            color = MaterialTheme.colorScheme.error)
                },
                isError = blankEmailError || wrongEmailFormatError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                label = {
                    Text("Correo electronico")
                })
            OutlinedTextField(value = passwordTextValue,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
                    .focusRequester(passwordFocusRequester),
                singleLine = true,
                onValueChange = {
                    blankPasswordError = it.trim().isEmpty()
                    passwordTextValue = it },
                supportingText = {
                    if(blankPasswordError)
                        Text(text = "Ingrese contraseña",
                            color = MaterialTheme.colorScheme.error)
                },
                isError = blankPasswordError,
                label = {
                    Text("Contraseña")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if(passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = {passwordVisible = !passwordVisible}
                    ){
                        if(passwordVisible){
                            Icon(Icons.Default.VisibilityOff,
                                contentDescription = null)
                        } else{
                            Icon(Icons.Default.Visibility,
                                contentDescription = null)
                        }
                    }
                })
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End) {
                Text(text = "¿Olvidaste la contraseña?",
                    modifier = Modifier.padding(end = 4.dp))
                Text(text = "Recuperar",
                    modifier = Modifier.clickable{
                        navController.navigate("forgot_password")
                    }.padding(end = 10.dp),
                    fontWeight = FontWeight.Bold)
            }

            Button(modifier = Modifier.width(250.dp)
                .padding(top = 15.dp),
                onClick = {
                    login()
                },
                shape = RoundedCornerShape(8.dp)) {
                Text(text = "INGRESAR")
            }
            Text(modifier = Modifier.padding(top = 20.dp),
                text = "¿No tienes una cuenta?",
                fontSize = 15.sp)
            Text(modifier = Modifier.padding(top = 10.dp)
                .clickable{
                    navController.navigate("register")
                },
                text = "REGISTRARME", fontWeight = FontWeight.Bold)
        }
        if(inProgress){
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