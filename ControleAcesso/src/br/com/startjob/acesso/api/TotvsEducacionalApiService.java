package br.com.startjob.acesso.api;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import br.com.startjob.acesso.modelo.ejb.BaseEJB;
import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.ejb.PedestreEJB;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.CargoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.DepartamentoEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PlanoEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;
import br.com.startjob.acesso.services.BaseServlet;
import br.com.startjob.acesso.to.TotvsEdu.AlunoTotvsTO;
import br.com.startjob.acesso.to.TotvsEdu.MensagemResponseTO;
import br.com.startjob.acesso.to.TotvsEdu.PermissoesEduTotvs;
import br.com.startjob.acesso.to.TotvsEdu.TipoTotvsEdu;

@Path("/educacional")
public class TotvsEducacionalApiService extends BaseService {
	
	private final String TOKEN_ACESSO = "KsOywRMRmGrqy903";
	
	@EJB
	private PedestreEJBRemote pedestreEJB;
	
	@POST
	@Path("/alunos")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response receberDadosAluno(
		List<AlunoTotvsTO> alunos,
	    @HeaderParam("Authorization") String authHeader) {

	    String token = extrairToken(authHeader);

	    if (!TOKEN_ACESSO.equals(token)) {
	        return Response.status(Response.Status.UNAUTHORIZED)
	                .entity(new MensagemResponseTO(401, "Credenciais inválidas"))
	                .build();
	    }
	    
	    try {
	    	System.out.println(alunos);
//	        processaAlunosTotvs(alunos);
	        return Response.status(Response.Status.CREATED)
	                .entity(new MensagemResponseTO(201,"Alunos processados com sucesso."))
	                .build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                .entity(new MensagemResponseTO(500, "Erro ao processar os alunos."))
	                .build();
	    }
	}

