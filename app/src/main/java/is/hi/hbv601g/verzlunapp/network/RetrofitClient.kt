package `is`.hi.hbv601g.verzlunapp.network

import android.content.Context
import android.util.Log
import `is`.hi.hbv601g.verzlunapp.persistence.UserStorage
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://verzla-71cda7a37a2e.herokuapp.com/"

    private var userStorage: UserStorage? = null

    fun initialize(context: Context) {
        if (userStorage == null) {
            userStorage = UserStorage(context.applicationContext)
            Log.i("RetrofitClient", "UserStorage initialized.")
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()

        val token = userStorage?.getAuthToken()

        token?.let {
            requestBuilder.header("Authorization", "Bearer $it")

        } ?: run {

        }

        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient by lazy {
        if (userStorage == null) {
            throw IllegalStateException("RetrofitClient must be initialized with Context before accessing OkHttpClient.")
        }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit by lazy {
        if (userStorage == null) {
            throw IllegalStateException("RetrofitClient must be initialized with Context before accessing Retrofit.")
        }
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    fun updateStoredToken(newToken: String?) {
        userStorage?.saveAuthToken(newToken)
        Log.i("RetrofitClient", "Stored token updated.")
    }

    fun clearStoredToken() {
        userStorage?.saveAuthToken(null)
        Log.i("RetrofitClient", "Stored token cleared.")
    }
}