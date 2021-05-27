package com.example.coco

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


public interface MessageService {
    @POST("/dev/api/dialogue")
    suspend fun sendMessage(@Body requestBody: RequestBody): Response<ResponseBody>
}
