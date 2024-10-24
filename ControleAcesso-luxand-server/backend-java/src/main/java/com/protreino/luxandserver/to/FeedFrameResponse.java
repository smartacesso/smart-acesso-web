package com.protreino.luxandserver.to;

public class FeedFrameResponse {
	
	private int resultCode = -1;
	private String resultDescription = "";
	private Face face;
	
	public FeedFrameResponse() {
	}
	
	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
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
	
}
