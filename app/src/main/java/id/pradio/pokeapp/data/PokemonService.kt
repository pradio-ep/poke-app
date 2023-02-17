package id.pradio.pokeapp.data

import id.pradio.pokeapp.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonService {

    @GET("pokemon?limit=45")
    suspend fun listPokemon(@Query("offset") offset: Int): ListResponse<NameAndUrl>

    @GET("pokemon/{id}")
    suspend fun getTypesById(@Path("id") id: Int): PokemonTypeResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonById(@Path("id") id: Int): PokemonDetailResponse

    @GET("pokemon-species/{id}")
    suspend fun getSpeciesById(@Path("id") id: Int): SpeciesDetailResponse

    @GET("evolution-chain/{id}")
    suspend fun getEvolutionChain(@Path("id") evolutionId: Int): EvolutionResponse

    @GET("ability/{id}")
    suspend fun getAbility(@Path("id") abilityId: Int): AbilityResponse
}