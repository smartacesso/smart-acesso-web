package br.com.startjob.acesso.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import br.com.startjob.acesso.api.WebSocketLiberacaoEndpoint;
import br.com.startjob.acesso.repositories.HikivisionEquipamentosRepository;
import br.com.startjob.acesso.to.HikivisionDeviceSimplificadoTO;

@SuppressWarnings("serial")
@Named("cameraController")
@SessionScoped
public class CameraController extends CadastroBaseController {

	private static final long serialVersionUID = 1L;
	
	
    private HikivisionEquipamentosRepository HikivisionEquipamentosRepository = new HikivisionEquipamentosRepository(); 

    private List<HikivisionDeviceSimplificadoTO> listaCameras;
    private String filtro;

    @PostConstruct
    public void init() {
        carregarCameras();
    }

    public void carregarCameras() {
        // Recupera todas as câmeras sem duplicidade
        listaCameras = new ArrayList<>();

        Map<String, HikivisionDeviceSimplificadoTO> equipamentosMap =
                HikivisionEquipamentosRepository.getEquipamentos(getUsuarioLogado().getCliente().getId().toString()); // ou id do cliente

        if (equipamentosMap != null && !equipamentosMap.isEmpty()) {
            listaCameras.addAll(equipamentosMap.values()); // pega apenas os equipamentos
        }
    }


//    public String buscar() {
//        carregarCameras();
//        if (filtro != null && !filtro.trim().isEmpty()) {
//            String filtroLower = filtro.toLowerCase();
//            listaCameras.removeIf(cam -> !cam.getDevName().toLowerCase().contains(filtroLower)
//                    && !cam.getDevIndex().toLowerCase().contains(filtroLower));
//        }
//    }

	public void liberarCamera(HikivisionDeviceSimplificadoTO camera) {
		
        WebSocketLiberacaoEndpoint.enviarLiberacao(getUsuarioLogado().getCliente().getId().toString(), camera.getDevIndex());
	}

    public List<HikivisionDeviceSimplificadoTO> getListaCameras() {
        return listaCameras;
    }

    public void setListaCameras(List<HikivisionDeviceSimplificadoTO> listaCameras) {
        this.listaCameras = listaCameras;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

}
