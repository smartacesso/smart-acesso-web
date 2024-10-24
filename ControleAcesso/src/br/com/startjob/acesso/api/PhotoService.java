package br.com.startjob.acesso.api;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.to.PedestrianPhotoTO;

/**
 * ServiÃ§o para atualizar lista de acesso de atletas nas catracas
 * 
 */
@Path("/photo")
public class PhotoService extends BaseService {
	
	private int DEFAULT_IMG_SIZE = 48;
	
	@Context
	private HttpServletRequest request;
	
	@Context 
	private HttpServletResponse response;
	
	/**
	 * Testa webservice.
	 * 
	 * @return string
	 */
	@GET
	@Path("/action")
	@Produces(MediaType.TEXT_PLAIN)
	public Response action(){
		return Response.status(Status.OK).entity("working").build();
	}
	
	@GET
	@Path("/query")
	@Produces(MediaType.TEXT_PLAIN)
	public Response query(@QueryParam("client") Long idClient, 
						   @QueryParam("lastsync") Long lastSync) {
		if(Objects.isNull(idClient) || Objects.isNull(lastSync)) {
			return Response.status(Status.BAD_REQUEST).entity(new StringBuilder().toString()).build();
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		Status statusResponse = Status.OK;

		try {
			Calendar cLastSync = Calendar.getInstance();
			cLastSync.setTimeInMillis(lastSync);
			
			List<Long> ids = new ArrayList<Long>();
			ids.add(idClient);
			
			List<Object> list = (List<Object>) ((PedestreEJBRemote) 
						getEjb("PedestreEJB")).buscaListaPedestresComAtualizacaoDeFoto(ids, cLastSync.getTime());
			
			if (list != null && !list.isEmpty()) {
				for (Object id : list) {
					stringBuilder.append(id.toString() + ";");
				}
				statusResponse = Status.OK;
			}
			
		} catch(Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		
		return Response.status(statusResponse).entity(stringBuilder.toString()).build();
	}

	
	@GET
	@Path("/request")
	@Produces(MediaType.APPLICATION_JSON)
	public Response request(@QueryParam("ids")String ids,
							@QueryParam("resize")Boolean resize,
							@QueryParam("imageSize")Integer imageSize,
							@QueryParam("targetWidth")Integer targetWidth,
							@QueryParam("targetHeight")Integer targetHeight) {

		if(Objects.isNull(ids) || ids.isEmpty() || Objects.isNull(resize)) {
			return Response.status(Status.BAD_REQUEST).entity(new ArrayList<>()).build();
		}
		
		List<PedestrianPhotoTO> returnList = new ArrayList<>();
		
		try {
			List<Long> listaIds = new ArrayList<>();
			for (String s : ids.split(";")) {
				if (listaIds.size() == 50) {
					break;
				}
				
				listaIds.add(Long.valueOf(s));
			}
			
			List<Object[]> list = (List<Object[]>) ((PedestreEJBRemote) 
						getEjb("PedestreEJB")).buscaFotoListaPedestre(listaIds);
			
			if(Objects.isNull(list) || list.isEmpty()) {
				Response.status(Status.NOT_FOUND).entity(returnList).build();
			}
			
			for (Object[] objects : list) {
				Long id = Long.valueOf(objects[0].toString());
				byte[] fotoOriginal = (byte[]) objects[1];
				String fotoBase64 = null;
				if (fotoOriginal != null) {
					byte[] bytes = getPhotoAsByteArray(fotoOriginal, resize, imageSize, targetWidth, targetHeight);
					fotoBase64 = org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
				}
				PedestrianPhotoTO to = new PedestrianPhotoTO(id, fotoBase64);
				returnList.add(to);
			}

		} catch(Exception e) {
			Response.status(Status.INTERNAL_SERVER_ERROR).entity(returnList).build();
			e.printStackTrace();
		}
		
		return Response.status(Status.OK).entity(returnList).build();
	}
	
	private byte[] getPhotoAsByteArray(final byte[] fotoOriginal, final Boolean resize, final Integer imageSize, 
			final Integer targetWidth, final Integer targetHeight) {
		
		if(Boolean.TRUE.equals(resize)
				&& Objects.nonNull(targetWidth) 
				&& Objects.nonNull(targetWidth)) {
			return resizeImageScaledInstance(fotoOriginal, targetWidth, targetHeight);
		}
		
		if(Boolean.TRUE.equals(resize)
				&& Objects.nonNull(imageSize)) {
			return resizeImage(fotoOriginal, imageSize);
		}
		
		return fotoOriginal;
	}

	private byte[] resizeImage(byte[] originalBytes, Integer imageSize) {
		try {
			if (imageSize == null) {
				imageSize = DEFAULT_IMG_SIZE;
			}
			
			BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalBytes));
			BufferedImage resizedImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(originalImage, 0, 0, imageSize, imageSize, null);
			g.dispose();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(resizedImage, "jpg", bos);
			return bos.toByteArray();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return originalBytes;
	}
	
	private byte[] resizeImageScaledInstance(byte[] originalBytes, final int targetWidth, final int targetHeight) {
		try (ByteArrayInputStream originalImageInputStream = new ByteArrayInputStream(originalBytes);
				ByteArrayOutputStream resizedImageOutputStream = new ByteArrayOutputStream();){
			
			final BufferedImage originalImage = ImageIO.read(originalImageInputStream);
			final Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
			final BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
			outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
			
			
			ImageIO.write(outputImage, "jpg", resizedImageOutputStream);
			return resizedImageOutputStream.toByteArray();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return originalBytes;
	}

}