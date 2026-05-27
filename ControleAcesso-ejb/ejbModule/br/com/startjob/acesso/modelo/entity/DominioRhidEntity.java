package br.com.startjob.acesso.modelo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

@Entity
@Table(name = "TB_DOMINIO_RHID")
@NamedQueries({
	@NamedQuery(name = "DominioRhidEntity.findByConfig",
			query = "select obj from DominioRhidEntity obj "
					+ "where obj.configuracao.id = :ID_CONFIG "
					+ "and (obj.removido = false or obj.removido is null) "
					+ "order by obj.id asc")
})
@SuppressWarnings("serial")
public class DominioRhidEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_DOMINIO_RHID", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_CONFIG_RHID", nullable = false)
	private ConfiguracaoRhidEntity configuracao;

	@Column(name = "NOME_DOMINIO", nullable = false, length = 255)
	private String nomeDominio;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ConfiguracaoRhidEntity getConfiguracao() {
		return configuracao;
	}

	public void setConfiguracao(ConfiguracaoRhidEntity configuracao) {
		this.configuracao = configuracao;
	}

	public String getNomeDominio() {
		return nomeDominio;
	}

	public void setNomeDominio(String nomeDominio) {
		this.nomeDominio = nomeDominio;
	}
}
