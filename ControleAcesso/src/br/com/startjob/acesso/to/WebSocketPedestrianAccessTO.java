package br.com.startjob.acesso.to;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;

import br.com.startjob.acesso.modelo.entity.PedestreEntity;

public class WebSocketPedestrianAccessTO {
	private Long id;
	private String name;
	private String cardNumber;
	private String tipo;
	private String status;
	private String cpf;
	private String rg;
	private Boolean sempreLiberado;
	private String fotoBase64;
	
	private Long idLocal;
	private Long idEmpresa;
	
	private List<PedestreRegraTO> pedestreRegras;
	
	public static WebSocketPedestrianAccessTO fromPedestre(PedestreEntity pedestre) {
		WebSocketPedestrianAccessTO object = new WebSocketPedestrianAccessTO();

		object.setId(pedestre.getId());

		object.setName(Objects.nonNull(pedestre.getNome()) ? pedestre.getNome() : null);
		object.setCardNumber(Objects.nonNull(pedestre.getCodigoCartaoAcesso()) ? pedestre.getCodigoCartaoAcesso() : null);

		object.setTipo(Objects.nonNull(pedestre.getTipo()) ? pedestre.getTipo().toString() : null);
		object.setStatus(Objects.nonNull(pedestre.getStatus()) ? pedestre.getStatus().toString() : null);

		object.setCpf(Objects.nonNull(pedestre.getCpf()) ? pedestre.getCpf() : null);
		object.setRg(Objects.nonNull(pedestre.getRg()) ? pedestre.getRg() : null);

		object.setSempreLiberado(Objects.nonNull(pedestre.getSempreLiberado()) ? pedestre.getSempreLiberado() : false);

		byte[] fotoBytes = pedestre.getFoto();
		object.setFotoBase64(fotoBytes != null ? Base64.encodeBase64String(fotoBytes) : "");

		object.setIdLocal(Objects.nonNull(pedestre.getIdLocal()) ? pedestre.getIdLocal() : null);
		object.setIdEmpresa(Objects.nonNull(pedestre.getEmpresa()) ? pedestre.getEmpresa().getId() : null);

		if (Objects.nonNull(pedestre.getRegras())) {
			List<PedestreRegraTO> pedestreRegraTO = pedestre.getRegras()
				.stream()
				.filter(Objects::nonNull)
				.map(PedestreRegraTO::fromDomain)
				.collect(Collectors.toList());

			object.setPedestreRegras(pedestreRegraTO);
		}

		return object;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public Boolean getSempreLiberado() {
		return sempreLiberado;
	}

	public void setSempreLiberado(Boolean sempreLiberado) {
		this.sempreLiberado = sempreLiberado;
	}

	public String getFotoBase64() {
		return fotoBase64;
	}

	public void setFotoBase64(String fotoBase64) {
		this.fotoBase64 = fotoBase64;
	}

	public Long getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(Long idLocal) {
		this.idLocal = idLocal;
	}

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public List<PedestreRegraTO> getPedestreRegras() {
		return pedestreRegras;
	}

	public void setPedestreRegras(List<PedestreRegraTO> pedestreRegras) {
		this.pedestreRegras = pedestreRegras;
	}
	
	
}
