package com.rodrigoloq.chatapp.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.rodrigoloq.chatapp.R
import com.rodrigoloq.chatapp.profile.viewmodel.ProfileViewModel
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme
import com.rodrigoloq.chatapp.utis.Utils

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileViewPreview(){
    ChatAppTheme() {
        ProfileView(Modifier, rememberNavController())
    }
}

@Composable
fun ProfileView(modifier: Modifier = Modifier,
                navController: NavController,
                viewModel: ProfileViewModel = viewModel()){
    val context = LocalContext.current
    val user = viewModel.userData
    val isLoading = viewModel.isLoading

    val givePermission =
        rememberLauncherForActivityResult(ActivityResultContracts
            .RequestPermission()){ isAllowed ->
            //
        }

    val requestNotificationPermission = {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_DENIED){
                givePermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadUserInfo(){errorMsg ->
            if(errorMsg != null){
                Toast.makeText(context,errorMsg,Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.addToken(){errorToken, errorDB ->
            if(errorToken != null){
                Toast.makeText(context,errorToken,Toast.LENGTH_SHORT).show()
            } else if (errorDB != null){
                Toast.makeText(context,errorDB,Toast.LENGTH_SHORT).show()
            }
        }
        requestNotificationPermission()
    }

    var formatedDate by remember { mutableStateOf("") }
    formatedDate = Utils().dateFormater(user?.rTime?.toLong() ?: 0, false)

    var providerType by remember { mutableStateOf(false) }
    providerType = user?.provider != "Google"

    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally){
        Box(modifier = Modifier.padding(top = 20.dp)){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user?.image ?: "")
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                placeholder = rememberVectorPainter(Icons.Default.HourglassEmpty),
                error = painterResource(R.drawable.ic_img_profile),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(120.dp)
            )
        }
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(modifier = Modifier.padding(top = 20.dp),
                text = "Nombres:",
                fontSize = 15.sp)
            Text(modifier = Modifier.padding(top = 20.dp),
                text = if (isLoading) "Cargando datos" else user?.names ?: "--",
                fontSize = 15.sp)
        }
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(modifier = Modifier.padding(top = 20.dp),
                text = "Email:",
                fontSize = 15.sp)
            Text(modifier = Modifier.padding(top = 20.dp),
                text = if (isLoading) "Cargando datos" else user?.email ?: "--",
                fontSize = 15.sp)
        }
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(modifier = Modifier.padding(top = 20.dp),
                text = "Proveedor:",
                fontSize = 15.sp)
            Text(modifier = Modifier.padding(top = 20.dp),
                text = if (isLoading) "Cargando datos" else user?.provider ?: "--",
                fontSize = 15.sp)
        }
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(modifier = Modifier.padding(top = 20.dp),
                text = "Miembro desde:",
                fontSize = 15.sp)
            Text(modifier = Modifier.padding(top = 20.dp),
                text = if (isLoading) "Cargando datos" else
                    if(formatedDate == "31/12/1969") "--" else formatedDate ,
                fontSize = 15.sp)
        }
        if(providerType){
            Button(modifier = Modifier
                .width(250.dp)
                .padding(top = 25.dp),
                onClick = {
                    navController.navigate("edit")
                },
                shape = RoundedCornerShape(8.dp)) {
                Text(text = "ACTUALIZAR")
            }
            Button(modifier = Modifier
                .width(250.dp)
                .padding(top = 10.dp),
                onClick = {
                    navController.navigate("change_password")
                },
                shape = RoundedCornerShape(8.dp)) {
                Text(text = "CAMBIAR CONTRASEÑA")
            }
        }
        Button(modifier = Modifier
            .width(250.dp)
            .padding(top = if(providerType) 10.dp else 25.dp),
            onClick = {
                viewModel.signOut()
                navController.navigate("auth") {
                    popUpTo("main") { inclusive = true }
                }
            },
            shape = RoundedCornerShape(8.dp)) {
            Text(text = "CERRAR SESION")
        }
    }
}