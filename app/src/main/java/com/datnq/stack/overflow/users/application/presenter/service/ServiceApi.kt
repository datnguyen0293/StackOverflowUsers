package com.datnq.stack.overflow.users.application.presenter.service

import com.datnq.stack.overflow.users.application.model.response.ReputationResponse
import com.datnq.stack.overflow.users.application.model.response.UsersResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
interface ServiceApi {
    @GET("users")
    fun getUsers(
        @Query("page") page: Int,
        @Query("pagesize") pageSize: Int,
        @Query("site") site: String?,
        @Query("sort") sort: String?,
        @Query("order") order: String?,
        @Query("key") key: String?
    ): Observable<UsersResponse?>

    @GET("users/{userId}/reputation-history")
    fun getReputations(
        @Path("userId") userId: Long,
        @Query("page") page: Int,
        @Query("pagesize") pageSize: Int,
        @Query("site") site: String?,
        @Query("sort") sort: String?,
        @Query("order") order: String?,
        @Query("key") key: String?
    ): Observable<ReputationResponse?>
}