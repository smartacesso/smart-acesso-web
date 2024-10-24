
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for tipoBuscaEmpresaEnum.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * &lt;pre&gt;
 * &amp;lt;simpleType name="tipoBuscaEmpresaEnum"&amp;gt;
 *   &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *     &amp;lt;enumeration value="CODIGO_SOC"/&amp;gt;
 *     &amp;lt;enumeration value="CODIGO_CLIENTE"/&amp;gt;
 *   &amp;lt;/restriction&amp;gt;
 * &amp;lt;/simpleType&amp;gt;
 * &lt;/pre&gt;
 * 
 */
@XmlType(name = "tipoBuscaEmpresaEnum")
@XmlEnum
public enum TipoBuscaEmpresaEnum {

    CODIGO_SOC,
    CODIGO_CLIENTE;

    public String value() {
        return name();
    }

    public static TipoBuscaEmpresaEnum fromValue(String v) {
        return valueOf(v);
    }

}
