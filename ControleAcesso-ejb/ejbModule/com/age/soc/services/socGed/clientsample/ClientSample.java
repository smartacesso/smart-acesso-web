package com.age.soc.services.socGed.clientsample;

import com.age.soc.services.socGed.*;

public class ClientSample {

	public static void main(String[] args) {
		
		try {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        UploadArquivosWsService service1 = new UploadArquivosWsService();
	        System.out.println("Create Web Service...");
	        UploadArquivosWs port1 = service1.getUploadArquivosWsPort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.uploadArquivo(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.updateGed(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        UploadArquivosWs port2 = service1.getUploadArquivosWsPort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.uploadArquivo(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.updateGed(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
}
