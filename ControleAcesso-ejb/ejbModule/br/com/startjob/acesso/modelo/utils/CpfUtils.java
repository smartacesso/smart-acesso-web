package br.com.startjob.acesso.modelo.utils;

public final class CpfUtils {

    // Private constructor prevents instantiation of the utility class
    private CpfUtils() {}

    public static String normalizarCpfSeguro(String cpf) {
        // Checks for null and handles strings that are just spaces
        if (cpf == null || cpf.trim().isEmpty()) {
            return null;
        }

        String apenasNumeros = cpf.replaceAll("\\D", "");

        // Prevents returning "00000000000" if the input had no numbers at all
        if (apenasNumeros.isEmpty()) {
            return null; 
        }

        if (apenasNumeros.length() == 11) {
            return apenasNumeros;
        }

        // Se tiver mais de 11, pega os últimos 11 (caso comum em integrações)
        if (apenasNumeros.length() > 11) {
            return apenasNumeros.substring(apenasNumeros.length() - 11);
        }

        // Se tiver menos, completa com zeros à esquerda
        return String.format("%11s", apenasNumeros).replace(' ', '0');
    }
}