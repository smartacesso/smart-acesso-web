package br.com.startjob.acesso.modelo.ejb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.startjob.acesso.modelo.entity.PedestreEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AppEJB extends BaseEJB implements AppEJBRemote {

	@Override
	public PedestreEntity buscarPorLoginECliente(String login, String cliente) {

	    long inicio = System.nanoTime();
	    
	    System.out.println("cliente recebido: [" + cliente + "]");
	    System.out.println("login recebido: [" + login + "]");

	    Map<String, Object> args = new HashMap<>();
	    args.put("LOGIN", login.trim().toLowerCase());
	    args.put("UNIDADE_ORGANIZACIONAL", cliente.trim().toLowerCase());

	    try {
	        @SuppressWarnings("unchecked")
	        List<PedestreEntity> resultado =
	                (List<PedestreEntity>) this.pesquisaArgFixos(
	                        PedestreEntity.class, "findByLoginOtimizado", args
	                );

	        if (resultado == null || resultado.isEmpty()) {
	            return null;
	        }

	        return resultado.get(0);

	    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        long fim = System.nanoTime();
	        System.out.println("Tempo de busca (ms): " + (fim - inicio) / 1_000_000);
	    }
		return null;
	}
}
