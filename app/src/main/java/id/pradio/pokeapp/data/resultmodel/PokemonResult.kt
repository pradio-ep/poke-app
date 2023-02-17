package id.pradio.pokeapp.data.resultmodel

data class PokemonResult(
    val id: Int,
    val name: String,
    val displayName: String,
    val elements: List<ElementType>
)
