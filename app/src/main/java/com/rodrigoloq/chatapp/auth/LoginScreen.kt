package com.rodrigoloq.chatapp.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.rodrigoloq.chatapp.R
import com.rodrigoloq.chatapp.auth.model.AuthRepository
import com.rodrigoloq.chatapp.auth.viewmodel.LoginViewModel
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme
import com.rodrigoloq.chatapp.ui.theme.ProgressBackground

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginViewPreview(){
    ChatAppTheme() {
        LoginView(navController = rememberNavController())
    }
}

@Composable
fun LoginView(modifier: Modifier = Modifier,
              navController: NavController,
              viewModel: LoginViewModel = viewModel()){
    val context = LocalContext.current
    var inProgress = viewModel.inProgress
    val googleSignInClient = remember {
        AuthRepository().getGoogleSignInClient(context)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.authGoogleAccount(idToken = account.idToken,
                    onAuth = { successAuth, errorMsg, isNewUser ->
                        if(successAuth){
                            if(isNewUser){
                                viewModel.updateUser(){onUpdate, errorMsg ->
                                    if(!onUpdate){
                                        Toast.makeText(context,
                                            errorMsg,
                                            Toast.LENGTH_SHORT).show()
                                    } else {
                                        navController.navigate("main"){
                                            popUpTo("auth") { inclusive = true }
                                        }
                                    }
                                }
                            } else {
                                navController.navigate("main"){
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        }else{
                            Toast.makeText(
                                context,
                                errorMsg,
                                Toast.LENGTH_SHORT
                            ).show()
                            inProgress = false
                        }
                    })
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                inProgress = false
            }
        } else {
            Toast.makeText(
                context,
                "Cancelado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val login = {
        inProgress = true
        val signInIntent = googleSignInClient.signInIntent
        googleSignInClient.signOut().addOnCompleteListener {
            launcher.launch(signInIntent)
        }
    }

    Scaffold() {it ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(modifier = Modifier.width(80.dp),painter = painterResource(R.drawable.icono_chat),
                contentDescription = null)
            Button(modifier = Modifier.width(250.dp)
                .padding(top = 35.dp),
                onClick = {navController.navigate("email_login")}) {
                Text(text = "CONTINUAR CON EMAIL")
            }
            Button(modifier = Modifier.width(250.dp),
                onClick = {
                    login()
                }) {
                Text(text = "CONTINUAR CON GOOGLE")
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