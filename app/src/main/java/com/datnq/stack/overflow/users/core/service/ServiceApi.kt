package com.datnq.stack.overflow.users.core.service

import com.datnq.stack.overflow.users.core.BaseResponse
import com.datnq.stack.overflow.users.core.Constants
import com.datnq.stack.overflow.users.hotel.model.weather.Ip
import com.datnq.stack.overflow.users.hotel.model.weather.Weather
import io.reactivex.Single
import retrofit2.http.*

interface ServiceApi {

    @POST("camera_producer/get")
    fun getCameraProviderList(@Query("token") token: String): Single<BaseResponse>

    @FormUrlEncoded @POST("get_lock_key")
    fun getLockKey(@Field(Constants.PARAM_HOME_ID) homeId: String, @Field("node_id") nodeId: String, @Field("token") token: String): Single<BaseResponse>

    @FormUrlEncoded @POST("add_lock_key")
    fun addLockKey(@Field("home_id") homeId: String, @Field("data") json: String, @Field("token") token: String): Single<BaseResponse>

    @FormUrlEncoded @POST("delete_lock_key")
    fun deleteLockKey(@Field("home_id") homeId: String, @Field("node_id") nodeId: String, @Field("index") index: String, @Field("token") token: String): Single<BaseResponse>

    @FormUrlEncoded @POST("get_context_content")
    fun getContextContent(@Field("home_id") homeId: String, @Field("context_id") contextId: String, @Field("token") token: String): Single<BaseResponse>

    @POST("49KIexouj1AoiJzHR0Ho")
    fun getWanIp(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("login")
    fun login(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("change_password")
    fun changePassword(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("hl6XVvigpJlkuVQLIT5e")
    fun backupData(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("UtvoGyzjuiUH9pIPCphh")
    fun synchronizeData(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("get_author")
    fun getClientAccounts(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("get_author_user")
    fun getClientAccountStatus(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("CcoXPmxvIjBsvauaWrFe")
    fun registerMobileId(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("add_context")
    fun addContext(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("delete_context")
    fun deleteContext(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("authority_create")
    fun createNewAuthority(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("authority_delete")
    fun deleteAuthority(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("client_approve")
    fun approveClientRequest(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("delete_author_user")
    fun deleteClientRequest(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("authority_get")
    fun clientRequestUpdateDatabase(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("client_request_access")
    fun clientRequestApproved(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("QKybe95eR2lSuONh92Ix")
    fun loadRoomImages(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("yALN2LmfXKBcQeVCBjbT")
    fun getLogs(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("sVBCF0LzpW0VhDPTnTMk")
    fun sendLog(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("1GVfIf6B5R4wzJmPFXDv")
    fun smartLockUpdateAdd(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("ig6rzMxk2T0H37mbgGxm")
    fun smartLockUpdateKey(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("userdevice/get")
    fun getUserDevices(@Body params: HashMap<String, String>): Single<BaseResponse>

    @POST("{id}/?")
    fun getWeather(@Path("id") geo: String, @Query("token") token: String): Single<Weather>

    @GET("ip")
    fun getCurrentIp(@Query("token") token: String): Single<Ip>
}