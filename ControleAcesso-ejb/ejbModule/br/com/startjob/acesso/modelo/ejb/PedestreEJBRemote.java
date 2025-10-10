
package br.com.startjob.acesso.modelo.ejb;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import com.senior.services.Enum.ModoImportacaoFuncionario;

import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.ImportacaoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoArquivo;

@Remote
public interface PedestreEJBRemote extends BaseEJBRemote {
	
	/**
	 * 
	 * @param ids
	 * @param time
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> recuperaPedestresControleAcesso(List<Long> ids, Date time) throws Exception;
	
	/**
	 * 
	 * @param ids
	 * @param time
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> recuperaPedestresControleAcessoComListas(List<Long> ids, Date time, Boolean todasDigitais) throws Exception;

	/**
	 * 
	 * @param ids
	 * @param time
	 * @return
	 * @throws Exception
	 */
	public List<Object> buscaListaPedestresComAtualizacaoDeFoto(List<Long> ids, Date time) throws Exception;

	/**
	 * 
	 * @param listaIds
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> buscaFotoListaPedestre(List<Long> listaIds) throws Exception;
	
	/**
	 * 
	 * @param dados
	 * @param separador
	 * @throws IOException
	 * @throws ParseException
	 */
	public void importarArquivo(ImportacaoEntity dados, String separador) throws IOException, ParseException; 

	/**
	 * 
	 * @param marcado
	 * @param args
	 * @param idCliente
	 */
	public void marcarOuDesmarcarTodos(boolean marcado, Map<String, Object> args, Long idCliente);
	
	/**
	 * 
	 * @param regra
	 * @param idEmpresa
	 * @param idDepartamento
	 * @param idCentro
	 * @param idCargo
	 * @param idCliente
	 * @param args
	 */
	public void alterarEmMassa(PedestreRegraEntity pedestreRegra, Long idEmpresa, Long idDepartamento, 
			Long idCentro, Long idCargo, Long idCliente, Map<String, Object> args);
	
	
	/**
	 * Realiza importação do sistema SOC
	 * 
	 * @param idCliente
	 * @throws Exception
	 */
	public void importarSOC() throws Exception;
	
	/**
	 * Exporta dados para o SOC.
	 * 
	 * @param idCliente
	 * @param pasta
	 * @throws Exception
	 */
	public void exportaSOC(ClienteEntity cliente) throws Exception;
	
	public void importarSenior() throws Exception;

	public void importarTotvs() throws Exception;
	
	public void resetAutoAtendimento() throws Exception;

	public void importarAD() throws Exception;
	
	public void importaFuncionariosSenior(final String empresaExistente, final ClienteEntity cliente,
			ModoImportacaoFuncionario modo, String codFil, String data, String numCad, String tipCol);
}

