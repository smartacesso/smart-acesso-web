package br.com.startjob.acesso.modelo.ejb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.ResponsibleEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ResponsibleEJB extends BaseEJB implements ResponsibleEJBRemote {

	@Override
	public Optional<ResponsibleEntity> findResponsibleByLoginAndPassword(final String login, final String password) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("LOGIN", login);
		args.put("PASSWORD", password);
		
		try {
			@SuppressWarnings("unchecked")
			List<ResponsibleEntity> responblibleList = (List<ResponsibleEntity>) this.pesquisaArgFixos(ResponsibleEntity.class, "findByLoginAndPassword", args);
			
			if(Objects.isNull(responblibleList) || responblibleList.isEmpty() || responblibleList.size() > 1) {
				return Optional.empty();
			}
			
			return Optional.of(responblibleList.get(0));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}


	@Override
	public List<PedestreEntity> findAllDependentsPageable(long idResponsible, int page, int size) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_RESPONSIBLE", idResponsible);
		
		try {
			@SuppressWarnings("unchecked")
			List<ResponsibleEntity> responblibleList = (List<ResponsibleEntity>) this.pesquisaArgFixos(ResponsibleEntity.class, "findAllDependentsPageable", args);
			
			if(responblibleList.size() > 0 && !responblibleList.get(0).getPedestre().isEmpty()) {
				return responblibleList.get(0).getPedestre();
			}
			
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public List<AcessoEntity> findAllAccessPageable(String login, String password, int page, int size) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("LOGIN", login);
		args.put("PASSWORD", password);
		 
		try {
			@SuppressWarnings("unchecked")
			List<AcessoEntity> dependentsAccess = (List<AcessoEntity>) this.pesquisaSimples(AcessoEntity.class, "findAllPedestresSemSaida", null);
			
			if(!dependentsAccess.isEmpty()) {
				return dependentsAccess;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}





}