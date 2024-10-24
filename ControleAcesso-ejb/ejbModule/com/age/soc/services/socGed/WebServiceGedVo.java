
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for webServiceGedVo complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="webServiceGedVo"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="gedWsVo" type="{http://services.soc.age.com/}gedWsVo" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="identificacaoWsVo" type="{http://services.soc.age.com/}identificacaoUsuarioWsVo" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "webServiceGedVo", propOrder = {
    "gedWsVo",
    "identificacaoWsVo"
})
public class WebServiceGedVo {

    protected GedWsVo gedWsVo;
    protected IdentificacaoUsuarioWsVo identificacaoWsVo;

    /**
     * Gets the value of the gedWsVo property.
     * 
     * @return
     *     possible object is
     *     {@link GedWsVo }
     *     
     */
    public GedWsVo getGedWsVo() {
        return gedWsVo;
    }

    /**
     * Sets the value of the gedWsVo property.
     * 
     * @param value
     *     allowed object is
     *     {@link GedWsVo }
     *     
     */
    public void setGedWsVo(GedWsVo value) {
        this.gedWsVo = value;
    }

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

}
