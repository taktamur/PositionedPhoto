package jp.paming.positionedphoto

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.activity_detail.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


// TODO 地図の位置調整
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

        // 背景地図の表示
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // TODO 緯度経度が無い場合の地図表示をどうするか？
    }

    override fun onMapReady(map: GoogleMap?) {
        // TODO 地図の初期表示位置がおかしい
        map?.addMarker(
            MarkerOptions()
                .position(LatLng((loc?.lat ?: 0.0), (loc?.lon ?: 0.0)))
                .title("Marker")
        )
    }
}
