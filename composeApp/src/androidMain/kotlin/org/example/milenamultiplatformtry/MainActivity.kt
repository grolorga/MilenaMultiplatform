package org.example.milenamultiplatformtry

import App
import ImagePickerFactory
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


class MainActivity : ComponentActivity() {

    companion object {
        lateinit var instance: MainActivity
            private set

        fun getAppContext(): Context = instance.applicationContext

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

        setContent {
            App(
                imagePicker = ImagePickerFactory().createPicker()
            )
        }
    }

}

