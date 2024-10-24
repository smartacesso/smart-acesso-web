package com.protreino.luxandapp.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.protreino.luxandapp.service.LoginService;
import com.protreino.luxandapp.service.StartTasksService;
import com.protreino.luxandapp.ui.home.HomeActivity;
import com.protreino.luxandapp.util.NetworkCall;
import com.protreino.luxandapp.util.SharedPreferencesUtil;
import com.protreino.services.to.SimpleUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //verifica usuário logado
        Intent intent = null;
        String loggedUser = SharedPreferencesUtil.getSharePreference(
                SharedPreferencesUtil.SharedPreferencesKey.LOGGED_USER, this);
        if (loggedUser != null) {

            //verifica se existe treino executando
            sleep();
            intent = new Intent(this, HomeActivity.class);

            LoginService.getInstance().setLoggedInUser(NetworkCall.getGson().fromJson(loggedUser, SimpleUser.class));

            //inicia serviços, caso não estejam funcionando
            StartTasksService.inicializa(this);

        } else {
            sleep();

            //verifica se vai apresentar uma tela de novo usuario
            intent = new Intent(this, LoginActivity.class);

        }

        //inicia tela
        startActivity(intent);
        finish();
    }

    private void sleep() {
        try {

            Thread.sleep(3000);

        } catch (Exception e) {

        }
    }
}
