package com.automation.data.api.client

import android.content.Context
import android.net.ConnectivityManager
import com.automation.common.utils.NetworkStateProvider
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.smskeeper.hmsmskeeper.data.api.client.NoInternetConnectionException
import timber.log.Timber
import java.net.InetAddress

class InternetAwareOkHttpClient(
    private val context: Context,
    private val baseClient: OkHttpClient
) : OkHttpClient() {

    override fun newCall(request: Request): Call {
        if (!isConnectedToInternet(context)) {
            Timber.tag("Internet ").e("off")
            throw NoInternetConnectionException()
        }

        if (!NetworkStateProvider.internetState) {
            Timber.tag("Internet ").e("off")
            throw NoInternetConnectionException()
        }

        if (!isConnected()) {
            Timber.tag("Internet ").e("off")
            throw NoInternetConnectionException()
        }
        return baseClient.newCall(request)
    }

    private fun isConnectedToInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = cm?.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    private fun isConnected(): Boolean {
        val address = InetAddress.getByName("77.88.8.8")
        val ans = address.isReachable(1000)
        Timber.tag("ANS").d(ans.toString())
        return ans
    }

}