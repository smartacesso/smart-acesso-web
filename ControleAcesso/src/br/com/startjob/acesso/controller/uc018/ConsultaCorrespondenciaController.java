package br.com.startjob.acesso.controller.uc018;


import java.io.Serializable;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.utils.MailSendUtils;

@Named("consultaCorrespondenciaController")
@ViewScoped
@UseCase(classEntidade=CorrespondenciaEntity.class, funcionalidade="Consulta de Correspondências",
		urlNovoRegistro="/paginas/sistema/correspondencia/cadastroCorrespondencia.jsf")
public class ConsultaCorrespondenciaController extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String filtroRetirado; // "S", "N" ou null para todos
	private CorrespondenciaEntity correspondenciaSelecionada;
	private String dataPesquisa;
	private String nomeRetirada;
	private String documentoRetirada;

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

		getParans().remove("confirmaRetirada");

		if (filtroRetirado != null && !filtroRetirado.isEmpty()) {
			getParans().put("confirmaRetirada", filtroRetirado);
		}

		return super.buscar();
	}
	
	public void confirmarRetiradaComDados() {
		if (correspondenciaSelecionada != null) {
			try {
				correspondenciaSelecionada.setConfirmaRetirada("S");
				correspondenciaSelecionada.setDataRetirada(new java.util.Date());
				
				// Seta os novos campos vindos do dialog
				correspondenciaSelecionada.setNomeQuemRetirou(nomeRetirada);
				correspondenciaSelecionada.setDocumentoQuemRetirou(documentoRetirada);
				
				// Salva no banco via EJB
				baseEJB.alteraObjeto(correspondenciaSelecionada);
				
				// ==========================================
				// DISPARA O E-MAIL DE CONFIRMAÇÃO DE RETIRADA
				// ==========================================
				try {
					MailSendUtils.enviaConfirmacaoRetirada(correspondenciaSelecionada, mailSession);
				} catch (Exception e) {
					System.err.println("Erro ao tentar enviar e-mail de confirmação de retirada: " + e.getMessage());
					e.printStackTrace();
					// Falhas de e-mail não travam a tela do porteiro
				}
				// ==========================================
				
				mensagemInfo("", "retirada.confirmada.sucesso");
				
				// Limpa os campos para o próximo uso
				nomeRetirada = "";
				documentoRetirada = "";
				
				// Recarrega a tabela de pesquisa
				buscar(); 
				
			} catch (Exception e) {
				mensagemFatal("", "retirada.falhou.erro");
				e.printStackTrace();
			}
		}
	}

	public String editar(Long id) {
		return "/paginas/sistema/correspondencia/cadastroCorrespondencia.jsf?id=" + id + "&faces-redirect=true";
	}

	public void excluirCorrespondencia() {
		if (correspondenciaSelecionada != null) {
			try {
				correspondenciaSelecionada.setRemovido(true);
				correspondenciaSelecionada.setDataRemovido(new Date());
				baseEJB.alteraObjeto(correspondenciaSelecionada);
				mensagemInfo("", "msg.generica.objeto.excluido.sucesso");
				buscar();
			} catch (Exception e) {
				mensagemFatal("", "msg.nao.excluido");
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
	
	public String getFotoBase64(byte[] foto) {
	    if (foto != null && foto.length > 0) {
	        return Base64.getEncoder().encodeToString(foto);
	    }
	    return "";
	}

	public String getNomeRetirada() {
	    return nomeRetirada;
	}

	public void setNomeRetirada(String nomeRetirada) {
	    this.nomeRetirada = nomeRetirada;
	}

	public String getDocumentoRetirada() {
	    return documentoRetirada;
	}

	public void setDocumentoRetirada(String documentoRetirada) {
	    this.documentoRetirada = documentoRetirada;
	}
}