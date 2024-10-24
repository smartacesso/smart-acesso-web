package com.protreino.luxandapp.rest;

import com.protreino.services.to.SendReceiveTrackerParams;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface FacialRest {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("facial/requestBackupFileTracker")
    Call<SendReceiveTrackerParams> requestBackupFileTracker(@Query("client") String client);


}
