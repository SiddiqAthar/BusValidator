package com.trverse.busvalidator.network

import com.google.gson.GsonBuilder
import com.trverse.busvalidator.utilities.Utils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object ClientInstance {

    private var retrofit: Retrofit? = null
    fun create(): Retrofit? {

        /*  if (retrofit == null) {
              val builder = Retrofit.Builder()
                  .baseUrl(urlType.baseURL.url)
                  .addConverterFactory(GsonConverterFactory.create())
              val interceptor = HttpLoggingInterceptor()
              interceptor.level = HttpLoggingInterceptor.Level.BODY
              val okHttpClientBuilder = OkHttpClient.Builder()
              okHttpClientBuilder.connectTimeout(10, TimeUnit.MINUTES)
              okHttpClientBuilder.readTimeout(10, TimeUnit.MINUTES)
              okHttpClientBuilder.addInterceptor(interceptor)
              builder.client(okHttpClientBuilder.build())
              retrofit = builder.build()
          } else {
              val builder = Retrofit.Builder()
                  .baseUrl(urlType.baseURL.url)
                  .addConverterFactory(GsonConverterFactory.create())
              retrofit = builder.build()
          }
          return retrofit*/

        if (retrofit == null) {

            val gson = GsonBuilder().setLenient()
                .create()
            val builder = Retrofit.Builder()
                .baseUrl(URLType.DATA_SYNC_SERVICE.baseURL.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClientBuilder = OkHttpClient.Builder()
            okHttpClientBuilder.connectTimeout(30, TimeUnit.SECONDS)
            okHttpClientBuilder.readTimeout(30, TimeUnit.SECONDS)
            okHttpClientBuilder.addInterceptor(interceptor)
            builder.client(okHttpClientBuilder.build())
            retrofit = builder.build()
        }

        return retrofit

    }


}