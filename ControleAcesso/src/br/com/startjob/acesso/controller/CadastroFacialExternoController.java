package br.com.startjob.acesso.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.primefaces.PrimeFaces;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.StatusCadastroExterno;

@SuppressWarnings("serial")
@Named("cadastroFacialExternoController")
@ViewScoped
public class CadastroFacialExternoController extends BaseController {

	private PedestreEntity pedestre;
	private CadastroExternoEntity cadastroExterno;
	
	private final Integer qtdeFotosNecessarias = 3;
	
	private Long idCliente;
	private Long idPedestre;
	private Long token;

	@PostConstruct
	public void init() {
		try {
			idPedestre = Long.valueOf(getRequest().getParameter("idPedestre"));
			idCliente = Long.valueOf(getRequest().getParameter("cliente"));
			token = Long.valueOf(getRequest().getParameter("token"));
		
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		pedestre = buscaPedestrePeloId(idPedestre, idCliente);
		
		if(pedestre == null)
			return;
		
		cadastroExterno = buscaCadastroExternoAtivo(idPedestre, idCliente, token);
		
		if(cadastroExterno == null)
			return;
		
		limpaSessao();
		
		setSessioAtrribute("numeroFotoAtual", 1);
	}

	@SuppressWarnings("unchecked")
	private CadastroExternoEntity buscaCadastroExternoAtivo(Long idPedestre, Long idCliente, Long token) {
		HashMap<String, Object> args = new HashMap<>();
		args.put("ID_PEDESTRE", idPedestre);
		args.put("ID_CLIENTE", idCliente);
		args.put("TOKEN", token);
		args.put("STATUS", StatusCadastroExterno.AGUARDANDO_CADASTRO);
		
		try {
			List<CadastroExternoEntity> cadastros = (List<CadastroExternoEntity>) baseEJB
					.pesquisaArgFixosLimitado(CadastroExternoEntity.class, "findByTokenAndIdPedestreAndIdCliente", args, 0, 1);
			
			if(cadastros != null && !cadastros.isEmpty())
				return cadastros.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private PedestreEntity buscaPedestrePeloId(Long idPedestre, Long idCliente) {
		HashMap<String, Object> args = new HashMap<>();
		args.put("ID", idPedestre);
		args.put("ID_CLIENTE", idCliente);
		
		try {
			List<PedestreEntity> pedestres = (List<PedestreEntity>) baseEJB
					.pesquisaArgFixosLimitado(PedestreEntity.class, "findByIdPedestreAndIdCliente", args, 0, 1);
			
			if(pedestres != null)
				return pedestres.stream().findFirst().orElse(null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void onFinalizaCapture() {
		byte[] foto1 = Base64.decodeBase64((String) getSessionAtrribute("imagem1"));
		byte[] foto2 = Base64.decodeBase64((String) getSessionAtrribute("imagem2"));
		byte[] foto3 = Base64.decodeBase64((String) getSessionAtrribute("imagem3"));
		
		if(cadastroExterno.getImageHeight() == null) {
			try {
				InputStream in = new ByteArrayInputStream(foto1);
				BufferedImage image = ImageIO.read(in);
				cadastroExterno.setImageHeight(image.getHeight());
				cadastroExterno.setImageWidth(image.getWidth());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		cadastroExterno.setPrimeiraFoto(foto1);
		cadastroExterno.setSegundaFoto(foto2);
		cadastroExterno.setTerceiraFoto(foto3);

		registrarCadastroFacialFinalizado();
		limpaSessao();
		
		redirect("/capturaDeFaceConcluida.xhtml");
	}
	
	public void download(int numeroFoto) {
		byte[] arquivo = null;
		
		if(numeroFoto == 1)
			arquivo = Base64.decodeBase64((String) getSessionAtrribute("imagem1"));
		else if(numeroFoto == 2)
			arquivo = Base64.decodeBase64((String) getSessionAtrribute("imagem2"));
		else if(numeroFoto == 3)
			arquivo = Base64.decodeBase64((String) getSessionAtrribute("imagem3"));
		
		String nome = "Foto " + numeroFoto + ".png";
		
		setSessioAtrribute(BaseConstant.EXPORT.BYTES, arquivo);
		setSessioAtrribute(BaseConstant.EXPORT.FILE_NAME, nome);
		
		PrimeFaces.current().executeScript("download('"+BaseConstant.URL_APLICACAO+"/export');");
	}
	
	public void reload() throws IOException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI() + "?cliente=" + idCliente 
					+ "&idPedestre=" + idPedestre + "&token=" + token);
	}
	
	private void registrarCadastroFacialFinalizado() {
		try {
			cadastroExterno.setDataCadastroDaFace(new Date());
			cadastroExterno.setToken(null);
			cadastroExterno.setStatusCadastroExterno(StatusCadastroExterno.CADASTRADO);

			baseEJB.gravaObjeto(cadastroExterno);
		
		} catch (Exception e) {
			mensagemFatal("", "#Falha ao gravar as fotos.");
			e.printStackTrace();
		}
	}
	
	private void limpaSessao() {
		setSessioAtrribute("imagem1", "");
		setSessioAtrribute("imagem2", "");
		setSessioAtrribute("imagem3", "");
	}
	
	public boolean isAindaCaputrandoFotos() {
		int numeroFotoAtual = (int) getSessionAtrribute("numeroFotoAtual");
		
		return numeroFotoAtual <= qtdeFotosNecessarias;
	}

	public byte[] pngToBmp(byte[] data) throws IOException {
	    BufferedImage imag = ImageIO.read(new ByteArrayInputStream(data));
	    BufferedImage bmpImg = new BufferedImage(imag.getWidth(), imag.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
	    bmpImg.createGraphics().drawImage(imag, 0, 0, Color.WHITE, null);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(bmpImg, "bmp", baos);
	    return baos.toByteArray();
	}

	public PedestreEntity getPedestre() {
		return pedestre;
	}

	public CadastroExternoEntity getCadastroExterno() {
		return cadastroExterno;
	}

	public Integer getQtdeFotosNecessarias() {
		return qtdeFotosNecessarias;
	}

}
