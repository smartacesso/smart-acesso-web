package br.com.startjob.acesso.modelo.ejb;

import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.ejb3.annotation.TransactionTimeout;

import com.rhid.services.RhidDominioResolver;
import com.rhid.services.RhidPersonPayloadBuilder;
import com.rhid.services.RhidService;
import com.rhid.services.RhidSincronizacaoControle;
import com.rhid.services.catalogo.RhidCatalogoDominio;
import com.rhid.services.catalogo.RhidCatalogoLoader;
import com.rhid.services.catalogo.RhidCatalogoResolver;
import com.rhid.services.dto.RhidFuncionarioExternoDTO;
import com.rhid.services.dto.RhidLoginResponseDTO;
import com.rhid.services.dto.RhidOperacaoResultDTO;
import com.rhid.services.fonte.RhidFuncionarioFonte;
import com.rhid.services.fonte.RhidFuncionarioFonteFactory;

import br.com.startjob.acesso.modelo.entity.ConfiguracaoRhidEntity;
import br.com.startjob.acesso.modelo.entity.DominioRhidEntity;

@Stateless
public class RhidIntegracaoEJB extends BaseEJB implements RhidIntegracaoEJBRemote {

	private static final long serialVersionUID = 1L;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ConfiguracaoRhidEntity salvarConfiguracao(ConfiguracaoRhidEntity configuracao) throws Exception {
		if (configuracao == null) {
			throw new IllegalArgumentException("Configuração RHID não informada.");
		}

		String email = normalizarEmail(configuracao.getEmail());
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Informe o e-mail de acesso ao RHID.");
		}
		if (configuracao.getSenha() == null || configuracao.getSenha().trim().isEmpty()) {
			throw new IllegalArgumentException("Informe a senha de acesso ao RHID.");
		}

		validarEmailUnico(email, configuracao.getId());
		validarDominiosObrigatorios(configuracao);
		configuracao.setEmail(email);
		prepararValoresPadrao(configuracao);
		prepararDominios(configuracao);

		Long idSalvo;
		if (configuracao.getId() == null) {
			Object[] retorno = gravaObjeto(configuracao);
			idSalvo = ((ConfiguracaoRhidEntity) retorno[0]).getId();
		} else {
			ConfiguracaoRhidEntity gerenciada = (ConfiguracaoRhidEntity) recuperaObjeto(
					ConfiguracaoRhidEntity.class, configuracao.getId());
			aplicarCamposConfiguracao(configuracao, gerenciada);
			sincronizarDominios(gerenciada, configuracao.getDominios());
			alteraObjeto(gerenciada);
			idSalvo = gerenciada.getId();
		}

