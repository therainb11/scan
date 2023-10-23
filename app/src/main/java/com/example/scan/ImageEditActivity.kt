package com.example.scan

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.escaner.R
import com.example.scan.ui.theme.ScanTheme

class ImageEdittActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit)

        val imageUri = intent.getStringExtra("imageUri")

        val imageView: ImageView = findViewById(R.id.imageView)


        // Cargar la imagen desde la URI
        if (!imageUri.isNullOrEmpty()) {
            val uri = Uri.parse(imageUri)
            imageView.setImageURI(uri)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ScanTheme {
        Greeting("Android")
    }
}