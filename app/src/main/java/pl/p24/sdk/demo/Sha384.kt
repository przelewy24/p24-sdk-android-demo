package pl.p24.sdk.demo

import java.security.MessageDigest

fun String.toSHA384(): String {
    val digest = MessageDigest.getInstance("SHA-384")
    val hashBytes = digest.digest(this.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) }
}