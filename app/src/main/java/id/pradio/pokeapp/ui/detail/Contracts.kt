package id.pradio.pokeapp.ui.detail

import id.pradio.pokeapp.data.resultmodel.NameAndOverview
import id.pradio.pokeapp.ui.home.ElementItemModel
import id.pradio.pokeapp.ui.home.PokemonItemModel

sealed class Event {
    data class GetPokemonDetail(val id: Int) : Event()
    data class GetMyPokemon(val id: Int) : Event()
    data class GetNextPokemon(val id: Int) : Event()
    data class GetPrevPokemon(val id: Int) : Event()
    data class GetEvolution(val id: Int) : Event()
    data class CatchPokemon(val model: PokemonItemModel) : Event()
    data class SavePokemon(val model: PokemonItemModel, val nickname: String) : Event()
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
            val name: String,
            val number: String,
            val imageUrl: String?,
            val elements: List<ElementItemModel>
        ) : DetailState()

        data class Failed(val message: String?) : DetailState()
        data class MyPokemon(val isMyPokemon: Boolean): DetailState()
        object CatchSuccess: DetailState()
        object CatchFailed: DetailState()
        object SavePokemonSuccess: DetailState()
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