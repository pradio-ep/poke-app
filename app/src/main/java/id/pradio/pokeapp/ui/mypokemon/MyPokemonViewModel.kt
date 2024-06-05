package id.pradio.pokeapp.ui.mypokemon

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.pradio.pokeapp.core.BaseViewModel
import id.pradio.pokeapp.core.ext.handleError
import id.pradio.pokeapp.data.ExtraRepository
import id.pradio.pokeapp.data.Repository
import id.pradio.pokeapp.data.entity.MyPokemonEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyPokemonViewModel @Inject constructor(
    private val repository: Repository,
    private val extraRepository: ExtraRepository
) : BaseViewModel<Event, State>() {

    override fun onEvent(event: Event) {
        when (event) {
            is Event.GetMyPokemonList -> requestMyPokemon()
            is Event.RenamePokemon -> renameMyPokemon(event.model)
            is Event.ReleasePokemon -> releaseMyPokemon(event.id)
        }
    }

    private fun requestMyPokemon() {
        pushState(State.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            repository.listMyPokemon()
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
                            pushState(State.Empty)
                        }
                    } else {
                        val item = result.map {
                            val imageUrl =
                                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${it.id}.png"
                            MyPokemonItemModel(
                                it.id,
                                it.name,
                                it.displayName,
                                it.renameAttempt,
                                imageUrl,
                            )
                        }
                        withContext(Dispatchers.Main) {
                            pushState(State.Loaded(item))
                        }
                    }
                }
        }
    }

    private fun renameMyPokemon(model: MyPokemonItemModel) {
        pushState(State.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            extraRepository.renamePokemon(model.name, model.renameAttempt)
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
                .collectLatest {
                    if (it.message == "Success") {
                        val myPokemonEntity = MyPokemonEntity(
                            model.id,
                            model.name,
                            it.result,
                            model.renameAttempt + 1
                        )
                        repository.renameMyPokemon(myPokemonEntity)
                        withContext(Dispatchers.Main) {
                            pushState(State.RenameSuccess)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            pushState(State.RenameFailed)
                        }
                    }
                }
        }
    }

    private fun releaseMyPokemon(id: Int) {
        pushState(State.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            extraRepository.releasePokemon()
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
                .collectLatest {
                    if (it.message == "Success") {
                        repository.releaseMyPokemon(id)
                        withContext(Dispatchers.Main) {
                            pushState(State.ReleaseSuccess)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            pushState(State.ReleaseFailed)
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