package jp.paming.positionedphoto

import android.content.ContentResolver
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import jp.paming.positionedphoto.databinding.PhotoCardBinding
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onCreatePhotoPermission {
            readAndShowPhoto(locswitch.isChecked)
        }
        locswitch.setOnCheckedChangeListener { buttonView, isChecked ->
            readAndShowPhoto(isChecked)
        }
    }

    // TODO extension化
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    readAndShowPhoto(locswitch.isChecked)

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

    private fun readAndShowPhoto(onlyLoc:Boolean){
        var photoDataList = read()
        Log.d("photoDataList","${photoDataList}")
        if( onlyLoc ) {
            photoDataList = photoDataList.filter {
                it.loc != null
            }
        }
        Log.d("photoDataList_loc","${photoDataList}")
        val layoutManager = GridLayoutManager(this,2)
        recycleView.layoutManager = layoutManager
        recycleView.adapter = MyRecycleAdapter(
            this,
            contentResolver,
            photoDataList
        )
    }


    // RecyclerView.ViewHolderを継承した自作ViewHolder
    // 親クラスの初期化にはBinding.rootで親Viewを渡し、
    // 子クラスはbindingを保持する
    class PhotoCardDataViewHolder(val binding: PhotoCardBinding) : RecyclerView.ViewHolder(binding.root)

    class MyRecycleAdapter(val context: Context, val contentResolver: ContentResolver, val list:List<PhotoData>) : RecyclerView.Adapter<PhotoCardDataViewHolder>() {
        private val layoutInflater = LayoutInflater.from(context)

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoCardDataViewHolder {
            val binding:PhotoCardBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.photo_card, parent, false)
            // クリックリスナを搭載
            val viewHolder = PhotoCardDataViewHolder(binding)

            binding.root.setOnClickListener{
                val position = viewHolder.getAdapterPosition() // positionを取得
                // 何かの処理をします
                // TODO Intentでuriを渡す
                Log.d("setOnClickListener","${position}")
                val uri = list[position].uri
                // ここ不自然
                val intent = Intent(context, DetailActivity::class.java)
                context.startActivity(intent)
            }
            return viewHolder
        }

        override fun onBindViewHolder(holder: PhotoCardDataViewHolder, position: Int) {
            // ここではモデルに値をセットしている
            // →DataBindingにより、自動でViewに反映される
            holder.binding.date = list[position].dateString
            holder.binding.visibleLocationIcon = list[position].loc != null
            //Picasso.get().load(list[position].uri).fit().centerInside().into(holder.binding.imageView)
            Glide.with(context).load(list[position].uri).into(holder.binding.imageView)
        }
    }
}
