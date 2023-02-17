package id.pradio.pokeapp.data.resultmodel

sealed class ErrorResult {
    object Connection : ErrorResult()
    data class Failed(val message: String?) : ErrorResult()
}