package jp.paming.positionedphoto

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
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
import android.databinding.ObservableArrayList
import android.net.Uri
import android.support.v7.util.DiffUtil
import android.widget.ImageView
import jp.paming.positionedphoto.databinding.ActivityMainBinding
import android.arch.lifecycle.ViewModelProvider




class MainActivity : AppCompatActivity(),ItemViewModel.Listener,LifecycleOwner {

    private lateinit var mainViewModel:MainViewModel

    // TODO 横画面では4行表示にする
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // ViewModelProvidersでViewModelを作る時のコンストラクタで値を渡す為に、
        // Factoryクラスを作ってそれで生成している
        mainViewModel = ViewModelProviders.of(
            this,
            MainViewModel.Factory(
                PhotoRepositoryImpl(this),
                this)
        ).get(MainViewModel::class.java)

        val binding:ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = mainViewModel
        binding.adapter = ItemAdapter()
        onCreatePhotoPermission {
            updateMainViewModel()
        }
        // TODO RecyclerViewの縦インジケータ表示
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        onRequestPermissionsResultPhotoPermission(
            requestCode,
            permissions,
            grantResults
        ){
            updateMainViewModel()
        }
    }

    private fun updateMainViewModel(){
        mainViewModel.update()
    }

    override fun onClickItem(photoData: PhotoData) {
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

    class Callback(private val old: List<ItemViewModel>,
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

class MainViewModel(
    private var repository:PhotoRepository,
    private var listener:ItemViewModel.Listener
): ViewModel(),ItemViewModel.Listener {

    // ここはObservable<T>でないと、DataBindingを経由して変更通知が届かない
    val items: ObservableArrayList<ItemViewModel> = ObservableArrayList()

    var onlyPositioned: Boolean = true

    // TODO 双方向バインディング化
    fun onCheckedChanged(checked: Boolean) {
        onlyPositioned = checked
        this.update()
    }

    fun update() {
        val list = repository.find(onlyPositioned)
        items.clear()
        // TODO 日付でのソート
        items.addAll(list.map {
            ItemViewModel(it, this)
        })
    }

    override fun onClickItem(photoData: PhotoData) {
        listener.onClickItem(photoData)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(var repository:PhotoRepository,
                  var listener:ItemViewModel.Listener) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository,listener) as T
        }
    }
}



class ItemViewModel(private val photoData:PhotoData,
                    private val listener:Listener){
    fun getDate():String {
        return photoData.dateString
    }
    fun getUri():Uri {
        return photoData.uri
    }
    fun getVisibleLocationIcon():Boolean {
        return photoData.loc != null
    }
    fun onClick() {
        listener.onClickItem(photoData)
    }
    interface Listener{
        fun onClickItem(photoData:PhotoData)
    }
}

@BindingAdapter("imageUri")
fun ImageView.loadImage(uri:Uri) {
    Glide.with(this.context).load(uri).into(this)
}
