package com.example.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import androidx.camera.core.Preview
import com.example.escaner.R

class MainActivity : AppCompatActivity() {
    private val permissions = arrayOf(Manifest.permission.CAMERA)
    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true) {
            startCamera()
        } else {
            // Permission denied
            // Handle it here (e.g., show a message to the user)
        }
    }

    private lateinit var cameraExecutor: Executor
    private lateinit var imageCapture: ImageCapture
    private lateinit var previewView: PreviewView
    private lateinit var outputDirectory: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan)

        previewView = findViewById(R.id.previewView)

        val takePictureButton: Button = findViewById(R.id.Bscanear)
        takePictureButton.setOnClickListener {
            checkCameraPermission()
        }

        cameraExecutor = ContextCompat.getMainExecutor(this)
        outputDirectory = getOutputDirectory()
    }

    private fun checkCameraPermission() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            cameraPermissionLauncher.launch(permissions)
        }
    }

    private fun allPermissionsGranted() = permissions.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        // Initialize the camera and capture use cases
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder().build()

            val preview = Preview.Builder().build()

            // Para conectar la vista previa al PreviewView
            preview.setSurfaceProvider(previewView.surfaceProvider)

            val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, cameraExecutor)
    }

    private fun takePhoto() {
        val imageCapture = imageCapture

        // Crear un archivo para la imagen capturada
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".png"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = photoFile.toUri()
                    val msg = "Photo capture succeeded: $savedUri"
                    runOnUiThread {
                        showToast(msg)
                        // Pasar la URI de la imagen a la actividad de edición
                        val intent = Intent(this@MainActivity, ImageEdittActivity::class.java)

                        intent.putExtra("imageUri", savedUri.toString())
                        startActivity(intent)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    val msg = "Photo capture failed: ${exception.message}"
                    runOnUiThread {
                        showToast(msg)
                    }
                }
            }
        )
    }


    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
