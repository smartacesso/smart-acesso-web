package com.protreino.luxandserver.controller;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.protreino.luxandserver.entity.ConfigurationEntity;
import com.protreino.luxandserver.enumeration.FieldType;
import com.protreino.luxandserver.repository.ConfigurationRepository;
import com.protreino.luxandserver.service.LuxandService;

@RestController
@CrossOrigin(value="*")
@RequestMapping(value = "/configurations")
public class ConfigurationController {
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

	@Autowired
    private ConfigurationRepository configurationRepository;
	
	@Autowired
	private LuxandService luxandService;
	
	@PostConstruct
    public void init() {
		logger.info("Iniciando ConfigurationController...");
		this.createDefaultConfigurations();
    }
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<ConfigurationEntity> getAllConfigurations() {
        return configurationRepository.findAll();
    }
	
	@RequestMapping(method =  RequestMethod.POST)
	public ResponseEntity<List<ConfigurationEntity>> saveAllConfigurations(@RequestBody List<ConfigurationEntity> configurations) {
		
		List<ConfigurationEntity> savedConfigurations = configurationRepository.saveAll(configurations);
        
        try {
        	luxandService.configureService();
        }
        catch (Exception e) {
        	return new ResponseEntity<List<ConfigurationEntity>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<List<ConfigurationEntity>>(savedConfigurations, HttpStatus.OK);
    }
	
	@RequestMapping(method =  RequestMethod.DELETE)
	public void deleteAllConfigurations() {
        configurationRepository.deleteAll();
    }
	
	@RequestMapping(value = "/savetrackerbackup", method = RequestMethod.POST)
	public ResponseEntity<String> saveTrackerAndSendBackupToMainServer() {
		return new ResponseEntity<String>(luxandService.saveTracker(), HttpStatus.OK);
	}
	
	private void createDefaultConfigurations() {
		logger.info("Criando configurações...");
		
		List<ConfigurationEntity> configurations = new ArrayList<ConfigurationEntity>();
		
		// Face tracking parameters
		
		//configurations.add(new ConfigurationEntity("FaceDetectionModel", "Modelo de detecção", "default", "default", FieldType.NUMBER));
		//configurations.add(new ConfigurationEntity("TrimOutOfScreenFaces", "Excluir face fora da imagem", "true", "true", FieldType.BOOLEAN));
		//configurations.add(new ConfigurationEntity("TrimFacesWithUncertainFacialFeatures", "Excluir face com características incertas", "true", "true", FieldType.BOOLEAN));
		configurations.add(new ConfigurationEntity("HandleArbitraryRotations", "Lidar com rotações arbitrárias", "false", "false", FieldType.BOOLEAN));
		configurations.add(new ConfigurationEntity("DetermineFaceRotationAngle", "Determinar o ângulo de rotação da face", "false", "false", FieldType.BOOLEAN));
		configurations.add(new ConfigurationEntity("InternalResizeWidth", "Largura do redimensionamento interno", "100", "100", FieldType.NUMBER));
		configurations.add(new ConfigurationEntity("FaceDetectionThreshold", "Limiar para detecção de face", "5", "5", FieldType.NUMBER));
		configurations.add(new ConfigurationEntity("FaceTrackingDistance", "Distância para rastreamento de face", "0.5", "0.5", FieldType.NUMBER));
		configurations.add(new ConfigurationEntity("FacilitateRecognizeWithMask", "Facilitar reconhecimento com máscara", "false", "false", FieldType.BOOLEAN));
		
		// Face recognition parameters
		
		//configurations.add(new ConfigurationEntity("RecognizeFaces", "Reconhecer faces", "true", "true", FieldType.BOOLEAN));
		//configurations.add(new ConfigurationEntity("DetectGender", "Detectar gênero", "false", "false", FieldType.BOOLEAN));
		//configurations.add(new ConfigurationEntity("DetectAge", "Detectar idade", "false", "false", FieldType.BOOLEAN));
		//configurations.add(new ConfigurationEntity("DetectExpression", "Detectar expressão", "false", "false", FieldType.BOOLEAN));
		//configurations.add(new ConfigurationEntity("Learning", "Aprender", "true", "true", FieldType.BOOLEAN));
		configurations.add(new ConfigurationEntity("MemoryLimit", "Limite de memória do tracker", "2150", "2015", FieldType.NUMBER));
		configurations.add(new ConfigurationEntity("Threshold", "Limiar para identificação", "0.992", "0.992", FieldType.NUMBER));
		configurations.add(new ConfigurationEntity("KeepFaceImages", "Salvar imagens originais no tracker", "true", "true", FieldType.BOOLEAN));
		
		// Facial feature tracking parameters
		
		//configurations.add(new ConfigurationEntity("DetectEyes", "Detectar olhos", "false", "false", FieldType.BOOLEAN));
		//configurations.add(new ConfigurationEntity("DetectFacialFeatures", "Detectar características faciais", "false", "false", FieldType.BOOLEAN));
		//configurations.add(new ConfigurationEntity("FacialFeatureJitterSuppression", "Suprimir tremor de características faciais", "false", "false", FieldType.BOOLEAN));
		//configurations.add(new ConfigurationEntity("SmoothFacialFeatures", "Suavizar características faciais", "true", "true", FieldType.BOOLEAN));
		//configurations.add(new ConfigurationEntity("FacialFeatureSmoothingSpatial", "Coeficiente de suavização espacial", "0.5", "0.5", FieldType.NUMBER));
		//configurations.add(new ConfigurationEntity("FacialFeatureSmoothingTemporal", "Coeficiente de suavização temporal", "250", "250", FieldType.NUMBER));
		
		// Server parameters
		
		configurations.add(new ConfigurationEntity("IntervalCheckRegistrationNewFaces", "Intervalo para verificar por cadastro de novas faces (min)", 
				"5", "5", FieldType.NUMBER));
		
		
		for (ConfigurationEntity configuration : configurations) {
			if (configurationRepository.findByName(configuration.getName()) == null) {
				logger.info("Criando nova configuração " + configuration.getName() + "...");
				configurationRepository.save(configuration);
			}
		}
		
		logger.info("Configurações criadas!");
		
	}
	
}
