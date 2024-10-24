package br.com.smartacesso.pedestre.model.network;

import br.com.smartacesso.pedestre.model.to.ResponseService;
import br.com.smartacesso.pedestre.model.to.SendService;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PedestreService {


    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("pedestre/get")
    Call<ResponseService> get(@Query("token") String token,
                              @Query("lastSync") Long lastSync);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("pedestre/registra/facial")
    Call<ResponseService> registraFacial(@Body SendService dados);


}
