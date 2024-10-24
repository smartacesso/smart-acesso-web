package com.protreino.luxandapp.rest;

import com.protreino.services.to.ResponseService;
import com.protreino.services.to.SendReceiveTrackerParams;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface LoginRest {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("login/do")
    Call<ResponseService> doLogin(@Query("unidadeName") String unidadeName, @Query("loginName") String loginName, @Query("passwd")String passwd);

}
