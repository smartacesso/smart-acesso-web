
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for alterarSocGed complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="alterarSocGed"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="socged" type="{http://services.soc.age.com/}alterarGedWsVo" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "alterarSocGed", propOrder = {
    "socged"
})
public class AlterarSocGed {

    protected AlterarGedWsVo socged;

    /**
     * Gets the value of the socged property.
     * 
     * @return
     *     possible object is
     *     {@link AlterarGedWsVo }
     *     
     */
    public AlterarGedWsVo getSocged() {
        return socged;
    }

    /**
     * Sets the value of the socged property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlterarGedWsVo }
     *     
     */
    public void setSocged(AlterarGedWsVo value) {
        this.socged = value;
    }

}
