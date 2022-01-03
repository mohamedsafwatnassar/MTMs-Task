package com.example.mtmstask.network

import com.example.mtmstask.map.model.PlacesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WebServices {

    @GET("place/queryautocomplete/json")
    suspend fun getPlaces(
        @Query("input") placeText: String,
        @Query("key") key: String,
    ): Response<PlacesResponse>
}