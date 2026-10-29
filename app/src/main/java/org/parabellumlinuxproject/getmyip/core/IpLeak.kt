package org.parabellumlinuxproject.getmyip.core

import org.json.JSONObject
import java.io.IOException

object IpLeak {
    const val NAME = "ipleak.net"
    private const val URL_V4 = "https://ipv4.ipleak.net/json/"
    private const val URL_V6 = "https://ipv6.ipleak.net/json/"

    fun fetchV4(): IpInfo = parse(Http.get(URL_V4))
    fun fetchV6(): IpInfo = parse(Http.get(URL_V6))

    // Only these fields are read. Everything else in the response is ignored on purpose.
    internal fun parse(body: String): IpInfo {
        val o = JSONObject(body)
        fun field(key: String): String? =
            if (o.has(key) && !o.isNull(key)) o.optString(key).trim().ifEmpty { null } else null
        return IpInfo(
            ip = field("ip") ?: throw IOException("Response has no ip field"),
            isp = field("isp_name"),
            asn = field("as_number"),
            country = field("country_name"),
            city = field("city_name"),
        )
    }
}
