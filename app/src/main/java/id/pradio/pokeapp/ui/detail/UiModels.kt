package id.pradio.pokeapp.ui.detail

import id.pradio.pokeapp.ui.home.HomeUiModel

interface DetailUiModel

data class StatItemModel(
    val hp: Int,
    val atk: Int,
    val def: Int,
    val satk: Int,
    val sdef: Int,
    val spd: Int
) : DetailUiModel

data class EvolutionItemModel(
    val currentId: Int,
    val fromId: Int,
    val fromImageUrl: String,
    val fromName: String,
    val toId: Int,
    val toImageUrl: String,
    val toName: String,
    val trigger: String
) : DetailUiModel

object LoadingItemModel : DetailUiModel, HomeUiModel
object ConnectionError : DetailUiModel, HomeUiModel