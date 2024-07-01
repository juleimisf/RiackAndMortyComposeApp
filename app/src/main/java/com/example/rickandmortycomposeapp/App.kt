package com.example.rickandmortycomposeapp

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.intercept.Interceptor
import coil.memory.MemoryCache
import coil.request.ImageResult
import coil.util.Logger
import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import java.util.concurrent.TimeUnit

class App : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .authenticator { _, response ->
                val credential = Credentials.basic("example_username", "example_password")
                response.request.newBuilder()
                    .header("Authorization", credential)
                    .build()
            }.build()

        return ImageLoader.Builder(this)
            .okHttpClient { client }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(5 * 1024 * 1024)
                    .build()
            }
            .components {
                add(SvgDecoder.Factory())
            }
            .crossfade(true)
            .crossfade(500)
            .logger(CustomLogger())
            .respectCacheHeaders(false)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }

}

class CustomLogger : Logger {
    override var level: Int = Log.VERBOSE

    override fun log(tag: String, priority: Int, message: String?, throwable: Throwable?) {
        Log.println(priority, tag, message ?: "null")
        throwable?.let { Log.e(tag, "Error", it) }
    }
}

class CoilLoggingInterceptor : Interceptor {
    companion object {
        val TAG: String = CoilLoggingInterceptor::class.java.simpleName
    }

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val logModel = CoilLogModel(
            url = chain.request.data.toString(),
            width = chain.size.width.toString(),
            height = chain.size.height.toString()
        )
        Log.d(TAG, logModel.toString())
        return chain.proceed(chain.request)
    }

    data class CoilLogModel(
        val url: String,
        val width: String,
        val height: String
    ) {
        override fun toString(): String {
            return """
                URL: $url
                width: $width
                height: $height
            """.trimIndent()
        }
    }
}