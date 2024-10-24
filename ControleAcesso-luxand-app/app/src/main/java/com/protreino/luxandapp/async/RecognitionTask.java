package com.protreino.luxandapp.async;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.protreino.luxandapp.R;
import com.protreino.luxandapp.interfaces.TaskCallback;
import com.protreino.luxandapp.main.App;
import com.protreino.luxandapp.model.ResultWrapper;
import com.protreino.luxandapp.util.HttpConnection;
import com.protreino.services.enumeration.TaskType;
import com.protreino.services.to.DetectFaceResult;
import com.protreino.services.to.Face;
import com.protreino.services.to.FeedFrameRequest;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.luxand.FSDK;

import static com.protreino.luxandapp.ui.home.HomeViewModel.tracker;

public class RecognitionTask extends AsyncTask<Void, Integer, ResultWrapper<DetectFaceResult>> {

    private TaskCallback callback;
    private HttpConnection httpConnection;
    private String url;
    private byte[] image;
    private int imageWidth;
    private int imageHeight;

    public RecognitionTask(TaskCallback callback, String url, byte[] image, int imageWidth, int imageHeight){
        super();
        this.callback = callback;
        this.url = url;
        this.image = image;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    protected ResultWrapper<DetectFaceResult> doInBackground(Void... params) {
        try {
            List<String> imagesBase64 = new ArrayList<>();
            String stringBase64 = new String(Base64.encodeBase64(image));
            imagesBase64.add(stringBase64);

            FeedFrameRequest feedFrameRequest = new FeedFrameRequest();
            feedFrameRequest.setImageWidth(imageWidth);
            feedFrameRequest.setImageHeight(imageHeight);
            feedFrameRequest.setImagesBase64(imagesBase64);

            if("Aplicativo".equals(App.getPreferenceAsString(R.string.pref_tipo_verificacao)))
                return processaNoApp(feedFrameRequest);
            else
                return processaNoServidor(feedFrameRequest);

        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResultWrapper<>("Não foi possível acessar o servidor de reconhecimento.", TaskType.RECOGNIZE);
        }
        finally {
            if (httpConnection != null && httpConnection.getHttpURLConnection() != null)
                httpConnection.getHttpURLConnection().disconnect();
        }
    }

    private ResultWrapper<DetectFaceResult> processaNoServidor(FeedFrameRequest feedFrameRequest) throws IOException {
        httpConnection = new HttpConnection(url + "/recognition/recognize");
        int status = httpConnection.sendResponse(new ObjectMapper().writeValueAsString(feedFrameRequest));

        if (status == HttpURLConnection.HTTP_OK) {
            String response = httpConnection.getResponseString();
            DetectFaceResult resultado = new ObjectMapper().readValue(response, DetectFaceResult.class);
            return new ResultWrapper<>(resultado, TaskType.RECOGNIZE);
        } else if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return new ResultWrapper<>("Acesso ao servidor não autorizado.", TaskType.RECOGNIZE);
        } else {
            return new ResultWrapper<>(httpConnection.getErrorString(), TaskType.RECOGNIZE);
        }
    }

    private ResultWrapper<DetectFaceResult> processaNoApp(FeedFrameRequest feedFrameRequest) {
        DetectFaceResult result = new DetectFaceResult();

        int cameraIdx = 0; // será sempre zero para essa release da SDK
        int scanLineMultiplier = 3; // FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT
        boolean hasError = false;
        boolean firstImage = true;
        Long identifierAssigned = null;

        // carrega a imagem
        FSDK.HImage imageHandle = new FSDK.HImage();
        FSDK.FSDK_IMAGEMODE fsdkMode = new FSDK.FSDK_IMAGEMODE();
        fsdkMode.mode = FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT;
        FSDK.LoadImageFromBuffer (imageHandle, image, feedFrameRequest.getImageWidth(), feedFrameRequest.getImageHeight(),
                (scanLineMultiplier * feedFrameRequest.getImageWidth()), fsdkMode);

        // alimenta o tracker. aqui é feito de fato o reconhecimento
        long[] identifiers = new long[1];
        long[] facesWithNameCount = new long[1];
        int ret = FSDK.FeedFrame(tracker, cameraIdx, imageHandle, facesWithNameCount, identifiers);

        if (ret != FSDK.FSDKE_OK) {
            hasError = true;
            result.setResultCode(ret);
            return new ResultWrapper<>(result, TaskType.RECOGNIZE);
        }

        // primeiro verifica se encontrou exatamente um rosto na imagem, identificado ou nao

        if (identifiers[0] == 0) {
            hasError = true;
            result.setResultCode(FSDK.FSDKE_FACE_NOT_FOUND);
            return new ResultWrapper<>(result, TaskType.RECOGNIZE);
        }


        // foi encontrado exatamente um identificador na imagem
        // agora precisa ver se é um rosto identificado ou não, para isso pega o nome vinculado à face
        // a funcao FSDK_GetAllNames retorna o nome de um identificador, concatenado
        // com os nomes de identificadores similares (caso existam), separados por ;

        String[] identifiedNames = new String[1];
        ret = FSDK.GetAllNames(tracker, identifiers[0], identifiedNames, 65536);
        //logger.info("\r\n********* Identificador: " + identifiers[0] + "    Nome: " + identifiedNames[0]);

        if (ret != FSDK.FSDKE_OK && ret != FSDK.FSDKE_ID_NOT_FOUND) {
            hasError = true;
            result.setResultCode(ret);
            return new ResultWrapper<>(result, TaskType.RECOGNIZE);
        }

        // valida se não retornou mais de um nome associado ao identificador, que seriam os similares
        // TODO será que isto seria um erro? o que poderia ser feito nesse caso?

        if (identifiedNames[0] != null && identifiedNames[0].contains(";")) {
            hasError = true;
            result.setResultCode(FSDK.FSDKE_USER_REGISTERED_WITH_OTHER_NAME);
            return new ResultWrapper<>(result, TaskType.RECOGNIZE);
        }

        long[] similarCount = new long[1];
        FSDK.GetSimilarIDCount(tracker, identifiers[0], similarCount);

        long[] similar = new long[Long.valueOf(similarCount[0]).intValue()];
        FSDK.GetSimilarIDList(tracker, identifiers[0], similar);

        long[] reassigned = new long[1];
        FSDK.GetIDReassignment(tracker, identifiers[0], reassigned);

        if (facesWithNameCount[0] == 0 || "".equals(identifiedNames[0])) {
            hasError = true;
            result.setResultCode(FSDK.FSDKE_USER_NOT_FOUND);
            return new ResultWrapper<>(result, TaskType.RECOGNIZE);
        }

        // tudo certo, retorna o nome que foi atribuido ao rosto encontrado juntamente com a posicao do rosto

        com.luxand.FSDK.TFacePosition facePosition = new com.luxand.FSDK.TFacePosition();
        FSDK.GetTrackerFacePosition(tracker, cameraIdx, identifiers[0], facePosition);

        result.setFace(new Face(identifiers[0], identifiedNames[0]));
        result.setResultCode(FSDK.FSDKE_OK);

        return new ResultWrapper<>(result, TaskType.RECOGNIZE);
    }

    @Override
    protected void onPostExecute(ResultWrapper<DetectFaceResult> resultWrapper) {
        super.onPostExecute(resultWrapper);
        callback.onTaskCompleted(resultWrapper);
    }

}
