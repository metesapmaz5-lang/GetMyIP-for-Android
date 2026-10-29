package org.parabellumlinuxproject.getmyip.core

import org.json.JSONObject
import java.io.IOException
import java.security.SecureRandom

data class DnsResolver(val ip: String, val info: IpInfo?)

/**
 * DNS leak test using ipleak.net.
 * Every request goes to a new random hostname, so the device's own DNS resolver
 * has to look it up, and ipleak.net records which resolver asked.
 */
object DnsLeakTest {
    const val NAME = "ipleak.net"
    private const val ROUNDS = 6
    private const val MAX_RESOLVERS = 10
    private const val CHARS = "abcdefghijklmnopqrstuvwxyz0123456789"

    fun run(): List<DnsResolver> {
        val session = randomSession()
        val seen = linkedSetOf<String>()
        var succeeded = 0
        var lastError: Exception? = null
        for (label in 1..ROUNDS) {
            try {
                seen += parse(Net.get("https://$session-$label.ipleak.net/dnsdetection/", accept = "application/json"))
                succeeded++
            } catch (e: Exception) {
                lastError = e
            }
        }
        if (succeeded == 0) throw lastError ?: IOException("DNS test failed")
        return seen.take(MAX_RESOLVERS).map { ip ->
            DnsResolver(ip, runCatching { IpLeakProvider.lookup(ip) }.getOrNull())
        }
    }

    /** The "ip" field is an object of resolver → count, or an empty array when nothing was seen. */
    internal fun parse(body: String): Set<String> {
        val ips = JSONObject(body).optJSONObject("ip") ?: return emptySet()
        return ips.keys().asSequence().filter { IP_LITERAL.matches(it) }.toSet()
    }

    private fun randomSession(): String {
        val random = SecureRandom()
        return (1..40).map { CHARS[random.nextInt(CHARS.length)] }.joinToString("")
    }
}
