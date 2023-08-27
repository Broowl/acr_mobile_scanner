package com.acr_mobile_scanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import android.content.Intent
import java.util.Date
import javax.xml.validation.Validator

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var scanner: Scanner? = null

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

        val eventCharacteristics = EventCharacteristics("test", strToDate("2023-08-27"))
        val publicKey = "-----BEGIN PUBLIC KEY-----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDcliT764RZu5Zl0LfGjJeIamdO\n" +
                "WExomreVs8NqbHb2ssFvpRtRZdYOrhLcNXoCggMGjBVzZp6ajdL6SHnKO7UnvTSa\n" +
                "bz/vLuTuqzfOQIhLSkHEz5/O7yPokFldk9pkAvd0pOwwZY1tQxLmQR7Gt0DqNC5K\n" +
                "PR9tEhRRLnARVw9e9wIDAQAB\n" +
                "-----END PUBLIC KEY-----"
        val validator = SignatureValidator(publicKey)
        scanner = Scanner(eventCharacteristics, validator)

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
                scanner?.processQrCode(result.contents)
            }
        }
    }
}