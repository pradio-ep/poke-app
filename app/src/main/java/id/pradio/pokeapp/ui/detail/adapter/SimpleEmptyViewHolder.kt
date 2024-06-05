package id.pradio.pokeapp.ui.detail.adapter

import id.pradio.pokeapp.core.BaseViewHolder
import id.pradio.pokeapp.databinding.ItemEmptyBinding
import id.pradio.pokeapp.ui.mypokemon.Empty

class SimpleEmptyViewHolder(
    binding: ItemEmptyBinding
) : BaseViewHolder<Empty>(binding.root) {
    override fun bind(model: Empty) {

    }
}