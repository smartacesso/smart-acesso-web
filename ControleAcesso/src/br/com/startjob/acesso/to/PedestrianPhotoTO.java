package br.com.startjob.acesso.to;

public class PedestrianPhotoTO {
	
	private Long id;
	private String fotoBase64;
	
	public PedestrianPhotoTO(Long id, String fotoBase64) {
		this.id = id;
		this.fotoBase64 = fotoBase64;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFotoBase64() {
		return fotoBase64;
	}

	public void setFotoBase64(String fotoBase64) {
		this.fotoBase64 = fotoBase64;
	}
	
}
