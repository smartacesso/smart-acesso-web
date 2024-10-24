package br.com.startjob.acesso.modelo.ejb;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class EmpresaEJB extends BaseEJB implements EmpresaEJBRemote {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<?> pesquisaArgFixos(Class classeEntidade, String namedQuery, Map<String, Object> arg) throws Exception {
		
		if(classeEntidade.equals(EmpresaEntity.class)) {
			List<EmpresaEntity> empresas = (List<EmpresaEntity>) super.pesquisaArgFixos(classeEntidade, namedQuery, arg);
			
			for (EmpresaEntity empresa : empresas) {
				if(empresa.getDepartamentos() != null) {
					empresa.getDepartamentos().isEmpty();
				}
				if(empresa.getCargos() != null) {
					empresa.getCargos().isEmpty();
				}
				if(empresa.getCentros() != null) {
					empresa.getCentros().isEmpty();
				}
			}
			
			return empresas;
		} else {
			return super.pesquisaArgFixos(classeEntidade, namedQuery, arg);
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public BaseEntity recuperaObjeto(Class classeEntidade, Object id) throws Exception {
		
		if(classeEntidade.equals(EmpresaEntity.class)) {
			EmpresaEntity empresa = (EmpresaEntity) super.recuperaObjeto(classeEntidade, id);
			
			if(empresa.getDepartamentos() != null) {
				empresa.getDepartamentos().isEmpty();
			}
			if(empresa.getCargos() != null) {
				empresa.getCargos().isEmpty();
			}
			if(empresa.getCentros() != null) {
				empresa.getCentros().isEmpty();
			}
			
			return empresa;
		} else {
			return super.recuperaObjeto(classeEntidade, id);
		}
		
	}
}
