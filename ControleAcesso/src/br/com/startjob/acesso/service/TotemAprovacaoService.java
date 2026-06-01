package br.com.startjob.acesso.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.enumeration.StatusCadastroExterno;
import br.com.startjob.acesso.modelo.enumeration.TipoCadastroExterno;

/**
 * Solicitações de visitante pelo totem com aprovação da empresa visitada.
 */
public class TotemAprovacaoService {

	private final BaseEJBRemote baseEJB;

	public TotemAprovacaoService(BaseEJBRemote baseEJB) {
		this.baseEJB = baseEJB;
	}

	public long calcularTokenAprovacao(Long idCliente) throws Exception {
		ParametroEntity param = baseEJB.getParametroSistema(
				BaseConstant.PARAMETERS_NAME.DIAS_VALIDADE_LINK_CADASTRO_FACIAL_EXTERNO, idCliente);
		int dias = param != null ? Integer.parseInt(param.getValor()) : 3;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, dias);
		return cal.getTimeInMillis();
	}

	public static boolean isTokenAprovacaoValido(Long token) {
		return token != null && token >= System.currentTimeMillis();
	}

	@SuppressWarnings("unchecked")
	public CadastroExternoEntity gravarSolicitacaoTotem(ClienteEntity cliente, EmpresaEntity empresa,
			String cpfLimpo, String nome, String observacao, byte[] foto, Long tokenAprovacao) throws Exception {

		CadastroExternoEntity cadastro = new CadastroExternoEntity();
		cadastro.setCliente(cliente);
		cadastro.setEmpresa(empresa);
		cadastro.setPedestre(null);
		cadastro.setTipo(TipoCadastroExterno.TOTEM_VISITANTE);
		cadastro.setStatusCadastroExterno(StatusCadastroExterno.AGUARDANDO_APROVACAO);
		cadastro.setToken(tokenAprovacao);
		cadastro.setCpfVisitante(cpfLimpo);
		cadastro.setNomeVisitante(nome != null ? nome.trim() : null);
		cadastro.setObservacaoTotem(observacao != null ? observacao.trim() : null);
		cadastro.setPrimeiraFoto(foto);
		baseEJB.gravaObjeto(cadastro);
		return cadastro;
	}

	@SuppressWarnings("unchecked")
	public List<CadastroExternoEntity> listarPendentes(Long idCliente) throws Exception {
		Map<String, Object> args = new HashMap<>();
		args.put("ID_CLIENTE", idCliente);
		args.put("TIPO", TipoCadastroExterno.TOTEM_VISITANTE);
		args.put("STATUS", StatusCadastroExterno.AGUARDANDO_APROVACAO);
		List<CadastroExternoEntity> lista = (List<CadastroExternoEntity>) baseEJB.pesquisaArgFixos(
				CadastroExternoEntity.class, "findTotemPendentesByCliente", args);
		return lista != null ? lista : java.util.Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public CadastroExternoEntity buscarPorId(Long id, Long idCliente) throws Exception {
		Map<String, Object> args = new HashMap<>();
		args.put("ID", id);
		args.put("ID_CLIENTE", idCliente);
		args.put("TIPO", TipoCadastroExterno.TOTEM_VISITANTE);
		List<CadastroExternoEntity> lista = (List<CadastroExternoEntity>) baseEJB.pesquisaArgFixos(
				CadastroExternoEntity.class, "findTotemByIdAndCliente", args);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public CadastroExternoEntity buscarPendentePorToken(Long idCliente, Long token) throws Exception {
		if (!isTokenAprovacaoValido(token)) {
			return null;
		}
		Map<String, Object> args = new HashMap<>();
		args.put("ID_CLIENTE", idCliente);
		args.put("TIPO", TipoCadastroExterno.TOTEM_VISITANTE);
		args.put("TOKEN", token);
		args.put("STATUS", StatusCadastroExterno.AGUARDANDO_APROVACAO);
		List<CadastroExternoEntity> lista = (List<CadastroExternoEntity>) baseEJB.pesquisaArgFixos(
				CadastroExternoEntity.class, "findTotemByTokenAndCliente", args);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}

	public String montarUrlAprovacaoPublica(Long idCliente, long token, String baseUrl) {
		return baseUrl + "aprovacaoTotem.xhtml?cliente=" + idCliente + "&token=" + token;
	}

	public void marcarRecusado(CadastroExternoEntity cadastro, String motivo) throws Exception {
		cadastro.setStatusCadastroExterno(StatusCadastroExterno.RECUSADO);
		cadastro.setMotivoRecusa(motivo != null ? motivo.trim() : null);
		cadastro.setDataDecisao(new java.util.Date());
		cadastro.setToken(null);
		baseEJB.alteraObjeto(cadastro);
	}

	public void marcarAprovado(CadastroExternoEntity cadastro, br.com.startjob.acesso.modelo.entity.PedestreEntity pedestre)
			throws Exception {
		cadastro.setPedestre(pedestre);
		cadastro.setStatusCadastroExterno(StatusCadastroExterno.APROVADO);
		cadastro.setDataDecisao(new java.util.Date());
		cadastro.setDataCadastroDaFace(new java.util.Date());
		cadastro.setToken(null);
		baseEJB.alteraObjeto(cadastro);
	}

	/** Mantém solicitação pendente após falha no envio facial (visitante já gravado). */
	public void manterPendenteAposFalhaFacial(CadastroExternoEntity cadastro,
			br.com.startjob.acesso.modelo.entity.PedestreEntity pedestre, String detalheFalha) throws Exception {
		cadastro.setPedestre(pedestre);
		cadastro.setStatusCadastroExterno(StatusCadastroExterno.AGUARDANDO_APROVACAO);
		if (detalheFalha != null && !detalheFalha.trim().isEmpty()) {
			String obs = cadastro.getObservacaoTotem();
			String nota = "[Facial pendente] " + detalheFalha.trim();
			cadastro.setObservacaoTotem(obs != null && !obs.isEmpty() ? obs + " — " + nota : nota);
		}
		baseEJB.alteraObjeto(cadastro);
	}

	@SuppressWarnings("unchecked")
	public CadastroExternoEntity gravarSolicitacaoTotemComPedestre(ClienteEntity cliente, EmpresaEntity empresa,
			br.com.startjob.acesso.modelo.entity.PedestreEntity pedestre, String observacao, byte[] foto,
			Long tokenAprovacao, String detalheFalhaFacial) throws Exception {

		CadastroExternoEntity cadastro = new CadastroExternoEntity();
		cadastro.setCliente(cliente);
		cadastro.setEmpresa(empresa);
		cadastro.setPedestre(pedestre);
		cadastro.setTipo(TipoCadastroExterno.TOTEM_VISITANTE);
		cadastro.setStatusCadastroExterno(StatusCadastroExterno.AGUARDANDO_APROVACAO);
		cadastro.setToken(tokenAprovacao);
		if (pedestre != null) {
			cadastro.setCpfVisitante(pedestre.getCpf());
			cadastro.setNomeVisitante(pedestre.getNome());
		}
		String obs = observacao != null ? observacao.trim() : "";
		if (detalheFalhaFacial != null && !detalheFalhaFacial.trim().isEmpty()) {
			String nota = "[Facial pendente] " + detalheFalhaFacial.trim();
			obs = obs.isEmpty() ? nota : obs + " — " + nota;
		}
		cadastro.setObservacaoTotem(obs.isEmpty() ? null : obs);
		if (foto != null && foto.length > 0) {
			cadastro.setPrimeiraFoto(foto);
		} else if (pedestre != null && pedestre.getFoto() != null) {
			cadastro.setPrimeiraFoto(pedestre.getFoto());
		}
		baseEJB.gravaObjeto(cadastro);
		return cadastro;
	}

	public int contarPendentes(Long idCliente) throws Exception {
		return listarPendentes(idCliente).size();
	}

	@SuppressWarnings("unchecked")
	public EmpresaEntity buscaEmpresaPorId(Long idEmpresa, Long idCliente) throws Exception {
		Map<String, Object> args = new HashMap<>();
		args.put("ID", idEmpresa);
		List<EmpresaEntity> lista = (List<EmpresaEntity>) baseEJB.pesquisaArgFixos(EmpresaEntity.class, "findById", args);
		if (lista == null) {
			return null;
		}
		return lista.stream()
				.filter(e -> e.getCliente() != null && idCliente.equals(e.getCliente().getId()))
				.findFirst()
				.orElse(null);
	}

	public static boolean empresaPermiteCadastroAutomatico(EmpresaEntity empresa) {
		return empresa != null && Boolean.TRUE.equals(empresa.getAutoAtendimentoLiberado());
	}

}
