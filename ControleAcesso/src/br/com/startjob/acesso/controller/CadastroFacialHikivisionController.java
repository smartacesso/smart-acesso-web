package br.com.startjob.acesso.controller;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.CaptureEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.startjob.acesso.api.WebSocketCadastroEndpoint;
import br.com.startjob.acesso.modelo.ejb.BaseEJB;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.HorarioEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;
import br.com.startjob.acesso.to.PedestrianAccessTO;
import br.com.startjob.acesso.to.WebSocketPedestrianAccessTO;

@SuppressWarnings("serial")
@Named("cadastroFacialHikivisionController")
@ViewScoped
public class CadastroFacialHikivisionController extends BaseController {
	
	@EJB
	private PedestreEJBRemote pedestreEJB;
	
	private PedestreEntity pedestre;
	private CadastroExternoEntity cadastroExterno;
	private final Gson gson = new Gson();

	private final Integer qtdeFotosNecessarias = 3;

	private Long idCliente;
	private Long idPedestre;
	private Long token;

	@PostConstruct
	public void init() {
		baseEJB = pedestreEJB;
		
		try {
			idPedestre = Long.valueOf(getRequest().getParameter("idPedestre"));
			idCliente = Long.valueOf(getRequest().getParameter("cliente"));
			token = Long.valueOf(getRequest().getParameter("token"));

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		pedestre = buscaPedestrePeloId(idPedestre, idCliente);

//		if(Objects.nonNull(pedestre) && Objects.nonNull(pedestre.getRegras())) {	
//			for(PedestreRegraEntity p : pedestre.getRegras()) {
//				if(Objects.nonNull(p.getRegra()) && p.getRegra().getTipo() == TipoRegra.ACESSO_HORARIO) {
//					List<HorarioEntity> horarios = buscaHorariosByIdPedestreRegra(p.getId());
//					p.setHorarios(horarios);
//				}
//			}
//		}
		
		if (pedestre == null) {
			return;
		}

		if (!pedestre.autoAtendimentoLiberado()) {
			mensagemFatal("", "msg.fatal.pedestre.nao.gravado");
			return;
		}
	}

	@Override
	public String salvar() {
		if (!pedestre.autoAtendimentoLiberado()) {
			mensagemFatal("", "msg.fatal.pedestre.nao.gravado");
			return "";
		}

		pedestre.setDataAlteracaoFoto(new Date());
		String jsonStr = gson.toJson(WebSocketPedestrianAccessTO.fromPedestre(pedestre));
		JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();

		// buscar cliente correto pelo idCliente e pegar unidade organizacional
		WebSocketCadastroEndpoint.enviarParaLocal(pedestre.getCliente().getId().toString(), json.toString());
		try {
			baseEJB.gravaObjeto(pedestre);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String retornoStr = "";

		redirect("/capturaDeFaceConcluida.xhtml");
		return retornoStr;
	}

	@SuppressWarnings("unchecked")
	private PedestreEntity buscaPedestrePeloId(Long idPedestre, Long idCliente) {
		HashMap<String, Object> args = new HashMap<>();
		args.put("ID", idPedestre);
		args.put("ID_CLIENTE", idCliente);

		try {
			List<PedestreEntity> pedestres = (List<PedestreEntity>) pedestreEJB
					.pesquisaArgFixos(PedestreEntity.class, "findByIdWithEmpRegrasAndHorarios", args);

			if (pedestres != null) {
				return pedestres.stream().findFirst().orElse(null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private List<HorarioEntity> buscaHorariosByIdPedestreRegra(Long id) {
		HashMap<String, Object> args = new HashMap<>();
		args.put("ID", id);

		try {
			@SuppressWarnings("unchecked")
			List<PedestreRegraEntity> pedestreRegras = (List<PedestreRegraEntity>) pedestreEJB
					.pesquisaArgFixos(PedestreRegraEntity.class, "findByIdComHorarios", args);

			if (pedestreRegras != null && !pedestreRegras.isEmpty()) {
				return pedestreRegras.get(0).getHorarios();
			}
		} catch (Exception e) {
			System.err.println("Erro ao buscar horários do pedestre: " + e.getMessage());
			e.printStackTrace();
		}

		return Collections.emptyList();
	}


	public void onCapture(CaptureEvent event) {
		byte[] fotoBase64 = event.getData();

		try {
			pedestre.setFoto(fotoBase64);
		} catch (IllegalArgumentException e) {
			System.err.println("Erro ao decodificar a imagem capturada: " + e.getMessage());
		}
	}

	public StreamedContent getStreamedContent(byte[] foto) {
		if (foto != null) {
			return DefaultStreamedContent.builder().contentType("image/jpeg")
					.stream(() -> new ByteArrayInputStream(foto)).build();
		}
		return null;
	}

	private void limpaSessao() {
		setSessioAtrribute("imagem1", "");
		setSessioAtrribute("imagem2", "");
		setSessioAtrribute("imagem3", "");
	}

	public PedestreEntity getPedestre() {
		return pedestre;
	}

	public void setPedestre(PedestreEntity pedestre) {
		this.pedestre = pedestre;
	}

	public CadastroExternoEntity getCadastroExterno() {
		return cadastroExterno;
	}

	public void setCadastroExterno(CadastroExternoEntity cadastroExterno) {
		this.cadastroExterno = cadastroExterno;
	}

	public Long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	public Long getIdPedestre() {
		return idPedestre;
	}

	public void setIdPedestre(Long idPedestre) {
		this.idPedestre = idPedestre;
	}

	public Long getToken() {
		return token;
	}

	public void setToken(Long token) {
		this.token = token;
	}

	public Integer getQtdeFotosNecessarias() {
		return qtdeFotosNecessarias;
	}

}
