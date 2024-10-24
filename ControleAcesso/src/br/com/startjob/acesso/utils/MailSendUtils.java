package br.com.startjob.acesso.utils;

import javax.faces.context.FacesContext;
import javax.mail.Session;

import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;
import br.com.startjob.acesso.modelo.utils.MailUtils;

public class MailSendUtils {
	
	/**
	 * Envia e-mail de confirmação de cadastro.
	 * 
	 * @param user
	 * @throws Exception
	 */
	public static void enviaResetSenha(UsuarioEntity user, Session mailSession, 
				FacesContext facesContext, String urlApp) throws Exception {
		
		String msg = ResourceBundleUtils.getInstance()
				.recuperaChave("mail.msg.nova.senha", facesContext, 
						new Object[]{urlApp+"/alterarDados.xhtml?id="+
									 user.getId().toString()+"&token="+user.getToken()});
		
		String sub = ResourceBundleUtils.getInstance()
				.recuperaChave("mail.title.nova.senha", facesContext, 
						new Object[]{AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP)});
		
		MailUtils.getInstance(mailSession)
			.send(user.getEmail(),sub, msg, "");
	}

}
