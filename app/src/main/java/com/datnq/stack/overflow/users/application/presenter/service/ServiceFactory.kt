package com.datnq.stack.overflow.users.application.presenter.service

import com.datnq.stack.overflow.users.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * @author dat nguyen
 * @since 2019 Sep 12
 */
class ServiceFactory {
    companion object {
        /**
         * Create service api
         * @return [ServiceApi]
         */
        @JvmStatic
        fun create(): ServiceApi {
            val gson = GsonBuilder().serializeNulls().create()
            val callAdapter = RxJava2CallAdapterFactory.create()
            val retrofitBuilder = Retrofit.Builder()
            retrofitBuilder.baseUrl(BuildConfig.SERVER_URL)
            retrofitBuilder.client(createHttpClient())
            retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson))
            retrofitBuilder.addCallAdapterFactory(callAdapter)
            return retrofitBuilder.build().create(ServiceApi::class.java)
        }

        /**
         * Initialize http client
         * @return [OkHttpClient]
         */
        @JvmStatic
        private fun createHttpClient(): OkHttpClient {
            val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

            httpClient.addInterceptor { chain ->
                val original: Request = chain.request()
                val builder: Request.Builder = original.newBuilder()
                builder.header("Accept", "x-www-form-urlencoded")
                builder.header("Accept-Language", "en")
                builder.method(original.method, original.body).build()
                val request: Request = builder.build()
                chain.proceed(request)
            }

            if (BuildConfig.DEBUG) {
                val interceptorBody = HttpLoggingInterceptor()
                interceptorBody.setLevel(HttpLoggingInterceptor.Level.BODY)
                httpClient.addInterceptor(interceptorBody)
            }
            httpClient.connectTimeout(300, TimeUnit.SECONDS)
            httpClient.readTimeout(300, TimeUnit.SECONDS)
            httpClient.writeTimeout(300, TimeUnit.SECONDS)
            httpClient.sslSocketFactory(sslSocketFactory(), trustAllCerts()[0])
            httpClient.hostnameVerifier{ _, _ -> true }
            return httpClient.build()
        }

        @JvmStatic
        private fun trustAllCerts(): Array<X509TrustManager> {
            return arrayOf(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String?) {
                    // Do nothing
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String?) {
                    // Do nothing
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
            )

        }

        @JvmStatic
        private fun sslSocketFactory(): SSLSocketFactory {
            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts(), SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            return sslContext.socketFactory
        }
    }
}