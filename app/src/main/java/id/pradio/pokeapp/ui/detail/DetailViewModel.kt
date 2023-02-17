package id.pradio.pokeapp.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.pradio.pokeapp.core.BaseViewModel
import id.pradio.pokeapp.core.ext.handleError
import id.pradio.pokeapp.data.Repository
import id.pradio.pokeapp.ui.home.ElementItemModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: Repository) :
    BaseViewModel<Event, State>() {

    private val evolutionId = MutableLiveData<Int>()
    private val pokemonId = MutableLiveData<Int>()

    override fun onEvent(event: Event) {
        when (event) {
            is Event.GetPokemonDetail -> {
                pokemonId.value = event.id
                requestDetailStat()
            }
            is Event.GetNextPokemon -> {
                pokemonId.value = event.id
                getPokemon(event.id)
                requestDetailStat()
            }
            is Event.GetPrevPokemon -> {
                pokemonId.value = event.id
                getPokemon(event.id)
                requestDetailStat()
            }
            is Event.GetEvolution -> {
                pokemonId.value = event.id
                getPokemon(event.id)
                requestDetailStat()
            }
            Event.StatClick -> requestDetailStat()
            Event.EvolutionClick -> requestEvolution()
            Event.RetryStat -> requestDetailStat()
            Event.RetryEvolution -> requestEvolution()
        }
    }

    private fun requestEvolution() {
        pushState(State.EvolutionState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            if (evolutionId.value == null) return@launch
            repository.evolution(pokemonId.value!!, evolutionId.value!!)
                .catch { e ->
                    e.handleError({
                        withContext(Dispatchers.Main) {
                            pushState(State.ConnectionFailed)
                        }
                    }) {
                        withContext(Dispatchers.Main) {
                            pushState(State.EvolutionState.Failed(it.message))
                        }
                    }
                }
                .collectLatest { result ->
                    val items = result.map { evolve ->
                        EvolutionItemModel(
                            pokemonId.value!!,
                            evolve.fromId,
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${evolve.fromId}.png",
                            evolve.from,
                            evolve.toId,
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${evolve.toId}.png",
                            evolve.to,
                            evolve.trigger
                        )
                    }
                    withContext(Dispatchers.Main) {
                        pushState(State.EvolutionState.Loaded(items))
                    }
                }

        }
    }

    private fun requestDetailStat() {
        pushState(State.DetailStatState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            if (pokemonId.value == null) {
                return@launch
            }
            val id = pokemonId.value!!
            repository.detail(id)
                .catch { e ->
                    e.handleError({
                        withContext(Dispatchers.Main) {
                            pushState(State.ConnectionFailed)
                        }
                    }) {
                        withContext(Dispatchers.Main) {
                            pushState(State.DetailStatState.Failed(it.message))
                        }
                    }
                }
                .collectLatest { result ->
                    val list = listOf(
                        StatItemModel(
                            result.stat.hp,
                            result.stat.atk,
                            result.stat.def,
                            result.stat.satk,
                            result.stat.sdef,
                            result.stat.spd
                        )
                    )
                    withContext(Dispatchers.Main) {
                        evolutionId.value = result.evolutionId
                        pushState(
                            State.DetailStatState.Loaded(
                                result.overview,
                                result.height,
                                result.weight,
                                result.abilities,
                                list
                            )
                        )
                    }
                }
        }
    }

    private fun getPokemon(id: Int) {
        pushState(State.DetailState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPokemon(id)
                .catch { e ->
                    e.handleError({
                        withContext(Dispatchers.Main) {
                            pushState(State.ConnectionFailed)
                        }
                    }) {
                        withContext(Dispatchers.Main) {
                            pushState(State.DetailState.Failed(it.message))
                        }
                    }
                }
                .collectLatest { result ->
                    val number = String.format("#%03d", result.id)
                    val imageUrl =
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${result.id}.png"
                    withContext(Dispatchers.Main) {
                        pushState(
                            State.DetailState.Loaded(
                                result.id,
                                result.displayName,
                                number,
                                imageUrl,
                                result.elements.map { element ->
                                    ElementItemModel(
                                        element.name,
                                        element.iconResId,
                                        element.colorResId
                                    )
                                }
                            )
                        )
                    }
                }
        }
    }

    override fun onCleared() {
        viewModelScope.coroutineContext.cancel()
        super.onCleared()
    }
}