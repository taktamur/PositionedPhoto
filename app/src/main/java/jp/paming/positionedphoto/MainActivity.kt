package jp.paming.positionedphoto

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import jp.paming.positionedphoto.databinding.PhotoCardBinding
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.BindingAdapter
import android.databinding.ObservableArrayList
import android.net.Uri
import android.support.v7.util.DiffUtil
import android.widget.ImageView
import android.widget.Toast
import jp.paming.positionedphoto.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.viewModel = {
            MainViewModel(PhotoRepositoryImpl(this)).also {
                it.callback = ::readAndShowPhoto
            }
        }()
        binding.adapter = ItemAdapter(this).also {
            it.onClick = ::onClick
        }
        onCreatePhotoPermission {
            readAndShowPhoto()
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
                    readAndShowPhoto()

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

    private fun readAndShowPhoto(){
        binding.viewModel?.update(locswitch.isChecked )
    }


    fun onClick(photoData:PhotoData){
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.INTENT_EXTRA_PHOTODATA, photoData)
        }
        this.startActivity(intent)
    }


}

// RecyclerView.ViewHolderを継承した自作ViewHolder
// 親クラスの初期化にはBinding.rootで親Viewを渡し、
// 子クラスはbindingを保持する
class PhotoCardDataViewHolder(val binding: PhotoCardBinding) : RecyclerView.ViewHolder(binding.root)

//class ItemAdapter(private val context: Context) : RecyclerView.Adapter<PhotoCardDataViewHolder>() {
class ItemAdapter(private val context: Context) : RecyclerView.Adapter<PhotoCardDataViewHolder>() {
    var onClick:((data:PhotoData)->Unit)? = null
    var items:List<ItemViewModel> = listOf()

    // TODO DiffUtilでいい感じに差分更新してくれるみたい。
    // https://qiita.com/Tsutou/items/69a28ebbd69b69e51703
//    private val layoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoCardDataViewHolder {
        val binding = PhotoCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        // クリックリスナを搭載
        // TODO クリックイベントはViewModel経由に変更
        val viewHolder = PhotoCardDataViewHolder(binding)
        onClick?.let { callback ->
            binding.root.setOnClickListener{
                val position = viewHolder.adapterPosition // positionを取得
                val data = items[position]
                Log.d("setOnClickListener","$position")
                callback(data.photoData)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PhotoCardDataViewHolder, position: Int) {
        // ここではモデルに値をセットしている
        // →DataBindingにより、自動でViewに反映される
        holder.binding.itemViewModel = items[position]
    }

}


class MainViewModel(val repository:PhotoRepository) {
    var callback:(()->Unit)? = null
    // ここはObservable<T>でないと、DataBindingを経由して変更通知が届かない
    val items: ObservableArrayList<ItemViewModel> = ObservableArrayList()

    fun onCheckedChanged(checked: Boolean) {
        callback?.let{
            it()
        }
    }
    fun updatePhotoList(list:List<PhotoData>){
        items.clear()
        items.addAll(list.map{
            ItemViewModel(it)
        })
    }
    fun update(isPositioned:Boolean){
        val items = repository.find(isPositioned)
        this.updatePhotoList(items)
    }
}

class ItemViewModel(val photoData:PhotoData){
    fun getDate():String {
        return photoData.dateString
    }
    fun getUri():Uri {
        return photoData.uri
    }
    fun getVisibleLocationIcon():Boolean {
        return photoData.loc != null
    }
}

class Callback(private val old: List<ItemViewModel>,
               private val new: List<ItemViewModel>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size
    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        old[oldItemPosition].getUri() == new[newItemPosition].getUri()


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        old[oldItemPosition].getUri() == new[newItemPosition].getUri()
}


@BindingAdapter("app:viewModels")
fun RecyclerView.setViewModels(items: ObservableArrayList<ItemViewModel>) {
    val adapter = this.adapter as ItemAdapter
    val diff = DiffUtil.calculateDiff(Callback(adapter.items, items), true)
    // ここでListに変換してあげないとdispatchUpdatesToをかけれない
    adapter.items = items.toList()
    diff.dispatchUpdatesTo(adapter)
}


@BindingAdapter("imageUri")
fun ImageView.loadImage(uri:Uri) {
    Glide.with(this.context).load(uri).into(this)
}
