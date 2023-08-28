package com.acr_mobile_scanner

class ScanResult constructor(isSuccessIn:Boolean, messageIn: String?){
    var isSuccess:Boolean
    var errorMessage:String?
    init {
        isSuccess = isSuccessIn
        errorMessage = messageIn
    }

    constructor(message:String) : this(false, message) {
    }

    constructor() : this(true, null) {
    }
}


class Scanner constructor(
    private val eventCharacteristics: EventCharacteristics,
    private val validator: SignatureValidator
) {

    fun processQrCode(payload: String): ScanResult  {
        var decodeResult = decodeMessage(payload) ?: return ScanResult("Decoding error")
        if (decodeResult.eventName != eventCharacteristics.name) {
            return ScanResult("Event name mismatch. Expected: ${eventCharacteristics.name}; Received: ${decodeResult.eventName}")
        }
        if (decodeResult.eventDate != eventCharacteristics.date) {
            return ScanResult("Event date mismatch. Expected: ${eventCharacteristics.date}; Received: ${decodeResult.eventDate}")
        }
        val isVerified = validator.verifyMessage(decodeResult.encoded, decodeResult.signature)
        if (!isVerified) {
            return ScanResult("Invalid signature")
        }
        return ScanResult()
    }
}