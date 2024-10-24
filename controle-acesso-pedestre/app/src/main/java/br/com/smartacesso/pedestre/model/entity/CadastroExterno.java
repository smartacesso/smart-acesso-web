package br.com.smartacesso.pedestre.model.entity;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CadastroExterno extends RealmObject implements Serializable, Cloneable {

    @PrimaryKey
    private Long id;
    private Long token;
    private String statusCadastroExterno;

    private Date dataCadastroDaFace;
    private Integer codigoResultadoProcessamento;
    private String descricaoResultadoProcessamento;
    private byte[] primeiraFoto;
    private byte[] segundaFoto;
    private byte[] terceiraFoto;
    private Integer imageWidth;
    private Integer imageHeight;

    private Date dataAlteracao = new Date();
    private Date dataCriacao = new Date();
    private Integer versao = 0;
    private Boolean removido = null;
    private Date dataRemovido = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getToken() {
        return token;
    }

    public void setToken(Long token) {
        this.token = token;
    }

    public String getStatusCadastroExterno() {
        return statusCadastroExterno;
    }

    public String getStatusCadastroExternoStr() {
        if("AGUARDANDO_CADASTRO".equals(statusCadastroExterno))
            return "AGUARDANDO CADASTRO";
        else if("CADASTRADO".equals(statusCadastroExterno))
            return "EM VALIDAÇÃO";
        return statusCadastroExterno;
    }

    public void setStatusCadastroExterno(String statusCadastroExterno) {
        this.statusCadastroExterno = statusCadastroExterno;
    }

    public Date getDataCadastroDaFace() {
        return dataCadastroDaFace;
    }

    public void setDataCadastroDaFace(Date dataCadastroDaFace) {
        this.dataCadastroDaFace = dataCadastroDaFace;
    }

    public Integer getCodigoResultadoProcessamento() {
        return codigoResultadoProcessamento;
    }

    public void setCodigoResultadoProcessamento(Integer codigoResultadoProcessamento) {
        this.codigoResultadoProcessamento = codigoResultadoProcessamento;
    }

    public String getDescricaoResultadoProcessamento() {
        return descricaoResultadoProcessamento;
    }

    public void setDescricaoResultadoProcessamento(String descricaoResultadoProcessamento) {
        this.descricaoResultadoProcessamento = descricaoResultadoProcessamento;
    }

    public byte[] getPrimeiraFoto() {
        return primeiraFoto;
    }

    public void setPrimeiraFoto(byte[] primeiraFoto) {
        this.primeiraFoto = primeiraFoto;
    }

    public byte[] getSegundaFoto() {
        return segundaFoto;
    }

    public void setSegundaFoto(byte[] segundaFoto) {
        this.segundaFoto = segundaFoto;
    }

    public byte[] getTerceiraFoto() {
        return terceiraFoto;
    }

    public void setTerceiraFoto(byte[] terceiraFoto) {
        this.terceiraFoto = terceiraFoto;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public Boolean getRemovido() {
        return removido;
    }

    public void setRemovido(Boolean removido) {
        this.removido = removido;
    }

    public Date getDataRemovido() {
        return dataRemovido;
    }

    public void setDataRemovido(Date dataRemovido) {
        this.dataRemovido = dataRemovido;
    }
}
