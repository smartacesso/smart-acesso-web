package br.com.startjob.acesso.to.responsible;

import java.util.Base64;

public class NewsLetterOutput {
	
	private String title;
	private String description;
	private byte[] image;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}

}
