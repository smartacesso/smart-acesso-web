package com.protreino.luxandapp.ui.home;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.luxand.FSDK;
import com.protreino.luxandapp.R;
import com.protreino.luxandapp.async.GetConfigurationsTask;
import com.protreino.luxandapp.interfaces.TaskCallback;
import com.protreino.luxandapp.main.App;
import com.protreino.luxandapp.model.ResultWrapper;
import com.protreino.luxandapp.util.AppConstants;
import com.protreino.services.to.SimpleDevice;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel implements TaskCallback {

    public static boolean isInitialized = false;
    public boolean hasError = false;
    public String errorMessage = "";
    public List<SimpleDevice> deviceList;

    private HomeActivity homeActivity;
    private String serverRecognizerUrl;

    public static FSDK.HTracker tracker = new FSDK.HTracker();

    public HomeViewModel() {
        super();

        String ip = App.getPreferenceAsString(R.string.pref_recognition_server_ip);
        Integer port = App.getPreferenceAsInteger(R.string.pref_recognition_server_port);
        serverRecognizerUrl = "http://" + ip + ":" + port.toString();

        initializeSDK();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        finalizeSDK();
    }

    public void initializeSDK() {
        try {
            String key = App.getProperty(R.string.prop_luxand_key);

            int ret = FSDK.ActivateLibrary(key);
            if (ret != FSDK.FSDKE_OK)
                throw new Exception("Não foi possível ativar a Luxand FaceSDK. Verifique a chave de licença. " + String.valueOf(ret));

            ret = FSDK.Initialize();
            if (ret != FSDK.FSDKE_OK)
                throw new Exception("Não foi possível inicializar a Luxand FaceSDK. " + String.valueOf(ret));

            isInitialized = true;

            if("Aplicativo".equals(App.getPreferenceAsString(R.string.pref_tipo_verificacao))){

                //cria configuracaoes padrao
                configureLocalSDK();

            }else{
                // Dispara uma task para buscar as configurações atualizadas
                GetConfigurationsTask getConfigurationsTask = new GetConfigurationsTask(HomeViewModel.this, serverRecognizerUrl);
                getConfigurationsTask.execute();

                // E enquanto isso utiliza as configuracoes locais
                configureSDK();
            }

        }
        catch(Exception e) {
            e.printStackTrace();
            hasError = true;
            errorMessage = e.getMessage();
        }
    }

    private void configureLocalSDK() throws Exception {

        int ret = FSDK.ActivateLibrary(AppConstants.LUXAND_KEY);
        if (ret != FSDK.FSDKE_OK)
            throw new Exception("Não foi possível ativar a Luxand FaceSDK: " + getResultDescription(ret));

        ret = FSDK.Initialize();
        if (ret != FSDK.FSDKE_OK)
            throw new Exception("Não foi possível inicializar a Luxand FaceSDK: " + getResultDescription(ret));

        // verifica se há algum tracker salvo localmente
        // image naming and path  to include sd card  appending name you choose for file
        String mPath = Environment.getExternalStorageDirectory().toString();

        tracker = new FSDK.HTracker();
        ret = FSDK.LoadTrackerMemoryFromFile(tracker, mPath + AppConstants.TRACKER_FILE);
        if (ret != FSDK.FSDKE_OK)
              throw new Exception("Não foi possível criar o Tracker: " + getResultDescription(ret));

        Map<String, String> configurationsMap = getConfigurationsMap();

        FSDK.SetFaceDetectionParameters(Boolean.parseBoolean(configurationsMap.get("HandleArbitraryRotations")),
                Boolean.parseBoolean(configurationsMap.get("DetermineFaceRotationAngle")),
                Integer.parseInt(configurationsMap.get("InternalResizeWidth")));

        FSDK.SetFaceDetectionThreshold(Integer.parseInt(configurationsMap.get("FaceDetectionThreshold")));


    }

    public void finalizeSDK() {
        if (isInitialized) {
            FSDK.Finalize();
            isInitialized = false;
        }
    }

    @Override
    public void onTaskCompleted(ResultWrapper<?> resultWrapper) {
        switch (resultWrapper.getTaskType()) {
            case GET_TICKET_GATE:
                homeActivity.hideLoading();

                if (resultWrapper == null || resultWrapper.hasError()) {
                    homeActivity.showAlert("Não foi possível obter lista de catracas.", new Runnable() {
                        @Override
                        public void run() {
                            homeActivity.resumePreview();
                        }
                    });
                    return;
                }

                deviceList = (List<SimpleDevice>) resultWrapper.getResult();

                homeActivity.showTicketGateDialog();
                break;

            case GET_CONFIGURATIONS:
                if (resultWrapper != null && !resultWrapper.hasError()) {
                    App.savePreferenceAsObject(R.string.pref_sdk_configurations, (Map<String, String>) resultWrapper.getResult());
                    configureSDK();
                }
                break;
        }
    }

    private void configureSDK() {
        Map<String, String> configurationsMap = getConfigurationsMap();

        FSDK.SetFaceDetectionParameters(Boolean.parseBoolean(configurationsMap.get("HandleArbitraryRotations")),
                Boolean.parseBoolean(configurationsMap.get("DetermineFaceRotationAngle")),
                Integer.parseInt(configurationsMap.get("InternalResizeWidth")));

        FSDK.SetFaceDetectionThreshold(Integer.parseInt(configurationsMap.get("FaceDetectionThreshold")));
    }

    private Map<String, String> getConfigurationsMap() {
        Map<String, String> configurationsMap = App.getPreferenceAsMap(R.string.pref_sdk_configurations);

        if (configurationsMap == null) {
            configurationsMap = new HashMap<String, String>();
            configurationsMap.put("HandleArbitraryRotations", "false");
            configurationsMap.put("DetermineFaceRotationAngle", "false");
            configurationsMap.put("InternalResizeWidth", "100");
            configurationsMap.put("FaceDetectionThreshold", "5");
            App.savePreferenceAsObject(R.string.pref_sdk_configurations, configurationsMap);
        }

        return configurationsMap;
    }

    public SimpleDevice getSelectedDevice() {
        return (SimpleDevice) App.getPreferenceAsObject(R.string.pref_selected_device, SimpleDevice.class);
    }

    public void setSelectedDevice(SimpleDevice device) {
        App.savePreferenceAsObject(R.string.pref_selected_device, device);
    }

    public String getSelectedDeviceName() {
        SimpleDevice device = getSelectedDevice();
        if (device != null)
            return device.getName();
        return "Nenhuma";
    }

    public void setHomeActivity(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @SuppressWarnings("serial")
    private static Map<Integer, String> resultDescriptions = new HashMap<Integer, String>() {
        {
            put(0, "OK"); // FSDKE_OK
            put(-1, "Falha"); // FSDKE_FAILED
            put(-2, "SDK não ativada"); // FSDKE_NOT_ACTIVATED
            put(-3, "Falta de memória"); // FSDKE_OUT_OF_MEMORY
            put(-4, "Argumentos inválidos"); // FSDKE_INVALID_ARGUMENT
            put(-5, "Erro de entrada de dados"); // FSDKE_IO_ERROR
            put(-6, "Imagem muito pequena"); // FSDKE_IMAGE_TOO_SMALL
            put(-7, "Rosto não encontrado"); // FSDKE_FACE_NOT_FOUND
            put(-8, "Tamanho de buffer insuficiente"); // FSDKE_INSUFFICIENT_BUFFER_SIZE
            put(-9, "Extensão de arquivo não suportado"); // FSDKE_UNSUPPORTED_IMAGE_EXTENSION
            put(-10, "Não foi possível abrir o arquivo"); // FSDKE_CANNOT_OPEN_FILE
            put(-11, "Não foi possível criar o arquivo"); // FSDKE_CANNOT_CREATE_FILE
            put(-12, "Formato de arquivo inválido"); // FSDKE_BAD_FILE_FORMAT
            put(-13, "Arquivo não encontrado"); // FSDKE_FILE_NOT_FOUND
            put(-14, "Conexão fechada"); // FSDKE_CONNECTION_CLOSED
            put(-15, "Conexão falhou"); // FSDKE_CONNECTION_FAILED
            put(-16, "Inicialização IP falhou"); // FSDKE_IP_INIT_FAILED
            put(-17, "Necessita ativação do servidor"); // FSDKE_NEED_SERVER_ACTIVATION
            put(-18, "ID não encontrado"); // FSDKE_ID_NOT_FOUND
            put(-19, "Atributo não detectado"); // FSDKE_ATTRIBUTE_NOT_DETECTED
            put(-20, "Limite de memória do tracker insuficiente"); // FSDKE_INSUFFICIENT_TRACKER_MEMORY_LIMIT
            put(-21, "Atributo desconhecido"); // FSDKE_UNKNOWN_ATTRIBUTE
            put(-22, "Versão de arquivo não suportada"); // FSDKE_UNSUPPORTED_FILE_VERSION
            put(-23, "Erro de sintaxe"); // FSDKE_SYNTAX_ERROR
            put(-24, "Parâmetro não encontrado"); // FSDKE_PARAMETER_NOT_FOUND
            put(-25, "Template inválido"); // FSDKE_INVALID_TEMPLATE
            put(-26, "Versão do template não suportada"); // FSDKE_UNSUPPORTED_TEMPLATE_VERSION
            put(-27, "Índice da câmera não existe"); // FSDKE_CAMERA_INDEX_DOES_NOT_EXIST
            put(-28, "Plataforma não licensiada"); // FSDKE_PLATFORM_NOT_LICENSED
            put(-29, "Muitos rostos na imagem"); // FSDKE_TOO_MANY_FACES_ON_IMAGE
            put(-30, "Exceção no servidor"); // FSDKE_SERVER_EXCEPTION
            put(-31, "Usuário já registrado"); // FSDKE_USER_ALREADY_REGISTERED
            put(-32, "Usuário registrado com outro nome"); // FSDKE_USER_REGISTERED_WITH_OTHER_NAME
            put(-33, "Reconhecimento falhou"); // FSDKE_RECOGNITION_FAILED
            put(-34, "Erro ao converter imagem para buffer"); // FSDKE_IMAGE_TO_BUFFER_ERROR
            put(-35, "Usuário não encontrado"); // FSDKE_USER_NOT_FOUND
            put(-36, "Usuário não está logado no servidor"); // FSDKE_USER_NOT_LOGGED_IN
            put(-37, "Exceção local"); // FSDKE_LOCAL_EXCEPTION
        }};

    public static String getResultDescription(int code) {
        return resultDescriptions.get(code);
    }



}
