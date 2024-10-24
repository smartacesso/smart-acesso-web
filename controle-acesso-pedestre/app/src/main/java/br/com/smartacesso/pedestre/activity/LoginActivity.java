package br.com.smartacesso.pedestre.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.smartacesso.pedestre.R;
import br.com.smartacesso.pedestre.model.bo.LoginBO;
import br.com.smartacesso.pedestre.model.entity.Pedestre;
import br.com.smartacesso.pedestre.model.network.NetworkCall;
import br.com.smartacesso.pedestre.service.StartTasksService;
import br.com.smartacesso.pedestre.service.SyncronizeService;
import br.com.smartacesso.pedestre.utils.MaskEditUtil;
import br.com.smartacesso.pedestre.utils.SharedPreferencesUtil;

@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewById
    public EditText editTextOrganizacao;
    @ViewById
    public EditText editTextLogin;
    @ViewById
    public EditText editTextSenha;
    @ViewById
    public ImageView imageView;


    /*Após criar a Activity, este método deverá ser chamado a fim de adicionar particularidades à
     tela sem a necessidade de usar o onCreate
      */
    @AfterViews
    public void init() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        editTextSenha.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextSenha.getWindowToken(), 0);
                conecta();
                return true;
            }
        });

    }

    @Click
    public void conecta() {

        //valida dados
        boolean valido = true;
        if ("".equals(editTextOrganizacao.getText().toString())) {
            editTextOrganizacao.setError(getString(R.string.error_informa_organizacao));
            valido = false;
        }

        if ("".equals(editTextLogin.getText().toString())) {
            editTextLogin.setError(getString(R.string.error_informa_login));
            valido = false;
        }

        if ("".equals(editTextSenha.getText().toString())) {
            editTextSenha.setError(getString(R.string.error_informe_senha));
            valido = false;
        }

        if (valido) {

            ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    editTextOrganizacao.getWindowToken(), 0);

            realizaLogin();
        }
    }

    @Click
    public void configurarServidor(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog confirmar = builder.create();

        LayoutInflater infalInflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = infalInflater.inflate(R.layout.dialog_config_servidor, null);

        final EditText endereco = (EditText)v.findViewById(R.id.endereco);
        endereco.setHint(NetworkCall.getURL(LoginActivity.this));

        Button cancelar = (Button)v.findViewById(R.id.cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmar.dismiss();

            }
        });
        Button confirma = (Button)v.findViewById(R.id.confirmar);
        confirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferencesUtil
                        .setSharePreference(SharedPreferencesUtil.SharedPreferencesKey.URL_SERVIDOR,
                                endereco.getText().toString(), LoginActivity.this);

                confirmar.dismiss();
            }
        });

        confirmar.setTitle(getString(R.string.configurar_servidor));
        confirmar.setView(v);
        confirmar.show();

    }

    public void forgotPass() {
        //valida dados
        boolean valido = true;
        if ("".equals(editTextOrganizacao.getText().toString())) {
            editTextOrganizacao.setError(getString(R.string.error_informa_organizacao));
            valido = false;
        }

        if ("".equals(editTextLogin.getText().toString())) {
            editTextLogin.setError(getString(R.string.error_informa_login));
            valido = false;
        }

        if (valido) {
            openConfirmDialog(getString(R.string.msg_alteracao_senha),
                    this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            iniciaEsqueceuSuaSenha();
                        }
                    }, null);
        }
    }

    @Background
    public void iniciaEsqueceuSuaSenha() {
        try {
            String response = new LoginBO(LoginActivity.this)
                    .alterPasswd(editTextOrganizacao.getText().toString(), editTextLogin.getText().toString());

            if ("OK".equalsIgnoreCase(response)) {
                exibeMsg(getString(R.string.msg_senha_enviada_email));
            } else {
                exibeMsg(getString(R.string.msg_senha_enviada_email));
            }

        } catch (Exception e) {
            Log.e("Erro", e.getMessage());
        }
    }

    @UiThread
    public void exibeMsg(String msg) {
        openNeutroDialog(msg, getString(R.string.msg_atencao), this);
    }

    @Background
    public void realizaLogin() {

        createProcessDialog(getString(R.string.msg_aguarde_procuro_dados));

        try {

            Pedestre user = new LoginBO(this)
                    .doLogin(editTextOrganizacao.getText().toString(),
                             editTextLogin.getText().toString(),
                             editTextSenha.getText().toString());

            if (user != null) {

                //sincronizacão inicial
                SyncronizeService.sincroniza(this, false);

                loginOk();


            } else {
                loginErro(getString(R.string.msgs_nao_encontrado));
            }

        } catch (Exception e) {
            String msg = e.getMessage();
            if(msg.equals("msgs.account.usuario.senha.invalida"))
                msg = getString(R.string.msgs_account_usuario_senha_invalida);
            else if(msg.equals("msgs.account.usuario.nao.encontrado"))
                msg = getString(R.string.msgs_account_usuario_nao_encontrado);
            else if(msg.equals("msgs.account.unidade.nao.encontrada"))
                msg = getString(R.string.msgs_account_unidade_nao_encontrada);
            loginErro(msg);
            e.printStackTrace();
        }

    }

    @UiThread
    public void loginErro(String msg) {
        openNeutroDialog(msg, getString(R.string.titulo_ops), this);
        destroyProcessDialog();
    }

    @UiThread
    public void loginOk() {

        //adiciona datas de sincronização como datas
        //a partir do login
        Date uCriacao = new Date();
        SimpleDateFormat sdfSync = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        for (String syncService : SyncronizeService.SYNC_SERVICES) {
            SharedPreferencesUtil
                    .setSharePreference(syncService,
                            sdfSync.format(uCriacao), this);
        }

        //inicia serviços, caso não estejam funcionando
        StartTasksService.inicializa(this);

        //envia para tela principal
        destroyProcessDialog();
        Intent intent = new Intent(this, MainActivity_.class);
        startActivity(intent);
        finish();
    }


}
