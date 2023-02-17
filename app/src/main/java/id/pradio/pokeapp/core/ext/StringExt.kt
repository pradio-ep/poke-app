package id.pradio.pokeapp.core.ext

fun String.clearWhiteSpace(): String {
    return replace("\\s".toRegex(), " ")
}