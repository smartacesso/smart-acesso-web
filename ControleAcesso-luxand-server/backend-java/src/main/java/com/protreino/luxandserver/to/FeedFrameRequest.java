package com.protreino.luxandserver.to;

import java.util.List;

public class FeedFrameRequest {
	
	private String mode;
	private String nameToAssign;
	private int imageWidth;
	private int imageHeight;
	private List<String> imagesBase64;
	private List<byte[]> images;
	
	public FeedFrameRequest() {
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getNameToAssign() {
		return nameToAssign;
	}

	public void setNameToAssign(String nameToAssign) {
		this.nameToAssign = nameToAssign;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public List<String> getImagesBase64() {
		return imagesBase64;
	}

	public void setImagesBase64(List<String> imagesBase64) {
		this.imagesBase64 = imagesBase64;
	}

	public List<byte[]> getImages() {
		return images;
	}

	public void setImages(List<byte[]> images) {
		this.images = images;
	}
	
}
