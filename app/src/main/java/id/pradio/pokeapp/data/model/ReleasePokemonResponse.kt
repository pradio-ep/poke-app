package id.pradio.pokeapp.data.model

import com.google.gson.annotations.SerializedName

data class ReleasePokemonResponse(

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: Int
)
