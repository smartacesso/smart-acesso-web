package com.protreino.luxandserver.util;

import java.security.MessageDigest;

import org.springframework.security.crypto.password.PasswordEncoder;

public class MyPasswordEncoder implements PasswordEncoder{

	public String encode(CharSequence rawPassword) {
		
		try {
			
			MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
			byte messageDigest[] = algorithm.digest(rawPassword.toString().getBytes("UTF-8"));

			StringBuilder hexString = new StringBuilder();
			for (byte b : messageDigest) {
				hexString.append(String.format("%02X", 0xFF & b));
			}

			return hexString.toString();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return encodedPassword.equals(encode(rawPassword));
	}

}
