package org.parabellumlinuxproject.getmyip.core

data class IpInfo(
    val ip: String,
    val isp: String?,
    val asn: String?,
    val country: String?,
    val city: String?,
)
