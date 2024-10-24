package com.protreino.luxandapp.ui.login;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.protreino.luxandapp.R;
import com.protreino.luxandapp.interfaces.TaskCallback;
import com.protreino.luxandapp.async.LoginTask;
import com.protreino.luxandapp.main.App;
import com.protreino.luxandapp.model.ResultWrapper;
import com.protreino.luxandapp.service.LoginService;
import com.protreino.luxandapp.service.StartTasksService;
import com.protreino.luxandapp.service.SyncronizeService;
import com.protreino.luxandapp.ui.home.HomeActivity;
import com.protreino.luxandapp.ui.settings.SettingsActivity;
import com.protreino.luxandapp.util.NetworkCall;
import com.protreino.luxandapp.util.SharedPreferencesUtil;
import com.protreino.services.enumeration.TaskType;
import com.protreino.services.to.InternalLoginResponse;
import com.protreino.services.to.SimpleUser;

import static com.protreino.luxandapp.ui.home.HomeActivity.CAMERA_PERMISSION_REQUEST_CODE;
import static com.protreino.luxandapp.ui.home.HomeActivity.REQUEST_WRITE_LOGS;

public class LoginActivity extends AppCompatActivity implements TaskCallback {

    private MutableLiveData<ResultWrapper<?>> loginResult = new MutableLiveData<>();
    public static boolean LOGIN_WEB = false;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText organizacaoEditText;
    private FrameLayout progressBarHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        if("Aplicativo".equals(App.getPreferenceAsString(R.string.pref_tipo_verificacao)))
            LOGIN_WEB = true;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(R.drawable.logo_small);
        }

        usernameEditText    = findViewById(R.id.username);
        passwordEditText    = findViewById(R.id.password);
        organizacaoEditText = findViewById(R.id.organizacao);
        if(!LOGIN_WEB)
            organizacaoEditText.setVisibility(View.GONE);
        Button loginButton  = findViewById(R.id.btn_login);
        progressBarHolder   = findViewById(R.id.progress_bar_holder);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doLogin();
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        loginResult.observe(this, new Observer<ResultWrapper<?>>() {
            @Override
            public void onChanged(@Nullable ResultWrapper loginResultWrapper) {
                hideLoading();

                if (loginResultWrapper == null) {
                    return;
                }

                if (loginResultWrapper.hasError()) {
                    Toast.makeText(getApplicationContext(), loginResultWrapper.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
                else {
                    InternalLoginResponse loginResponse = (InternalLoginResponse) loginResultWrapper.getResult();

                    if (loginResponse.getSuccess()) {

                        SimpleUser loggedUser = loginResponse.getUser();
                        LoginService.getInstance().setLoggedInUser(loggedUser);

                        //deixa usuário logado e inicia servicos
                        try {

                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            finish();
                            startActivity(intent);

                            Toast.makeText(getApplicationContext(), loggedUser.getName() + " logado com sucesso!", Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    else {
                        Toast.makeText(getApplicationContext(), loginResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        requestPermission();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //usernameEditText.getText().clear();
        //passwordEditText.getText().clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_login) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivityForResult(intent, 500);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 500){
            if("Aplicativo".equals(App.getPreferenceAsString(R.string.pref_tipo_verificacao))) {
                LOGIN_WEB = true;
                organizacaoEditText.setVisibility(View.VISIBLE);
            }else{
                LOGIN_WEB = false;
                organizacaoEditText.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideLoading();
    }

    @Override
    public void onTaskCompleted(ResultWrapper<?> resultWrapper) {
        if (TaskType.LOGIN.equals(resultWrapper.getTaskType())) {
            loginResult.setValue(resultWrapper);
        }
    }

    private void doLogin(){
        showLoading();
        LoginTask loginTask = new LoginTask(LoginActivity.this,
                usernameEditText.getText().toString(), passwordEditText.getText().toString(), organizacaoEditText.getText().toString());
        loginTask.execute();
    }

    private void showLoading() {
        progressBarHolder.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideLoading() {
        progressBarHolder.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}