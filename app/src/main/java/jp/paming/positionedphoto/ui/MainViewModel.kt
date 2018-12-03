package jp.paming.positionedphoto.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import jp.paming.positionedphoto.service.Orientation
import jp.paming.positionedphoto.service.OrientationService
import jp.paming.positionedphoto.service.PhotoData
import jp.paming.positionedphoto.service.PhotoRepository

class MainViewModel: ViewModel() {
    var photoRepository: PhotoRepository? = null
    var orientationService: OrientationService? = null
    var onClickListener:((PhotoData)->Unit)? = null
    var updateGridSpanListener:((Int)->Unit)? = null

    // ここはObservable<T>やLiveDataでないと、DataBindingを経由して変更通知が届かない
    val items: MutableLiveData<List<MainItemViewModel>> =
        MutableLiveData()
    var onlyPositioned: Boolean = true

    // TODO 双方向バインディング化
    fun onCheckedChanged(checked: Boolean) {
        onlyPositioned = checked
        this.updateItems()
    }

    fun updateItems() {
        val list = photoRepository?.find(onlyPositioned) ?: emptyList()
        items.value = list.map {
            MainItemViewModel(it, onClickListener)
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