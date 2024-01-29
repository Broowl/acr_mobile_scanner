package com.acr_mobile_scanner


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.navigation.fragment.findNavController


class ScannerFragment : Fragment() {
    private var _cameraExecutor: ExecutorService? = null
    private var _barcodeScanner: BarcodeScanner? = null
    private val _viewModel: EntityViewModel by activityViewModels()
    private var _camera: Camera? = null
    private var _previewView: PreviewView? = null
    private var _valid = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)
        _previewView = view.findViewById(R.id.previewView)
        _previewView!!.setOnTouchListener { view, event ->
            view.performClick()
            return@setOnTouchListener onTouch(event)
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                requireContext(),
                "Camera permission denied. Cannot scan QR codes.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            startCamera()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _cameraExecutor = Executors.newSingleThreadExecutor()
        val options: BarcodeScannerOptions = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        _barcodeScanner = BarcodeScanning.getClient(options)
    }

    private fun startCamera() {
        val resolution = Size(1280, 720)
        val cameraProvider: ProcessCameraProvider =
            ProcessCameraProvider.getInstance(requireContext()).get()
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
            scanQrCode(imageProxy)
        }
        _camera = cameraProvider.bindToLifecycle(
            requireContext() as LifecycleOwner,
            cameraSelector,
            preview,
            imageCapture,
            imageAnalysis
        )
    }

    private fun onBarcodeScanned(barCodes: List<Barcode>) {
        if (barCodes.isEmpty()) {
            return
        }
        val barcode = barCodes[0]
        if (!_valid) { // make sure that the code is only scanned once
            return
        }
        _valid = false
        val scannedData: String = barcode.rawValue!!
        val scanResult = _viewModel.scanner!!.processQrCode(scannedData)
        if (scanResult.isSuccess) {
            findNavController().navigate(R.id.action_ScannerFragment_to_ResultSuccessFragment)
        } else {
            setFragmentResult("scan_result", bundleOf("message" to scanResult.errorMessage))
            findNavController().navigate(R.id.action_ScannerFragment_to_ResultErrorFragment)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun scanQrCode(imageProxy: ImageProxy) {
        val image: InputImage = InputImage.fromMediaImage(
            imageProxy.image!!,
            imageProxy.imageInfo.rotationDegrees
        )
        _barcodeScanner!!.process(image)
            .addOnSuccessListener { barCodes ->
                imageProxy.toBitmap()
                onBarcodeScanned(barCodes)
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    requireContext(),
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnCompleteListener { _ -> imageProxy.close() }

    }

    override fun onDestroy() {
        super.onDestroy()
        _cameraExecutor!!.shutdown()
    }
}