package com.protreino.luxandapp.async;

import android.content.Context;
import android.os.AsyncTask;

import com.protreino.luxandapp.R;
import com.protreino.luxandapp.interfaces.TaskCallback;
import com.protreino.luxandapp.main.App;
import com.protreino.luxandapp.model.ResultWrapper;
import com.protreino.luxandapp.rest.FacialRest;
import com.protreino.luxandapp.rest.LoginRest;
import com.protreino.luxandapp.service.SyncronizeService;
import com.protreino.luxandapp.ui.login.LoginActivity;
import com.protreino.luxandapp.util.EncryptionUtils;
import com.protreino.luxandapp.util.NetworkCall;
import com.protreino.luxandapp.util.SharedPreferencesUtil;
import com.protreino.services.enumeration.TaskType;
import com.protreino.services.enumeration.TcpMessageType;
import com.protreino.services.to.InternalLoginResponse;
import com.protreino.services.to.ResponseService;
import com.protreino.services.to.SendReceiveTrackerParams;
import com.protreino.services.to.SimpleUser;
import com.protreino.services.to.TcpMessageTO;
import com.protreino.services.to.login.Usuario;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Annotation;
import java.net.InetSocketAddress;
import java.net.Socket;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;

public class LoginTask extends AsyncTask<Void, Integer, ResultWrapper<InternalLoginResponse>> {

    private TaskCallback callback;
    private Socket socket;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private String username;
    private String password;
    private int timeout = 4000;
    private String organizacao;

    public LoginTask(TaskCallback callback, String username, String password, String organizacao){
        super();
        this.callback    = callback;
        this.username    = username;
        this.password    = password;
        this.organizacao = organizacao;
    }

    @Override
    protected ResultWrapper<InternalLoginResponse> doInBackground(Void... params) {
        try {

            if (LoginActivity.LOGIN_WEB && organizacao.trim().isEmpty())
                return new ResultWrapper<>("Organização é obrigatório", TaskType.LOGIN);

            if (username.trim().isEmpty())
                return new ResultWrapper<>("E-mail é obrigatório", TaskType.LOGIN);

            if (password.trim().isEmpty())
                return new ResultWrapper<>("Senha é obrigatória", TaskType.LOGIN);

            String ip = App.getPreferenceAsString(R.string.pref_desktop_ip);
            int port = App.getPreferenceAsInteger(R.string.pref_desktop_port);

            if (ip.isEmpty() || port <= 0)
                return new ResultWrapper<>("Endereço do servidor não configurado", TaskType.LOGIN);

            String errorMessage = "";
            SimpleUser user = null;

            if(LoginActivity.LOGIN_WEB) {
                try {
                    LoginRest loginRest = NetworkCall.getDefaultConfig().create(LoginRest.class);
                    Call<ResponseService> response = loginRest.doLogin(organizacao, username, password);
                    if (response != null) {
                        Response<ResponseService> rServer = response.execute();
                        ResponseService responseService = null;
                        if (rServer.errorBody() != null) {
                            Converter<ResponseBody, ResponseService> errorConverter =
                                    NetworkCall.getDefaultConfig().responseBodyConverter(ResponseService.class, new Annotation[0]);
                            responseService = errorConverter.convert(rServer.errorBody());
                        } else {
                            responseService = rServer.body();
                        }

                        if ("OK".equals(responseService.getStatus())) {
                            Usuario usuario = NetworkCall.getGson().fromJson(responseService.getObject().toString(), Usuario.class);
                            user = new SimpleUser();
                            user.setId(usuario.getId());
                            user.setName(usuario.getNome());
                            user.setIdClient(usuario.getCliente().getId().toString());
                        } else {
                            throw new Exception(responseService.getMessage());
                        }
                    }
                }catch (Exception e){
                    errorMessage = e.getMessage();
                }
            }else{

                TcpMessageTO messageToServer = new TcpMessageTO(TcpMessageType.INTERNAL_LOGIN_REQUEST);
                messageToServer.getParans().put("username", username);
                messageToServer.getParans().put("password", EncryptionUtils.encrypt(password));

                System.out.println("doInBackground: Tentando conectar em " + ip + ":" + port);

                socket = new Socket();
                socket.setTcpNoDelay(true);
                socket.connect(new InetSocketAddress(ip, port), timeout);
                socket.setSoTimeout(timeout);

                ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                out.writeObject(messageToServer);
                out.flush();

                ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                TcpMessageTO serverResponse = (TcpMessageTO) in.readObject();

                if (serverResponse == null)
                    return new ResultWrapper<>("Resposta nula do servidor", TaskType.LOGIN);

                InternalLoginResponse response = (InternalLoginResponse) serverResponse.getParans().get("response");
                errorMessage = response.getErrorMessage();
                user = response.getUser();

            }

            if(errorMessage == null
                    || "".equals(errorMessage)){

                if ("Aplicativo".equals(App.getPreferenceAsString(R.string.pref_tipo_verificacao))){
                    SharedPreferencesUtil
                            .setSharePreference(SharedPreferencesUtil.SharedPreferencesKey.LOGGED_USER,
                                    NetworkCall.getGson().toJson(user), (Context) callback);
                    //inicia serviços, caso não estejam funcionando
                    SyncronizeService.sincroniza((Context) callback, false);
                }

            }

            //cria resposta para não influenciar logins
            InternalLoginResponse r = new InternalLoginResponse(errorMessage == null || "".equals(errorMessage) ? true : false);
            r.setErrorMessage(errorMessage);
            r.setUser(user);

            return new ResultWrapper<>(r, TaskType.LOGIN);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeAll();
        }
        return new ResultWrapper<>("Falhar ao entrar", TaskType.LOGIN);
    }

    @Override
    protected void onPostExecute(ResultWrapper<InternalLoginResponse> resultWrapper) {
        super.onPostExecute(resultWrapper);
        callback.onTaskCompleted(resultWrapper);
    }

    private void closeAll(){
        try {
            if (socket != null)
                socket.close();
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
