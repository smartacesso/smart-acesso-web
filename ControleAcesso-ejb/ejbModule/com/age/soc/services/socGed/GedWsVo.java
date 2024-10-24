
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for gedWsVo complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="gedWsVo"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="codigoEmpresa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="tipoBuscaEmpresa" type="{http://services.soc.age.com/}tipoBuscaEmpresaEnum" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="codigoTipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="codigoUnidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="dataCriacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="dataValidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="observacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="revisao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gedWsVo", propOrder = {
    "codigoEmpresa",
    "tipoBuscaEmpresa",
    "codigo",
    "nome",
    "codigoTipo",
    "codigoUnidade",
    "dataCriacao",
    "dataValidade",
    "observacao",
    "revisao"
})
public class GedWsVo {

    protected String codigoEmpresa;
    @XmlSchemaType(name = "string")
    protected TipoBuscaEmpresaEnum tipoBuscaEmpresa;
    protected String codigo;
    protected String nome;
    protected String codigoTipo;
    protected String codigoUnidade;
    protected String dataCriacao;
    protected String dataValidade;
    protected String observacao;
    protected String revisao;

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
     * Gets the value of the codigo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Sets the value of the codigo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigo(String value) {
        this.codigo = value;
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

}
