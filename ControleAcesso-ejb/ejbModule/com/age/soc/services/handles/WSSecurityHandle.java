package com.age.soc.services.handles;

import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class WSSecurityHandle implements SOAPHandler<SOAPMessageContext> {
	
	//2014-06-18T20:20:44.623Z
	private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss.sss'Z'");
	private Calendar c = null;
	
	private static final SecureRandom RANDOM;
	
	private static final String MESSAGE_DIGEST_ALGORITHM_NAME_SHA_1 = "SHA-1";
    private static final String SECURE_RANDOM_ALGORITHM_SHA_1_PRNG = "SHA1PRNG";
	
	static {
        try {
            RANDOM = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM_SHA_1_PRNG);
            RANDOM.setSeed(currentTimeMillis());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
	
	
    public boolean handleMessage(SOAPMessageContext smc) {

        Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outboundProperty.booleanValue()) {

            SOAPMessage message = smc.getMessage();

            try {
            	
            	c = Calendar.getInstance();
            	sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

                SOAPEnvelope envelope = smc.getMessage().getSOAPPart().getEnvelope();
                SOAPHeader header = envelope.addHeader();

                SOAPElement security =
                        header.addChildElement("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
                security.addAttribute(new QName("xmlns:wsu"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
                
                createTimestampNode(security);

                createUserNameTokenNode(security);

                //Print out the outbound SOAP message to System.out
                message.writeTo(System.out);
                System.out.println("");
                
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try {
                
                //This handler does nothing with the response from the Web Service so
                //we just print out the SOAP message.
                SOAPMessage message = smc.getMessage();
                message.writeTo(System.out);
                System.out.println("");

            } catch (Exception ex) {
                ex.printStackTrace();
            } 
        }


        return outboundProperty;

    }

	private void createUserNameTokenNode(SOAPElement security)
			throws SOAPException, NoSuchAlgorithmException, UnsupportedEncodingException, DatatypeConfigurationException {
		SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
		usernameToken.addAttribute(new QName("xmlns:wsu"),
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
		usernameToken.addAttribute(new QName("wsu:Id"), "32E6B34377D7E4A58614607283662221");

		SOAPElement username = usernameToken.addChildElement("Username", "wsse");
		username.addTextNode("312493");// 312493

//		byte [] nonceBytes = EncryptionUtils.getRandomPassword().getBytes();
//	    String passwordStr = "f98a0c25f9";
//	    String dateTime = sdf.format(c.getTime());

		final byte[] nonceBytes = generateNonce();
        XMLGregorianCalendar dateTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(Instant.now().toString());
        String passwordStr = "f98a0c25f9";

        final byte[] passwordDigestBytes = constructPasswordDigest(nonceBytes, dateTime, passwordStr);

        final Encoder base64Encoder = Base64.getEncoder();
        final String nonceBase64Encoded = base64Encoder.encodeToString(nonceBytes);
        final String passwordDigestBase64Encoded = base64Encoder.encodeToString(passwordDigestBytes);
        System.out.println(String.format("nonce: [%s], password digest: [%s]", nonceBase64Encoded, passwordDigestBase64Encoded));


		SOAPElement password = usernameToken.addChildElement("Password", "wsse");
		password.setAttribute("Type",
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest");
		password.addTextNode(passwordDigestBase64Encoded);

		// Nonce
		SOAPElement nonce = usernameToken.addChildElement("Nonce", "wsse");
		nonce.setAttribute("EncodingType",
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
		nonce.addTextNode(nonceBase64Encoded);

		// Created
		SOAPElement created = usernameToken.addChildElement("Created", "wsu");
		created.addTextNode(dateTime.toString());
	}
	
	private byte[] generateNonce() {
		byte[] nonceBytes = new byte[16];
        RANDOM.nextBytes(nonceBytes);
        return nonceBytes;
	}
	
	private static byte[] constructPasswordDigest(byte[] nonceBytes, XMLGregorianCalendar createdDate, String password) {
        try {
            final MessageDigest sha1MessageDigest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM_NAME_SHA_1);
            sha1MessageDigest.update(nonceBytes);
            final String createdDateAsString = createdDate.toString();
            sha1MessageDigest.update(createdDateAsString.getBytes(UTF_8));
            sha1MessageDigest.update(password.getBytes(UTF_8));
        
            return sha1MessageDigest.digest();
       
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
	
	private void createTimestampNode(SOAPElement security) throws SOAPException {
		SOAPElement timestamp =
		        security.addChildElement("Timestamp", "wsu");
		timestamp.addAttribute(new QName("xmlns:wsu"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
		timestamp.addAttribute(new QName("wsu:Id"), "TS-32E6B34377D7E4A58614607283662392");
		
		    SOAPElement created =
		    		timestamp.addChildElement("Created", "wsu");
		    created.addTextNode(sdf.format(c.getTime()));
		    
		    c.add(Calendar.MINUTE, 2);
		    
		    SOAPElement expires =
		    		timestamp.addChildElement("Expires", "wsu");
		    expires.addTextNode(sdf.format(c.getTime()));
	}

    public Set getHeaders() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return null;
    }

    public boolean handleFault(SOAPMessageContext context) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return true;
    }

    public void close(MessageContext context) {
    //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static class HeaderHandlerResolver implements HandlerResolver {
        
    	public List getHandlerChain(PortInfo portInfo) {
    	      List handlerChain = new ArrayList();
    	      WSSecurityHandle hh = new WSSecurityHandle();
    	      handlerChain.add(hh);
    	      return handlerChain;
    	   }
    	}

}
