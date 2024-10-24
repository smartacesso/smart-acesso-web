package com.protreino.luxandserver.service;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.protreino.luxandserver.entity.ConfigurationEntity;
import com.protreino.luxandserver.entity.UserEntity;
import com.protreino.luxandserver.repository.ConfigurationRepository;
import com.protreino.luxandserver.repository.UserRepository;
import com.protreino.luxandserver.to.CadastroExterno;
import com.protreino.luxandserver.to.CadastroExternoProcessado;
import com.protreino.luxandserver.to.Face;
import com.protreino.luxandserver.to.FeedFrameRequest;
import com.protreino.luxandserver.to.FeedFrameResponse;
import com.protreino.luxandserver.to.ResultadoProcessamentoCadastroExterno;
import com.protreino.luxandserver.to.SendReceiveTrackerParams;
import com.protreino.luxandserver.util.FSDK;
import com.protreino.luxandserver.util.HttpConnection;
import com.protreino.luxandserver.util.Utils;

import Luxand.FSDK.FSDK_IMAGEMODE;
import Luxand.FSDK.HImage;
import Luxand.FSDK.HTracker;
import Luxand.FSDK.TFacePosition;

@Service
public class LuxandService {
	
	private static final Logger logger = LoggerFactory.getLogger(LuxandService.class);
	
	public static final String PATH_FILE_MASKS_BIN = "fd_masks1.bin";
	

	@Value("${main.server.url}")
	private String mainServerUrl;
	
	@Autowired
	private ConfigurationRepository configurationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private String trackerMemoryFilePath = com.protreino.luxandserver.util.Utils.getAppDataFolder() + "/tracker.dat";
	private HTracker tracker;
	private boolean usingTracker = false;
	private boolean savingTracker = false;
	private boolean configuringTracker = false;
	private long saveTrackerInterval = 30 * 60 * 1000; // 30 min
	private long getPedestresCadastradosInterval;
	private Timer saveTrackerTimer;
	private Timer getPedestresCadastradosTimer;
	private boolean initialized = false;
	