		return carregarConfiguracaoPorId(idSalvo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ConfiguracaoRhidEntity buscarConfiguracaoPorId(Long id) throws Exception {
		if (id == null) {
			return null;
		}
		return carregarConfiguracaoPorId(id);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("unchecked")
	public List<ConfiguracaoRhidEntity> listarConfiguracoes() throws Exception {
		List<ConfiguracaoRhidEntity> configs = (List<ConfiguracaoRhidEntity>) pesquisaSimples(
				ConfiguracaoRhidEntity.class, "findAll", new HashMap<>());
		if (configs == null) {
			return new java.util.ArrayList<>();
		}
		for (ConfiguracaoRhidEntity config : configs) {
			if (config.getDominios() != null) {
				config.getDominios().size();
			}
		}
		return configs;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void excluirConfiguracao(Long id) throws Exception {
		if (id == null) {
			throw new IllegalArgumentException("Informe a configuração RHID a excluir.");
		}

		ConfiguracaoRhidEntity gerenciada = carregarConfiguracaoPorId(id);
		if (gerenciada == null) {
			throw new IllegalStateException("Configuração RHID não encontrada.");
		}

		Date agora = new Date();
		gerenciada.setRemovido(true);
		gerenciada.setDataRemovido(agora);
		if (gerenciada.getDominios() != null) {
			for (DominioRhidEntity dominio : gerenciada.getDominios()) {
				dominio.setRemovido(true);
				dominio.setDataRemovido(agora);
			}
		}
		alteraObjeto(gerenciada);
	}

	private ConfiguracaoRhidEntity carregarConfiguracaoPorId(Long id) throws Exception {
		ConfiguracaoRhidEntity salva = (ConfiguracaoRhidEntity) recuperaObjeto(ConfiguracaoRhidEntity.class, id);
		if (salva.getDominios() != null) {
			salva.getDominios().size();
		}
		return salva;
	}

	private void aplicarCamposConfiguracao(ConfiguracaoRhidEntity origem, ConfiguracaoRhidEntity destino) {
		destino.setEmail(origem.getEmail());
		destino.setSenha(origem.getSenha());
		destino.setDominio(origem.getDominio());
		destino.setUrlBase(origem.getUrlBase());
		destino.setIntervaloMinutos(origem.getIntervaloMinutos());
		destino.setExportacaoAutomatica(origem.getExportacaoAutomatica());
		destino.setSemDominioAcao(origem.getSemDominioAcao());
	}

	private void sincronizarDominios(ConfiguracaoRhidEntity configuracao,
			List<DominioRhidEntity> dominiosOrigem) {
		configuracao.getDominios().clear();
		for (DominioRhidEntity domOrigem : dominiosOrigem) {
			DominioRhidEntity dominio = new DominioRhidEntity();
			dominio.setNomeDominio(domOrigem.getNomeDominio());
			dominio.setConfiguracao(configuracao);
			configuracao.getDominios().add(dominio);
		}
	}

	private void validarDominiosObrigatorios(ConfiguracaoRhidEntity configuracao) {
		if (configuracao.getDominios() == null || configuracao.getDominios().isEmpty()) {
			throw new IllegalArgumentException(
					"Informe ao menos um domínio RHID. O login na API exige o parâmetro domain.");
		}

		for (DominioRhidEntity dominio : configuracao.getDominios()) {
			if (dominio.getNomeDominio() == null || dominio.getNomeDominio().trim().isEmpty()) {
				throw new IllegalArgumentException("Todos os domínios devem possuir um nome válido.");
			}
		}
	}

	private void validarEmailUnico(String email, Long idAtual) throws Exception {
		Map<String, Object> args = new HashMap<>();
		args.put("EMAIL", email);

		@SuppressWarnings("unchecked")
		List<ConfiguracaoRhidEntity> existentes = (List<ConfiguracaoRhidEntity>) pesquisaArgFixos(
				ConfiguracaoRhidEntity.class, "findByEmail", args);

		if (existentes == null || existentes.isEmpty()) {
			return;
		}

		for (ConfiguracaoRhidEntity existente : existentes) {
			if (idAtual == null || !idAtual.equals(existente.getId())) {
				throw new IllegalArgumentException(
						"Já existe uma configuração RHID cadastrada para o e-mail informado.");
			}
		}
	}

	private void prepararValoresPadrao(ConfiguracaoRhidEntity configuracao) {
		if (configuracao.getUrlBase() == null || configuracao.getUrlBase().trim().isEmpty()) {
			configuracao.setUrlBase("https://rhid.com.br/v2");
		}
		if (configuracao.getIntervaloMinutos() == null) {
			configuracao.setIntervaloMinutos(60);
		}
		if (configuracao.getExportacaoAutomatica() == null) {
			configuracao.setExportacaoAutomatica(Boolean.FALSE);
		}
		if (configuracao.getSemDominioAcao() == null) {
			configuracao.setSemDominioAcao(com.rhid.services.RhidSemDominioAcaoEnum.IGNORAR);
		}
		if (configuracao.getDominios() == null) {
			configuracao.setDominios(new java.util.ArrayList<DominioRhidEntity>());
		}
	}

	private void prepararDominios(ConfiguracaoRhidEntity configuracao) {
		for (DominioRhidEntity dominio : configuracao.getDominios()) {
			dominio.setConfiguracao(configuracao);
		}
	}

	private String normalizarEmail(String email) {
		if (email == null) {
			return null;
		}
		return email.trim().toLowerCase();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
	public RhidOperacaoResultDTO exportarRhidAutomatico() throws Exception {
		ConfiguracaoRhidEntity config = carregarConfiguracaoRhid();
		if (config == null) {
			throw new IllegalStateException("Configuração RHID não encontrada.");
		}
		boolean completa = config.getUltimaExportacao() == null;
		return exportarRhid(completa);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
	public RhidOperacaoResultDTO exportarRhid(boolean completa) throws Exception {
		ConfiguracaoRhidEntity config = carregarConfiguracaoRhid();
		if (config == null) {
			throw new IllegalStateException("Configuração RHID não encontrada. Cadastre as credenciais na tela de configuração.");
		}
		return exportarRhidInterno(config, completa);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
	public RhidOperacaoResultDTO exportarRhidPorId(Long configId, boolean completa) throws Exception {
		if (configId == null) {
			throw new IllegalArgumentException("Informe a configuração RHID para exportação.");
		}
		ConfiguracaoRhidEntity config = carregarConfiguracaoPorId(configId);
		if (config == null) {
			throw new IllegalStateException("Configuração RHID não encontrada.");
		}
		return exportarRhidInterno(config, completa);
	}

	private RhidOperacaoResultDTO exportarRhidInterno(ConfiguracaoRhidEntity config, boolean completa) throws Exception {
		RhidOperacaoResultDTO resultado = new RhidOperacaoResultDTO();
		resultado.setCompleta(completa);

		if (config.getEmail() == null || config.getSenha() == null) {
			throw new IllegalStateException("E-mail e senha do RHID são obrigatórios.");
		}

		List<DominioRhidEntity> dominios = resolverDominios(config);
		RhidFuncionarioFonte fonte = RhidFuncionarioFonteFactory.criarFonte();
		List<RhidFuncionarioExternoDTO> funcionarios = buscarFuncionariosExternos(fonte, completa,
				config.getUltimaExportacao());
		Map<String, Integer> mapaIds = RhidSincronizacaoControle.carregarMapa(config);

		if (funcionarios == null || funcionarios.isEmpty()) {
			resultado.addMensagem("Nenhum funcionário retornado pela API externa para exportação "
					+ (completa ? "completa" : "incremental") + ".");
			atualizarUltimaExportacao(config.getId());
			return resultado;
		}

		resultado.addMensagem("Fonte: mock (substituir por API externa quando disponível)");
		resultado.addMensagem("Funcionários obtidos da API externa: " + funcionarios.size());
		resultado.addMensagem("Domínios configurados: " + dominios.size()
				+ " (roteamento por nomeDominio; sem domínio: "
				+ RhidDominioResolver.descreverAcaoSemDominio(config.getSemDominioAcao()) + ")");

		Map<DominioRhidEntity, List<RhidFuncionarioExternoDTO>> porDominio = new LinkedHashMap<>();
		for (DominioRhidEntity dominio : dominios) {
			porDominio.put(dominio, new ArrayList<>());
		}

		for (RhidFuncionarioExternoDTO funcionario : funcionarios) {
			try {
				List<DominioRhidEntity> destinos = RhidDominioResolver.resolverDestinos(funcionario, dominios,
						config.getSemDominioAcao());
				if (destinos.isEmpty()) {
					resultado.addMensagem("Funcionário " + funcionario.getNome() + " (" + funcionario.getIdExterno()
							+ ") ignorado: sem nomeDominio (configuração: "
							+ RhidDominioResolver.descreverAcaoSemDominio(config.getSemDominioAcao()) + ").");
					continue;
				}
				for (DominioRhidEntity dominio : destinos) {
					porDominio.get(dominio).add(funcionario);
				}
			} catch (Exception e) {
				resultado.incrementErros();
				resultado.addMensagem("Funcionário " + funcionario.getNome() + " (" + funcionario.getIdExterno()
						+ ") ignorado: " + e.getMessage());
			}
		}

		String urlBase = config.getUrlBase() != null ? config.getUrlBase() : "https://rhid.com.br/v2";
		RhidService rhidService = new RhidService(urlBase);

		boolean interromperPorLimite = false;
		for (Map.Entry<DominioRhidEntity, List<RhidFuncionarioExternoDTO>> entry : porDominio.entrySet()) {
			DominioRhidEntity dominio = entry.getKey();
			List<RhidFuncionarioExternoDTO> funcionariosDominio = entry.getValue();
			if (funcionariosDominio.isEmpty()) {
				continue;
			}

			resultado.addMensagem("Processando domínio: " + dominio.getNomeDominio()
					+ " (funcionários: " + funcionariosDominio.size() + ")");
			rhidService.inicializarCredenciais(config.getEmail(), config.getSenha(), dominio.getNomeDominio());

			RhidLoginResponseDTO login = rhidService.loginAutenticar();
			if (login.requerSelecaoDominio()) {
				resultado.incrementErros();
				resultado.addMensagem("Domínio '" + dominio.getNomeDominio()
						+ "' inválido. Credenciais possuem múltiplos clientes RHID — confira o nome cadastrado.");
				continue;
			}
			if (login.getAccessToken() == null || login.getAccessToken().trim().isEmpty()) {
				resultado.incrementErros();
				String detalhe = login.getError() != null && !login.getError().trim().isEmpty()
						? login.getError()
						: "Token de acesso não retornado pelo RHID.";
				resultado.addMensagem("Login RHID falhou no domínio " + dominio.getNomeDominio() + ": " + detalhe);
				continue;
			}

			RhidCatalogoDominio catalogo;
			try {
				catalogo = RhidCatalogoLoader.carregar(rhidService);
				resultado.addMensagem("Catálogo RHID carregado (" + dominio.getNomeDominio() + "): " + catalogo.resumo());
			} catch (Exception e) {
				resultado.incrementErros();
				resultado.addMensagem("Falha ao carregar catálogos RHID no domínio " + dominio.getNomeDominio() + ": "
						+ (e.getMessage() != null ? e.getMessage() : "erro desconhecido"));
				continue;
			}

			for (RhidFuncionarioExternoDTO funcionario : funcionariosDominio) {
				resultado.incrementProcessados();
				try {
					RhidCatalogoResolver.aplicarIdsInternos(funcionario, catalogo);
					sincronizarFuncionarioComRhid(rhidService, funcionario, config, dominio, mapaIds, resultado);
				} catch (Exception e) {
					resultado.incrementErros();
					String mensagem = e.getMessage() != null ? e.getMessage() : "Erro inesperado";
					resultado.addMensagem("Erro no funcionário " + funcionario.getNome() + " ("
							+ funcionario.getIdExterno() + ") no domínio " + dominio.getNomeDominio() + ": "
							+ mensagem);
					if (mensagem.toLowerCase().contains("limite de funcion")) {
						resultado.addMensagem(
								"Exportação interrompida: limite de licença RHID atingido. Contate a revendedora.");
						interromperPorLimite = true;
						break;
					}
				}
			}
			if (interromperPorLimite) {
				break;
			}
		}

		salvarMapaIds(config.getId(), mapaIds);
		if (resultado.getTotalErros() == 0) {
			atualizarUltimaExportacao(config.getId());
		} else {
			resultado.addMensagem(
					"ultimaExportacao não atualizada devido a erros — a próxima incremental reprocessará os pendentes.");
		}

		resultado.addMensagem("Exportação " + (completa ? "completa" : "incremental")
				+ " finalizada. Processados: " + resultado.getTotalProcessados()
				+ ", criados: " + resultado.getTotalCriados()
				+ ", atualizados: " + resultado.getTotalAtualizados()
				+ ", erros: " + resultado.getTotalErros() + ".");
		return resultado;
	}

	private void salvarMapaIds(Long configId, Map<String, Integer> mapaIds) throws Exception {
		ConfiguracaoRhidEntity gerenciada = carregarConfiguracaoPorId(configId);
		gerenciada.setMapaIdsRhid(serializarMapa(mapaIds));
		alteraObjeto(gerenciada);
	}

	private void atualizarUltimaExportacao(Long configId) throws Exception {
		ConfiguracaoRhidEntity gerenciada = carregarConfiguracaoPorId(configId);
		gerenciada.setUltimaExportacao(new Date());
		alteraObjeto(gerenciada);
	}

	private String serializarMapa(Map<String, Integer> mapa) {
		javax.json.JsonObjectBuilder builder = javax.json.Json.createObjectBuilder();
		for (Map.Entry<String, Integer> entry : mapa.entrySet()) {
			builder.add(entry.getKey(), entry.getValue());
		}
		return builder.build().toString();
	}

	private List<DominioRhidEntity> resolverDominios(ConfiguracaoRhidEntity config) {
		List<DominioRhidEntity> dominios = config.getDominios();
		if (dominios == null || dominios.isEmpty()) {
			throw new IllegalStateException("Informe ao menos um domínio RHID na configuração.");
		}
		return dominios;
	}

	private List<RhidFuncionarioExternoDTO> buscarFuncionariosExternos(RhidFuncionarioFonte fonte, boolean completa,
			Date ultimaExportacao) {
		if (completa) {
			return fonte.buscarTodos();
		}
		return fonte.buscarAlteradosDesde(ultimaExportacao);
	}

	@SuppressWarnings("unchecked")
	private ConfiguracaoRhidEntity carregarConfiguracaoRhid() throws Exception {
		List<ConfiguracaoRhidEntity> configs = (List<ConfiguracaoRhidEntity>) pesquisaSimples(
				ConfiguracaoRhidEntity.class, "findAll", new HashMap<>());
		if (configs == null || configs.isEmpty()) {
			return null;
		}
		ConfiguracaoRhidEntity config = configs.get(0);
		config.getDominios().size();
		return config;
	}

	private void sincronizarFuncionarioComRhid(RhidService rhidService, RhidFuncionarioExternoDTO funcionario,
			ConfiguracaoRhidEntity config, DominioRhidEntity dominio, Map<String, Integer> mapaIds,
			RhidOperacaoResultDTO resultado) throws Exception {

		if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
			throw new RuntimeException("Nome obrigatório.");
		}
		if (!com.rhid.services.RhidPisUtil.possuiPisOuCpf(funcionario)) {
			throw new RuntimeException("Informe PIS ou CPF para exportação RHID.");
		}
		if (!possuiCpfValido(funcionario)) {
			throw new RuntimeException("CPF obrigatório e deve conter 11 dígitos para exportação RHID.");
		}
		if (funcionario.getIdExterno() == null || funcionario.getIdExterno().trim().isEmpty()) {
			throw new RuntimeException("ID externo obrigatório.");
		}
		if (funcionario.getIdCompany() == null) {
			throw new RuntimeException(
					"idCompany não resolvido — informe idCompany ou cnpjEmpresa (busca RHID somente por CNPJ).");
		}

		Integer idRhid = RhidSincronizacaoControle.obterIdRhid(mapaIds, dominio.getNomeDominio(),
				funcionario.getIdExterno());

		if (idRhid != null) {
			String payload = RhidPersonPayloadBuilder.toUpdatePayload(funcionario, config, dominio, idRhid);
			rhidService.atualizarFuncionario(payload);
			resultado.incrementAtualizados();
		} else {
			if (funcionario.getIdShift() == null) {
				throw new RuntimeException(
						"idShift não resolvido — informe id ou nomeHorario (catálogo shift.svc/a) para cadastro.");
			}
			String payload = RhidPersonPayloadBuilder.toCreatePayload(funcionario, config, dominio);
			String respostaJson = rhidService.cadastrarFuncionario(payload);
			Integer novoId = rhidService.extrairNovoId(respostaJson);
			if (novoId != null) {
				RhidSincronizacaoControle.registrarIdRhid(mapaIds, dominio.getNomeDominio(),
						funcionario.getIdExterno(), novoId);
			}
			resultado.incrementCriados();
		}
	}

	private boolean possuiCpfValido(RhidFuncionarioExternoDTO funcionario) {
		return com.rhid.services.RhidPisUtil.normalizarCpf11(funcionario.getCpf()) != null;
	}
}
