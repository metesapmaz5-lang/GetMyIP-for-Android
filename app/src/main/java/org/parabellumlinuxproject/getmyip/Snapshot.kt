package org.parabellumlinuxproject.getmyip

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.parabellumlinuxproject.getmyip.core.Family
import org.parabellumlinuxproject.getmyip.core.IpInfo
import org.parabellumlinuxproject.getmyip.core.IpProvider
import org.parabellumlinuxproject.getmyip.core.TorExitList
import org.parabellumlinuxproject.getmyip.net.NetworkState
import org.parabellumlinuxproject.getmyip.net.readNetworkState

data class Snapshot(
    val v4: IpInfo?,
    val v6: IpInfo?,
    val net: NetworkState,
    val tor: Boolean?,
)

suspend fun takeSnapshot(context: Context, provider: IpProvider): Snapshot =
    withContext(Dispatchers.IO) {
        coroutineScope {
            val v4 = async { runCatching { provider.fetch(Family.V4) }.getOrNull() }
            val v6 = async { runCatching { provider.fetch(Family.V6) }.getOrNull() }
            val exits = async { runCatching { TorExitList.get() }.getOrNull() }
            val net = readNetworkState(context)
            val v4Info = v4.await()
            val exitSet = exits.await()
            Snapshot(
                v4 = v4Info,
                v6 = v6.await(),
                net = net,
                tor = if (v4Info == null || exitSet == null) null else v4Info.ip in exitSet,
            )
        }
    }
