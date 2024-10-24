
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for uploadArquivosWsVo complex type.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="uploadArquivosWsVo"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="arquivo" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="classificacao" type="{http://services.soc.age.com/}tipoClassificacaoUploadArquivoGedWs" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="codigoEmpresa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="codigoFuncionario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="codigoGed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="codigoSequencialFicha" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="codigoTipoGed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="extensaoArquivo" type="{http://services.soc.age.com/}regrasArquivosGed" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="identificacaoVo" type="{http://services.soc.age.com/}identificacaoUsuarioWsVo" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="nomeArquivo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="nomeGed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="nomeTipoGed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="sobreescreveArquivo" type="{http://www.w3.org/2001/XMLSchema}boolean"/&amp;gt;
 *         &amp;lt;element name="codigoUnidadeGed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="dataValidadeGed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="revisaoGed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="string01" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadArquivosWsVo", propOrder = {
    "arquivo",
    "classificacao",
    "codigoEmpresa",
    "codigoFuncionario",
    "codigoGed",
    "codigoSequencialFicha",
    "codigoTipoGed",
    "extensaoArquivo",
    "identificacaoVo",
    "nomeArquivo",
    "nomeGed",
    "nomeTipoGed",
    "sobreescreveArquivo",
    "codigoUnidadeGed",
    "dataValidadeGed",
    "revisaoGed",
    "string01"
})
public class UploadArquivosWsVo {

    protected byte[] arquivo;
    @XmlSchemaType(name = "string")
    protected TipoClassificacaoUploadArquivoGedWs classificacao;
    protected String codigoEmpresa;
    protected String codigoFuncionario;
    protected String codigoGed;
    protected String codigoSequencialFicha;
    protected String codigoTipoGed;
    @XmlSchemaType(name = "string")
    protected RegrasArquivosGed extensaoArquivo;
    protected IdentificacaoUsuarioWsVo identificacaoVo;
    protected String nomeArquivo;
    protected String nomeGed;
    protected String nomeTipoGed;
    protected boolean sobreescreveArquivo;
    protected String codigoUnidadeGed;
    protected String dataValidadeGed;
    protected String revisaoGed;
    protected String string01;

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
     * Gets the value of the classificacao property.
     * 
     * @return
     *     possible object is
     *     {@link TipoClassificacaoUploadArquivoGedWs }
     *     
     */
    public TipoClassificacaoUploadArquivoGedWs getClassificacao() {
        return classificacao;
    }

    /**
     * Sets the value of the classificacao property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoClassificacaoUploadArquivoGedWs }
     *     
     */
    public void setClassificacao(TipoClassificacaoUploadArquivoGedWs value) {
        this.classificacao = value;
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
     * Gets the value of the codigoFuncionario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoFuncionario() {
        return codigoFuncionario;
    }

    /**
     * Sets the value of the codigoFuncionario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoFuncionario(String value) {
        this.codigoFuncionario = value;
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

    /**
     * Gets the value of the codigoSequencialFicha property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoSequencialFicha() {
        return codigoSequencialFicha;
    }

    /**
     * Sets the value of the codigoSequencialFicha property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoSequencialFicha(String value) {
        this.codigoSequencialFicha = value;
    }

    /**
     * Gets the value of the codigoTipoGed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoTipoGed() {
        return codigoTipoGed;
    }

    /**
     * Sets the value of the codigoTipoGed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoTipoGed(String value) {
        this.codigoTipoGed = value;
    }

    /**
     * Gets the value of the extensaoArquivo property.
     * 
     * @return
     *     possible object is
     *     {@link RegrasArquivosGed }
     *     
     */
    public RegrasArquivosGed getExtensaoArquivo() {
        return extensaoArquivo;
    }

    /**
     * Sets the value of the extensaoArquivo property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegrasArquivosGed }
     *     
     */
    public void setExtensaoArquivo(RegrasArquivosGed value) {
        this.extensaoArquivo = value;
    }

    /**
     * Gets the value of the identificacaoVo property.
     * 
     * @return
     *     possible object is
     *     {@link IdentificacaoUsuarioWsVo }
     *     
     */
    public IdentificacaoUsuarioWsVo getIdentificacaoVo() {
        return identificacaoVo;
    }

    /**
     * Sets the value of the identificacaoVo property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentificacaoUsuarioWsVo }
     *     
     */
    public void setIdentificacaoVo(IdentificacaoUsuarioWsVo value) {
        this.identificacaoVo = value;
    }

    /**
     * Gets the value of the nomeArquivo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeArquivo() {
        return nomeArquivo;
    }

    /**
     * Sets the value of the nomeArquivo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeArquivo(String value) {
        this.nomeArquivo = value;
    }

    /**
     * Gets the value of the nomeGed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeGed() {
        return nomeGed;
    }

    /**
     * Sets the value of the nomeGed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeGed(String value) {
        this.nomeGed = value;
    }

    /**
     * Gets the value of the nomeTipoGed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeTipoGed() {
        return nomeTipoGed;
    }

    /**
     * Sets the value of the nomeTipoGed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeTipoGed(String value) {
        this.nomeTipoGed = value;
    }

    /**
     * Gets the value of the sobreescreveArquivo property.
     * 
     */
    public boolean isSobreescreveArquivo() {
        return sobreescreveArquivo;
    }

    /**
     * Sets the value of the sobreescreveArquivo property.
     * 
     */
    public void setSobreescreveArquivo(boolean value) {
        this.sobreescreveArquivo = value;
    }

    /**
     * Gets the value of the codigoUnidadeGed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoUnidadeGed() {
        return codigoUnidadeGed;
    }

    /**
     * Sets the value of the codigoUnidadeGed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoUnidadeGed(String value) {
        this.codigoUnidadeGed = value;
    }

    /**
     * Gets the value of the dataValidadeGed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataValidadeGed() {
        return dataValidadeGed;
    }

    /**
     * Sets the value of the dataValidadeGed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataValidadeGed(String value) {
        this.dataValidadeGed = value;
    }

    /**
     * Gets the value of the revisaoGed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRevisaoGed() {
        return revisaoGed;
    }

    /**
     * Sets the value of the revisaoGed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRevisaoGed(String value) {
        this.revisaoGed = value;
    }

    /**
     * Gets the value of the string01 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getString01() {
        return string01;
    }

    /**
     * Sets the value of the string01 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setString01(String value) {
        this.string01 = value;
    }

}
