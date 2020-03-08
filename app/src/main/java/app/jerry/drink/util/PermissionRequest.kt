package app.jerry.drink.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import app.jerry.drink.DrinkApplication
import app.jerry.drink.MainActivity
import app.jerry.drink.R

class PermissionRequest (private val context: Context, private val activity: MainActivity){

    fun fineLocationIfDenied() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(context)
                .setMessage(Util.getString(R.string.need_gps_permission))
                .setPositiveButton(Util.getString(R.string.open_permission)) { _, _ ->
                    activity.startActivity(openAppSettingsIntent())
                }
                .setNegativeButton(Util.getString(R.string.permission_permanently_denied_negative_button)) { _, _ -> }
                .show()
        }
    }

    fun showDialogIfLocationServiceOff() {
        AlertDialog.Builder(context)
            .setMessage("請開啟裝置定位功能")
            .setPositiveButton("前往設定") { _, _ ->
                activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("先不要") { _, _ ->
                Toast.makeText(
                    DrinkApplication.context,
                    "未開啟定位服務",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .create()
            .show()
    }

    fun openAppSettingsIntent(): Intent {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }
}
