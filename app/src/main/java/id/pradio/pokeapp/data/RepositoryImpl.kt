package id.pradio.pokeapp.data

import id.pradio.pokeapp.R
import id.pradio.pokeapp.core.ext.clearWhiteSpace
import id.pradio.pokeapp.data.entity.*
import id.pradio.pokeapp.data.model.Evolve
import id.pradio.pokeapp.data.model.NameAndUrl
import id.pradio.pokeapp.data.model.PokemonAbility
import id.pradio.pokeapp.data.resultmodel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val service: PokemonService,
    private val elementTypes: Map<String, Pair<Int, Int>>,
    private val dao: PokemonDao
) : Repository {

    private fun String.urlLastToId(): Int {
        return split("/".toRegex()).dropLast(1).last().toInt()
    }

    override suspend fun listPokemon(offset: Int, sortByName: Boolean): Flow<List<PokemonResult>> = flow {
        coroutineScope {
            //get from local
            val data = async { dao.listPokemon() }
            if (data.await().isNotEmpty()) {
                val list = data.await().map {
                    PokemonResult(
                        it.pokemon.id,
                        it.pokemon.name,
                        it.pokemon.displayName,
                        it.elements.map { type ->
                            ElementType(
                                type.name,
                                elementTypes[type.name]?.first ?: R.drawable.ic_type_normal,
                                elementTypes[type.name]?.second
                                    ?: R.color.color_type_normal,
                            )
                        }
                    )
                }

                //emmit result from local
                if (sortByName) {
                    emit(list.sortedBy { it.displayName })
                } else {
                    emit(list)
                }
            } else {
                //async request from network
                val requestPokemon = async { service.listPokemon(offset) }

                //load element (type) each item from network and map it to result
                val pokemons = requestElementPokemon(requestPokemon.await().result)

                //prepare save to local
                val pokemonEntities = pokemons.map {
                    PokemonEntity(it.id, it.name, it.displayName)
                }

                //save toLocal
                val savePokemons = async { dao.saveAllPokemon(pokemonEntities) }
                val saveElements = async {
                    val elementEntities = mutableListOf<ElementEntity>()
                    pokemons.forEach {
                        elementEntities.addAll(it.elements.map { element ->
                            ElementEntity(it.id, element.name)
                        })
                    }
                    dao.deleteAllElements()
                    dao.saveElements(elementEntities)
                }
                savePokemons.await()
                saveElements.await()

                //emit result from network
                if (sortByName) {
                    emit(pokemons.sortedBy { it.displayName })
                } else {
                    emit(pokemons)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun searchPokemon(name: String): Flow<List<PokemonResult>> = flow {
        coroutineScope {
            //get from local
            val data = dao.searchPokemon(name)
            if (data.isNotEmpty()) {
                val list = data.map {
                    PokemonResult(
                        it.pokemon.id,
                        it.pokemon.name,
                        it.pokemon.displayName,
                        it.elements.map { type ->
                            ElementType(
                                type.name,
                                elementTypes[type.name]?.first ?: R.drawable.ic_type_normal,
                                elementTypes[type.name]?.second
                                    ?: R.color.color_type_normal,
                            )
                        }
                    )
                }

                //emmit result from local
                emit(list)
            } else {
                emit(emptyList<PokemonResult>())
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getPokemon(id: Int): Flow<PokemonResult> = flow<PokemonResult> {
        coroutineScope {
            //get from local
            val data = dao.getPokemon(id)
            val pokemon = PokemonResult(
                data.pokemon.id,
                data.pokemon.name,
                data.pokemon.displayName,
                data.elements.map { type ->
                    ElementType(
                        type.name,
                        elementTypes[type.name]?.first ?: R.drawable.ic_type_normal,
                        elementTypes[type.name]?.second
                            ?: R.color.color_type_normal,
                    )
                }
            )
            //emmit result from local
            emit(pokemon)
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun requestElementPokemon(list: List<NameAndUrl>): List<PokemonResult> {
        return list.map {
            val id = it.url.urlLastToId()
            val displayName = it.name.capitalize(Locale.getDefault())

            val types = service.getTypesById(id).types.map { type ->
                ElementType(
                    type.type.name,
                    elementTypes[type.type.name]?.first ?: R.drawable.ic_type_normal,
                    elementTypes[type.type.name]?.second ?: R.color.color_type_normal,
                )
            }

            PokemonResult(id, it.name, displayName, types)
        }
    }

    override suspend fun detail(id: Int): Flow<DetailPokemonResult> = flow {
        coroutineScope {
            //get from local
            val localDetail = async { getDetailFromLocal(id) }
            if (localDetail.await() != null) {
                emit(localDetail.await()!!)
            } else {
                val reqPokemon = async { service.getPokemonById(id) }
                val reqSpecies = async { service.getSpeciesById(id) }

                val pokemon = reqPokemon.await()
                val species = reqSpecies.await()

                val overview = species.flavorTextEntries.firstOrNull {
                    it.language.name == "en"
                }?.flavorText?.clearWhiteSpace()

                val abilities = async { requestAbilities(pokemon.abilities) }

                val evolutionId = species.evolutionChain.url.urlLastToId()

                val weight = pokemon.weight

                val height = pokemon.height

                val detailResult = DetailPokemonResult(
                    evolutionId,
                    weight,
                    height,
                    overview,
                    StatResult(
                        pokemon.stat[0].baseStat ?: 0,
                        pokemon.stat[1].baseStat ?: 0,
                        pokemon.stat[2].baseStat ?: 0,
                        pokemon.stat[3].baseStat ?: 0,
                        pokemon.stat[4].baseStat ?: 0,
                        pokemon.stat[5].baseStat ?: 0
                    ),
                    abilities.await()
                )

                //save to local
                val saveDetail = async {
                    dao.deleteDetailByPokemonId(id)
                    dao.saveDetail(
                        DetailEntity(
                            pokemon.id,
                            overview,
                            evolutionId,
                            weight,
                            height
                        )
                    )
                }

                val saveStat = async {
                    dao.deleteStatByPokemonId(id)
                    dao.saveStat(
                        StatEntity(
                            id,
                            pokemon.stat[0].baseStat ?: 0,
                            pokemon.stat[1].baseStat ?: 0,
                            pokemon.stat[2].baseStat ?: 0,
                            pokemon.stat[3].baseStat ?: 0,
                            pokemon.stat[4].baseStat ?: 0,
                            pokemon.stat[5].baseStat ?: 0
                        )
                    )
                }

                val saveAbility = async {
                    dao.deleteAbilityByPokemonId(id)
                    dao.saveAllAbility(abilities.await().map {
                        AbilityEntity(id, it.name, it.overview)
                    })
                }

                saveDetail.await()
                saveStat.await()
                saveAbility.await()

                //emit result from network
                emit(detailResult)
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun getDetailFromLocal(id: Int): DetailPokemonResult? {
        val data = dao.getDetail(id)
        var result: DetailPokemonResult? = null
        if (data?.detail != null && data.stat != null && data.abilities.isNotEmpty()) {
            result = DetailPokemonResult(
                data.detail.evolutionId,
                data.detail.weight,
                data.detail.height,
                data.detail.overview,
                StatResult(
                    data.stat.hp,
                    data.stat.atk,
                    data.stat.def,
                    data.stat.satk,
                    data.stat.sdef,
                    data.stat.def
                ),
                data.abilities.map {
                    NameAndOverview(it.name, it.overview)
                }
            )
        }
        return result
    }

    override suspend fun evolution(
        pokemonId: Int,
        evolutionId: Int?
    ): Flow<List<EvolutionResult>> = flow {
        coroutineScope {
            //Req from network
            val chain = service.getEvolutionChain(evolutionId!!)
            //Get from local
            val data = dao.getEvolutionByEvolutionId(evolutionId)
            if (data.isNotEmpty()) {
                val result = data.map {
                    EvolutionResult(
                        it.fromName,
                        it.fromId,
                        it.toName,
                        it.toId,
                        it.trigger
                    )
                }
                //emit result from local
                emit(result)
            }

            val list = mutableListOf<EvolutionResult>()
            var evolve: Evolve = chain.chain
            var find = true
            do {
                try {
                    val evolutionDetails = evolve.evolvesTo[0].evolutionDetails[0]
                    val evolutionTrigger = when (evolutionDetails.trigger.name) {
                        "level-up" -> {
                            when {
                                evolutionDetails.minLevel != null -> "Lv. ${evolutionDetails.minLevel}"
                                evolutionDetails.minHappiness != null -> "Lv. Up & Happy ${evolutionDetails.minHappiness}"
                                evolutionDetails.minBeauty != null -> "Lv. Up & Beauty ${evolutionDetails.minBeauty}"
                                evolutionDetails.minAffection != null -> "Lv. Up & Affect ${evolutionDetails.minAffection}"
                                else -> "Level Up"
                            }
                        }
                        "use-item" -> evolutionDetails.item?.name ?: "Use Item"
                        else -> "Level Up"
                    }
                    list.add(
                        EvolutionResult(
                            evolve.species.name.capitalize(Locale.getDefault()),
                            evolve.species.url.urlLastToId(),
                            evolve.evolvesTo[0].species.name.capitalize(Locale.getDefault()),
                            evolve.evolvesTo[0].species.url.urlLastToId(),
                            evolutionTrigger
                        )
                    )
                    if (evolve.evolvesTo[0].evolvesTo.isEmpty()) {
                        find = false
                    } else {
                        evolve = evolve.evolvesTo[0]
                    }
                } catch (e: Throwable) {
                    find = false
                }
            } while (find)

            //save to local
            dao.saveAllEvolution(list.map {
                EvolutionEntity(
                    pokemonId,
                    evolutionId,
                    it.from,
                    it.fromId,
                    it.to,
                    it.toId,
                    it.trigger
                )
            })

            //emit result from network
            emit(list)
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun requestAbilities(abilitiesFromPokemon: List<PokemonAbility>): List<NameAndOverview> {
        return abilitiesFromPokemon.map {
            val id = it.ability.url.urlLastToId()
            val ability = service.getAbility(id)

            val formattedName = ability.names.firstOrNull { name ->
                name.language.name == "en"
            }?.name ?: ability.name

            val abilityOverview = ability.flavorTextEntries.firstOrNull { flavor ->
                flavor.language.name == "en"
            }?.flavorText?.clearWhiteSpace()

            NameAndOverview(formattedName, abilityOverview)
        }
    }

    override suspend fun saveMyPokemon(myPokemonEntity: MyPokemonEntity) {
        dao.saveMyPokemon(myPokemonEntity)
    }

    override suspend fun listMyPokemon(): Flow<List<MyPokemonResult>> = flow {
        coroutineScope {
            val data = async { dao.listMyPokemon() }
            if (data.await().isNotEmpty()) {
                val list = data.await().map {
                    MyPokemonResult(
                        it.id,
                        it.name,
                        it.displayName,
                        it.renameAttempt
                    )
                }
                emit(list)
            } else {
                emit(emptyList<MyPokemonResult>())
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getMyPokemon(id: Int): Flow<List<MyPokemonResult>> = flow {
        coroutineScope {
            val data = async { dao.getMyPokemon(id) }
            if (data.await().isNotEmpty()) {
                val list = data.await().map {
                    MyPokemonResult(
                        it.id,
                        it.name,
                        it.displayName,
                        it.renameAttempt
                    )
                }
                emit(list)
            } else {
                emit(emptyList<MyPokemonResult>())
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun renameMyPokemon(myPokemonEntity: MyPokemonEntity) {
        dao.renameMyPokemon(myPokemonEntity)
    }

    override suspend fun releaseMyPokemon(id: Int) {
        dao.releaseMyPokemonById(id)
    }
}