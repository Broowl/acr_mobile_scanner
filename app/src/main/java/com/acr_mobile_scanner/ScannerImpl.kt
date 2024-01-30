package com.acr_mobile_scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Size
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.MeteringPointFactory
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerImpl {
    private var _cameraExecutor: ExecutorService? = null
    private var _barcodeScanner: BarcodeScanner? = null
    private var _camera: Camera? = null
    private var _previewView: PreviewView? = null

    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        context: Context,
        onBarcodeScanned: (codes: List<Barcode>) -> Unit
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)
        _previewView = view.findViewById(R.id.previewView)
        _previewView!!.setOnTouchListener { view, event ->
            view.performClick()
            return@setOnTouchListener onTouch(event)
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                context,
                "Camera permission denied. Cannot scan QR codes.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            startCamera(context, onBarcodeScanned)
        }

        return view
    }

    private fun onTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                true
            }

            MotionEvent.ACTION_UP -> {
                autoFocus(event)
                true
            }

            else -> false // Unhandled event.
        }
        return false
    }

    private fun autoFocus(event: MotionEvent) {
        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
            _previewView!!.width.toFloat(), _previewView!!.height.toFloat()
        )
        val autoFocusPoint = factory.createPoint(event.x, event.y)
        try {
            _camera!!.cameraControl.startFocusAndMetering(
                FocusMeteringAction.Builder(
                    autoFocusPoint,
                    FocusMeteringAction.FLAG_AF
                ).apply {
                    //focus only when the user tap the preview
                    disableAutoCancel()
                }.build()
            )
        } catch (e: CameraInfoUnavailableException) {
        }
    }

    fun onCreate() {
        _cameraExecutor = Executors.newSingleThreadExecutor()
        val options: BarcodeScannerOptions = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        _barcodeScanner = BarcodeScanning.getClient(options)
    }

    private fun startCamera(context: Context, onBarcodeScanned: (codes: List<Barcode>) -> Unit) {
        val resolution = Size(1280, 720)
        val cameraProvider: ProcessCameraProvider =
            ProcessCameraProvider.getInstance(context).get()
        cameraProvider.unbindAll()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        val preview = Preview.Builder()
            .setTargetResolution(resolution)
            .build()
        preview.setSurfaceProvider(_previewView!!.surfaceProvider)
        val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(resolution)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        val imageCapture = ImageCapture.Builder()
            .setTargetResolution(resolution)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
        imageAnalysis.setAnalyzer(_cameraExecutor!!) { imageProxy ->
            scanQrCode(imageProxy, context, onBarcodeScanned)
        }
        _camera = cameraProvider.bindToLifecycle(
            context as LifecycleOwner,
            cameraSelector,
            preview,
            imageCapture,
            imageAnalysis
        )
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun scanQrCode(
        imageProxy: ImageProxy,
        context: Context,
        onBarcodeScanned: (codes: List<Barcode>) -> Unit
    ) {
        val image: InputImage = InputImage.fromMediaImage(
            imageProxy.image!!,
            imageProxy.imageInfo.rotationDegrees
        )
        _barcodeScanner!!.process(image)
            .addOnSuccessListener { barCodes ->
                onBarcodeScanned(barCodes)
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    context,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnCompleteListener { _ -> imageProxy.close() }
    }

    fun onDestroy() {
        _cameraExecutor!!.shutdown()
    }
}