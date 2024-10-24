package br.com.startjob.acesso.controller.uc015;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.EquipamentoEntity;

@SuppressWarnings("serial")
@Named("equipamentosConectadosController")
@ViewScoped
@UseCase(classEntidade=EquipamentoEntity.class, funcionalidade="Equipamentos conectados", 
		lazyLoad = true, quantPorPagina = 10)
public class EquipamentosConectadosController extends BaseController {
	
	@PostConstruct
	@Override
	public void init() {
		super.init();
		buscar();
	}
	
	@Override
	public String buscar() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		return super.buscar();
	}
}
