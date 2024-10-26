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
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc")
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
	
	@OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_INTEGRACAO_SOC", nullable=true)
	private IntegracaoSOCEntity integracaoSoc;

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
	
	
}
