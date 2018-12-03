package jp.paming.positionedphoto.ui

import android.support.v7.widget.RecyclerView
import jp.paming.positionedphoto.databinding.PhotoCardBinding

// RecyclerView.ViewHolderを継承した自作ViewHolder
// 親クラスの初期化にはBinding.rootで親Viewを渡し、
// 子クラスはbindingを保持する
class PhotoCardDataViewHolder(val binding: PhotoCardBinding) : RecyclerView.ViewHolder(binding.root)