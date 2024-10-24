package br.com.smartacesso.pedestre.activity;

import android.content.Intent;
import android.os.Bundle;

import br.com.smartacesso.pedestre.service.StartTasksService;
import br.com.smartacesso.pedestre.utils.SharedPreferencesUtil;


public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //verifica usuário logado
        Intent intent = null;
        if (SharedPreferencesUtil.getSharePreference(
                SharedPreferencesUtil.SharedPreferencesKey.LOGGED_USER, this) != null) {

            //verifica se existe treino executando
                sleep();
                intent = new Intent(this, MainActivity_.class);

            //inicia serviços, caso não estejam funcionando
            StartTasksService.inicializa(this);

        } else {
            sleep();

            //verifica se vai apresentar uma tela de novo usuario
            intent = new Intent(this, LoginActivity_.class);

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
