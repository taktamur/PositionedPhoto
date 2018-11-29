package jp.paming.positionedphoto

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.activity_detail.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    // TODO パラメータ毎にINTENTを作るのめんどくさい
    companion object {
        const val INTENT_EXTRA_URI = "IntentExtraUri"
        const val INTENT_EXTRA_LAT = "IntentExtraLat"
        const val INTENT_EXTRA_LON = "IntentExtraLon"
    }

    private var loc:Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val message = intent.getStringExtra(DetailActivity.INTENT_EXTRA_URI)
        if( intent.hasExtra(DetailActivity.INTENT_EXTRA_LAT) &&
                intent.hasExtra(DetailActivity.INTENT_EXTRA_LON)){
            val lat = intent.getDoubleExtra(DetailActivity.INTENT_EXTRA_LAT,0.0)
            val lon = intent.getDoubleExtra(DetailActivity.INTENT_EXTRA_LON,0.0)
            loc = Location(lat,lon)
        }
        val uri = Uri.parse(message)
        Glide.with(this).load(uri).into(imageView)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        if( loc != null ) {
            // 背景地図の表示
            mapFragment.getMapAsync(this)
        }else{
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.hide(mapFragment)
            fragmentTransaction.commit()
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        if( map == null) return
        loc?.let{
            val newLocation = LatLng(it.lat, it.lon)
            map.addMarker(
                MarkerOptions()
                    .position(newLocation)
            )
            map.moveCamera(CameraUpdateFactory.newLatLng(newLocation))
            map.moveCamera(CameraUpdateFactory.zoomTo(12.0f))
        }
    }
}
