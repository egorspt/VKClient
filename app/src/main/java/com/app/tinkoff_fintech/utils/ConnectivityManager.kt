package com.app.tinkoff_fintech.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.widget.Toast
import com.app.tinkoff_fintech.R
import javax.inject.Inject

class ConnectivityManager @Inject constructor(private val context: Context) {
    private var connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var netList = mutableListOf<Int>()
    private var firstConnection = true

    init {
        val networkRequestBuilder = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(
            networkRequestBuilder,
            object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    netList.remove(network.describeContents())
                    notifyConnection()
                }

                override fun onAvailable(network: Network) {
                    netList.add(network.describeContents())
                    if (firstConnection)
                        firstConnection = false
                    else notifyConnection()
                }
            })
    }

    fun isConnection() = netList.isNotEmpty()

    fun notifyConnection() {
        if (netList.isEmpty())
            Toast.makeText(context, context.getString(R.string.connectionLost), Toast.LENGTH_SHORT)
                .show()
        else Toast.makeText(context, context.getString(R.string.connectionAvailable), Toast.LENGTH_SHORT)
            .show()
    }
}