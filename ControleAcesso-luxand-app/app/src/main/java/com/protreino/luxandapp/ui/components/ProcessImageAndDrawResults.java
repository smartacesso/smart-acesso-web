package com.protreino.luxandapp.ui.components;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;

import com.luxand.FSDK;
import com.protreino.luxandapp.R;
import com.protreino.luxandapp.async.ProcessAccessRequestTask;
import com.protreino.luxandapp.async.RecognitionTask;
import com.protreino.luxandapp.interfaces.TaskCallback;
import com.protreino.luxandapp.main.App;
import com.protreino.luxandapp.model.PendingAction;
import com.protreino.luxandapp.model.ResultWrapper;
import com.protreino.luxandapp.ui.home.HomeActivity;
import com.protreino.services.enumeration.TaskType;
import com.protreino.services.enumeration.VerificationResult;
import com.protreino.services.to.DetectFaceResult;
import com.protreino.services.to.SimpleDevice;

// Draw graphics on top of the video
@SuppressLint({"DrawAllocation", "ViewConstructor"})
public class ProcessImageAndDrawResults extends View implements TaskCallback {

    private Paint rectPaint, textPaint;
    private float ratio;
    private HomeActivity homeActivity;
    private Canvas canvas;
    private String serverRecognizerUrl;
    private int unsuccessfulTriesInSequence;
    private Preview preview;
    private DetectFaceResult resultadoFeedFrame;
    private int detectFaceFramesCount = 0;
    private PendingAction pendingAction;
    private Rect faceRect;

    public byte[] dataYUV;
    public byte[] dataRGB;
    public int previewWidth, previewHeight;
    private int imageWidth, imageHeight;
    public int imageRotation = 0;
    public boolean mirrorHorizontal = false;
    public boolean mirrorVertical = false;
    public int stopping = 0;
    public boolean blockNewFrames = false;

    public ProcessImageAndDrawResults(HomeActivity homeActivity) {
        super(homeActivity.getApplicationContext());
        this.homeActivity = homeActivity;

        String ip = App.getPreferenceAsString(R.string.pref_recognition_server_ip);
        Integer port = App.getPreferenceAsInteger(R.string.pref_recognition_server_port);
        serverRecognizerUrl = "http://" + ip + ":" + port.toString();

        rectPaint = createRectPaint();
        textPaint = createTextPaint();
    }

    public void invalidateForced() {
        pendingAction = null;
        dataYUV = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        blockNewFrames = true;

        try {
            if (pendingAction != null) {
                resolvePendingAction();
                return;
            }

            if (stopping == 1 || dataYUV == null) {
                super.onDraw(canvas);
                blockNewFrames = false;
                return;
            }

            imageWidth = previewWidth;
            imageHeight = previewHeight;
            if (imageRotation != 0) {
                dataYUV = rotateYUV420Degree90(dataYUV, imageWidth, imageHeight);
                imageWidth = previewHeight;
                imageHeight = previewWidth;
            }

            // Convert from YUV to RGB
            decodeYUV420SP(dataRGB, dataYUV, imageWidth, imageHeight);

            ratio = (canvas.getWidth() * 1.0f) / imageWidth;

            // Load image to FaceSDK
            FSDK.HImage imageHandle = new FSDK.HImage();
            FSDK.FSDK_IMAGEMODE imagemode = new FSDK.FSDK_IMAGEMODE();
            imagemode.mode = FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT;
            FSDK.LoadImageFromBuffer(imageHandle, dataRGB, imageWidth, imageHeight, imageWidth * 3, imagemode);

            if (mirrorHorizontal)
                FSDK.MirrorImage(imageHandle, false);

            if (mirrorVertical)
                FSDK.MirrorImage(imageHandle, true);

            FSDK.TFacePosition tFacePosition = new FSDK.TFacePosition();
            int ret = FSDK.DetectFace(imageHandle, tFacePosition);

            if (ret == FSDK.FSDKE_OK) {
                detectFaceFramesCount++;

                if (detectFaceFramesCount == 10) {
                    detectFaceFramesCount = 0;

                    // pause o preview, destaca o rosto de cinza e envia a imagem para o servidor
                    // para reconhecimento e validação
                    pausePreview();

                    faceRect = getRectFromFacePosition(tFacePosition);
                    drawFace(faceRect, "Reconhecendo...", Color.GRAY);

                    homeActivity.setResultLabel("Reconhecendo...", R.color.colorPrimary);

                    // exporta a imagem do handler que está rotacionada
                    int[] bufferSize = new int[1];
                    FSDK.GetImageBufferSize(imageHandle, bufferSize, imagemode);

                    byte[] buffer = new byte[bufferSize[0]];
                    FSDK.SaveImageToBuffer(imageHandle, buffer, imagemode);

                    RecognitionTask recognitionTask = new RecognitionTask(this,
                            serverRecognizerUrl, buffer, imageWidth, imageHeight);
                    recognitionTask.execute();

                }
                else {
                    faceRect = getRectFromFacePosition(tFacePosition);
                    drawFace(faceRect, " ", Color.GRAY);
                    blockNewFrames = false;
                    homeActivity.setResultLabel("Posicione o rosto", Color.GRAY);
                }
            }
            else {
                detectFaceFramesCount = 0;
                blockNewFrames = false;
                homeActivity.setResultLabel("Posicione o rosto", Color.GRAY);

                if (ret != FSDK.FSDKE_FACE_NOT_FOUND) {
                    // Erro inesperado no DetectFace, imprime para ver
                    System.out.println("**** Erro inesperado no DetectFace: " + ret);
                }
            }

            FSDK.FreeImage(imageHandle);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            blockNewFrames = false;
        }
    }

