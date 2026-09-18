package com.rodrigoloq.chatapp.auth.login

import android.app.Activity
import android.content.Context
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.rodrigoloq.chatapp.BuildConfig
import com.rodrigoloq.chatapp.R
import com.rodrigoloq.chatapp.auth.login.viewmodel.LoginUIState
import com.rodrigoloq.chatapp.auth.model.AuthRepository
import com.rodrigoloq.chatapp.auth.login.viewmodel.LoginViewModel
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme
import com.rodrigoloq.chatapp.ui.theme.ProgressBackground

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginViewPreview(){
    ChatAppTheme() {
        LoginViewContent(
            state = LoginUIState(),
            modifier = Modifier,
            navController = rememberNavController(),
            authGoogleAccount = {}
        ) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("idToken")
                .requestEmail()
                .build()
            GoogleSignIn.getClient(it, gso)
        }
    }
}

@Composable
fun LoginViewContent(state: LoginUIState,
                     modifier: Modifier = Modifier,
                     navController: NavController,
                     authGoogleAccount: (String?) -> Unit,
                     getGoogleSignInClient:(Context) -> GoogleSignInClient){
    val context = LocalContext.current

    LaunchedEffect(state.successAuth) {
        when(state.successAuth){
            true -> {
                navController.navigate("main"){
                    popUpTo("auth") { inclusive = true } }
            }
            false -> {
                Toast.makeText(context,
                    state.errorMessage,
                    Toast.LENGTH_SHORT).show()
            }
            null -> Unit
        }
    }

    val googleSignInClient = remember {
        getGoogleSignInClient(context)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                authGoogleAccount(account.idToken)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
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
        val signInIntent = googleSignInClient.signInIntent
        googleSignInClient.signOut().addOnCompleteListener {
            launcher.launch(signInIntent)
        }
    }

    Scaffold() {it ->
        Column(modifier = modifier
            .fillMaxSize()
            .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(modifier = Modifier.width(80.dp),painter = painterResource(R.drawable.icono_chat),
                contentDescription = null)
            Button(modifier = Modifier
                .width(250.dp)
                .padding(top = 35.dp),
                onClick = { navController.navigate("email_login") }) {
                Text(text = "CONTINUAR CON EMAIL")
            }
            Button(modifier = Modifier.width(250.dp),
                onClick = {
                    login()
                }) {
                Text(text = "CONTINUAR CON GOOGLE")
            }
        }
        if(state.inProgress){
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
fun LoginView(modifier: Modifier = Modifier,
              navController: NavController,
              viewModel: LoginViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    LoginViewContent(state = uiState,
        modifier = modifier,
        navController = navController,
        authGoogleAccount = {idToken ->
            viewModel.authGoogleAccount(idToken)
        }) {context ->
        val gso = viewModel.getGoogleSignInOptions()
        GoogleSignIn.getClient(context, gso)
    }
}