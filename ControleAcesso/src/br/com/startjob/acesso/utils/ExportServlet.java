package br.com.startjob.acesso.utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.startjob.acesso.modelo.BaseConstant;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

/**
 * Servlet para download de arquivos
 * @author Gustavo Diniz
 *
 */
@SuppressWarnings("serial")
@WebServlet("/export")
public class ExportServlet extends HttpServlet {
	
	/**
     * 
     * Executa doGEt
     *
     * @param req r
     * @param resp  r
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    {
        doPost(req, resp);
    }

    /**
     * 
     * Executa download completo
     * 
     * @param req r
     * @param resp r
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp){
    	
    	try {
    		
    		String fileName = "";
    		byte[] bytes = null;
    		
    		
			//pega arquivo
        	fileName = (String)req.getSession().getAttribute(BaseConstant.EXPORT.FILE_NAME);
        	bytes = (byte[]) req.getSession().getAttribute(BaseConstant.EXPORT.BYTES);
        	
        	//limpa
        	req.getSession().removeAttribute(BaseConstant.EXPORT.BYTES);
			req.getSession().removeAttribute(BaseConstant.EXPORT.FILE_NAME);
        	
        	
    		if(bytes != null) {
    		
	    		ServletOutputStream out = resp.getOutputStream();
	    		
	        	String contentType = "text/plain";
	        	if(!fileName.contains(".sh") && !fileName.contains(".mp4")
	        			&& !fileName.toLowerCase().endsWith(".tst") 
	        			&& !fileName.toLowerCase().endsWith(".rem")){
	        		MagicMatch match = Magic.getMagicMatch(bytes);
	            	contentType = match.getMimeType();
	        	}
	        	
	        	resp.setHeader(	"Content-disposition", "attachment; filename="+fileName);
	        	resp.setContentType(contentType);
	        	resp.setContentLength(bytes.length); 
	
				out.write(bytes);
	        	
	        	out.flush();
				out.close();
			
    		}
			
		
    	} catch (Exception e) {
			e.printStackTrace();
		}
		
    }

}
