package br.com.startjob.acesso.service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.StatusCadastroExterno;
import br.com.startjob.acesso.modelo.enumeration.TipoCadastroExterno;

/**
 * Fila de cadastros internos (cadastro pedestre / simplificado) que falharam ao gravar ou ao enviar facial.
 */
public class CadastroErroService {

	private static final int MOTIVO_MAX = 500;

	private final BaseEJBRemote baseEJB;

	public CadastroErroService(BaseEJBRemote baseEJB) {
		this.baseEJB = baseEJB;
	}

	public static TipoCadastroExterno tipoPorFluxoSimplificado(boolean cadastroSimplificado) {
		return cadastroSimplificado ? TipoCadastroExterno.CADASTRO_INTERNO_SIMPLIFICADO
				: TipoCadastroExterno.CADASTRO_INTERNO_COMPLETO;
	}

	private Map<String, Object> argsErroInterno(Long idCliente) {
		Map<String, Object> args = new HashMap<>();
		args.put("ID_CLIENTE", idCliente);
		args.put("STATUS", StatusCadastroExterno.ERRO);
		args.put("TIPO_SIMPLIFICADO", TipoCadastroExterno.CADASTRO_INTERNO_SIMPLIFICADO);
		args.put("TIPO_COMPLETO", TipoCadastroExterno.CADASTRO_INTERNO_COMPLETO);
		return args;
	}

	@SuppressWarnings("unchecked")
	public Long gravarOuAtualizarErro(ClienteEntity cliente, PedestreEntity pedestre, boolean cadastroSimplificado,
			String motivo) throws Exception {
		if (cliente == null || cliente.getId() == null) {
			return null;
		}
		TipoCadastroExterno tipo = tipoPorFluxoSimplificado(cadastroSimplificado);
		String motivoTrim = truncarMotivo(motivo);

		CadastroExternoEntity existente = null;
		if (pedestre != null && pedestre.getId() != null) {
			Map<String, Object> args = argsErroInterno(cliente.getId());
			args.put("ID_PEDESTRE", pedestre.getId());
			List<CadastroExternoEntity> abertos = (List<CadastroExternoEntity>) baseEJB.pesquisaArgFixos(
					CadastroExternoEntity.class, "findErroInternoAbertoByPedestre", args);
			if (abertos != null && !abertos.isEmpty()) {
				existente = abertos.get(0);
			}
		}

		if (existente != null) {
			existente.setObservacaoTotem(motivoTrim);
			existente.setTipo(tipo);
			if (pedestre != null) {
				existente.setPedestre(pedestre);
				preencherSnapshotVisitante(existente, pedestre);
			}
			baseEJB.alteraObjeto(existente);
			return existente.getId();
		}

		CadastroExternoEntity cadastro = new CadastroExternoEntity();
		cadastro.setCliente(cliente);
		cadastro.setTipo(tipo);
		cadastro.setStatusCadastroExterno(StatusCadastroExterno.ERRO);
		cadastro.setObservacaoTotem(motivoTrim);
		cadastro.setToken(null);
		if (pedestre != null) {
			cadastro.setPedestre(pedestre);
			preencherSnapshotVisitante(cadastro, pedestre);
			if (pedestre.getFoto() != null && pedestre.getFoto().length > 0) {
				cadastro.setPrimeiraFoto(pedestre.getFoto());
			}
		}
		baseEJB.gravaObjeto(cadastro);
		return cadastro.getId();
	}

	private void preencherSnapshotVisitante(CadastroExternoEntity cadastro, PedestreEntity pedestre) {
		if (pedestre.getCpf() != null) {
			cadastro.setCpfVisitante(pedestre.getCpf().replaceAll("\\D", ""));
		}
		if (pedestre.getNome() != null) {
			cadastro.setNomeVisitante(pedestre.getNome().trim());
		}
		if (pedestre.getEmpresa() != null && pedestre.getEmpresa().getId() != null) {
			cadastro.setEmpresa(pedestre.getEmpresa());
		}
	}

	@SuppressWarnings("unchecked")
	public List<CadastroExternoEntity> listarErros(Long idCliente) throws Exception {
		List<CadastroExternoEntity> lista = (List<CadastroExternoEntity>) baseEJB.pesquisaArgFixos(
				CadastroExternoEntity.class, "findErrosInternosByCliente", argsErroInterno(idCliente));
		return lista != null ? lista : Collections.emptyList();
	}

