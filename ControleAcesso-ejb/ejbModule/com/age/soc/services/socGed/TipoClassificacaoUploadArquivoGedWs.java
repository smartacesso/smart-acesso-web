
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for tipoClassificacaoUploadArquivoGedWs.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * &lt;pre&gt;
 * &amp;lt;simpleType name="tipoClassificacaoUploadArquivoGedWs"&amp;gt;
 *   &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *     &amp;lt;enumeration value="GED"/&amp;gt;
 *     &amp;lt;enumeration value="ASO"/&amp;gt;
 *     &amp;lt;enumeration value="ASO_BRANCO"/&amp;gt;
 *     &amp;lt;enumeration value="FICHA_CLINICA"/&amp;gt;
 *     &amp;lt;enumeration value="FICHA_CLINICA_BRANCO"/&amp;gt;
 *     &amp;lt;enumeration value="PEDIDO_EXAME"/&amp;gt;
 *     &amp;lt;enumeration value="RESULTADO_EXAME"/&amp;gt;
 *     &amp;lt;enumeration value="ENFERMAGEM"/&amp;gt;
 *     &amp;lt;enumeration value="CONSULTA_ASSISTENCIAL"/&amp;gt;
 *     &amp;lt;enumeration value="LICENCA_MEDICA"/&amp;gt;
 *     &amp;lt;enumeration value="FICHA_PERSONALIZADA"/&amp;gt;
 *     &amp;lt;enumeration value="RECEITA_MEDICA"/&amp;gt;
 *     &amp;lt;enumeration value="ATESTADO"/&amp;gt;
 *     &amp;lt;enumeration value="GESTAO_FAP"/&amp;gt;
 *   &amp;lt;/restriction&amp;gt;
 * &amp;lt;/simpleType&amp;gt;
 * &lt;/pre&gt;
 * 
 */
@XmlType(name = "tipoClassificacaoUploadArquivoGedWs")
@XmlEnum
public enum TipoClassificacaoUploadArquivoGedWs {

    GED,
    ASO,
    ASO_BRANCO,
    FICHA_CLINICA,
    FICHA_CLINICA_BRANCO,
    PEDIDO_EXAME,
    RESULTADO_EXAME,
    ENFERMAGEM,
    CONSULTA_ASSISTENCIAL,
    LICENCA_MEDICA,
    FICHA_PERSONALIZADA,
    RECEITA_MEDICA,
    ATESTADO,
    GESTAO_FAP;

    public String value() {
        return name();
    }

    public static TipoClassificacaoUploadArquivoGedWs fromValue(String v) {
        return valueOf(v);
    }

}
