package id.pradio.pokeapp.ui.mypokemon

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

interface SaveUiModel

@Keep
@Parcelize
data class MyPokemonItemModel(
    val id: Int,
    val name: String,
    val displayName: String,
    val renameAttempt: Int,
    val imageUrl: String?,
) : SaveUiModel, Parcelable

object Empty : SaveUiModel