package br.com.startjob.acesso.modelo.entity;

import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.enumeration.Status;

@Entity
@Table(name="TB_USUARIO")
@NamedQueries({
	@NamedQuery(name  = "UsuarioEntity.findAll", 
				query = "select obj "
				      + "from UsuarioEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "UsuarioEntity.findById", 
				query = "select obj from UsuarioEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "UsuarioEntity.findByLogin", 
				query = "select obj from UsuarioEntity obj "
					  + " left join fetch obj.cliente c "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "	and lower(c.nomeUnidadeOrganizacional) = lower(:UNIDADE_ORGANIZACIONAL) "
					  + "	and obj.login = :LOGIN "),
	@NamedQuery(name  = "UsuarioEntity.findByAllStatusLoginPass", 
				query = "select obj from UsuarioEntity obj "
					  + " left join fetch obj.cliente c "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "	   and lower(c.nomeUnidadeOrganizacional) = lower(:UNIDADE_ORGANIZACIONAL) "
					  + "	   and obj.login = :LOGIN and obj.senha = :SENHA "),
	@NamedQuery(name =  "UsuarioEntity.findByIdComplete", 
				query = "select obj from UsuarioEntity obj "
					  + " left join fetch obj.endereco e "
					  + " where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "UsuarioEntity.findByEmail", 
				query = "select obj from UsuarioEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "	   and obj.email = :EMAIL "),
	@NamedQuery(name = "UsuarioEntity.findAllByIdCliente",
				query = "select obj from UsuarioEntity obj "
					  + "where obj.cliente.id = :ID_CLIENTE "
					  + "and obj.dataAlteracao >= :LAST_SYNC "
					  + "and (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name = "UsuarioEntity.findAllWithRemovidosByIdCliente",
				query = "select obj from UsuarioEntity obj "
					  + "where obj.cliente.id = :ID_CLIENTE "
					  + "and obj.dataAlteracao >= :LAST_SYNC "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "UsuarioEntity.findByIdComConfiguracoes", 
				query = "select obj from UsuarioEntity obj "
					  + " left join fetch obj.configuracoesDesktop e "
					  + " left join fetch obj.cliente c "
					  + "where obj.id = :ID_USER order by obj.id asc"),
	
})
@SuppressWarnings("serial")
public class UsuarioEntity extends ClienteBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_USUARIO", nullable=false, length=4)
	private Long id;
	
	@Column(name="NOME", nullable=true, length=255)
	private String nome;
	
	@Enumerated(EnumType.STRING)
	@Column(name="STATUS", nullable=true, length=100)
	private Status status;

	@Column(name="LOGIN", nullable=true, length=100)
	private String login;
	
	@Column(name="SENHA", nullable=true, length=255)
	private String senha;
	
	@Column(name="EMAIL", nullable=true, length=100)
	private String email;
	
	@Column(name="CPF", nullable=true, length=20)
	private String cpf;
	
	@Column(name="RG", nullable=true, length=20)
	private String rg;
	
	@Column(name="TELEFONE", nullable=true, length=20)
	private String telefone;
	
	@Column(name="CELULAR", nullable=true, length=20)
	private String celular;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_NASCIMENTO", nullable=true, length=11)
	private Date dataNascimento;
	
	@ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_ENDERECO", nullable=true)
	private EnderecoEntity endereco;
	
	@Enumerated(EnumType.STRING)
	@Column(name="PERFIL", nullable=true, length=20)
	private PerfilAcesso perfil;
	
	@Column(name="TOKEN", nullable=true, length=100)
	private String token;
	
	@ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_CONFIGURACAO_DESKTOP", nullable=true)
	private ConfiguracoesDesktopEntity configuracoesDesktop;
	
	@Column(name="ACESSA_WEB", nullable=true)
	private Boolean acessaWeb = true;
	
	@Column(name="USUARIO_MASTER", nullable=true)
	private Boolean usuarioMaster = false;
	
	@Column(name="HABILITA_EXPEDIDORA", nullable=true)
	private Boolean expedidora = false;
	
	@Column(name="REDEFINIR_SENHA", nullable=false)
	private Boolean redefinirSenha= false;
	
	@Transient
	private String chaveIntegracaoComtele;
	
	@Transient
	private Integer qtdePadraoDigitosCartao;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public EnderecoEntity getEndereco() {
		return endereco;
	}
	public void setEndereco(EnderecoEntity endereco) {
		this.endereco = endereco;
	}
	public PerfilAcesso getPerfil() {
		return perfil;
	}
	public void setPerfil(PerfilAcesso perfil) {
		this.perfil = perfil;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Integer getQtdePadraoDigitosCartao() {
		return qtdePadraoDigitosCartao;
	}
	public void setQtdePadraoDigitosCartao(Integer qtdePadraoDigitosCartao) {
		this.qtdePadraoDigitosCartao = qtdePadraoDigitosCartao;
	}
	public String getChaveIntegracaoComtele() {
		return chaveIntegracaoComtele;
	}
	public void setChaveIntegracaoComtele(String chaveIntegracaoComtele) {
		this.chaveIntegracaoComtele = chaveIntegracaoComtele;
	}
	public ConfiguracoesDesktopEntity getConfiguracoesDesktop() {
		return configuracoesDesktop;
	}
	public void setConfiguracoesDesktop(ConfiguracoesDesktopEntity configuracoesDesktop) {
		this.configuracoesDesktop = configuracoesDesktop;
	}
	public Boolean getAcessaWeb() {
		if(acessaWeb == null)
			return true;
		return acessaWeb;
	}
	public void setAcessaWeb(Boolean acessaWeb) {
		this.acessaWeb = acessaWeb;
	}
	public Boolean getExpedidora() {
		return expedidora;
	}
	public void setExpedidora(Boolean expedidora) {
		this.expedidora = expedidora;
	}
	public Boolean getRedefinirSenha() {
		return redefinirSenha;
	}
	public void setRedefinirSenha(Boolean redefinirSenha) {
		this.redefinirSenha = redefinirSenha;
	}
	
}
