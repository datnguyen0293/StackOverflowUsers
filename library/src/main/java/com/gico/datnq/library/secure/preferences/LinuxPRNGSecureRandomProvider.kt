package com.datnq.stack.overflow.users.datnq.library.secure.preferences

import java.security.Provider

/**
 * {@code Provider} of {@code SecureRandom} engines which pass through
 * all requests to the Linux PRNG.
 */
class LinuxPRNGSecureRandomProvider(): Provider("LinuxPRNG", 1.0,
        "A Linux-specific random number provider that uses/dev/urandom") {

    init {
        // Although /dev/urandom is not a SHA-1 PRNG, some apps
        // explicitly request a SHA1PRNG SecureRandom and we thus need
        // to prevent them from getting the default implementation whose
        // output may have low entropy.
        put("SecureRandom.SHA1PRNG", LinuxPRNGSecureRandom::class.java.name)
        put("SecureRandom.SHA1PRNG ImplementedIn", "Software")
    }
}