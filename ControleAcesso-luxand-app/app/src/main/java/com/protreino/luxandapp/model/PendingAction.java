package com.protreino.luxandapp.model;

import android.graphics.Rect;

public class PendingAction {

    public boolean draw; // indica se desenhará na tela
    public Rect faceRect; // retangulo a ser desenhado
    public String faceLabel; // texto a ser escrito no retangulo
    public int color; // cor do retangulo e dos textos
    public String message; // mensagem inferior
    public boolean resumeFramesAndClean; // indica se removerá o pause da tela e limpará tudo
    public long resumeFrameDelay; // indica se haverá um delay para remover o pause da tela

    public PendingAction(Rect faceRect, String faceLabel, int color, String message) {
        this.draw = true;
        this.faceRect = faceRect;
        this.faceLabel = faceLabel;
        this.color = color;
        this.message = message;
    }

    public PendingAction(Rect faceRect, String faceLabel, int color, String message, boolean resumeFramesAndClean, long resumeFrameDelay) {
        this.draw = true;
        this.faceRect = faceRect;
        this.faceLabel = faceLabel;
        this.color = color;
        this.message = message;
        this.resumeFramesAndClean = resumeFramesAndClean;
        this.resumeFrameDelay = resumeFrameDelay;
    }

    public PendingAction(boolean resumeFramesAndClean) {
        this.resumeFramesAndClean = resumeFramesAndClean;
    }

}
