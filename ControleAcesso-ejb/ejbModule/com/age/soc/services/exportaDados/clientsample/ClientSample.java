package com.age.soc.services.exportaDados.clientsample;

import com.age.soc.services.exportaDados.*;

public class ClientSample {

	public static void main(String[] args) {
		
			try {
		
		        System.out.println("***********************");
		        System.out.println("Create Web Service Client...");
		        ExportaDadosWsService service1 = new ExportaDadosWsService();
		        System.out.println("Create Web Service...");
		        ExportaDadosWs port1 = service1.getExportaDadosWsPort();
		        System.out.println("Call Web Service Operation...");
		        System.out.println("Server said: " + port1.exportaDadosWs(null));
		        //Please input the parameters instead of 'null' for the upper method!
		
		        System.out.println("Create Web Service...");
		        ExportaDadosWs port2 = service1.getExportaDadosWsPort();
		        System.out.println("Call Web Service Operation...");
		        System.out.println("Server said: " + port2.exportaDadosWs(null));
		        //Please input the parameters instead of 'null' for the upper method!
		
		        System.out.println("***********************");
		        System.out.println("Call Over!");
	        
			}catch (Exception e) {
				// TODO: handle exception
			}
	}
}
