package com.age.soc.services.socGed;

import java.net.MalformedURLException;
import java.net.URL;

import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.BindingType;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 3.3.6
 * 2022-02-17T10:06:30.672-02:00
 * Generated source version: 3.3.6
 *
 */
@WebServiceClient(name = "UploadArquivosWsService",
                  wsdlLocation = "https://ws1.soc.com.br/WSSoc/services/UploadArquivosWs?wsdl",
                  targetNamespace = "http://services.soc.age.com/")
@SOAPBinding(
	    style=SOAPBinding.Style.DOCUMENT,
	    use=SOAPBinding.Use.LITERAL,
	    parameterStyle=SOAPBinding.ParameterStyle.WRAPPED
	    )
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
@MTOM (enabled = true, threshold = 0)
public class UploadArquivosWsService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://services.soc.age.com/", "UploadArquivosWsService");
    public final static QName UploadArquivosWsPort = new QName("http://services.soc.age.com/", "UploadArquivosWsPort");
    static {
        URL url = null;
        try {
            url = new URL("https://ws1.soc.com.br/WSSoc/services/UploadArquivosWs?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(UploadArquivosWsService.class.getName())
                .log(java.util.logging.Level.INFO,
                     "Can not initialize the default wsdl from {0}", "https://ws1.soc.com.br/WSSoc/services/UploadArquivosWs?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public UploadArquivosWsService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public UploadArquivosWsService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public UploadArquivosWsService() {
        super(WSDL_LOCATION, SERVICE);
    }

    public UploadArquivosWsService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public UploadArquivosWsService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public UploadArquivosWsService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }




    /**
     *
     * @return
     *     returns UploadArquivosWs
     */
    @WebEndpoint(name = "UploadArquivosWsPort")
    public UploadArquivosWs getUploadArquivosWsPort() {
        return super.getPort(UploadArquivosWsPort, UploadArquivosWs.class);
    }

    /**
     *
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns UploadArquivosWs
     */
    @WebEndpoint(name = "UploadArquivosWsPort")
    public UploadArquivosWs getUploadArquivosWsPort(WebServiceFeature... features) {
        return super.getPort(UploadArquivosWsPort, UploadArquivosWs.class, features);
    }

}
