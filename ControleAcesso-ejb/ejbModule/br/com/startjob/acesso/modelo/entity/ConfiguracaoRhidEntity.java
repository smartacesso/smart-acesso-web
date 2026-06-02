package br.com.startjob.acesso.modelo.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.rhid.services.RhidSemDominioAcaoEnum;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

@Entity
@Table(name = "TB_CONFIG_RHID")
@NamedQueries({
	@NamedQuery(name = "ConfiguracaoRhidEntity.findAll",
			query = "select obj from ConfiguracaoRhidEntity obj "
					+ "where (obj.removido = false or obj.removido is null) "
					+ "order by obj.id asc"),
	@NamedQuery(name = "ConfiguracaoRhidEntity.findById",
			query = "select obj from ConfiguracaoRhidEntity obj "
					+ "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name = "ConfiguracaoRhidEntity.findByEmail",
			query = "select obj from ConfiguracaoRhidEntity obj "
					+ "where lower(obj.email) = lower(:EMAIL) "
					+ "and (obj.removido = false or obj.removido is null) "
					+ "order by obj.id asc")
})
@SuppressWarnings("serial")
public class ConfiguracaoRhidEntity extends BaseEntity {

	/** Hora padrão da rotina diária (após processamento da folha TOTVS). */
	public static final int HORA_EXECUCAO_AUTOMATICA_PADRAO = 22;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_CONFIG_RHID", nullable = false, length = 4)
	private Long id;

	@Column(name = "EMAIL", nullable = false, unique = true, length = 255)
	private String email;

	@Column(name = "SENHA", nullable = true, length = 255)
	private String senha;

	@Column(name = "DOMINIO", nullable = true)
	private String dominio;

	@Column(name = "URL_BASE", nullable = true, length = 255)
	private String urlBase = "https://rhid.com.br/v2";

	/** @deprecated usar {@link #horaExecucaoAutomatica} — mantido por compatibilidade de coluna. */
	@Column(name = "INTERVALO_MINUTOS", nullable = true)
	private Integer intervaloMinutos = 60;

	@Column(name = "EXPORTACAO_AUTOMATICA", nullable = true)
	private Boolean exportacaoAutomatica = Boolean.FALSE;

	@Enumerated(EnumType.STRING)
	@Column(name = "SEM_DOMINIO_ACAO", nullable = true, length = 20)
	private RhidSemDominioAcaoEnum semDominioAcao = RhidSemDominioAcaoEnum.ENVIAR_TODOS;

	@Temporal(TemporalType.DATE)
	@Column(name = "ULTIMA_EXPORTACAO", nullable = true)
	private Date ultimaExportacao;

	@Column(name = "URL_TOTVS", nullable = true, length = 500)
	private String urlTotvs;

	@Column(name = "USUARIO_TOTVS", nullable = true, length = 100)
	private String usuarioTotvs;

	@Column(name = "SENHA_TOTVS", nullable = true, length = 255)
	private String senhaTotvs;

	@Column(name = "COD_COLIGADA_TOTVS", nullable = true, length = 10)
	private String codColigadaTotvs = "1";

	@Column(name = "COD_SENTENCA_TOTVS", nullable = true, length = 50)
	private String codSentencaTotvs = "API.PTO.001";

	@Column(name = "COD_SISTEMA_TOTVS", nullable = true, length = 5)
	private String codSistemaTotvs = "A";

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_INICIO_COMPLETA", nullable = true)
	private Date dataInicioCompleta;

	@Column(name = "HORA_EXECUCAO_AUTOMATICA", nullable = true)
	private Integer horaExecucaoAutomatica = HORA_EXECUCAO_AUTOMATICA_PADRAO;

	@Column(name = "MAPA_IDS_RHID", nullable = true, length = 4000)
	private String mapaIdsRhid;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "configuracao")
	@Fetch(FetchMode.SUBSELECT)
	private List<DominioRhidEntity> dominios = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getDominio() {
		return dominio;
	}

	public void setDominio(String dominio) {
		this.dominio = dominio;
	}

	public String getUrlBase() {
		return urlBase;
	}

	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}

	public Integer getIntervaloMinutos() {
		return intervaloMinutos;
	}

	public void setIntervaloMinutos(Integer intervaloMinutos) {
		this.intervaloMinutos = intervaloMinutos;
	}

	public Boolean getExportacaoAutomatica() {
		return exportacaoAutomatica;
	}

	public void setExportacaoAutomatica(Boolean exportacaoAutomatica) {
		this.exportacaoAutomatica = exportacaoAutomatica;
	}

	public RhidSemDominioAcaoEnum getSemDominioAcao() {
		return semDominioAcao;
	}

	public void setSemDominioAcao(RhidSemDominioAcaoEnum semDominioAcao) {
		this.semDominioAcao = semDominioAcao;
	}

	public Date getUltimaExportacao() {
		return ultimaExportacao;
	}

	public void setUltimaExportacao(Date ultimaExportacao) {
		this.ultimaExportacao = ultimaExportacao;
	}

	public String getMapaIdsRhid() {
		return mapaIdsRhid;
	}

	public void setMapaIdsRhid(String mapaIdsRhid) {
		this.mapaIdsRhid = mapaIdsRhid;
	}

	public List<DominioRhidEntity> getDominios() {
		return dominios;
	}

	public void setDominios(List<DominioRhidEntity> dominios) {
		this.dominios = dominios;
	}

	public String getUrlTotvs() {
		return urlTotvs;
	}

	public void setUrlTotvs(String urlTotvs) {
		this.urlTotvs = urlTotvs;
	}

	public String getUsuarioTotvs() {
		return usuarioTotvs;
	}

	public void setUsuarioTotvs(String usuarioTotvs) {
		this.usuarioTotvs = usuarioTotvs;
	}

	public String getSenhaTotvs() {
		return senhaTotvs;
	}

	public void setSenhaTotvs(String senhaTotvs) {
		this.senhaTotvs = senhaTotvs;
	}

	public String getCodColigadaTotvs() {
		return codColigadaTotvs;
	}

	public void setCodColigadaTotvs(String codColigadaTotvs) {
		this.codColigadaTotvs = codColigadaTotvs;
	}

	public String getCodSentencaTotvs() {
		return codSentencaTotvs;
	}

	public void setCodSentencaTotvs(String codSentencaTotvs) {
		this.codSentencaTotvs = codSentencaTotvs;
	}

	public String getCodSistemaTotvs() {
		return codSistemaTotvs;
	}

	public void setCodSistemaTotvs(String codSistemaTotvs) {
		this.codSistemaTotvs = codSistemaTotvs;
	}

	public Date getDataInicioCompleta() {
		return dataInicioCompleta;
	}

	public void setDataInicioCompleta(Date dataInicioCompleta) {
		this.dataInicioCompleta = dataInicioCompleta;
	}

	public Integer getHoraExecucaoAutomatica() {
		return horaExecucaoAutomatica;
	}

	public void setHoraExecucaoAutomatica(Integer horaExecucaoAutomatica) {
		this.horaExecucaoAutomatica = horaExecucaoAutomatica;
	}
}
