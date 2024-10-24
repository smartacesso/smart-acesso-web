package br.com.startjob.acesso.modelo.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FeriadoUtils {
	
	public static final String URL_API = "http://api.calendario.com.br/"
			+ "?token=Z3VzdGF2b2RpbmlAZ21haWwuY29tJmhhc2g9MTczODE0MzQ4&ano=";
	
	public static boolean isFeriado(Calendar data){
		
			String resp = ConectionUtils.get(URL_API+data.get(Calendar.YEAR)); 
			
			if(resp == null)
				return false;
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
			return resp.contains(sdf.format(data.getTime()));
		
	}

}
