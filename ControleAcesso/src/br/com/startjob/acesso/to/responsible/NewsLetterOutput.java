package br.com.startjob.acesso.to.responsible;

import java.util.Base64;
import java.util.Date;

public class NewsLetterOutput {
	
	private String title;
	private String description;
	private String image;
	private Date eventDate;
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

}
