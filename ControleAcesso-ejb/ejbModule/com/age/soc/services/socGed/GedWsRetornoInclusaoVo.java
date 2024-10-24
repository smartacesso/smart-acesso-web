
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for GedWsRetornoInclusaoVo complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="GedWsRetornoInclusaoVo"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="gedWsVo" type="{http://services.soc.age.com/}gedInclusaoWsVo" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="informacaoGeral" type="{http://services.soc.age.com/}webServiceInfoGeralVo" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GedWsRetornoInclusaoVo", propOrder = {
    "codigo",
    "gedWsVo",
    "informacaoGeral"
})
public class GedWsRetornoInclusaoVo {

    protected String codigo;
    protected GedInclusaoWsVo gedWsVo;
    protected WebServiceInfoGeralVo informacaoGeral;

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
     * Gets the value of the gedWsVo property.
     * 
     * @return
     *     possible object is
     *     {@link GedInclusaoWsVo }
     *     
     */
    public GedInclusaoWsVo getGedWsVo() {
        return gedWsVo;
    }

    /**
     * Sets the value of the gedWsVo property.
     * 
     * @param value
     *     allowed object is
     *     {@link GedInclusaoWsVo }
     *     
     */
    public void setGedWsVo(GedInclusaoWsVo value) {
        this.gedWsVo = value;
    }

    /**
     * Gets the value of the informacaoGeral property.
     * 
     * @return
     *     possible object is
     *     {@link WebServiceInfoGeralVo }
     *     
     */
    public WebServiceInfoGeralVo getInformacaoGeral() {
        return informacaoGeral;
    }

    /**
     * Sets the value of the informacaoGeral property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebServiceInfoGeralVo }
     *     
     */
    public void setInformacaoGeral(WebServiceInfoGeralVo value) {
        this.informacaoGeral = value;
    }

}
