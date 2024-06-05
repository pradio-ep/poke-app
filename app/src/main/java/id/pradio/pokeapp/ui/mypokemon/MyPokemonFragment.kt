package id.pradio.pokeapp.ui.mypokemon

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.pradio.pokeapp.R
import id.pradio.pokeapp.core.BaseFragment
import id.pradio.pokeapp.core.ext.observe
import id.pradio.pokeapp.databinding.FragmentHomeBinding
import id.pradio.pokeapp.databinding.FragmentMyPokemonBinding


@AndroidEntryPoint
class MyPokemonFragment : BaseFragment<FragmentMyPokemonBinding>(FragmentMyPokemonBinding::inflate) {

    private val viewModel: MyPokemonViewModel by viewModels()

    private val adapter by lazy {
        ListMyPokemonAdapter { model, clickAction ->
            onItemClick(model, clickAction)
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

        binding.btnClose.setOnClickListener {
            navController.popBackStack()
        }

        binding.rvPokemon.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPokemon.adapter = adapter

        viewModel.onEvent(Event.GetMyPokemonList)

        observe(viewModel.state) {
            when (it) {
                State.Loading -> {
                    adapter.loading()
                }
                State.Empty -> {
                    adapter.empty()
                }
                is State.Loaded -> {
                    adapter.updateList(it.items)
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
                State.RenameSuccess -> {
                    viewModel.onEvent(Event.GetMyPokemonList)
                    showSnackBar("Rename Success")
                }
                State.RenameFailed -> {
                    viewModel.onEvent(Event.GetMyPokemonList)
                    showSnackBar("Rename Failed")
                }
                State.ReleaseSuccess -> {
                    viewModel.onEvent(Event.GetMyPokemonList)
                    showSnackBar("Release Success")
                }
                State.ReleaseFailed -> {
                    viewModel.onEvent(Event.GetMyPokemonList)
                    showSnackBar("Release Failed")
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

    private fun onItemClick(model: MyPokemonItemModel, clickAction: ClickAction) {
        when (clickAction) {
            is ClickAction.Rename -> {
                viewModel.onEvent(Event.RenamePokemon(model))
            }
            is ClickAction.Release -> {
                viewModel.onEvent(Event.ReleasePokemon(model.id))
            }
        }
    }
}