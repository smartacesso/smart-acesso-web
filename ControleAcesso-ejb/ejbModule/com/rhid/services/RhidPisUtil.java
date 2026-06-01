package com.rhid.services;

import com.rhid.services.dto.RhidFuncionarioExternoDTO;

/**
 * Regras de composição do PIS enviado ao RHID.
 */
public final class RhidPisUtil {

	private RhidPisUtil() {
	}

	/**
	 * Retorna o PIS informado ou, se ausente, {@code 9 + CPF} (12 dígitos numéricos).
	 */
	public static long resolverPisNumerico(RhidFuncionarioExternoDTO funcionario) {
		String pis = somenteNumeros(funcionario.getPis());
		if (pis != null && !pis.isEmpty()) {
			return Long.parseLong(pis);
		}

		String cpf = normalizarCpf11(funcionario.getCpf());
		if (cpf == null) {
			throw new IllegalArgumentException("CPF obrigatório quando PIS não informado.");
		}
		return Long.parseLong("9" + cpf);
	}

	public static boolean possuiPisOuCpf(RhidFuncionarioExternoDTO funcionario) {
		String pis = somenteNumeros(funcionario.getPis());
		if (pis != null && !pis.isEmpty()) {
			return true;
		}
		return normalizarCpf11(funcionario.getCpf()) != null;
	}

	public static String normalizarCpf11(String cpf) {
		String numeros = somenteNumeros(cpf);
		if (numeros == null || numeros.isEmpty()) {
			return null;
		}
		if (numeros.length() < 11) {
			numeros = String.format("%011d", Long.parseLong(numeros));
		} else if (numeros.length() > 11) {
			numeros = numeros.substring(0, 11);
		}
		return numeros.length() == 11 ? numeros : null;
	}

	private static String somenteNumeros(String valor) {
		if (valor == null) {
			return null;
		}
		return valor.replaceAll("\\D", "");
	}
}
