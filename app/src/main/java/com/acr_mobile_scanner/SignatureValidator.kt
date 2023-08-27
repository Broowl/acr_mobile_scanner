package com.acr_mobile_scanner

import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

fun convertPemToPublicKey(pemKey: String): PublicKey? {
    val publicKeyPEM = pemKey
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replace("\n", "")
        .trim()

    val keyBytes = android.util.Base64.decode(publicKeyPEM, android.util.Base64.DEFAULT)

    return try {
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKeySpec = X509EncodedKeySpec(keyBytes)
        keyFactory.generatePublic(publicKeySpec)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

class SignatureValidator constructor(publicKeyPem: String) {
    private val publicKey: PublicKey?

    init {
        publicKey = convertPemToPublicKey(publicKeyPem)
    }

    fun verifyMessage(message: String, signature: ByteArray): Boolean {
        if (publicKey == null){
            return false
        }
        return try {
            val signatureInstance = Signature.getInstance("SHA256withRSAandMGF1")
            signatureInstance.initVerify(publicKey)
            signatureInstance.update(message.toByteArray())

            signatureInstance.verify(signature)
        } catch (e: Exception) {
            false
        }

    }
}