package br.com.startjob.acesso.modelo.ejb;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.NewsLetterEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.ResponsibleEntity;
import br.com.startjob.acesso.modelo.entity.TokenNotificationEntity;

public interface ResponsibleEJBRemote extends BaseEJBRemote {

	Optional<ResponsibleEntity> findResponsibleByLoginAndPassword(final String login, final String password);

	List<PedestreEntity> findAllDependentsPageable(final long idResponsible, final int page, final int size);

	List<AcessoEntity> findAllAccessPageable(final Long idPedestre, final int Page, final int size);

	Optional<TokenNotificationEntity> findTokenNotification();

	void createNewsLetter(final long idResponsible, final String description, final String title, final byte[] image,
			Date eventDate);

	List<NewsLetterEntity> findNewsLetter(final long idResponsible);

	Optional<ResponsibleEntity> findResponsibleByID(final long idResponsible);
}
