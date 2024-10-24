
package com.age.soc.services.exportaDados;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.age.soc.services.exportaDados package. 
 * &lt;p&gt;An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ExportaDadosWs_QNAME = new QName("http://services.soc.age.com/", "exportaDadosWs");
    private final static QName _ExportaDadosWsResponse_QNAME = new QName("http://services.soc.age.com/", "exportaDadosWsResponse");
    private final static QName _WSException_QNAME = new QName("http://services.soc.age.com/", "WSException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.age.soc.services.exportaDados
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExportaDadosWs_Type }
     * 
     */
    public ExportaDadosWs_Type createExportaDadosWs_Type() {
        return new ExportaDadosWs_Type();
    }

    /**
     * Create an instance of {@link ExportaDadosWsResponse }
     * 
     */
    public ExportaDadosWsResponse createExportaDadosWsResponse() {
        return new ExportaDadosWsResponse();
    }

    /**
     * Create an instance of {@link WSException }
     * 
     */
    public WSException createWSException() {
        return new WSException();
    }

    /**
     * Create an instance of {@link ExportaDadosWsVo }
     * 
     */
    public ExportaDadosWsVo createExportaDadosWsVo() {
        return new ExportaDadosWsVo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportaDadosWs_Type }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ExportaDadosWs_Type }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.soc.age.com/", name = "exportaDadosWs")
    public JAXBElement<ExportaDadosWs_Type> createExportaDadosWs(ExportaDadosWs_Type value) {
        return new JAXBElement<ExportaDadosWs_Type>(_ExportaDadosWs_QNAME, ExportaDadosWs_Type.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportaDadosWsResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ExportaDadosWsResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.soc.age.com/", name = "exportaDadosWsResponse")
    public JAXBElement<ExportaDadosWsResponse> createExportaDadosWsResponse(ExportaDadosWsResponse value) {
        return new JAXBElement<ExportaDadosWsResponse>(_ExportaDadosWsResponse_QNAME, ExportaDadosWsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WSException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link WSException }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.soc.age.com/", name = "WSException")
    public JAXBElement<WSException> createWSException(WSException value) {
        return new JAXBElement<WSException>(_WSException_QNAME, WSException.class, null, value);
    }

}
