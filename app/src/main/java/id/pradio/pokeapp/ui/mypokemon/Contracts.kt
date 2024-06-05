package id.pradio.pokeapp.ui.mypokemon

sealed class Event {
    object GetMyPokemonList : Event()
    data class RenamePokemon(val model: MyPokemonItemModel) : Event()
    data class ReleasePokemon(val id: Int) : Event()
}

sealed class State {
    object Loading : State()
    object Empty : State()
    data class Loaded(val items: List<MyPokemonItemModel>) : State()
    data class Failed(val message: String?) : State()
    object RenameSuccess : State()
    object RenameFailed : State()
    object ReleaseSuccess : State()
    object ReleaseFailed : State()
    object ConnectionError : State()
}