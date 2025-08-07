package com.automation.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.automation.BuildConfig
import com.automation.common.utils.getDefaultSharedPreferences
import com.automation.data.api.ApiHost
import com.automation.data.api.KeeperService
import com.automation.data.api.UrlSettingsManager
import com.automation.data.api.client.InternetAwareOkHttpClient
import com.automation.data.api.interceptor.AuthorizationInterceptor
import com.automation.data.database.KeeperDatabase
import com.automation.data.repositories.AllApplicationDataSourceImpl
import com.automation.data.repositories.LocalDataSourceImpl
import com.automation.domain.datasource.AllApplicationDataSource
import com.automation.domain.datasource.LocalDataSource
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

private const val NAME_API_HOST_URL = "api_host_url"
private const val DEFAULT_TIMEOUT = 10L

val dataModule: Module = module {
    single<UrlSettingsManager> {
        UrlSettingsManager(androidContext().getDefaultSharedPreferences(), androidContext())
    }
    single<AllApplicationDataSource> { AllApplicationDataSourceImpl(preferences = get(), service = get()) }
    single<LocalDataSource> {
        LocalDataSourceImpl(
            preferences = get(),
            messagesDb = get<KeeperDatabase>().messagesDao(),
        )
    }

    single { Json { ignoreUnknownKeys = true } }

    single(named(NAME_API_HOST_URL)) {
        androidContext().getString(ApiHost.getApiHost())
    }

    single {
        Retrofit.Builder()
            .baseUrl(get<UrlSettingsManager>().getBaseUrl())
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
            .create(KeeperService::class.java)
    }

    single<SecretKeyProvider> {
        object : SecretKeyProvider {
            override fun getSecretKey(): String = BuildConfig.API_KEY
        }
    }

    single {
        val okhttpBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            okhttpBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }

        okhttpBuilder.addInterceptor(AuthorizationInterceptor(get(), get<SecretKeyProvider>().getSecretKey()))


        val client: OkHttpClient = InternetAwareOkHttpClient(androidContext(), okhttpBuilder.callTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .build() )

        client
    }
}
interface SecretKeyProvider {
    fun getSecretKey(): String
}