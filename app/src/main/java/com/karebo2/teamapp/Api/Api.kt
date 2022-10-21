package com.karebo2.teamapp.Api


import com.karebo2.teamapp.dataclass.CodeListDataClass
import com.karebo2.teamapp.dataclass.CounterDataClass
import com.karebo2.teamapp.dataclass.MeterDataModel
import com.karebo2.teamapp.dataclass.meterData.meterauditDataModel
import io.reactivex.Observable
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.http.*


interface Api {


    @GET("CodesList2")
    fun codelist(
    ): Call<CodeListDataClass>


    @GET("Test")
    fun test(
        @Query("userkey") userkey: String?,
    ): Call<ResponseBody>

    @GET("Counters")
    fun count(
        @Query("pin") pin: String?,
    ): Call<CounterDataClass>



    @POST("SHEQ")
    fun addToolbox(
        @Query("userkey") userkey: String?,
        @Query("app") app: String?,
        @Body body: Any = Object()

        ): Call<ResponseBody>


    @GET("ResendPin")
    fun resendPin(
        @Query("userkey") userkey: String?,
        @Query("app") app: String?,
    ): Call<ResponseBody>



    @GET("Inbox")
    fun checkOtp(
        @Query("pin") pin: String?,

    ): Call<List<meterauditDataModel>>



    @GET("MeterList")
    fun MeterList(
        @Query("pin") pin: Int?,
        @Query("reset") reset: Boolean?,
    ): Call<ResponseBody>


    @GET("Data")
    fun meterData(
        @Query("project") project: String?,
        @Query("serial") serial: String?,
        @Query("code") code : String?,
    ): Call<MeterDataModel>


    @POST("Save")
    fun addPhoto64(
        @Query("id") id: String?,
        @Body body: String
    ): Observable<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("Submit")
    fun submitMeter(
        @Body body: String
    ): Observable<ResponseBody>


    @Headers("Content-Type: application/json")
    @POST("Submit")
    fun submitMeter2(
        @Body body: String
    ): Call<ResponseBody>

}