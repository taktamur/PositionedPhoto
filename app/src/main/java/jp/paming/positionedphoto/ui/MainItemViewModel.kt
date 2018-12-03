package jp.paming.positionedphoto.ui

import android.net.Uri
import jp.paming.positionedphoto.service.PhotoData


interface OnItemClickListner {
    fun onItemClick(photoData:PhotoData)
}

class MainItemViewModel(private val photoData: PhotoData,
                        private val listener:OnItemClickListner?){

    fun getDate():String = photoData.getDateString()

    fun getUri(): Uri = photoData.uri

    fun getVisibleLocationIcon():Boolean = (photoData.loc != null)

    fun onClick() {
        listener?.onItemClick(photoData)
    }
}