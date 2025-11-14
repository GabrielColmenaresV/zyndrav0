package com.example.zyndrav0

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.zyndrav0.navigation.AppNavigation
import com.example.zyndrav0.ui.theme.ZyndraV0Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZyndraV0Theme {
                AppNavigation()
            }
        }
    }
}