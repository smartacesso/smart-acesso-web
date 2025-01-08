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
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Filter;

import com.senior.services.dto.EmpresaSeniorDto;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;

@Entity
@Table(name="TB_EMPRESA")
@NamedQueries({
	@NamedQuery(name  = "EmpresaEntity.findAll", 
				query = "select obj "
				      + "from EmpresaEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "EmpresaEntity.findById", 
				query = "select obj from EmpresaEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name =  "EmpresaEntity.findByIdComplete",
				query = "select obj from EmpresaEntity obj "
					  + " left join fetch obj.endereco e "
					  + " left join obj.departamentos d "
					  + " left join obj.centros ce "
					  + " left join obj.cargos ca "
					  + "where obj.id = :ID "
					  + "order by obj.id asc"),
	@NamedQuery(name = "EmpresaEntity.findAllByIdCliente",
				query = "select obj from EmpresaEntity obj "
					  + "  left join fetch obj.cliente c "
					  + " where c.id = :ID_CLIENTE "
					  + " and (obj.removido = false or obj.removido is null) "
					  + " order by obj.id asc"),
	@NamedQuery(name = "EmpresaEntity.findAllByIdCliente2",
				query = "select obj from EmpresaEntity obj "
					  + "where obj.cliente.id = :ID_CLIENTE "
					  + "and (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name = "EmpresaEntity.findByNomeEmpresa", 
				query = "select obj from EmpresaEntity obj "
					  + "where obj.nome = :NOME_EMPRESA "
					  + "and (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name = "EmpresaEntity.findAllByIdClienteAfterLastSync",
				query = "select obj from EmpresaEntity obj "
					  + "where obj.cliente.id = :ID_CLIENTE "
					  + "and obj.dataAlteracao >= :LAST_SYNC "
					  + "order by obj.id asc"),
	@NamedQuery(name = "EmpresaEntity.findByCodSeniorAndIdCliente",
				query = "select obj from EmpresaEntity obj "
					  + "left join fetch obj.cliente c "
					  + "where obj.codEmpresaSenior = :COD_EMPRESA_SENIOR "
					  + "and obj.cliente.id = :ID_CLIENTE "
					  + "order by obj.id asc")
})
@SuppressWarnings("serial")
public class EmpresaEntity extends ClienteBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_EMPRESA", nullable=false, length=4)
	private Long id;
	
	@Column(name="NOME", nullable=true, length=255)
	private String nome;
	
	@Column(name="CNPJ", nullable=true, length=50)
	private String cnpj;
	
	@Column(name="EMAIL", nullable=true, length=100)
	private String email;
	
	@Column(name="TELEFONE", nullable=true, length=20)
	private String telefone;
	
	@Column(name="CELULAR", nullable=true, length=20)
	private String celular;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name="STATUS", nullable=true, length=10)
	private Status status;
	
	@ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_ENDERECO", nullable=true)
	private EnderecoEntity endereco;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, 
			   orphanRemoval=false, targetEntity=DepartamentoEntity.class,
			   mappedBy="empresa")
	@Fetch(FetchMode.SUBSELECT)
	@Filter(name="departValido")
	private List<DepartamentoEntity> departamentos;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, 
			   orphanRemoval=false, targetEntity=CentroCustoEntity.class,
			   mappedBy="empresa")
	@Fetch(FetchMode.SUBSELECT)
	@Filter(name="centroValido")
	private List<CentroCustoEntity> centros;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, 
			   orphanRemoval=false, targetEntity=CargoEntity.class,
			   mappedBy="empresa")
	@Fetch(FetchMode.SUBSELECT)
	@Filter(name="cargoValido")
	private List<CargoEntity> cargos;
	
	@Column(name="CODIGO_EXTERNO", nullable=true, length=255)
	private String codigoExterno;
	
	@Column(name="COD_EMPRESA_SENIOR", nullable=true, length=40)
	private String codEmpresaSenior;
	
	@Column(name = "PRIM_IMPORT_FUNC_SENIOR_SUCESSO", nullable = true)
	private Boolean primeiroImportacaoFuncionarioSeniorSucesso;
	
	public EmpresaEntity() {
		
	}
	
	public EmpresaEntity(final EmpresaSeniorDto empresaSeniorDto, final Boolean primeiroImportacaoFuncionarioSeniorSucesso,
			final ClienteEntity cliente) {
		this.nome = empresaSeniorDto.getNomEmp();
		this.telefone = empresaSeniorDto.getDddTel() + empresaSeniorDto.getNumTel();
		this.cnpj = empresaSeniorDto.getNumIns();
		this.codEmpresaSenior = empresaSeniorDto.getNumEmp();
		this.email = empresaSeniorDto.getEmaEmp();
		this.primeiroImportacaoFuncionarioSeniorSucesso = primeiroImportacaoFuncionarioSeniorSucesso;
		this.status = Status.ATIVO;

		this.cliente = cliente;
	}
	
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
	public List<DepartamentoEntity> getDepartamentos() {
		return departamentos;
	}
	public void setDepartamentos(List<DepartamentoEntity> departamentos) {
		this.departamentos = departamentos;
	}
	public List<CentroCustoEntity> getCentros() {
		return centros;
	}
	public void setCentros(List<CentroCustoEntity> centros) {
		this.centros = centros;
	}
	public List<CargoEntity> getCargos() {
		return cargos;
	}
	public void setCargos(List<CargoEntity> cargos) {
		this.cargos = cargos;
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
	public String getCodigoExterno() {
		return codigoExterno;
	}
	public void setCodigoExterno(String codigoExterno) {
		this.codigoExterno = codigoExterno;
	}
	
	public Boolean getPrimeiroImportacaoFuncionarioSeniorSucesso() {
		return primeiroImportacaoFuncionarioSeniorSucesso;
	}
	public void setPrimeiroImportacaoFuncionarioSeniorSucesso(Boolean primeiroImportacaoFuncionarioSeniorSucesso) {
		this.primeiroImportacaoFuncionarioSeniorSucesso = primeiroImportacaoFuncionarioSeniorSucesso;
	}

	public String getCodEmpresaSenior() {
		return codEmpresaSenior;
	}

	public void setCodEmpresaSenior(String codEmpresaSenior) {
		this.codEmpresaSenior = codEmpresaSenior;
	}
	
}
