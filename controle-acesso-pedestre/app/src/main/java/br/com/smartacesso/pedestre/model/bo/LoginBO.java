package br.com.smartacesso.pedestre.model.bo;

import android.content.Context;

import java.lang.annotation.Annotation;

import br.com.smartacesso.pedestre.model.entity.Pedestre;
import br.com.smartacesso.pedestre.model.network.LoginService;
import br.com.smartacesso.pedestre.model.network.NetworkCall;
import br.com.smartacesso.pedestre.model.to.ResponseService;
import br.com.smartacesso.pedestre.utils.SharedPreferencesUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;

public class LoginBO extends AppBO {
    public LoginBO(Context context) {
        super(context);
    }

    public String alterPasswd(String toString, String toString1) {

        return null;
    }

    public Pedestre doLogin(String org, String login, String senha) throws Exception{
        LoginService service = NetworkCall
                .getDefaultConfig(context).create(LoginService.class);

        Call<ResponseService> response = service
                .doLogin(org, login, senha);

        if (response != null) {
            Response<ResponseService> rServer = response.execute();
            ResponseService responseService = null;
            if (rServer.errorBody() != null) {
                Converter<ResponseBody, ResponseService> errorConverter =
                        NetworkCall.getDefaultConfig(context).responseBodyConverter(ResponseService.class, new Annotation[0]);
                responseService = errorConverter.convert(rServer.errorBody());
            } else {
                responseService = rServer.body();
            }

            if ("OK".equals(responseService.getStatus())) {

                Pedestre user = NetworkCall.getGson().fromJson(responseService.getObject().toString(), Pedestre.class);

                SharedPreferencesUtil
                        .setSharePreference(SharedPreferencesUtil.SharedPreferencesKey.LOGGED_USER,
                                responseService.getObject().toString(), context);
                return user;

            } else {
                throw new Exception(responseService.getMessage());
            }
        }
        return null;
    }
}
