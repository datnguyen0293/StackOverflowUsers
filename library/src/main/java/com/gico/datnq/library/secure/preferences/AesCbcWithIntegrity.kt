/*
 * Copyright (c) 2014-2015 Tozny LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Created by Isaac Potoczny-Jones on 11/12/14.
 */

package com.datnq.stack.overflow.users.datnq.library.secure.preferences

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.concurrent.atomic.AtomicBoolean
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.xor

/**
 * Simple library for the "right" defaults for AES key generation, encryption,
 * and decryption using 128-bit AES, CBC, PKCS5 padding, and a random 16-byte IV
 * with SHA1PRNG. Integrity with HmacSHA256.
 */
class AesCbcWithIntegrity {
    companion object {
        // If the PRNG fix would not succeed for some reason, we normally will throw an exception.
        private const val CIPHER = "AES"
        private const val AES_KEY_LENGTH_BITS = 128
        private const val IV_LENGTH_BYTES = 16
        private const val PBE_ITERATION_COUNT = 10000
        private const val PBE_ALGORITHM = "PBKDF2WithHmacSHA1"

        /**
         * default for testing
         */
        private val prngFixed = AtomicBoolean(false)

        private const val HMAC_ALGORITHM = "HmacSHA256"
        private const val HMAC_KEY_LENGTH_BITS = 256

        /**
         * An aes key derived from a base64 encoded key. This does not generate the key.
         * It's not random or a PBE key.
         *
         * @param keysStr a base64 encoded AES key / hmac key as base64(aesKey) : base64(hmacKey).
         * @return an AES and HMAC key set suitable for other functions.
         */
        @JvmStatic
        fun keys(keysStr: String): SecretKeys {
            val keysArr = keysStr.split(":")

            if (keysArr.size != 2) {
                throw IllegalArgumentException("Cannot parse aesKey:hmacKey")
            } else {
                val confidentialityKey = Base64.decode(keysArr[0], SecretKeys.BASE64_FLAGS)
                if (confidentialityKey.size != AES_KEY_LENGTH_BITS / 8) {
                    throw InvalidKeyException("Base64 decoded key is not " + AES_KEY_LENGTH_BITS + " bytes")
                }
                val integrityKey = Base64.decode(keysArr[1], SecretKeys.BASE64_FLAGS)
                if (integrityKey.size != HMAC_KEY_LENGTH_BITS / 8) {
                    throw InvalidKeyException("Base64 decoded key is not " + HMAC_KEY_LENGTH_BITS + " bytes")
                }

                return SecretKeys(
                        SecretKeySpec(confidentialityKey, 0, confidentialityKey.size, CIPHER),
                        SecretKeySpec(integrityKey, HMAC_ALGORITHM))
            }
        }

        /**
         * A function that generates random AES and HMAC keys and prints out exceptions but
         * doesn't throw them since none should be encountered.
         * If they are encountered, the return value is null.
         *
         * @return The AES and HMAC keys.
         * @throws GeneralSecurityException if AES is not implemented on this system,
         *                                  or a suitable RNG is not available
         */
        @JvmStatic
        fun generateKey(): SecretKeys {
            fixPrng()
            val keyGen = KeyGenerator.getInstance(CIPHER)
            // No need to provide a SecureRandom or set a seed since that will
            // happen automatically.
            keyGen.init(AES_KEY_LENGTH_BITS)
            val confidentialityKey = keyGen.generateKey()

            //Now make the HMAC key
            val integrityKeyBytes = randomBytes(HMAC_KEY_LENGTH_BITS / 8)//to get bytes
            val integrityKey = SecretKeySpec(integrityKeyBytes, HMAC_ALGORITHM)

            return SecretKeys(confidentialityKey, integrityKey)
        }

        /**
         * A function that generates password-based AES and HMAC keys. It prints out exceptions but
         * doesn't throw them since none should be encountered. If they are
         * encountered, the return value is null.
         *
         * @param password The password to derive the keys from.
         * @return The AES and HMAC keys.
         * @throws GeneralSecurityException if AES is not implemented on this system,
         *                                  or a suitable RNG is not available
         */
        @JvmStatic
        fun generateKeyFromPassword(password: String, salt: ByteArray): SecretKeys {

            fixPrng()

            //Get enough random bytes for both the AES key and the HMAC key:
            val keySpec = PBEKeySpec(password.toCharArray(), salt,
                    PBE_ITERATION_COUNT, AES_KEY_LENGTH_BITS + HMAC_KEY_LENGTH_BITS)
            val keyFactory = SecretKeyFactory.getInstance(PBE_ALGORITHM)
            val keyBytes = keyFactory.generateSecret(keySpec).encoded

            // Split the random bytes into two parts:
            val confidentialityKeyBytes = copyOfRange(keyBytes, 0, AES_KEY_LENGTH_BITS / 8)
            val integrityKeyBytes = copyOfRange(keyBytes,
                    AES_KEY_LENGTH_BITS / 8, AES_KEY_LENGTH_BITS / 8 + HMAC_KEY_LENGTH_BITS / 8)

            //Generate the AES key
            val confidentialityKey = SecretKeySpec(confidentialityKeyBytes, CIPHER)

            //Generate the HMAC key
            val integrityKey = SecretKeySpec(integrityKeyBytes, HMAC_ALGORITHM)

            return SecretKeys(confidentialityKey, integrityKey)
        }

        /**
         * Creates a random Initialization Vector (IV) of IV_LENGTH_BYTES.
         *
         * @return The byte array of this IV
         */
        @JvmStatic
        private fun generateIv(): ByteArray {
            return randomBytes(IV_LENGTH_BYTES)
        }

        @JvmStatic
        private fun randomBytes(length: Int): ByteArray {
            fixPrng()
            val random = SecureRandom()
            val b = ByteArray(length)
            random.nextBytes(b)
            return b
        }

        /*
     * -----------------------------------------------------------------
     * Encryption
     * -----------------------------------------------------------------
     */

        /**
         * Generates a random IV and encrypts this plain text with the given key. Then attaches
         * a hashed MAC, which is contained in the CipherTextIvMac class.
         *
         * @param plaintext  The text that will be encrypted, which
         *                   will be serialized with UTF-8
         * @param secretKeys The AES and HMAC keys with which to encrypt
         * @return a tuple of the IV, ciphertext, mac
         * @throws GeneralSecurityException     if AES is not implemented on this system
         * @throws UnsupportedEncodingException if UTF-8 is not supported in this system
         */
        @JvmStatic
        fun encrypt(plaintext: String, secretKeys: SecretKeys): CipherTextIvMac {
            return encrypt(plaintext, secretKeys, StandardCharsets.UTF_8)
        }

        /**
         * Generates a random IV and encrypts this plain text with the given key. Then attaches
         * a hashed MAC, which is contained in the CipherTextIvMac class.
         *
         * @param plaintext  The bytes that will be encrypted
         * @param secretKeys The AES and HMAC keys with which to encrypt
         * @return a tuple of the IV, ciphertext, mac
         * @throws GeneralSecurityException     if AES is not implemented on this system
         * @throws UnsupportedEncodingException if the specified encoding is invalid
         */
        @JvmStatic
        private fun encrypt(plaintext: String, secretKeys: SecretKeys, encoding: Charset): CipherTextIvMac {
            return encrypt(plaintext.toByteArray(encoding), secretKeys)
        }

        /**
         * Generates a random IV and encrypts this plain text with the given key. Then attaches
         * a hashed MAC, which is contained in the CipherTextIvMac class.
         *
         * @param plaintext  The text that will be encrypted
         * @param secretKeys The combined AES and HMAC keys with which to encrypt
         * @return a tuple of the IV, ciphertext, mac
         * @throws GeneralSecurityException if AES is not implemented on this system
         */
        @JvmStatic
        private fun encrypt(plaintext: ByteArray, secretKeys: SecretKeys): CipherTextIvMac {
            var iv = generateIv()
            val aesCipherForEncryption = Cipher.getInstance("AES/GCM/NoPadding")
            aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKeys.confidentialityKey, IvParameterSpec(iv))

            /*
         * Now we get back the IV that will actually be used. Some Android
         * versions do funny stuff w/ the IV, so this is to work around bugs:
         */
            iv = aesCipherForEncryption.iv
            val byteCipherText = aesCipherForEncryption.doFinal(plaintext)
            val ivCipherConcat = CipherTextIvMac.ivCipherConcat(iv, byteCipherText)

            val integrityMac = generateMac(ivCipherConcat, secretKeys.integrityKey)
            return CipherTextIvMac(byteCipherText, iv, integrityMac)
        }

