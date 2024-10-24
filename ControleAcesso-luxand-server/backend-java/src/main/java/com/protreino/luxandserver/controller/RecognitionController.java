package com.protreino.luxandserver.controller;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.protreino.luxandserver.repository.ConfigurationRepository;
import com.protreino.luxandserver.repository.UserRepository;
import com.protreino.luxandserver.service.LuxandService;
import com.protreino.luxandserver.to.FeedFrameRequest;
import com.protreino.luxandserver.to.FeedFrameResponse;

@RestController
@CrossOrigin(value = "*")
@RequestMapping(value = "/recognition")
public class RecognitionController {

	@Autowired
	private ConfigurationRepository configurationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LuxandService luxandService;

	@Value("${main.server.url}")
	private String mainServerUrl;
	
	@RequestMapping(value = "/configurations", method = RequestMethod.GET)
	public ResponseEntity<Map<String, String>> getConfigurationsForDesktop() {
		
		if (!isLoggedIn()) {
			return new ResponseEntity<Map<String, String>>(HttpStatus.UNAUTHORIZED);
		}
		
		Map<String, String> configurationsMap = new HashMap<String, String>();
		
		configurationsMap.put("HandleArbitraryRotations", configurationRepository.findByName("HandleArbitraryRotations").getValue());
		configurationsMap.put("DetermineFaceRotationAngle", configurationRepository.findByName("DetermineFaceRotationAngle").getValue());
		configurationsMap.put("InternalResizeWidth", configurationRepository.findByName("InternalResizeWidth").getValue());
		configurationsMap.put("FaceDetectionThreshold", configurationRepository.findByName("FaceDetectionThreshold").getValue());
		configurationsMap.put("FacilitateRecognizeWithMask", configurationRepository.findByName("FacilitateRecognizeWithMask").getValue());
		
        return new ResponseEntity<Map<String, String>>(configurationsMap, HttpStatus.OK);
    }
	
	@RequestMapping(value = "/identifiers/{identifier}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeTagForIdentifier(@PathVariable(value = "identifier") long identifier) {
		
		if (!isLoggedIn()) {
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		}
		
		String ret = luxandService.clearName(identifier);
		
		if (ret.equals("")) {
			return new ResponseEntity<String>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<String>(ret, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	@RequestMapping(value = "/recognize", method =  RequestMethod.POST)
	public ResponseEntity<FeedFrameResponse> reconize(@RequestBody FeedFrameRequest request) {
		
		if (!isLoggedIn()) {
			return new ResponseEntity<FeedFrameResponse>(HttpStatus.UNAUTHORIZED);
		}
		
		List<byte[]> images = new ArrayList<byte[]>();
		for (String imageBase64 : request.getImagesBase64()) {
			images.add(Base64.decodeBase64(imageBase64));
		}
		
		request.setMode("VERIFICATION");
		request.setImages(images);
		
		FeedFrameResponse feedFrameResponse = luxandService.feedFrame(request);
		
		return new ResponseEntity<FeedFrameResponse>(feedFrameResponse, HttpStatus.OK);
    }

	@RequestMapping(value = "/enroll", method =  RequestMethod.POST)
	public ResponseEntity<FeedFrameResponse> enroll(@RequestBody FeedFrameRequest request) {
		
		if (!isLoggedIn()) {
			return new ResponseEntity<FeedFrameResponse>(HttpStatus.UNAUTHORIZED);
		}
		
		List<byte[]> images = new ArrayList<byte[]>();
		for (String imageBase64 : request.getImagesBase64()) {
			images.add(Base64.decodeBase64(imageBase64));
		}
		
		request.setMode("ENROLLMENT");
		request.setImages(images);
		
		FeedFrameResponse feedFrameResponse = luxandService.feedFrame(request);
		
		return new ResponseEntity<FeedFrameResponse>(feedFrameResponse, HttpStatus.OK);
    }
	
	private boolean isLoggedIn() {
		return userRepository.count() > 0;
	}
	
}
