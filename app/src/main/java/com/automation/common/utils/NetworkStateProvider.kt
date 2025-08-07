package com.automation.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.BatteryManager
import android.text.format.Formatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.getKoin
import timber.log.Timber
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Enumeration

object NetworkStateProvider {
    private var started: Boolean = false
    private val appContext: Context = getKoin().get()
    var network: MutableMap<Network, NetworkCapabilities?> = HashMap()
    private var batteryManager: BatteryManager? = null
    private val _state: MutableStateFlow<NetworkState> = MutableStateFlow(NetworkState.UNAVAILABLE)
    val state: StateFlow<NetworkState> = _state.asStateFlow()
    var internetState: Boolean = true
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        scope.launch {
            checkInternetConnection()
        }
    }

    private var isDataNetworkAwailable: Boolean = false
        get() =
            checkCapability { hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) }
        private set

    private var isUnmetteredNetworkAvailable: Boolean = false
        get() =
            checkCapability { hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) }
        private set

    private val isWifi: Boolean
        get() = checkCapability { hasTransport(NetworkCapabilities.TRANSPORT_WIFI) }

    private var isMobileNetworkAvailable: Boolean = false
        get() =
            checkCapability { hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) }
        private set

    private inline fun checkCapability(condition: NetworkCapabilities.() -> Boolean): Boolean =
        network.any { networkCapability ->
            networkCapability.value
                ?.condition()
                ?: false
        }

    fun start(context: Context) {
        check(!started) { "Already started" }
        started = true

        try {
            val connectivityManager: ConnectivityManager? =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            connectivityManager?.registerNetworkCallback(
                NetworkRequest.Builder().build(),
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) =
                        onNetworkAvailable(connectivityManager, network)

                    override fun onLost(network: Network) = onNetworkLost(network)
                }
            )

        } catch (e: SecurityException) {
            Timber.tag(NetworkStateProvider::class.simpleName.toString())
                .e(e, "NetworkStateProvider#start")
        }

        batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }

    private fun onNetworkAvailable(
        connectivityManager: ConnectivityManager,
        network: Network
    ) {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        NetworkStateProvider.network[network] = networkCapabilities
        Timber.tag(NetworkStateProvider::class.simpleName.toString())
            .v("[NET] Found network NET_CAPABILITY_NOT_METERED: %s", isUnmetteredNetworkAvailable)
        Timber.tag(NetworkStateProvider::class.simpleName.toString())
            .v("[NET] NET_CAPABILITY_INTERNET: %s", isDataNetworkAwailable)
        updateState()
    }

    private fun onNetworkLost(network: Network) {
        NetworkStateProvider.network.remove(network)
        if (NetworkStateProvider.network.isEmpty()) {
            Timber.tag(NetworkStateProvider::class.simpleName.toString())
                .v("[NET] onLost No network")
        }
        updateState()
    }

    private fun updateState() {
        when {
            isWifi -> _state.value = NetworkState.WIFI
            isMobileNetworkAvailable -> _state.value = NetworkState.MOBILE
            !isDataNetworkAwailable -> _state.value = NetworkState.UNAVAILABLE
        }
    }

    fun getBatteryState(): Int {
        return batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: -1
    }

    fun getUpSpeedInMbps(context: Context): Int {
        val cm: ConnectivityManager? =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        return (cm?.getNetworkCapabilities(cm.activeNetwork)?.linkUpstreamBandwidthKbps ?: 0) / 1000
    }

    fun getDownSpeedInMbps(): Int {
        val cm: ConnectivityManager? =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        return (cm?.getNetworkCapabilities(cm.activeNetwork)?.linkDownstreamBandwidthKbps
            ?: 0) / 1000
    }

    fun getLocalIpAddress(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        val ip = Formatter.formatIpAddress(inetAddress.hashCode())
                        Timber.tag(NetworkStateProvider::class.simpleName.toString())
                            .i("***** IP=" + ip)
                        return ip
                    }
                }
            }
        } catch (ex: SocketException) {
            Timber.tag(NetworkStateProvider::class.simpleName.toString()).e(ex.toString())
        }
        return null
    }

    private suspend fun checkInternetConnection() {
        while (true) {
            runCatching {
                val ping1 = withContext(Dispatchers.IO) {
                    ping()
                }
                Timber.tag("PING").d("1: %s", ping1)
                delay(400)
                val ping2 = withContext(Dispatchers.IO) {
                    ping()
                }
                Timber.tag("PING").d("2: %s", ping2)
                delay(400)
                val state = ping1 && ping2
                internetState = state
                delay(4000)
            }.onFailure {
                internetState = false
            }
        }
    }

    fun ping(host: String = "77.88.8.8"): Boolean {
        return try {
            val runtime = Runtime.getRuntime()
            val ipProcess = runtime.exec("/system/bin/ping -c 1 $host");
            val exitValue = ipProcess.waitFor();
            (exitValue == 0);
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}


enum class NetworkState {
    UNAVAILABLE, MOBILE, WIFI
}