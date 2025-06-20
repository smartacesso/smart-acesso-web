package br.com.startjob.acesso.controller;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.CaptureEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.google.gson.JsonObject;

import br.com.startjob.acesso.api.WebSocketEndpoint;
import br.com.startjob.acesso.modelo.ejb.BaseEJB;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;

@SuppressWarnings("serial")
@Named("cadastroFacialHikivisionController")
@ViewScoped
public class CadastroFacialHikivisionController extends BaseController {
	private PedestreEntity pedestre;
	private ClienteEntity cliente;
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
		cliente = buscaClientePeloId(idCliente);

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

		String cpfNumeros = pedestre.getCpf().replaceAll("\\D", "");

		JsonObject json = new JsonObject();
		json.addProperty("nome", pedestre.getNome());

		if (pedestre.getCodigoCartaoAcesso() != null) {
			if (!pedestre.getCodigoCartaoAcesso().isEmpty()) {
				json.addProperty("cardNumber", pedestre.getCodigoCartaoAcesso());
			}
		} else {
			json.addProperty("cardNumber", cpfNumeros);
		}

		byte[] fotoBytes = pedestre.getFoto();

		if (fotoBytes != null) {
			String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
			json.addProperty("foto", fotoBase64);
		} else {
			json.addProperty("foto", "");
		}

		// buscar cliente correto pelo idCliente e pegar unidade organizacional
		WebSocketEndpoint.enviarParaLocal(cliente.getNomeUnidadeOrganizacional(), json.toString());

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
			List<PedestreEntity> pedestres = (List<PedestreEntity>) baseEJB
					.pesquisaArgFixosLimitado(PedestreEntity.class, "findByIdPedestreAndIdCliente", args, 0, 1);

			if (pedestres != null)
				return pedestres.stream().findFirst().orElse(null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private ClienteEntity buscaClientePeloId(Long idCliente) {
		HashMap<String, Object> args = new HashMap<>();
		args.put("ID", idCliente);

		try {
			List<ClienteEntity> clientes = (List<ClienteEntity>) baseEJB.pesquisaArgFixosLimitado(ClienteEntity.class,
					"findById", args, 0, 1);

			if (clientes != null)
				return clientes.stream().findFirst().orElse(null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
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
