package id.pradio.pokeapp.data.model

import com.google.gson.annotations.SerializedName

data class PokemonDetailResponse(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("height")
    val height: Int,

    @SerializedName("weight")
    val weight: Int,

    @SerializedName("abilities")
    val abilities: List<PokemonAbility>,

    @SerializedName("stats")
    val stat: List<PokemonStat>,

    @SerializedName("types")
    val types: List<PokemonType>?,

    @SerializedName("sprites")
    val sprites: Sprite
)
