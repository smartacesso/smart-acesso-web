package com.age.soc.services.socGed;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.3.6
 * 2022-02-17T10:06:30.662-02:00
 * Generated source version: 3.3.6
 *
 */
@WebService(targetNamespace = "http://services.soc.age.com/", name = "UploadArquivosWs")
@XmlSeeAlso({ObjectFactory.class})
public interface UploadArquivosWs {

    @WebMethod
    @RequestWrapper(localName = "uploadArquivo", targetNamespace = "http://services.soc.age.com/", className = "com.age.soc.services.socGed.UploadArquivo")
    @ResponseWrapper(localName = "uploadArquivoResponse", targetNamespace = "http://services.soc.age.com/", className = "com.age.soc.services.socGed.UploadArquivoResponse")
    @WebResult(name = "return", targetNamespace = "")
    public boolean uploadArquivo(

        @WebParam(name = "arg0", targetNamespace = "")
        com.age.soc.services.socGed.UploadArquivosWsVo arg0
    ) throws WSException_Exception;

    @WebMethod
    @RequestWrapper(localName = "updateGed", targetNamespace = "http://services.soc.age.com/", className = "com.age.soc.services.socGed.UpdateGed")
    @ResponseWrapper(localName = "updateGedResponse", targetNamespace = "http://services.soc.age.com/", className = "com.age.soc.services.socGed.UpdateGedResponse")
    @WebResult(name = "return", targetNamespace = "")
    public com.age.soc.services.socGed.GedWsRetornoVo updateGed(

        @WebParam(name = "socged", targetNamespace = "")
        com.age.soc.services.socGed.WebServiceGedVo socged
    );
}
