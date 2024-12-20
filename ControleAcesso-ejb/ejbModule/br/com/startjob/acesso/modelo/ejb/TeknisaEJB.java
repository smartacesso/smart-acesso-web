package br.com.startjob.acesso.modelo.ejb;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import br.com.startjob.acesso.modelo.to.TeknisaTO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TeknisaEJB extends BaseEJB implements TeknisaEJBRemote {
	
	@Override
	public List<TeknisaTO> findAccessByClientId(final long client) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("CLIENTE", client);
		try {
			System.out.println("Bucando logs para sincronização teknisa: cliente = " + client);

			Query nativeQuery = getEntityManager().createNativeQuery(
					"select ac.ID_ACESSO, ac.DATA, pe.NOME, emp.CNPJ, ac.ID_CLIENTE, cl.ORGANIZACAO_TEKNISA, cl.FILIAL_TEKNISA "
							+ "from tb_acesso ac "
							+ "left join tb_cliente cl " + "on ac.ID_CLIENTE = cl.ID_CLIENTE "
							+ "left join tb_pedestre pe  " + "on ac.ID_PEDESTRE = pe.ID_PEDESTRE "
							+ "left join tb_empresa emp " + "on pe.ID_EMPRESA = emp.ID_EMPRESA "
							+ "where ac.ID_CLIENTE = :CLIENTE "
							+ "and (ac.IS_SINCRONIZADO is null or ac.IS_SINCRONIZADO = false) "
							+ "order by ac.DATA ASC "
							+ "LIMIT 100");

			nativeQuery.setParameter("CLIENTE", client);

			List<Object[]> query = nativeQuery.getResultList();
			
			List<TeknisaTO> teknisaListResponse = query.stream().map(this::convert).collect(Collectors.toList());
			
			if(Objects.isNull(teknisaListResponse) || teknisaListResponse.isEmpty()) {
				System.out.println("Sem logs encontrados para sincronização teknisa. Cliente = " + client);
				return new ArrayList<TeknisaTO>();
			}
			
			syncSendAccess(teknisaListResponse);

			return teknisaListResponse;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void syncSendAccess(List<TeknisaTO> teknisaListResponse) {
		List<Long> idsAcesso = teknisaListResponse.stream()
				.map(tekisaEvent -> tekisaEvent.getIdAcesso())
				.collect(Collectors.toList());
		
		Query nativeQuery = getEntityManager().createNativeQuery(
				"update tb_acesso "
				+ "set tb_acesso.IS_SINCRONIZADO = true "
				+ "where tb_acesso.ID_ACESSO IN (:IDS) ");
		
		nativeQuery.setParameter("IDS", idsAcesso);
		
		nativeQuery.executeUpdate();
	}

	private TeknisaTO convert(Object[] object) {
			return new TeknisaTO(
					Long.valueOf(object[0].toString()), 
					Objects.nonNull(object[1]) ? convertToDate(object[1]) : null,
					Objects.nonNull(object[2]) ? object[2].toString() : null,
					Objects.nonNull(object[3]) ? object[3].toString() : null,
					Long.valueOf(object[4].toString()),
					object[5].toString(),
					object[6].toString());
	
	}
	
	private Date convertToDate(Object value) {
	    if (value instanceof Timestamp) {
	        return new Date(((Timestamp) value).getTime());
	    } else if (value instanceof String) {
	        try {
	            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            return formatter.parse((String) value);
	        } catch (ParseException e) {
	            throw new IllegalArgumentException("Data em formato inválido: " + value, e);
	        }
	    } else {
	        throw new IllegalArgumentException("Tipo inesperado para a data: " + value.getClass().getName());
	    }
	}
}
