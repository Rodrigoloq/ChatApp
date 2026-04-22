package com.rodrigoloq.chatapp.register

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
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
import com.rodrigoloq.chatapp.register.viewmodel.RegisterViewModel
import com.rodrigoloq.chatapp.ui.theme.ProgressBackground


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterViewPreview(){
    RegisterView(modifier = Modifier, navController = rememberNavController())
}

@Composable
fun RegisterView(modifier: Modifier = Modifier,
                 navController: NavController,
                 viewModel: RegisterViewModel = viewModel()){
    val context = LocalContext.current

    val focus = LocalFocusManager.current
    val nameFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val rPasswordFocusRequester = remember { FocusRequester() }

    var inProgress = viewModel.inProgress

    var nameTextValue by remember { mutableStateOf("") }
    var blankNameError by remember { mutableStateOf(false) }

    var emailTextValue by remember { mutableStateOf("") }
    var blankEmailError by remember { mutableStateOf(false) }
    var wrongEmailFormatError by remember { mutableStateOf(false) }

    var passwordTextValue by remember { mutableStateOf("") }
    var blankPasswordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    var rPasswordTextValue by remember { mutableStateOf("") }
    var blankRPasswordError by remember { mutableStateOf(false) }
    var passwordsNotMatchError by remember { mutableStateOf(false) }
    var rPasswordVisible by remember { mutableStateOf(false) }

    val register = {
        focus.clearFocus()
        if(nameTextValue.trim().isEmpty()){
            blankNameError = true
            nameFocusRequester.requestFocus()
        } else if(emailTextValue.trim().isEmpty() || wrongEmailFormatError){
            blankEmailError = true
            emailFocusRequester.requestFocus()
        } else if(passwordTextValue.trim().isEmpty()){
            blankPasswordError = true
            passwordFocusRequester.requestFocus()
        } else if(rPasswordTextValue.trim().isEmpty() || passwordsNotMatchError){
            blankRPasswordError = true
            rPasswordFocusRequester.requestFocus()
        } else {
            viewModel.registerUser(emailTextValue,passwordTextValue){onRegister, errorMsg ->
                if(!onRegister){
                    Toast.makeText(context,errorMsg, Toast.LENGTH_SHORT).show()
                }else{
                   viewModel.updateUserInfo(nameTextValue){onUpdate, errorMsg ->
                       if(!onUpdate){
                           Toast.makeText(context,errorMsg, Toast.LENGTH_SHORT).show()
                       } else {
                           navController.navigate("main"){
                               popUpTo("auth") { inclusive = true }
                           }
                       }
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
                text = "REGISTRO")
            Image(modifier = Modifier.width(100.dp)
                .padding(top = 15.dp),
                painter = painterResource(R.drawable.registro_usuario),
                contentDescription = null)
            //nombres
            OutlinedTextField(modifier = Modifier.fillMaxWidth()
                .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                .focusRequester(nameFocusRequester),
                value = nameTextValue,
                onValueChange = {
                    blankNameError = it.trim().isEmpty()
                    nameTextValue = it },
                supportingText = {
                    if(blankNameError)
                        Text(text = "Ingrese nombre/s",
                            color = MaterialTheme.colorScheme.error)
                },
                isError = blankNameError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                label = {
                    Text("Nombres")
                },
                singleLine = true)
            //correo
            OutlinedTextField(modifier = Modifier.fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .focusRequester(emailFocusRequester),
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
                },
                singleLine = true)
            //contraseña
            OutlinedTextField(value = passwordTextValue,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
                    .focusRequester(passwordFocusRequester),
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
                },
                singleLine = true)
            //repetir contraseña
            OutlinedTextField(value = rPasswordTextValue,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
                    .focusRequester(rPasswordFocusRequester),
                onValueChange = {
                    blankRPasswordError = it.trim().isEmpty()
                    rPasswordTextValue = it
                    passwordsNotMatchError = passwordTextValue != rPasswordTextValue},
                supportingText = {
                    if(blankRPasswordError)
                        Text(text = "Repita contraseña",
                            color = MaterialTheme.colorScheme.error)
                    else if(passwordsNotMatchError)
                        Text(text = "Las contraseñas no coinciden",
                            color = MaterialTheme.colorScheme.error)
                },
                isError = blankRPasswordError || passwordsNotMatchError,
                label = {
                    Text("Repetir contraseña")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if(rPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = {rPasswordVisible = !rPasswordVisible}
                    ){
                        if(rPasswordVisible){
                            Icon(Icons.Default.VisibilityOff,
                                contentDescription = null)
                        } else{
                            Icon(Icons.Default.Visibility,
                                contentDescription = null)
                        }
                    }
                },
                singleLine = true)

            Button(modifier = Modifier.width(250.dp)
                .padding(top = 15.dp),
                onClick = {
                    register()
                },
                shape = RoundedCornerShape(8.dp)) {
                Text(text = "REGISTRAR")
            }
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