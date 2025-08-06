package br.com.startjob.acesso.tasks;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.totvs.dto.CadastroDTO;
import com.totvs.services.IntegracaoTotvsEducacionalService;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.DepartamentoEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.IntegracaoSalesianoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PlanoEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;
import br.com.startjob.acesso.services.BaseServlet;
import br.com.startjob.acesso.to.TotvsEdu.NivelDeEnsino;
import br.com.startjob.acesso.to.TotvsEdu.PermissoesEduTotvs;
import br.com.startjob.acesso.to.TotvsEdu.TipoTotvsEdu;

public class ImportarSalesianoTask extends TimerTask {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private final BaseEJBRemote baseEJB;
	private final IntegracaoTotvsEducacionalService integracaoTotvsEducacionalService = new IntegracaoTotvsEducacionalService();

	public ImportarSalesianoTask() throws Exception {
		baseEJB = ((BaseEJBRemote) BaseServlet.getEjb(BaseEJBRemote.class));
	}

	@Override
	public void run() {
		logger.info("Iniciando processo da integração Salesiano...");
		try {
			long inicio = System.currentTimeMillis();
			processaSalesiano();
			logger.info("Processo da integração Salesiano finalizado... " + (System.currentTimeMillis() - inicio)+"ms");

		} catch (Exception e) {
			logger.severe("Erro ao executar a integração Salesiano");
			e.printStackTrace();
		}
	}

