package com.example.doctor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.doctor.Model.ExpedienteData
import com.example.doctor.navigation.NavGraph
import com.example.doctor.Model.TVDataViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val tvDataViewModel: TVDataViewModel = viewModel()
            NavGraph(navController = navController, tvDataViewModel = tvDataViewModel)
        }
    }
}
