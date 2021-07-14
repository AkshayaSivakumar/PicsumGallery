package com.experiment.android.picsumgallery.data.remote

import com.experiment.android.picsumgallery.model.PicsumResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PicsumApi {
    @GET("v2/list")
    suspend fun getPicsumResponse(
        @Query("page") page: Int,
    ): List<PicsumResponse>
}