package com.protreino.luxandserver.controller;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.protreino.luxandserver.entity.UserEntity;
import com.protreino.luxandserver.repository.UserRepository;
import com.protreino.luxandserver.security.JwtTokenUtil;
import com.protreino.luxandserver.service.LuxandService;
import com.protreino.luxandserver.to.ExternalLoginResponse;
import com.protreino.luxandserver.to.LoginRequest;
import com.protreino.luxandserver.to.LoginResponse;
import com.protreino.luxandserver.util.HttpConnection;

@RestController
@CrossOrigin(value="*")
@RequestMapping(value = "/auth")
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private LuxandService luxandService;

	@Value("${main.server.url}")
	private String mainServerUrl;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<String> test() {
		return new ResponseEntity<String>("Luxand Server working!", HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/getunitname", method = RequestMethod.GET)
	public ResponseEntity<String> getUnitName() {
		List<UserEntity> users = userRepository.findAll();
		return new ResponseEntity<String>(!users.isEmpty() ? users.get(0).getUnitName() : "", HttpStatus.OK);
	}

	@RequestMapping(value = "/istokenvalid", method = RequestMethod.POST)
	public ResponseEntity<Boolean> isTokenValid(@RequestBody String accessToken) {
		return new ResponseEntity<Boolean>(!jwtTokenUtil.isTokenExpired(accessToken), HttpStatus.OK);
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

		LoginResponse loginResponse = new LoginResponse();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		final String unitName = loginRequest.getUnitname();
		final String username = loginRequest.getUsername();
		final String password = loginRequest.getPassword();

		// Verifica se há algum usuário salvo localmente
		List<UserEntity> users = userRepository.findAll();
		UserEntity user = null;

		// Se houver usuário salvo localmente, então verifica se o username é o mesmo
		if (!users.isEmpty()) {
			
			user = users.get(0);

			// Se o username N�o for o mesmo, então retorna 401
			if (!user.getUsername().equals(username)) {
				loginResponse.setMessage("Usuário N�o autorizado.");
				return new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.UNAUTHORIZED);
			}

			// Se for o mesmo username, então primeiro tenta fazer a validação localmente
			if (encoder.matches(password, user.getPassword())) {
				
				// Se a senha estiver correta, então retorna o token de acesso
				final String token = jwtTokenUtil.generateToken(username);

				loginResponse.setName(user.getName());
				loginResponse.setAccessToken(token);
				return new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.OK);
			}

		}

		// Se N�o houver usuário salvo localmente, ou se a senha N�o coincidir com a senha salva localmente,
		// então valida o login no servidor externo
		
		ExternalLoginResponse externalLoginResponse = null;
		
		try {
			HttpConnection httpCon = new HttpConnection(mainServerUrl + "/login/do?loginName=" + username + "&passwd=" + password + "&unidadeName=" + unitName);
			
			int status = httpCon.getResponseCode();
			
			if (status == HttpURLConnection.HTTP_OK) {
				externalLoginResponse = new ObjectMapper().readValue(httpCon.getResponse(), ExternalLoginResponse.class);
				
				// Se o acesso for validado, então salva os dados localmente e retorna os dados
				// para geração do token de acesso
				if (externalLoginResponse != null && "OK".equals(externalLoginResponse.getStatus())) {
		
					final String token = jwtTokenUtil.generateToken(username);
					final String hash = encoder.encode(password);
					
					// salva os dados do usuário localmente
					if (user == null) {
						// cria um novo registro
						
						user = new UserEntity(username, hash, unitName, externalLoginResponse.getObject().getNome(),
								externalLoginResponse.getObject().getCliente().getId());
					} 
					else {
						// apenas atualiza a senha salva locamente
						user.setPassword(hash);
					}
					userRepository.save(user);
					
					// inicia o serviço de reconhecimento
					luxandService.init();
		
					loginResponse.setName(user.getName());
					loginResponse.setAccessToken(token);
					return new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.OK);
				}
				
			}
			
		}
		catch (ConnectException ce) {
			logger.error("***** Erro de conexao com o servidor *****");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Se N�o conseguiu acessar o servidor, então retorna erro 500
		if (externalLoginResponse == null) {
			loginResponse.setMessage("N�o foi possível acessar o servidor de acesso.");
			return new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Se o acesso N�o for validado, então retorna erro 401
		if ("Internal Server Error".equals(externalLoginResponse.getStatus())) {
			loginResponse.setMessage(externalLoginResponse.getTranslatedMessage());
			return new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.UNAUTHORIZED);
		}

		// Erro desconhecido
		loginResponse.setMessage(externalLoginResponse.getStatus() + " - " + externalLoginResponse.getMessage());
		return new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public void logout() {
		luxandService.FinalizeSDK();
		
		userRepository.deleteAll();
	}

}
