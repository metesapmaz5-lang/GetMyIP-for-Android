package org.parabellumlinuxproject.getmyip.core

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException

interface IpProvider {
    val id: String
    val displayName: String
    fun fetch(family: Family): IpInfo
}

// Only the fields below are read. Everything else in a response is ignored on purpose.
private fun JSONObject.text(key: String): String? =
    if (has(key) && !isNull(key)) optString(key).trim().ifEmpty { null } else null

internal val IP_LITERAL = Regex("^[0-9a-fA-F:.]+$")

object IpLeakProvider : IpProvider {
    override val id = "ipleak"
    override val displayName = "ipleak.net"

    override fun fetch(family: Family): IpInfo {
        val url = when (family) {
            Family.V4 -> "https://ipv4.ipleak.net/json/"
            Family.V6 -> "https://ipv6.ipleak.net/json/"
        }
        return parse(Net.get(url, family, accept = "application/json"))
    }

    /** Looks up details of another IP address, for example a DNS server. */
    fun lookup(ip: String): IpInfo {
        require(IP_LITERAL.matches(ip)) { "Not an IP address" }
        return parse(Net.get("https://ipleak.net/json/$ip", accept = "application/json"))
    }

    internal fun parse(body: String): IpInfo {
        val o = JSONObject(body)
        return IpInfo(
            ip = o.text("ip") ?: throw IOException("Response has no ip field"),
            isp = o.text("isp_name"),
            asn = o.text("as_number"),
            country = o.text("country_name"),
            city = o.text("city_name"),
        )
    }
}

/** Works with any server running echoip (https://github.com/mpolden/echoip). */
open class EchoIpProvider(
    override val id: String,
    override val displayName: String,
    private val baseUrl: String,
) : IpProvider {
    override fun fetch(family: Family): IpInfo =
        parse(Net.get(baseUrl.trimEnd('/') + "/json", family, accept = "application/json"))

    companion object {
        internal fun parse(body: String): IpInfo {
            val o = JSONObject(body)
            return IpInfo(
                ip = o.text("ip") ?: throw IOException("Response has no ip field"),
                isp = o.text("asn_org"),
                asn = o.text("asn")?.removePrefix("AS"),
                country = o.text("country"),
                city = o.text("city"),
            )
        }
    }
}

object IfconfigCoProvider : EchoIpProvider("ifconfig", "ifconfig.co", "https://ifconfig.co")

object Providers {
    /** Returns a provider for a user's own echoip server, or null if the address is not valid https. */
    fun custom(address: String): IpProvider? {
        val url = address.trim().toHttpUrlOrNull() ?: return null
        if (!url.isHttps) return null
        return EchoIpProvider("custom", url.host, url.toString().trimEnd('/'))
    }
}
