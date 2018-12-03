package jp.paming.positionedphoto.ui

import android.arch.lifecycle.*
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.content.Intent
import android.databinding.BindingAdapter
import jp.paming.positionedphoto.databinding.ActivityMainBinding
import android.support.v7.widget.GridLayoutManager
import jp.paming.positionedphoto.*
import jp.paming.positionedphoto.R
import jp.paming.positionedphoto.service.PhotoData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider


class MainActivity : AppCompatActivity(),LifecycleOwner,OnItemClickListner {

    // TODO ここにViewModelは持たないようにする
 //   private lateinit var mainViewModel: MainViewModel

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun ActivityMainBinding.getGridLayoutManager():GridLayoutManager? {
            return (this.recycleView.layoutManager as? GridLayoutManager)
        }

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.setLifecycleOwner(this)

        // ViewModelProvidersでViewModelを作る時のコンストラクタで値を渡す為に、
        // Factoryクラスを作ってそれで生成している
        // TODO ここFactoryクラス作って、MyAppのServiceの参照を使ってインスタンスを作る
        // Factoryクラスの書き方：
        //  https://starzero.hatenablog.com/entry/2017/05/19/005437
        val mainViewModel = ViewModelProviders.of(this,Factory(
            this,
            binding.getGridLayoutManager()
        ))
            .get(MainViewModel::class.java)
        // ここManagerを再設定しているのは、回転時の列数が1になったから
        // TODO BindingAdapterでの設定になれば、この上書きも不要になると思う
        mainViewModel.updateGridSpanListener = binding.getGridLayoutManager()

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
            binding.mainViewModel?.updateItems()
        }
    }

    override fun onItemClick(photoData: PhotoData) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.INTENT_EXTRA_PHOTODATA, photoData)
        }
        this.startActivity(intent)
    }

    class Factory(
        val mainActibity: MainActivity,
        val gridLayoutManager: GridLayoutManager?
        ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val mainViewModel = MainViewModel()
            val application = mainActibity.application as MyApp
            mainViewModel.photoRepository = application.photoRepository
            mainViewModel.orientationService = application.orientationService
            mainViewModel.onClickListener = mainActibity
            mainViewModel.updateGridSpanListener = gridLayoutManager
            return mainViewModel as T
        }
    }
}


@BindingAdapter("app:viewModels")
fun RecyclerView.setViewModels(newMainItem: List<MainItemViewModel>) {
    val adapter = this.adapter as MainItemAdapter
    adapter.update(newMainItem.toList())
}

