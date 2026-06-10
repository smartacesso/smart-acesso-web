package br.com.startjob.acesso.utils;

import java.util.Base64;

public final class ImagemBase64Util {

	private ImagemBase64Util() {
	}

	public static byte[] decodificar(String imagemBase64) {
		if (imagemBase64 == null || imagemBase64.trim().isEmpty()) {
			return null;
		}
		String valor = imagemBase64.trim();
		int separador = valor.indexOf(',');
		if (valor.startsWith("data:") && separador > 0) {
			valor = valor.substring(separador + 1);
		}
		return Base64.getDecoder().decode(valor);
	}
}
