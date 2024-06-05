package id.pradio.pokeapp.data

import id.pradio.pokeapp.data.resultmodel.*
import kotlinx.coroutines.flow.Flow

interface ExtraRepository {
    suspend fun catchPokemon(): Flow<CatchPokemonResult>

    suspend fun renamePokemon(name: String, attempt: Int): Flow<RenamePokemonResult>

    suspend fun releasePokemon(): Flow<ReleasePokemonResult>
}