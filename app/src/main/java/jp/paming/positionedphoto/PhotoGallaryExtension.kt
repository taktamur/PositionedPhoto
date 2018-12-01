package jp.paming.positionedphoto

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

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
            // app-defined int constant. The readAndShowPhoto method gets the
            // result of the request.
        }
    }else {
        grant()
    }
}

fun AppCompatActivity.onRequestPermissionsResultPhotoPermission(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray,
    grant:()->Unit
) {
    when (requestCode) {
        REQUEST_PERMISSION_CODE -> {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                grant()

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "パーミッションが許可されませんでした。", Toast.LENGTH_SHORT).show()
            }
            return
        }
    }// other 'case' lines to check for other
    // permissions this app might request
}


