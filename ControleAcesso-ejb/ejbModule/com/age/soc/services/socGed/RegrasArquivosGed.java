
package com.age.soc.services.socGed;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for regrasArquivosGed.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.
 * &lt;pre&gt;
 * &amp;lt;simpleType name="regrasArquivosGed"&amp;gt;
 *   &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&amp;gt;
 *     &amp;lt;enumeration value="XLS"/&amp;gt;
 *     &amp;lt;enumeration value="RTF"/&amp;gt;
 *     &amp;lt;enumeration value="PDF"/&amp;gt;
 *     &amp;lt;enumeration value="RPT"/&amp;gt;
 *     &amp;lt;enumeration value="TXT"/&amp;gt;
 *     &amp;lt;enumeration value="CSV"/&amp;gt;
 *     &amp;lt;enumeration value="ZIP"/&amp;gt;
 *     &amp;lt;enumeration value="HTML"/&amp;gt;
 *     &amp;lt;enumeration value="XML"/&amp;gt;
 *     &amp;lt;enumeration value="REM"/&amp;gt;
 *     &amp;lt;enumeration value="DAT"/&amp;gt;
 *     &amp;lt;enumeration value="XLX"/&amp;gt;
 *     &amp;lt;enumeration value="XLSX"/&amp;gt;
 *     &amp;lt;enumeration value="JPEG"/&amp;gt;
 *     &amp;lt;enumeration value="BMP"/&amp;gt;
 *     &amp;lt;enumeration value="GIF"/&amp;gt;
 *     &amp;lt;enumeration value="JPG"/&amp;gt;
 *     &amp;lt;enumeration value="WMF"/&amp;gt;
 *     &amp;lt;enumeration value="PNG"/&amp;gt;
 *     &amp;lt;enumeration value="TIFF"/&amp;gt;
 *     &amp;lt;enumeration value="RAR"/&amp;gt;
 *     &amp;lt;enumeration value="DOC"/&amp;gt;
 *     &amp;lt;enumeration value="DOCX"/&amp;gt;
 *     &amp;lt;enumeration value="PPS"/&amp;gt;
 *     &amp;lt;enumeration value="PPT"/&amp;gt;
 *     &amp;lt;enumeration value="PPSX"/&amp;gt;
 *     &amp;lt;enumeration value="PBIX"/&amp;gt;
 *     &amp;lt;enumeration value="TIF"/&amp;gt;
 *     &amp;lt;enumeration value="MP4"/&amp;gt;
 *     &amp;lt;enumeration value="SETE_ZIP"/&amp;gt;
 *   &amp;lt;/restriction&amp;gt;
 * &amp;lt;/simpleType&amp;gt;
 * &lt;/pre&gt;
 * 
 */
@XmlType(name = "regrasArquivosGed")
@XmlEnum
public enum RegrasArquivosGed {

    XLS("XLS"),
    RTF("RTF"),
    PDF("PDF"),
    RPT("RPT"),
    TXT("TXT"),
    CSV("CSV"),
    ZIP("ZIP"),
    HTML("HTML"),
    XML("XML"),
    REM("REM"),
    DAT("DAT"),
    XLX("XLX"),
    XLSX("XLSX"),
    JPEG("JPEG"),
    BMP("BMP"),
    GIF("GIF"),
    JPG("JPG"),
    WMF("WMF"),
    PNG("PNG"),
    TIFF("TIFF"),
    RAR("RAR"),
    DOC("DOC"),
    DOCX("DOCX"),
    PPS("PPS"),
    PPT("PPT"),
    PPSX("PPSX"),
    PBIX("PBIX"),
    TIF("TIF"),
    @XmlEnumValue("MP4")
    MP_4("MP4"),
    SETE_ZIP("SETE_ZIP");
    private final String value;

    RegrasArquivosGed(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RegrasArquivosGed fromValue(String v) {
        for (RegrasArquivosGed c: RegrasArquivosGed.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
