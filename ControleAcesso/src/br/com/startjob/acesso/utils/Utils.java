package br.com.startjob.acesso.utils;

import java.io.ByteArrayInputStream;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class Utils {
	
	public static StreamedContent getStreamedContent(byte[] foto) {
		return DefaultStreamedContent.builder()
				.contentType("image/png")
				.stream(() -> new ByteArrayInputStream(foto))
				.build();
	}
}
