package jp.paming.positionedphoto.ui

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import jp.paming.positionedphoto.databinding.PhotoCardBinding

class MainItemAdapter : RecyclerView.Adapter<PhotoCardDataViewHolder>() {

    private var mainItem:List<MainItemViewModel> = listOf()

    override fun getItemCount(): Int {
        return mainItem.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoCardDataViewHolder {
        val binding = PhotoCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoCardDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoCardDataViewHolder, position: Int) {
        // ここではモデルに値をセットしている
        // →DataBindingにより、自動でViewに反映される
        holder.binding.mainItemViewModel = mainItem[position]
    }

    fun update(newMainItem: List<MainItemViewModel>){
        // DiffUtilでいい感じに差分更新してくれる。
        // https://qiita.com/Tsutou/items/69a28ebbd69b69e51703
        val diff = DiffUtil.calculateDiff(Callback(mainItem, newMainItem), true)
        mainItem = newMainItem
        diff.dispatchUpdatesTo(this)
    }

    private class Callback(private val old: List<MainItemViewModel>,
                           private val aNew: List<MainItemViewModel>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size
        override fun getNewListSize(): Int = aNew.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            old[oldItemPosition].getUri() == aNew[newItemPosition].getUri()

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            old[oldItemPosition].getUri() == aNew[newItemPosition].getUri()
    }
}