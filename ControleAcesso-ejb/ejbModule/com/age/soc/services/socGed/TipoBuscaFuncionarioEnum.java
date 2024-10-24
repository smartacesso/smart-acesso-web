
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for tipoBuscaFuncionarioEnum.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * &lt;pre&gt;
 * &amp;lt;simpleType name="tipoBuscaFuncionarioEnum"&amp;gt;
 *   &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *     &amp;lt;enumeration value="CODIGO"/&amp;gt;
 *     &amp;lt;enumeration value="MATRICULA"/&amp;gt;
 *     &amp;lt;enumeration value="CPF_ATIVO"/&amp;gt;
 *   &amp;lt;/restriction&amp;gt;
 * &amp;lt;/simpleType&amp;gt;
 * &lt;/pre&gt;
 * 
 */
@XmlType(name = "tipoBuscaFuncionarioEnum")
@XmlEnum
public enum TipoBuscaFuncionarioEnum {

    CODIGO,
    MATRICULA,
    CPF_ATIVO;

    public String value() {
        return name();
    }

    public static TipoBuscaFuncionarioEnum fromValue(String v) {
        return valueOf(v);
    }

}
