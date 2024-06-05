package id.pradio.pokeapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_pokemon")
data class MyPokemonEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "display_name")
    val displayName: String,

    @ColumnInfo(name = "rename_attempt", defaultValue = "0")
    val renameAttempt: Int
)
