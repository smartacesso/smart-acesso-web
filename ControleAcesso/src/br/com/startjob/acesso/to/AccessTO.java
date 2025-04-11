package br.com.startjob.acesso.to;

import java.text.ParseException;
import java.text.SimpleDateFormat;



import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;

public class AccessTO {
	
	
	private PedestreAppDto pedestre;
	
	private String data;
	
	private String sentido;
	
	private String equipamento;

	private String tipo;
	
	private String local;
	
	private String razao;

	public PedestreAppDto getPedestre() {
		return pedestre;
	}

	public void setPedestre(PedestreAppDto pedestre) {
		this.pedestre = pedestre;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getSentido() {
		return sentido;
	}

	public void setSentido(String sentido) {
		this.sentido = sentido;
	}

	public String getEquipamento() {
		return equipamento;
	}

	public void setEquipamento(String equipamento) {
		this.equipamento = equipamento;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getRazao() {
		return razao;
	}

	public void setRazao(String razao) {
		this.razao = razao;
	}

	
	@SuppressWarnings("static-access")
	public AccessTO convertToAccessTO(AcessoEntity acesso) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		this.data = formatter.format(acesso.getData());
		this.tipo = acesso.getTipo();
		this.local = acesso.getLocal();
		this.razao = acesso.getRazao();
		this.sentido = acesso.getSentido();
		this.equipamento = acesso.getEquipamento();
		this.pedestre = convertToResponseDTO(acesso.getPedestre());

		return this;
	}
	
	public PedestreAppDto convertToResponseDTO(PedestreEntity entity) {
		PedestreAppDto dto = new PedestreAppDto();
	    dto.setId(entity.getId());
	    dto.setIdTemp(entity.getIdTemp());
	    dto.setIdUsuario(entity.getUsuario() != null ? entity.getUsuario().getId() : null);
	    dto.setName(entity.getNome());
	    dto.setTipo(entity.getTipo() != null ? entity.getTipo().name() : null);
	    dto.setEmail(entity.getEmail());
	    dto.setCpf(entity.getCpf());
	    dto.setGenero(entity.getGenero() != null ? entity.getGenero().name() : null);
	    dto.setRg(entity.getRg());
	    dto.setTelefone(entity.getTelefone());
	    dto.setCelular(entity.getCelular());
	    return dto;
	}

	

}
