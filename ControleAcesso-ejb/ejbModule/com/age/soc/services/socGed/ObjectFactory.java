
package com.age.soc.services.socGed;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.age.soc.services.socGed package. 
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

    private final static QName _UpdateGed_QNAME = new QName("http://services.soc.age.com/", "updateGed");
    private final static QName _UpdateGedResponse_QNAME = new QName("http://services.soc.age.com/", "updateGedResponse");
    private final static QName _UploadArquivo_QNAME = new QName("http://services.soc.age.com/", "uploadArquivo");
    private final static QName _UploadArquivoResponse_QNAME = new QName("http://services.soc.age.com/", "uploadArquivoResponse");
    private final static QName _WSException_QNAME = new QName("http://services.soc.age.com/", "WSException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.age.soc.services.socGed
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UpdateGed }
     * 
     */
    public UpdateGed createUpdateGed() {
        return new UpdateGed();
    }

    /**
     * Create an instance of {@link UpdateGedResponse }
     * 
     */
    public UpdateGedResponse createUpdateGedResponse() {
        return new UpdateGedResponse();
    }

    /**
     * Create an instance of {@link UploadArquivo }
     * 
     */
    public UploadArquivo createUploadArquivo() {
        return new UploadArquivo();
    }

    /**
     * Create an instance of {@link UploadArquivoResponse }
     * 
     */
    public UploadArquivoResponse createUploadArquivoResponse() {
        return new UploadArquivoResponse();
    }

    /**
     * Create an instance of {@link WSException }
     * 
     */
    public WSException createWSException() {
        return new WSException();
    }

    /**
     * Create an instance of {@link UploadArquivosWsVo }
     * 
     */
    public UploadArquivosWsVo createUploadArquivosWsVo() {
        return new UploadArquivosWsVo();
    }

    /**
     * Create an instance of {@link IdentificacaoUsuarioWsVo }
     * 
     */
    public IdentificacaoUsuarioWsVo createIdentificacaoUsuarioWsVo() {
        return new IdentificacaoUsuarioWsVo();
    }

    /**
     * Create an instance of {@link IdentificacaoWsVo }
     * 
     */
    public IdentificacaoWsVo createIdentificacaoWsVo() {
        return new IdentificacaoWsVo();
    }

    /**
     * Create an instance of {@link WebServiceGedVo }
     * 
     */
    public WebServiceGedVo createWebServiceGedVo() {
        return new WebServiceGedVo();
    }

    /**
     * Create an instance of {@link GedWsVo }
     * 
     */
    public GedWsVo createGedWsVo() {
        return new GedWsVo();
    }

    /**
     * Create an instance of {@link GedWsRetornoVo }
     * 
     */
    public GedWsRetornoVo createGedWsRetornoVo() {
        return new GedWsRetornoVo();
    }

    /**
     * Create an instance of {@link WebServiceInfoGeralVo }
     * 
     */
    public WebServiceInfoGeralVo createWebServiceInfoGeralVo() {
        return new WebServiceInfoGeralVo();
    }

    /**
     * Create an instance of {@link WebServiceInfoOperacaoDetalheVo }
     * 
     */
    public WebServiceInfoOperacaoDetalheVo createWebServiceInfoOperacaoDetalheVo() {
        return new WebServiceInfoOperacaoDetalheVo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateGed }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UpdateGed }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.soc.age.com/", name = "updateGed")
    public JAXBElement<UpdateGed> createUpdateGed(UpdateGed value) {
        return new JAXBElement<UpdateGed>(_UpdateGed_QNAME, UpdateGed.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateGedResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UpdateGedResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.soc.age.com/", name = "updateGedResponse")
    public JAXBElement<UpdateGedResponse> createUpdateGedResponse(UpdateGedResponse value) {
        return new JAXBElement<UpdateGedResponse>(_UpdateGedResponse_QNAME, UpdateGedResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UploadArquivo }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UploadArquivo }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.soc.age.com/", name = "uploadArquivo")
    public JAXBElement<UploadArquivo> createUploadArquivo(UploadArquivo value) {
        return new JAXBElement<UploadArquivo>(_UploadArquivo_QNAME, UploadArquivo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UploadArquivoResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UploadArquivoResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://services.soc.age.com/", name = "uploadArquivoResponse")
    public JAXBElement<UploadArquivoResponse> createUploadArquivoResponse(UploadArquivoResponse value) {
        return new JAXBElement<UploadArquivoResponse>(_UploadArquivoResponse_QNAME, UploadArquivoResponse.class, null, value);
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
