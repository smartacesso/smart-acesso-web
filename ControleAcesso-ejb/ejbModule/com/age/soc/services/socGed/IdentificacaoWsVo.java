
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for identificacaoWsVo complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="identificacaoWsVo"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="chaveAcesso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="codigoEmpresaPrincipal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="codigoResponsavel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="homologacao" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "identificacaoWsVo", propOrder = {
    "chaveAcesso",
    "codigoEmpresaPrincipal",
    "codigoResponsavel",
    "homologacao"
})
@XmlSeeAlso({
    IdentificacaoUsuarioWsVo.class
})
public class IdentificacaoWsVo {

    protected String chaveAcesso;
    protected String codigoEmpresaPrincipal;
    protected String codigoResponsavel;
    protected boolean homologacao;

    /**
     * Gets the value of the chaveAcesso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChaveAcesso() {
        return chaveAcesso;
    }

    /**
     * Sets the value of the chaveAcesso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChaveAcesso(String value) {
        this.chaveAcesso = value;
    }

    /**
     * Gets the value of the codigoEmpresaPrincipal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoEmpresaPrincipal() {
        return codigoEmpresaPrincipal;
    }

    /**
     * Sets the value of the codigoEmpresaPrincipal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoEmpresaPrincipal(String value) {
        this.codigoEmpresaPrincipal = value;
    }

    /**
     * Gets the value of the codigoResponsavel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoResponsavel() {
        return codigoResponsavel;
    }

    /**
     * Sets the value of the codigoResponsavel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoResponsavel(String value) {
        this.codigoResponsavel = value;
    }

    /**
     * Gets the value of the homologacao property.
     * 
     */
    public boolean isHomologacao() {
        return homologacao;
    }

    /**
     * Sets the value of the homologacao property.
     * 
     */
    public void setHomologacao(boolean value) {
        this.homologacao = value;
    }

}
