package io.cosmostation.suikotlin.api

import com.github.cosmostation.suikotlin.BuildConfig
import io.cosmostation.suikotlin.SuiClient
import io.cosmostation.suikotlin.model.JsonRpcRequest
import io.cosmostation.suikotlin.model.JsonRpcResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface ApiService {
    companion object {
        fun create(): ApiService {
            val builder = Retrofit.Builder().baseUrl(SuiClient.instance.currentNetwork.rpcUrl).addConverterFactory(GsonConverterFactory.create())

            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val client = OkHttpClient.Builder().addInterceptor(interceptor).connectTimeout(60, TimeUnit.SECONDS).build()
                builder.client(client)
            } else {
                val client = OkHttpClient.Builder().connectTimeout(180, TimeUnit.SECONDS).build()
                builder.client(client)
            }

            return builder.build().create(ApiService::class.java)
        }
    }

    @POST("/")
    suspend fun postJsonRpcRequests(@Body requests: List<JsonRpcRequest>): Response<List<JsonRpcResponse>>

    @POST("/")
    suspend fun postJsonRpcRequest(@Body requests: JsonRpcRequest): Response<JsonRpcResponse>
}
