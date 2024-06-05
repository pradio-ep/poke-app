package id.pradio.pokeapp.data.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.pradio.pokeapp.BuildConfig
import id.pradio.pokeapp.data.ExtraService
import id.pradio.pokeapp.data.PokemonService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideChuckerCollector(@ApplicationContext context: Context) = ChuckerCollector(
        context = context,
        showNotification = true,
        retentionPeriod = RetentionManager.Period.ONE_DAY
    )

    @Provides
    fun provideChuckerInterceptor(
        @ApplicationContext context: Context,
        chuckerCollector: ChuckerCollector
    ) = ChuckerInterceptor
        .Builder(context)
        .collector(chuckerCollector)
        .alwaysReadResponseBody(true)
        .build()

    @Provides
    fun provideOkHttpClient(chuckerInterceptor: ChuckerInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.BUILD_TYPE == "debug") {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            })
            builder.addInterceptor(chuckerInterceptor)
        }
        builder.connectTimeout(30L, TimeUnit.SECONDS)
        builder.readTimeout(30L, TimeUnit.SECONDS)
        builder.writeTimeout(30L, TimeUnit.SECONDS)

        return builder.build()
    }

    @Provides
    fun providePokemonService(client: OkHttpClient): PokemonService {
        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonService::class.java)
    }

    @Provides
    fun provideExtraService(client: OkHttpClient): ExtraService {
        return Retrofit.Builder()
            .baseUrl("https://ktor-pokemon-8b6c412af9bd.herokuapp.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExtraService::class.java)
    }
}