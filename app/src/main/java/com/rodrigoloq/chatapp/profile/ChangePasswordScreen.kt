package com.rodrigoloq.chatapp.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.rodrigoloq.chatapp.profile.viewmodel.ChangePasswordViewModel
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme
import com.rodrigoloq.chatapp.ui.theme.ProgressBackground
import kotlin.text.trim

@Preview
@Composable
fun ChangePasswordViewPreview(){
    ChatAppTheme() {
        ChangePasswordView(Modifier, rememberNavController())
    }
}

@Composable
fun ChangePasswordView(modifier: Modifier = Modifier,
                       navController: NavController,
                       viewModel: ChangePasswordViewModel = viewModel()){
    val context = LocalContext.current

    val focus = LocalFocusManager.current
    var inProgress = viewModel.inProgress

    val passwordFocusRequester = remember { FocusRequester() }
    val newPasswordFocusRequester = remember { FocusRequester() }
    val rPasswordFocusRequester = remember { FocusRequester() }

    var passwordTextValue by remember { mutableStateOf("") }
    var blankPasswordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    var newPasswordTextValue by remember { mutableStateOf("") }
    var blankNewPasswordError by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }

    var rPasswordTextValue by remember { mutableStateOf("") }
    var blankRPasswordError by remember { mutableStateOf(false) }
    var passwordsNotMatchError by remember { mutableStateOf(false) }
    var rPasswordVisible by remember { mutableStateOf(false) }

    val changePassword = {
        focus.clearFocus()
        if(passwordTextValue.trim().isEmpty()){
            passwordFocusRequester.requestFocus()
        }else if (newPasswordTextValue.trim().isEmpty()){
            newPasswordFocusRequester.requestFocus()
        }else if(rPasswordTextValue.trim().isEmpty() || passwordsNotMatchError){
            rPasswordFocusRequester.requestFocus()
        }else{
            viewModel.authUser(passwordTextValue){ success, errorMsg ->
                if(!success){
                    Toast.makeText(context,
                        "Fallo la autenticacion debido a $errorMsg",
                        Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.updatePassword(newPasswordTextValue){ success, errorMsg ->
                        if(!success){
                            Toast.makeText(context,
                                "Fallo el cambio de contraseña debido a $errorMsg",
                                Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context,
                                "La contraseña se ha actualizado",
                                Toast.LENGTH_SHORT).show()
                            viewModel.signOut()
                            navController.navigate("auth") {
                                popUpTo("main") { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold() { it ->
        Column(modifier = Modifier.padding(it)
            .fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp)
                        .clickable{
                            //navController.navigate("email_login")
                            navController.popBackStack()
                        })
                Text(text = "Cambiar contraseña",
                    modifier = Modifier.align(Alignment.Center),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp)
            }
            HorizontalDivider(thickness = 1.dp)
            OutlinedTextField(value = passwordTextValue,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp)
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
                    Text("Contraseña actual")
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
            OutlinedTextField(value = newPasswordTextValue,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
                    .focusRequester(newPasswordFocusRequester),
                onValueChange = {
                    blankNewPasswordError = it.trim().isEmpty()
                    newPasswordTextValue = it },
                supportingText = {
                    if(blankNewPasswordError)
                        Text(text = "Ingrese contraseña",
                            color = MaterialTheme.colorScheme.error)
                },
                isError = blankNewPasswordError,
                label = {
                    Text("Nueva contraseña")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if(newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = {newPasswordVisible = !newPasswordVisible}
                    ){
                        if(newPasswordVisible){
                            Icon(Icons.Default.VisibilityOff,
                                contentDescription = null)
                        } else{
                            Icon(Icons.Default.Visibility,
                                contentDescription = null)
                        }
                    }
                },
                singleLine = true)
            OutlinedTextField(value = rPasswordTextValue,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
                    .focusRequester(rPasswordFocusRequester),
                onValueChange = {
                    blankRPasswordError = it.trim().isEmpty()
                    rPasswordTextValue = it
                    passwordsNotMatchError = newPasswordTextValue != rPasswordTextValue},
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
                    Text("Repita la nueva contraseña")
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
            Box(modifier = Modifier.fillMaxHeight()
                .padding(bottom = 10.dp)) {
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
                    .align(Alignment.BottomCenter),
                    onClick = {
                        changePassword()
                    },
                    shape = RoundedCornerShape(8.dp)) {
                    Text(text = "CAMBIAR CONTRASEÑA")
                }
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