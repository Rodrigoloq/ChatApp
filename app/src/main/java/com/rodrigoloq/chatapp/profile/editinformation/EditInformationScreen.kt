package com.rodrigoloq.chatapp.profile.editinformation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.rodrigoloq.chatapp.R
import com.rodrigoloq.chatapp.profile.editinformation.viewmodel.EditInformationUIState
import com.rodrigoloq.chatapp.profile.editinformation.viewmodel.EditInformationViewModel
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme
import com.rodrigoloq.chatapp.ui.theme.ProgressBackground
import kotlin.text.trim

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditInformationViewPreview() {
    ChatAppTheme() {
        EditInformationViewContent(
            state = EditInformationUIState(),
            modifier = Modifier,
            navController = rememberNavController(),
            loadUserInfo = { },
            updateNames = { }
        ) { }
    }
}

@Composable
fun EditInformationViewContent(
    state: EditInformationUIState,
    modifier: Modifier,
    navController: NavController,
    loadUserInfo: () -> Unit,
    updateNames: (String) -> Unit,
    updateProfileImage: (Uri) -> Unit
) {
    val context = LocalContext.current
    val focus = LocalFocusManager.current

    LaunchedEffect(Unit) {
        loadUserInfo()
    }

    LaunchedEffect(state.loadUserSuccess) {
        when (state.loadUserSuccess) {
            true -> Unit
            false -> {
                Toast.makeText(
                    context,
                    state.loadUserErrorMsg,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    LaunchedEffect(state.updateNamesSuccess) {
        when (state.updateNamesSuccess) {
            true -> {
                Toast.makeText(
                    context,
                    "Se ha actualizado correctamente su informacion",
                    Toast.LENGTH_SHORT
                ).show()
                navController.popBackStack()
            }

            false -> {
                Toast.makeText(
                    context,
                    state.loadUserErrorMsg,
                    Toast.LENGTH_SHORT
                ).show()
            }

            null -> Unit
        }
    }

    LaunchedEffect(state.updateImageSuccess) {
        when (state.updateImageSuccess) {
            true -> {
                Toast.makeText(
                    context,
                    "Se ha actualizado correctamente su informacion",
                    Toast.LENGTH_SHORT
                ).show()
                navController.popBackStack()
            }

            false -> {
                Toast.makeText(
                    context,
                    state.updateImageErrorMsg,
                    Toast.LENGTH_SHORT
                ).show()
            }

            null -> Unit
        }
    }

    val nameFocusRequester = remember { FocusRequester() }
    var nameTextValue by remember(state.user) { mutableStateOf(state.user.names) }
    var imageValue by remember(state.user) { mutableStateOf(state.user.image) }
    var blankNameError by remember { mutableStateOf(false) }

    val updateInfo = {
        focus.clearFocus()
        if (blankNameError) {
            nameFocusRequester.requestFocus()
        } else {
//            viewModel.updateInfoNames(nameTextValue){errorMsg ->
//                if(errorMsg == null){
//                    Toast.makeText(context,
//                        "Se actualizo su informacion",
//                        Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(context,
//                        errorMsg,
//                        Toast.LENGTH_SHORT).show()
//                }
//            }
            updateNames(nameTextValue)
        }
    }

    val galleryARL = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val imageUri = data!!.data
            if (imageUri != null) {
                updateProfileImage(imageUri)
            }
        } else {
            Toast.makeText(
                context,
                "Cancelado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val openGallery = {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryARL.launch(intent)
    }

    val requestStoragePermit = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isAllowed ->
        if (isAllowed) {
            openGallery()
        } else {
            Toast.makeText(
                context,
                "Permiso de almacenamiento denegado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val updateImage = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            openGallery()
        } else {
            requestStoragePermit.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    val updateImageToDB = {
        focus.clearFocus()
        updateImage()
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
                            //navController.navigate("main")
                            navController.popBackStack()
                        })
                Text(
                    text = "Editar informacion",
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
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageValue)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_img_profile),
                    error = rememberVectorPainter(Icons.Default.BrokenImage),
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(120.dp)
                )
                Image(
                    painter = painterResource(R.drawable.icono_editar),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(50.dp)
                        .align(Alignment.TopEnd)
                        .clickable {
                            updateImageToDB()
                        }
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                    .focusRequester(nameFocusRequester),
                singleLine = true,
                value = nameTextValue,
                onValueChange = {
                    blankNameError = it.trim().isEmpty()
                    nameTextValue = it
                },
                supportingText = {
                    if (blankNameError)
                        Text(
                            text = "Ingrese nombres",
                            color = MaterialTheme.colorScheme.error
                        )
                },
                isError = blankNameError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = {
                    Text("Nombres")
                })
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                onClick = {
                    updateInfo()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "ACTUALIZAR")
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
fun EditInformationView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: EditInformationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    EditInformationViewContent(
        state = uiState,
        modifier = modifier,
        navController = navController,
        loadUserInfo = {
            viewModel.loadUserInfo()
        },
        updateNames = { names ->
            viewModel.updateNames(names)
        },
        updateProfileImage = { uri ->
            viewModel.updateProfileImage(uri)
        }
    )
}
