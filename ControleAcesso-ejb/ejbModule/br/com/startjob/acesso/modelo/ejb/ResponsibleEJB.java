package br.com.startjob.acesso.modelo.ejb;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.NewsLetterEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.ResponsibleEntity;
import br.com.startjob.acesso.modelo.entity.TokenNotificationEntity;
import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

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
			List<ResponsibleEntity> responblibleList = (List<ResponsibleEntity>) this
					.pesquisaArgFixos(ResponsibleEntity.class, "findByLoginAndPassword", args);

			if (Objects.isNull(responblibleList) || responblibleList.isEmpty() || responblibleList.size() > 1) {
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
		args.put("ID", idResponsible);

		try {
			@SuppressWarnings("unchecked")
			List<ResponsibleEntity> responblibleList = (List<ResponsibleEntity>) this
					.pesquisaArgFixos(ResponsibleEntity.class, "findByIdComplete", args);

			if (responblibleList.size() > 0)
				return responblibleList.get(0).getPedestre();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<AcessoEntity> findAllAccessPageable(Long idPedestre, int page, int size) { 
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_PEDESTRE", idPedestre);

		try {
			@SuppressWarnings("unchecked")
			List<AcessoEntity> dependentsAccess = (List<AcessoEntity>) this.pesquisaArgFixos(AcessoEntity.class,
					"findAllByIdPedestreSemData", args);
			
//			page = Math.max(page, 1); // Garante que o mínimo seja 1
//			int ini = (page - 1) * size;
//			
//			@SuppressWarnings("unchecked")
//			List<AcessoEntity> dependentsAccess = (List<AcessoEntity>) this.pesquisaArgFixosLimitado(
//			    AcessoEntity.class, "findAllByIdPedestre", args, ini, size);


			if (!dependentsAccess.isEmpty()) {
				System.out.println("Acessos do dependente : " + idPedestre + " encontrado");
				return dependentsAccess;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Optional<TokenNotificationEntity> findTokenNotification() {
		// TODO Auto-generated method stub
		try {
			@SuppressWarnings("unchecked")
			List<BaseEntity> tokenNotification = (List<BaseEntity>) this.pesquisaSimples(TokenNotificationEntity.class,
					"findAll");

			if (tokenNotification.size() > 0) {
				TokenNotificationEntity token = (TokenNotificationEntity) tokenNotification.get(0);
				return Optional.of(token);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public void createNewsLetter(long idResponsible, String description, String title, byte[] image, Date eventDate) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_RESPONSIBLE", idResponsible);

		try {
			@SuppressWarnings("unchecked")
			List<ResponsibleEntity> responblibleList = (List<ResponsibleEntity>) this
					.pesquisaArgFixos(ResponsibleEntity.class, "findByIDResposible", args);

			if (responblibleList.size() > 0) {

				ResponsibleEntity responsible = responblibleList.get(0);
				NewsLetterEntity newsLetter = new NewsLetterEntity();
				newsLetter.setDescricao(description);
				newsLetter.setImage(image);
				newsLetter.setTitle(title);
				newsLetter.setCliente(responsible.getCliente());
				newsLetter.setEventDate(eventDate);
				newsLetter.setResponsavel(responsible);
				gravaObjeto(newsLetter);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<NewsLetterEntity> findNewsLetter(long idResponsible) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_RESPONSIBLE", idResponsible);

		try {
			@SuppressWarnings("unchecked")
			List<NewsLetterEntity> responblibleList = (List<NewsLetterEntity>) this
					.pesquisaArgFixos(NewsLetterEntity.class, "findByIDResposible", args);

			if (responblibleList.size() > 0) {

				return responblibleList;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Optional<ResponsibleEntity> findResponsibleByID(long idResponsible) {
		// TODO Auto-generated method stub
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID", idResponsible);
		try {
			@SuppressWarnings("unchecked")
			List<ResponsibleEntity> responblibleList = (List<ResponsibleEntity>) this
					.pesquisaArgFixos(ResponsibleEntity.class, "findById", args);

			if (Objects.nonNull(responblibleList) && responblibleList.size() > 0) {
				return Optional.of(responblibleList.get(0));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Optional.empty();
	}

}