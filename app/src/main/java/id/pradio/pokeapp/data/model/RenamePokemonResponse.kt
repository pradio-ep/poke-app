package id.pradio.pokeapp.data.model

import com.google.gson.annotations.SerializedName

data class RenamePokemonResponse(

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: String
)
