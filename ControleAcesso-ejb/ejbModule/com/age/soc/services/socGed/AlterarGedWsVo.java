
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for alterarGedWsVo complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="alterarGedWsVo"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="identificacaoWsVo" type="{http://services.soc.age.com/}identificacaoUsuarioWsVo"/&amp;gt;
 *         &amp;lt;element name="gedWsVo" type="{http://services.soc.age.com/}gedWsVo"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "alterarGedWsVo", propOrder = {
    "identificacaoWsVo",
    "gedWsVo"
})
public class AlterarGedWsVo {

    @XmlElement(required = true)
    protected IdentificacaoUsuarioWsVo identificacaoWsVo;
    @XmlElement(required = true)
    protected GedWsVo gedWsVo;

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

}
