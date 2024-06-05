package id.pradio.pokeapp.data

import id.pradio.pokeapp.data.entity.MyPokemonEntity
import id.pradio.pokeapp.data.resultmodel.DetailPokemonResult
import id.pradio.pokeapp.data.resultmodel.EvolutionResult
import id.pradio.pokeapp.data.resultmodel.MyPokemonResult
import id.pradio.pokeapp.data.resultmodel.PokemonResult
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun listPokemon(offset: Int, sortByName: Boolean): Flow<List<PokemonResult>>

    suspend fun searchPokemon(name: String): Flow<List<PokemonResult>>

    suspend fun getPokemon(id: Int): Flow<PokemonResult>

    suspend fun detail(id: Int): Flow<DetailPokemonResult>

    suspend fun evolution(
        pokemonId: Int,
        evolutionId: Int?
    ): Flow<List<EvolutionResult>>

    suspend fun saveMyPokemon(myPokemonEntity: MyPokemonEntity)
    suspend fun listMyPokemon(): Flow<List<MyPokemonResult>>
    suspend fun getMyPokemon(id: Int): Flow<List<MyPokemonResult>>
    suspend fun renameMyPokemon(myPokemonEntity: MyPokemonEntity)
    suspend fun releaseMyPokemon(id: Int)
}