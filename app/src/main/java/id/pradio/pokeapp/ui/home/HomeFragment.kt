package id.pradio.pokeapp.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.pradio.pokeapp.R
import id.pradio.pokeapp.core.BaseFragment
import id.pradio.pokeapp.core.ext.observe
import id.pradio.pokeapp.databinding.FragmentHomeBinding


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()

    private val adapter by lazy {
        ListPokemonAdapter { model, extra ->
            onItemClick(model, extra)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isSortByName = false

        binding.icSort.setOnClickListener {
            if (!isSortByName) {
                isSortByName = true
                viewModel.onEvent(Event.SortByName)
                binding.icSort.setImageResource(R.drawable.ic_sort_by_name)
            } else {
                isSortByName = false
                viewModel.onEvent(Event.GetPokemonList)
                binding.icSort.setImageResource(R.drawable.ic_sort_by_id)
            }
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.imgClear.visibility = if (s.isNotEmpty()) View.VISIBLE else View.GONE
                viewModel.onEvent(Event.SearchPokemon(s.toString()))
            }
            override fun afterTextChanged(s: Editable) {}
        })
        binding.imgClear.setOnClickListener {
            binding.etSearch.setText("")
            binding.icSort.setImageResource(R.drawable.ic_sort_by_id)
        }
        binding.rvPokemon.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvPokemon.adapter = adapter

        viewModel.onEvent(Event.GetPokemonList)

        observe(viewModel.state) {
            when (it) {
                State.Loading -> {
                    adapter.loading()
                    binding.icSort.isEnabled = false
                }
                is State.Loaded -> {
                    adapter.updateList(it.items)
                    binding.icSort.isEnabled = true
                }
                is State.Failed -> {
                    if (adapter.itemCount <= 1) {
                        adapter.clear()
                    }
                    showSnackBar(it.message ?: "Unknown error")
                }
                State.ConnectionError -> {
                    adapter.connectionError()
                    showSnackBar(getString(R.string.label_connection_trouble))
                }
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            requireContext(),
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun onItemClick(model: PokemonItemModel, extras: Navigator.Extras?) {
        val directions = HomeFragmentDirections.actionHomeFragmentToDetailFragment(model)
        if (extras != null) {
            navController.navigate(directions, extras)
        } else {
            navController.navigate(directions)
        }
    }
}