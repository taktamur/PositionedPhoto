package jp.paming.positionedphoto

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detail.*

// TODO 地図を表示
class DetailActivity : AppCompatActivity() {
    companion object {
        const val INTENT_EXTRA_URI = "IntentExtraUri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val message = intent.getStringExtra(DetailActivity.INTENT_EXTRA_URI)
        val uri = Uri.parse(message)
        Glide.with(this).load(uri).into(imageView)

    }
}
