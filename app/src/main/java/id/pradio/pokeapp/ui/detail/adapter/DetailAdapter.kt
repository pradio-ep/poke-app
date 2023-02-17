package id.pradio.pokeapp.ui.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.pradio.pokeapp.databinding.*
import id.pradio.pokeapp.ui.detail.*

class DetailAdapter(
    private var color: Int,
    private val onClick: (Int) -> Unit = { _ -> }
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_STAT = 1
        const val VIEW_TYPE_EVOLUTION = 2
        const val VIEW_TYPE_LOADING = 3
        const val VIEW_TYPE_CONN_ERR = 4
    }

    private val items = mutableListOf<DetailUiModel>()

    fun updateElementColor(newColor: Int) {
        color = newColor
    }

    fun update(list: List<DetailUiModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun clear(){
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
            VIEW_TYPE_STAT -> StatViewHolder(
                ListItemStatBinding.inflate(inflater, parent, false),
                color
            )
            VIEW_TYPE_EVOLUTION -> EvolutionViewHolder(
                ListItemEvolutionBinding.inflate(inflater, parent, false),
                color,
                onClick
            )
            VIEW_TYPE_LOADING -> LoadingViewHolder(
                ListItemLoadingBinding.inflate(inflater, parent, false),
                color
            )
            VIEW_TYPE_CONN_ERR -> ConnectionErrorViewHolder(
                ListItemConnectionErrorBinding.inflate(inflater, parent, false),
            )
            else -> throw IllegalStateException("Unsupported view type $viewType ")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_STAT -> {
                (holder as StatViewHolder).bind(items[position] as StatItemModel)
            }
            VIEW_TYPE_EVOLUTION -> {
                (holder as EvolutionViewHolder).bind(items[position] as EvolutionItemModel)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is StatItemModel -> VIEW_TYPE_STAT
            is EvolutionItemModel -> VIEW_TYPE_EVOLUTION
            LoadingItemModel -> VIEW_TYPE_LOADING
            ConnectionError -> VIEW_TYPE_CONN_ERR
            else -> throw IllegalStateException("Unsupported item ${items[position]::class}, required ${DetailUiModel::class} ")
        }
    }
}