    private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    @Override
    public void onTaskCompleted(ResultWrapper<?> resultWrapper) {
        if (stopping == 1) {
            invalidateForced();
        }

        if (TaskType.RECOGNIZE.equals(resultWrapper.getTaskType())) {

            if (resultWrapper.hasError()) {

                pendingAction = new PendingAction(faceRect, resultWrapper.getErrorMessage(),
                        Color.RED, resultWrapper.getErrorMessage(), true, 5000);

            }
            else {
                resultadoFeedFrame = (DetectFaceResult) resultWrapper.getResult();

                if (resultadoFeedFrame.getResultCode() == FSDK.FSDKE_OK) {
                    unsuccessfulTriesInSequence = 0;

                    SimpleDevice selectedDevice = (SimpleDevice) App.getPreferenceAsObject(R.string.pref_selected_device, SimpleDevice.class);

                    if (selectedDevice != null) {

                        // dispara a task com requisicao para o desktop
                        String codigo = getCodigoFromName(resultadoFeedFrame.getFace().getName());
                        ProcessAccessRequestTask processAccessRequestTask = new ProcessAccessRequestTask(this,
                                codigo, selectedDevice.getIdentifier());
                        processAccessRequestTask.execute();

                        pendingAction = new PendingAction(faceRect, resultadoFeedFrame.getFace().getName(),
                                R.color.colorAccent,  "Enviando acesso para " + selectedDevice.getName() + "...");

                    }
                    else {

                        // dispara a task com requisicao para o desktop
                        String codigo = getCodigoFromName(resultadoFeedFrame.getFace().getName());
                        ProcessAccessRequestTask processAccessRequestTask = new ProcessAccessRequestTask(this,
                                codigo, null);
                        processAccessRequestTask.execute();

                        pendingAction = new PendingAction(faceRect, resultadoFeedFrame.getFace().getName(),
                                R.color.colorAccent, "Processando exibição...", true, 5000);

                    }
                }
                else {
                    if (resultadoFeedFrame.getResultCode() == FSDK.FSDKE_USER_NOT_FOUND) {
                        unsuccessfulTriesInSequence++;

                        long resumeFrameDelay = 5000;
                        String text = "Usuário não encontrado";

                        if (unsuccessfulTriesInSequence == 2) {
                            unsuccessfulTriesInSequence = 0;
                            resumeFrameDelay = 5000;
                            text = "Usuário não cadastrado";
                        }

                        pendingAction = new PendingAction(faceRect, text, Color.RED, text, true, resumeFrameDelay);
                    }
                    else {
                        unsuccessfulTriesInSequence = 0;

                        pendingAction = new PendingAction(faceRect, resultadoFeedFrame.getResultDescription(),
                                Color.RED, resultadoFeedFrame.getResultDescription(), true, 5000);
                    }
                }

            }

            invalidate();

        }
        else if (TaskType.ACCESS_REQUEST.equals(resultWrapper.getTaskType())) {

            String message;
            int color;

            if (resultWrapper.hasError()) {
                message = resultWrapper.getErrorMessage();
                color = Color.RED;
            }
            else {
                VerificationResult verificationResult = (VerificationResult) resultWrapper.getResult();

                if (VerificationResult.ALLOWED.equals(verificationResult)
                        || VerificationResult.TOLERANCE_PERIOD.equals(verificationResult)) {
                    message = "Acesso permitido para\n" + resultadoFeedFrame.getFace().getName();
                    color = Color.GREEN;
                }
                else if (VerificationResult.NOT_FOUND.equals(verificationResult)) {
                    message = "Usuário não encontrado";
                    color = Color.RED;
                }
                else {
                    message = "Acesso negado para\n" + resultadoFeedFrame.getFace().getName();
                    color = Color.RED;
                }
            }

            pendingAction = new PendingAction(faceRect, resultadoFeedFrame.getFace().getName(),
                    color, message, true, 5000);
            invalidate();
        }
    }

