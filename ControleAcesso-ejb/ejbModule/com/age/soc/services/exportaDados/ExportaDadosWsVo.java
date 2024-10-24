
package com.age.soc.services.exportaDados;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for exportaDadosWsVo complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="exportaDadosWsVo"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="arquivo" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="campoLivre1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="campoLivre2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="campoLivre3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="campoLivre4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="campoLivre5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="erro" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *         &amp;lt;element name="mensagemErro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="parametros" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="retorno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="tipoArquivoRetorno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exportaDadosWsVo", propOrder = {
    "arquivo",
    "campoLivre1",
    "campoLivre2",
    "campoLivre3",
    "campoLivre4",
    "campoLivre5",
    "erro",
    "mensagemErro",
    "parametros",
    "retorno",
    "tipoArquivoRetorno"
})
public class ExportaDadosWsVo {

    protected byte[] arquivo;
    protected String campoLivre1;
    protected String campoLivre2;
    protected String campoLivre3;
    protected String campoLivre4;
    protected String campoLivre5;
    protected boolean erro;
    protected String mensagemErro;
    protected String parametros;
    protected String retorno;
    protected String tipoArquivoRetorno;

    /**
     * Gets the value of the arquivo property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getArquivo() {
        return arquivo;
    }

    /**
     * Sets the value of the arquivo property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setArquivo(byte[] value) {
        this.arquivo = value;
    }

    /**
     * Gets the value of the campoLivre1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampoLivre1() {
        return campoLivre1;
    }

    /**
     * Sets the value of the campoLivre1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampoLivre1(String value) {
        this.campoLivre1 = value;
    }

    /**
     * Gets the value of the campoLivre2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampoLivre2() {
        return campoLivre2;
    }

    /**
     * Sets the value of the campoLivre2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampoLivre2(String value) {
        this.campoLivre2 = value;
    }

    /**
     * Gets the value of the campoLivre3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampoLivre3() {
        return campoLivre3;
    }

    /**
     * Sets the value of the campoLivre3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampoLivre3(String value) {
        this.campoLivre3 = value;
    }

    /**
     * Gets the value of the campoLivre4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampoLivre4() {
        return campoLivre4;
    }

    /**
     * Sets the value of the campoLivre4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampoLivre4(String value) {
        this.campoLivre4 = value;
    }

    /**
     * Gets the value of the campoLivre5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampoLivre5() {
        return campoLivre5;
    }

    /**
     * Sets the value of the campoLivre5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampoLivre5(String value) {
        this.campoLivre5 = value;
    }

    /**
     * Gets the value of the erro property.
     * 
     */
    public boolean isErro() {
        return erro;
    }

    /**
     * Sets the value of the erro property.
     * 
     */
    public void setErro(boolean value) {
        this.erro = value;
    }

    /**
     * Gets the value of the mensagemErro property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMensagemErro() {
        return mensagemErro;
    }

    /**
     * Sets the value of the mensagemErro property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMensagemErro(String value) {
        this.mensagemErro = value;
    }

    /**
     * Gets the value of the parametros property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParametros() {
        return parametros;
    }

    /**
     * Sets the value of the parametros property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParametros(String value) {
        this.parametros = value;
    }

    /**
     * Gets the value of the retorno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRetorno() {
        return retorno;
    }

    /**
     * Sets the value of the retorno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRetorno(String value) {
        this.retorno = value;
    }

    /**
     * Gets the value of the tipoArquivoRetorno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoArquivoRetorno() {
        return tipoArquivoRetorno;
    }

    /**
     * Sets the value of the tipoArquivoRetorno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoArquivoRetorno(String value) {
        this.tipoArquivoRetorno = value;
    }

}
