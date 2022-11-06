package com.example.slpn.firebase

import com.example.inzynierka.firebase.PushNotification
import com.example.slpn.constants.Constants.Companion.BASE_URL
import com.example.slpn.constants.Constants.Companion.CONTENT_TYPE
import com.example.slpn.constants.Constants.Companion.FB_TOKEN
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
//Wysyłanie notyfikacji
interface NotificationInterface {

    @Headers("Authorization: key=$FB_TOKEN", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(@Body notificationData: PushNotification): Response<ResponseBody>
}

class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationInterface::class.java)
        }
    }
}