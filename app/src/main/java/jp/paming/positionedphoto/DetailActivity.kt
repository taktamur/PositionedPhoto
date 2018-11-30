package jp.paming.positionedphoto

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.activity_detail.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions


class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val INTENT_EXTRA_PHOTODATA = "IntentExtraUri"
    }

    private lateinit var photoData:PhotoData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        photoData = intent.getParcelableExtra(DetailActivity.INTENT_EXTRA_PHOTODATA)
        Glide.with(this).load( photoData.uri).into(imageView)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        if( photoData.loc != null ) {
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
        photoData.loc?.let{
            map.addMarker(
                MarkerOptions()
                    .position(it)
            )
            map.moveCamera(CameraUpdateFactory.newLatLng(it))
            map.moveCamera(CameraUpdateFactory.zoomTo(12.0f))
        }
    }
}
