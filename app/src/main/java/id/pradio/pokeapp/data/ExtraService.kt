package id.pradio.pokeapp.data

import id.pradio.pokeapp.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path

interface ExtraService {

    @GET("pokemon/catch")
    suspend fun catchPokemon(): CatchPokemonResponse

    @GET("pokemon/rename/{name}/{attempt}")
    suspend fun renamePokemon(
        @Path("name") name: String,
        @Path("attempt") attempt: Int
    ): RenamePokemonResponse

    @GET("pokemon/release")
    suspend fun releasePokemon(): ReleasePokemonResponse
}