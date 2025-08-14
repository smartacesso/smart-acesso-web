package br.com.startjob.acesso.controller.uc007;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.controller.MenuController;
import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.base.BaseEntity;
import br.com.startjob.acesso.modelo.utils.ConectionUtils;
import br.com.startjob.acesso.utils.ResourceBundleUtils;

@ViewScoped
@Named("gerenciarParametrosController")
public class GerenciarParametrosController extends BaseController {

	private static final long serialVersionUID = 1L;

	private List<ParametroEntity> parametrosGerais;

	private List<SelectItem> listaCamposObrigatorios;
	
	private List<String> camposObrigatorios;

	private String campoObrigatorio;

	private Boolean valorHabilitaAcessoReconhecimentoFacial;

	private String valorOriginalSOC = "-1";

	@Inject
	private MenuController menuController;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("cliente.id", getUsuarioLogado().getCliente().getId());

			List<BaseEntity> all = (List<BaseEntity>) baseEJB.pesquisaSimples(ParametroEntity.class, "findAll", args);

			valorHabilitaAcessoReconhecimentoFacial = getValorHabilitaAcessoReconhecimentoFacial(all);

			atualizaListas(all);

			camposObrigatorios = new ArrayList<String>();
			montaListCamposObrigatorios();

			for (ParametroEntity baseEntity : parametrosGerais) {
				if (baseEntity.getNome().startsWith("Campos")) {
					String[] value = baseEntity.getValor().split(",");

					for (int i = 0; i < value.length; i++) {
						if (!"".equals(value[i]))
							camposObrigatorios.add(value[i]);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void atualizaListas(List<? extends BaseEntity> all) {

		parametrosGerais = new ArrayList<ParametroEntity>();

		if (all != null) {
			boolean preencherCartaoComMatricula = false;
			boolean preencherCartaoAuto = false;
			boolean gerarMatriculaSequencial = false;
			boolean escolherDigitos = false;
			boolean tempoToleranciaEntrada = false;
			boolean permitirCampoAdicionalMatricula = false;
			boolean formatoDataImportacao = false;
			boolean formatoHoraImportacao = false;
			boolean camposObrigatoriosPedestre = false;
			boolean validarMatriculasDuplicadas = false;
			boolean validarCPFDuplicado = false;
			boolean validarRGDuplicado = false;
			boolean validarCartaoAcessoDuplicado = false;
			boolean cadastroEmLote = false;
			boolean permitarAcessoViaQRCode = false;
			boolean chaveIntegracaoContelli = false;
			boolean diasValidadeLinkCadastroFacialExterno = false;
			boolean exibeCampoSempreLiberadoParaTodos = false;
			boolean habilitaAcessoPorReconhecimentoFacial = false;
			boolean limiteDigitaisPorPedestre = false;
			boolean tipoQRCode = false;
			boolean removerCartaoPedestreRemovidos = false;
			boolean tipoQRCodePadrao = false;
			boolean tempoQRCodeDinamico = false;
			boolean horarioDisparoSOC = false;
			boolean habilitaAppPedestre = false;

			for (BaseEntity baseEntity : all) {
				ParametroEntity item = (ParametroEntity) baseEntity;

				parametrosGerais.add(item);
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.PREENCHER_CARTAO_COM_MATRICULA)) {
					preencherCartaoComMatricula = true;
				}
				
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.PREENCHER_CARTAO_AUTO)) {
					preencherCartaoAuto = true;
				}

				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.HORARIO_DISPARO_SOC)) {
					horarioDisparoSOC = true;
					valorOriginalSOC = item.getValor().toString() + "";
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.GERAR_MATRICULA_SEQUENCIAL)) {
					gerarMatriculaSequencial = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.ESCOLHER_QTDE_DIGITOS_CARTAO)) {
					escolherDigitos = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.GERAR_MATRICULA_SEQUENCIAL)) {
					permitirCampoAdicionalMatricula = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.FORMATO_DATA_PARA_IMPORTACAO)) {
					formatoDataImportacao = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.FORMATO_HORA_PARA_IMPORTACAO)) {
					formatoHoraImportacao = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE)) {
					camposObrigatoriosPedestre = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.VALIDAR_MATRICULAS_DUPLICADAS)) {
					validarMatriculasDuplicadas = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.VALIDAR_CPF_DUPLICADO)) {
					validarCPFDuplicado = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.VALIDAR_RG_DUPLICADO)) {
					validarRGDuplicado = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.VALIDAR_CARTAO_ACESSO_DUPLICADO)) {
					validarCartaoAcessoDuplicado = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.CADASTRO_EM_LOTE)) {
					cadastroEmLote = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.PERMITIR_ACESSO_QR_CODE)) {
					permitarAcessoViaQRCode = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.CHAVE_DE_INTEGRACAO_COMTELE)) {
					chaveIntegracaoContelli = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.DIAS_VALIDADE_LINK_CADASTRO_FACIAL_EXTERNO)) {
					diasValidadeLinkCadastroFacialExterno = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.EXIBE_CAMPO_SEMPRE_LIBERADO_PARA_TODOS)) {
					exibeCampoSempreLiberadoParaTodos = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.HABILITA_ACESSO_POR_RECONHECIMENTO_FACIAL)) {
					habilitaAcessoPorReconhecimentoFacial = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.LIMITE_DIGITAIS_PEDESTRE)) {
					limiteDigitaisPorPedestre = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.TIPO_QR_CODE)) {
					tipoQRCode = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.TIPO_QR_CODE_PADRAO)) {
					tipoQRCodePadrao = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.TEMPO_QRCODE_DINAMICO)) {
					tempoQRCodeDinamico = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.HABILITA_APP_PEDESTRE)) {
					habilitaAppPedestre = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.REMOVE_CARTAO_EXCLUIDO)) {
					removerCartaoPedestreRemovidos = true;
				}
				if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.TEMPO_TOLERANCIA_ENTRADA_E_SAIDA)) {
					tempoToleranciaEntrada = true;
				}

			}
			if (!preencherCartaoComMatricula) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.PREENCHER_CARTAO_COM_MATRICULA,
						"false", getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			
			if (!preencherCartaoAuto) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.PREENCHER_CARTAO_AUTO,
						"false", getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}

			if (!gerarMatriculaSequencial) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.GERAR_MATRICULA_SEQUENCIAL,
						"false", getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!escolherDigitos) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.ESCOLHER_QTDE_DIGITOS_CARTAO, "10",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!permitirCampoAdicionalMatricula) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.PERMITIR_CAMPO_ADICIONAL_CRACHA,
						"true", getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!formatoDataImportacao) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.FORMATO_DATA_PARA_IMPORTACAO,
						"dd/MM/yyyy", getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!formatoHoraImportacao) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.FORMATO_HORA_PARA_IMPORTACAO,
						"HH:mm", getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!horarioDisparoSOC) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.HORARIO_DISPARO_SOC, "-1",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!camposObrigatoriosPedestre) {
				ParametroEntity p = new ParametroEntity(
						BaseConstant.PARAMETERS_NAME.CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE, "",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!validarMatriculasDuplicadas) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.VALIDAR_MATRICULAS_DUPLICADAS,
						"false", getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!validarCPFDuplicado) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.VALIDAR_CPF_DUPLICADO, "false",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!validarRGDuplicado) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.VALIDAR_RG_DUPLICADO, "false",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!validarCartaoAcessoDuplicado) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.VALIDAR_CARTAO_ACESSO_DUPLICADO,
						"false", getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!cadastroEmLote) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.CADASTRO_EM_LOTE, "false",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!permitarAcessoViaQRCode) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.PERMITIR_ACESSO_QR_CODE, "true",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!chaveIntegracaoContelli) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.CHAVE_DE_INTEGRACAO_COMTELE, "",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!diasValidadeLinkCadastroFacialExterno) {
				ParametroEntity p = new ParametroEntity(
						BaseConstant.PARAMETERS_NAME.DIAS_VALIDADE_LINK_CADASTRO_FACIAL_EXTERNO, "3",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!exibeCampoSempreLiberadoParaTodos) {
				ParametroEntity p = new ParametroEntity(
						BaseConstant.PARAMETERS_NAME.EXIBE_CAMPO_SEMPRE_LIBERADO_PARA_TODOS, "false",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!habilitaAcessoPorReconhecimentoFacial) {
				ParametroEntity p = new ParametroEntity(
						BaseConstant.PARAMETERS_NAME.HABILITA_ACESSO_POR_RECONHECIMENTO_FACIAL, "false",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			if (!limiteDigitaisPorPedestre) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.LIMITE_DIGITAIS_PEDESTRE,
						"Sem limites", getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}

			if (!tipoQRCode) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.TIPO_QR_CODE, "false",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}

			if (!tipoQRCodePadrao) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.TIPO_QR_CODE_PADRAO, "ESTATICO",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}

			if (!tempoQRCodeDinamico) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.TEMPO_QRCODE_DINAMICO, "5",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}

			if (!habilitaAppPedestre) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.HABILITA_APP_PEDESTRE, "false",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}

			if (!removerCartaoPedestreRemovidos) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.REMOVE_CARTAO_EXCLUIDO, "false",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}
			
			if (!tempoToleranciaEntrada) {
				ParametroEntity p = new ParametroEntity(BaseConstant.PARAMETERS_NAME.TEMPO_TOLERANCIA_ENTRADA_E_SAIDA, "5",
						getUsuarioLogado().getCliente());
				parametrosGerais.add(p);
			}

			// ordena configurações
			Collections.sort(parametrosGerais, new Comparator<ParametroEntity>() {
				@Override
				public int compare(ParametroEntity o1, ParametroEntity o2) {
					return o1.getNomeAux().compareTo(o2.getNomeAux());
				}
			});
		}
	}

	@Override
	public String salvar() {
		List<BaseEntity> all = new ArrayList<BaseEntity>();
		String valorOriginalSOC = this.valorOriginalSOC + "";
		all.addAll(parametrosGerais);

		for (ParametroEntity baseEntity : parametrosGerais) {
			if (baseEntity.getNome().startsWith("Campos")) {
				StringBuilder value = new StringBuilder();

				camposObrigatorios.forEach(campo -> {
					value.append(campo + ",");
				});

				baseEntity.setValor(value.toString());
				break;
			}
		}

		try {
			all = baseEJB.gravaTodos(all);

			atualizaListas(all);

			mensagemInfo("", "msg.uc007.atualizado");

			if (valorHabilitaAcessoReconhecimentoFacial != getValorHabilitaAcessoReconhecimentoFacial(all))
				menuController.montaMenu();
			
			if(!valorOriginalSOC.equals(getValorSOC(all)))
                ConectionUtils.get("http://localhost:8081/sistema/configuraRotinas"
                                    + "?registraSOC=S&idCliente="+getUsuarioLogado().getCliente().getId());

		} catch (Exception e) {
			mensagemFatal("", "msg.uc007.nao.atualizado");
			e.printStackTrace();
		}

		return "";

	}

	private Boolean getValorHabilitaAcessoReconhecimentoFacial(List<BaseEntity> all) {
		for (BaseEntity baseEntity : all) {
			ParametroEntity item = (ParametroEntity) baseEntity;

			if (item.getNome().equals(BaseConstant.PARAMETERS_NAME.HABILITA_ACESSO_POR_RECONHECIMENTO_FACIAL)) {
				return Boolean.valueOf(item.getValor());
			}
		}

		return false;
	}
	
    private String getValorSOC(List<BaseEntity> all) {
        for(BaseEntity baseEntity : all) {
            ParametroEntity item = (ParametroEntity) baseEntity;
            if(item.getNome().equals(BaseConstant.PARAMETERS_NAME.HORARIO_DISPARO_SOC))
                return item.getValor();
        }
        
        return "-1";
    }

	public void adicionaCampoObrigatorio() {
		if (camposObrigatorios.contains(campoObrigatorio)) {
			mensagemFatal("", "#Campo já adicionado!");
			return;
		}
		camposObrigatorios.add(campoObrigatorio);
	}

	public void removerCampoObrigatorio(String campoSelecioando) {
		camposObrigatorios.remove(campoSelecioando);

	}

	public void montaListCamposObrigatorios() {
		listaCamposObrigatorios = new ArrayList<SelectItem>();
		listaCamposObrigatorios.add(new SelectItem(null, "Selecione"));

		listaCamposObrigatorios.add(new SelectItem("data.nascimento",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.data.nascimento", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("email",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.email", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("cpf",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.cpf", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("genero",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.genero", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("rg",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.rg", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("telefone",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.telefone", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("celular",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.celular", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("observacoes",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.observacoes", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("responsavel",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.responsavel", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("empresa",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.empresa", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("departamento",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.departamento", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("cargo",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.cargo", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("centro.custo",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.centro.custo", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("endereco",
				ResourceBundleUtils.getInstance().recuperaChave("label.uc008.endereco", getFacesContext())));
		listaCamposObrigatorios.add(new SelectItem("cartao.acesso", ResourceBundleUtils.getInstance()
				.recuperaChave("label.uc008.codigo.cartao.acesso", getFacesContext())));
	}
	
	public List<SelectItem> getListaCamposObrigatorios() {
		return listaCamposObrigatorios;
	}

	public List<String> getCamposObrigatorios() {
		return camposObrigatorios;
	}

	public void setCamposObrigatorios(List<String> camposObrigatorios) {
		this.camposObrigatorios = camposObrigatorios;
	}

	public String getCampoObrigatorio() {
		return campoObrigatorio;
	}

	public void setCampoObrigatorio(String campoObrigatorio) {
		this.campoObrigatorio = campoObrigatorio;
	}

	public List<ParametroEntity> getParametrosGerais() {
		return parametrosGerais;
	}

}
