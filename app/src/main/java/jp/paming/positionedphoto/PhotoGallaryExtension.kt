package jp.paming.positionedphoto

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

val REQUEST_PERMISSION_CODE = 1234

fun AppCompatActivity.onCreatePhotoPermission(grant:()->Unit){
    // Here, thisActivity is the current activity
    if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            Toast.makeText(this, "パーミッションがOFFになっています。", Toast.LENGTH_SHORT).show()

        } else {

            // No explanation needed, we can request the permission.
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            ActivityCompat.requestPermissions(this,
                arrayOf(permission),
                REQUEST_PERMISSION_CODE)

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }else {
        grant()
    }
}


