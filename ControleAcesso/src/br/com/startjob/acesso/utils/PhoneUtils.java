package br.com.startjob.acesso.utils;

public class PhoneUtils {
	
	public static String [] phoneSplited(String phoneString){
		
		//processa string do telefone
		String phone = phone(phoneString);
		
		//retorna ddd e telefone separados
		return phone.replace("(", "")
					.replace(")", "")
					.split(" ");
		
	}
	
	public static String phone(String phoneString) throws IllegalArgumentException {

	    String str = phoneString.replaceAll("[^\\d]", "");

	    if (str.charAt(0) == '0') {
	        if (str.charAt(1) == '0') {
	            str = str.substring(2);
	        } else {
	            str = str.substring(1);
	        }
	    }

	    switch (str.length()) {
	        case 8:
	            return applyMask(str, "AAAA-AAAA", 'A');
	        case 9:
	            return applyMask(str, "AAAAA-AAAA", 'A');
	        case 10:
	            return applyMask(str, "(AA) AAAA-AAAA", 'A');
	        case 11:
	            return applyMask(str, "(AA) AAAAA-AAAA", 'A');
	        default:
	            return str;
	    }
	}
	
	public static String applyMask(String str, String mask, char specialChar) {

	    // Conta quantos caracteres especiais existem na máscara
	    int maskChCount = mask.length() - mask.replaceAll("[^" + specialChar + "]", "").length();

	    // Conta apenas os números
	    int strChCount = str.length() - str.replaceAll("\\d", "").length();

	    // Exceção caso a string nao tenha números suficientes para competar a máscara
	    if (strChCount < maskChCount) {
	        throw new IllegalArgumentException("The number of chars in the string should not be smaller than the " +
	                "number of special chars in the mask");
	    }

	    char[] maskChars = mask.toCharArray();
	    char[] strChars = str.toCharArray();

	    // Itera por todos os elementos da máscara
	    for (int i = 0, j = 0; i < maskChars.length && j < strChars.length; i++) {
	        char ch = maskChars[i];
	        char sh = strChars[j];

	        if (ch == specialChar) {
	            // Se achou o caractere especial, buscar o próximo elemento aceito da String e 
	            // substituí-lo no local do caractere especial
	            while (!Character.toString(sh).matches("\\d")) {
	                j++;
	                sh = strChars[j];
	            }
	            maskChars[i] = sh;
	            j++;
	        }
	    }

	    return new String(maskChars);
	}
	
	public static void main(String args []){
		System.out.println(phone("9114-2626"));
		System.out.println(phone("91142626"));
		System.out.println(phone("6799123-1234"));
		System.out.println(phone("(19) 3241-3322"));
		System.out.println(phone("89 2123-1231"));
		System.out.println(phone("0xx11.2123-2123"));
		System.out.println(phone("(0XX11) 9 3232-3232"));
		System.out.println(phone("XX21991231234"));
		System.out.println(phone("0XX1131231234"));
		System.out.println(phone("0800 530 0800"));
		System.out.println(phone("08005300800"));
		System.out.println(phone("190"));
		System.out.println(phone("10315"));
	}

}
