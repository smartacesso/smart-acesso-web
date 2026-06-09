package br.com.startjob.acesso.controller.uc018;


import java.io.Serializable;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.modelo.enumeration.WebPermissao;
import br.com.startjob.acesso.utils.MailSendUtils;

@Named("consultaCorrespondenciaController")
@ViewScoped
@UseCase(classEntidade=CorrespondenciaEntity.class, funcionalidade="Consulta de Correspondências",
		urlNovoRegistro="/paginas/sistema/correspondencia/cadastroCorrespondencia.jsf",
		lazyLoad = true, quantPorPagina = 6)
public class ConsultaCorrespondenciaController extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String filtroRetirado; // "S", "N" ou null para todos
	private CorrespondenciaEntity correspondenciaSelecionada;
	private Date dataPesquisa;
	private String nomeRetirada;
	private String documentoRetirada;

	@PostConstruct
	@Override
	public void init() {
		super.init();
		
	    if (getParans() == null) {
	        setParans(new HashMap<>());
	    }
		// Opcional: Iniciar filtrando apenas o que NÃO foi retirado
		this.filtroRetirado = "N"; 
		buscar();
	}

	@Override
	public String buscar() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());

		getParans().remove("confirmaRetirada");
		getParans().remove("dataRecebimento_maior_data");
		getParans().remove("dataRecebimento_menor_data");

		if (filtroRetirado != null && !filtroRetirado.isEmpty()) {
			getParans().put("confirmaRetirada", filtroRetirado);
		}

		if (dataPesquisa != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataPesquisa);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			getParans().put("dataRecebimento_maior_data", cal.getTime());

			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			getParans().put("dataRecebimento_menor_data", cal.getTime());
		}

		removerParansVazios();

		return super.buscar();
	}

	@Override
	public String limpar() {
		filtroRetirado = "N";
		dataPesquisa = null;
		limparSelecaoRetirada();
		super.limpar();
		buscar();
		return "";
	}

	private void removerParansVazios() {
		Iterator<Map.Entry<String, Object>> it = getParans().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			Object valor = entry.getValue();
			if (valor == null || (valor instanceof String && ((String) valor).trim().isEmpty())) {
				it.remove();
			}
		}
	}
	
	public void limparSelecaoRetirada() {
		correspondenciaSelecionada = null;
		nomeRetirada = "";
		documentoRetirada = "";
	}

	public String prepararDialogRetirada() {
		nomeRetirada = "";
		documentoRetirada = "";
		return null;
	}

	public void confirmarRetiradaComDados() {
		if (nomeRetirada == null || nomeRetirada.trim().isEmpty()) {
			mensagemFatal("", "Informe o nome de quem está retirando.");
			return;
		}
		if (documentoRetirada == null || documentoRetirada.trim().isEmpty()) {
			mensagemFatal("", "Informe o documento de quem está retirando.");
			return;
		}
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
		salvarUrlRetornoLista();
		return "/paginas/sistema/correspondencia/cadastroCorrespondencia.jsf?id=" + id + "&faces-redirect=true";
	}

	public void excluirCorrespondencia() {
		if (correspondenciaSelecionada == null) {
			return;
		}
		if (!validarPermissaoWeb(br.com.startjob.acesso.modelo.enumeration.WebPermissao.CORRESPONDENCIA_EDITAR)) {
			return;
		}
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

	public Date getDataPesquisa() {
		return dataPesquisa;
	}

	public void setDataPesquisa(Date dataPesquisa) {
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

	public boolean isPodeEditar() {
		return temPermissaoWeb(WebPermissao.CORRESPONDENCIA_EDITAR);
	}
}