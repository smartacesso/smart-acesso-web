
package com.age.soc.services.socGed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for webServiceInfoGeralVo complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="webServiceInfoGeralVo"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="codigoMensagem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="mensagem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="mensagemOperacaoDetalheList" type="{http://services.soc.age.com/}webServiceInfoOperacaoDetalheVo" maxOccurs="unbounded" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="numeroErros" type="{http://www.w3.org/2001/XMLSchema}int"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "webServiceInfoGeralVo", propOrder = {
    "codigoMensagem",
    "mensagem",
    "mensagemOperacaoDetalheList",
    "numeroErros"
})
public class WebServiceInfoGeralVo {

    protected String codigoMensagem;
    protected String mensagem;
    @XmlElement(nillable = true)
    protected List<WebServiceInfoOperacaoDetalheVo> mensagemOperacaoDetalheList;
    protected int numeroErros;

    /**
     * Gets the value of the codigoMensagem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoMensagem() {
        return codigoMensagem;
    }

    /**
     * Sets the value of the codigoMensagem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoMensagem(String value) {
        this.codigoMensagem = value;
    }

    /**
     * Gets the value of the mensagem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMensagem() {
        return mensagem;
    }

    /**
     * Sets the value of the mensagem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMensagem(String value) {
        this.mensagem = value;
    }

    /**
     * Gets the value of the mensagemOperacaoDetalheList property.
     * 
     * &lt;p&gt;
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a &lt;CODE&gt;set&lt;/CODE&gt; method for the mensagemOperacaoDetalheList property.
     * 
     * &lt;p&gt;
     * For example, to add a new item, do as follows:
     * &lt;pre&gt;
     *    getMensagemOperacaoDetalheList().add(newItem);
     * &lt;/pre&gt;
     * 
     * 
     * &lt;p&gt;
     * Objects of the following type(s) are allowed in the list
     * {@link WebServiceInfoOperacaoDetalheVo }
     * 
     * 
     */
    public List<WebServiceInfoOperacaoDetalheVo> getMensagemOperacaoDetalheList() {
        if (mensagemOperacaoDetalheList == null) {
            mensagemOperacaoDetalheList = new ArrayList<WebServiceInfoOperacaoDetalheVo>();
        }
        return this.mensagemOperacaoDetalheList;
    }

    /**
     * Gets the value of the numeroErros property.
     * 
     */
    public int getNumeroErros() {
        return numeroErros;
    }

    /**
     * Sets the value of the numeroErros property.
     * 
     */
    public void setNumeroErros(int value) {
        this.numeroErros = value;
    }

}
