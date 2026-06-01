package br.com.startjob.acesso.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.StatusCadastroExterno;
import br.com.startjob.acesso.modelo.enumeration.TipoCadastroExterno;
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;

/**
 * Geração e persistência de tokens/links de cadastro facial (convite e pré-cadastro).
 */
public class CadastroFacialLinkService {

	private final BaseEJBRemote baseEJB;

	public CadastroFacialLinkService(BaseEJBRemote baseEJB) {
		this.baseEJB = baseEJB;
	}

	public long calcularTokenValidade(Long idCliente) throws Exception {
		ParametroEntity param = baseEJB.getParametroSistema(
				BaseConstant.PARAMETERS_NAME.DIAS_VALIDADE_LINK_CADASTRO_FACIAL_EXTERNO, idCliente);
		int diasValidade = param != null ? Integer.parseInt(param.getValor()) : 1;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, diasValidade);
		return calendar.getTimeInMillis();
	}

	public String montarUrlConvite(Long idCliente, Long idEmpresa, long token) {
		String baseUrl = AppAmbienteUtils.isProdution()
				? AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_MAIN_SITE)
						+ AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP) + "/"
				: "http://localhost:8081/";
		return baseUrl + "cadastroFacialPorLink.xhtml" + "?cliente=" + idCliente + "&idEmpresa=" + idEmpresa + "&token="
				+ token;
	}

	public String montarUrlPrecadastro(Long idCliente, Long idPedestre, long token) {
		String baseUrl = AppAmbienteUtils.isProdution()
				? AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_MAIN_SITE)
						+ AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP) + "/"
				: "http://localhost:8081/";
		return baseUrl + "cadastroFacialPorLink.xhtml" + "?cliente=" + idCliente + "&idPedestre=" + idPedestre
				+ "&token=" + token;
	}

	/**
	 * Convite para visitante sem cadastro prévio. Empresa livre (qualquer do cliente).
	 * @param gerador pedestre gerencial (app) ou null (operador web)
	 */
	@SuppressWarnings("unchecked")
	public void gravarCadastroExternoConvite(ClienteEntity cliente, EmpresaEntity empresa, long tokenLink,
			PedestreEntity gerador) throws Exception {

		Map<String, Object> args = new HashMap<>();
		args.put("ID_CLIENTE", cliente.getId());
		args.put("ID_EMPRESA", empresa.getId());
		args.put("STATUS", StatusCadastroExterno.AGUARDANDO_CADASTRO);
		args.put("TIPO", TipoCadastroExterno.FACIAL_LINK_CONVITE);
		args.put("TOKEN", System.currentTimeMillis());

		List<CadastroExternoEntity> ativos = (List<CadastroExternoEntity>) baseEJB.pesquisaArgFixos(
				CadastroExternoEntity.class, "findConviteTokenAtivoByEmpresa", args);

		if (ativos != null && !ativos.isEmpty()) {
			CadastroExternoEntity existente = ativos.get(0);
			existente.setToken(tokenLink);
			if (gerador != null) {
				existente.setPedestreGerador(gerador);
			}
			baseEJB.alteraObjeto(existente);
			return;
		}

		CadastroExternoEntity cadastroExterno = new CadastroExternoEntity();
		cadastroExterno.setCliente(cliente);
		cadastroExterno.setEmpresa(empresa);
		cadastroExterno.setPedestre(null);
		cadastroExterno.setPedestreGerador(gerador);
		cadastroExterno.setToken(tokenLink);
		cadastroExterno.setTipo(TipoCadastroExterno.FACIAL_LINK_CONVITE);
		cadastroExterno.setStatusCadastroExterno(StatusCadastroExterno.AGUARDANDO_CADASTRO);
		baseEJB.gravaObjeto(cadastroExterno);
	}

	@SuppressWarnings("unchecked")
	public void gravarCadastroExternoPrecadastro(PedestreEntity pedestre, ClienteEntity cliente, long tokenLink)
			throws Exception {

		Map<String, Object> args = new HashMap<>();
		args.put("ID_PEDESTRE", pedestre.getId());
		args.put("STATUS", StatusCadastroExterno.AGUARDANDO_CADASTRO);
		args.put("TOKEN", System.currentTimeMillis());
		args.put("TIPO", TipoCadastroExterno.FACIAL_LINK_PRECADASTRO);

		List<CadastroExternoEntity> ativos = (List<CadastroExternoEntity>) baseEJB.pesquisaArgFixos(
				CadastroExternoEntity.class, "findAllTokensActiveByTipo", args);

		if (ativos != null && !ativos.isEmpty()) {
			CadastroExternoEntity existente = ativos.get(0);
			existente.setToken(tokenLink);
			existente.setTipo(TipoCadastroExterno.FACIAL_LINK_PRECADASTRO);
			baseEJB.alteraObjeto(existente);
			return;
		}

		CadastroExternoEntity cadastroExterno = new CadastroExternoEntity();
		cadastroExterno.setCliente(cliente);
		cadastroExterno.setPedestre(pedestre);
		cadastroExterno.setToken(tokenLink);
		cadastroExterno.setTipo(TipoCadastroExterno.FACIAL_LINK_PRECADASTRO);
		cadastroExterno.setStatusCadastroExterno(StatusCadastroExterno.AGUARDANDO_CADASTRO);
		baseEJB.gravaObjeto(cadastroExterno);
	}

	@SuppressWarnings("unchecked")
	public EmpresaEntity buscaEmpresaPorId(Long idEmpresa, Long idCliente) throws Exception {
		Map<String, Object> args = new HashMap<>();
		args.put("ID", idEmpresa);
		List<EmpresaEntity> lista = (List<EmpresaEntity>) baseEJB.pesquisaArgFixos(EmpresaEntity.class, "findById",
				args);
		if (lista == null) {
			return null;
		}
		return lista.stream()
				.filter(e -> e.getCliente() != null && idCliente.equals(e.getCliente().getId()))
				.findFirst()
				.orElse(null);
	}

}
