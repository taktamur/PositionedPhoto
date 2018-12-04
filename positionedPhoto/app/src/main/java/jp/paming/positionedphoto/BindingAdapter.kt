package jp.paming.positionedphoto

import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide


@BindingAdapter("imageUri")
fun ImageView.loadImage(uri: Uri) {
    // TODO Cardのサイズいっぱいに引き延ばす
    Glide.with(this.context).load(uri).into(this)
}
