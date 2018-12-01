package jp.paming.positionedphoto

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class PhotoData constructor(
    val uri: Uri,
    val date: Date,
    val loc: LatLng?): Parcelable {

    @SuppressLint("SimpleDateFormat")
    fun getDateString():String {
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm")
        return df.format(date)
    }
}


interface PhotoRepository{
    fun find(isPositioned:Boolean): List<PhotoData>
}

// TODO 日付と緯度経度は、ファイルの内容から取得するように変更
class PhotoRepositoryImpl(context: Context):PhotoRepository {
    private val contentResolver = context.contentResolver

    @SuppressLint("Recycle")
    override fun find(isPositioned: Boolean): List<PhotoData> {
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} desc"
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            sortOrder
        ) ?: return emptyList()

        Log.d("column", "${cursor.columnCount}")
        for (i in (0 until cursor.columnCount)) {
            Log.d("column_name", cursor.getColumnName(i))
        }
        Log.d("count", "${cursor.count}")

        val list = ArrayList<PhotoData>()
        if (cursor.moveToFirst()) {
            do {
                val bmpUri = {
                    val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                }()
                val date = {
                    val dateSec = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
                    Date(dateSec * 1000)

                }()
                val lat = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE))
                val lon = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE))
                val loc = when {
                    lat != 0.0 && lon != 0.0 -> LatLng(lat, lon)
                    else -> null
                }
                list.add(PhotoData(bmpUri, date, loc))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return when( isPositioned ) {
            true -> list.filter { it.loc != null }
            false -> list
        }
    }
}
