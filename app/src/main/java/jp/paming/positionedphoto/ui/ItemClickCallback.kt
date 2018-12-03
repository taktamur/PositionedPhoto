package jp.paming.positionedphoto.ui

import jp.paming.positionedphoto.service.PhotoData

interface ItemClickCallback {
    fun onItemClick(photoData: PhotoData)
}