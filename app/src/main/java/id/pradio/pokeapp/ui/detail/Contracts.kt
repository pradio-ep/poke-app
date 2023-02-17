package id.pradio.pokeapp.ui.detail

import id.pradio.pokeapp.data.resultmodel.NameAndOverview
import id.pradio.pokeapp.ui.home.ElementItemModel

sealed class Event {
    data class GetPokemonDetail(val id: Int) : Event()
    data class GetNextPokemon(val id: Int) : Event()
    data class GetPrevPokemon(val id: Int) : Event()
    data class GetEvolution(val id: Int) : Event()
    object StatClick : Event()
    object EvolutionClick : Event()
    object RetryStat : Event()
    object RetryEvolution : Event()
}

sealed class State {
    sealed class DetailState : State() {
        object Loading : DetailState()
        data class Loaded(
            val id: Int,
            val name: String?,
            val number: String,
            val imageUrl: String?,
            val elements: List<ElementItemModel>
        ) : DetailState()

        data class Failed(val message: String?) : DetailState()
    }

    sealed class DetailStatState : State() {
        object Loading : DetailStatState()
        data class Loaded(
            val overview: String?,
            val height: Int,
            val weight: Int,
            val abilities: List<NameAndOverview>,
            val list: List<DetailUiModel>
        ) : DetailStatState()

        data class Failed(val message: String?) : DetailStatState()
    }

    sealed class EvolutionState : State() {
        object Loading : EvolutionState()
        data class Loaded(
            val list: List<EvolutionItemModel>
        ) : EvolutionState()

        data class Failed(val message: String?) : EvolutionState()
    }

    object ConnectionFailed : State()
}