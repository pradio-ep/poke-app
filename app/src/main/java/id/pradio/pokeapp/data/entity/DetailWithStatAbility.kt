package id.pradio.pokeapp.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class DetailWithStatAbility(
    @Embedded val detail: DetailEntity?,
    @Relation(entity = StatEntity::class, parentColumn = "id", entityColumn = "pokemon_id")
    val stat: StatEntity?,
    @Relation(entity = AbilityEntity::class, parentColumn = "id", entityColumn = "pokemon_id")
    val abilities: List<AbilityEntity>
)
