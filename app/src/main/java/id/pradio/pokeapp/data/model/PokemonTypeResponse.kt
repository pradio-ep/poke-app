package id.pradio.pokeapp.data.model

import com.google.gson.annotations.SerializedName

data class PokemonTypeResponse(

    @SerializedName("types")
    val types: List<PokemonType>
)