    private void resolvePendingAction() {
        boolean pendingResolved = true;

        if (pendingAction.draw) {
            drawFace(pendingAction.faceRect, pendingAction.faceLabel, pendingAction.color);
        }

        if (pendingAction.message != null) {
            homeActivity.setResultLabel(pendingAction.message, pendingAction.color);
        }

        if (pendingAction.resumeFramesAndClean) {

            if (pendingAction.resumeFrameDelay > 0) {

                // agenda uma nova pendencia para só entao remover o pause e limpar a tela
                new Handler().postDelayed(() -> {
                    pendingAction = new PendingAction(true);
                    invalidate();
                }, pendingAction.resumeFrameDelay);

                pendingResolved = false;

            }
            else {
                clearCanvas();
                blockNewFrames = false;
                homeActivity.setResultLabel("Posicione o rosto", Color.GRAY);
                resumePreview();
            }
        }

        if (pendingResolved)
            pendingAction = null;

    }

    private void pausePreview() {
        preview.pause();
    }

    private void resumePreview() {
        preview.resume();
    }

    public void clearCanvas() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    public void drawFace(Rect rect, String text, int color) {
        int bottomShift = (int) (22 * HomeActivity.sDensity);
        int topShift = (int) (5 * HomeActivity.sDensity);

        rectPaint.setColor(color);
        textPaint.setColor(color);

        canvas.drawRect(rect, rectPaint);

        if (rect.bottom < imageHeight * ratio) { // write on bottom
            canvas.drawText(text, rect.centerX(), rect.bottom + bottomShift, textPaint);
        }
        else { // write on top
            canvas.drawText(text, rect.centerX(), rect.top - topShift, textPaint);
        }
    }

    private Rect getRectFromFacePosition(FSDK.TFacePosition facePosition) {
        Rect rect = new Rect();
        float margin = facePosition.w * 0.6f;
        rect.top = Float.valueOf((facePosition.yc - margin) * ratio).intValue();
        rect.bottom = Float.valueOf((facePosition.yc + margin) * ratio).intValue();
        rect.left = Float.valueOf((facePosition.xc - margin) * ratio).intValue();
        rect.right = Float.valueOf((facePosition.xc + margin) * ratio).intValue();
        return rect;
    }

    private void decodeYUV420SP(byte[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int yp = 0;
        for (int j = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0) r = 0; else if (r > 262143) r = 262143;
                if (g < 0) g = 0; else if (g > 262143) g = 262143;
                if (b < 0) b = 0; else if (b > 262143) b = 262143;

                rgb[3*yp] = (byte) ((r >> 10) & 0xff);
                rgb[3*yp+1] = (byte) ((g >> 10) & 0xff);
                rgb[3*yp+2] = (byte) ((b >> 10) & 0xff);
                ++yp;
            }
        }
    }

    private Paint createRectPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        return paint;
    }

    private Paint createTextPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(6);
        paint.setTextSize(16 * HomeActivity.sDensity);
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }

    public void setPreview(Preview preview) {
        this.preview = preview;
    }

    private String getCodigoFromName(String name) {
        return name.substring(name.indexOf("(") + 1, name.length() - 1);
    }

}
