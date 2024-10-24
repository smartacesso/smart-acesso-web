
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for gedExcluirWsVo complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="gedExcluirWsVo"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="identificacaoWsVo" type="{http://services.soc.age.com/}identificacaoUsuarioWsVo"/&amp;gt;
 *         &amp;lt;element name="codigoEmpresa" type="{http://www.w3.org/2001/XMLSchema}string"/&amp;gt;
 *         &amp;lt;element name="tipoBuscaEmpresa" type="{http://services.soc.age.com/}tipoBuscaEmpresaEnum" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="codigoGed" type="{http://www.w3.org/2001/XMLSchema}string"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gedExcluirWsVo", propOrder = {
    "identificacaoWsVo",
    "codigoEmpresa",
    "tipoBuscaEmpresa",
    "codigoGed"
})
public class GedExcluirWsVo {

    @XmlElement(required = true)
    protected IdentificacaoUsuarioWsVo identificacaoWsVo;
    @XmlElement(required = true)
    protected String codigoEmpresa;
    @XmlSchemaType(name = "string")
    protected TipoBuscaEmpresaEnum tipoBuscaEmpresa;
    @XmlElement(required = true)
    protected String codigoGed;

    /**
     * Gets the value of the identificacaoWsVo property.
     * 
     * @return
     *     possible object is
     *     {@link IdentificacaoUsuarioWsVo }
     *     
     */
    public IdentificacaoUsuarioWsVo getIdentificacaoWsVo() {
        return identificacaoWsVo;
    }

    /**
     * Sets the value of the identificacaoWsVo property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentificacaoUsuarioWsVo }
     *     
     */
    public void setIdentificacaoWsVo(IdentificacaoUsuarioWsVo value) {
        this.identificacaoWsVo = value;
    }

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
     * Gets the value of the codigoGed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoGed() {
        return codigoGed;
    }

    /**
     * Sets the value of the codigoGed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoGed(String value) {
        this.codigoGed = value;
    }

}
