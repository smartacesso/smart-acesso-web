package br.com.smartacesso.pedestre.model.bo;

import android.content.Context;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Date;

import br.com.smartacesso.pedestre.model.entity.CadastroExterno;
import br.com.smartacesso.pedestre.model.entity.Pedestre;
import br.com.smartacesso.pedestre.model.network.LoginService;
import br.com.smartacesso.pedestre.model.network.NetworkCall;
import br.com.smartacesso.pedestre.model.network.PedestreService;
import br.com.smartacesso.pedestre.model.to.ResponseService;
import br.com.smartacesso.pedestre.model.to.SendService;
import br.com.smartacesso.pedestre.utils.SharedPreferencesUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;

public class PedestreBO extends AppBO {

    public PedestreBO(Context context) {
        super(context);
    }

    public Pedestre get(String token, Date dLastSync) throws Exception {
        PedestreService service = NetworkCall
                .getDefaultConfig(context).create(PedestreService.class);

        Call<ResponseService> response = service
                .get(token, dLastSync.getTime());

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

    public ResponseService registraFacial(CadastroExterno cadastroExterno) throws  Exception{

        PedestreService service = NetworkCall
                .getDefaultConfig(context).create(PedestreService.class);

        SendService request = new SendService();
        request.setToken(SharedPreferencesUtil.getLoggedUser(context).getToken());
        request.setUser(SharedPreferencesUtil.getLoggedUser(context).getId());
        request.setObject(cadastroExterno);

        Call<ResponseService> response = service
                .registraFacial(request);

        if(response != null ) {
            Response<ResponseService> rServer = response.execute();
            ResponseService r = null;
            if (rServer.errorBody() != null) {
                Converter<ResponseBody, ResponseService> errorConverter =
                        NetworkCall.getDefaultConfig(context)
                                .responseBodyConverter(ResponseService.class, new Annotation[0]);
                r = errorConverter.convert(rServer.errorBody());
            } else {
                r = rServer.body();
            }

            if ("OK".equals(r.getStatus())) {
                return NetworkCall.getGson()
                        .fromJson(r.getObject().toString(), ResponseService.class);
            }else{
                throw new Exception(r.getMessage());
            }
        }

        return null;

    }
}