	@PostConstruct
    public void init() {
		
		if (userRepository.count() == 0) {
			logger.info("LuxandService não iniciado porque não há usuário logado.");
			return;
		}
		
		logger.info("Iniciando LuxandService...");
		
		try {
			//String binPath = System.getProperty("user.dir");
			//System.setProperty("jna.library.path", binPath + "\\bin\\win32_x86");
			
			int ret = FSDK.ActivateLibrary(Utils.LUXAND_KEY);
			if (ret != FSDK.FSDKE_OK)
				throw new Exception("Não foi possível ativar a Luxand FaceSDK: " + getResultDescription(ret));
			
			ret = FSDK.Initialize();
			if (ret != FSDK.FSDKE_OK)
				throw new Exception("Não foi possível inicializar a Luxand FaceSDK: " + getResultDescription(ret));
			
			// verifica se há algum tracker salvo localmente
			tracker = new HTracker();
			ret = FSDK.LoadTrackerMemoryFromFile(tracker, trackerMemoryFilePath);
	        if (ret != FSDK.FSDKE_OK) {
	        	
	        	ret = loadTrackerFromMainServer();
	        	
	        	if (ret != FSDK.FSDKE_OK) {
		        	ret = FSDK.CreateTracker(tracker);
		        	if (ret != FSDK.FSDKE_OK)
		        		throw new Exception("Não foi possível criar o Tracker: " + getResultDescription(ret));
	        	}
	        }
	        
	        configureService();
	        
	        saveTrackerTimer = new Timer();
	        saveTrackerTimer.scheduleAtFixedRate(new TimerTask() {
	        	public void run() {
	        		saveTracker();
	        	}
	        }, saveTrackerInterval, saveTrackerInterval);
	        
	        configurePedestresCadastradosTimer();
	        
	        initialized = true;
		}
		catch(Exception e) {
			logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
	
	@PreDestroy
	public void FinalizeSDK() {
		logger.info("=====================================================");
		logger.info("Finalizando SDK...");
		
		if (initialized) {
			if (saveTrackerTimer != null)
				saveTrackerTimer.cancel();
			
			if (getPedestresCadastradosTimer != null)
				getPedestresCadastradosTimer.cancel();
			
			if (tracker != null) {
				saveTracker();
				FSDK.FreeTracker(tracker);
			}
			
			FSDK.Finalize();
			
			initialized = false;
			tracker = null;
			saveTrackerTimer = null;
			getPedestresCadastradosTimer = null;
		}
		
		logger.info("SDK finalizada.");
	}
	
	public synchronized String saveTracker() {
		String returnMessage = null;
		try {
			logger.info("=====================================================");
			logger.info("Salvando tracker...");
    		
			while (usingTracker || configuringTracker) { }
    		
			savingTracker = true;
			
			if (tracker != null) {
				int ret = FSDK.SaveTrackerMemoryToFile(tracker, trackerMemoryFilePath);
				
				if (ret == FSDK.FSDKE_OK)
					logger.info("Tracker salvo!");
				else
					logger.error("Erro ao salvar o Tracker. Error code: " + getResultDescription(ret));
				
				// Preparando buffer de bytes para enviar para o servidor principal.
				// A flag savingTracker será liberada ser precisar esperar pela resposta do servidor.
				
				long[] buffSize = new long[1];
				ret = FSDK.GetTrackerMemoryBufferSize(tracker, buffSize);
				if (ret == FSDK.FSDKE_OK) {
					
					byte[] buffer = new byte[Long.valueOf(buffSize[0]).intValue()];
					ret = FSDK.SaveTrackerMemoryToBuffer(tracker, buffer);
					if (ret == FSDK.FSDKE_OK) {
						
						// Libera a flag savingTracker
						savingTracker = false;
						
						returnMessage = sendTrackerToMainServer(buffer);
					}
					else {
						returnMessage = "Não foi possível enviar o tracker para o servidor. Error code: " + ret;
						logger.error(returnMessage);
					}
				}
				else {
					returnMessage = "Não foi possível enviar o tracker para o servidor. Error code: " + ret;
					logger.error(returnMessage);
				}
			}
			else {
				returnMessage = "Tracker não carregado.";
				logger.error(returnMessage);
			}
    		
		}
		catch (Exception e) {
			e.printStackTrace();
			returnMessage = e.getMessage();
		}
		finally {
			savingTracker = false;
		}
		
		return returnMessage;
	}
	
	private int loadTrackerFromMainServer() {
		try {
			UserEntity loggedUser = userRepository.findAll().get(0);
			
			HttpConnection httpConn = new HttpConnection(mainServerUrl + "/facial/requestBackupFileTracker?client=" + loggedUser.getClientId()); 
			
			int status = httpConn.getResponseCode();
			
			if (status == HttpURLConnection.HTTP_OK) {
				SendReceiveTrackerParams params = new ObjectMapper().readValue(httpConn.getResponse(), SendReceiveTrackerParams.class);
				
				if (params.getFileTracker() != null && !params.getFileTracker().isEmpty()) {
					byte[] buffer = Base64.decodeBase64(params.getFileTracker());
					tracker = new HTracker();
					return FSDK.LoadTrackerMemoryFromBuffer(tracker, buffer);
				}
			}
			
			return FSDK.FSDKE_FILE_NOT_FOUND;
			
		}
		catch (ConnectException ce) {
			logger.error("***** Erro de conexao com o servidor *****");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return FSDK.FSDKE_SERVER_EXCEPTION;
	}
	
	private String sendTrackerToMainServer(byte[] buffer) {
		try {
			UserEntity loggedUser = userRepository.findAll().get(0);
			
			SendReceiveTrackerParams params = new SendReceiveTrackerParams();
			params.setIdCliente(loggedUser.getClientId());
			params.setFileTracker(Base64.encodeBase64String(buffer));
			
			HttpConnection httpConn = new HttpConnection(mainServerUrl + "/facial/uploadFileTracker"); 
			int status = httpConn.sendJson(new ObjectMapper().writeValueAsString(params));
			
			if (status == HttpURLConnection.HTTP_OK) {
				return "";
			}
			
		}
		catch (ConnectException ce) {
			logger.error("***** Erro de conexao com o servidor *****");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return "Erro ao enviar o arquivo Tracker para o servidor principal.";
	}
	
	public void configureService() throws Exception {
		try {
			while (savingTracker || usingTracker) { }
			
			configuringTracker = true;
			
			List<ConfigurationEntity> configurations = configurationRepository.findAll();
	        
	        Map<String, String> configurationsMap = new HashMap<String, String>();
	        for (ConfigurationEntity configuration : configurations) {
	        	if (configuration.getName().equals("IntervalCheckRegistrationNewFaces")) {
	        		getPedestresCadastradosInterval = Long.valueOf(configuration.getValue()) * 60 * 1000;
	        		configurePedestresCadastradosTimer();
	        	}
	        	else {
	        		configurationsMap.put(configuration.getName(), configuration.getValue());
	        	}
	        }
	        
	        // Tracker API designed to work with continuous video streams.
	        // To work with images need to set this parameter to false.
	        configurationsMap.put("VideoFeedDiscontinuity", "0");
	        
	        for (Entry<String, String> entry : configurationsMap.entrySet()) {
	        	if("FacilitateRecognizeWithMask".equals(entry.getKey())) {
	        		if(Boolean.valueOf(entry.getValue())) {
	        			int [] erros = new int [10];
	        	        FSDK.SetTrackerMultipleParameters(tracker, "FaceDetectionModel="+PATH_FILE_MASKS_BIN+";TrimFacesWithUncertainFacialFeatures=false", erros);
	        		}
	        		continue;
	        	}
	        	
	        	int ret = FSDK.SetTrackerParameter(tracker, entry.getKey(), entry.getValue());
	        	if (ret != FSDK.FSDKE_OK)
		        	throw new Exception("Não foi possível setar o parâmetro " + entry.getKey() + "da Luxand FaceSDK: " + getResultDescription(ret));
	        }
	        
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			configuringTracker = false;
		}
	}
	
	private void configurePedestresCadastradosTimer() {
		if (getPedestresCadastradosTimer != null)
			getPedestresCadastradosTimer.cancel();
		
		getPedestresCadastradosTimer = new Timer();
        getPedestresCadastradosTimer.scheduleAtFixedRate(new TimerTask() {
        	public void run() {
        		getPedestresCadastrados();
        	}
        }, getPedestresCadastradosInterval, getPedestresCadastradosInterval);
	}
	
	private void getPedestresCadastrados() {
		try {
			UserEntity loggedUser = userRepository.findAll().get(0);
			
			HttpConnection httpConn = new HttpConnection(mainServerUrl + "/facial/requestAllPedestresCadastrados?client=" + loggedUser.getClientId()); 
			
			int status = httpConn.getResponseCode();
			
			if (status == HttpURLConnection.HTTP_OK) {
				
				TypeReference<List<CadastroExterno>> typeRef = new TypeReference<List<CadastroExterno>>() {};
				List<CadastroExterno> cadastros = new ObjectMapper().readValue(httpConn.getResponse(), typeRef);
				
				List<ResultadoProcessamentoCadastroExterno> resultados = new ArrayList<ResultadoProcessamentoCadastroExterno>();
				
				for (CadastroExterno cadastro : cadastros) {
					
					List<byte[]> images = new ArrayList<byte[]>();
					if(cadastro.getPrimeiraFoto() != null) 
						images.add(Base64.decodeBase64(cadastro.getPrimeiraFoto()));
					if(cadastro.getSegundaFoto() != null)
						images.add(Base64.decodeBase64(cadastro.getSegundaFoto()));					
					if(cadastro.getTerceiraFoto() != null) 
						images.add(Base64.decodeBase64(cadastro.getTerceiraFoto()));
					
					FeedFrameRequest request = new FeedFrameRequest();
					request.setMode("ENROLLMENT");
					request.setNameToAssign(cadastro.getNome() + " (" + cadastro.getIdPedestre() + ")");
					request.setImageWidth(cadastro.getImageWidth());
					request.setImageHeight(cadastro.getImageHeight());
					request.setImages(images);
					
					FeedFrameResponse feedFrameResponse = null;
					for (int i = 0; i <= 2; i++) {
						//System.out.println("Cadastrar pedestre " + cadastro.getNome() + ". Tentativa " + (i+1));
						feedFrameResponse = feedFrame(request, true);
						if(feedFrameResponse.getResultCode() == 0)
							break;
					}
					//FeedFrameResponse feedFrameResponse = feedFrame(request, true);
					
					ResultadoProcessamentoCadastroExterno resultado = new ResultadoProcessamentoCadastroExterno();
					resultado.setIdCadastro(cadastro.getId());
					resultado.setCodigoResultado(feedFrameResponse.getResultCode());
					resultado.setDescricaoResultado(feedFrameResponse.getResultDescription());
					resultado.setLuxandIdentifier(feedFrameResponse.getResultCode() == FSDK.FSDKE_OK ? feedFrameResponse.getFace().getIdentifier() : null);
					
					resultados.add(resultado);
				}
				
				CadastroExternoProcessado cadastroExternoProcessado = new CadastroExternoProcessado();
				cadastroExternoProcessado.setIdCliente(loggedUser.getClientId());
				cadastroExternoProcessado.setResultados(resultados);
				
				httpConn = new HttpConnection(mainServerUrl + "/facial/registerAllCadastrosExternosProcessados");
				
				status = httpConn.sendJson(new ObjectMapper().writeValueAsString(cadastroExternoProcessado));
				
				if (status != HttpURLConnection.HTTP_OK) {
					logger.error("Erro ao enviar os cadastros externos processados. Status Http: " + status);
				}
			}
			
		}
		catch (ConnectException ce) {
			logger.error("***** Erro de conexao com o servidor *****");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public synchronized FeedFrameResponse feedFrame(FeedFrameRequest request) {
		return this.feedFrame(request, false);
	}
	
	public synchronized FeedFrameResponse feedFrame(FeedFrameRequest request, boolean rotate) {
		
		FeedFrameResponse result = new FeedFrameResponse();
		
		HImage imageHandle = null;
		int cameraIdx = 0; // será sempre zero para essa release da SDK
		int scanLineMultiplier = 3; // FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT
		boolean hasError = false;
		boolean firstImage = true;
		Long identifierAssigned = null;
		
		try {
			
			while (savingTracker || configuringTracker) {}
			
			usingTracker = true;
			
			// itera por todas as imagens quando for treinamento, e se for verificacao, haverá somente uma imagem
			
			int countImg = 0;
			int countEnrollSuccess = 1;
			for (byte[] image : request.getImages()) {
				
				//aguarda um pouco antes de verificar as outras imagens
				//caso esteja avaliando a face
				if ("ENROLLMENT".equals(request.getMode())
						&& countImg > 0)
					Thread.sleep(150);

				countImg++;
			
				// carrega a imagem
				imageHandle = new HImage();
				if(rotate) {
					FSDK.LoadImageFromPngBuffer(imageHandle, image, image.length);
				}else {
					FSDK.LoadImageFromBuffer(imageHandle, image, request.getImageWidth(), request.getImageHeight(), 
							(scanLineMultiplier * request.getImageWidth()), FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT);
				}
				
				//FSDK.SaveImageToFile(imageHandle, "fotos/foto_" + new Date().getTime() + ".png");
				
				// alimenta o tracker. aqui é feito de fato o reconhecimento
				
				long[] identifiers = new long[1];
		        long[] facesWithNameCount = new long[1];
				int ret = FSDK.FeedFrame(tracker, cameraIdx, imageHandle, facesWithNameCount, identifiers);
				
				if (ret != FSDK.FSDKE_OK) {
					hasError = true;
					result.setResultCode(ret);
					break;
				}
				
				// primeiro verifica se encontrou exatamente um rosto na imagem, identificado ou nao
				
				if (identifiers[0] == 0) {
					hasError = true;
					result.setResultCode(FSDK.FSDKE_FACE_NOT_FOUND);
					break;
				}
				
				//if (identifiers[1] > 0 || facesWithNameCount[0] > 1) {
				//	hasError = true;
				//	result.setResultCode(FSDK.FSDKE_TOO_MANY_FACES_ON_IMAGE); 
				//	break;
				//}
				
				
				// foi encontrado exatamente um identificador na imagem
				// agora precisa ver se é um rosto identificado ou não, para isso pega o nome vinculado à face
				// a funcao FSDK_GetAllNames retorna o nome de um identificador, concatenado
				// com os nomes de identificadores similares (caso existam), separados por ;
				
				String[] identifiedNames = new String[1];
				ret = FSDK.GetAllNames(tracker, identifiers[0], identifiedNames, 65536);
				logger.info("\r\n********* Identificador: " + identifiers[0] + "    Nome: " + identifiedNames[0]);
				
				if (ret != FSDK.FSDKE_OK && ret != FSDK.FSDKE_ID_NOT_FOUND) {
					hasError = true;
					result.setResultCode(ret);
					break;
				}
				
				// valida se não retornou mais de um nome associado ao identificador, que seriam os similares
				// TODO será que isto seria um erro? o que poderia ser feito nesse caso?
				
				if (identifiedNames[0] != null && identifiedNames[0].contains(";")) {
					hasError = true;
					result.setResultCode(FSDK.FSDKE_USER_REGISTERED_WITH_OTHER_NAME);
					break;
				}
				
				long[] similarCount = new long[1];
				FSDK.GetSimilarIDCount(tracker, identifiers[0], similarCount);
				
				long[] similar = new long[Long.valueOf(similarCount[0]).intValue()];
				FSDK.GetSimilarIDList(tracker, identifiers[0], similar);
				
				long[] reassigned = new long[1];
				FSDK.GetIDReassignment(tracker, identifiers[0], reassigned);
				
				
				if ("VERIFICATION".equals(request.getMode())) {
					
					if (facesWithNameCount[0] == 0 || "".equals(identifiedNames[0])) {
						hasError = true;
						result.setResultCode(FSDK.FSDKE_USER_NOT_FOUND);
						break;
					}
					
					// tudo certo, retorna o nome que foi atribuido ao rosto encontrado juntamente com a posicao do rosto
					
					TFacePosition.ByReference facePosition = new TFacePosition.ByReference();
					FSDK.GetTrackerFacePosition(tracker, cameraIdx, identifiers[0], facePosition);
					
					result.setFace(new Face(identifiers[0], identifiedNames[0], facePosition));
					result.setResultCode(FSDK.FSDKE_OK);
					break;
					
				}
				else if ("ENROLLMENT".equals(request.getMode())) {
					
					// durante o cadastra, espera-se que na primeira imagem não haja nenhum nome associado ao identificador
					// será entao associado um nome e a partir dai, para as outras imagens, 
					// deve ser retornado exatamente esse nome para confirmar o cadastro
					
					if (firstImage) {
						
						if (facesWithNameCount[0] > 0
								&& identifiedNames[0] != null
								&& !"".equals(identifiedNames[0])
								&& !"null".equals(identifiedNames[0])) {
							System.out.println("identificador if 1 :" +identifiedNames[0]);
							if (request.getNameToAssign().equals(identifiedNames[0])) {
								hasError = true;
								result.setResultCode(FSDK.FSDKE_USER_ALREADY_REGISTERED);
								result.setFace(new Face(identifiers[0], identifiedNames[0], null));
								System.out.println("nome identificador if 2 :" +identifiedNames[0]);
								System.out.println("identificador if 2 :" +identifiers[0]);
							}
							else {
								hasError = true;
								result.setResultCode(FSDK.FSDKE_USER_REGISTERED_WITH_OTHER_NAME);
								result.setFace(new Face(identifiers[0], identifiedNames[0], null));
								System.out.println("nome identificador else :" +identifiedNames[0]);
								System.out.println("identificador else :" +identifiers[0]);
								break;
							}
						}
						
						// associa o nome ao identificador
						setName(identifiers[0], request.getNameToAssign());
						
						// salva o identificador para retorno
						identifierAssigned = identifiers[0];
						
						firstImage = false;
						
					}
					else {
						
						// depois da primeira imagem, espera-se que retorne exatamente o nome que foi associado na iteracao anterior 
						logger.info("Nome esperado: " + identifierAssigned +  "/ Nome encontrado: " + identifiers[0]);
						
//						if (facesWithNameCount[0] == 0 || !identifierAssigned.equals(identifiers[0])) {
//							continue;
//						}
						
						if (!request.getNameToAssign().equals(identifiedNames[0])) {
							hasError = true;
							result.setResultCode(FSDK.FSDKE_USER_REGISTERED_WITH_OTHER_NAME);
							result.setFace(new Face(identifiers[0], identifiedNames[0], null));
							break;
						}
						
						countEnrollSuccess++;
						
					}
					
				}
				
				if (imageHandle != null)
					FSDK.FreeImage(imageHandle);
				
			}
			
//			if ("ENROLLMENT".equals(request.getMode())){
//				int total = request.getImages().size();
//				double porcentagem = (countEnrollSuccess*100.d)/total;
//				logger.info("Porcentagem de acerto: " + porcentagem);
//				if(porcentagem <= 75.d) {
//					//a maioria dos rostos deu erro
//					hasError = true;
//					result.setResultCode(FSDK.FSDKE_RECOGNITION_FAILED_DURING_ENROLLMENT);
//				}
//			}
			
			if (hasError && identifierAssigned != null) {
				clearName(identifierAssigned);
			}
			
			if (!hasError && "ENROLLMENT".equals(request.getMode())) {
				result.setFace(new Face(identifierAssigned, request.getNameToAssign(), null));
				result.setResultCode(FSDK.FSDKE_OK);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(FSDK.FSDKE_SERVER_EXCEPTION);
		}
		finally {
			usingTracker = false;
			
			if (imageHandle != null)
				FSDK.FreeImage(imageHandle);
		}
		
		result.setResultDescription(getResultDescription(result.getResultCode()));
		
		logger.info("FeedFrame finalizado!");
		
		return result;
	}
	
	private void setName(long id, String name) {
		try {
			FSDK.LockID(tracker, id);
			FSDK.SetName(tracker, id, name);
			FSDK.UnlockID(tracker, id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized String clearName(long id) {
		try {
			FSDK.LockID(tracker, id);
			FSDK.SetName(tracker, id, "");
			FSDK.PurgeID(tracker, id);
			FSDK.UnlockID(tracker, id);
			return "";
		}
		catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	@SuppressWarnings("serial")
	private Map<Integer, String> resultDescriptions = new HashMap<Integer, String>() {
	{
		put(0, "OK"); // FSDKE_OK
		put(-1, "Falha"); // FSDKE_FAILED
		put(-2, "SDK não ativada"); // FSDKE_NOT_ACTIVATED
		put(-3, "Falta de memória"); // FSDKE_OUT_OF_MEMORY
		put(-4, "Argumentos inválidos"); // FSDKE_INVALID_ARGUMENT
		put(-5, "Erro de entrada de dados"); // FSDKE_IO_ERROR
		put(-6, "Imagem muito pequena"); // FSDKE_IMAGE_TOO_SMALL
		put(-7, "Rosto não encontrado"); // FSDKE_FACE_NOT_FOUND
		put(-8, "Tamanho de buffer insuficiente"); // FSDKE_INSUFFICIENT_BUFFER_SIZE
		put(-9, "Extensão de arquivo não suportado"); // FSDKE_UNSUPPORTED_IMAGE_EXTENSION
		put(-10, "Não foi possível abrir o arquivo"); // FSDKE_CANNOT_OPEN_FILE
		put(-11, "Não foi possível criar o arquivo"); // FSDKE_CANNOT_CREATE_FILE
		put(-12, "Formato de arquivo inválido"); // FSDKE_BAD_FILE_FORMAT
		put(-13, "Arquivo não encontrado"); // FSDKE_FILE_NOT_FOUND
		put(-14, "Conexão fechada"); // FSDKE_CONNECTION_CLOSED
		put(-15, "Conexão falhou"); // FSDKE_CONNECTION_FAILED
		put(-16, "Inicialização IP falhou"); // FSDKE_IP_INIT_FAILED
		put(-17, "Necessita ativação do servidor"); // FSDKE_NEED_SERVER_ACTIVATION
		put(-18, "ID não encontrado"); // FSDKE_ID_NOT_FOUND
		put(-19, "Atributo não detectado"); // FSDKE_ATTRIBUTE_NOT_DETECTED
		put(-20, "Limite de memória do tracker insuficiente"); // FSDKE_INSUFFICIENT_TRACKER_MEMORY_LIMIT
		put(-21, "Atributo desconhecido"); // FSDKE_UNKNOWN_ATTRIBUTE
		put(-22, "Versão de arquivo não suportada"); // FSDKE_UNSUPPORTED_FILE_VERSION
		put(-23, "Erro de sintaxe"); // FSDKE_SYNTAX_ERROR
		put(-24, "Parâmetro não encontrado"); // FSDKE_PARAMETER_NOT_FOUND
		put(-25, "Template inválido"); // FSDKE_INVALID_TEMPLATE
		put(-26, "Versão do template não suportada"); // FSDKE_UNSUPPORTED_TEMPLATE_VERSION
		put(-27, "Índice da câmera não existe"); // FSDKE_CAMERA_INDEX_DOES_NOT_EXIST
		put(-28, "Plataforma não licensiada"); // FSDKE_PLATFORM_NOT_LICENSED
		put(-29, "Muitos rostos na imagem"); // FSDKE_TOO_MANY_FACES_ON_IMAGE
		put(-30, "Exceção no servidor"); // FSDKE_SERVER_EXCEPTION
		put(-31, "Usuário já registrado"); // FSDKE_USER_ALREADY_REGISTERED
		put(-32, "Usuário registrado com outro nome"); // FSDKE_USER_REGISTERED_WITH_OTHER_NAME
		put(-33, "Reconhecimento falhou"); // FSDKE_RECOGNITION_FAILED
		put(-34, "Erro ao converter imagem para buffer"); // FSDKE_IMAGE_TO_BUFFER_ERROR
		put(-35, "Usuário não encontrado"); // FSDKE_USER_NOT_FOUND
		put(-36, "Usuário não está logado no servidor"); // FSDKE_USER_NOT_LOGGED_IN
		put(-37, "Exceção local"); // FSDKE_LOCAL_EXCEPTION
	}};
	
	public String getResultDescription(int code) {
		return this.resultDescriptions.getOrDefault(code, String.valueOf(code));
	}
	
}
