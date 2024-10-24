package br.com.smartacesso.pedestre.model.network;

import br.com.smartacesso.pedestre.model.to.ResponseService;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by gustavo on 06/06/17.
 */

public interface LoginService {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("pedestre/login")
    Call<ResponseService> doLogin(@Query("unidadeName") String organizacao,
                                  @Query("login") String login,
                                  @Query("passwd") String senha);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("pedestre/alterPasswd")
    Call<ResponseService> alterPasswd(@Query("organizacao") String organizacao, @Query("login") String login);


    @GET("pedestre/action")
    Call<String> isWorking();


}
