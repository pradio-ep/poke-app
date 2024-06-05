package id.pradio.pokeapp.data

import id.pradio.pokeapp.R
import id.pradio.pokeapp.data.entity.ElementEntity
import id.pradio.pokeapp.data.entity.PokemonEntity
import id.pradio.pokeapp.data.entity.PokemonWithElement
import id.pradio.pokeapp.data.resultmodel.ElementType
import id.pradio.pokeapp.data.resultmodel.PokemonResult
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class RepositoryTest {

	private val testDispatcher = StandardTestDispatcher()

	lateinit var repository: RepositoryImpl

	private var elementTypes: Map<String, Pair<Int, Int>> = mapOf(
		"normal" to (R.drawable.ic_type_normal to R.color.color_type_normal),
		"fighting" to (R.drawable.ic_type_fighting to R.color.color_type_fighting),
		"dragon" to (R.drawable.ic_type_dragon to R.color.color_type_dragon),
		"flying" to (R.drawable.ic_type_flying to R.color.color_type_flying),
		"poison" to (R.drawable.ic_type_poison to R.color.color_type_poison),
		"ground" to (R.drawable.ic_type_ground to R.color.color_type_ground),
		"rock" to (R.drawable.ic_type_rock to R.color.color_type_rock),
		"bug" to (R.drawable.ic_type_bug to R.color.color_type_bug),
		"ghost" to (R.drawable.ic_type_ghost to R.color.color_type_ghost),
		"steel" to (R.drawable.ic_type_steel to R.color.color_type_steal),
		"fire" to (R.drawable.ic_type_fire to R.color.color_type_fire),
		"water" to (R.drawable.ic_type_water to R.color.color_type_water),
		"grass" to (R.drawable.ic_type_grass to R.color.color_type_grass),
		"electric" to (R.drawable.ic_type_electric to R.color.color_type_electric),
		"psychic" to (R.drawable.ic_type_psychic to R.color.color_type_physic),
		"ice" to (R.drawable.ic_type_ice to R.color.color_type_ice),
		"dark" to (R.drawable.ic_type_dark to R.color.color_type_dark),
		"fairy" to (R.drawable.ic_type_fairy to R.color.color_type_fairy)
	)

	@Mock
	lateinit var pokemonService: PokemonService

	@Mock
	lateinit var dao: PokemonDao

	@Before
	fun setup() {
		MockitoAnnotations.openMocks(this)
		Dispatchers.setMain(testDispatcher)
		repository = RepositoryImpl(pokemonService, elementTypes, dao)
	}

	private val listPokemon = listOf(
		PokemonWithElement(
			PokemonEntity(1, "Bulbasaur", "Bulbasaur"),
			listOf(ElementEntity(1, "grass"))
		),
		PokemonWithElement(
			PokemonEntity(2, "Ivysaur", "Ivysaur"),
			listOf(ElementEntity(2, "grass"))
		),
		PokemonWithElement(
			PokemonEntity(3, "Venusaur", "Venusaur"),
			listOf(ElementEntity(3, "grass"))
		),
		PokemonWithElement(
			PokemonEntity(4, "Charmander", "Charmander"),
			listOf(ElementEntity(4, "fire"))
		),
		PokemonWithElement(
			PokemonEntity(5, "Charmeleon", "Charmeleon"),
			listOf(ElementEntity(5, "fire"))
		),
		PokemonWithElement(
			PokemonEntity(6, "Charizard", "Charizard"),
			listOf(ElementEntity(6, "fire"))
		)
	)

	private val listPokemonResult = listOf(
		PokemonResult(1, "Bulbasaur", "Bulbasaur", listOf(ElementType("grass", R.drawable.ic_type_grass, R.color.color_type_grass))),
		PokemonResult(2, "Ivysaur", "Ivysaur", listOf(ElementType("grass", R.drawable.ic_type_grass, R.color.color_type_grass))),
		PokemonResult(3, "Venusaur", "Venusaur", listOf(ElementType("grass", R.drawable.ic_type_grass, R.color.color_type_grass))),
		PokemonResult(4, "Charmander", "Charmander", listOf(ElementType("fire", R.drawable.ic_type_fire, R.color.color_type_fire))),
		PokemonResult(5, "Charmeleon", "Charmeleon", listOf(ElementType("fire", R.drawable.ic_type_fire, R.color.color_type_fire))),
		PokemonResult(6, "Charizard", "Charizard", listOf(ElementType("fire", R.drawable.ic_type_fire, R.color.color_type_fire)))
	)

	private val listSearchPokemon = listOf(
		PokemonWithElement(
			PokemonEntity(1, "Bulbasaur", "Bulbasaur"),
			listOf(ElementEntity(1, "grass"))
		),
		PokemonWithElement(
			PokemonEntity(2, "Ivysaur", "Ivysaur"),
			listOf(ElementEntity(2, "grass"))
		),
		PokemonWithElement(
			PokemonEntity(3, "Venusaur", "Venusaur"),
			listOf(ElementEntity(3, "grass"))
		)
	)

	private val listSearchPokemonResult = listOf(
		PokemonResult(1, "Bulbasaur", "Bulbasaur", listOf(ElementType("grass", R.drawable.ic_type_grass, R.color.color_type_grass))),
		PokemonResult(2, "Ivysaur", "Ivysaur", listOf(ElementType("grass", R.drawable.ic_type_grass, R.color.color_type_grass))),
		PokemonResult(3, "Venusaur", "Venusaur", listOf(ElementType("grass", R.drawable.ic_type_grass, R.color.color_type_grass)))
	)

	private val pokemonDetail = PokemonWithElement(
		PokemonEntity(1, "Bulbasaur", "Bulbasaur"),
		listOf(ElementEntity(1, "grass"))
	)

	private val pokemonDetailResult = PokemonResult(1, "Bulbasaur", "Bulbasaur", listOf(ElementType("grass", R.drawable.ic_type_grass, R.color.color_type_grass)))

	@Test
	fun `Get list all pokemon`() {
		val offset = 0
		val sortByName = false
		runBlocking {
			Mockito.`when`(dao.listPokemon())
				.thenReturn(listPokemon)

			val result = repository.listPokemon(offset, sortByName).first()

			assertEquals(listPokemonResult, result)
		}
	}

	@Test
	fun `Search pokemon by name`() {
		val name = "saur"
		runBlocking {
			Mockito.`when`(dao.searchPokemon(name))
				.thenReturn(listSearchPokemon)

			val result = repository.searchPokemon(name).first()

			assertEquals(listSearchPokemonResult, result)
		}
	}

	@Test
	fun `Get pokemon detail`() {
		val id = 1
		runBlocking {
			Mockito.`when`(dao.getPokemon(id))
				.thenReturn(pokemonDetail)

			val result = repository.getPokemon(id).first()

			assertEquals(pokemonDetailResult, result)
		}
	}
}