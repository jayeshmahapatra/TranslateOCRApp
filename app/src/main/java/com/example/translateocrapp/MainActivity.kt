package com.example.translateocrapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import android.hardware.Camera
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.widget.Toast
import android.view.SurfaceHolder
import android.widget.Button

// Import the required permissions
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {
    // This is the main activity of the app

    // Variable to store the SurfaceView object
    private var cameraView: SurfaceView? = null

    // Camera permission request code
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    private var camera: Camera? = null

    // Variable to keep track of permission request
    private var isPermissionRequested = false

    // Variable to store Button object
    private var captureButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check for camera permissions
        if (camera == null){
            checkPermissions()

        }


        // Initialize the SurfaceView object and connect it to the camera_view in the layout
        cameraView = findViewById(R.id.camera_view)
        cameraView?.holder?.addCallback(this)

        // Initialize the Button object and connect it to the capture_button in the layout
        captureButton = findViewById(R.id.capture_button)
        // Set an onClickListener on the captureButton to captureButtonClicked
        captureButton?.setOnClickListener {
            captureButtonClicked()
        }

    }

    // Function to check and request camera permissions
    private fun checkPermissions() {
        // Check if the camera permission is already available
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // If the permission is already available then set up the camera
            setupCamera()
        } else {
            // If the permission is not available and it is not being requested then request the permission
            if (!isPermissionRequested) {
                // Request the camera permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            }

            // Mark permission as requested
            isPermissionRequested = true

        }
    }

    // Function to handle the permission request response
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if the request code is for camera permission
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // Check if the permission is granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If the permission is granted then set up the camera
                setupCamera()
            } else {
                // If the permission is not granted then display a toast
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to set up the camera and connect it to the SurfaceView object
    private fun setupCamera() {
        // check if the device has a camera
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // If the device has a camera then get the camera object
            camera = Camera.open()

            // Set the camera orientation to portrait
            camera?.setDisplayOrientation(90)

            // Set the camera preview to the SurfaceView object
            try {
                camera?.setPreviewDisplay(cameraView?.holder)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Start the preview
            camera?.startPreview()

        } else {
            // If the device does not have a camera then display a toast
            Toast.makeText(this, "No camera detected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // The SurfaceView holder has been created
        // You can now set up the camera preview
        checkPermissions()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // The SurfaceView dimensions have changed
        // You might need to handle this event if necessary
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // The SurfaceView holder has been destroyed
        // You might need to handle this event if necessary

        // check if the camera is open
        if (camera != null) {
            // stop the preview
            camera?.stopPreview()

            // release the camera
            camera?.release()

            // set the camera object to null
            camera = null
        }
    }

    override fun onResume() {
        super.onResume()

        // check if camera is open, if not then check for permissions and set it up
        if (camera == null) {
            // Check for camera permissions
            checkPermissions()

            // If permission was requested and not granted, show a dialog or a message
            if (isPermissionRequested && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Show dialog or message to inform the user that camera permission is required
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onPause() {
        super.onPause()

        // check if the camera is open
        if (camera != null) {
            // stop the preview
            camera?.stopPreview()

            // release the camera
            camera?.release()

            // set the camera object to null
            camera = null
        }
    }

    // Function to handle the capture button click
    fun captureButtonClicked() {
        // Capture button has been clicked

        // Check if the camera is open
        if (camera != null) {
            // If the camera is open then take a picture
            camera?.takePicture(null, null, pictureCallback)


        }
    }

    // Create a picture callback object
    private val pictureCallback = Camera.PictureCallback { data, camera ->
        // The picture has been taken

        // Convert the picture data to a bitmap
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

        // Create an intent to start PreviewActivity
        val intent = Intent(this, PreviewActivity::class.java)

        // Pass the captured image bitmap to PreviewActivity
        intent.putExtra("capturedImage", bitmap)

        // Start PreviewActivity
        startActivity(intent)
    }
}
