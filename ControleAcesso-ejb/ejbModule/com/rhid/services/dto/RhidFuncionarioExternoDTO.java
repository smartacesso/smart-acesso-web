package com.rhid.services.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Representa um funcionário obtido de API externa (não persistido como pedestre).
 * Campos opcionais são enviados ao RHID quando preenchidos.
 */
public class RhidFuncionarioExternoDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Identificador na fonte externa (controle de sync). */
	private String idExterno;
	private String nome;
	private String cpf;
	private String pis;
	private String matricula;
	private String email;
	private Integer status;
	private Date dataAdmissao;
	private Date dataAlteracao;

	/** Empresa dentro do domínio RHID (campo idCompany do payload). Não identifica o domínio. */
	private Integer idCompany;
	/** Resolve idCompany via catálogo RHID (company.svc/a) — busca somente por CNPJ. */
	private String cnpjEmpresa;
	/** Nome do domínio RHID (parâmetro domain do login). Define para qual domínio exportar. */
	private String nomeDominio;

	private Integer idShift;
	/** Resolve idShift via catálogo RHID (shift.svc/a). */
	private String nomeHorario;
	private Integer idDepartment;
	/** Resolve idDepartment via catálogo RHID (department.svc/a). */
	private String nomeDepartamento;
	private Integer idCostCenter;
	/** Resolve idCostCenter via catálogo RHID (costcenter.svc/a). */
	private String nomeCentroCusto;
	private Integer idPersonRole;
	/** Resolve idPersonRole via catálogo RHID (personrole.svc/a). */
	private String nomeCargo;
	private String idPersonBoss;
	private String numFolha;
	private String ctps;
	private String rg;
	private Date dataNascimento;
	private String cidade;
	private String endereco;
	private String bairro;
	private String uf;
	private String cep;
	private String telefone;
	private Boolean admin;

	public String getIdExterno() {
		return idExterno;
	}

	public void setIdExterno(String idExterno) {
		this.idExterno = idExterno;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getPis() {
		return pis;
	}

	public void setPis(String pis) {
		this.pis = pis;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getDataAdmissao() {
		return dataAdmissao;
	}

	public void setDataAdmissao(Date dataAdmissao) {
		this.dataAdmissao = dataAdmissao;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public Integer getIdCompany() {
		return idCompany;
	}

	public void setIdCompany(Integer idCompany) {
		this.idCompany = idCompany;
	}

	public String getCnpjEmpresa() {
		return cnpjEmpresa;
	}

	public void setCnpjEmpresa(String cnpjEmpresa) {
		this.cnpjEmpresa = cnpjEmpresa;
	}

	public String getNomeDominio() {
		return nomeDominio;
	}

	public void setNomeDominio(String nomeDominio) {
		this.nomeDominio = nomeDominio;
	}

	public Integer getIdShift() {
		return idShift;
	}

	public void setIdShift(Integer idShift) {
		this.idShift = idShift;
	}

	public String getNomeHorario() {
		return nomeHorario;
	}

	public void setNomeHorario(String nomeHorario) {
		this.nomeHorario = nomeHorario;
	}

	public Integer getIdDepartment() {
		return idDepartment;
	}

	public void setIdDepartment(Integer idDepartment) {
		this.idDepartment = idDepartment;
	}

	public String getNomeDepartamento() {
		return nomeDepartamento;
	}

	public void setNomeDepartamento(String nomeDepartamento) {
		this.nomeDepartamento = nomeDepartamento;
	}

	public Integer getIdCostCenter() {
		return idCostCenter;
	}

	public void setIdCostCenter(Integer idCostCenter) {
		this.idCostCenter = idCostCenter;
	}

	public String getNomeCentroCusto() {
		return nomeCentroCusto;
	}

	public void setNomeCentroCusto(String nomeCentroCusto) {
		this.nomeCentroCusto = nomeCentroCusto;
	}

	public Integer getIdPersonRole() {
		return idPersonRole;
	}

	public void setIdPersonRole(Integer idPersonRole) {
		this.idPersonRole = idPersonRole;
	}

	public String getNomeCargo() {
		return nomeCargo;
	}

	public void setNomeCargo(String nomeCargo) {
		this.nomeCargo = nomeCargo;
	}

	public String getIdPersonBoss() {
		return idPersonBoss;
	}

	public void setIdPersonBoss(String idPersonBoss) {
		this.idPersonBoss = idPersonBoss;
	}

	public String getNumFolha() {
		return numFolha;
	}

	public void setNumFolha(String numFolha) {
		this.numFolha = numFolha;
	}

	public String getCtps() {
		return ctps;
	}

	public void setCtps(String ctps) {
		this.ctps = ctps;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
}
