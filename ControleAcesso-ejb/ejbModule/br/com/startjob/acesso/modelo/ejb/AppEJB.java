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
	public PedestreEntity buscarPorLoginECliente(String nome, String cliente) {

	    Map<String, Object> args = new HashMap<>();
	    args.put("LOGIN", nome);
	    args.put("UNIDADE_ORGANIZACIONAL", cliente);

	    try {
	        @SuppressWarnings("unchecked")
	        List<PedestreEntity> pedestreEncontrado =
	                (List<PedestreEntity>) this.pesquisaArgFixos(
	                        PedestreEntity.class, "findByLogin", args
	                );

	        if (pedestreEncontrado == null || pedestreEncontrado.isEmpty()) {
	            return null;
	        }

	        if (pedestreEncontrado.size() > 1) {
	            // ⚠️ Ideal logar isso (dados duplicados)
	            System.err.println("Mais de um usuário encontrado para login: " + nome);
	        }

	        return pedestreEncontrado.get(0);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null;
	}
	
	
}
