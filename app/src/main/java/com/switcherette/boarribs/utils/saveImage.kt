import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


@Throws(IOException::class)
fun saveImage(context: Context, bitmap: Bitmap, name: String): File? {
    val saved: Boolean
    var image: File? = null
    val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver: ContentResolver = context.contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/")
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        image = imageUri?.let { getRealPathFromUri(context, it) }?.let { File(it) }
        imageUri?.let { resolver.openOutputStream(it) }
    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            .toString() + File.separator
        val file = File(imagesDir)
        if (!file.exists()) {
            file.mkdir()
        }

        image = File(imagesDir, "$name.png")
        FileOutputStream(image)
    }
    saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    fos?.flush()
    fos?.close()

    return image
}