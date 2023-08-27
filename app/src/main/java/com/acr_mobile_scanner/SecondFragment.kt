package com.acr_mobile_scanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import java.util.Date
import javax.xml.validation.Validator

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _scanner: Scanner? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)

        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR code")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.initiateScan()

        setFragmentResultListener("requestConfiguration") { _, bundle ->
            val validator = SignatureValidator(bundle.getString("publicKey", ""))
            val eventName = bundle.getString("eventName", "")
            val eventDate = strToDate(bundle.getString("eventDate", ""))
            _scanner = Scanner(EventCharacteristics(eventName,eventDate), validator)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                // Handle cancellation
            } else {
                _scanner?.processQrCode(result.contents)
            }
        }
    }
}