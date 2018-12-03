package jp.paming.positionedphoto.ui

import android.arch.lifecycle.*
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.bumptech.glide.Glide
import android.content.Intent
import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import jp.paming.positionedphoto.databinding.ActivityMainBinding
import android.support.v7.widget.GridLayoutManager
import jp.paming.positionedphoto.*
import jp.paming.positionedphoto.R
import jp.paming.positionedphoto.service.PhotoData


class MainActivity : AppCompatActivity(),LifecycleOwner {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun ActivityMainBinding.getGridLayoutManager():GridLayoutManager? {
            return (this.recycleView.layoutManager as? GridLayoutManager)
        }

        val binding:ActivityMainBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
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
        binding.adapter = MainItemAdapter()

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


@BindingAdapter("app:viewModels")
fun RecyclerView.setViewModels(newMainItem: List<MainItemViewModel>) {
    val adapter = this.adapter as MainItemAdapter
    adapter.update(newMainItem.toList())
}

