package br.com.startjob.acesso.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;

/**
 * Montagem e persistência de visitante após aprovação de solicitação do totem.
 */
public final class TotemAprovacaoPedestreHelper {

	private TotemAprovacaoPedestreHelper() {
	}

	public static PedestreEntity montarPedestreFromSolicitacao(CadastroExternoEntity sol, BaseEJBRemote baseEJB,
			UsuarioEntity usuario) throws Exception {

		Long idCliente = sol.getCliente().getId();
		String cpfLimpo = sol.getCpfVisitante() != null ? sol.getCpfVisitante().replaceAll("\\D", "") : "";
		if (cpfLimpo.length() < 11) {
			throw new IllegalArgumentException("CPF inválido na solicitação");
		}

		PedestreEntity pedestre = buscaPedestrePeloCpf(baseEJB, cpfLimpo, idCliente);
		if (pedestre != null && pedestre.isPedestre()) {
			throw new IllegalStateException("CPF pertence a colaborador, não visitante");
		}

		if (pedestre == null) {
			pedestre = new PedestreEntity();
			pedestre.setTipo(TipoPedestre.VISITANTE);
			pedestre.setStatus(Status.ATIVO);
			pedestre.setCliente(sol.getCliente());
			pedestre.setCpf(cpfLimpo);
			aplicarRegraPadraoVisitante(pedestre, baseEJB, idCliente);
		}

		if (sol.getNomeVisitante() != null && !sol.getNomeVisitante().trim().isEmpty()) {
			pedestre.setNome(sol.getNomeVisitante().trim());
		}
		if (sol.getObservacaoTotem() != null) {
			pedestre.setObservacoes(sol.getObservacaoTotem().trim());
		}
		if (sol.getPrimeiraFoto() != null && sol.getPrimeiraFoto().length > 0) {
			pedestre.setFoto(sol.getPrimeiraFoto());
		}

		EmpresaEntity empresa = sol.getEmpresa();
		if (empresa != null && empresa.getId() != null) {
			EmpresaEntity valida = buscaEmpresaPorId(baseEJB, empresa.getId(), idCliente);
			String nomeEmp = valida != null ? valida.getNome() : empresa.getNome();
			pedestre.aplicarEmpresaVisitadaInformada(nomeEmp, valida != null ? valida : empresa);
		}

		if (usuario != null) {
			pedestre.setUsuario(usuario);
		}

		if (pedestre.getCodigoCartaoAcesso() == null || pedestre.getCodigoCartaoAcesso().trim().isEmpty()) {
			pedestre.setCodigoCartaoAcesso(cpfLimpo);
		}

		normalizarListasPedestre(pedestre);
		return pedestre;
	}

	public static PedestreEntity persistir(PedestreEntity pedestre, BaseEJBRemote baseEJB) throws Exception {
		if (pedestre.getId() == null) {
			Object[] ret = baseEJB.gravaObjeto(pedestre);
			return (PedestreEntity) ret[0];
		}
		baseEJB.alteraObjeto(pedestre);
		return (PedestreEntity) baseEJB.recuperaObjeto(PedestreEntity.class, pedestre.getId());
	}

	@SuppressWarnings("unchecked")
	private static PedestreEntity buscaPedestrePeloCpf(BaseEJBRemote baseEJB, String cpf, Long idCliente)
			throws Exception {
		Map<String, Object> args = new HashMap<>();
		args.put("CPF", cpf);
		args.put("ID_CLIENTE", idCliente);
		List<PedestreEntity> lista = (List<PedestreEntity>) baseEJB.pesquisaArgFixos(PedestreEntity.class,
				"findByCpfAndIdCliente", args);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static EmpresaEntity buscaEmpresaPorId(BaseEJBRemote baseEJB, Long idEmpresa, Long idCliente)
			throws Exception {
		Map<String, Object> args = new HashMap<>();
		args.put("ID", idEmpresa);
		List<EmpresaEntity> lista = (List<EmpresaEntity>) baseEJB.pesquisaArgFixos(EmpresaEntity.class, "findById",
				args);
		if (lista == null) {
			return null;
		}
		return lista.stream()
				.filter(e -> e.getCliente() != null && idCliente.equals(e.getCliente().getId()))
				.findFirst()
				.orElse(null);
	}

	private static void aplicarRegraPadraoVisitante(PedestreEntity p, BaseEJBRemote baseEJB, Long idCliente)
			throws Exception {
		if (p.getRegras() != null && !p.getRegras().isEmpty()) {
			return;
		}
		RegraEntity regra = buscaRegraPeloNome(baseEJB, BaseConstant.NOME_REGRA_PADRAO_VISITANTE, idCliente);
		if (regra == null) {
			regra = cadastraNovaRegraVisitante(baseEJB, idCliente);
		}
		PedestreRegraEntity pedestreRegra = new PedestreRegraEntity();
		pedestreRegra.setRegra(regra);
		pedestreRegra.setQtdeTotalDeCreditos(1L);
		pedestreRegra.setPedestre(p);
		p.setRegras(Arrays.asList(pedestreRegra));
	}

	private static RegraEntity cadastraNovaRegraVisitante(BaseEJBRemote baseEJB, Long idClienteRef) throws Exception {
		RegraEntity regra = new RegraEntity();
		regra.setNome(BaseConstant.NOME_REGRA_PADRAO_VISITANTE);
		regra.setTipoPedestre(TipoPedestre.VISITANTE);
		regra.setTipo(TipoRegra.ACESSO_UNICO);
		regra.setStatus(Status.ATIVO);
		ClienteEntity cliente = new ClienteEntity();
		cliente.setId(idClienteRef);
		regra.setCliente(cliente);
		return (RegraEntity) baseEJB.gravaObjeto(regra)[0];
	}

	@SuppressWarnings("unchecked")
	private static RegraEntity buscaRegraPeloNome(BaseEJBRemote baseEJB, String nomeRegra, Long idClienteRef)
			throws Exception {
		Map<String, Object> args = new HashMap<>();
		args.put("NOME_REGRA", nomeRegra);
		args.put("ID_CLIENTE", idClienteRef);
		List<RegraEntity> regras = (List<RegraEntity>) baseEJB.pesquisaArgFixosLimitado(RegraEntity.class, "findByNome",
				args, 0, 1);
		if (regras != null && !regras.isEmpty()) {
			return regras.get(0);
		}
		return null;
	}

	private static void normalizarListasPedestre(PedestreEntity p) {
		if (p.getEndereco() == null || p.getEndereco().getCep() == null
				|| p.getEndereco().getCep().trim().isEmpty()) {
			p.setEndereco(null);
		}
		if (p.getDepartamento() != null && p.getDepartamento().getId() == null) {
			p.setDepartamento(null);
		}
		if (p.getCargo() != null && p.getCargo().getId() == null) {
			p.setCargo(null);
		}
		if (p.getCentroCusto() != null && p.getCentroCusto().getId() == null) {
			p.setCentroCusto(null);
		}
	}

}
