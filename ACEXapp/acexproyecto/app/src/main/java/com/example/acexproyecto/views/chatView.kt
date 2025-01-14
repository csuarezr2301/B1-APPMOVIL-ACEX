package com.example.acexproyecto.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun chatView(navController: NavController) {
    Scaffold(
        topBar = {TopBar(navController)},
        content = {

        },
        bottomBar = { BottomDetailBar(navController) }
    )
}


@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    val navController = rememberNavController()
    chatView(navController)
}
