package br.com.startjob.acesso.modelo.entity;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;


@Entity
@Table(name="TB_CLIENTE")
@NamedQueries({
	@NamedQuery(name  = "ClienteEntity.findAll", 
				query = "select obj "
				      + "from ClienteEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "ClienteEntity.findById", 
				query = "select obj from ClienteEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "ClienteEntity.findByIdComplete", 
				query = "select obj from ClienteEntity obj "
					  + " left join fetch obj.endereco e "
					  + " left join fetch obj.planos p "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "ClienteEntity.findByIdComConfiguracoes", 
				query = "select obj from ClienteEntity obj "
					  + " left join fetch obj.configuracoesDesktop e "
					  + "where obj.id = :ID_CLIENTE order by obj.id asc"),
	@NamedQuery(name = "ClienteEntity.findByNomeUnidadeOrganizacional",
				query = "select obj from ClienteEntity obj "
					  + "where lower(obj.nomeUnidadeOrganizacional) = lower(:UNIDADE_ORGANIZACIONAL) "
					  + "and (obj.removido = false or obj.removido is null) "),
	@NamedQuery(name = "ClienteEntity.findAllComIntegracaoSOC",
				query = "select obj from ClienteEntity obj "
					  + "join fetch obj.integracaoSoc i "
					  + "where ((obj.removido = false or obj.removido is null) and (i.empresa is not null or i.empresa = '')) "
					  + "order by obj.id asc"),
	@NamedQuery(name = "ClienteEntity.findAllComIntegracaoSenior",
				query = "select obj from ClienteEntity obj "
					  + "join fetch obj.integracaoSenior i "
					  + "where ((obj.removido = false or obj.removido is null) "
					  + "and (i.usuario is not null or i.usuario = '') "
					  + "and (i.senha is not null or i.senha = '') "
					  + "and (i.url is not null or i.url = '')) "
					  + "order by obj.id asc"),
	@NamedQuery(name = "ClienteEntity.findAllComIntegracaoTotvs",
				query = "select obj from ClienteEntity obj "
					  + "join fetch obj.integracaoTotvs i "
					  + "where ((obj.removido = false or obj.removido is null) "
					  + "and (i.url is not null or i.url = '')) "
					  + "order by obj.id asc"),
	@NamedQuery(name = "ClienteEntity.findComFilialTeknisa",
				query = "select obj from ClienteEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "and obj.organizacaoTeknisa = :ORGANIZACAO_TEKNISA and obj.filialTeknisa = :FILIAL_TEKNISA "
					  + "order by obj.id asc"),
	@NamedQuery(name = "ClienteEntity.findAllComIntegracaoAD",
				query = "select obj from ClienteEntity obj "
					  + "join fetch obj.integracaoAD i "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name = "ClienteEntity.findComFilialAndColigadaTotvs",
				query = "select obj from ClienteEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "and obj.codfilial = :CFILIAL_TOTVS and obj.codcoligad = :CCOLIGADA_TOTVS "
					  + "order by obj.id asc"),
	@NamedQuery(name = "ClienteEntity.findByUnidadeOrganizacional",
				query = "select obj from ClienteEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "and obj.nomeUnidadeOrganizacional = :UNIDADE_ORGANIZACIONAL and (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),

})
@SuppressWarnings("serial")
public class ClienteEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_CLIENTE", nullable=false, length=4)
	private Long id;
	
	@Column(name="NOME", nullable=true, length=255)
	private String nome;
	
	@Column(name="EMAIL", nullable=true, length=100)
	private String email;
	
	@Column(name="CNPJ", nullable=true, length=50)
	private String cnpj;
	
	@Column(name="TELEFONE", nullable=true, length=30)
	private String telefone;
	
	@Column(name="CELULAR", nullable=true, length=30)
	private String celular;
	
	@Column(name="CONTATO", nullable=true, length=255)
	private String contato;
	
	@Enumerated(EnumType.STRING)
	@Column(name="STATUS", nullable=true, length=10)
	private Status status;
	
	@Column(name="UNIDADE_ORGANIZACIONAL", nullable=true, length=60)
	private String nomeUnidadeOrganizacional;
	
	@ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_ENDERECO", nullable=true)
	private EnderecoEntity endereco;
	
	@ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_CONFIGURACAO_DESKTOP", nullable=true)
	private ConfiguracoesDesktopEntity configuracoesDesktop;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, 
			   orphanRemoval=true, targetEntity=PlanoEntity.class,
			   mappedBy="cliente")
	@Fetch(FetchMode.SUBSELECT)
	private List<PlanoEntity> planos;
	
	@JsonIgnore
	@OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@JoinColumn(name="ID_INTEGRACAO_SOC", nullable=true)
	private IntegracaoSOCEntity integracaoSoc;
	
	@JsonIgnore
	@OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@JoinColumn(name="ID_INTEGRACAO_SENIOR", nullable=true)
	private IntegracaoSeniorEntity integracaoSenior;
	
	@JsonIgnore
	@OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@JoinColumn(name="ID_INTEGRACAO_TOTVS", nullable=true)
	private IntegracaoTotvsEntity integracaoTotvs;

	@JsonIgnore
	@OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_INTEGRACAO_AD", nullable=true)
	private IntegracaoSeniorEntity integracaoAD;

	@Column(name="ORGANIZACAO_TEKNISA", nullable=true, length=60)
	private String organizacaoTeknisa;
	
	@Column(name="FILIAL_TEKNISA", nullable=true, length=60)
	private String filialTeknisa;

	@Column(name="CFILIAL_TOTVS", nullable=true, length=60)
	private String codfilial;
	
	@Column(name="CCOLIGADA_TOTVS", nullable=true, length=60)
	private String codcoligad;

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public EnderecoEntity getEndereco() {
		return endereco;
	}
	public void setEndereco(EnderecoEntity endereco) {
		this.endereco = endereco;
	}
	public List<PlanoEntity> getPlanos() {
		return planos;
	}
	public void setPlanos(List<PlanoEntity> planos) {
		this.planos = planos;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getContato() {
		return contato;
	}
	public void setContato(String contato) {
		this.contato = contato;
	}
	public ConfiguracoesDesktopEntity getConfiguracoesDesktop() {
		return configuracoesDesktop;
	}
	public void setConfiguracoesDesktop(ConfiguracoesDesktopEntity configuracoesDesktop) {
		this.configuracoesDesktop = configuracoesDesktop;
	}
	public String getNomeUnidadeOrganizacional() {
		return nomeUnidadeOrganizacional;
	}
	public void setNomeUnidadeOrganizacional(String nomeUnidadeOrganizacional) {
		this.nomeUnidadeOrganizacional = nomeUnidadeOrganizacional;
	}
	public IntegracaoSOCEntity getIntegracaoSoc() {
		return integracaoSoc;
	}
	public void setIntegracaoSoc(IntegracaoSOCEntity integracaoSoc) {
		this.integracaoSoc = integracaoSoc;
	}
	public IntegracaoSeniorEntity getIntegracaoSenior() {
		return integracaoSenior;
	}
	public void setIntegracaoSenior(IntegracaoSeniorEntity integracaoSenior) {
		this.integracaoSenior = integracaoSenior;
	}
	public String getOrganizacaoTeknisa() {
		return organizacaoTeknisa;
	}
	public void setOrganizacaoTeknisa(String organizacaoTeknisa) {
		this.organizacaoTeknisa = organizacaoTeknisa;
	}
	public String getFilialTeknisa() {
		return filialTeknisa;
	}
	public void setFilialTeknisa(String filialTeknisa) {
		this.filialTeknisa = filialTeknisa;
	}
	public IntegracaoTotvsEntity getIntegracaoTotvs() {
		return integracaoTotvs;
	}
	public void setIntegracaoTotvs(IntegracaoTotvsEntity integracaoTotvs) {
		this.integracaoTotvs = integracaoTotvs;
	}
	public IntegracaoSeniorEntity getIntegracaoAD() {
		return integracaoAD;
	}
	public void setIntegracaoAD(IntegracaoSeniorEntity integracaoAD) {
		this.integracaoAD = integracaoAD;
	}
	public String getCodfilial() {
		return codfilial;
	}
	public void setCodfilial(String codfilial) {
		this.codfilial = codfilial;
	}
	public String getCodcoligad() {
		return codcoligad;
	}
	public void setCodcoligad(String codcoligad) {
		this.codcoligad = codcoligad;
	}

	
}
