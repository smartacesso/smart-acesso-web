package br.com.startjob.acesso.utils;

/**
 * Mascaramento de dados sensíveis para exibição na web.
 */
public final class DadosSensiveisUtil {

	public static final String CPF_MASCARADO = "XXX.XXX.XXX-XX";
	public static final String RG_MASCARADO = "XX-XXXXXXXX";
	public static final String EMAIL_MASCARADO = "XXXXX@XXXXX";
	public static final String TELEFONE_MASCARADO = "(XX) XXXXX-XXXX";
	public static final String CELULAR_MASCARADO = "(XX) X XXXXX-XXXX";

	private DadosSensiveisUtil() {
	}

	public static String cpfExibicao(String cpf, boolean podeVer) {
		if (!podeVer) {
			return CPF_MASCARADO;
		}
		return formatarCpf(cpf);
	}

	public static String rgExibicao(String rg, boolean podeVer) {
		if (!podeVer) {
			return RG_MASCARADO;
		}
		return rg != null ? rg : "";
	}

	public static String emailExibicao(String email, boolean podeVer) {
		if (!podeVer) {
			return EMAIL_MASCARADO;
		}
		return email != null ? email : "";
	}

	public static String telefoneExibicao(String telefone, boolean podeVer) {
		if (!podeVer) {
			return TELEFONE_MASCARADO;
		}
		return telefone != null ? telefone : "";
	}

	public static String formatarCpf(String cpf) {
		if (cpf == null) {
			return "";
		}
		String v = cpf.replaceAll("\\D", "");
		if (v.length() != 11) {
			return cpf;
		}
		return v.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
	}
}
