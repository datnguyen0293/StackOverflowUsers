package com.gico.datnq.library.secure.preferences

import android.util.Base64
import javax.crypto.SecretKey

/**
 * Holder class that has both the secret AES key for encryption (confidentiality)
 * and the secret HMAC key for integrity.
 */
class SecretKeys internal constructor(confidentialityKeyIn: SecretKey, integrityKeyIn: SecretKey) {
    var confidentialityKey: SecretKey? = null
        private set
    var integrityKey: SecretKey? = null
        private set

    private fun setConfidentialityKey(confidentialityKey: SecretKey) {
        this.confidentialityKey = confidentialityKey
    }

    private fun setIntegrityKey(integrityKey: SecretKey) {
        this.integrityKey = integrityKey
    }

    /**
     * Encodes the two keys as a string
     *
     * @return base64(confidentialityKey):base64(integrityKey)
     */
    override fun toString(): String {
        confidentialityKey?.let { c ->
            integrityKey?.let { i->
                return (Base64.encodeToString(c.encoded, BASE64_FLAGS)
                        + ":" + Base64.encodeToString(i.encoded, BASE64_FLAGS))
            }
        }
        return ""
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + confidentialityKey.hashCode()
        result = prime * result + integrityKey.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        var b = true
        if (other == null) b = false
        other?.let {
            if (javaClass != other.javaClass) b = false
        }
        val obj = other as SecretKeys?

        obj?.let { o ->
            integrityKey?.let { i ->
                if (i != o.integrityKey) b = false
            }
            confidentialityKey?.let { c ->
                if (c != o.confidentialityKey) b = false
            }
        }
        return b
    }

    companion object {
        /** Made BASE_64_FLAGS public as it's useful to know for compatibility.  */
        const val BASE64_FLAGS = Base64.NO_WRAP
    }

    init {
        setConfidentialityKey(confidentialityKeyIn)
        setIntegrityKey(integrityKeyIn)
    }
}