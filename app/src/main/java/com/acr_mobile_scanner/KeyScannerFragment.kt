package com.acr_mobile_scanner


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
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
import java.util.Date


class KeyScannerFragment : Fragment() {
    private var _cameraExecutor: ExecutorService? = null
    private var _barcodeScanner: BarcodeScanner? = null
    private var _camera: Camera? = null
    private var _previewView: PreviewView? = null
    private var _lastScan: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)
        _previewView = view.findViewById(R.id.previewView)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startCamera()
        }

        return view
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
        val cameraProvider: ProcessCameraProvider =
            ProcessCameraProvider.getInstance(requireContext()).get()
        cameraProvider.unbindAll()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        val preview = Preview.Builder()
            .setTargetResolution(Size(640, 480))
            .build()
        preview.setSurfaceProvider(_previewView!!.surfaceProvider)
        val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(_cameraExecutor!!) { imageProxy ->
            scanQrCode(imageProxy)
            imageProxy.close()
        }
        _camera = cameraProvider.bindToLifecycle(
            requireContext() as LifecycleOwner,
            cameraSelector,
            preview,
            imageAnalysis
        )
    }

    private fun onBarcodeScanned(barCodes: List<Barcode>) {
        for (barcode in barCodes) {
            val scannedData: String = barcode.rawValue!!
            val time = Calendar.getInstance().time
            val lastScan = _lastScan
            if (lastScan != null && (lastScan.time - time.time < 1000)){
                return // avoid rapid calls to the navigation controller since it can cause a crash
            }
            setFragmentResult("key_scan_result", bundleOf("public_key" to scannedData))
            findNavController().navigate(R.id.action_KeyScannerFragment_to_ConfigurationFragment)
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
                onBarcodeScanned(barCodes)
            }
            .addOnFailureListener { e -> }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Camera permission denied. Cannot scan QR codes.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _cameraExecutor!!.shutdown()
    }


    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}