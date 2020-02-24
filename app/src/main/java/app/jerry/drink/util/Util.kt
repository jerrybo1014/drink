package app.jerry.drink.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import app.jerry.drink.DrinkApplication

object Util {

    /**
     * Determine and monitor the connectivity status
     *
     * https://developer.android.com/training/monitoring-device-state/connectivity-monitoring
     */
    fun isInternetConnected(): Boolean {
        val cm = DrinkApplication.instance
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun getString(resourceId: Int): String {
        return DrinkApplication.instance.getString(resourceId)
    }

    fun getColor(resourceId: Int): Int {
        return DrinkApplication.instance.getColor(resourceId)
    }
}