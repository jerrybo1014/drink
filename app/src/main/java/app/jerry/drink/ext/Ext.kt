package app.jerry.drink.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.media.ExifInterface
import android.net.Uri
import app.jerry.drink.DrinkApplication
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*

fun Long.toDisplayFormat(): String {
    return SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.TAIWAN).format(this)
}

fun Long.toDisplayTimePass(): String {

    val now = System.currentTimeMillis()
    val diff = (now - this)/1000

    val years = diff / (60 * 60 * 24 * 30 * 12)
    val  months = diff / (60 * 60 * 24 * 30)
    val days = diff / (60 * 60 * 24)
//    val hours = (diff - days * (60 * 60 * 24)) / (60 * 60)
    val hours = diff / (60 * 60)
//    val minutes = (diff - days * (60 * 60 * 24) - hours * (60 * 60)) / 60
    val minutes = diff / (60)

    return when {
        years >=1 -> "${years}年前"
        months >= 1 -> "${months}個月前"
        days >= 1 -> "${days}天前"
        hours >= 1 -> "${hours}小時前"
        minutes >= 1 -> "${minutes}分鐘前"
        else -> "剛剛"
    }

    }

fun Uri.getBitmap(width: Int, height: Int): Bitmap? {
    var rotatedDegree = 0
    var stream = DrinkApplication.context.contentResolver.openInputStream(this)
    /** GET IMAGE ORIENTATION */
    if(stream != null) {
        val exif = ExifInterface(stream)
        rotatedDegree = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL).fromExifInterfaceOrientationToDegree()
        stream.close()
    }
    /** GET IMAGE SIZE */
    stream = DrinkApplication.context.contentResolver.openInputStream(this)
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeStream(stream, null,options)
    try {
        stream?.close()
    } catch (e: NullPointerException) {
        e.printStackTrace()
        return null
    }
    // The resulting width and height of the bitmap
    if(options.outWidth == -1 || options.outHeight == -1) return null
    var bitmapWidth = options.outWidth.toFloat()
    var bitmapHeight = options.outHeight.toFloat()
    if (rotatedDegree == 90) {
        // Side way -> options.outWidth is actually HEIGHT
        //          -> options.outHeight is actually WIDTH
        bitmapWidth = options.outHeight.toFloat()
        bitmapHeight = options.outWidth.toFloat()
    }
    var scale = 1
    while(true) {
        if(bitmapWidth / 2 < width || bitmapHeight / 2 < height)
            break
        bitmapWidth /= 2
        bitmapHeight /= 2
        scale *= 2
    }
    val finalOptions = BitmapFactory.Options()
    finalOptions.inSampleSize = scale
    stream = DrinkApplication.context.contentResolver.openInputStream(this)
    val bitmap = BitmapFactory.decodeStream(stream, null, finalOptions)
    try {
        stream?.close()
    } catch (e: NullPointerException) {
        e.printStackTrace()
        return null
    }
    val matrix = Matrix()
    if (rotatedDegree != 0) {
        matrix.preRotate(rotatedDegree.toFloat())
    }
    var bmpWidth = 0
    try {
        if (bitmap == null) {
            return null
        }
        bmpWidth = bitmap.width
    } catch (e: Exception) {
        return null
    }
    var adjustedBitmap = bitmap
    if (bmpWidth > 0) {
        adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    return adjustedBitmap
}

fun Int.fromExifInterfaceOrientationToDegree(): Int {
    return when(this) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
}