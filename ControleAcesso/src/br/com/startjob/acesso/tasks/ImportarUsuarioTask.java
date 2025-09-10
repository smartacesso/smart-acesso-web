package br.com.startjob.acesso.tasks;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.logging.Logger;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.PlanoEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;
import br.com.startjob.acesso.services.BaseServlet;

public class ImportarUsuarioTask extends TimerTask {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private final BaseEJBRemote baseEJB;

	
	public ImportarUsuarioTask() throws Exception {
		baseEJB = ((BaseEJBRemote) BaseServlet.getEjb(BaseEJBRemote.class));
	}

	@Override
	public void run() {
		logger.info("Iniciando processo da criacao de primeiro usuario...");
		try {
			long inicio = System.currentTimeMillis();
			recuperaOuCriaCliente();
			logger.info("Processo da criacao de primeiro usuario... " + (System.currentTimeMillis() - inicio)+"ms");

		} catch (Exception e) {
			logger.severe("Erro ao executar a criacao de primeiro usuario");
			e.printStackTrace();
		}
	}
	
	
	private void recuperaOuCriaCliente() throws Exception {
		ClienteEntity clienteRecuperado = buscaClientesPorUnidadeOrganizacional("startjob");

		if (Objects.nonNull(clienteRecuperado)) {
			return;
		}

		criaClienteAndPlano(baseEJB);
	}

	private ClienteEntity criaClienteAndPlano(BaseEJBRemote baseEJB)
			throws Exception, NoSuchAlgorithmException, UnsupportedEncodingException {
		// cria cliente
		ClienteEntity newCliente = new ClienteEntity();
		newCliente.setCnpj("75.136.431/0001-89");
		newCliente.setNome("startjob");

		String unidadeOrganizacional = "startjob";
		newCliente.setNomeUnidadeOrganizacional(unidadeOrganizacional);

		newCliente.setEmail("contato@smartempresarial.com.br");
		newCliente.setCelular("31999999999");
		newCliente.setTelefone("3199999999");
		newCliente.setStatus(Status.ATIVO);

		// salva cliente
		newCliente = (ClienteEntity) baseEJB.gravaObjeto(newCliente)[0];

		// cria plano
		PlanoEntity newPlano = new PlanoEntity();
		Date dataPlano = new Date();
		newPlano.setCliente(newCliente);
		newPlano.setNome("Smartponto");
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
	
	private ClienteEntity buscaClientesPorUnidadeOrganizacional(String unidadeOrganizacional) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("UNIDADE_ORGANIZACIONAL", unidadeOrganizacional);

		@SuppressWarnings("unchecked")
		List<ClienteEntity> clientes = (List<ClienteEntity>) baseEJB.pesquisaArgFixos(ClienteEntity.class,
				"findByUnidadeOrganizacional", args);

		if (clientes != null && !clientes.isEmpty()) {
			return clientes.get(0);
		}

		return null;
	}

}
