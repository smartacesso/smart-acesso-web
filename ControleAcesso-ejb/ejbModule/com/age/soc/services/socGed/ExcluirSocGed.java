
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for excluirSocGed complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="excluirSocGed"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="ExcluirGedWsVo" type="{http://services.soc.age.com/}gedExcluirWsVo" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "excluirSocGed", propOrder = {
    "excluirGedWsVo"
})
public class ExcluirSocGed {

    @XmlElement(name = "ExcluirGedWsVo")
    protected GedExcluirWsVo excluirGedWsVo;

    /**
     * Gets the value of the excluirGedWsVo property.
     * 
     * @return
     *     possible object is
     *     {@link GedExcluirWsVo }
     *     
     */
    public GedExcluirWsVo getExcluirGedWsVo() {
        return excluirGedWsVo;
    }

    /**
     * Sets the value of the excluirGedWsVo property.
     * 
     * @param value
     *     allowed object is
     *     {@link GedExcluirWsVo }
     *     
     */
    public void setExcluirGedWsVo(GedExcluirWsVo value) {
        this.excluirGedWsVo = value;
    }

}
