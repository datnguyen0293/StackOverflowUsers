package com.gico.datnq.library.secure.preferences

import android.os.Process
import com.gico.datnq.library.utilities.LoggerUtil
import java.io.*
import java.security.SecureRandomSpi

/**
 * {@link SecureRandomSpi} which passes all requests to the Linux PRNG (
 * {@code /dev/urandom}).
 */
class LinuxPRNGSecureRandom: SecureRandomSpi() {

    /*
     * IMPLEMENTATION NOTE: Requests to generate bytes and to mix in a
     * seed are passed through to the Linux PRNG (/dev/urandom).
     * Instances of this class seed themselves by mixing in the current
     * time, PID, UID, build fingerprint, and hardware serial number
     * (where available) into Linux PRNG.
     *
     * Concurrency: Read requests to the underlying Linux PRNG are
     * serialized (on sLock) to ensure that multiple threads do not get
     * duplicated PRNG output.
     */

    /**
     * Whether this engine instance has been seeded. This is needed
     * because each instance needs to seed itself if the client does not
     * explicitly seed it.
     */
    private var mSeeded: Boolean = false

    companion object {
        private const val FILE = "/dev/urandom"
        private val URANDOM_FILE = File(FILE)

        private val sLock = Object()

        /**
         * Input stream for reading from Linux PRNG or {@code null} if not
         * yet opened.
         */
        @JvmField
        var sUrandomIn: DataInputStream? = null

        /**
         * Output stream for writing to Linux PRNG or {@code null} if not
         * yet opened.
         */
        @JvmField
        var sUrandomOut: OutputStream? = null

        /**
         * Generates a device- and invocation-specific seed to be mixed into the
         * Linux PRNG.
         */
        @JvmStatic
        fun generateSeed(): ByteArray
        {
            try {
                val seedBuffer = ByteArrayOutputStream()
                val seedBufferOut = DataOutputStream(seedBuffer)
                seedBufferOut.writeLong(System.currentTimeMillis())
                seedBufferOut.writeLong(System.nanoTime())
                seedBufferOut.writeInt(Process.myPid())
                seedBufferOut.writeInt(Process.myUid())
                seedBufferOut.write(PrngFixes.BUILD_FINGERPRINT_AND_DEVICE_SERIAL)
                seedBufferOut.close()
                return seedBuffer.toByteArray()
            } catch (e: IOException) {
                throw SecurityException ("Failed to generate seed", e)
            }
        }
    }

    override fun engineSetSeed(seed: ByteArray?) {
        try {
            var out: OutputStream?
            synchronized (sLock) {
                out = getUrandomOutputStream()
            }
            out?.write(seed)
            out?.flush()
        } catch (e: IOException) {
            // On a small fraction of devices /dev/urandom is not
            // writable Log and ignore.
            LoggerUtil.e(PrngFixes::javaClass.name, "engineSetSeed", e)
        } finally {
            mSeeded = true
        }
    }

    override fun engineNextBytes(bytes: ByteArray?) {
        if (!mSeeded) {
            // Mix in the device- and invocation-specific seed.
            engineSetSeed(generateSeed())
        }

        var inputStream: DataInputStream? = null
        try {
            synchronized(sLock) {
                inputStream = getUrandomInputStream()
            }
            inputStream?.let {
                synchronized(it) {
                    it.readFully(bytes)
                }
            }
        } catch (e: IOException) {
            LoggerUtil.e(javaClass.simpleName, "engineNextBytes", e)
        } finally {
            inputStream?.close()
        }
    }

    override fun engineGenerateSeed(numBytes: Int): ByteArray {
        val seed = ByteArray(numBytes)
        engineNextBytes(seed)
        return seed
    }

    private fun getUrandomInputStream(): DataInputStream? {
        synchronized (sLock) {
            if (sUrandomIn == null) {
                // NOTE: Consider inserting a BufferedInputStream
                // between DataInputStream and FileInputStream if you need
                // higher PRNG output performance and can live with future PRNG
                // output being pulled into this process prematurely.
                try {
                    sUrandomIn = DataInputStream(FileInputStream(URANDOM_FILE))
                } catch (e: IOException) {
                    throw SecurityException("Failed to open " + URANDOM_FILE
                            + " for reading", e)
                }
            }
            return sUrandomIn
        }
    }

    private fun getUrandomOutputStream(): OutputStream? {
        synchronized (sLock) {
            if (sUrandomOut == null) {
                sUrandomOut = FileOutputStream(URANDOM_FILE)
            }
            return sUrandomOut
        }
    }
}