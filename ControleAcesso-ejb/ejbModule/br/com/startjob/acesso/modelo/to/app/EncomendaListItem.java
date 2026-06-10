package br.com.startjob.acesso.modelo.to.app;

import java.io.Serializable;
import java.util.Date;

/**
 * Projeção de encomenda/correspondência para API mobile (com destinatário).
 */
public class EncomendaListItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date dataRecebimento;
	private Date dataRetirada;
	private String tipo;
	private String codigoRastreio;
	private String confirmaRetirada;
	private String nomeQuemRetirou;
	private String documentoQuemRetirou;
	private Long destinatarioId;
	private String destinatarioNome;

	public EncomendaListItem(Long id, Date dataRecebimento, Date dataRetirada, String tipo, String codigoRastreio,
			String confirmaRetirada, String nomeQuemRetirou, String documentoQuemRetirou, Long destinatarioId,
			String destinatarioNome) {
		this.id = id;
		this.dataRecebimento = dataRecebimento;
		this.dataRetirada = dataRetirada;
		this.tipo = tipo;
		this.codigoRastreio = codigoRastreio;
		this.confirmaRetirada = confirmaRetirada;
		this.nomeQuemRetirou = nomeQuemRetirou;
		this.documentoQuemRetirou = documentoQuemRetirou;
		this.destinatarioId = destinatarioId;
		this.destinatarioNome = destinatarioNome;
	}

	public Long getId() {
		return id;
	}

	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	public Date getDataRetirada() {
		return dataRetirada;
	}

	public String getTipo() {
		return tipo;
	}

	public String getCodigoRastreio() {
		return codigoRastreio;
	}

	public String getConfirmaRetirada() {
		return confirmaRetirada;
	}

	public String getNomeQuemRetirou() {
		return nomeQuemRetirou;
	}

	public String getDocumentoQuemRetirou() {
		return documentoQuemRetirou;
	}

	public Long getDestinatarioId() {
		return destinatarioId;
	}

	public String getDestinatarioNome() {
		return destinatarioNome;
	}
}
