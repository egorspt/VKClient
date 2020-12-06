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
    private var netList = mutableListOf<String>()
    private var firstConnection = true

    init {
        val networkRequestBuilder = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(
            networkRequestBuilder,
            object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    if (!netList.contains(network.toString()))
                        return
                    netList.remove(network.toString())
                    if (netList.isEmpty())
                        showConnectionLost()
                }

                override fun onAvailable(network: Network) {
                    if (netList.contains(network.toString()))
                        return
                    netList.add(network.toString())
                    if (firstConnection)
                        firstConnection = false
                    else if (netList.size == 1)
                        showConnectionAvailable()
                }
            })
    }

    fun isConnection() = netList.isNotEmpty()

    fun notifyConnection() {
        if (netList.isEmpty())
            showConnectionLost()
        else showConnectionAvailable()
    }

    private fun showConnectionLost() {
        Toast.makeText(context, context.getString(R.string.connectionLost), Toast.LENGTH_SHORT)
            .show()
    }

    private fun showConnectionAvailable() {
        Toast.makeText(context, context.getString(R.string.connectionAvailable), Toast.LENGTH_SHORT)
            .show()
    }
}