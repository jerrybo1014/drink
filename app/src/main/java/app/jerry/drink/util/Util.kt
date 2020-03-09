package app.jerry.drink.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.Store

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

    class OnPermissionCheckedListener(val listener: (store: Store) -> Unit) {
        fun onPermissionChecked(store: Store) = listener(store)
    }

    fun checkGpsPermission(
        context: Context,
        onPermissionCheckedListener: OnPermissionCheckedListener
    ) {

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val hasGps =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!hasGps) {
            showDialogIfLocationServiceOff(context)
        } else {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    context as Activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PermissionCode.LOCATION.requestCode
                )
            } else {
                onPermissionCheckedListener.onPermissionChecked(Store())
            }
        }
    }

    fun checkGpsPermissionFragment(
        context: Context,
        onPermissionCheckedListener: OnPermissionCheckedListener,
        fragment: Fragment
    ) {

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val hasGps =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!hasGps) {
            showDialogIfLocationServiceOff(context)
        } else {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                fragment.requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PermissionCode.LOCATION.requestCode
                )
            } else {
                onPermissionCheckedListener.onPermissionChecked(Store())
            }
        }
    }

    fun openAppSettingsIntent(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openLocationIfDenied(context: Context) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(context)
                .setMessage(getString(R.string.need_gps_permission))
                .setPositiveButton(getString(R.string.open_permission)) { _, _ ->
                    openAppSettingsIntent(context)
                }
                .setNegativeButton(getString(R.string.permission_permanently_denied_negative_button)) { _, _ -> }
                .show()
        }
    }

    private fun showDialogIfLocationServiceOff(context: Context) {
        AlertDialog.Builder(context)
            .setMessage(getString(R.string.open_devices_location))
            .setPositiveButton(getString(R.string.open_permission)) { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(getString(R.string.permission_permanently_denied_negative_button)) { _, _ ->
                Toast.makeText(
                    DrinkApplication.context,
                    getString(R.string.devices_location_close),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .create()
            .show()
    }
}