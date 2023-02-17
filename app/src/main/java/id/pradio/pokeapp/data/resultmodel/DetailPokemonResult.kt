package id.pradio.pokeapp.data.resultmodel

data class DetailPokemonResult(
    val evolutionId : Int,
    val weight: Int,
    val height: Int,
    val overview: String?,
    val stat: StatResult,
    val abilities: List<NameAndOverview>
)
