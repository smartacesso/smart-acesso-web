package br.com.startjob.acesso.utils;

import javax.faces.context.FacesContext;
import javax.mail.Session;

import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
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
	
	
	/**
	 * Envia e-mail notificando o morador sobre a chegada de uma correspondência.
	 * * @param correspondencia
	 * @param mailSession
	 * @throws Exception
	 */
	public static void enviaNotificacaoCorrespondencia(CorrespondenciaEntity correspondencia, Session mailSession) throws Exception {
		
		// Considerando que a sua entidade Pedestre tem o campo de email mapeado.
		// Caso o e-mail fique dentro de um usuário vinculado ao pedestre, adapte para: correspondencia.getDestinatario().getUsuario().getEmail()
		String emailDestinatario = correspondencia.getDestinatario().getEmail();
		
		if (emailDestinatario == null || emailDestinatario.trim().isEmpty()) {
			System.out.println("Destinatário não possui e-mail cadastrado. Notificação cancelada.");
			return; 
		}

		// Nome do App vindo das configurações
		String nomeApp = "SmartAcesso";
		String subject = "Nova correspondência na portaria - " + nomeApp;
		
		// Prevenção de nulos para exibição
		String tipo = correspondencia.getTipo() != null ? correspondencia.getTipo() : "Pacote/Volume";
		String rastreio = correspondencia.getCodigoRastreio() != null && !correspondencia.getCodigoRastreio().isEmpty() 
							? correspondencia.getCodigoRastreio() : "Não informado";
		
		// Corpo da mensagem em HTML
		StringBuilder msg = new StringBuilder();
		msg.append("Olá, <b>").append(correspondencia.getDestinatario().getNome()).append("</b>.<br/><br/>");
		msg.append("Uma nova correspondência chegou para você e já está aguardando retirada na portaria.<br/><br/>");
		
		msg.append("<div style='background-color: #f9f9f9; padding: 15px; border-left: 4px solid #22c55e; margin-bottom: 20px;'>");
		msg.append("<b>Detalhes da Entrega:</b><br/>");
		msg.append("Tipo: ").append(tipo).append("<br/>");
		msg.append("Rastreio: ").append(rastreio).append("<br/>");
		msg.append("Data de Recebimento: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(correspondencia.getDataRecebimento())).append("<br/>");
		msg.append("</div>");
		
		msg.append("Por favor, dirija-se à portaria com seu documento de identificação para realizar a retirada.<br/>");
		
		// Envia usando a sua MailUtils (o último parâmetro "tipo" pode ser nulo ou o identificador do seu template)
		MailUtils.getInstance(mailSession).send(emailDestinatario, subject, msg.toString(), null);
	}
	
	/**
	 * Envia e-mail notificando o morador sobre a retirada da sua correspondência.
	 * @param correspondencia
	 * @param mailSession
	 * @throws Exception
	 */
	public static void enviaConfirmacaoRetirada(CorrespondenciaEntity correspondencia, Session mailSession) throws Exception {
		
		String emailDestinatario = correspondencia.getDestinatario().getEmail();
		
		if (emailDestinatario == null || emailDestinatario.trim().isEmpty()) {
			System.out.println("Destinatário não possui e-mail cadastrado. Confirmação de retirada cancelada.");
			return; 
		}

		String nomeApp = "SmartAcesso";
		String subject = "Pacote Retirado na Portaria - " + nomeApp;
		
		String tipo = correspondencia.getTipo() != null ? correspondencia.getTipo() : "Pacote/Volume";
		
		StringBuilder msg = new StringBuilder();
		msg.append("Olá, <b>").append(correspondencia.getDestinatario().getNome()).append("</b>.<br/><br/>");
		msg.append("Sua correspondência acaba de ser retirada na portaria.<br/><br/>");
		
		// Caixa de destaque em azul para diferenciar do e-mail de chegada
		msg.append("<div style='background-color: #f0f7ff; padding: 15px; border-left: 4px solid #3b82f6; margin-bottom: 20px;'>");
		msg.append("<b>Comprovante de Retirada:</b><br/>");
		msg.append("Item: ").append(tipo).append("<br/>");
		msg.append("Entregue para: <b>").append(correspondencia.getNomeQuemRetirou()).append("</b><br/>");
		msg.append("Documento Apresentado: ").append(correspondencia.getDocumentoQuemRetirou()).append("<br/>");
		msg.append("Data e Hora: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy às HH:mm").format(correspondencia.getDataRetirada())).append("<br/>");
		msg.append("</div>");
		
		msg.append("<i>Se você não reconhece esta pessoa ou esta retirada, por favor, entre em contato imediatamente com a administração do condomínio.</i><br/>");
		
		MailUtils.getInstance(mailSession).send(emailDestinatario, subject, msg.toString(), null);
	}

}
