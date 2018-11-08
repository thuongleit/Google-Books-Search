package me.thuongle.googlebookssearch.di

import dagger.Module
import dagger.Provides
import me.thuongle.googlebookssearch.api.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [NetworkLoggerModule::class])
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
        @NetworkInterceptor loggingInterceptors:
        Set<@JvmSuppressWildcards okhttp3.Interceptor>
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                loggingInterceptors.forEach {
                    addNetworkInterceptor(it)
                }
            }.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GOOGLE_BOOK_API_ENDPOINT)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofitService(retrofit: Retrofit): GoogleBooksRetrofitService =
        retrofit.create(GoogleBooksRetrofitService::class.java)

    @Singleton
    @Provides
    fun provideRestClient(): RestClient = RestClient()

    @Singleton
    @Provides
    fun provideLegacyService(restClient: RestClient): GoogleBooksLegacyService =
        GoogleBooksLegacyService(restClient)

    @Singleton
    @Provides
    fun provideBookService(
        legacyService: GoogleBooksLegacyService,
        retrofitService: GoogleBooksRetrofitService
    ): BookService {
        return BookServiceImpl(legacyService, retrofitService)
    }
}