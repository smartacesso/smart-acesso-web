package br.com.startjob.acesso.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;

/**
 * Utilitário para recuperar mensagens do bundle JSF
 *
 * @author: Gustavo Diniz
 * @since 01/02/2012
 */
public final class ResourceBundleUtils {

	/**
	 * 
	 */
	private static final Locale LOCALE_BRASIL = new Locale("pt","BR");
	/**
	 * Instancia singleton
	 */
	private static ResourceBundleUtils instance;

	/**
	 * 
	 * Construtor padrão privado (Singleton)
	 *
	 * @author: Gustavo Diniz
	 *
	 */
	private ResourceBundleUtils() {

	}

	/**
	 * 
	 * Retorna instancia do Singleton
	 *
	 * @author: Gustavo Diniz
	 * @return resourceBundleUtils
	 */
	public static ResourceBundleUtils getInstance() {

		if (instance == null) {
			instance = new ResourceBundleUtils();
		}

		return instance;
	}

	/**
	 * 
	 * Retorna mensagem determinada pela chave no resource bundle
	 *
	 * @author: Gustavo Diniz
	 * @param chave - chave para procura
	 * @param context - facesContext atual
	 * @return mensagem
	 */
	public String recuperaChave(String chave, FacesContext context) {

		if (chave == null) {
			return "???chave não encontrada???";
		}

		//retorna exatamente o texto recebido
		if (chave.indexOf("#") != -1) {
			return chave.replace('#', ' ');
		}

		if (context != null) {
			try {

				String bundle = context.getApplication().getMessageBundle();
				
				//para recupera dados do path
				String path = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_MANAGEMENT_PATH);
				if(path != null && !"".equals(path)){
					path = path.replace("/flavours/", "")+"_";
					bundle = bundle.replace("message", path+"message");
				}
				

				ResourceBundle resource =
					ResourceBundle.getBundle(bundle, LOCALE_BRASIL);
				
				if(resource == null){
					return "???" + chave + "???";
				}
				
				return resource.getString(chave);

			} catch (MissingResourceException e) {
				return "???" + chave + "???";
			} 

		}

		return chave;
	}
	
	
	public String [] recuperaListaChaves(String chave, FacesContext context, boolean base) {

		if (chave == null) {
			return new String[]{"???chave não encontrada???"};
		}

		//retorna exatamente o texto recebido
		if (chave.indexOf("#") != -1) {
			return new String[]{chave.replace('#', ' ')};
		}

		if (context != null) {
			try {

				String bundle = context.getApplication().getMessageBundle();
				
				//para recupera dados do path
				String path = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_MANAGEMENT_PATH);
				if(!base && path != null && !"".equals(path)){
					path = path.replace("/flavours/", "")+"_";
					bundle = bundle.replace("message", path+"message");
				}
				

				ResourceBundle resource =
					ResourceBundle.getBundle(bundle, LOCALE_BRASIL);
				
				if(resource == null){
					return new String[]{"???" + chave + "???"};
				}
				
				//procura chaves
				List<String> keys = new ArrayList<String>();
				Enumeration<String> keysValue = resource.getKeys();
				for (; keysValue.hasMoreElements();) {
					String string = (String) keysValue.nextElement();
					if(string.startsWith(chave))
						keys.add(string);
				}
				
				//ordena em ordem alfabética
				Collections.sort(keys, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o2.compareTo(o1);
					}
				});
				
				//monta array com valores
				List<String> values = new ArrayList<String>();
				for (String string : keys)
					values.add(resource.getString(string));
				
				return values.toArray(new String[]{});

			} catch (MissingResourceException e) {
				return new String[]{"???" + chave + "???"};
			} 

		}

		return new String[]{chave};
	}
	
	
	/**
	 * 
	 * Retorna mensagem determinada pela chave no resource bundle
	 *
	 * @author: Gustavo Diniz
	 * @param chave - chave para procura
	 * @param context - facesContext atual
	 * @return mensagem
	 */
	public String recuperaChavePrincipal(String chave, FacesContext context) {

		if (chave == null) {
			return "???chave não encontrada???";
		}

		//retorna exatamente o texto recebido
		if (chave.indexOf("#") != -1) {
			return chave.replace('#', ' ');
		}

		if (context != null) {
			try {

				String bundle = context.getApplication().getMessageBundle();
				ResourceBundle resource =
					ResourceBundle.getBundle(bundle, LOCALE_BRASIL);
				
				if(resource == null){
					return "???" + chave + "???";
				}
				
				return resource.getString(chave);

			} catch (MissingResourceException e) {
				return "???" + chave + "???";
			} 

		}

		return chave;
	}

	/**
	 * Retorna mensagem com parametros determinada 
	 * pela chave no resource bundle
	 *
	 * @author: Gustavo Diniz
	 * @param chave - chave da mensagem
	 * @param context - faces context
	 * @param params - parametros
	 * @return msg
	 */
	public String recuperaChave( String chave, FacesContext context, Object[] params) {

		//retorna exatamente o texto recebido
		if (chave.indexOf("#") != -1) {
			return chave.replace('#', ' ');
		}
		
		if (context != null) {
			try {

				String bundle = context.getApplication().getMessageBundle();
				
				//para recupera dados do path
				String path = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_MANAGEMENT_PATH);
				if(path != null && !"".equals(path)){
					path = path.replace("/flavours/", "")+"_";
					bundle = bundle.replace("message", path+"message");
				}

				ResourceBundle resource =
					ResourceBundle.getBundle(bundle, LOCALE_BRASIL);

				if(resource == null){
					return "???" + chave + "???";
				}
				
				List<String> descricoes = new ArrayList<String>();

				for (int i = 0; i < params.length; i++) {
					try {
						descricoes.add(resource.getString((String) params[i]));
					} catch (Exception e) {
						descricoes.add((String) params[i]);
					}
				}

				MessageFormat mf = new MessageFormat(resource.getString(chave));

				return mf.format(descricoes.toArray()).toString();

			} catch (MissingResourceException e) {
				return "???" + chave + "???";
			} 

		}

		return chave;
	}

}
