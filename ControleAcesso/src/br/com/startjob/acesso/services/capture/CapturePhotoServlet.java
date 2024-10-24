package br.com.startjob.acesso.services.capture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.com.startjob.acesso.utils.BaseServlet;

@SuppressWarnings("serial")
@WebServlet("/capturePhoto")
public class CapturePhotoServlet extends BaseServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		StringBuffer jb = new StringBuffer();
		String line = null;
		BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null)
			jb.append(line);

		String img64 = jb.toString();
		// check if the image is really a base64 png, maybe a bit hard-coded
		if (img64 != null && img64.startsWith("data:image/png;base64,")) {
			// Remove Content-type declaration
			img64 = img64.substring(img64.indexOf(',') + 1);
		} else {
			response.setStatus(403);
			out.print("A imagem deve ser png");
			return;
		}
		
		HttpSession session = request.getSession();
		
		if (session.getAttribute("imagem1") == null || session.getAttribute("imagem1") == "")
			session.setAttribute("imagem1", img64);
		else if (session.getAttribute("imagem2") == null || session.getAttribute("imagem2") == "")
			session.setAttribute("imagem2", img64);
		else if (session.getAttribute("imagem3") == null || session.getAttribute("imagem3") == "")
			session.setAttribute("imagem3", img64);
		
		int numFotoAtual = (int) session.getAttribute("numeroFotoAtual");
		numFotoAtual++;
		
		session.setAttribute("numeroFotoAtual", numFotoAtual);
	}
}
