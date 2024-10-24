package br.com.startjob.acesso.controller.uc008;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;

@SuppressWarnings("serial")
@Named("buscarRegrasController")
@ViewScoped
@UseCase(classEntidade=RegraEntity.class, funcionalidade="Buscar regras", lazyLoad = true, quantPorPagina = 5)
public class BuscarRegrasController extends BaseController{

	@PostConstruct
	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public String buscar() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		setNamedQueryPesquisa("findAllComEmpresa");
		return super.buscar();
	}
	
	public String buscarRegrasPedestreOuVisitante(PedestreEntity pedestre) {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		getParans().put("bloco_or", " (obj.tipoPedestre = '" + pedestre.getTipo() + "' or obj.tipoPedestre = 'AMBOS') ");
		
		setNamedQueryPesquisa("findAllRegrasDePedestreComEmpresa");
		return super.buscar();
	}
	
	public String buscarRegraPedestre() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		getParans().put("bloco_or", " (obj.tipoPedestre = '" + TipoPedestre.PEDESTRE + "' or obj.tipoPedestre = 'AMBOS') ");
		
		setNamedQueryPesquisa("findAllRegrasDePedestreComEmpresa");
		return super.buscar();
	}
}
