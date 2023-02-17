package id.pradio.pokeapp.ui.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.pradio.pokeapp.core.BaseViewModel
import id.pradio.pokeapp.core.ext.handleError
import id.pradio.pokeapp.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : BaseViewModel<Event, State>() {

    override fun onEvent(event: Event) {
        when (event) {
            is Event.GetPokemonList -> requestPokemon(false)
            is Event.SortByName -> requestPokemon(true)
            is Event.SearchPokemon -> searchPokemon(event.name)
        }
    }

    private fun requestPokemon(sortByName: Boolean) {
        pushState(State.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            repository.listPokemon(0, sortByName)
                .catch { e ->
                    e.handleError({
                        withContext(Dispatchers.Main) {
                            pushState(State.ConnectionError)
                        }
                    }) {
                        withContext(Dispatchers.Main) {
                            pushState(State.Failed(it.message))
                        }
                    }
                }
                .collectLatest { result ->
                    val item = result.map {
                        val number = String.format("#%03d", it.id)
                        val imageUrl =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${it.id}.png"
                        PokemonItemModel(
                            it.id,
                            it.displayName,
                            number,
                            imageUrl,
                            it.elements.map { element ->
                                ElementItemModel(
                                    element.name,
                                    element.iconResId,
                                    element.colorResId
                                )
                            }
                        )
                    }
                    withContext(Dispatchers.Main) {
                        pushState(State.Loaded(item))
                    }
                }
        }
    }

    private fun searchPokemon(name: String) {
        pushState(State.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchPokemon(name)
                .catch { e ->
                    e.handleError({
                        withContext(Dispatchers.Main) {
                            pushState(State.ConnectionError)
                        }
                    }) {
                        withContext(Dispatchers.Main) {
                            pushState(State.Failed(it.message))
                        }
                    }
                }
                .collectLatest { result ->
                    if (result.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            pushState(State.Failed("Not Found!"))
                        }
                    } else {
                        val item = result.map {
                            val number = String.format("#%03d", it.id)
                            val imageUrl =
                                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${it.id}.png"
                            PokemonItemModel(
                                it.id,
                                it.displayName,
                                number,
                                imageUrl,
                                it.elements.map { element ->
                                    ElementItemModel(
                                        element.name,
                                        element.iconResId,
                                        element.colorResId
                                    )
                                }
                            )
                        }
                        withContext(Dispatchers.Main) {
                            pushState(State.Loaded(item))
                        }
                    }
                }
        }
    }

    override fun onCleared() {
        viewModelScope.coroutineContext.cancel()
        super.onCleared()
    }
}