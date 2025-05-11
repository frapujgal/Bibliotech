package dam.paco.bibliotech.data.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // ngrok http http://localhost:8080
    // AÑADIR LA IP RECIBIDA A LA SIGUIENTE URL
    //private const val BASE_URL = "https://773e-2a0c-5a82-c100-2b00-85f2-ba80-15c4-749.ngrok-free.app/api/"
    private const val BASE_URL = "http://192.168.1.205:8080/api/"

    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }
}