	private void processaAlunosTotvs(List<AlunoTotvsTO> alunos) {
		for(AlunoTotvsTO aluno : alunos) {
			try {
				ClienteEntity cliente = recuperaOuCriaCliente(aluno);
				salvarFuncionario(aluno, cliente);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void salvarFuncionario(AlunoTotvsTO aluno, ClienteEntity cliente) throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		pedestreEJB = ((PedestreEJBRemote) BaseServlet.getEjb(PedestreEJBRemote.class));
		
		PedestreEntity pedestre = buscaPedestrePorMatricula(aluno.getMatricula(), cliente.getId());

		if (Objects.isNull(pedestre)) {
			String tipo = TipoTotvsEdu.fromTabelaRm(aluno.getTabelarm()).getDesc();
			
			pedestre = new PedestreEntity();
			pedestre.setTipo(TipoPedestre.PEDESTRE);
			pedestre.setNome(aluno.getNome());
			pedestre.setCliente(cliente);
			pedestre.setCpf(aluno.getCpf());
			pedestre.setMatricula(aluno.getMatricula());
			pedestre.setCodigoCartaoAcesso(aluno.getMatricula()); // vem apenas numeros?

			pedestre.setObservacoes("criado em: " + LocalDateTime.now().format(dtf));

			pedestre.setEmpresa(recuperaEmpresa(aluno.getNomeColigada(), cliente));
			pedestre.setDepartamento(recuperaDepartamento(tipo, pedestre.getEmpresa()));
//			pedestre.setCargo(recuperaCargo(funcionario.NOMECARGO, pedestre.getEmpresa()));
			// pedestre.setCentroCusto(null)

			pedestre.setSempreLiberado(Boolean.TRUE);

		}

		// verifica situação
		boolean isPermitido = PermissoesEduTotvs.isPermitido(Integer.valueOf(aluno.getCodStatus()));
		if (isPermitido) {
			pedestre.setStatus(Status.ATIVO);
		}else {
			pedestre.setStatus(Status.INATIVO);
		}
		
		try {
			pedestre.setObservacoes("criado em: " + LocalDateTime.now().format(dtf));
			pedestre = (PedestreEntity) pedestreEJB.gravaObjeto(pedestre)[0];

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private ClienteEntity recuperaOuCriaCliente(AlunoTotvsTO aluno) throws Exception {
		BaseEJBRemote baseEJB = getEjb("BaseEJB");
		
		ClienteEntity clienteRecuperado = buscaClientesPorFilialEColigada(aluno.getCodColigada(), aluno.getCodFilial());
		
		if(Objects.nonNull(clienteRecuperado)) {
			return clienteRecuperado;
		}
		
		return criaClienteAndPlano(aluno, baseEJB);
	}

	private ClienteEntity criaClienteAndPlano(AlunoTotvsTO aluno, BaseEJBRemote baseEJB)
			throws Exception, NoSuchAlgorithmException, UnsupportedEncodingException {
		//cria cliente
		ClienteEntity newCliente = new ClienteEntity();
		newCliente.setCnpj(aluno.getCnpjFilial());
		newCliente.setCodcoligad(aluno.getCodColigada());
		newCliente.setCodfilial(aluno.getCodFilial());
		newCliente.setNome(aluno.getNomeFilial());
		newCliente.setNomeUnidadeOrganizacional(aluno.getNomeFilial().trim().replaceAll("\\s+", ""));
		
		newCliente.setEmail("salasiano@adm.com");
		newCliente.setCelular("31999999999");
		newCliente.setTelefone("3199999999");
		newCliente.setStatus(Status.ATIVO);
		
		//salva cliente
		newCliente = (ClienteEntity) baseEJB.gravaObjeto(newCliente)[0];
		
		//cria plano
		PlanoEntity newPlano = new PlanoEntity();
		Date dataPlano = new Date();
		newPlano.setCliente(newCliente);
		newPlano.setNome(aluno.getCodFilial());
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
		
		//salva plano
		baseEJB.gravaObjeto(newPlano);
		
		//cria usuario
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
//		newUsuario.setEndereco(cliente.getEndereco());
		newUsuario.setPerfil(PerfilAcesso.ADMINISTRADOR);
		
	
		//salva usuario
		baseEJB.gravaObjeto(newUsuario);
		
		return newCliente;
	}
	
	private ClienteEntity buscaClientesPorFilialEColigada(String codColigada, String codFilial) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("CCOLIGADA_TOTVS", codColigada);
		args.put("CFILIAL_TOTVS", codFilial);
		
		@SuppressWarnings("unchecked")
		List<ClienteEntity> clientes = (List<ClienteEntity>) ((BaseEJBRemote) getEjb("BaseEJB"))
				.pesquisaArgFixos(ClienteEntity.class,
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
		List<PedestreEntity> pedestres = (List<PedestreEntity>) ((PedestreEJBRemote) getEjb("PedestreEJB"))
				.pesquisaArgFixos(PedestreEntity.class,
				"findByMatriculaAndIdCliente", args);
		
		if (pedestres != null && !pedestres.isEmpty()) {
			return pedestres.get(0);
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private EmpresaEntity recuperaEmpresa(String nomeEmpresa, ClienteEntity cliente) throws Exception {
		BaseEJBRemote baseEJB = getEjb("BaseEJB");
		
		if (nomeEmpresa == null || "".equals(nomeEmpresa)) {
			return null;
		}

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("nome_equals", nomeEmpresa);
			args.put("cliente.id", cliente.getId());

			List<EmpresaEntity> listaEmpresa = (List<EmpresaEntity>) baseEJB.pesquisaSimplesLimitado(EmpresaEntity.class,
					"findAll", args, 0, 1);

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
		BaseEJBRemote baseEJB = getEjb("BaseEJB");
		
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

			List<CargoEntity> lista = (List<CargoEntity>) baseEJB.pesquisaSimplesLimitado(CargoEntity.class, "findAll", args, 0,
					1);

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
	private DepartamentoEntity recuperaDepartamento(String nomeSetor, EmpresaEntity empresa) throws Exception {
		BaseEJBRemote baseEJB = getEjb("BaseEJB");
		
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

			List<DepartamentoEntity> lista = (List<DepartamentoEntity>) baseEJB.pesquisaSimplesLimitado(
					DepartamentoEntity.class, "findAll", args, 0, 1);

			DepartamentoEntity departamento = null;
			if (lista != null && !lista.isEmpty()) {
				departamento = lista.get(0);
			}

			if (departamento == null) {
				// cria empresa
				departamento = new DepartamentoEntity();
				departamento.setNome(nomeSetor);
				departamento.setEmpresa(empresa);
				departamento.setStatus(Status.ATIVO);

				departamento = (DepartamentoEntity) baseEJB.gravaObjeto(departamento)[0];
				departamento = (DepartamentoEntity) baseEJB.recuperaObjeto(DepartamentoEntity.class, departamento.getId());

			}

			return departamento;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String extrairToken(String authHeader) {
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        return authHeader.substring(7).trim();
	    }
	    return null;
	}

}
