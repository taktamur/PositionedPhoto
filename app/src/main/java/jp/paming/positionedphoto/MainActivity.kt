package jp.paming.positionedphoto

import android.arch.lifecycle.*
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import jp.paming.positionedphoto.databinding.PhotoCardBinding
import android.content.Intent
import android.databinding.BindingAdapter
import android.net.Uri
import android.support.v7.util.DiffUtil
import android.widget.ImageView
import jp.paming.positionedphoto.databinding.ActivityMainBinding
import android.support.v7.widget.GridLayoutManager


class MainActivity : AppCompatActivity(),LifecycleOwner {

    private lateinit var mainViewModel:MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun ActivityMainBinding.getGridLayoutManager():GridLayoutManager? {
            return (this.recycleView.layoutManager as? GridLayoutManager)
        }

        val binding:ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.setLifecycleOwner(this)

        // ViewModelProvidersでViewModelを作る時のコンストラクタで値を渡す為に、
        // Factoryクラスを作ってそれで生成している
        // TODO ここFactoryクラス作って、MyAppのServiceの参照を使ってインスタンスを作る
        mainViewModel = ViewModelProviders.of(this)
            .get(MainViewModel::class.java)
            .also{
                it.photoRepository = (application as MyApp).photoRepository
                it.orientationService = (application as MyApp).orientationService
                it.onClickListener = this::onClickItem
                binding.getGridLayoutManager()?.let{ manager: GridLayoutManager ->
                    it.updateGridSpanListener = manager::setSpanCount
                }
            }
        binding.mainViewModel = mainViewModel
        binding.adapter = ItemAdapter()

        mainViewModel.updateGridSpanCount()
        onCreatePhotoPermission {
            mainViewModel.updateItems()
        }
        // TODO RecyclerViewの縦インジケータ表示
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        onRequestPermissionsResultPhotoPermission(
            requestCode,
            grantResults
        ){
            mainViewModel.updateItems()
        }
    }

    private fun onClickItem(photoData: PhotoData) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.INTENT_EXTRA_PHOTODATA, photoData)
        }
        this.startActivity(intent)
    }
}


class MainViewModel: ViewModel() {
    var photoRepository:PhotoRepository? = null
    var orientationService:OrientationService? = null
    var onClickListener:((PhotoData)->Unit)? = null
    var updateGridSpanListener:((Int)->Unit)? = null

    // ここはObservable<T>やLiveDataでないと、DataBindingを経由して変更通知が届かない
    val items: MutableLiveData<List<ItemViewModel>> = MutableLiveData()
    var onlyPositioned: Boolean = true

    // TODO 双方向バインディング化
    fun onCheckedChanged(checked: Boolean) {
        onlyPositioned = checked
        this.updateItems()
    }

    fun updateItems() {
        val list = photoRepository?.find(onlyPositioned) ?: emptyList()
        items.value = list.map {
            ItemViewModel(it, onClickListener)
        }
    }

    fun updateGridSpanCount(){
        val spanCount = when(orientationService?.orientation()){
            Orientation.Portrait -> 2
            Orientation.Landscape -> 4
            else -> 1
        }
        updateGridSpanListener?.invoke(spanCount)
    }
}




// RecyclerView.ViewHolderを継承した自作ViewHolder
// 親クラスの初期化にはBinding.rootで親Viewを渡し、
// 子クラスはbindingを保持する
class PhotoCardDataViewHolder(val binding: PhotoCardBinding) : RecyclerView.ViewHolder(binding.root)

class ItemAdapter : RecyclerView.Adapter<PhotoCardDataViewHolder>() {

    private var items:List<ItemViewModel> = listOf()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoCardDataViewHolder {
        val binding = PhotoCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return PhotoCardDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoCardDataViewHolder, position: Int) {
        // ここではモデルに値をセットしている
        // →DataBindingにより、自動でViewに反映される
        holder.binding.itemViewModel = items[position]
    }

    fun update(newItems: List<ItemViewModel>){
        // DiffUtilでいい感じに差分更新してくれる。
        // https://qiita.com/Tsutou/items/69a28ebbd69b69e51703
        val diff = DiffUtil.calculateDiff(Callback(items, newItems), true)
        items = newItems
        diff.dispatchUpdatesTo(this)
    }

    private class Callback(private val old: List<ItemViewModel>,
                           private val new: List<ItemViewModel>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size
        override fun getNewListSize(): Int = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            old[oldItemPosition].getUri() == new[newItemPosition].getUri()

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            old[oldItemPosition].getUri() == new[newItemPosition].getUri()
    }
}

@BindingAdapter("app:viewModels")
fun RecyclerView.setViewModels(newItems: List<ItemViewModel>) {
    val adapter = this.adapter as ItemAdapter
    adapter.update(newItems.toList())
}


class ItemViewModel(private val photoData:PhotoData,
                    private val listener:((PhotoData)->Unit)?){

    fun getDate():String = photoData.getDateString()

    fun getUri():Uri = photoData.uri

    fun getVisibleLocationIcon():Boolean = (photoData.loc != null)

    fun onClick() = listener?.invoke(photoData)
}

@BindingAdapter("imageUri")
fun ImageView.loadImage(uri:Uri) {
    // TODO Cardのサイズいっぱいに引き延ばす
    Glide.with(this.context).load(uri).into(this)
}
