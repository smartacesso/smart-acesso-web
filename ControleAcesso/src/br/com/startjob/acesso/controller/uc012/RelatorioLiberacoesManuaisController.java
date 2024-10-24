package br.com.startjob.acesso.controller.uc012;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.RelatorioController;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.utils.DateUtils;

@SuppressWarnings("serial")
@Named("relatorioLiberacoesManuaisController")
@ViewScoped
@UseCase(classEntidade=AcessoEntity.class, funcionalidade="Relatório de liberações manuais", 
		lazyLoad = true, quantPorPagina = 10)
public class RelatorioLiberacoesManuaisController extends RelatorioController {

	@PostConstruct
	@Override
	public void init() {
		super.init();
		
		getParans().put("data_maior_data", formatarDataInicioDia(new Date()));
		getParans().put("data_menor_data", formatarDataFimDia(new Date()));
		
		buscar();
	}
	
	@Override
	public String buscar() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		
		setNamedQueryPesquisa("findAllComPedestreNulo");
		return super.buscar();
	}
}
