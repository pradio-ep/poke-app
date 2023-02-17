package id.pradio.pokeapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detail")
data class DetailEntity(
    @ColumnInfo(name = "pokemon_id")
    val pokemonId: Int,

    @ColumnInfo(name = "overview")
    val overview: String?,

    @ColumnInfo(name = "evolution_id")
    val evolutionId: Int,

    @ColumnInfo(name = "weight")
    val weight: Int,

    @ColumnInfo(name = "height")
    val height: Int,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