        /**
         * Ensures that the PRNG is fixed. Should be used before generating any keys.
         * Will only run once, and every subsequent call should return immediately.
         */
        @JvmStatic
        private fun fixPrng() {
            if (!prngFixed.get()) {
                synchronized(PrngFixes::class.java) {
                    if (!prngFixed.get()) {
                        PrngFixes.apply()
                        prngFixed.set(true)
                    }
                }
            }
        }

        /*
     * -----------------------------------------------------------------
     * Decryption
     * -----------------------------------------------------------------
     */

        /**
         * AES CBC decrypt.
         *
         * @param civ        The cipher text, IV, and mac
         * @param secretKeys The AES and HMAC keys
         * @param encoding   The string encoding to use to decode the bytes after decryption
         * @return A string derived from the decrypted bytes (not base64 encoded)
         * @throws GeneralSecurityException     if AES is not implemented on this system
         * @throws UnsupportedEncodingException if the encoding is unsupported
         */
        @JvmStatic
        private fun decryptString(civ: CipherTextIvMac, secretKeys: SecretKeys, encoding: Charset): String {
            return String(decrypt(civ, secretKeys), encoding)
        }

        /**
         * AES CBC decrypt.
         *
         * @param civ        The cipher text, IV, and mac
         * @param secretKeys The AES and HMAC keys
         * @return A string derived from the decrypted bytes, which are interpreted as a UTF-8 String
         * @throws GeneralSecurityException     if AES is not implemented on this system
         * @throws UnsupportedEncodingException if UTF-8 is not supported
         */
        @JvmStatic
        fun decryptString(civ: CipherTextIvMac, secretKeys: SecretKeys): String {
            return decryptString(civ, secretKeys, StandardCharsets.UTF_8)
        }

