package br.com.startjob.acesso.modelo.ejb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.startjob.acesso.modelo.entity.CredenciaisIntegracaoTotvsEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TotvsEduEJB extends BaseEJB implements TotvsEduEJBRemote{
	
	
	public CredenciaisIntegracaoTotvsEntity findByUsuario(String usuario) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("USUARIO", usuario);

		try {
			@SuppressWarnings("unchecked")
			List<CredenciaisIntegracaoTotvsEntity> credenciais = (List<CredenciaisIntegracaoTotvsEntity>) this
					.pesquisaArgFixos(CredenciaisIntegracaoTotvsEntity.class, "findByUsuario", args);

			if (Objects.isNull(credenciais) || credenciais.isEmpty() || credenciais.size() > 1) {
				return null;
			}

			return credenciais.get(0);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
		
	}
}
