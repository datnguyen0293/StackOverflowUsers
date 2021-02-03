package com.datnq.stack.overflow.users.datnq.library.secure.preferences

import android.util.Base64

/**
 * Holder class that allows us to bundle ciphertext and IV together.
 */
@Suppress("DEPRECATION")
class CipherTextIvMac {
    var cipherText: ByteArray = ByteArray(DEFAULT_BUFFER_SIZE)
    var iv: ByteArray = ByteArray(DEFAULT_BUFFER_SIZE)
    var mac: ByteArray = ByteArray(DEFAULT_BUFFER_SIZE)

    /**
     * Constructs a new bundle of ciphertext and IV from a string of the
     * format <code>base64(iv):base64(ciphertext)</code>.
     *
     * @param base64IvAndCipherText A string of the format
     * <code>iv:ciphertext</code> The IV and ciphertext must each
     * be base64-encoded.
     */
    constructor(base64IvAndCipherText: String) {
        val civArray = base64IvAndCipherText.split(":")
        if (civArray.size != 3) {
            throw IllegalArgumentException("Cannot parse iv:ciphertext:mac")
        } else {
            iv = Base64.decode(civArray[0], SecretKeys.BASE64_FLAGS)
            mac = Base64.decode(civArray[1], SecretKeys.BASE64_FLAGS)
            cipherText = Base64.decode(civArray[2], SecretKeys.BASE64_FLAGS)
        }
    }

    /**
     * Construct a new bundle of ciphertext and IV.
     *
     * @param c The ciphertext
     * @param i The IV
     * @param h The mac
     */
    constructor(c: ByteArray, i: ByteArray, h: ByteArray) {
        cipherText = ByteArray(c.size)
        System.arraycopy(c, 0, cipherText, 0, c.size)
        iv = ByteArray(i.size)
        System.arraycopy(i, 0, iv, 0, i.size)
        mac = ByteArray(h.size)
        System.arraycopy(h, 0, mac, 0, h.size)
    }

    companion object {
        /**
         * Concatinate the IV to the cipherText using array copy.
         * This is used e.g. before computing mac.
         *
         * @param iv The IV to prepend
         * @param cipherText the cipherText to append
         * @return iv:cipherText, a new byte array.
         */
        @JvmStatic
        fun ivCipherConcat(iv: ByteArray, cipherText: ByteArray): ByteArray {
            val combined = ByteArray(iv.size + cipherText.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)
            return combined
        }
    }

    /**
     * Encodes this ciphertext, IV, mac as a string.
     *
     * @return base64(iv) : base64(mac) : base64(ciphertext).
     * The iv and mac go first because they're fixed length.
     */
    override fun toString(): String {
        val ivString = Base64.encodeToString(iv, SecretKeys.BASE64_FLAGS)
        val cipherTextString = Base64.encodeToString(cipherText, SecretKeys.BASE64_FLAGS)
        val macString = Base64.encodeToString(mac, SecretKeys.BASE64_FLAGS)
        return "$ivString:$macString:$cipherTextString"
    }

    override fun hashCode(): Int {
        super.hashCode()
        val prime = 31
        var result = 1
        result = prime * result + cipherText.contentHashCode()
        result = prime * result + iv.contentHashCode()
        result = prime * result + mac.contentHashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val obj = other as CipherTextIvMac
        if (!cipherText.contentEquals(obj.cipherText))
            return false
        if (!iv.contentEquals(obj.iv))
            return false
        return mac.contentEquals(obj.mac)
    }
}