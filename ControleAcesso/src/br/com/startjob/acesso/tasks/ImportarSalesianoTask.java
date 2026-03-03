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
import java.util.Optional;
import java.util.Set;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.totvs.dto.CadastroDTO;
import com.totvs.services.IntegracaoTotvsEducacionalService;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.CargoEntity;
import br.com.startjob.acesso.modelo.entity.CentroCustoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.DepartamentoEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.IntegracaoSalesianoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PlanoEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.Genero;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;
import br.com.startjob.acesso.services.BaseServlet;
import br.com.startjob.acesso.to.TotvsEdu.NivelDeEnsino;
import br.com.startjob.acesso.to.TotvsEdu.OrigemStatusTotvs;
import br.com.startjob.acesso.to.TotvsEdu.PermissoesEduTotvs;
import br.com.startjob.acesso.to.TotvsEdu.TipoTotvsEdu;
import br.com.startjob.acesso.utils.Utils;

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
	    String nome = cadastro.getNome();
	    String matricula = cadastro.getMatricula();
	    String cpf = cadastro.getCpf();
	    String tipo = TipoTotvsEdu.fromTabelaRm(cadastro.getOrigem()).getDesc();
	    System.out.println();
	    System.out.printf("Salvando, nome: %s, matricula: %s, cpf: %s, tipo: %s, status : %s%n", 
	            nome, matricula, cpf, tipo, cadastro.getCodStatus());
	    
	    PedestreEntity pedestre = buscarPedestre(cpf, nome, matricula, cliente, tipo, cadastro);

	    if (pedestre == null) {
	        System.out.println("Nenhum cadastro existente encontrado → criando novo.");
	        pedestre = criarNovoCadastro(cadastro, cliente);
	        pedestre = (PedestreEntity) baseEJB.gravaObjeto(pedestre)[0];
	    } else {
	        System.out.println("Cadastro existente encontrado → atualizando.");
	        atualizarCamposComuns(pedestre, cadastro, cliente);;
	        pedestre = (PedestreEntity) baseEJB.alteraObjeto(pedestre)[0];
	    }
	}
	
	private PedestreEntity criarNovoCadastro(CadastroDTO cadastro, ClienteEntity cliente) throws Exception {

	    String cpf = somenteNumeros(cadastro.getCpf());
	    String tipo = TipoTotvsEdu.fromTabelaRm(cadastro.getOrigem()).getDesc();
	    boolean responsavel = isResponsavel(tipo);

	    String codigoCartao = responsavel
	            ? somenteNumeros(cadastro.getCpf())
	            : somenteNumeros(cadastro.getMatricula());

	    String matricula = gerarMatricula(cadastro, responsavel);

	    PedestreEntity pedestre = criarPedestre(
	            cadastro.getNome().toUpperCase(),
	            cpf,
	            cadastro.getGenero(),
	            codigoCartao,
	            matricula,
	            cliente	    );

	    atualizarCamposComuns(pedestre, cadastro, cliente);

	    return pedestre;
	}

	private PedestreEntity criarPedestre(
	        String nome,
	        String cpf,
	        String genero,
	        String codigoCartao,
	        String matricula,
	        ClienteEntity cliente
	) throws Exception {

	    PedestreEntity pedestre = new PedestreEntity();
	    pedestre.setNome(nome.toUpperCase());
	    pedestre.setCpf(normalizarCpf(cpf));
	    pedestre.setCliente(cliente);
	    pedestre.setGenero(Genero.fromString(genero));
	    pedestre.setCodigoCartaoAcesso(codigoCartao);
	    pedestre.setTipo(TipoPedestre.PEDESTRE);
	    pedestre.setSempreLiberado(Boolean.TRUE);
	    pedestre.setObservacoes("Criado em: " +
	            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

	    return pedestre;
	}

	
	private void atualizarCamposComuns(
	        PedestreEntity pedestre,
	        CadastroDTO cadastro,
	        ClienteEntity cliente
	) throws Exception {

	    boolean permitido = PermissoesEduTotvs.isPermitido(cadastro.getCodStatus());
	    String tipo = TipoTotvsEdu.fromTabelaRm(cadastro.getOrigem()).getDesc();
	    boolean responsavel = isResponsavel(tipo);


	    EmpresaEntity empresa = recuperaEmpresa(cadastro.getNomeColigada(), cliente);
	    DepartamentoEntity departamento = recuperaDepartamento(tipo, empresa);
	    
	    pedestre.setNome(cadastro.getNome().toUpperCase());
	    pedestre.setCpf(normalizarCpf(cadastro.getCpf()));
	    pedestre.setStatus(permitido ? Status.ATIVO : Status.INATIVO);
	    
	    pedestre.setMatriculaReferencia(cadastro.getMatricula());
	    pedestre.setGenero(Genero.fromString(cadastro.getGenero()));
	    
	    pedestre.setEmpresa(empresa);
	    pedestre.setDepartamento(departamento);
	    pedestre.setImportadoEducacional(true);
	    
	    if("ALUNO".equalsIgnoreCase(tipo)) {
		    CargoEntity cargo = recuperaCargo(cadastro.getCodTurma(), empresa);
		    CentroCustoEntity centroDecusto = recuperaCentroCusto(cadastro.getCurso(), empresa);
		    
		    pedestre.setCargo(cargo);
		    pedestre.setCentroCusto(centroDecusto);
	    }
	    
	    pedestre.setMatricula(gerarMatricula(cadastro, responsavel));
	    pedestre.setObservacoes("Atualizado em: " +
	            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
	}

	
	private boolean isResponsavel(String tipo) {
	    return tipo != null && tipo.toUpperCase().contains("RESPONSAVEL");
	}

	private String somenteNumeros(String valor) {
	    if (valor == null) return "0";
	    String numeros = valor.replaceAll("\\D", "");
	    return numeros.isEmpty() ? "0" : String.valueOf(Long.parseLong(numeros));
	}

	private String gerarMatricula(CadastroDTO cadastro, boolean responsavel) {
	    if (!responsavel) {
	        return cadastro.getMatricula();
	    }
	    int codigo = OrigemStatusTotvs.getCodigoPorTipo(cadastro.getOrigem());
	    return cadastro.getMatricula().concat(String.valueOf(codigo));
	}
	
	public PedestreEntity buscarPedestre(String cpf, String nome, String matricula, ClienteEntity cliente, String tipo,
			CadastroDTO cadastro) {
		PedestreEntity pedestre = null;

		// 1. Busca por Matrícula
		System.out.println("Buscando por nome e cpf: ");

//		pedestre = buscaPedestreNomeAndMatriculaReferencia(nome, cpf, matricula, cliente.getId());
		pedestre = buscaPedestrePorNomeAndCpf(nome, cpf, cliente.getId());

		if (pedestre != null) {
			System.out.println("Encontrado por nome e cpf. nome : " + pedestre.getNome());
			return pedestre;
		}

		System.out.println("Nenhum pedestre encontrado com os dados fornecidos.");
		return null;
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
	
	@SuppressWarnings("unchecked")
	private PedestreEntity buscaPedestrePorMatricula(String matricula, Long idCliente) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("MATRICULA", matricula);
		args.put("ID_CLIENTE", idCliente);

		@SuppressWarnings("unchecked")
		List<PedestreEntity> pedestres;
		try {
			pedestres = (List<PedestreEntity>) baseEJB.pesquisaArgFixos(PedestreEntity.class,
					"findByMatriculaAndIdCliente", args);

			if (pedestres != null && !pedestres.isEmpty()) {
				return pedestres.get(0);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private PedestreEntity buscaPedestrePorNomeAndCpf(String nome, String cpf, Long idCliente) {
		
		String cpf_valido = normalizarCpf(cpf);
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("NOME", nome);
		args.put("CPF", cpf_valido);
		args.put("ID_CLIENTE", idCliente);

		@SuppressWarnings("unchecked")
		List<PedestreEntity> pedestres;
		try {
			pedestres = (List<PedestreEntity>) baseEJB.pesquisaArgFixos(PedestreEntity.class, "findByNomeAndCpfAndIdCliente",
					args);

			if (pedestres != null && !pedestres.isEmpty()) {
				return pedestres.get(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public static String normalizarCpf(String cpf) {
	    if (cpf == null) return null;

	    // remove pontos, traços, espaços, etc
	    String apenasNumeros = cpf.replaceAll("\\D", "");

	    if (apenasNumeros.length() > 11) {
	        throw new IllegalArgumentException("CPF inválido: mais de 11 dígitos");
	    }

	    // completa com zeros à esquerda
	    return String.format("%11s", apenasNumeros).replace(' ', '0');
	}


	@SuppressWarnings("unchecked")
	private PedestreEntity buscaPedestrePorNome(String nome, Long idCliente) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("NOME", nome);
		args.put("ID_CLIENTE", idCliente);

		@SuppressWarnings("unchecked")
		List<PedestreEntity> pedestres;
		try {
			pedestres = (List<PedestreEntity>) baseEJB.pesquisaArgFixos(PedestreEntity.class, "findByNomeAndIdCliente",
					args);

			if (pedestres != null && !pedestres.isEmpty()) {
				return pedestres.get(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	private PedestreEntity buscaPedestreNomeAndMatriculaReferencia(String nome, String cpf , String matricula, Long idCliente) {
		Map<String, Object> args = new HashMap<String, Object>();
		
		args.put("NOME", nome);
		args.put("MATRICULA_S", matricula);
		args.put("ID_CLIENTE", idCliente);

		List<PedestreEntity> pedestres;
		try {
			
			pedestres = (List<PedestreEntity>) baseEJB.pesquisaArgFixos(PedestreEntity.class, "findByNomeAndMatriculaAndIdCliente",
					args);

			if (pedestres != null && !pedestres.isEmpty()) {
				return pedestres.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	@SuppressWarnings("unchecked")
	private CargoEntity recuperaCargo(String nomeCargo, EmpresaEntity empresa) throws Exception {
		if (nomeCargo == null || "".equals(nomeCargo)) {
			return null;
		}

		if (empresa == null) {
			return null;
		}

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("nome_equals", nomeCargo);
			args.put("empresa.id", empresa.getId());

			List<CargoEntity> lista = (List<CargoEntity>) baseEJB.pesquisaSimplesLimitado(CargoEntity.class, "findAll",
					args, 0, 1);

			CargoEntity cargo = null;
			if (lista != null && !lista.isEmpty()) {
				cargo = lista.get(0);
			}

			if (cargo == null) {
				// cria empresa
				cargo = new CargoEntity();
				cargo.setNome(nomeCargo);
				cargo.setEmpresa(empresa);
				cargo.setStatus(Status.ATIVO);

				cargo = (CargoEntity) baseEJB.gravaObjeto(cargo)[0];
				cargo = (CargoEntity) baseEJB.recuperaObjeto(CargoEntity.class, cargo.getId());

			}

			return cargo;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private CentroCustoEntity recuperaCentroCusto(String centroCustoRecebido, EmpresaEntity empresa) throws Exception {
		if (centroCustoRecebido == null || "".equals(centroCustoRecebido)) {
			return null;
		}

		if (empresa == null) {
			return null;
		}

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("nome_equals", centroCustoRecebido);
			args.put("empresa.id", empresa.getId());

			List<CentroCustoEntity> lista = (List<CentroCustoEntity>) baseEJB.pesquisaSimplesLimitado(CentroCustoEntity.class, "findAll",
					args, 0, 1);

			CentroCustoEntity centroCusto = null;
			if (lista != null && !lista.isEmpty()) {
				centroCusto = lista.get(0);
			}

			if (centroCusto == null) {
				// cria empresa
				centroCusto = new CentroCustoEntity();
				centroCusto.setNome(centroCustoRecebido);
				centroCusto.setEmpresa(empresa);
				centroCusto.setStatus(Status.ATIVO);

				centroCusto = (CentroCustoEntity) baseEJB.gravaObjeto(centroCusto)[0];
				centroCusto = (CentroCustoEntity) baseEJB.recuperaObjeto(CargoEntity.class, centroCusto.getId());

			}

			return centroCusto;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

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
