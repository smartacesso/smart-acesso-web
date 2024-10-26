package br.com.startjob.acesso.modelo.entity;

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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;

@SuppressWarnings("serial")
@Entity
@Table(name="TB_RESPONSIBLE")
@NamedQueries({
	@NamedQuery(name= "ResponsibleEntity.findAll",
			query = "select obj "
				      + "from ResponsibleEntity obj "
				      + "order by obj.nome "),
	@NamedQuery(name= "ResponsibleEntity.findAllNaoRemovido",
			query = "select obj "
				      + "from ResponsibleEntity obj "
				      + "where (obj.removido = false or obj.removido is null) "
				      + "order by obj.nome"),
	@NamedQuery(name  = "ResponsibleEntity.findById", 
			query = "select obj from ResponsibleEntity obj "
				  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name= "ResponsibleEntity.findByLoginAndPassword",
			query = "select obj "
				      + "from ResponsibleEntity obj "
					  + "where (lower(obj.login) = lower(:LOGIN) and obj.password = :PASSWORD) "),
	@NamedQuery(name= "ResponsibleEntity.findAllDependentsPageable",
			query = "select obj.pedestre "
                    + "from ResponsibleEntity obj "
                    + "left join obj.pedestre p "
                    + "where obj.id = :ID_RESPONSIBLE "),
	@NamedQuery(name = "ResponsibleEntity.findByIdComplete", 
			query = "select obj from ResponsibleEntity obj "
					+ "left join fetch obj.pedestre p "
					+ "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name = "ResponsibleEntity.findAllResponsaveis",
	query = "select obj from ResponsibleEntity obj "
		  + "where (obj.removido = false or obj.removido is null) "
		  + "order by obj.id asc")
})
public class ResponsibleEntity extends ClienteBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_RESPONSIBLE", nullable=false, length=4)
	private Long id;
	
	@Column(name="NOME", nullable=true, length=100)
	private String nome;
	
	@Column(name="EMAIL", nullable=true, length=100)
	private String email;
	
	@Column(name="CPF", nullable=true, length=100)
	private String cpf;
	
	@Enumerated(EnumType.STRING)
	@Column(name="STATUS", nullable=true, length=100)
	private Status status;
	
	@Column(name="RG", nullable=true, length=100)
	private String rg;
	
	@Column(name="CELULAR", nullable=true, length=100)
	private String celular;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_NASCIMENTO", nullable=true, length=11)
	private Date dataNascimento;
	
	@Column(name="LOGIN", nullable=true, length=100)
	private String login;
	
	@Column(name="PASSWORD", nullable=true, length=100)
	private String password;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	private List<PedestreEntity> pedestre;
	
	@Column(name="DESCRICAO", nullable=true, length=255)
	private String descricao;
	
	public ResponsibleEntity () {
	}
	
	public ResponsibleEntity(Long id, String nome, Date dataNascimento, String login, String password,
			List<PedestreEntity> pedestre) {
		super();
		this.id = id;
		this.nome = nome;
		this.dataNascimento = dataNascimento;
		this.login = login;
		this.password = password;
		this.pedestre = pedestre;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<PedestreEntity> getPedestre() {
		return pedestre;
	}
	public void setPedestre(List<PedestreEntity> pedestre) {
		this.pedestre = pedestre;
	}
	public Long getId() {
		return id;
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
