package id.pradio.pokeapp.data

import id.pradio.pokeapp.data.resultmodel.CatchPokemonResult
import id.pradio.pokeapp.data.resultmodel.ReleasePokemonResult
import id.pradio.pokeapp.data.resultmodel.RenamePokemonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ExtraRepositoryImpl @Inject constructor(
    private val service: ExtraService
) : ExtraRepository {
    override suspend fun catchPokemon(): Flow<CatchPokemonResult> = flow {
        val data = service.catchPokemon()
        val result = CatchPokemonResult(
            data.message,
            data.result
        )
        emit(result)
    }.flowOn(Dispatchers.IO)

    override suspend fun renamePokemon(name: String, attempt: Int): Flow<RenamePokemonResult> = flow {
        val data = service.renamePokemon(name, attempt)
        val result = RenamePokemonResult(
            data.message,
            data.result
        )
        emit(result)
    }.flowOn(Dispatchers.IO)

    override suspend fun releasePokemon(): Flow<ReleasePokemonResult> = flow {
        val data = service.releasePokemon()
        val result = ReleasePokemonResult(
            data.message,
            data.result
        )
        emit(result)
    }.flowOn(Dispatchers.IO)

}