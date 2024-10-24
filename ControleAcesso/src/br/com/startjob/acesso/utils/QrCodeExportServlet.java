package br.com.startjob.acesso.utils;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.startjob.acesso.controller.CadastroBaseController;
import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

@SuppressWarnings("serial")
@WebServlet("/qrCode")
public class QrCodeExportServlet extends BaseServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (request.getParameter("idUser") == null
				|| request.getParameter("idUser").isEmpty())
			return;
		
		try {
			BaseEJBRemote ejb = getEjb(BaseEJBRemote.class);
			Long idUser = Long.valueOf(request.getParameter("idUser"));

			PedestreEntity pedestre = (PedestreEntity) ejb.recuperaObjeto(PedestreEntity.class, idUser);

			if (pedestre == null || pedestre.getQrCodeParaAcesso() == null)
				return;

			byte[] bytes = CadastroBaseController.gerarImagemQRCode(pedestre.getQrCodeParaAcesso());

			if (bytes == null)
				return;

			ServletOutputStream out = response.getOutputStream();
			
			String fileName = "qrCode.png";
			String contentType = "text/plain";
			MagicMatch match = Magic.getMagicMatch(bytes);
			contentType = match.getMimeType();

			response.setHeader("Content-disposition", "attachment; filename=" + fileName);
			response.setContentType(contentType);
			response.setContentLength(bytes.length);

			out.write(bytes);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
