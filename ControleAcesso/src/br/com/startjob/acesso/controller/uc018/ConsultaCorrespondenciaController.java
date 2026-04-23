package br.com.startjob.acesso.controller.uc018;


import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;

@Named("consultaCorrespondenciaController")
@ViewScoped
@UseCase(classEntidade=CorrespondenciaEntity.class, funcionalidade="Consulta de Correspondências",
		urlNovoRegistro="/paginas/sistema/correspondencia/cadastroCorrespondencia.jsf")
public class ConsultaCorrespondenciaController extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String filtroRetirado; // "S", "N" ou null para todos
	private CorrespondenciaEntity correspondenciaSelecionada;
	private String dataPesquisa;

	@PostConstruct
	@Override
	public void init() {
		super.init();
		// Opcional: Iniciar filtrando apenas o que NÃO foi retirado
		this.filtroRetirado = "N"; 
		buscar();
	}

	@Override
	public String buscar() {
		// Montamos os parâmetros para a query baseada no seu padrão de framework
		
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		
//		if (getParans() != null && !getParans().isEmpty()) {
//			// Busca por nome do destinatário ou código de rastreio
//			getParans().put("destinatario.nome", paramBuscaGeral);
//			getParans().put("codigoRastreio", paramBuscaGeral);
//		}
		
		if (filtroRetirado != null && !filtroRetirado.isEmpty()) {
			getParans().put("confirmaRetirada", filtroRetirado);
		}

		return super.buscar();
	}

	/**
	 * Ação para confirmar a retirada diretamente pela lista (atalho)
	 */
	public void confirmarRetiradaRapida(CorrespondenciaEntity c) {
		try {
			c.setConfirmaRetirada("S");
			c.setDataRetirada(new java.util.Date());
			baseEJB.alteraObjeto(c);
			mensagemInfo("", "Retirada confirmada com sucesso!");
			buscar();
		} catch (Exception e) {
			mensagemFatal("", "Erro ao confirmar retirada.");
		}
	}

	public String editar(Long id) {
		return "/paginas/sistema/correspondencia/cadastroCorrespondencia.jsf?id=" + id + "&faces-redirect=true";
	}

	public void excluirCorrespondencia() {
		if (correspondenciaSelecionada != null) {
			try {
				baseEJB.excluiObjeto(correspondenciaSelecionada);
				mensagemInfo("", "Registro excluído.");
				buscar();
			} catch (Exception e) {
				mensagemFatal("", "Não foi possível excluir.");
			}
		}
	}

	// Getters e Setters
	public String getFiltroRetirado() {
		return filtroRetirado;
	}

	public void setFiltroRetirado(String filtroRetirado) {
		this.filtroRetirado = filtroRetirado;
	}

	public CorrespondenciaEntity getCorrespondenciaSelecionada() {
		return correspondenciaSelecionada;
	}

	public void setCorrespondenciaSelecionada(CorrespondenciaEntity correspondenciaSelecionada) {
		this.correspondenciaSelecionada = correspondenciaSelecionada;
	}

	public String getDataPesquisa() {
		return dataPesquisa;
	}

	public void setDataPesquisa(String dataPesquisa) {
		this.dataPesquisa = dataPesquisa;
	}
}