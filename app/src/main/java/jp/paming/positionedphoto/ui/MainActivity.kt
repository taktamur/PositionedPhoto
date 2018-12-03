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
import android.content.res.Configuration


class MainActivity : AppCompatActivity(),LifecycleOwner,ItemClickCallback {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.setLifecycleOwner(this)

        // ViewModelProvidersでViewModelを作る時のコンストラクタで値を渡す為に、
        // Factoryクラスを作ってそれで生成している
        // Factoryクラスの書き方：
        //  https://starzero.hatenablog.com/entry/2017/05/19/005437
        val mainViewModel = ViewModelProviders.of(this,Factory(
            this
        )).get(MainViewModel::class.java)

        binding.mainViewModel = mainViewModel
        binding.adapter = MainItemAdapter()

        // 画面回転への対応
        binding.mainViewModel?.spanCount?.value = calSpanCount()

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
        private val mainActibity: MainActivity
        ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val mainViewModel = MainViewModel()
            val application = mainActibity.application as MyApp
            mainViewModel.photoRepository = application.photoRepository
            mainViewModel.clickListener = mainActibity
            @Suppress("UNCHECKED_CAST")
            return mainViewModel as T
        }
    }

    private fun calSpanCount():Int {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 2
        }
    }
}


@BindingAdapter("app:viewModels")
fun RecyclerView.setViewModels(newMainItem: List<MainItemViewModel>) {
    val adapter = this.adapter as MainItemAdapter
    adapter.update(newMainItem.toList())
}

@BindingAdapter("app:updateSpanCount")
fun RecyclerView.setUpdateSpanCount(count:Int) {
    val manager = this.layoutManager as GridLayoutManager
    manager.spanCount = count
}
