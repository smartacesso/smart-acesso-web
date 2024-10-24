package com.protreino.luxandserver;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.protreino.luxandserver.util.AppTrayIcon;
import com.protreino.luxandserver.util.Utils;


@SpringBootApplication
public class BackendJavaApplication {

	public static final Logger logger = LoggerFactory.getLogger(BackendJavaApplication.class);
	
	public static ConfigurableApplicationContext context;

	public static void main(String[] args) {

		logger.info("Iniciando BackendJavaApplication...");
		
		if(isRunning(8080)) {
			System.exit(0);
			return;
		}
		
		System.setProperty("spring.devtools.restart.enabled", "false");
		System.setProperty("derby.system.home", new File(Utils.getAppDataFolder() + "/logs").getAbsolutePath());

		SpringApplication.run(BackendJavaApplication.class, args);
		
		try {
			Thread.sleep(10000);
		}catch (Exception e) {
			
		}
		
	}
	
	private static boolean isRunning(int port) {
		try (Socket s = new Socket("localhost", port);) {
			// If the code makes it this far without an exception it means
			// something is using the port and has responded.
			return true;
			
		} catch (IOException e) {
			return false;
		} 
	}

}
