package com.app.tinkoff_fintech.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import javax.inject.Inject

typealias OnLostListener = () -> Unit
typealias OnAvailableListener = () -> Unit

class ConnectivityManager @Inject constructor(context: Context){
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val listOfLostListener = mutableListOf<OnLostListener>()
    val listOfAvailableListener = mutableListOf<OnAvailableListener>()

    init {
        val networkRequestBuilder = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequestBuilder, object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                listOfLostListener.forEach { function -> function }
            }

            override fun onAvailable(network: Network) {
                listOfAvailableListener.forEach { function -> function }
            }
        })
    }
}