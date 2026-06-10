package br.com.startjob.acesso.to.app;

import java.util.Date;

import br.com.startjob.acesso.modelo.to.app.EncomendaListItem;

public class EncomendaItemDTO {

	private Long id;
	private Date dataRecebimento;
	private Date dataRetirada;
	private String tipo;
	private String codigoRastreio;
	private String confirmaRetirada;
	private String nomeQuemRetirou;
	private String documentoQuemRetirou;
	private DestinatarioEncomendaDTO destinatario;
	private Boolean podeConfirmarRetirada;

	public static EncomendaItemDTO from(EncomendaListItem item) {
		return from(item, null, false);
	}

	public static EncomendaItemDTO from(EncomendaListItem item, Long userIdLogado, boolean perfilGerencial) {
		if (item == null) {
			return null;
		}
		EncomendaItemDTO dto = new EncomendaItemDTO();
		dto.setId(item.getId());
		dto.setDataRecebimento(item.getDataRecebimento());
		dto.setDataRetirada(item.getDataRetirada());
		dto.setTipo(item.getTipo());
		dto.setCodigoRastreio(item.getCodigoRastreio());
		dto.setConfirmaRetirada(item.getConfirmaRetirada());
		dto.setNomeQuemRetirou(item.getNomeQuemRetirou());
		dto.setDocumentoQuemRetirou(item.getDocumentoQuemRetirou());
		if (item.getDestinatarioId() != null) {
			DestinatarioEncomendaDTO dest = new DestinatarioEncomendaDTO();
			dest.setId(item.getDestinatarioId());
			dest.setNome(item.getDestinatarioNome());
			dto.setDestinatario(dest);
		}
		dto.setPodeConfirmarRetirada(calcularPodeConfirmarRetirada(item, userIdLogado, perfilGerencial));
		return dto;
	}

	private static boolean calcularPodeConfirmarRetirada(EncomendaListItem item, Long userIdLogado,
			boolean perfilGerencial) {
		if (item == null || "S".equals(item.getConfirmaRetirada())) {
			return false;
		}
		if (perfilGerencial) {
			return true;
		}
		return userIdLogado != null && item.getDestinatarioId() != null
				&& userIdLogado.equals(item.getDestinatarioId());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public Date getDataRetirada() {
		return dataRetirada;
	}

	public void setDataRetirada(Date dataRetirada) {
		this.dataRetirada = dataRetirada;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCodigoRastreio() {
		return codigoRastreio;
	}

	public void setCodigoRastreio(String codigoRastreio) {
		this.codigoRastreio = codigoRastreio;
	}

	public String getConfirmaRetirada() {
		return confirmaRetirada;
	}

	public void setConfirmaRetirada(String confirmaRetirada) {
		this.confirmaRetirada = confirmaRetirada;
	}

	public String getNomeQuemRetirou() {
		return nomeQuemRetirou;
	}

	public void setNomeQuemRetirou(String nomeQuemRetirou) {
		this.nomeQuemRetirou = nomeQuemRetirou;
	}

	public String getDocumentoQuemRetirou() {
		return documentoQuemRetirou;
	}

	public void setDocumentoQuemRetirou(String documentoQuemRetirou) {
		this.documentoQuemRetirou = documentoQuemRetirou;
	}

	public DestinatarioEncomendaDTO getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(DestinatarioEncomendaDTO destinatario) {
		this.destinatario = destinatario;
	}

	public Boolean getPodeConfirmarRetirada() {
		return podeConfirmarRetirada;
	}

	public void setPodeConfirmarRetirada(Boolean podeConfirmarRetirada) {
		this.podeConfirmarRetirada = podeConfirmarRetirada;
	}
}
