package br.com.startjob.acesso.controller.uc008;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import com.google.gson.Gson;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.api.WebSocketCadastroEndpoint;
import br.com.startjob.acesso.controller.CadastroBaseController;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;
import br.com.startjob.acesso.to.WebSocketPedestrianAccessTO;


@Named("aprovacaoPedestreController")
@ViewScoped
@UseCase(classEntidade = PedestreEntity.class, funcionalidade = "Aprovacao de pedestres")
public class AprovacaoPedestreController extends CadastroBaseController {

	private static final long serialVersionUID = 1L;

	@EJB
	private PedestreEJBRemote pedestreEJB;

	private PedestreEntity pedestreSelecionado;
	private final Gson gson = new Gson();

	@PostConstruct
    @Override
    public void init() {
        super.init();
        buscar(); // Inicia a busca logo que a tela abre
    }

	@Override
    public String buscar() {
        try {
            // 1. Força o filtro para buscar SÓ quem está aguardando aprovação
            getParans().put("aguardandoAprovacao", true);
            
            // 2. Chama a busca padrão limpa
            return super.buscar(); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
    }
	
	public String getNomeEmpresa(PedestreEntity pedestre) {
        try {
            if (pedestre != null && pedestre.getEmpresa() != null && pedestre.getEmpresa().getId() != null) {
                // Recupera a empresa abrindo uma nova sessão com o banco rapidinho
                br.com.startjob.acesso.modelo.entity.EmpresaEntity emp = 
                    (br.com.startjob.acesso.modelo.entity.EmpresaEntity) baseEJB.recuperaObjeto(
                        br.com.startjob.acesso.modelo.entity.EmpresaEntity.class, 
                        pedestre.getEmpresa().getId()
                    );
                
                if (emp != null) {
                    return emp.getNome();
                }
            }
        } catch (Exception e) {
            // Ignora erro silenciosamente para não travar a tela
        }
        return "N/A";
    }

	/**
	 * Ação do botão "Aprovar"
	 */
	/**
	 * Ação do botão "Aprovar"
	 */
	public void aprovarPedestre() {
		try {
			if (pedestreSelecionado != null && pedestreSelecionado.getId() != null) {

				// 1. RECARREGA O PEDESTRE COMPLETO DO BANCO (Evita o LazyInitializationException nas regras)
				java.util.Map<String, Object> args = new java.util.HashMap<>();
				args.put("ID", pedestreSelecionado.getId());
				
				@SuppressWarnings("unchecked")
				List<PedestreEntity> lista = (List<PedestreEntity>) baseEJB.pesquisaArgFixos(PedestreEntity.class, "findByIdComplete", args);
				
				if (lista == null || lista.isEmpty()) {
					mensagemErro("Erro", "Pedestre não encontrado.");
					return;
				}
				
				// Pegamos o objeto 100% carregado com regras, empresa, foto, etc.
				PedestreEntity pedestreCompleto = lista.get(0);

				// 2. Muda o status
				pedestreCompleto.setAguardandoAprovacao(false);
				pedestreCompleto.setUsuario(getUsuarioLogado());

				// 3. Aplica a regra padrão caso não tenha nenhuma
				if (pedestreCompleto.getRegras() == null || pedestreCompleto.getRegras().isEmpty()) {
					// IMPORTANTE: Certifique-se de que o método buscaPedestreRegraPadraoVisitante() 
					// foi copiado para este Controller também!
					PedestreRegraEntity pedestreRegra = buscaPedestreRegraPadraoVisitante();
					pedestreRegra.setPedestre(pedestreCompleto);
					pedestreCompleto.setRegras(java.util.Arrays.asList(pedestreRegra));
				}

				// 4. Salva no banco o objeto recarregado
			

				// 5. Integração com a Hikvision (Envio da foto e cartão)
				String jsonStr = gson.toJson(WebSocketPedestrianAccessTO.fromPedestre(pedestreCompleto));
				String resposta = WebSocketCadastroEndpoint
						.enviarEEsperar(pedestreCompleto.getCliente().getId().toString(), jsonStr);

				if ("ok".equals(resposta)) {
					pedestreEJB.alteraObjeto(pedestreCompleto);
					mensagemInfo("Sucesso", "Visitante aprovado e liberado na catraca!");
				} else {
					// Tratamento dos erros da placa
					if (resposta.contains("UsuarioErro")) {
						mensagemAviso("Atenção", "Aprovado no sistema, mas erro ao enviar Usuário para o equipamento.");
					} else if (resposta.contains("CartaoErro")) {
						mensagemAviso("Atenção", "Aprovado no sistema, mas erro ao enviar Cartão para o equipamento.");
					} else if (resposta.contains("FotoErro")) {
						mensagemAviso("Atenção", "Aprovado no sistema, mas erro ao enviar Foto para o equipamento.");
					} else {
						mensagemAviso("Atenção", "Resposta inesperada do equipamento: " + resposta);
					}
				}

				// Limpa a seleção e recarrega a tabela
				pedestreSelecionado = null;
				buscar();
			}
		} catch (Exception e) {
			e.printStackTrace();
			mensagemErro("Erro", "falha.ao.aprovar" + e.getMessage());
		}
	}

	/**
	 * Ação do botão "Rejeitar"
	 */
	public void rejeitarPedestre() {
		try {
			if (pedestreSelecionado != null) {
				// Opção 2: Inativar (se usar Soft Delete)
				 pedestreSelecionado.setAguardandoAprovacao(false);
				 pedestreEJB.gravaObjeto(pedestreSelecionado);

				mensagemInfo("Rejeitado", "O cadastro do visitante foi cancelado e removido.");

				// Limpa a seleção e recarrega a tabela
				pedestreSelecionado = null;
				buscar();
			}
		} catch (Exception e) {
			e.printStackTrace();
			mensagemErro("Erro", "Falha ao rejeitar: " + e.getMessage());
		}
	}

	/**
	 * Converte a String Base64 do banco para imagem visível no dataTable
	 */
//	public StreamedContent getSreamedContent(String fotoBase64) {
//		if (fotoBase64 != null && fotoBase64.contains(",")) {
//			try {
//				// Separa o prefixo "data:image/jpeg;base64," do conteúdo real
//				String base64Image = fotoBase64.split(",")[1];
//				byte[] imageBytes = Base64.getDecoder().decode(base64Image);
//
//				return DefaultStreamedContent.builder().contentType("image/jpeg")
//						.stream(() -> new ByteArrayInputStream(imageBytes)).build();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}

	// Getters e Setters
	public PedestreEntity getPedestreSelecionado() {
		return pedestreSelecionado;
	}

	public void setPedestreSelecionado(PedestreEntity pedestreSelecionado) {
		this.pedestreSelecionado = pedestreSelecionado;
	}

}
