package br.com.startjob.acesso.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.pdf.BarcodeQRCode;

import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.utils.Utils;

public abstract class CadastroBaseController extends BaseController {

	private static final long serialVersionUID = 1L;

	public boolean verificaLoginExistente(String login, Long idCliente) {
		
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("login", login);
			args.put("cliente.id", idCliente);
			
			@SuppressWarnings("unchecked")
			ArrayList<UsuarioEntity> userList = (ArrayList<UsuarioEntity>) baseEJB.pesquisaSimplesLimitado(UsuarioEntity.class, 
					"findAll", args, 0, 1);
			
			return userList != null && !userList.isEmpty();
		} catch (Exception e) {
			mensagemFatal("", "#Não foi possível verificar a existência deste login.");
			e.printStackTrace();
		}
		return false;
	}
	
	public UsuarioEntity buscaLoginExistente(String login, Long idCliente) {
	    try {
	        Map<String, Object> args = new HashMap<>();
	        args.put("login", login);
	        args.put("cliente.id", idCliente);

	        @SuppressWarnings("unchecked")
	        List<UsuarioEntity> userList = (List<UsuarioEntity>) baseEJB.pesquisaSimplesLimitado(
	                UsuarioEntity.class, "findAll", args, 0, 1);

	        // retorna o primeiro usuário encontrado ou null
	        return (userList != null && !userList.isEmpty()) ? userList.get(0) : null;

	    } catch (Exception e) {
	        mensagemFatal("", "#Não foi possível verificar a existência deste login.");
	        e.printStackTrace();
	    }
	    return null;
	}

	
	public boolean verificaEmailExistente(String email) {
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("email", email);
			
			@SuppressWarnings("unchecked")
			List<ClienteEntity> clientList = (List<ClienteEntity>) baseEJB.pesquisaSimplesLimitado(ClienteEntity.class, 
					"findAll", args, 0, 1);
			
			return clientList != null && !clientList.isEmpty();
		} catch (Exception e) {
			mensagemFatal("", "#Não foi possível verificar a existência deste e-mail.");
			e.printStackTrace();
		}
		return false;
	}
	
	public UsuarioEntity buscaUsuario(Long id) {
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID", id);
			
			@SuppressWarnings("unchecked")
			List<UsuarioEntity> usuarioList = (List<UsuarioEntity>) baseEJB.pesquisaArgFixos(UsuarioEntity.class, 
					"findById", args);
			
			if( usuarioList != null && !usuarioList.isEmpty()) {
				return usuarioList.get(0);
			}
		} catch (Exception e) {
			mensagemFatal("", "#Não foi possível verificar a existência deste e-mail.");
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean matriculaJaExistente(String matricula, Long idCliente, Long idPedestre) {
		Integer qtdePedestres = 0;
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("matricula_equals", matricula);
			args.put("cliente.id", idCliente);
			args.put("status", Status.ATIVO);
			if(idPedestre != null)
				args.put("id_dif", idPedestre);
			
			qtdePedestres = baseEJB.pesquisaSimplesCount(PedestreEntity.class, "findAll", args);
		} catch(Exception e) {
			mensagemFatal("", "#Não foi possível verificar a existência desta matrícula.");
			e.printStackTrace();
		}
		return qtdePedestres > 0;
	}
	
	public boolean cpfJaExistente(String cpf, Long idCliente, Long idPedestre) {
		Integer qtdePedestres = 0;
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("cpf", cpf);
			args.put("cliente.id", idCliente);
			args.put("status", Status.ATIVO);
			if(idPedestre != null)
				args.put("id_dif", idPedestre);
			
			qtdePedestres = baseEJB.pesquisaSimplesCount(PedestreEntity.class, "findAll", args);
		} catch(Exception e) {
			mensagemFatal("", "#Não foi possível verificar a existência deste CPF.");
			e.printStackTrace();
		}
		return qtdePedestres > 0;
	}
	
	public boolean rgJaExistente(String rg, Long idCliente, Long idPedestre) {
		Integer qtdePedestres = 0;
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("rg", rg);
			args.put("cliente.id", idCliente);
			args.put("status", Status.ATIVO);
			if(idPedestre != null)
				args.put("id_dif", idPedestre);
			
			qtdePedestres = baseEJB.pesquisaSimplesCount(PedestreEntity.class, "findAll", args);
		} catch(Exception e) {
			mensagemFatal("", "#Não foi possível verificar a existência deste RG.");
			e.printStackTrace();
		}
		return qtdePedestres > 0;
	}
	
	public boolean cartaoAcessoJaExistente(String cartaoAcesso, Long idCliente, Long idPedestre) {
		Integer qtdePedestres = 0;
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("codigoCartaoAcesso", cartaoAcesso);
			args.put("cliente.id", idCliente);
			args.put("status", Status.ATIVO);
			if(idPedestre != null)
				args.put("id_dif", idPedestre);
			
			qtdePedestres = baseEJB.pesquisaSimplesCount(PedestreEntity.class, "findAll", args);
		} catch(Exception e) {
			mensagemFatal("", "#Não foi possível verificar a existência deste cartão de acesso.");
			e.printStackTrace();
		}
		return qtdePedestres > 0;
	}
	
	public static byte[] gerarImagemQRCode(String text) {
		BarcodeQRCode qrcode = new BarcodeQRCode(text, 1, 1, null);
		Image awtImage = qrcode.createAwtImage(Color.BLACK, Color.WHITE);

		BufferedImage bi = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = bi.createGraphics();
		graphics2D.drawImage(awtImage, 0, 0, 500, 500, null);

		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		try {
			ImageIO.write(bi, "JPG", buff);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return buff.toByteArray();
	}
	
	public StreamedContent getSreamedContent(byte[] foto) {
		return Utils.getStreamedContent(foto);
	}
}
