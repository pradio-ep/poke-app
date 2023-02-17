package id.pradio.pokeapp.ui.home

sealed class Event {
    object GetPokemonList : Event()
    object SortByName : Event()
    data class SearchPokemon(val name: String) : Event()
}

sealed class State {
    object Loading : State()
    data class Loaded(val items: List<PokemonItemModel>) : State()
    data class Failed(val message: String?) : State()
    object ConnectionError : State()
}