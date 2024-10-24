
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for gedInclusaoWsVo complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="gedInclusaoWsVo"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="codigoEmpresa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="tipoBuscaEmpresa" type="{http://services.soc.age.com/}tipoBuscaEmpresaEnum" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="codigoEmpresaSocnet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string"/&amp;gt;
 *         &amp;lt;element name="codigoTipo" type="{http://www.w3.org/2001/XMLSchema}string"/&amp;gt;
 *         &amp;lt;element name="codigoUnidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="funcionario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="tipoBuscaFuncionario" type="{http://services.soc.age.com/}tipoBuscaFuncionarioEnum" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="mandatoCipa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="exportaDados" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="cadastroDinamico" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="registroCadastroDinamico" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="turma" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="fichaClinica" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="dataCriacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="dataValidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="revisao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="observacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gedInclusaoWsVo", propOrder = {
    "codigoEmpresa",
    "tipoBuscaEmpresa",
    "codigoEmpresaSocnet",
    "nome",
    "codigoTipo",
    "codigoUnidade",
    "funcionario",
    "tipoBuscaFuncionario",
    "mandatoCipa",
    "exportaDados",
    "cadastroDinamico",
    "registroCadastroDinamico",
    "turma",
    "fichaClinica",
    "dataCriacao",
    "dataValidade",
    "revisao",
    "observacao"
})
public class GedInclusaoWsVo {

    protected String codigoEmpresa;
    @XmlSchemaType(name = "string")
    protected TipoBuscaEmpresaEnum tipoBuscaEmpresa;
    protected String codigoEmpresaSocnet;
    @XmlElement(required = true)
    protected String nome;
    @XmlElement(required = true)
    protected String codigoTipo;
    protected String codigoUnidade;
    protected String funcionario;
    @XmlSchemaType(name = "string")
    protected TipoBuscaFuncionarioEnum tipoBuscaFuncionario;
    protected String mandatoCipa;
    protected String exportaDados;
    protected String cadastroDinamico;
    protected String registroCadastroDinamico;
    protected String turma;
    protected String fichaClinica;
    protected String dataCriacao;
    protected String dataValidade;
    protected String revisao;
    protected String observacao;

    /**
     * Gets the value of the codigoEmpresa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoEmpresa() {
        return codigoEmpresa;
    }

    /**
     * Sets the value of the codigoEmpresa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoEmpresa(String value) {
        this.codigoEmpresa = value;
    }

    /**
     * Gets the value of the tipoBuscaEmpresa property.
     * 
     * @return
     *     possible object is
     *     {@link TipoBuscaEmpresaEnum }
     *     
     */
    public TipoBuscaEmpresaEnum getTipoBuscaEmpresa() {
        return tipoBuscaEmpresa;
    }

    /**
     * Sets the value of the tipoBuscaEmpresa property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoBuscaEmpresaEnum }
     *     
     */
    public void setTipoBuscaEmpresa(TipoBuscaEmpresaEnum value) {
        this.tipoBuscaEmpresa = value;
    }

    /**
     * Gets the value of the codigoEmpresaSocnet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoEmpresaSocnet() {
        return codigoEmpresaSocnet;
    }

    /**
     * Sets the value of the codigoEmpresaSocnet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoEmpresaSocnet(String value) {
        this.codigoEmpresaSocnet = value;
    }

    /**
     * Gets the value of the nome property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNome() {
        return nome;
    }

    /**
     * Sets the value of the nome property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNome(String value) {
        this.nome = value;
    }

    /**
     * Gets the value of the codigoTipo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoTipo() {
        return codigoTipo;
    }

    /**
     * Sets the value of the codigoTipo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoTipo(String value) {
        this.codigoTipo = value;
    }

    /**
     * Gets the value of the codigoUnidade property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    /**
     * Sets the value of the codigoUnidade property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoUnidade(String value) {
        this.codigoUnidade = value;
    }

    /**
     * Gets the value of the funcionario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFuncionario() {
        return funcionario;
    }

    /**
     * Sets the value of the funcionario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFuncionario(String value) {
        this.funcionario = value;
    }

    /**
     * Gets the value of the tipoBuscaFuncionario property.
     * 
     * @return
     *     possible object is
     *     {@link TipoBuscaFuncionarioEnum }
     *     
     */
    public TipoBuscaFuncionarioEnum getTipoBuscaFuncionario() {
        return tipoBuscaFuncionario;
    }

    /**
     * Sets the value of the tipoBuscaFuncionario property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoBuscaFuncionarioEnum }
     *     
     */
    public void setTipoBuscaFuncionario(TipoBuscaFuncionarioEnum value) {
        this.tipoBuscaFuncionario = value;
    }

    /**
     * Gets the value of the mandatoCipa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMandatoCipa() {
        return mandatoCipa;
    }

    /**
     * Sets the value of the mandatoCipa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMandatoCipa(String value) {
        this.mandatoCipa = value;
    }

    /**
     * Gets the value of the exportaDados property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExportaDados() {
        return exportaDados;
    }

    /**
     * Sets the value of the exportaDados property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExportaDados(String value) {
        this.exportaDados = value;
    }

    /**
     * Gets the value of the cadastroDinamico property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCadastroDinamico() {
        return cadastroDinamico;
    }

    /**
     * Sets the value of the cadastroDinamico property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCadastroDinamico(String value) {
        this.cadastroDinamico = value;
    }

    /**
     * Gets the value of the registroCadastroDinamico property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistroCadastroDinamico() {
        return registroCadastroDinamico;
    }

    /**
     * Sets the value of the registroCadastroDinamico property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistroCadastroDinamico(String value) {
        this.registroCadastroDinamico = value;
    }

    /**
     * Gets the value of the turma property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTurma() {
        return turma;
    }

    /**
     * Sets the value of the turma property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTurma(String value) {
        this.turma = value;
    }

    /**
     * Gets the value of the fichaClinica property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFichaClinica() {
        return fichaClinica;
    }

    /**
     * Sets the value of the fichaClinica property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFichaClinica(String value) {
        this.fichaClinica = value;
    }

    /**
     * Gets the value of the dataCriacao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataCriacao() {
        return dataCriacao;
    }

    /**
     * Sets the value of the dataCriacao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataCriacao(String value) {
        this.dataCriacao = value;
    }

    /**
     * Gets the value of the dataValidade property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataValidade() {
        return dataValidade;
    }

    /**
     * Sets the value of the dataValidade property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataValidade(String value) {
        this.dataValidade = value;
    }

    /**
     * Gets the value of the revisao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRevisao() {
        return revisao;
    }

    /**
     * Sets the value of the revisao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRevisao(String value) {
        this.revisao = value;
    }

    /**
     * Gets the value of the observacao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObservacao() {
        return observacao;
    }

    /**
     * Sets the value of the observacao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObservacao(String value) {
        this.observacao = value;
    }

}
