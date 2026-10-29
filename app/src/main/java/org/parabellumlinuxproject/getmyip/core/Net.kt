package org.parabellumlinuxproject.getmyip.core

import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

enum class Family { V4, V6 }

internal object Net {
    private const val USER_AGENT =
        "PLP-GetMyIP/1.0.0 (+https://github.com/metesapmaz5-lang/GetMyIP-for-Android)"

    private val base: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    // Forces the connection over IPv4 or IPv6, so every provider can show both addresses.
    private fun familyDns(family: Family) = object : Dns {
        override fun lookup(hostname: String): List<InetAddress> {
            val all = Dns.SYSTEM.lookup(hostname)
            val picked: List<InetAddress> = when (family) {
                Family.V4 -> all.filterIsInstance<Inet4Address>()
                Family.V6 -> all.filterIsInstance<Inet6Address>()
            }
            if (picked.isEmpty()) throw UnknownHostException("No ${family.name} address for $hostname")
            return picked
        }
    }

    private val v4Client = base.newBuilder().dns(familyDns(Family.V4)).build()
    private val v6Client = base.newBuilder().dns(familyDns(Family.V6)).build()

    fun get(url: String, family: Family? = null, accept: String? = null): String {
        val client = when (family) {
            Family.V4 -> v4Client
            Family.V6 -> v6Client
            null -> base
        }
        val builder = Request.Builder().url(url).header("User-Agent", USER_AGENT)
        if (accept != null) builder.header("Accept", accept)
        client.newCall(builder.build()).execute().use { response ->
            if (!response.isSuccessful) throw IOException("HTTP ${response.code}")
            return response.body.string()
        }
    }
}
