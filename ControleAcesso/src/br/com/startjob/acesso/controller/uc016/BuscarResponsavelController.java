package br.com.startjob.acesso.controller.uc016;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.entity.ResponsibleEntity;


@Named("buscarResponsavelController")
@ViewScoped
@UseCase(classEntidade=ResponsibleEntity.class, funcionalidade="Buscar responsaveis", lazyLoad = true, quantPorPagina = 5)
public class BuscarResponsavelController extends BaseController{

	private static final long serialVersionUID = 1L;
	
	public String findResponsiblebyName() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		
		setNamedQueryPesquisa("findAllResponsaveis");
		return super.buscar();
	}
	
	public void bindDependencies(PedestreEntity pedestrian, ResponsibleEntity responsible) {
		
		
	}
	

}
