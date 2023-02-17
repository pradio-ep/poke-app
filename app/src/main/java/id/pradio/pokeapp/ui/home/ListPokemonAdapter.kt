package id.pradio.pokeapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
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
import id.pradio.pokeapp.ui.detail.adapter.SimpleLoadingViewHolder


class ListPokemonAdapter(private val onClick: (PokemonItemModel, Navigator.Extras?) -> Unit = { _, _ -> }) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_POKEMON = 1
        const val VIEW_TYPE_LOADING = 2
        const val VIEW_TYPE_CONN_ERR = 3
    }

    private val items = mutableListOf<HomeUiModel>()

    fun updateList(data: List<HomeUiModel>) {
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

    fun connectionError() {
        items.clear()
        items.add(ConnectionError)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_POKEMON -> PokemonViewHolder(
                ListItemPokemonBinding.inflate(
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
            else -> throw IllegalStateException("Unsupported view type $viewType ")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_POKEMON -> {
                (holder as PokemonViewHolder).bind(items[position] as PokemonItemModel)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is PokemonItemModel -> VIEW_TYPE_POKEMON
            LoadingItemModel -> VIEW_TYPE_LOADING
            ConnectionError -> VIEW_TYPE_CONN_ERR
            else -> throw IllegalStateException("Unsupported item ${items[position]::class}, required ${DetailUiModel::class} ")
        }
    }

    class PokemonViewHolder(
        private val binding: ListItemPokemonBinding,
        private val click: (PokemonItemModel, Navigator.Extras?) -> Unit
    ) : BaseViewHolder<PokemonItemModel>(binding.root) {

        override fun bind(model: PokemonItemModel) {

            itemView.setOnClickListener {
                val extra = FragmentNavigatorExtras(
                    binding.ivAvatar to "avatar_transform",
                    binding.tvName to "title_transform"
                )
                click(model, extra)
            }

            val elementColor = ContextCompat.getColor(itemView.context, model.elements.first().colorResId)

            binding.root.strokeColor = elementColor
            binding.tvName.text = model.name
            binding.tvName.setBackgroundColor(elementColor)
            binding.tvNumber.text = model.number
            binding.tvNumber.setTextColor(elementColor)

            Glide.with(binding.ivAvatar)
                .load(model.imageUrl)
                .thumbnail(0.5f)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(binding.ivAvatar)
        }
    }
}