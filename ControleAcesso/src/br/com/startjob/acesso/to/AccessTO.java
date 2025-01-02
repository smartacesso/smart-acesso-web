package br.com.startjob.acesso.to;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;

public class AccessTO {
	
	
	private PedestrianAccessTO pedestre;
	
	private Date data;
	
	private String sentido;
	
	private String equipamento;

	private String tipo;
	
	private String local;
	
	private String razao;
	
	private String cartaoAcessoRecebido;
	
	private Long qtdePedestresHora;
	
	private Integer hora;
	
	private Boolean bloquearSaida;
	
	private Long idPedestre;

	public PedestrianAccessTO getPedestre() {
		return pedestre;
	}

	public void setPedestre(PedestrianAccessTO pedestre) {
		this.pedestre = pedestre;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
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

	public String getCartaoAcessoRecebido() {
		return cartaoAcessoRecebido;
	}

	public void setCartaoAcessoRecebido(String cartaoAcessoRecebido) {
		this.cartaoAcessoRecebido = cartaoAcessoRecebido;
	}

	public Long getQtdePedestresHora() {
		return qtdePedestresHora;
	}

	public void setQtdePedestresHora(Long qtdePedestresHora) {
		this.qtdePedestresHora = qtdePedestresHora;
	}

	public Integer getHora() {
		return hora;
	}

	public void setHora(Integer hora) {
		this.hora = hora;
	}

	public Boolean getBloquearSaida() {
		return bloquearSaida;
	}

	public void setBloquearSaida(Boolean bloquearSaida) {
		this.bloquearSaida = bloquearSaida;
	}

	public Long getIdPedestre() {
		return idPedestre;
	}

	public void setIdPedestre(Long idPedestre) {
		this.idPedestre = idPedestre;
	}
	
	@SuppressWarnings("static-access")
	public AccessTO convertToAccessTO(AcessoEntity acesso) throws ParseException {
		
		this.data = acesso.getData();
		this.tipo = acesso.getTipo();
		this.local = acesso.getLocal();
		this.razao = acesso.getRazao();
		this.sentido = acesso.getSentido();
		this.equipamento = acesso.getEquipamento();
		this.bloquearSaida = acesso.getBloquearSaida();
		
		PedestrianAccessTO pedestreTO = new PedestrianAccessTO();
		this.pedestre = (pedestreTO.convertPedestrianAccess(acesso.getPedestre()));
		return this;
		
	}
	

}
