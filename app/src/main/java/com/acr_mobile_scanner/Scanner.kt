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
    private val _eventCharacteristics: EventCharacteristics,
    private val _validator: SignatureValidator,
    private val _idStorage: IdStorage
) {

    fun processQrCode(payload: String): ScanResult  {
        var decodeResult = decodeMessage(payload) ?: return ScanResult("Decoding error")
        if (decodeResult.eventName != _eventCharacteristics.name) {
            return ScanResult("Event name mismatch.\nExpected: ${_eventCharacteristics.name}\nReceived: ${decodeResult.eventName}")
        }
        if (decodeResult.eventDate != _eventCharacteristics.date) {
            return ScanResult("Event date mismatch.\nExpected: ${_eventCharacteristics.date}\nReceived: ${decodeResult.eventDate}")
        }
        val isVerified = _validator.verifyMessage(decodeResult.encoded, decodeResult.signature)
        if (!isVerified) {
            return ScanResult("Invalid signature")
        }
        val isNotDuplicate = _idStorage.tryAddId(decodeResult.ticketId)
        if (!isNotDuplicate){
            return ScanResult("Duplicate ID")
        }
        return ScanResult()
    }
}