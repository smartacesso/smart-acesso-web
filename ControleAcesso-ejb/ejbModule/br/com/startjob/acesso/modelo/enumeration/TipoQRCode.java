package br.com.startjob.acesso.modelo.enumeration;

public enum TipoQRCode {
	
	ESTATICO,
	DINAMICO_TEMPO,
	DINAMICO_USO;
	
	@Override
	public String toString() {
		
		if(this.equals(ESTATICO))
			return "Estático";
		
		if(this.equals(DINAMICO_TEMPO))
			return "Dinâmico por tempo";
		
		if(this.equals(DINAMICO_USO))
			return "Dinâmico por uso";
		
		return super.toString();
	}

}
