package br.com.smartacesso.pedestre.model.entity;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Pedestre extends RealmObject implements Serializable, Cloneable  {

    @PrimaryKey
    private Long id;
    private String nome;
    private String login;
    private String senha;
    private String token;

    private Date dataCriacao;
    private Date dataAlteracao;

    private String status;
    private String email;
    private String cpf;
    private String rg;
    private String telefone;
    private String celular;
    private String matricula;
    private String codigoCartaoAcesso;
    private String observacoes;
    private String responsavel;
    private String tipoQRCode;
    private String qrCodeParaAcesso;
    private Boolean sempreLiberado;
    private Boolean habilitarTeclado;
    private Boolean cadastroFacialObrigatorio;
    private Date ultimasFotosTiradas;
    private Boolean fotosForamExcluidas;
    private byte[] foto;
    private Date dataAlteracaoFoto = new Date();
    private Date dataNascimento;
    private String tipo;
    private String genero;
    private String luxandIdentifier;
    private String tipoAcesso;

    private String tempoRenovacaoQRCode;
    private String unidade;

    private CadastroExterno facialAtual;

    public String getTempoRenovacaoQRCode() {
        return tempoRenovacaoQRCode;
    }

    public void setTempoRenovacaoQRCode(String tempoRenovacaoQRCode) {
        this.tempoRenovacaoQRCode = tempoRenovacaoQRCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCodigoCartaoAcesso() {
        return codigoCartaoAcesso;
    }

    public void setCodigoCartaoAcesso(String codigoCartaoAcesso) {
        this.codigoCartaoAcesso = codigoCartaoAcesso;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getTipoQRCode() {
        return tipoQRCode;
    }

    public void setTipoQRCode(String tipoQRCode) {
        this.tipoQRCode = tipoQRCode;
    }

    public String getQrCodeParaAcesso() {
        return qrCodeParaAcesso;
    }

    public void setQrCodeParaAcesso(String qrCodeParaAcesso) {
        this.qrCodeParaAcesso = qrCodeParaAcesso;
    }

    public Boolean getSempreLiberado() {
        return sempreLiberado;
    }

    public void setSempreLiberado(Boolean sempreLiberado) {
        this.sempreLiberado = sempreLiberado;
    }

    public Boolean getHabilitarTeclado() {
        return habilitarTeclado;
    }

    public void setHabilitarTeclado(Boolean habilitarTeclado) {
        this.habilitarTeclado = habilitarTeclado;
    }

    public Boolean getCadastroFacialObrigatorio() {
        return cadastroFacialObrigatorio;
    }

    public void setCadastroFacialObrigatorio(Boolean cadastroFacialObrigatorio) {
        this.cadastroFacialObrigatorio = cadastroFacialObrigatorio;
    }

    public Date getUltimasFotosTiradas() {
        return ultimasFotosTiradas;
    }

    public void setUltimasFotosTiradas(Date ultimasFotosTiradas) {
        this.ultimasFotosTiradas = ultimasFotosTiradas;
    }

    public Boolean getFotosForamExcluidas() {
        return fotosForamExcluidas;
    }

    public void setFotosForamExcluidas(Boolean fotosForamExcluidas) {
        this.fotosForamExcluidas = fotosForamExcluidas;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public Date getDataAlteracaoFoto() {
        return dataAlteracaoFoto;
    }

    public void setDataAlteracaoFoto(Date dataAlteracaoFoto) {
        this.dataAlteracaoFoto = dataAlteracaoFoto;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getLuxandIdentifier() {
        return luxandIdentifier;
    }

    public void setLuxandIdentifier(String luxandIdentifier) {
        this.luxandIdentifier = luxandIdentifier;
    }

    public String getTipoAcesso() {
        return tipoAcesso;
    }

    public void setTipoAcesso(String tipoAcesso) {
        this.tipoAcesso = tipoAcesso;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public CadastroExterno getFacialAtual() {
        return facialAtual;
    }

    public void setFacialAtual(CadastroExterno facialAtual) {
        this.facialAtual = facialAtual;
    }
}