	private void processaSalesiano() {
		try {
			List<CadastroDTO> cadastros = new ArrayList<CadastroDTO>();
			// buscar linha da tabela Salesiano pelo id 1
			IntegracaoSalesianoEntity integracaoSalesianoEntity = buscaIntegracaoSalesianoPorId(1L);

			if (integracaoSalesianoEntity == null) {
			    logger.info("Não encontrou a configuração de integração para Salesiano (ID 1)");
			    return;
			}
			
			// criar data de backup: new Date();
			Date updateAt = new Date();
			String resultado = integracaoTotvsEducacionalService.getCadastros(integracaoSalesianoEntity.getLastUpate());
			
			try {
				cadastros = integracaoTotvsEducacionalService.parseCadastros(resultado);
				System.out.println("quantidade de cadastros : " + cadastros.size());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (Objects.isNull(cadastros) || cadastros.isEmpty()) {
				//Mesmo vindo vazio salva?
				integracaoSalesianoEntity.setLastUpate(updateAt);
				baseEJB.gravaObjeto(integracaoSalesianoEntity);
				
				logger.info("Sem alunos Salesiano para processar");
				return;
			}
			processaAlunosTotvs(cadastros);
			
			// atulizar a linha da tabela salesiano com o campo de backup criado acima
			integracaoSalesianoEntity.setLastUpate(updateAt);
			baseEJB.alteraObjeto(integracaoSalesianoEntity);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private IntegracaoSalesianoEntity buscaIntegracaoSalesianoPorId(Long id) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID", id);

		@SuppressWarnings("unchecked")
		List<IntegracaoSalesianoEntity> importacao = (List<IntegracaoSalesianoEntity>) baseEJB.pesquisaArgFixos(IntegracaoSalesianoEntity.class,
				"findById", args);

		if (importacao != null && !importacao.isEmpty()) {
			return importacao.get(0);
		}

		return null;
	}

	private void processaAlunosTotvs(List<CadastroDTO> cadastros) {
		for (CadastroDTO aluno : cadastros) {
			try {
				ClienteEntity cliente = recuperaOuCriaCliente(aluno);
				salvarCadastro(aluno, cliente);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private ClienteEntity recuperaOuCriaCliente(CadastroDTO aluno) throws Exception {
		ClienteEntity clienteRecuperado = buscaClientesPorFilialEColigada(String.valueOf(aluno.getCodColigada()),
				String.valueOf(aluno.getCodFilial()));

		if (Objects.nonNull(clienteRecuperado)) {
			return clienteRecuperado;
		}

		return criaClienteAndPlano(aluno, baseEJB);
	}

	private ClienteEntity criaClienteAndPlano(CadastroDTO aluno, BaseEJBRemote baseEJB)
			throws Exception, NoSuchAlgorithmException, UnsupportedEncodingException {
		// cria cliente
		ClienteEntity newCliente = new ClienteEntity();
		newCliente.setCnpj(aluno.getCnpjFilial());
		newCliente.setCodcoligad(String.valueOf(aluno.getCodColigada()));
		newCliente.setCodfilial(String.valueOf(aluno.getCodFilial()));
		newCliente.setNome(aluno.getNomeFilial());

		String unidadeOrganizacional = aluno.getNomeColigada() + gerarUnidade(aluno.getNomeFilial().trim().replaceAll("\\s+", ""));
		newCliente.setNomeUnidadeOrganizacional(unidadeOrganizacional);

		newCliente.setEmail("salasiano@adm.com");
		newCliente.setCelular("31999999999");
		newCliente.setTelefone("3199999999");
		newCliente.setStatus(Status.ATIVO);

		// salva cliente
		newCliente = (ClienteEntity) baseEJB.gravaObjeto(newCliente)[0];

		// cria plano
		PlanoEntity newPlano = new PlanoEntity();
		Date dataPlano = new Date();
		newPlano.setCliente(newCliente);
		newPlano.setNome(String.valueOf(aluno.getCodFilial()));
		newPlano.setStatus(Status.ATIVO);
		newPlano.setExistente(true);
		newPlano.setPeriodicidadeCobranca("1");
		newPlano.setDiaVencimento(10L);
		newPlano.setValor(100D);

		newPlano.setInicio(dataPlano);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataPlano);
		cal.add(Calendar.YEAR, 5); // adiciona 1 ano
		Date dataFim = cal.getTime(); // nova data com 1 ano a mais
		newPlano.setFim(dataFim);

		// salva plano
		baseEJB.gravaObjeto(newPlano);

		// cria usuario
		UsuarioEntity newUsuario = new UsuarioEntity();
		newUsuario.setCliente(newCliente);
		newUsuario.setAcessaWeb(true);
		newUsuario.setNome(newCliente.getNome());
		newUsuario.setStatus(newCliente.getStatus());
		newUsuario.setLogin("admin");
		newUsuario.setSenha(EncryptionUtils.encrypt("123456"));
		newUsuario.setTelefone(newCliente.getTelefone());
		newUsuario.setCelular(newCliente.getCelular());
		newUsuario.setEmail(newCliente.getEmail());
		newUsuario.setPerfil(PerfilAcesso.ADMINISTRADOR);

		// salva usuario
		baseEJB.gravaObjeto(newUsuario);

		return newCliente;
	}
	
	
	private void salvarCadastro(CadastroDTO cadastro, ClienteEntity cliente) throws Exception {
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    PedestreEntity novoCadastro = buscaPedestrePorMatricula(cadastro.getMatricula(), cliente.getId());

	    if (novoCadastro == null) {
	        String tipo = TipoTotvsEdu.fromTabelaRm(cadastro.getOrigem()).getDesc();

	        String nome = cadastro.getNome();
	        String cpf = cadastro.getCpf();
	        String matricula = cadastro.getMatricula();
	        String codigoCartao = matricula.replaceAll("\\D", "");

	        EmpresaEntity empresa = recuperaEmpresa(cadastro.getNomeColigada(), cliente);

	        novoCadastro = criarPedestre(nome, cpf, codigoCartao, tipo, cliente, empresa);
	        novoCadastro.setMatricula(matricula);

			// caso especial: responsável
			if (tipo == TipoTotvsEdu.ALUNO.getDesc() && NivelDeEnsino.EDUCACAO_BASICA.getNivel().equals(cadastro.getNivelEnsino())
					&& cadastro.getCpfCnpfResponsavel() != null && !cadastro.getCpfCnpfResponsavel().isEmpty()) {
				PedestreEntity responsavel = buscaPedestrePorCpf(cadastro.getCpfCnpfResponsavel(), cliente.getId());

				if (responsavel == null) {
					String cpfResp = cadastro.getCpfCnpfResponsavel().replaceAll("\\D", "");
					responsavel = criarPedestre(cadastro.getNomeResponsavel(), cadastro.getCpfCnpfResponsavel(),
							cpfResp, "RESPONSAVEL", cliente, empresa);

					boolean isPermitido = PermissoesEduTotvs.isPermitido(cadastro.getCodStatus());
					responsavel.setStatus(isPermitido ? Status.ATIVO : Status.INATIVO);

					try {
						baseEJB.gravaObjeto(responsavel);
					} catch (Exception e) {
						System.out.println("Erro ao salvar pedestre: {}" + cpfResp);
						throw e;
					}
				}
			}
		}

	    // Atualiza status
	    boolean isPermitido = PermissoesEduTotvs.isPermitido(cadastro.getCodStatus());
	    novoCadastro.setStatus(isPermitido ? Status.ATIVO : Status.INATIVO);

	    novoCadastro.setObservacoes("Atualizado em: " + LocalDateTime.now().format(dtf));

	    try {
	        novoCadastro = (PedestreEntity) baseEJB.gravaObjeto(novoCadastro)[0];
	    } catch (Exception e) {
	        System.out.println("Erro ao salvar pedestre: {}" + novoCadastro.getCpf());
	        throw e;
	    }
	}

	private PedestreEntity criarPedestre(String nome, String cpf, String codigoCartao, String tipoDepartamento, ClienteEntity cliente, EmpresaEntity empresa) throws Exception {
	    PedestreEntity pedestre = new PedestreEntity();
	    pedestre.setTipo(TipoPedestre.PEDESTRE);
	    pedestre.setNome(nome);
	    pedestre.setCliente(cliente);
	    pedestre.setCpf(cpf);
	    pedestre.setCodigoCartaoAcesso(codigoCartao);
	    pedestre.setObservacoes("Criado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
	    pedestre.setEmpresa(empresa);
	    pedestre.setDepartamento(recuperaDepartamento(tipoDepartamento, empresa));
	    pedestre.setSempreLiberado(Boolean.TRUE);
	    return pedestre;
	}


	private ClienteEntity buscaClientesPorFilialEColigada(String codColigada, String codFilial) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("CCOLIGADA_TOTVS", codColigada);
		args.put("CFILIAL_TOTVS", codFilial);

		@SuppressWarnings("unchecked")
		List<ClienteEntity> clientes = (List<ClienteEntity>) baseEJB.pesquisaArgFixos(ClienteEntity.class,
				"findComFilialAndColigadaTotvs", args);

		if (clientes != null && !clientes.isEmpty()) {
			return clientes.get(0);
		}

		return null;
	}
	
	private PedestreEntity buscaPedestrePorMatricula(String matricula, Long idCliente) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("MATRICULA", matricula);
		args.put("ID_CLIENTE", idCliente);

		@SuppressWarnings("unchecked")
		List<PedestreEntity> pedestres = (List<PedestreEntity>) baseEJB.pesquisaArgFixos(PedestreEntity.class,
				"findByMatriculaAndIdCliente", args);

		if (pedestres != null && !pedestres.isEmpty()) {
			return pedestres.get(0);
		}

		return null;
	}
	
	private PedestreEntity buscaPedestrePorCpf(String cpf, Long idCliente) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("CPF", cpf);
		args.put("ID_CLIENTE", idCliente);

		@SuppressWarnings("unchecked")
		List<PedestreEntity> pedestres = (List<PedestreEntity>) baseEJB.pesquisaArgFixos(PedestreEntity.class,
				"findByCpfAndIdCliente", args);

		if (pedestres != null && !pedestres.isEmpty()) {
			return pedestres.get(0);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private EmpresaEntity recuperaEmpresa(String nomeEmpresa, ClienteEntity cliente) throws Exception {
		if (nomeEmpresa == null || "".equals(nomeEmpresa)) {
			return null;
		}

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("nome_equals", nomeEmpresa);
			args.put("cliente.id", cliente.getId());

			List<EmpresaEntity> listaEmpresa = (List<EmpresaEntity>) baseEJB
					.pesquisaSimplesLimitado(EmpresaEntity.class, "findAll", args, 0, 1);

			EmpresaEntity empresa = null;
			if (listaEmpresa != null && !listaEmpresa.isEmpty()) {
				empresa = listaEmpresa.get(0);
			}

			if (empresa == null) {
				// cria empresa
				empresa = new EmpresaEntity();
				empresa.setNome(nomeEmpresa);
				empresa.setCliente(cliente);
				empresa.setStatus(Status.ATIVO);

				empresa = (EmpresaEntity) baseEJB.gravaObjeto(empresa)[0];
				empresa = (EmpresaEntity) baseEJB.recuperaObjeto(EmpresaEntity.class, empresa.getId());
			}

			return empresa;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

//	@SuppressWarnings("unchecked")
//	private CargoEntity recuperaCargo(String nomeCargo, EmpresaEntity empresa) throws Exception {
//		if (nomeCargo == null || "".equals(nomeCargo)) {
//			return null;
//		}
//
//		if (empresa == null) {
//			return null;
//		}
//
//		try {
//			Map<String, Object> args = new HashMap<String, Object>();
//			args.put("nome_equals", nomeCargo);
//			args.put("empresa.id", empresa.getId());
//
//			List<CargoEntity> lista = (List<CargoEntity>) baseEJB.pesquisaSimplesLimitado(CargoEntity.class, "findAll",
//					args, 0, 1);
//
//			CargoEntity cargo = null;
//			if (lista != null && !lista.isEmpty()) {
//				cargo = lista.get(0);
//			}
//
//			if (cargo == null) {
//				// cria empresa
//				cargo = new CargoEntity();
//				cargo.setNome(nomeCargo);
//				cargo.setEmpresa(empresa);
//				cargo.setStatus(Status.ATIVO);
//
//				cargo = (CargoEntity) baseEJB.gravaObjeto(cargo)[0];
//				cargo = (CargoEntity) baseEJB.recuperaObjeto(CargoEntity.class, cargo.getId());
//
//			}
//
//			return cargo;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	@SuppressWarnings("unchecked")
	private DepartamentoEntity recuperaDepartamento(String nomeSetor, EmpresaEntity empresa) throws Exception {
		if (nomeSetor == null || "".equals(nomeSetor)) {
			return null;
		}

		if (empresa == null) {
			return null;
		}

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("nome_equals", nomeSetor);
			args.put("empresa.id", empresa.getId());

			List<DepartamentoEntity> lista = (List<DepartamentoEntity>) baseEJB
					.pesquisaSimplesLimitado(DepartamentoEntity.class, "findAll", args, 0, 1);

			DepartamentoEntity departamento = null;
			if (lista != null && !lista.isEmpty()) {
				departamento = lista.get(0);
			}

			if (departamento == null) {
				departamento = new DepartamentoEntity();
				departamento.setNome(nomeSetor);
				departamento.setEmpresa(empresa);
				departamento.setStatus(Status.ATIVO);

				departamento = (DepartamentoEntity) baseEJB.gravaObjeto(departamento)[0];
				departamento = (DepartamentoEntity) baseEJB.recuperaObjeto(DepartamentoEntity.class,
						departamento.getId());
			}

			return departamento;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final Set<String> IGNORAR = new HashSet<>(
			Arrays.asList("colegio", "colégio", "salesiano", "santa", "sao", "dom"));

	public static String gerarUnidade(String nomeFilial) {
		if (nomeFilial == null || nomeFilial.isEmpty())
			return "";

		// Pega a parte depois do primeiro "-"
		String[] partes = nomeFilial.split("-", 2);
		String parteUtil = partes.length > 1 ? partes[1] : partes[0];

		// Quebra em palavras
		String[] palavras = parteUtil.trim().toLowerCase().split("\\s+");

		// Pega a primeira que não está na lista de ignoradas
		for (String palavra : palavras) {
			if (!IGNORAR.contains(palavra)) {
				// Remove acentos e caracteres especiais
				return removerAcentos(palavra).replaceAll("[^a-z0-9]", "");
			}
		}

		return "";
	}

	private static String removerAcentos(String texto) {
		return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
	}

}
