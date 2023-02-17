package id.pradio.pokeapp.ui.detail

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.pradio.pokeapp.R
import id.pradio.pokeapp.core.BaseFragment
import id.pradio.pokeapp.core.ext.observe
import id.pradio.pokeapp.data.resultmodel.NameAndOverview
import id.pradio.pokeapp.databinding.FragmentDetailBinding
import id.pradio.pokeapp.ui.detail.adapter.DetailAdapter
import id.pradio.pokeapp.ui.home.PokemonItemModel


@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {

    private val args by navArgs<DetailFragmentArgs>()

    private lateinit var pokemon: PokemonItemModel

    private val viewModel: DetailViewModel by viewModels()

    private lateinit var detailAdapter: DetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("DEPRECATION")
        view.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        pokemon = args.pokemonItemModel

        updateView()
        bindViewModel()

        viewModel.onEvent(Event.GetPokemonDetail(pokemon.id))

        binding.scrollView.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return gestureDetector.onTouchEvent(event)
            }
            override fun onSwipeRight() {
                nextPokemon()
            }
            override fun onSwipeLeft() {
                prevPokemon()
            }
            override fun onSwipeTop() {}
            override fun onSwipeBottom() {}
        })
    }

    private fun nextPokemon() {
        if (pokemon.id < 45) viewModel.onEvent(Event.GetNextPokemon(pokemon.id.plus(1)))
    }

    private fun prevPokemon() {
        if (pokemon.id > 1) viewModel.onEvent(Event.GetPrevPokemon(pokemon.id.minus(1)))
    }

    private fun updateView() {
        configureHeaderView()
        configureViewElements()
        configureButtonTabs()
        configureRecyclerView()
    }

    private fun configureHeaderView() {
        val elementColor = ContextCompat.getColor(
            requireContext(), pokemon.elements[0].colorResId
        )
        Glide.with(binding.ivAvatar)
            .load(pokemon.imageUrl)
            .thumbnail(0.5f)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(binding.ivAvatar)
        binding.btnPrev.visibility = if (pokemon.id == 1) GONE else VISIBLE
        binding.btnNext.visibility = if (pokemon.id == 45) GONE else VISIBLE
        binding.btnPrev.setOnClickListener { prevPokemon() }
        binding.btnNext.setOnClickListener { nextPokemon() }
        binding.tvName.text = pokemon.name
        binding.tvId.text = String.format("#%03d", pokemon.id)
        binding.root.setBackgroundColor(elementColor)
        binding.btnClose.setOnClickListener {
            navController.popBackStack()
        }
        binding.tvAbout.setTextColor(elementColor)
    }

    private fun configureRecyclerView() {
        val elementColor = ContextCompat.getColor(
            requireContext(), pokemon.elements[0].colorResId
        )
        if (!::detailAdapter.isInitialized) {
            detailAdapter = DetailAdapter(elementColor) { id ->
                onEvolutionClick(id)
            }
        } else {
            detailAdapter.updateElementColor(elementColor)
        }
        binding.rvDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDetails.adapter = detailAdapter
    }

    private fun configureButtonTabs() {
        val elementColor = ContextCompat.getColor(
            requireContext(), pokemon.elements[0].colorResId
        )
        val buttonBackgroundStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
            ),
            intArrayOf(
                Color.WHITE,
                elementColor
            )
        )
        val buttonTextStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
            ),
            intArrayOf(
                elementColor,
                Color.WHITE
            )
        )
        binding.btnStat.backgroundTintList = buttonBackgroundStateList
        binding.btnStat.setTextColor(buttonTextStateList)
        binding.btnStat.setOnClickListener {
            binding.btnStat.isEnabled = false
            binding.btnEvolutions.isEnabled = true
            viewModel.onEvent(Event.StatClick)
        }

        binding.btnEvolutions.backgroundTintList = buttonBackgroundStateList
        binding.btnEvolutions.setTextColor(buttonTextStateList)
        binding.btnEvolutions.setOnClickListener {
            binding.btnStat.isEnabled = true
            binding.btnEvolutions.isEnabled = false
            viewModel.onEvent(Event.EvolutionClick)
        }
    }

    private fun configureViewElements() {
        if (pokemon.elements.isEmpty()) return
        binding.llElements.removeAllViews()
        pokemon.elements.forEach { type ->
            binding.llElements.post {
                val child = LayoutInflater.from(requireContext()).inflate(
                    R.layout.item_detail_element,
                    binding.llElements,
                    false
                ) as MaterialButton
                val id = generateViewId()
                child.id = id
                val color = ContextCompat.getColor(
                    requireContext(),
                    type.colorResId
                )
                child.text = type.name
                child.backgroundTintList = ColorStateList.valueOf(color)
                binding.llElements.addView(child)
            }
        }
    }

    private fun bindViewModel() = observe(viewModel.state) {
        when (it) {
            is State.DetailState.Loading -> {}
            is State.DetailState.Failed -> { showSnackBar(it.message ?: "Unknown Error") }
            is State.DetailState.Loaded -> {
                pokemon = PokemonItemModel(it.id, it.name, it.number, it.imageUrl, it.elements)
                updateView()
                binding.btnStat.performClick()
            }
            is State.DetailStatState.Loading -> updateStatUiLoading()
            is State.DetailStatState.Failed -> updateStatUiError(it)
            is State.DetailStatState.Loaded -> updateStatUiDetail(it.overview, it.weight, it.height, it.abilities, it.list)
            is State.EvolutionState.Loading -> updateEvolutionUiLoading()
            is State.EvolutionState.Failed -> updateEvolutionUiError(it)
            is State.EvolutionState.Loaded -> updateEvolutionUiDetail(it.list)
            is State.ConnectionFailed -> {
                detailAdapter.connectionError()
                showSnackBar(getString(R.string.label_connection_trouble))
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            requireContext(),
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            setAction("Retry") {
                val event = if (binding.btnEvolutions.isEnabled) {
                    Event.RetryEvolution
                } else {
                    Event.RetryStat
                }
                viewModel.onEvent(event)
                dismiss()
            }
            show()
        }
    }

    private fun updateStatUiDetail(overview: String?, weight: Int, height: Int, abilities: List<NameAndOverview>, items: List<DetailUiModel>) {
        if (binding.btnStat.isEnabled) return

        binding.tvWeight.text = "${weight.toFloat()/10} kg"
        binding.tvHeight.text = "${height.toFloat()/10} m"
        binding.tvMove.text = abilities.map { it.name }.joinToString("\n")
        binding.tvOverview.text = overview
        detailAdapter.update(items)
    }

    private fun updateStatUiLoading() {
        if (binding.btnStat.isEnabled) return
        detailAdapter.loading()
    }

    private fun updateStatUiError(state: State.DetailStatState.Failed) {
        if (binding.btnStat.isEnabled) return

        if (detailAdapter.itemCount <= 1) {
            detailAdapter.clear()
        }
        showSnackBar(state.message ?: "Unknown Error")
    }

    private fun updateEvolutionUiDetail(items: List<DetailUiModel>) {
        if (binding.btnEvolutions.isEnabled) return
        detailAdapter.update(items)
    }

    private fun updateEvolutionUiError(state: State.EvolutionState.Failed) {
        if (binding.btnEvolutions.isEnabled) return
        if (detailAdapter.itemCount <= 1) {
            detailAdapter.clear()
        }
        showSnackBar(state.message ?: "Unknown Error")
    }

    private fun updateEvolutionUiLoading() {
        if (binding.btnEvolutions.isEnabled) return
        detailAdapter.loading()
    }

    private fun onEvolutionClick(id: Int) {
        viewModel.onEvent(Event.GetEvolution(id))
    }
}