package br.com.startjob.acesso.controller.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.AvisoAppEntity;
import br.com.startjob.acesso.modelo.enumeration.WebPermissao;
import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

@Named("consultaAvisoAppController")
@ViewScoped
@UseCase(classEntidade = AvisoAppEntity.class, funcionalidade = "Avisos do App",
		namedQueryPesquisa = "findAllByIdCliente",
		urlNovoRegistro = "/paginas/sistema/avisoApp/cadastroAvisoApp.jsf", quantPorPagina = 10)
public class ConsultaAvisoAppController extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;

	private AvisoAppEntity avisoSelecionado;

	@PostConstruct
	@Override
	public void init() {
		super.init();
		if (getParans() == null) {
			setParans(new HashMap<>());
		}
		buscar();
	}

	/**
	 * Named query já contém WHERE/ORDER BY; não usar {@code super.buscar()} com
	 * {@code ID_CLIENTE} em parans (o BaseEJB duplicaria o WHERE após o ORDER BY).
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String buscar() {
		avisoSelecionado = null;
		parametrosSessao();
		try {
			Map<String, Object> args = new HashMap<>();
			args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());

			List<AvisoAppEntity> lista = (List<AvisoAppEntity>) baseEJB.pesquisaArgFixos(AvisoAppEntity.class,
					"findAllByIdCliente", args);

			if (lista == null) {
				lista = new ArrayList<>();
			}

			String tituloFiltro = getParans() != null ? (String) getParans().get("titulo") : null;
			if (tituloFiltro != null && !tituloFiltro.trim().isEmpty()) {
				String busca = tituloFiltro.trim().toLowerCase();
				lista = lista.stream().filter(a -> matchesBusca(a, busca)).collect(Collectors.toList());
			}

			preencherIndicadorImagem(lista, args);

			List<BaseEntity> resultado = new ArrayList<>();
			resultado.addAll(lista);
			setResult(resultado);

		} catch (Exception e) {
			e.printStackTrace();
			mensagemFatal("", "#" + e.getMessage());
		}
		return "";
	}

	@Override
	public String limpar() {
		avisoSelecionado = null;
		super.limpar();
		buscar();
		return "";
	}

	public void prepararExclusaoAviso(ActionEvent event) {
		if (avisoSelecionado == null) {
			mensagemFatal("", "msg.nao.excluido");
			return;
		}
		PrimeFaces.current().executeScript("PF('confirmarExclusaoAviso').show();");
	}

	@SuppressWarnings("unchecked")
	private void preencherIndicadorImagem(List<AvisoAppEntity> lista, Map<String, Object> args) {
		if (lista == null || lista.isEmpty()) {
			return;
		}
		try {
			List<Long> idsComImagem = (List<Long>) baseEJB.pesquisaArgFixos(AvisoAppEntity.class,
					"findIdsComImagemByCliente", args);
			Set<Long> comImagem = idsComImagem != null ? new HashSet<>(idsComImagem) : new HashSet<>();
			for (AvisoAppEntity aviso : lista) {
				aviso.setPossuiImagem(aviso.getId() != null && comImagem.contains(aviso.getId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			for (AvisoAppEntity aviso : lista) {
				aviso.setPossuiImagem(false);
			}
		}
	}

	private boolean matchesBusca(AvisoAppEntity aviso, String busca) {
		if (aviso.getTitulo() != null && aviso.getTitulo().toLowerCase().contains(busca)) {
			return true;
		}
		return aviso.getDescricao() != null && aviso.getDescricao().toLowerCase().contains(busca);
	}

	public String editar(Long id) {
		salvarUrlRetornoLista();
		return "/paginas/sistema/avisoApp/cadastroAvisoApp.jsf?id=" + id + "&faces-redirect=true";
	}

	public void excluirAviso() {
		if (avisoSelecionado == null) {
			mensagemFatal("", "msg.nao.excluido");
			return;
		}
		if (!validarPermissaoWeb(br.com.startjob.acesso.modelo.enumeration.WebPermissao.AVISO_APP_EDITAR)) {
			return;
		}
		try {
			avisoSelecionado.setRemovido(true);
			avisoSelecionado.setDataRemovido(new Date());
			baseEJB.alteraObjeto(avisoSelecionado);
			mensagemInfo("", "msg.generica.objeto.excluido.sucesso");
			buscar();
		} catch (Exception e) {
			mensagemFatal("", "msg.nao.excluido");
		}
	}

	public AvisoAppEntity getAvisoSelecionado() {
		return avisoSelecionado;
	}

	public void setAvisoSelecionado(AvisoAppEntity avisoSelecionado) {
		this.avisoSelecionado = avisoSelecionado;
	}

	public boolean isPodeEditar() {
		return temPermissaoWeb(WebPermissao.AVISO_APP_EDITAR);
	}
}
