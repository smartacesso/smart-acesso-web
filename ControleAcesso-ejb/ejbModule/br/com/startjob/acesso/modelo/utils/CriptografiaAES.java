package br.com.startjob.acesso.modelo.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CriptografiaAES {
	private final String IV = "0000000000000000";
	private final String chaveencriptacao = "0123456789abcdef";

	public String encript(String token) {
		byte[] textoencriptado = null;
		try {
			textoencriptado = encrypt(token, chaveencriptacao);
			return Base64.getEncoder().encodeToString(textoencriptado);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String decript(String textoCriptografado) throws Exception {
		byte[] decoded = Base64.getDecoder().decode(textoCriptografado);
		String decript  =  decrypt(decoded, chaveencriptacao);
		return decript;
	}

	public byte[] encrypt(String textopuro, String chaveencriptacao) throws Exception {
		Cipher encripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		SecretKeySpec key = new SecretKeySpec(chaveencriptacao.getBytes("UTF-8"), "AES");
		encripta.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
		return encripta.doFinal(textopuro.getBytes("UTF-8"));
	}

	public String decrypt(byte[] textoencriptado, String chaveencriptacao) throws Exception {
		Cipher decripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		SecretKeySpec key = new SecretKeySpec(chaveencriptacao.getBytes("UTF-8"), "AES");
		decripta.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
		return new String(decripta.doFinal(textoencriptado), "UTF-8");
	}

	public static void main(String[] args) throws Exception {
	String teste = "TokenOutput [idResponsible=" + 1 + ", login=" + 1 + ", senha=" + 2 + "]";
		
		CriptografiaAES criptografiaAES = new CriptografiaAES();
		byte[] encrypt = criptografiaAES.encrypt(teste, criptografiaAES.chaveencriptacao);
		
		String encoded = java.util.Base64.getEncoder().encodeToString(encrypt);
		System.out.println("Resposta: " + encoded);
		byte[] decoded = java.util.Base64.getDecoder().decode(encoded);
		
		String decript = criptografiaAES.decrypt(decoded, criptografiaAES.chaveencriptacao);
		
		System.out.println("Decript: " + decript);
	}

}
