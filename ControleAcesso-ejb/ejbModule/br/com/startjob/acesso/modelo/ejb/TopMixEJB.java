package br.com.startjob.acesso.modelo.ejb;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TopMixEJB extends BaseEJB implements TopMixEJBRemote{

	@Override
	public List<AcessoEntity> findAccessByCPF(String cpf, Date dataInicio, Date dataFim , int page, int size) {
		// TODO Auto-generated method stub
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("CPF", cpf);
		args.put("DATA_INICIO", dataInicio);
		args.put("DATA_FIM", dataFim);
		 
		try {
			@SuppressWarnings("unchecked")
			List<AcessoEntity> acessos = (List<AcessoEntity>) this.pesquisaArgFixosLimitado(AcessoEntity.class, "findByCpfAndDateRange", args, page, size);
			
			if(!acessos.isEmpty()) {
				return acessos;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
