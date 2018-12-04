package jp.paming.positionedphoto.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import jp.paming.positionedphoto.service.PhotoRepository

class MainViewModel: ViewModel() {
    var photoRepository: PhotoRepository? = null
    var clickListener:ItemClickCallback? = null

    // ここはObservable<T>やLiveDataでないと、DataBindingを経由して変更通知が届かない
    val items: MutableLiveData<List<MainItemViewModel>> = MutableLiveData()
    val spanCount: MutableLiveData<Int> = MutableLiveData()

    var onlyPositioned: Boolean = true

    init{
        // TODO LiveDataのvalueを使っているけど、putValueとかsetValueとかとの違いは何か？
        spanCount.value = 1 // 初期値
    }
    // TODO 双方向バインディング化
    fun onCheckedChanged(checked: Boolean) {
        onlyPositioned = checked
        this.updateItems()
    }

    fun updateItems() {
        val list = photoRepository?.find(onlyPositioned) ?: emptyList()
        items.value = list.map {
            MainItemViewModel(it, clickListener)
        }
    }
}