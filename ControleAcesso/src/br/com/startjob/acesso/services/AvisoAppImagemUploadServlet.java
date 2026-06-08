package br.com.startjob.acesso.services;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.utils.BaseServlet;

/**
 * Recebe a imagem do aviso (multipart) e grava na sessao HTTP para o cadastro web.
 */
@SuppressWarnings("serial")
@WebServlet("/avisoApp/uploadImagem")
@MultipartConfig(maxFileSize = 3145728L, maxRequestSize = 3145728L, fileSizeThreshold = 0)
public class AvisoAppImagemUploadServlet extends BaseServlet {

	public static final String SESSION_IMAGEM_PREFIX = "avisoAppImagemPendente_";
	public static final String SESSION_IMAGEM_NOME_SUFFIX = "_nome";
	public static final String SESSION_IMAGEM_ALTERADA_SUFFIX = "_alterada";

	private static final long LIMITE_IMAGEM_BYTES = 3145728L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain;charset=UTF-8");

		HttpSession session = request.getSession(false);
		UsuarioEntity usuario = session != null
				? (UsuarioEntity) session.getAttribute(BaseConstant.LOGIN.USER_ENTITY)
				: null;
		if (usuario == null || usuario.getCliente() == null || usuario.getCliente().getId() == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("NAO_AUTORIZADO");
			return;
		}

		Part part;
		try {
			part = request.getPart("imagem");
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("SEM_ARQUIVO");
			return;
		}

		if (part == null || part.getSize() <= 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("ARQUIVO_VAZIO");
			return;
		}
		if (part.getSize() > LIMITE_IMAGEM_BYTES) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("ARQUIVO_GRANDE");
			return;
		}

		byte[] conteudo;
		try (InputStream in = part.getInputStream()) {
			conteudo = IOUtils.toByteArray(in);
		}
		if (conteudo == null || conteudo.length == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("ARQUIVO_VAZIO");
			return;
		}

		String fileName = extrairNomeArquivo(part);
		if (!isImagemPermitida(fileName, conteudo)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("TIPO_INVALIDO");
			return;
		}

		String idPart = request.getParameter("avisoId");
		if (idPart == null || idPart.trim().isEmpty()) {
			idPart = "novo";
		} else {
			idPart = idPart.trim();
		}

		String chave = SESSION_IMAGEM_PREFIX + usuario.getCliente().getId() + "_" + idPart;
		session.setAttribute(chave, conteudo);
		session.setAttribute(chave + SESSION_IMAGEM_NOME_SUFFIX, fileName);
		session.setAttribute(chave + SESSION_IMAGEM_ALTERADA_SUFFIX, Boolean.TRUE);

		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write("OK");
	}

	private String extrairNomeArquivo(Part part) {
		String header = part.getHeader("content-disposition");
		if (header == null) {
			return "imagem.jpg";
		}
		for (String token : header.split(";")) {
			token = token.trim();
			if (token.startsWith("filename=")) {
				String nome = token.substring("filename=".length()).trim().replace("\"", "");
				if (nome.contains("\\")) {
					nome = nome.substring(nome.lastIndexOf('\\') + 1);
				}
				if (nome.contains("/")) {
					nome = nome.substring(nome.lastIndexOf('/') + 1);
				}
				return nome;
			}
		}
		return "imagem.jpg";
	}

	private boolean isImagemPermitida(String fileName, byte[] content) {
		if (fileName != null) {
			String lower = fileName.toLowerCase();
			if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")) {
				return true;
			}
		}
		if (content != null && content.length >= 2 && (content[0] & 0xFF) == 0xFF && (content[1] & 0xFF) == 0xD8) {
			return true;
		}
		return content != null && content.length >= 4
				&& (content[0] & 0xFF) == 0x89 && content[1] == 0x50 && content[2] == 0x4E && content[3] == 0x47;
	}
}
