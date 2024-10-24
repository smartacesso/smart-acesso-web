package br.com.startjob.acesso.modelo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import br.com.startjob.acesso.modelo.BaseConstant;

/**
 * Utilitï¿½ria para envio de e-mails
 * 
 * @author Gustavo Diniz
 *
 */
public class MailUtils {
	
	/**		
	 * Assinatura de e-mails.
	 */
	private static final String ASS =   "<table><tr><td>"+
											  "<img style='margin-top: 20px' src=\"http://smartacesso.com.br/sistema/tema/img/logo.png\" width=\"180\" height=\"65\">"+
										"</td><td>"+
										"	  <br/><span style='margin-left:15px;'><b>Equipe de Suporte</b></span>"+
										"	  <br/><span style='margin-left:15px;'>Telefones: +55 (31) 99109-4687 / +55 (31) 97225-9259</span>"+
										"	  <br/><span style='margin-left:15px;'>e-mail: tecnologia@smartempresarial.com.br</span>"+
										"	  <br/><span style='margin-left:15px;'><a href=\"https://www.smartempresarial.com.br/contato\" target=\"_blank\">www.smartempresarial.com.br/contato</a></span>"+
										"</td></tr></table>";
	
	/**
	 * E-mail padrão do sistema.
	 */
	public static String EMAIL_SUPORTE_PRO = AppAmbienteUtils
								.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOREPLY_EMAIL);
	
	/**
	 * Instancia estática.
	 */
	private static MailUtils instance;
	
	/**
	 * Sessï¿½o de e-mail.
	 */
	private Session session;
	
	/**
	 * Construtor privado.
	 */
	private MailUtils(){}
	
	/**
	 * Retorna instï¿½ncia configurada.
	 * @return
	 */
	public static MailUtils getInstance(Session session){
		if(instance == null)
			instance = new MailUtils();
		instance.session = session;
		return instance;
	}
	
	/**
	 * Executa envio efetivamente.
	 * 
	 * @param mailTo
	 * @param subject
	 * @param msg
	 * @throws Exception
	 */
	public void send(String mailTo, String mailCco, String subject, String msg, String tipo) throws Exception{
		
		mailTo = mailTo.replaceAll(" ", "");
		
		MimeMessage m = new MimeMessage(session);
		Address from = new InternetAddress(EMAIL_SUPORTE_PRO, AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP)+"");
		Address[] to = montaEmails(mailTo);
		Address[] toCco = montaEmails(mailCco);

		//monta header
		m.setHeader("Return-Path", EMAIL_SUPORTE_PRO);
		m.setHeader("Reply-To", EMAIL_SUPORTE_PRO);
		m.setHeader("From", EMAIL_SUPORTE_PRO);
		m.setHeader("Organization", "www.pro-treino.com");
		m.setHeader("Content-Type", "text/html");
		
		m.setFrom(from);
		m.setRecipients(Message.RecipientType.TO, to);
		m.setRecipients(Message.RecipientType.BCC, toCco);
		m.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
		m.setSentDate(new java.util.Date());
		
		String htmlMessage = montaHtmlEmail(msg, null, tipo);
		Multipart multipart = new MimeMultipart();
		MimeBodyPart attachment0 = new MimeBodyPart();
		attachment0.setContent(htmlMessage,"text/html; charset=UTF-8");
		multipart.addBodyPart(attachment0);
		m.setContent(multipart);
		Transport.send(m);
	}
	
	/**
	 * Executa envio efetivamente.
	 * 
	 * @param mailTo
	 * @param subject
	 * @param msg
	 * @throws Exception
	 */
	public void sendHTML(String mailTo, String subject, String htmlMessage, String tipo) throws Exception{
		
		mailTo = mailTo.replaceAll(" ", "");
		System.out.println("Enviando e-mail com sendHtml() para: " + mailTo);
		
		MimeMessage m = new MimeMessage(session);
		Address from = new InternetAddress(EMAIL_SUPORTE_PRO, AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP)+"");
		
		Address[] to = montaEmails(mailTo);
		
		//monta header
		m.setHeader("Return-Path", EMAIL_SUPORTE_PRO);
		m.setHeader("Reply-To", EMAIL_SUPORTE_PRO);
		m.setHeader("From", EMAIL_SUPORTE_PRO);
		m.setHeader("Organization", "www.pro-treino.com");
		m.setHeader("Content-Type", "text/html");
		
		m.setFrom(from);
		m.setRecipients(Message.RecipientType.TO, to);
		m.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
		m.setSentDate(new java.util.Date());
		
		Multipart multipart = new MimeMultipart();
		MimeBodyPart attachment0 = new MimeBodyPart();
		attachment0.setContent(htmlMessage,"text/html; charset=UTF-8");
		multipart.addBodyPart(attachment0);
		m.setContent(multipart);
		Transport.send(m);
		
	}
	
	/**
	 * Funcao para envio personalizado de novo usuario cadastrado no sistema
	 * 
	 * @param mailTo
	 * @param emailFrom
	 * @param idAcademy
	 * @param subject
	 * @param msg
	 * @param tipo
	 * @throws Exception
	 */
	public void sendCustomizedEmail(String mailTo, String emailFrom, String idAcademy, String subject, 
									String msg, String tipo) throws Exception{
		
		String [] dados = new String[]{emailFrom};
		if(emailFrom != null && emailFrom.contains(";"))
			dados = emailFrom.split(";");
		
		mailTo = mailTo.replaceAll(" ", "");
		System.out.println("Enviando e-mail com sendCustomizeEmail() para: " + mailTo);
		
		MimeMessage m = new MimeMessage(session);
		Address from = new InternetAddress(dados[0], dados.length == 1 ? AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP) : dados[1]);
		
		Address[] to = montaEmails(mailTo);
		
		//monta header
		m.setHeader("Return-Path", dados[0]);
		m.setHeader("Reply-To", dados[0]);
		m.setHeader("From", dados[1]);
		m.setHeader("Organization", "www.pro-treino.com");
		m.setHeader("Content-Type", "text/html");
		
		m.setFrom(from);
		m.setRecipients(Message.RecipientType.TO, to);
		m.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
		m.setSentDate(new java.util.Date());
		
		String htmlMessage = montaHtmlEmail(msg, idAcademy, tipo);
		Multipart multipart = new MimeMultipart();
		MimeBodyPart attachment0 = new MimeBodyPart();
		attachment0.setContent(htmlMessage,"text/html; charset=UTF-8");
		multipart.addBodyPart(attachment0);
		m.setContent(multipart);
		Transport.send(m);
		
	}

	private Address[] montaEmails(String mailTo) throws AddressException {
		String [] mails = mailTo.split(",");
		Address[] to = new InternetAddress[mails.length];
		int i = 0;
		for (String email : mails) {
			to[i] = new InternetAddress(email) ;
			i++;
		}
		return to;
	}
	
	/**
	 * Executa envio efetivamente.
	 * 
	 * @param mailTo
	 * @param subject
	 * @param msg
	 * @throws Exception
	 */
	public void send(String mailTo, String subject, String msg, String tipo) throws Exception{
		
		mailTo = mailTo.replaceAll(" ", "");
		System.out.println("Enviando e-mail comum sem anexo para: " + mailTo);
		
		MimeMessage m = new MimeMessage(session);
		Address from = new InternetAddress(EMAIL_SUPORTE_PRO, AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP)+"");
		
		Address[] to = montaEmails(mailTo);
		
		//monta header
		m.setHeader("Return-Path", EMAIL_SUPORTE_PRO);
		m.setHeader("Reply-To", EMAIL_SUPORTE_PRO);
		m.setHeader("From", EMAIL_SUPORTE_PRO);
		m.setHeader("Organization", "www.pro-treino.com");
		m.setHeader("Content-Type", "text/html");
		
		m.setFrom(from);
		m.setRecipients(Message.RecipientType.TO, to);
		m.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
		m.setSentDate(new java.util.Date());
		
		String htmlMessage = montaHtmlEmail(msg, null, tipo);
		Multipart multipart = new MimeMultipart();
		MimeBodyPart attachment0 = new MimeBodyPart();
		attachment0.setContent(htmlMessage,"text/html; charset=UTF-8");
		multipart.addBodyPart(attachment0);
		m.setContent(multipart);
		Transport.send(m);
		
	}
	
	/**
	 * Executa envio efetivamente com anexo.
	 * 
	 * @param mailTo
	 * @param subject
	 * @param msg
	 * @throws Exception
	 */
	public void send(String mailTo, String subject, String msg, byte [] anexo, String nomeAnexo, 
					String tipo) throws Exception{
		
		mailTo = mailTo.replaceAll(" ", "");
		System.out.println("Enviando e-mail comum com anexo para: " + mailTo);
		
		MimeMessage message = new MimeMessage(session);
		Address from = new InternetAddress(EMAIL_SUPORTE_PRO, "Suporte "+AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP)+"");
		Address[] to = new InternetAddress[] {new InternetAddress(mailTo) };
		
		//monta header
		message.setHeader("Return-Path", EMAIL_SUPORTE_PRO);
		message.setHeader("Reply-To", EMAIL_SUPORTE_PRO);
		message.setHeader("From", EMAIL_SUPORTE_PRO);
		message.setHeader("Organization", "www.pro-treino.com");
		message.setHeader("Content-Type", "text/html");
		
		message.setFrom(from);
		message.setRecipients(Message.RecipientType.TO, to);
		message.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
		message.setSentDate(new java.util.Date());
		
		String htmlMessage = montaHtmlEmail(msg, null, tipo);
		Multipart multipart = new MimeMultipart();
		MimeBodyPart attachment0 = new MimeBodyPart();
		attachment0.setContent(htmlMessage,"text/html; charset=UTF-8");
		multipart.addBodyPart(attachment0);
		MimeBodyPart attachment1 = new MimeBodyPart();  
		attachment1.setDataHandler(new DataHandler(new ByteArrayDataSource(anexo, "aplication/pdf")));
		attachment1.setFileName(nomeAnexo);
		multipart.addBodyPart(attachment1);
		message.setContent(multipart);
		Transport.send(message);
		
	}
	
	/**
	 * Funcao para envio de email com anexo, usando template personalizado
	 * 
	 * @param mailTo
	 * @param emailFrom
	 * @param subject
	 * @param idAcademy
	 * @param nameAcademy
	 * @param msg
	 * @param anexo
	 * @param nomeAnexo
	 * @param tipo
	 * @throws Exception
	 */
	public void sendTemplate(String mailTo, String emailFrom, String subject, String idAcademy,
			String nameAcademy, String msg, byte [] anexo, String nomeAnexo, String tipo) throws Exception {
		
		mailTo = mailTo.replaceAll(" ", "");
		
		System.out.println("Enviando e-mail customizado com anexo para: " + mailTo);
		
		if(emailFrom == null || "".equals(emailFrom)){
			emailFrom = "noreply@pro-treino.com";
		}
		
		MimeMessage message = new MimeMessage(session);
		Address from = new InternetAddress(emailFrom, nameAcademy);
		
		Address[] to = montaEmails(mailTo);
		
		//monta header
		message.setHeader("Return-Path", emailFrom);
		message.setHeader("Reply-To", emailFrom);
		message.setHeader("From", nameAcademy);
		message.setHeader("Organization", "www.pro-treino.com");
		message.setHeader("Content-Type", "text/html");
		
		message.setFrom(from);
		message.setRecipients(Message.RecipientType.TO, to);
		message.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
		message.setSentDate(new java.util.Date());
		
		String htmlMessage = montaHtmlEmail(msg, idAcademy, tipo);
		Multipart multipart = new MimeMultipart();
		MimeBodyPart attachment0 = new MimeBodyPart();
		attachment0.setContent(htmlMessage,"text/html; charset=UTF-8");
		multipart.addBodyPart(attachment0);
		
		MimeBodyPart attachment1 = new MimeBodyPart();
		attachment1.setDataHandler(new DataHandler(new ByteArrayDataSource(anexo, "aplication/pdf")));
		attachment1.setFileName(nomeAnexo);
		multipart.addBodyPart(attachment1);
		message.setContent(multipart);
		Transport.send(message);
		
	}
	
	/**
	 * Funcao para envio de email sem anexo, usando template personalizado
	 * 
	 * @param mailTo
	 * @param emailFrom
	 * @param subject
	 * @param idAcademy
	 * @param nameAcademy
	 * @param msg
	 * @param tipo
	 * @throws Exception
	 */
	public void sendTemplate(String mailTo, String emailFrom, String subject, String idAcademy, String nameAcademy, 
							String msg, String tipo) throws Exception{
		
		mailTo = mailTo.replaceAll(" ", "");
		
		System.out.println("Enviando e-mail customizado sem anexo para: " + mailTo);
		
		if(emailFrom == null || "".equals(emailFrom)){
			emailFrom = "noreply@pro-treino.com";
		}
		
		MimeMessage message = new MimeMessage(session);
		Address from = new InternetAddress(emailFrom, nameAcademy);
		
		Address[] to = montaEmails(mailTo);
		
		//monta header
		message.setHeader("Return-Path", emailFrom);
		message.setHeader("Reply-To", emailFrom);
		message.setHeader("From", nameAcademy);
		message.setHeader("Organization", "www.pro-treino.com");
		message.setHeader("Content-Type", "text/html");
		
		message.setFrom(from);
		message.setRecipients(Message.RecipientType.TO, to);
		message.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
		message.setSentDate(new java.util.Date());
		
		String htmlMessage = montaHtmlEmail(msg, idAcademy, tipo);
		Multipart multipart = new MimeMultipart();
		MimeBodyPart attachment0 = new MimeBodyPart();
		attachment0.setContent(htmlMessage,"text/html; charset=UTF-8");
		multipart.addBodyPart(attachment0);
		message.setContent(multipart);
		Transport.send(message);
		
	}

	/**
	 * Monta email seguindo o template cadastrado no servidor.
	 * 
	 * @param msg - mensagem
	 * 
	 * @return html da mensagem
	 */
	private String montaHtmlEmail(String msg, String idAcademy, String tipo){
		
		try {
			
			//recupera template do projeto
			File template = new File(AppAmbienteUtils.getResourcesFolder()
					+"/mail-template"+(idAcademy == null ? "" : idAcademy)+".html");
			if(template.exists() && template.isFile()){
				
				//lÃª template
				BufferedReader br = new BufferedReader(new FileReader(template));
				StringBuffer arquivo = new StringBuffer();
				while(br.ready()){
					arquivo.append(br.readLine());
				}
				br.close();
				
				//preenche tags
				String retorno = arquivo.toString().replace("#{msg}", msg);
				
				return retorno;
	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		//caso template não exista
		return msg + ASS;
	}


}
