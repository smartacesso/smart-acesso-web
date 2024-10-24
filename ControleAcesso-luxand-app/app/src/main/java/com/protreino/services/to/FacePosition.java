package com.protreino.services.to;

import com.luxand.FSDK;

public class FacePosition {

    public int w;
    public int xc;
    public int yc;

    public FacePosition() {
    }

    public FacePosition(FSDK.TFacePosition tFacePosition) {
        this.w = tFacePosition.w;
        this.xc = tFacePosition.xc;
        this.yc = tFacePosition.yc;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getXc() {
        return xc;
    }

    public void setXc(int xc) {
        this.xc = xc;
    }

    public int getYc() {
        return yc;
    }

    public void setYc(int yc) {
        this.yc = yc;
    }

}
