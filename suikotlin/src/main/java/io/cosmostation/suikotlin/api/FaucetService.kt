package io.cosmostation.suikotlin.api

import com.github.cosmostation.suikotlin.BuildConfig
import io.cosmostation.suikotlin.SuiClient
import io.cosmostation.suikotlin.model.FixedAmountRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface FaucetService {
    companion object {
        fun create(): FaucetService {
            val builder = Retrofit.Builder().baseUrl(SuiClient.instance.currentNetwork.faucetUrl)
                .addConverterFactory(GsonConverterFactory.create())

            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val client = OkHttpClient.Builder().addInterceptor(interceptor)
                    .connectTimeout(15, TimeUnit.SECONDS).build()
                builder.client(client)
            }

            return builder.build().create(FaucetService::class.java)
        }
    }

    @POST("/gas")
    suspend fun faucet(@Body fixedAmountRequest: FixedAmountRequest): Response<Any>
}