	public int contarErros(Long idCliente) throws Exception {
		return listarErros(idCliente).size();
	}

	@SuppressWarnings("unchecked")
	public CadastroExternoEntity buscarPorId(Long id, Long idCliente) throws Exception {
		Map<String, Object> args = argsErroInterno(idCliente);
		args.put("ID", id);
		List<CadastroExternoEntity> lista = (List<CadastroExternoEntity>) baseEJB.pesquisaArgFixos(
				CadastroExternoEntity.class, "findErroInternoByIdAndCliente", args);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}

	public void resolverErro(Long idErro, Long idCliente) throws Exception {
		if (idErro == null) {
			return;
		}
		CadastroExternoEntity item = buscarPorId(idErro, idCliente);
		if (item == null) {
			return;
		}
		item.setStatusCadastroExterno(StatusCadastroExterno.PROCESSADO);
		item.setDataDecisao(new Date());
		item.setRemovido(true);
		baseEJB.alteraObjeto(item);
	}

	public void resolverErroPorPedestre(Long idPedestre, Long idCliente) throws Exception {
		if (idPedestre == null || idCliente == null) {
			return;
		}
		Map<String, Object> args = argsErroInterno(idCliente);
		args.put("ID_PEDESTRE", idPedestre);
		@SuppressWarnings("unchecked")
		List<CadastroExternoEntity> abertos = (List<CadastroExternoEntity>) baseEJB.pesquisaArgFixos(
				CadastroExternoEntity.class, "findErroInternoAbertoByPedestre", args);
		if (abertos == null || abertos.isEmpty()) {
			return;
		}
		for (CadastroExternoEntity item : abertos) {
			item.setStatusCadastroExterno(StatusCadastroExterno.PROCESSADO);
			item.setDataDecisao(new Date());
			item.setRemovido(true);
			baseEJB.alteraObjeto(item);
		}
	}

	public void descartar(Long idErro, Long idCliente) throws Exception {
		CadastroExternoEntity item = buscarPorId(idErro, idCliente);
		if (item == null) {
			throw new IllegalStateException("Registro de erro não encontrado ou já tratado.");
		}
		item.setStatusCadastroExterno(StatusCadastroExterno.RECUSADO);
		item.setDataDecisao(new Date());
		item.setRemovido(true);
		item.setMotivoRecusa("Descartado pelo operador");
		baseEJB.alteraObjeto(item);

		if (item.getPedestre() != null && item.getPedestre().getId() != null) {
			PedestreEntity p = (PedestreEntity) baseEJB.recuperaObjeto(PedestreEntity.class, item.getPedestre().getId());
			if (p != null) {
				p.setStatus(Status.INATIVO);
				p.setRemovido(true);
				baseEJB.alteraObjeto(p);
			}
		}
	}

	public String montarUrlRefazer(CadastroExternoEntity item) {
		if (item == null) {
			return null;
		}
		boolean simplificado = TipoCadastroExterno.CADASTRO_INTERNO_SIMPLIFICADO.equals(item.getTipo());
		String tipoParam = "vi";
		if (item.getPedestre() != null && item.getPedestre().getTipo() != null) {
			tipoParam = br.com.startjob.acesso.modelo.enumeration.TipoPedestre.VISITANTE
					.equals(item.getPedestre().getTipo()) ? "vi" : "pe";
		}
		StringBuilder url = new StringBuilder();
		if (simplificado) {
			url.append("/paginas/sistema/pedestres/cadastroSimplificado.xhtml?tipo=").append(tipoParam);
		} else {
			url.append("/paginas/sistema/pedestres/cadastroPedestre.xhtml");
			if (!"vi".equals(tipoParam)) {
				url.append("?tipo=").append(tipoParam);
			}
		}
		if (item.getPedestre() != null && item.getPedestre().getId() != null) {
			url.append(url.indexOf("?") >= 0 ? "&" : "?").append("id=").append(item.getPedestre().getId());
		}
		url.append(url.indexOf("?") >= 0 ? "&" : "?").append("origem=erro&idErro=").append(item.getId());
		return url.toString();
	}

	public boolean podeRefazer(CadastroExternoEntity item) {
		return item != null && item.getId() != null;
	}

	private static String truncarMotivo(String motivo) {
		if (motivo == null) {
			return null;
		}
		String t = motivo.trim();
		if (t.length() <= MOTIVO_MAX) {
			return t;
		}
		return t.substring(0, MOTIVO_MAX);
	}

}
