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


class PhotoBitmapUtils {
    /**
     * 存放拍摄图片的文件夹
     */
    val FILES_NAME = "/MyPhoto";
    /**
     * 获取的时间格式
     */
    val TIME_STYLE = "yyyyMMddHHmmss";
    /**
     * 图片种类
     */
    val IMAGE_TYPE = ".png";

    // 防止实例化
//    private PhotoBitmapUtils() {
//    }

    /**
     * 获取手机可存储路径
     *
     * @param context 上下文
     * @return 手机可存储路径
     */
//    private static String getPhoneRootPath(Context context) {
//        // 是否有SD卡
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
//            || !Environment.isExternalStorageRemovable()) {
//            // 获取SD卡根目录
//            return context.getExternalCacheDir().getPath();
//        } else {
//            // 获取apk包下的缓存路径
//            return context.getCacheDir().getPath();
//        }
//    }

    /**
     * 使用当前系统时间作为上传图片的名称
     *
     * @return 存储的根路径+图片名称
     */
    fun getPhotoFileName(context: Context): String {
        val file = File(context.cacheDir.path + FILES_NAME)
        // 判断文件是否已经存在，不存在则创建
        if (!file.exists()) {
            file.mkdirs()
        }
        // 设置图片文件名称
        val format = SimpleDateFormat(TIME_STYLE, Locale.getDefault())
        val date = Date(System.currentTimeMillis())
        val time = format.format(date)
        val photoName = "/$time"
        return "$file$photoName"
    }

    /**
     * 保存Bitmap图片在SD卡中
     * 如果没有SD卡则存在手机中
     *
     * @param mbitmap 需要保存的Bitmap图片
     * @return 保存成功时返回图片的路径，失败时返回null
     */
    fun savePhotoToSD(mbitmap: Bitmap, context: Context): String {
        var outStream: FileOutputStream? = null
        val fileName = getPhotoFileName(context)
        try {
            outStream = FileOutputStream(fileName)
            // 把数据写入文件，100表示不压缩
            mbitmap.compress(Bitmap.CompressFormat.PNG, 50, outStream);
            return fileName
        } catch (e: Exception) {
            e.printStackTrace()
            return "$e"
        } finally {
            try {
                outStream?.close()
                mbitmap?.recycle()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 把原图按1/10的比例压缩
     *
     * @param path 原图的路径
     * @return 压缩后的图片
     */
    fun getCompressPhoto(path: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        options.inSampleSize = 10  // 图片的大小设置为原来的十分之一
        val bmp = BitmapFactory.decodeFile(path, options)
//        options = null!!
        return bmp
    }

    /**
     * 处理旋转后的图片
     * @param originpath 原图路径
     * @param context 上下文
     * @return 返回修复完毕后的图片路径
     */
    fun amendRotatePhoto(originpath: String, context: Context): String {

        // 取得图片旋转角度
        val angle = readPictureDegree(originpath)

        // 把原图压缩后得到Bitmap对象
        val bmp = getCompressPhoto(originpath)

        // 修复图片被旋转的角度
        val bitmap = rotaingImageView(angle, bmp)

        // 保存修复后的图片并返回保存后的图片路径
        return savePhotoToSD(bitmap, context)
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            degree = when(orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    /**
     * 旋转图片
     * @param angle 被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    fun rotaingImageView(angle: Int, bitmap: Bitmap): Bitmap {
        var returnBm: Bitmap? = null
        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: OutOfMemoryError) {
        }
        if (returnBm == null) {
            returnBm = bitmap
        }
        if (bitmap != returnBm) {
            bitmap.recycle()
        }
        return returnBm
    }
}
