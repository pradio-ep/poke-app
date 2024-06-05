package id.pradio.pokeapp.ui.mypokemon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import id.pradio.pokeapp.core.BaseViewHolder
import id.pradio.pokeapp.databinding.*
import id.pradio.pokeapp.ui.detail.ConnectionError
import id.pradio.pokeapp.ui.detail.DetailUiModel
import id.pradio.pokeapp.ui.detail.LoadingItemModel
import id.pradio.pokeapp.ui.detail.adapter.SimpleConnectionErrorViewHolder
import id.pradio.pokeapp.ui.detail.adapter.SimpleEmptyViewHolder
import id.pradio.pokeapp.ui.detail.adapter.SimpleLoadingViewHolder


sealed class ClickAction {
    object Rename: ClickAction()
    object Release: ClickAction()
}

class ListMyPokemonAdapter(
    private val onClick: (MyPokemonItemModel, clickAction: ClickAction) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_POKEMON = 1
        const val VIEW_TYPE_LOADING = 2
        const val VIEW_TYPE_CONN_ERR = 3
        const val VIEW_TYPE_EMPTY = 4
    }

    private val items = mutableListOf<SaveUiModel>()

    fun updateList(data: List<SaveUiModel>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun loading() {
        items.clear()
        items.add(LoadingItemModel)
        notifyDataSetChanged()
    }

    fun empty() {
        items.clear()
        items.add(Empty)
        notifyDataSetChanged()
    }

    fun connectionError() {
        items.clear()
        items.add(ConnectionError)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_POKEMON -> PokemonViewHolder(
                ListItemMyPokemonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClick
            )
            VIEW_TYPE_LOADING -> SimpleLoadingViewHolder(
                ItemLoadingBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_CONN_ERR -> SimpleConnectionErrorViewHolder(
                ItemConnectionErrorBinding.inflate(inflater, parent, false),
            )
            VIEW_TYPE_EMPTY -> SimpleEmptyViewHolder(
                ItemEmptyBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unsupported view type $viewType ")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_POKEMON -> {
                (holder as PokemonViewHolder).bind(items[position] as MyPokemonItemModel)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MyPokemonItemModel -> VIEW_TYPE_POKEMON
            LoadingItemModel -> VIEW_TYPE_LOADING
            ConnectionError -> VIEW_TYPE_CONN_ERR
            Empty -> VIEW_TYPE_EMPTY
            else -> throw IllegalStateException("Unsupported item ${items[position]::class}, required ${DetailUiModel::class} ")
        }
    }

    class PokemonViewHolder(
        private val binding: ListItemMyPokemonBinding,
        private val click: (MyPokemonItemModel, ClickAction) -> Unit
    ) : BaseViewHolder<MyPokemonItemModel>(binding.root) {

        override fun bind(model: MyPokemonItemModel) {
            Glide.with(binding.ivAvatar)
                .load(model.imageUrl)
                .thumbnail(0.5f)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(binding.ivAvatar)

            binding.tvName.text = model.displayName
            binding.btnRename.setOnClickListener {
                click(model, ClickAction.Rename)
            }
            binding.btnRelease.setOnClickListener {
                click(model, ClickAction.Release)
            }
        }
    }
}