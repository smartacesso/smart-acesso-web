package com.protreino.luxandapp.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.protreino.luxandapp.R;
import com.protreino.luxandapp.main.App;
import com.protreino.luxandapp.ui.BaseActivity;
import com.protreino.luxandapp.ui.components.DialogoSelecaoListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends BaseActivity {

    private EditText recognitionServerEditText;
    private EditText desktopEditText;
    private Button saveButton;
    private EditText tipoVerificacao;
    private TextInputLayout textInputVerificacao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        recognitionServerEditText = findViewById(R.id.recognition_server_url);
        desktopEditText = findViewById(R.id.desktop_url);
        saveButton = findViewById(R.id.btn_save);
        tipoVerificacao = (EditText) findViewById(R.id.tipo_verificacao);
        textInputVerificacao = (TextInputLayout) findViewById(R.id.textInputVerificacao);

        ArrayList<String> tipos = new ArrayList<String>();
        tipos.add("Servidor");
        tipos.add("Aplicativo");

        tipoVerificacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoCampoSelecao(getString(R.string.selecione), tipos, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tipoVerificacao.setText(tipos.get(i));
                        textInputVerificacao.setVisibility("Aplicativo".equals(tipos.get(i)) ? View.GONE : View.VISIBLE);
                    }
                });
            }
        });



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        String recognitionServerIp = App.getPreferenceAsString(R.string.pref_recognition_server_ip);
        Integer recognitionServerPort = App.getPreferenceAsInteger(R.string.pref_recognition_server_port);
        String desktopIp = App.getPreferenceAsString(R.string.pref_desktop_ip);
        Integer desktopPort = App.getPreferenceAsInteger(R.string.pref_desktop_port);
        String tipoVerificacaoPref = App.getPreferenceAsString(R.string.pref_tipo_verificacao);

        if (!recognitionServerIp.isEmpty() && recognitionServerPort > 0)
            recognitionServerEditText.setText(recognitionServerIp + ":" + recognitionServerPort);

        if (!desktopIp.isEmpty() && desktopPort > 0)
            desktopEditText.setText(desktopIp + ":" + desktopPort);

        if (!tipoVerificacaoPref.isEmpty()) {
            tipoVerificacao.setText(tipoVerificacaoPref);
            textInputVerificacao.setVisibility("Aplicativo".equals(tipoVerificacaoPref) ? View.GONE : View.VISIBLE);

        }

    }

    private void saveSettings() {
        try {

            //verifica o tipo de verificacao para validar servidor
            String errorMessage = "";
            if("Servidor".equals(tipoVerificacao.getText().toString())) {

                errorMessage = validateRecognitionServerUrl();
                if (!errorMessage.isEmpty()) {
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    return;
                }

                String recognitionServerUrl = recognitionServerEditText.getText().toString().trim();
                String[] parts = recognitionServerUrl.split(":");
                String recognitionServerIp = parts[0];
                Integer recognitionServerPort = Integer.valueOf(parts[1]);

                App.savePreferenceAsString(R.string.pref_recognition_server_ip, recognitionServerIp);
                App.savePreferenceAsInteger(R.string.pref_recognition_server_port, recognitionServerPort);

            }

            errorMessage = validateDesktopUrl();
            if (!errorMessage.isEmpty()) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                return;
            }

            String desktopUrl = desktopEditText.getText().toString().trim();
            String[] parts = desktopUrl.split(":");
            String desktopIp = parts[0];
            Integer desktopPort = Integer.valueOf(parts[1]);


            App.savePreferenceAsString(R.string.pref_desktop_ip, desktopIp);
            App.savePreferenceAsInteger(R.string.pref_desktop_port, desktopPort);
            App.savePreferenceAsString(R.string.pref_tipo_verificacao, tipoVerificacao.getText().toString());

            Toast.makeText(getApplicationContext(), "Configurações salvas!", Toast.LENGTH_LONG).show();

            finish();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String validateRecognitionServerUrl() {
        String recognitionServerUrl = recognitionServerEditText.getText().toString().trim();

        if (recognitionServerUrl.isEmpty())
            return "Endereço do servidor de reconhecimento é obrigatório";

        if (recognitionServerUrl.contains("http://") || recognitionServerUrl.contains("https://"))
            return "Endereço do servidor de reconhecimento inválido";

        String[] partes = recognitionServerUrl.split(":");

        if (partes.length != 2)
            return "Endereço do servidor de reconhecimento inválido";

        String recognitionServerIp = partes[0];
        Integer recognitionServerPort = Integer.valueOf(partes[1]);

        if (recognitionServerIp.isEmpty() || recognitionServerPort <= 0)
            return "Endereço do servidor de reconhecimento inválido";

        return "";
    }

    private String validateDesktopUrl() {
        String desktopUrl = desktopEditText.getText().toString().trim();

        if (desktopUrl.isEmpty())
            return "Endereço do aplicativo desktop é obrigatório";

        if (desktopUrl.contains("http://") || desktopUrl.contains("https://"))
            return "Endereço do aplicativo desktop inválido";

        String[] partes = desktopUrl.split(":");

        if (partes.length != 2)
            return "Endereço do aplicativo desktop inválido";

        String desktopIp = partes[0];
        Integer desktopPort = Integer.valueOf(partes[1]);

        if (desktopIp.isEmpty() || desktopPort <= 0)
            return "Endereço do aplicativo desktop inválido";

        return "";
    }

}
