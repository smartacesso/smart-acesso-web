package br.com.startjob.acesso.tasks;

import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Singleton;

import br.com.startjob.acesso.modelo.entity.ConfiguracaoRhidEntity;
import br.com.startjob.acesso.services.rhid.RhidService;
import br.com.startjob.acesso.to.rhid.RhidPersonDTO;

@Singleton
@Startup
public class RhidSyncSchedule {

    @Inject
    private RhidService rhidService; // Certifique-se de gerenciar o escopo de forma que persista

    @Schedule(minute = "*/2", hour = "*", persistent = false)
	public void executarSincronizacaoDiaria() {
		// 1. Carrega as credenciais salvas no banco do SmartAcesso
//        ConfiguracaoRhidEntity config = buscarConfiguracaoDoBanco(); 
    	System.out.println("chamou sched");
    	
		ConfiguracaoRhidEntity config = null;
		// 2. Prepara o service
		rhidService.inicializarCredenciais(config.getEmail(), config.getSenha(), config.getDominio());

		// 3. Busca os funcionários que precisam ir para o equipamento Hikvision / RHID
		List<RhidPersonDTO> listaParaCadastrar = obterFuncionariosPendentes(); // obter esses funcionarios do TOTVS

		for (RhidPersonDTO funcionario : listaParaCadastrar) {
			try {
				// O service faz o login automático na primeira volta do laço!
				// Nas próximas voltas, ele usa o token em cache.
				String resultado = rhidService.salvarFuncionario(funcionario);
//                atualizarStatusSucessoNoBanco(funcionario, resultado);
			} catch (Exception e) {
				System.out.println(e.getMessage());
//                gravarLogErro(funcionario, e);
			}
		}
	}

	private List<RhidPersonDTO> obterFuncionariosPendentes() {
		// TODO Auto-generated method stub
		//buscar no rm
		//converter para dto
		return null;
	}
}