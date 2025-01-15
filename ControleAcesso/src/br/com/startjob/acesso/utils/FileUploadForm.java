package br.com.startjob.acesso.utils;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class FileUploadForm {

	  private byte[] filedata;

	    public FileUploadForm() {}

	    public byte[] getFileData() {
	        return filedata;
	    }

	    @FormParam("image")
	    @PartType("application/octet-stream")
	    public void setFileData(final byte[] filedata) {
	        this.filedata = filedata;
	    }
}
