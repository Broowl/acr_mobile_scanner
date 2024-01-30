package com.acr_mobile_scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.common.Barcode


class ScannerFragment : Fragment() {
    private var _impl = ScannerImpl()
    private val _viewModel: EntityViewModel by activityViewModels()
    private var _valid = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return _impl.onCreateView(
            inflater,
            container,
            requireContext()
        ) { barcodes -> onBarcodeScanned(barcodes) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _impl.onCreate()
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

    override fun onDestroy() {
        super.onDestroy()
        _impl.onDestroy()
    }
}