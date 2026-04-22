package com.rodrigoloq.chatapp.navigationbar

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rodrigoloq.chatapp.navigation.AppNavHost
import com.rodrigoloq.chatapp.navigation.Destination
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NavigationBarViewPreview(){
    ChatAppTheme() {
        NavigationBarView(navController = rememberNavController())
    }
}

@Composable
fun NavigationBarView(navController: NavController){
    val innerNavController = rememberNavController()
    val startDestination = Destination.PROFILE
    var selectedDestination by rememberSaveable{ mutableIntStateOf(startDestination.ordinal) }

    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            innerNavController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(destination.icon, destination.contentDescription)
                        },
                        label = {
                            Text(destination.label)
                        }
                    )
                }
            }
        }) { it ->
        AppNavHost(innerNavController,
            navController,
            startDestination,
            Modifier.padding(it))
    }
}