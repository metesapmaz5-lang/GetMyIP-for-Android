package org.parabellumlinuxproject.getmyip.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

enum class Transport { WIFI, CELLULAR, ETHERNET, VPN, OTHER, OFFLINE }

data class NetworkState(
    val transport: Transport,
    val isVpn: Boolean,
    val localAddresses: List<String>,
)

fun readNetworkState(context: Context): NetworkState {
    val offline = NetworkState(Transport.OFFLINE, false, emptyList())
    val cm = context.getSystemService(ConnectivityManager::class.java) ?: return offline
    val network = cm.activeNetwork ?: return offline
    val caps = cm.getNetworkCapabilities(network)
    val isVpn = caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
    val transport = when {
        caps == null -> Transport.OFFLINE
        isVpn -> Transport.VPN
        caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> Transport.WIFI
        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> Transport.CELLULAR
        caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> Transport.ETHERNET
        else -> Transport.OTHER
    }
    val addresses = cm.getLinkProperties(network)?.linkAddresses.orEmpty()
        .map { it.address }
        .filter { !it.isLoopbackAddress && !it.isLinkLocalAddress }
        .mapNotNull { it.hostAddress?.substringBefore('%') }
    return NetworkState(transport, isVpn, addresses)
}
