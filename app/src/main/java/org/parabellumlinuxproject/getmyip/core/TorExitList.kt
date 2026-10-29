package org.parabellumlinuxproject.getmyip.core

/**
 * Downloads the Tor Project's public exit list and checks it on the device.
 * The app never asks the Tor Project about the user's address. Like any download,
 * torproject.org sees the connection itself.
 */
object TorExitList {
    const val NAME = "torproject.org"
    private const val URL_LIST = "https://check.torproject.org/exit-addresses"
    private const val MAX_AGE_MS = 60 * 60 * 1000L

    @Volatile private var cached: Set<String>? = null
    @Volatile private var cachedAt = 0L

    fun get(now: Long = System.currentTimeMillis()): Set<String> {
        val c = cached
        if (c != null && now - cachedAt < MAX_AGE_MS) return c
        val fresh = parse(Net.get(URL_LIST))
        cached = fresh
        cachedAt = now
        return fresh
    }

    internal fun parse(text: String): Set<String> =
        text.lineSequence()
            .filter { it.startsWith("ExitAddress ") }
            .mapNotNull { it.split(' ').getOrNull(1) }
            .toSet()
}
