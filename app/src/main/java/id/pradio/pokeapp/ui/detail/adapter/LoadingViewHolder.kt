package id.pradio.pokeapp.ui.detail.adapter

import androidx.annotation.ColorInt
import id.pradio.pokeapp.core.BaseViewHolder
import id.pradio.pokeapp.databinding.ListItemLoadingBinding
import id.pradio.pokeapp.ui.detail.LoadingItemModel

class LoadingViewHolder(
    binding: ListItemLoadingBinding,
    @ColorInt color: Int
) :
    BaseViewHolder<LoadingItemModel>(binding.root) {
    init {
        binding.progressBar.setIndicatorColor(color)
    }

    override fun bind(model: LoadingItemModel) {

    }
}