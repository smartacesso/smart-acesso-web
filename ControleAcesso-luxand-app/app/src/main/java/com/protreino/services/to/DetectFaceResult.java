package com.protreino.services.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.protreino.luxandapp.ui.home.HomeViewModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DetectFaceResult {

    private int resultCode;
    private String resultDescription;
    private Face face;

    public DetectFaceResult() {
        this.resultCode = -1;
        this.resultDescription = getErrorDescription(resultCode);
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int result) {
        this.resultCode = result;
        this.resultDescription = getErrorDescription(resultCode);
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public Face getFace() {
        return face;
    }

    public void setFace(Face face) {
        this.face = face;
    }

    private String getErrorDescription(int code) {
        return HomeViewModel.getResultDescription(code);
    }

}