        /**
         * AES CBC decrypt.
         *
         * @param civ        the cipher text, iv, and mac
         * @param secretKeys the AES and HMAC keys
         * @return The raw decrypted bytes
         * @throws GeneralSecurityException if MACs don't match or AES is not implemented
         */
        @JvmStatic
        private fun decrypt(civ: CipherTextIvMac, secretKeys: SecretKeys): ByteArray {
            val ivCipherConcat = CipherTextIvMac.ivCipherConcat(civ.iv, civ.cipherText)
            val computedMac = generateMac(ivCipherConcat, secretKeys.integrityKey)
            if (constantTimeEq(computedMac, civ.mac)) {
                val aesCipherForDecryption = Cipher.getInstance("AES/GCM/NoPadding")
                aesCipherForDecryption.init(Cipher.DECRYPT_MODE,
                        secretKeys.confidentialityKey, IvParameterSpec(civ.iv))
                return aesCipherForDecryption.doFinal(civ.cipherText)
            } else {
                throw GeneralSecurityException("MAC stored in civ does not match computed MAC.")
            }
        }

        /*
     * -----------------------------------------------------------------
     * Helper Code
     * -----------------------------------------------------------------
     */

        /**
         * Generate the mac based on HMAC_ALGORITHM
         *
         * @param integrityKey   The key used for hmac
         * @param byteCipherText the cipher text
         * @return A byte array of the HMAC for the given key and ciphertext
         * @throws NoSuchAlgorithmException if no such algorithm
         * @throws InvalidKeyException      if invalid key
         */
        @JvmStatic
        private fun generateMac(byteCipherText: ByteArray, integrityKey: SecretKey?): ByteArray {

            //Now compute the mac for later integrity checking
            val sha256hmac = Mac.getInstance(HMAC_ALGORITHM)
            sha256hmac.init(integrityKey)
            return sha256hmac.doFinal(byteCipherText)
        }

        /**
         * Simple constant-time equality of two byte arrays. Used for security to avoid timing attacks.
         *
         * @return true iff the arrays are exactly equal.
         */
        @JvmStatic
        private fun constantTimeEq(a: ByteArray, b: ByteArray): Boolean {
            if (a.size != b.size) {
                return false
            }
            var result = 0
            for (i in a.indices) {
                result = result or (a[i] xor b[i]).toInt()
            }
            return result == 0
        }

        /**
         * Copy the elements from the start to the end
         *
         * @param from  the source
         * @param start the start index to copy
         * @param end   the end index to finish
         * @return the buffer
         */
        @JvmStatic
        private fun copyOfRange(from: ByteArray, start: Int, end: Int): ByteArray {
            val length = end - start
            val result = ByteArray(length)
            System.arraycopy(from, start, result, 0, length)
            return result
        }
    }
}