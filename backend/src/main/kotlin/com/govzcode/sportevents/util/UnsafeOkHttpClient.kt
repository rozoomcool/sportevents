package com.govzcode.sportevents.util

import okhttp3.OkHttpClient
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class UnsafeOkHttpClient {
    companion object {
        fun createUnsafeOkHttpClient(): OkHttpClient {
            try {
                // Создаём TrustManager, который игнорирует проверки сертификатов
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
                })

                // Создаём SSLContext, использующий наш TrustManager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())

                // Создаём SSLSocketFactory с этим SSLContext
                val sslSocketFactory = sslContext.socketFactory

                // Возвращаем клиент OkHttp, отключив проверку имени хоста
                return OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true } // Отключаем проверку имени хоста
                    .build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}