package id.pradio.pokeapp.data.model

import com.google.gson.annotations.SerializedName

data class CatchPokemonResponse(

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: Boolean
)
