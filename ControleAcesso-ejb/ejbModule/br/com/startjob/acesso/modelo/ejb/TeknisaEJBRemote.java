package br.com.startjob.acesso.modelo.ejb;

import java.util.List;

import javax.ejb.Remote;

import br.com.startjob.acesso.modelo.to.TeknisaTO;

@Remote
public interface TeknisaEJBRemote extends BaseEJBRemote {

	List<TeknisaTO> findAccessByClientId(final long client);

}
