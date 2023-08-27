package com.acr_mobile_scanner


class Scanner constructor(
    private val eventCharacteristics: EventCharacteristics,
    private val validator: SignatureValidator
) {

    fun processQrCode(payload: String): Unit {
        var decodeResult = decodeMessage(payload) ?: return
        if (decodeResult.eventName != eventCharacteristics.name) {
            return
        }
        if (decodeResult.eventDate != eventCharacteristics.date) {
            return
        }
        val isVerified = validator.verifyMessage(decodeResult.encoded, decodeResult.signature)
        if (!isVerified) {
            return
        }
        print("success")
    }
}