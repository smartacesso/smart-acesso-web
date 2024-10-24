package br.com.startjob.acesso.modelo.ejb;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Query;
import javax.xml.ws.soap.MTOMFeature;

import org.jboss.ejb3.annotation.TransactionTimeout;

import com.age.soc.services.exportaDados.ExportaDadosWs;
import com.age.soc.services.exportaDados.ExportaDadosWsService;
import com.age.soc.services.exportaDados.ExportaDadosWsVo;
import com.age.soc.services.exportaDados.parameters.FuncionarioParam;
import com.age.soc.services.exportaDados.results.EmpresaResult;
import com.age.soc.services.exportaDados.results.FuncionarioResult;
import com.age.soc.services.handles.WSSecurityHandle.HeaderHandlerResolver;
import com.age.soc.services.socGed.IdentificacaoUsuarioWsVo;
import com.age.soc.services.socGed.RegrasArquivosGed;
import com.age.soc.services.socGed.TipoClassificacaoUploadArquivoGedWs;
import com.age.soc.services.socGed.UploadArquivosWs;
import com.age.soc.services.socGed.UploadArquivosWsService;
import com.age.soc.services.socGed.UploadArquivosWsVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senior.services.IntegracaoSeniorService;
import com.senior.services.dto.EmpresaSeniorDto;
import com.senior.services.dto.FuncionarioSeniorDto;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.CargoEntity;
import br.com.startjob.acesso.modelo.entity.CentroCustoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.DepartamentoEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.EnderecoEntity;
import br.com.startjob.acesso.modelo.entity.HorarioEntity;
import br.com.startjob.acesso.modelo.entity.ImportacaoEntity;
import br.com.startjob.acesso.modelo.entity.IntegracaoSOCEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEquipamentoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.entity.base.BaseEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PedestreEJB extends BaseEJB implements PedestreEJBRemote {

	private static final ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<?> pesquisaArgFixos(Class classeEntidade, String namedQuery, Map<String, Object> arg) throws Exception {

		if (classeEntidade.equals(PedestreEntity.class)) {
			List<PedestreEntity> pedestres = (List<PedestreEntity>) super.pesquisaArgFixos(classeEntidade, namedQuery,
					arg);

			for (PedestreEntity pedestre : pedestres) {
				if (pedestre.getRegras() != null && !pedestre.getRegras().isEmpty()) {
					for (PedestreRegraEntity pe : pedestre.getRegras()) {
						if (pe.getRegra() != null)
							pe.getRegra().getId();
					}
				}

				if (pedestre.getEquipamentos() != null && !pedestre.getEquipamentos().isEmpty()) {
					for (PedestreEquipamentoEntity eq : pedestre.getEquipamentos()) {
						if (eq.getEquipamento() != null)
							eq.getEquipamento().getId();
					}
				}

				if (pedestre.getMensagensPersonalizadas() != null)
					pedestre.getMensagensPersonalizadas().isEmpty();

				if (pedestre.getDocumentos() != null)
					pedestre.getDocumentos().isEmpty();

				if (pedestre.getBiometrias() != null)
					pedestre.getBiometrias().isEmpty();

				if (pedestre.getMatricula() != null)
					pedestre.getMatricula().isEmpty();

				if (pedestre.getCargo() != null)
					pedestre.getCargo();
			}

			return pedestres;
		} else {
			return super.pesquisaArgFixos(classeEntidade, namedQuery, arg);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public BaseEntity recuperaObjeto(Class classeEntidade, Object id) throws Exception {
		if (classeEntidade.equals(PedestreEntity.class)) {
			PedestreEntity pedestre = (PedestreEntity) super.recuperaObjeto(classeEntidade, id);

			if (pedestre.getRegras() != null) {
				pedestre.getRegras().isEmpty();
				for (PedestreRegraEntity pedestreRegra : pedestre.getRegras()) {
					if (pedestreRegra.getRegra() != null) {
						pedestreRegra.getRegra().getId();
					}
				}

			}

			if (pedestre.getEquipamentos() != null) {
				pedestre.getEquipamentos().isEmpty();
				for (PedestreEquipamentoEntity eq : pedestre.getEquipamentos()) {
					if (eq.getEquipamento() != null)
						eq.getEquipamento().getId();
				}
			}

			if (pedestre.getMensagensPersonalizadas() != null)
				pedestre.getMensagensPersonalizadas().isEmpty();

			if (pedestre.getDocumentos() != null)
				pedestre.getDocumentos().isEmpty();

			if (pedestre.getBiometrias() != null)
				pedestre.getBiometrias().isEmpty();

			return pedestre;
		} else {
			return super.recuperaObjeto(classeEntidade, id);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> recuperaPedestresControleAcesso(List<Long> ids, Date time) throws Exception {

		String sgdb = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SGDB);

		if (sgdb == null || "".equals(sgdb)) {
			sgdb = "mysql";
		}

		String schema = "";
		if ("plsql".equals(sgdb)) {
			schema = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SCHEMA);
		}

		Query q = getEntityManager().createNativeQuery("select pd.ID_PEDESTRE, " + // 0
				"       pd.NOME,  " + // 1
				"       pd.CARTAO_ACESSO,  " + // 2
				"	    pd.STATUS as status, " + // 3
				"       pd.DATA_NASCIMENTO as dataNascimento, " + // 4
				"       pd.DATA_CRIACAO as dataCadastro, " + // 5
				"       pd.TIPO_PEDESTRE as tipo, " + // 6
				"	    b.TEMPLATE as template,  " + // 7
				"       rp.ID_PEDESTRE_REGRA as horario,  " + // 8
				"       rp.QTDE_CREDITOS as qtdCreditos, " + // 9
				"       rp.DIAS_VALIDADE_CREDITO as validadeCreditos, " + // 10
				"       r.TIPO_ESCALA as tipoEscala, " + // 11
				"       r.HORARIO_INICIO_TURNO, " + // 12
				"       rHorario.ID_HORARIO as idHorario, " + // 13
				"       rHorario.DIAS_SEMANA as diasPermitidos, " + // 14
				formataHora(sgdb, "rHorario.HORARIO_INI") + " as inicio, " + // 15
				formataHora(sgdb, "rHorario.HORARIO_FIM") + " as fim, " + // 16
				"       teq.IDENTIFICADOR as equipamento, " + // 17
				"       mp.ID_MENSAGEM_EQUIPAMENTO as idMensagem, " + // 18
				"       mp.MENSAGEM as mensagem, " + // 19
				"       mp.QUANTIDADE as qtdMensagens, " + // 20
				" 		rp.DATA_INICIO_PERIODO as dataInicioPeriodo, " + // 21
				"		rp.DATA_FIM_PERIODO as dataFimPeriodo, " + // 22
				"		pd.EMAIL as email, " + // 23
				"		pd.CPF as cpf, " + // 24
				"		pd.GENERO as genero, " + // 25
				"		pd.RG as rg, " + // 26
				"		pd.TELEFONE as telefone, " + // 27
				"		pd.CELULAR as celular, " + // 28
				"		pd.RESPONSAVEL as responsavel, " + // 29
				"		pd.OBSERVACOES as observacoes, " + // 30
				"		e.CEP as cep, " + // 31
				"		e.LOGRADOURO as logradouro, " + // 32
				"		e.NUMERO as numero, " + // 33
				"		e.COMPLEMENTO as complemento, " + // 34
				"		e.BAIRRO as bairro, " + // 35
				"		e.CIDADE as cidade, " + // 36
				"		e.ESTADO as estado,  " + // 37
				"		pd.MATRICULA as matricula, " + // 38
				"		pd.REMOVIDO as removido, " + // 39
				"		pd.SEMPRE_LIBERADO as sempreLiberado, " + // 40
				"		pd.HABILITAR_TECLADO as habilitarTeclado, " + // 41
				"		pd.ID_TEMP as idTemp, " + // 42
				"		pd.QR_CODE_PARA_ACESSO as qrCodeParaAcesso, " + // 43
				"		pd.CADASTRO_FACIAL_OBRIGATORIO as cadFacialObrig, " + // 44
				"		emp.ID_EMPRESA as idEmpresa, " + // 45
				"		cargo.ID_CARGO as idCargo, " + // 46
				"		cc.ID_CENTRO_CUSTO as idCentroCusto, " + // 47
				"		dp.ID_DEPARTAMENTO as idDepartamento, " + // 48
				"		pd.ENVIAR_SMS_AO_PASSAR_NA_CATRACA as enviaSMS, " + // 49
				"		pd.LUXAND_IDENTIFIER as luxandID " + // 50
				"from " + schema + "TB_PEDESTRE pd " + "		left join " + schema
				+ "TB_ENDERECO e on e.ID_ENDERECO = pd.ID_ENDERECO " + "		left join " + schema
				+ "TB_EMPRESA emp on emp.ID_EMPRESA = pd.ID_EMPRESA " + "		left join " + schema
				+ "TB_CARGO cargo on cargo.ID_CARGO = pd.ID_CARGO " + "		left join " + schema
				+ "TB_CENTRO_CUSTO cc on cc.ID_CENTRO_CUSTO = pd.ID_CENTRO_CUSTO " + "		left join " + schema
				+ "TB_DEPARTAMENTO dp on dp.ID_DEPARTAMENTO = pd.ID_DEPARTAMENTO " + "		left join " + schema
				+ "TB_BIOMETRIA b on b.ID_PEDESTRE = pd.ID_PEDESTRE "
				+ "			and b.DATA_ALTERACAO >= :LAST_SYNC  " + "		left join " + schema
				+ "RE_PEDESTRE_REGRA rp  " + "					on rp.ID_PEDESTRE = pd.ID_PEDESTRE "
				+ "					and (rp.VALIDADE IS NULL or rp.VALIDADE >= :DATA_ATUAL)   "
				+ "					and (rp.REMOVIDO IS NULL or rp.REMOVIDO = 0) " + "		left join " + schema
				+ "TB_REGRA r on r.ID_REGRA = rp.ID_REGRA " + "		left join " + schema
				+ "TB_HORARIO rHorario on rHorario.ID_REGRA = r.ID_REGRA  " + "       left join " + schema
				+ "RE_PEDESTRE_EQUIPAMENTO ep  " + "			on ep.ID_PEDESTRE = pd.ID_PEDESTRE "
				+ "           and (ep.VALIDADE IS NULL or ep.VALIDADE >= :DATA_ATUAL )  " + "		left join " + schema
				+ "TB_EQUIPAMENTO teq " + "			on ep.ID_EQUIPAMENTO = teq.ID_EQUIPAMENTO " + "		left join "
				+ schema + "TB_MENSAGEM_EQUIPAMENTO mp " + "			on mp.ID_PEDESTRE = pd.ID_PEDESTRE "
				+ "			and mp.status = 'ATIVO' "
				+ "           and (mp.VALIDADE IS NULL or mp.VALIDADE >= :DATA_ATUAL )  "
				+ "where pd.ID_CLIENTE in ( :ID_CLIENTES )  "
				+ ("mysql".equals(sgdb)
						? "group by pd.ID_PEDESTRE, r.ID_REGRA, rHorario.ID_HORARIO, b.TEMPLATE, ep.ID_EQUIPAMENTO, mp.ID_MENSAGEM_EQUIPAMENTO "
						: "")
				+ "order by pd.ID_PEDESTRE asc ");

		q.setParameter("ID_CLIENTES", ids);
		q.setParameter("LAST_SYNC", time);
		q.setParameter("DATA_ATUAL", new Date());

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> recuperaPedestresControleAcessoComListas(List<Long> ids, Date time, Boolean todasDigitais)
			throws Exception {

		String sgdb = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SGDB);
		String schema = "";
		if (sgdb == null || "".equals(sgdb)) {
			sgdb = "mysql";
		}

		if ("plsql".equals(sgdb)) {
			schema = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SCHEMA);
		}

		Query q = getEntityManager().createNativeQuery("select pd.ID_PEDESTRE, " + // 0
				"       pd.NOME,  " + // 1
				"       pd.CARTAO_ACESSO,  " + // 2
				"	    pd.STATUS as status, " + // 3
				"       pd.DATA_NASCIMENTO as dataNascimento, " + // 4
				"       pd.DATA_CRIACAO as dataCadastro, " + // 5
				"       pd.TIPO_PEDESTRE as tipo, " + // 6
				"	    b.TEMPLATE as template,  " + // 7
				"       rp.ID_PEDESTRE_REGRA as horario,  " + // 8
				"       rp.QTDE_CREDITOS as qtdCreditos, " + // 9
				"       rp.DIAS_VALIDADE_CREDITO as validadeCreditos, " + // 10
				"       r.TIPO_ESCALA as tipoEscala, " + // 11
				"       r.HORARIO_INICIO_TURNO, " + // 12
				"       rHorario.ID_HORARIO as idHorario, " + // 13
				"       rHorario.DIAS_SEMANA as diasPermitidos, " + // 14
				formataHora(sgdb, "rHorario.HORARIO_INI") + " as inicio, " + // 15
				formataHora(sgdb, "rHorario.HORARIO_FIM") + " as fim, " + // 16
				"       teq.IDENTIFICADOR as equipamento, " + // 17
				"       mp.ID_MENSAGEM_EQUIPAMENTO as idMensagem, " + // 18
				"       mp.MENSAGEM as mensagem, " + // 19
				"       mp.QUANTIDADE as qtdMensagens, " + // 20
				" 		rp.DATA_INICIO_PERIODO as dataInicioPeriodo, " + // 21
				"		rp.DATA_FIM_PERIODO as dataFimPeriodo, " + // 22
				"		pd.EMAIL as email, " + // 23
				"		pd.CPF as cpf, " + // 24
				"		pd.GENERO as genero, " + // 25
				"		pd.RG as rg, " + // 26
				"		pd.TELEFONE as telefone, " + // 27
				"		pd.CELULAR as celular, " + // 28
				"		pd.RESPONSAVEL as responsavel, " + // 29
				"		pd.OBSERVACOES as observacoes, " + // 30
				"		e.CEP as cep, " + // 31
				"		e.LOGRADOURO as logradouro, " + // 32
				"		e.NUMERO as numero, " + // 33
				"		e.COMPLEMENTO as complemento, " + // 34
				"		e.BAIRRO as bairro, " + // 35
				"		e.CIDADE as cidade, " + // 36
				"		e.ESTADO as estado,  " + // 37
				"		pd.MATRICULA as matricula, " + // 38
				"		pd.REMOVIDO as removido, " + // 39
				"		pd.SEMPRE_LIBERADO as sempreLiberado, " + // 40
				"		pd.HABILITAR_TECLADO as habilitarTeclado, " + // 41
				"		pd.ID_TEMP as idTemp, " + // 42
				"		pd.QR_CODE_PARA_ACESSO as qrCodeParaAcesso, " + // 43
				"		pd.CADASTRO_FACIAL_OBRIGATORIO as cadFacialObrig, " + // 44
				"		emp.ID_EMPRESA as idEmpresa, " + // 45
				"		cargo.ID_CARGO as idCargo, " + // 46
				"		cc.ID_CENTRO_CUSTO as idCentroCusto, " + // 47
				"		dp.ID_DEPARTAMENTO as idDepartamento, " + // 48
				"		pd.ENVIAR_SMS_AO_PASSAR_NA_CATRACA as enviaSMS, " + // 49
				"		pd.LUXAND_IDENTIFIER as luxandID, " + // 50
				"		mp.NOME as nomeMensagem, " + // 51
				"		mp.STATUS as statusMensagem, " + // 52
				"		mp.VALIDADE as validadeMensagem, " + // 53
				"		ep.ID_PEDESTRE_EQUIPAMENTO as idEquipamento, " + // 54
				"		ep.VALIDADE as validadeEquipamento, " + // 55
				"		teq.NOME as nomeEquipamento, " + // 56
				"		doc.ID_DOCUMENTO as idDoc, " + // 57
				"		doc.NOME as nomeDoc, " + // 58
				"		doc.VALIDADE as validadeDoc, " + // 59
				"		r.ID_REGRA as idRegra, " + // 60
				"		rp.VALIDADE as validadeRegra, " + // 61
				"		rp.QTDE_TOTAL_CREDITOS as qtdeTotalCreditos, " + // 62
				"		pd.QUANTIDADE_ACESSO_ANTES_SINC as qtdeAcessosAntesSinc, " + // 63
				"		usuario.ID_USUARIO as idUsuario, " + // 64
				" 		rp.BLOQUEADO AS bloqueioRegra, " + // 65
				"       pd.senha as senha, " + // 66
				"       pd.TIPO_ACESSO as tipoAcesso, " + // 67
				"       pd.TIPO_QRCODE as tipoQRCode, " + // 68
				"		pd.DATA_CADASTRO_FOTO_HIKIVISION as dataCadastroFotoHikivision, " + // 69
				"		rp.DATA_INICIO_ESCALA_3_3 as dataInicioEscala " + // 70
				"from " + schema + "TB_PEDESTRE pd " + "		left join " + schema
				+ "TB_ENDERECO e on e.ID_ENDERECO = pd.ID_ENDERECO " + "		left join " + schema
				+ "TB_EMPRESA emp on emp.ID_EMPRESA = pd.ID_EMPRESA " + "		left join " + schema
				+ "TB_CARGO cargo on cargo.ID_CARGO = pd.ID_CARGO " + "		left join " + schema
				+ "TB_CENTRO_CUSTO cc on cc.ID_CENTRO_CUSTO = pd.ID_CENTRO_CUSTO " + "		left join " + schema
				+ "TB_DEPARTAMENTO dp on dp.ID_DEPARTAMENTO = pd.ID_DEPARTAMENTO " + "		left join " + schema
				+ "TB_BIOMETRIA b on b.ID_PEDESTRE = pd.ID_PEDESTRE "
				+ (todasDigitais ? "" : " and b.DATA_ALTERACAO >= :LAST_SYNC  ") + "		left join " + schema
				+ "RE_PEDESTRE_REGRA rp  " + "					on rp.ID_PEDESTRE = pd.ID_PEDESTRE "
				+ "					and (rp.VALIDADE IS NULL or rp.VALIDADE >= :DATA_ATUAL)   "
				+ "					and (rp.REMOVIDO IS NULL or rp.REMOVIDO = 0) " + "		left join " + schema
				+ "TB_REGRA r on r.ID_REGRA = rp.ID_REGRA " + "		left join " + schema
				+ "TB_HORARIO rHorario on rHorario.ID_REGRA = r.ID_REGRA  " + "       left join " + schema
				+ "RE_PEDESTRE_EQUIPAMENTO ep  " + "			on ep.ID_PEDESTRE = pd.ID_PEDESTRE "
				+ "			and (ep.REMOVIDO IS NULL or ep.REMOVIDO = 0) "
				+ "        	and (ep.VALIDADE IS NULL or ep.VALIDADE >= :DATA_ATUAL )  " + "		left join " + schema
				+ "TB_EQUIPAMENTO teq " + "			on ep.ID_EQUIPAMENTO = teq.ID_EQUIPAMENTO " + "		left join "
				+ schema + "TB_DOCUMENTO doc " + "					on doc.ID_PEDESTRE = pd.ID_PEDESTRE "
				+ "					and (doc.REMOVIDO IS NULL or doc.REMOVIDO = 0) " + "		left join " + schema
				+ "TB_MENSAGEM_EQUIPAMENTO mp " + "			on mp.ID_PEDESTRE = pd.ID_PEDESTRE "
				+ "			and (mp.REMOVIDO IS NULL or mp.REMOVIDO = 0) " + "			and mp.status = 'ATIVO' "
				+ "           and (mp.VALIDADE IS NULL or mp.VALIDADE >= :DATA_ATUAL )  " + "		left join " + schema
				+ "TB_USUARIO usuario " + "			on usuario.ID_USUARIO = pd.ID_USUARIO "
				+ "where pd.ID_CLIENTE in ( :ID_CLIENTES )  " + "		  and pd.DATA_ALTERACAO >= :LAST_SYNC "
				+ ("mysql".equals(sgdb)
						? "group by pd.ID_PEDESTRE, r.ID_REGRA, rHorario.ID_HORARIO, b.TEMPLATE, ep.ID_EQUIPAMENTO, mp.ID_MENSAGEM_EQUIPAMENTO "
						: "")
				+ "order by pd.ID_PEDESTRE asc ");

		q.setParameter("ID_CLIENTES", ids);
		q.setParameter("LAST_SYNC", time);
		q.setParameter("DATA_ATUAL", new Date());

		return q.getResultList();
	}

	private String formataHora(String sgdb, String campo) {
		String hora = " DATE_FORMAT(" + campo + ", '%H:%i')";

		if (sgdb.equals("plsql")) {
			String sqlServerVersion = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SQL_SERVER_VESION);

			if (Integer.valueOf(sqlServerVersion) >= 2012) {
				hora = " FORMAT(" + campo + ", N'HH:mm')";
			} else {
				hora = " CONVERT(VARCHAR(5), " + campo + ", 108)";
			}

		} else if ("oracle".equals(sgdb)) {
			hora = " TO_CHAR(TO_DATE(" + campo + ", N'HH:mm'))";
		}

		return hora;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> buscaListaPedestresComAtualizacaoDeFoto(List<Long> ids, Date lastSync) throws Exception {
		String sgdb = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SGDB);
		String schema = "";

		if (sgdb == null || "".equals(sgdb)) {
			sgdb = "mysql";
		}

		if ("plsql".equals(sgdb)) {
			schema = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SCHEMA);
		}

		Query q = getEntityManager().createNativeQuery("select u.ID_PEDESTRE " + "from " + schema + "TB_PEDESTRE u "
				+ "where ((u.DATA_ALTERACAO_FOTO is not null "
				+ "				and u.DATA_ALTERACAO_FOTO > :LAST_SYNC) " + "			or "
				+ "		  (u.DATA_ALTERACAO_FOTO is null " + "				and u.FOTO is not null "
				+ "				and u.DATA_CRIACAO > :LAST_SYNC)) " + "		and u.ID_CLIENTE in ( :ID_CLIENTE ) "
				+ "order by u.ID_PEDESTRE asc ");
		q.setParameter("ID_CLIENTE", ids);
		q.setParameter("LAST_SYNC", lastSync);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> buscaFotoListaPedestre(List<Long> listaIds) throws Exception {

		String sgdb = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SGDB);
		String schema = "";
		if (sgdb == null || "".equals(sgdb)) {
			sgdb = "mysql";
		}

		if ("plsql".equals(sgdb)) {
			schema = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SCHEMA);
		}

		Query q = getEntityManager().createNativeQuery("select p.ID_PEDESTRE, p.FOTO " + "from " + schema
				+ "TB_PEDESTRE p " + "where p.ID_PEDESTRE in ( :ID_PEDESTRE ) " + "order by p.ID_PEDESTRE asc ");
		q.setParameter("ID_PEDESTRE", listaIds);
		return q.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
	public void importarArquivo(ImportacaoEntity dados, String separador) throws ParseException {
		InputStream inputStream = new ByteArrayInputStream(dados.getArquivo());

		String formatoData = "dd/MM/yyyy";
		String formatoHora = "HH:mm";

		ParametroEntity param = getParametroSistema(BaseConstant.PARAMETERS_NAME.FORMATO_DATA_PARA_IMPORTACAO,
				dados.getCliente().getId());
		if (param != null) {
			formatoData = param.getValor();

		}
		param = getParametroSistema(BaseConstant.PARAMETERS_NAME.FORMATO_HORA_PARA_IMPORTACAO,
				dados.getCliente().getId());
		if (param != null) {
			formatoHora = param.getValor();
		}

//		removepedestresSenai();
		String linha = null;
		try (BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

			int i = 1;
			while ((linha = bf.readLine()) != null) {
				String[] dadosArquivo = linha.replace("\"", "").replaceAll("[^\\p{Graph}\n\r\t ]", "").split(separador);
				int j = 1;
				// verifica se separou, se não, tenta separar com virgula
				if (dadosArquivo.length <= 1 && separador.equals(";"))
					dadosArquivo = linha.replace("\"", "").split(",");
				System.out.println("importação do cliente : " + dados.getCliente().getNome());
				System.out.println("Importando linha: " + i);

				if (dadosArquivo.length <= 1)
					continue;

				if ("centaurus".equals(dadosArquivo[0])) {
					importaCentaurus(dadosArquivo, dados);
				} else if ("CST".equals(dadosArquivo[0])) {
					importaSulacap(dadosArquivo, dados);
				} else if ("gasmig".equals(dadosArquivo[0])) {
					importagasmig(dadosArquivo, dados);
				}

//				PedestreEntity pedestreLeftZero = null;
//
////			somente para o sesi, pois o sulacap não tem matricula
//				PedestreEntity pedestre = null;
//				String matriculaConferidor = RemoverQuantidadeDeZeros(matriculaPedestre);
//				pedestreLeftZero = buscaPedestrePorNome(matriculaPedestre);
//				String matriculaBuscaConferidor = "";
//				System.out.println(" o que é pedestre pedestreLeftZero  " + pedestreLeftZero);
//				if (pedestreLeftZero != null) {
//					matriculaBuscaConferidor = RemoverQuantidadeDeZeros(pedestreLeftZero.getMatricula());
//				}
//				if (!matriculaConferidor.equals(matriculaBuscaConferidor))
//					pedestre = new PedestreEntity();
//				else {
//					pedestre = pedestreLeftZero;
//				}
				// trocar para busca pedestre por matricula

				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void importagasmig(String[] dadosArquivo, ImportacaoEntity dados) {

		String numeroCartao = "";
		String nomePedestre = "";
		String rg = "";
		String cpf = "";
		String nomeRegra = null;

		PedestreEntity pedestre = null;

		numeroCartao = dadosArquivo[1];
		nomePedestre = dadosArquivo[2];

		pedestre = buscaPedestrePorNome(nomePedestre);
		if (pedestre == null)
			pedestre = new PedestreEntity();
//        RegraEntity regra = buscarRegraPeloNome(nomeRegra, dados.getCliente().getId());
		pedestre.setCodigoCartaoAcesso(numeroCartao);
		pedestre.setNome(nomePedestre);
		pedestre.setMatricula(null);
		pedestre.setCpf(cpf);
		pedestre.setRg(rg);
		pedestre.setObservacoes("Importado : " + LocalDate.now());

		pedestre.setExistente(true);
		pedestre.setSempreLiberado(true);
		pedestre.setStatus(Status.ATIVO);
		pedestre.setTipo(TipoPedestre.PEDESTRE);
		pedestre.setCliente(dados.getCliente());

		pedestre.setExistente(true);
		pedestre.setDataRemovido(null);
		pedestre.setRemovido(null);

//        if (regra != null) {
//            PedestreRegraEntity pedestreRegra = new PedestreRegraEntity();
//            pedestreRegra.setRegra(regra);
//            pedestreRegra.setPedestre(pedestre);
//
//            if (pedestre.getRegras() == null) {            	
//            	pedestre.setRegras(new ArrayList<>());
//            }else {
//            	for(PedestreRegraEntity removeRegra: pedestre.getRegras()) {
//            		removeRegra.setRemovido(true);
//            		removeRegra.setDataRemovido(new Date());
//            	}
//            	pedestre.getRegras().add(pedestreRegra);
//            }
//            
//        }

//        if (regra != null) {
//            salvarObjeto(regra);
//        }

		if (pedestre != null) {
			salvarObjeto(pedestre);
		}

	}

	public void importaCentaurus(String[] dadosArquivo, ImportacaoEntity dados) {

		String numeroCartao = "";
		String nomePedestre = "";
		String rg = "";
		String cpf = "";
		String nomeRegra = null;

		PedestreEntity pedestre = null;

		numeroCartao = dadosArquivo[0];
		nomePedestre = dadosArquivo[1];
		cpf = dadosArquivo[2];
		rg = dadosArquivo[3];
		nomeRegra = dadosArquivo[4];

		pedestre = buscaPedestrePorNome(nomePedestre);
		if (pedestre == null)
			pedestre = new PedestreEntity();
		RegraEntity regra = buscarRegraPeloNome(nomeRegra, dados.getCliente().getId());
		pedestre.setCodigoCartaoAcesso(numeroCartao);
		pedestre.setNome(nomePedestre);
		pedestre.setMatricula(null);
		pedestre.setCpf(cpf);
		pedestre.setRg(rg);

		pedestre.setExistente(true);
		pedestre.setSempreLiberado(true);
		pedestre.setStatus(Status.ATIVO);
		pedestre.setTipo(TipoPedestre.PEDESTRE);
		pedestre.setCliente(dados.getCliente());

		pedestre.setExistente(true);
		pedestre.setDataRemovido(null);
		pedestre.setRemovido(null);

		if (regra != null) {
			PedestreRegraEntity pedestreRegra = new PedestreRegraEntity();
			pedestreRegra.setRegra(regra);
			pedestreRegra.setPedestre(pedestre);

			if (pedestre.getRegras() == null) {
				pedestre.setRegras(new ArrayList<>());
			} else {
				for (PedestreRegraEntity removeRegra : pedestre.getRegras()) {
					removeRegra.setRemovido(true);
					removeRegra.setDataRemovido(new Date());
				}
				pedestre.getRegras().add(pedestreRegra);
			}

		}

		if (regra != null) {
			salvarObjeto(regra);
		}

		if (pedestre != null) {
			salvarObjeto(pedestre);
		}

	}

	public void importaSulacap(String[] dadosArquivo, ImportacaoEntity dados) throws ParseException {

		SimpleDateFormat formatadorData = new SimpleDateFormat();
		SimpleDateFormat formatadorDataSemHora = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatadorHora = new SimpleDateFormat();

		EmpresaEntity empresa = new EmpresaEntity();
		String nomePedestre = "";
		String rg = "";
		String cpf = "";

//		DepartamentoEntity departamento = new DepartamentoEntity();
//		CentroCustoEntity centroCusto = new CentroCustoEntity();
//		CargoEntity cargo = new CargoEntity();
//		String matriculaPedestre = "";
//		Date dataNascimento = new Date();
//		String logradouro = "";
//		String numero = "";
//		String complemento = "";
//		String bairro = "";
//		String cidade = "";
//		String uf = "";
//		String cep = "";

//		String telefone = "";

		Date horaInicial = new Date();
		Date dataInicial = new Date();
//		Date horaFinal = new Date();
//		Date dataFinal = new Date();

//		String nomeResponsavel = "";
//		String numeroCartao = "";
		RegraEntity regra = null;
		HorarioEntity horario = null;
		String nomeRegra = null;
		PedestreEntity pedestre = null;

		SimpleDateFormat formatadorHoraSulacap = new SimpleDateFormat("HH:mm");

		dataInicial = formatadorDataSemHora.parse(dadosArquivo[0]);
		horaInicial = formatadorHoraSulacap.parse(dadosArquivo[1]);
		nomePedestre = dadosArquivo[2];
		rg = dadosArquivo[3];
		cpf = dadosArquivo[4];
		empresa = buscarEmpresaPeloNome(dadosArquivo[5]);
		regra = buscarRegraPeloNome("LIVRE", dados.getCliente().getId());

		pedestre = buscaPedestrePorNome(nomePedestre);
		if (pedestre == null) {
			pedestre = new PedestreEntity();
		}

		pedestre.setNome(nomePedestre);
		pedestre.setMatricula(null);
		pedestre.setRg(rg);
		pedestre.setCpf(cpf);
		pedestre.setDataNascimento(null);
		pedestre.setResponsavel(null);
		pedestre.setCodigoCartaoAcesso(null);
		pedestre.setTelefone(null);
		pedestre.setStatus(Status.ATIVO);
		pedestre.setTipo(TipoPedestre.VISITANTE);
		pedestre.setCliente(dados.getCliente());
		pedestre.setCargo(null);
		pedestre.setExistente(true);
		pedestre.setDataRemovido(null);
		pedestre.setRemovido(null);
		pedestre.setEmpresa(empresa);

		if (regra != null) {
			salvarObjeto(regra);
		}

		if (pedestre != null) {
			salvarObjeto(pedestre);
		}
	}

	public BaseEntity salvarObjeto(BaseEntity entidade) {
		try {
			Long id = null;
			Method getId = entidade.getClass().getDeclaredMethod("getId", new Class[] {});
			if (getId != null)
				id = (Long) getId.invoke(entidade, new Object[] {});

			if (id != null) {
				entidade = (BaseEntity) alteraObjeto(entidade)[0];
			} else {
				entidade = (BaseEntity) gravaObjeto(entidade)[0];
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return entidade;
	}

	@SuppressWarnings("unchecked")
	private CentroCustoEntity buscarCentroCustoPeloNome(String nomeCentroCusto) {
		CentroCustoEntity centroCusto = null;

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("NOME_CENTRO", nomeCentroCusto);

			List<CentroCustoEntity> listaCentro = (List<CentroCustoEntity>) pesquisaArgFixos(CentroCustoEntity.class,
					"findByNomeCentro", args);

			if (listaCentro != null && !listaCentro.isEmpty()) {
				centroCusto = listaCentro.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return centroCusto;
	}

	@SuppressWarnings("unchecked")
	private DepartamentoEntity buscarDepartamentoPeloNome(String nomeDepartamento) {
		DepartamentoEntity departamento = null;

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("NOME_DEPARTAMENTO", nomeDepartamento);

			List<DepartamentoEntity> listaDepartamento = (List<DepartamentoEntity>) pesquisaArgFixos(
					DepartamentoEntity.class, "findByNomeDepartamento", args);

			if (listaDepartamento != null && !listaDepartamento.isEmpty()) {
				departamento = listaDepartamento.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return departamento;
	}

	@SuppressWarnings("unchecked")
	private EmpresaEntity buscarEmpresaPeloNome(String nomeEmpresa) {
		EmpresaEntity empresa = null;

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("NOME_EMPRESA", nomeEmpresa);

			List<EmpresaEntity> listaEmpresa = (List<EmpresaEntity>) pesquisaArgFixos(EmpresaEntity.class,
					"findByNomeEmpresa", args);

			if (listaEmpresa != null && !listaEmpresa.isEmpty()) {
				empresa = listaEmpresa.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return empresa;
	}

	@SuppressWarnings("unchecked")
	private List<RegraEntity> buscarTodosRegras() {
		List<RegraEntity> regras = null;

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			List<RegraEntity> listaRegra = (List<RegraEntity>) pesquisaArgFixos(RegraEntity.class, "findAll", args);

			if (listaRegra != null && !listaRegra.isEmpty()) {
				regras = listaRegra;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return regras;
	}

	@SuppressWarnings("unchecked")
	private RegraEntity buscarRegraPeloNome(String nomeRegra, Long IdCliente) {
		RegraEntity regra = null;

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("NOME_REGRA", nomeRegra);
			args.put("ID_CLIENTE", IdCliente);

			List<RegraEntity> listaRegra = (List<RegraEntity>) pesquisaArgFixos(RegraEntity.class, "findByNome", args);

			if (listaRegra != null && !listaRegra.isEmpty()) {
				regra = listaRegra.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return regra;
	}

	private boolean isRegraPorPeriodo(Date horaInicio, Date horaFim, Date dataIni, Date dataFim) {
		if (horaInicio == null)
			return false;
		if (horaFim == null)
			return false;
		if (dataIni == null)
			return false;
		if (dataFim == null)
			return false;

		return true;
	}

	private boolean isRegraPorHorario(Date horaInicio, Date horaFim, Date dataIni, Date dataFim) {
		if (horaInicio == null)
			return false;
		if (horaFim == null)
			return false;
		if (dataIni != null && dataFim != null)
			return false;

		return true;
	}

	private HorarioEntity criaNovoHorario(Date horaInicio, Date horaFim, String nome) {
		HorarioEntity horario = new HorarioEntity();

		horario.setNome(nome);
		horario.setHorarioInicio(horaInicio);
		horario.setHorarioFim(horaFim);
		horario.setDiasSemana("1234567");
		horario.setStatus(Status.ATIVO);

		return horario;
	}

	private RegraEntity criaNovaRegra(TipoRegra tipoRegra, String nome, ClienteEntity cliente) {
		RegraEntity regra = new RegraEntity();

		regra.setNome(nome);
		regra.setTipo(tipoRegra);
		regra.setTipoPedestre(TipoPedestre.PEDESTRE);
		regra.setStatus(Status.ATIVO);
		regra.setCliente(cliente);

		return regra;
	}

	@Override
	public void marcarOuDesmarcarTodos(boolean marcado, Map<String, Object> args, Long idCliente) {
		StringBuilder query = new StringBuilder();
		String sgdb = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SGDB);
		String schema = "";
		if (sgdb == null || "".equals(sgdb)) {
			sgdb = "mysql";
		}

		if ("plsql".equals(sgdb)) {
			schema = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SCHEMA);
		}
		query.append("update " + schema + "TB_PEDESTRE ").append("set ALTERAR_EM_MASSA = ").append(marcado ? "1" : "0")
				.append(" ").append("where ID_CLIENTE = ").append(idCliente).append(" and TIPO_PEDESTRE = 'PEDESTRE' ")
				.append(" ");

		Map<String, Object> arg = new HashMap<String, Object>();
		arg.putAll(args);
		arg.remove("cliente.id");

		query.append(concatenaFiltros(arg));

		System.out.println(query);

		Query q = em.createNativeQuery(query.toString());
		q.executeUpdate();
	}

	@Override
	public void alterarEmMassa(PedestreRegraEntity pedestreRegra, Long idEmpresa, Long idDepartamento, Long idCentro,
			Long idCargo, Long idCliente, Map<String, Object> args) {

		Map<String, Object> arg = new HashMap<String, Object>();
		arg.putAll(args);
		arg.remove("cliente.id");

		String query = "";
		Query q = null;

//		altera campos na tabela pedestre se tiver empresa escolhida
		if (idEmpresa != null) {
			query = queryAlterarDadosDeEmpresa(idEmpresa, idDepartamento, idCentro, idCargo, idCliente, arg);
			q = em.createNativeQuery(query);
			q.executeUpdate();
		}

		if (pedestreRegra != null && pedestreRegra.getRegra() != null) {
//			desativa regras dos pedestres
			query = queryDesativarRegras(idCliente, arg);
			q = em.createNativeQuery(query);
			q.executeUpdate();

//			Faz a insercao de novas regras
			query = queryCriarNovasRegras(pedestreRegra, args);
			if (query != null && !"".equals(query)) {
				q = em.createNativeQuery(query);

				if (pedestreRegra.getValidade() != null)
					q.setParameter("VALIDADE", pedestreRegra.getValidade());
				if (pedestreRegra.getDataInicioPeriodo() != null)
					q.setParameter("DATA_INICIO_PERIODO", pedestreRegra.getDataInicioPeriodo());
				if (pedestreRegra.getDataFimPeriodo() != null)
					q.setParameter("DATA_FIM_PERIODO", pedestreRegra.getDataFimPeriodo());

				q.executeUpdate();
			}
		}

		query = queryMudarDataAlteracaoPedestres(idCliente);
		q = em.createNativeQuery(query);
		q.executeUpdate();

// 		apagar marcado/desmarcado no pedestre
		query = queryApagarMarcadoTodosPedestres(idCliente);
		q = em.createNativeQuery(query);
		q.executeUpdate();

	}

	public String queryAlterarDadosDeEmpresa(Long idEmpresa, Long idDepartamento, Long idCentro, Long idCargo,
			Long idCliente, Map<String, Object> arg) {

		String sgdb = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SGDB);
		String schema = "";
		if (sgdb == null || "".equals(sgdb)) {
			sgdb = "mysql";
		}

		if ("plsql".equals(sgdb)) {
			schema = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SCHEMA);
		}

		StringBuilder query = new StringBuilder();
		query.append("update " + schema + "TB_PEDESTRE set ");

		query.append("ID_EMPRESA = ").append(idEmpresa).append(" ");

		if (idDepartamento != null)
			query.append(", ID_DEPARTAMENTO = ").append(idDepartamento).append(" ");
		if (idCentro != null)
			query.append(", ID_CENTRO_CUSTO = ").append(idCentro).append(" ");
		if (idCargo != null)
			query.append(", ID_CARGO = ").append(idCargo).append(" ");

		query.append("where ID_CLIENTE = ").append(idCliente).append(" and TIPO_PEDESTRE = 'PEDESTRE' ")
				.append(" and (ALTERAR_EM_MASSA IS NULL OR ALTERAR_EM_MASSA = 1) ");

		query.append(concatenaFiltros(arg));

		return query.toString();
	}

	public String queryDesativarRegras(Long idCliente, Map<String, Object> arg) {
		StringBuilder query = new StringBuilder();

		String sgdb = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SGDB);
		String schema = "";
		if (sgdb == null || "".equals(sgdb)) {
			sgdb = "mysql";
		}

		if ("plsql".equals(sgdb)) {
			schema = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SCHEMA);
		}

		query.append("update " + schema + "RE_PEDESTRE_REGRA set DATA_REMOVIDO = curdate(), REMOVIDO = 1 ");
		query.append("where ID_PEDESTRE in ( ");
		query.append("select ID_PEDESTRE from " + schema + "TB_PEDESTRE ");
		query.append("where ID_CLIENTE = ").append(idCliente).append(" and TIPO_PEDESTRE = 'PEDESTRE' ")
				.append(" and (ALTERAR_EM_MASSA IS NULL OR ALTERAR_EM_MASSA = 1) ");

		query.append(concatenaFiltros(arg));
		query.append(")");

		return query.toString();
	}

	@SuppressWarnings("unchecked")
	public String queryCriarNovasRegras(PedestreRegraEntity pedestreRegra, Map<String, Object> args) {
		StringBuilder query = new StringBuilder();

		try {
			Long idCliente = (Long) args.get("cliente.id");

			HashMap<String, Object> argsFixos = new HashMap<String, Object>();
			argsFixos.put("ID_CLIENTE", idCliente);
			argsFixos.put("TIPO", TipoPedestre.PEDESTRE);
			List<PedestreEntity> listaPedestres = (List<PedestreEntity>) pesquisaArgFixos(PedestreEntity.class,
					"findAllParaAlterarEmMassa", argsFixos);

//			List<PedestreEntity> listaPedestres = (List<PedestreEntity>) 
//					pesquisaSimples(PedestreEntity.class, "findAll", args);

			if (listaPedestres == null || listaPedestres.isEmpty())
				return null;

			String sgdb = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SGDB);
			String schema = "";
			if (sgdb == null || "".equals(sgdb)) {
				sgdb = "mysql";
			}

			if ("plsql".equals(sgdb)) {
				schema = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SCHEMA);
			}

			query.append(
					"insert into " + schema + "RE_PEDESTRE_REGRA (VERSAO, ID_REGRA, DATA_ALTERACAO, DATA_CRIACAO ");

			if (pedestreRegra.getValidade() != null)
				query.append(", VALIDADE");
			if (pedestreRegra.getDataInicioPeriodo() != null)
				query.append(", DATA_INICIO_PERIODO");
			if (pedestreRegra.getDataFimPeriodo() != null)
				query.append(", DATA_FIM_PERIODO");
			if (pedestreRegra.getQtdeDeCreditos() != null)
				query.append(", QTDE_CREDITOS");
			if (pedestreRegra.getDiasValidadeCredito() != null)
				query.append(", DIAS_VALIDADE_CREDITO");

			query.append(", ID_PEDESTRE) values ");

			for (PedestreEntity pedestre : listaPedestres) {
				if (TipoPedestre.VISITANTE.equals(pedestre.getTipo()))
					continue;
				if (Boolean.FALSE.equals(pedestre.getAlterarEmMassa()))
					continue;

				query.append(" (0, ").append(pedestreRegra.getRegra().getId());
				query.append(", curdate(), curdate()");

				if (pedestreRegra.getValidade() != null)
					query.append(", :VALIDADE");
				if (pedestreRegra.getDataInicioPeriodo() != null)
					query.append(", :DATA_INICIO_PERIODO");
				if (pedestreRegra.getDataFimPeriodo() != null)
					query.append(", :DATA_FIM_PERIODO");
				if (pedestreRegra.getQtdeDeCreditos() != null)
					query.append(", ").append(pedestreRegra.getQtdeDeCreditos());
				if (pedestreRegra.getDiasValidadeCredito() != null)
					query.append(", ").append(pedestreRegra.getDiasValidadeCredito());

				query.append(", ").append(pedestre.getId()).append("), ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (query.toString().endsWith(", "))
			return query.substring(0, query.length() - 2);

		return query.toString();
	}

	public String queryApagarMarcadoTodosPedestres(Long idCliente) {
		StringBuilder query = new StringBuilder();
		String sgdb = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SGDB);
		String schema = "";
		if (sgdb == null || "".equals(sgdb)) {
			sgdb = "mysql";
		}

		if ("plsql".equals(sgdb)) {
			schema = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SCHEMA);
		}

		query.append("update " + schema + "TB_PEDESTRE set ALTERAR_EM_MASSA = NULL ").append("where ID_CLIENTE = ")
				.append(idCliente);

		return query.toString();
	}

	private String queryMudarDataAlteracaoPedestres(Long idCliente) {
		StringBuilder query = new StringBuilder();
		String sgdb = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SGDB);
		String schema = "";
		if (sgdb == null || "".equals(sgdb)) {
			sgdb = "mysql";
		}

		if ("plsql".equals(sgdb)) {
			schema = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SCHEMA);
		}

		query.append("update " + schema + "TB_PEDESTRE set DATA_ALTERACAO = current_timestamp() ")
				.append("where ID_CLIENTE = ").append(idCliente).append(" and TIPO_PEDESTRE = 'PEDESTRE' ")
				.append(" and (ALTERAR_EM_MASSA IS NULL OR ALTERAR_EM_MASSA = 1) ");

		return query.toString();
	}

	private String concatenaFiltros(Map<String, Object> args) {
		StringBuilder sb = new StringBuilder();

		if (args == null || args.isEmpty())
			return sb.toString();

		for (String key : args.keySet()) {
			if (args.get(key) != null) {
				if (args.get(key) instanceof String)
					sb.append(
							" and " + getNomeColunaBanco(key.replace(".id", "")) + " like '%" + args.get(key) + "%' ");
				else
					sb.append(" and " + getNomeColunaBanco(key.replace(".id", "")) + " = " + args.get(key) + " ");
			}
		}

		return sb.toString();
	}

	private String getNomeColunaBanco(String key) {
		PedestreEntity pedestre = new PedestreEntity();

		for (Field field : pedestre.getClass().getDeclaredFields()) {

			if (field.getName().equals(key)) {
				Annotation[] annotations = field.getDeclaredAnnotations();

				for (Annotation annotation : annotations) {
					if (annotation instanceof Column) {
						Column myAnnotation = (Column) annotation;
						return myAnnotation.name();
					} else if (annotation instanceof JoinColumn) {
						JoinColumn myAnnotation = (JoinColumn) annotation;
						return myAnnotation.name();
					}
				}
			}
		}
		return "";
	}

	private static String createJSON(Object to) throws JsonProcessingException {
		return mapper.writeValueAsString(to);
	}

	private static Object createFuncionario(String json) throws JsonProcessingException {
		try {
			return mapper.readValue(json, new TypeReference<List<FuncionarioResult>>() {
			});

		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static Object createObject(String json) throws JsonProcessingException {
		List<FuncionarioResult> result = null;
		TypeReference<FuncionarioResult> funcionario = new TypeReference<FuncionarioResult>() {
		};
		// json = json.equals("[]") ? "" : json;
		/*
		 * 
		 * try { //result = mapper.readValue(json, new
		 * TypeReference<List<FuncionarioResult>>() {});
		 * 
		 * @SuppressWarnings("unchecked") List<FuncionarioResult> readValue =
		 * (List<FuncionarioResult>) mapper.readValue(json, new
		 * TypeReference<List<FuncionarioResult>>() {}); result = readValue; } catch
		 * (JsonMappingException e) { e.printStackTrace(); }
		 * 
		 */
		try {
			result = mapper.readValue(json, new TypeReference<List<FuncionarioResult>>() {
			});
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
	public void importarSenior() throws Exception {
		final List<ClienteEntity> clientes = (List<ClienteEntity>) pesquisaSimples(ClienteEntity.class,
				"findAllComIntegracaoSenior", new HashMap<>());

		if (Objects.isNull(clientes) || clientes.isEmpty()) {
			System.out.println("Não existem clientes com integração Senior");
			return;
		}

		clientes.forEach(cliente -> {
			importarEmpresasSenior(cliente);
		});
	}
	
	private void importarEmpresasSenior(final ClienteEntity cliente) {
		List<EmpresaSeniorDto> empresasSenior = buscaTodasEmpresasSenior(cliente);
		if (Objects.isNull(empresasSenior) || empresasSenior.isEmpty()) {
			return;
		}

		empresasSenior.forEach(empresaSenior -> {
			Optional<EmpresaEntity> empresaExistenteOpt = buscaEmpresaExistente(empresaSenior.getNumEmp(),
					cliente.getId());

			EmpresaEntity empresaExistente = null;

			if (empresaExistenteOpt.isPresent()) {
				empresaExistente = empresaExistenteOpt.get();

			} else {
				empresaExistente = new EmpresaEntity(empresaSenior, false, cliente);
				try {
					empresaExistente = (EmpresaEntity) gravaObjeto(empresaExistente)[0];
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			importaFuncionariosSenior(empresaExistente, cliente);

			if (Objects.isNull(empresaExistente.getPrimeiroImportacaoFuncionarioSeniorSucesso())
					|| Boolean.FALSE.equals(empresaExistente.getPrimeiroImportacaoFuncionarioSeniorSucesso())) {
				empresaExistente.setPrimeiroImportacaoFuncionarioSeniorSucesso(true);
				try {
					empresaExistente = (EmpresaEntity) gravaObjeto(empresaExistente)[0];
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void importaFuncionariosSenior(final EmpresaEntity empresaExistente, final ClienteEntity cliente) {
		List<FuncionarioSeniorDto> funcionarios = null;

		if (Boolean.TRUE.equals(empresaExistente.getPrimeiroImportacaoFuncionarioSeniorSucesso())) {
			funcionarios = buscaFuncionariosAtualizadosNoDia(empresaExistente.getCodEmpresaSenior(), cliente);
		} else {
			funcionarios = buscaTodosOsFuncioriosDaEmpresa(empresaExistente.getCodEmpresaSenior(), cliente);
		}

		if (Objects.isNull(funcionarios) || funcionarios.isEmpty()) {
			return;
		}

		funcionarios.forEach(funcionario -> {
			Optional<PedestreEntity> pedestreExistente = buscaPedestreExistente(
					funcionario.getNumeroMatricula(), empresaExistente);

			PedestreEntity pedestre = null;
			
			if (pedestreExistente.isPresent()) {
				pedestre = pedestreExistente.get();
				pedestre.updateFuncionarioSenior(funcionario, empresaExistente);

			} else {
				pedestre = new PedestreEntity(funcionario, empresaExistente);
			}
			
			try {
				pedestre = (PedestreEntity) gravaObjeto(pedestre)[0];
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		});
	}
	
	private List<FuncionarioSeniorDto> buscaTodosOsFuncioriosDaEmpresa(final String numEmp, final ClienteEntity cliente) {
		System.out.println("buscando todos funcionarios da empresa :" + numEmp );
		IntegracaoSeniorService integracaoSeniorService = new IntegracaoSeniorService(
				cliente.getIntegracaoSenior().getUsuario(), cliente.getIntegracaoSenior().getSenha());

		return integracaoSeniorService.buscarFuncionarios(numEmp);
	}

	private List<FuncionarioSeniorDto> buscaFuncionariosAtualizadosNoDia(String numEmp, final ClienteEntity cliente) {
		System.out.println("buscando todos funcionarios atualizados do dia");
		IntegracaoSeniorService integracaoSeniorService = new IntegracaoSeniorService(
				cliente.getIntegracaoSenior().getUsuario(), cliente.getIntegracaoSenior().getSenha());

		return integracaoSeniorService.buscarFuncionariosAtualizadosNoDia(numEmp);
	}

	@SuppressWarnings("unchecked")
	private Optional<PedestreEntity> buscaPedestreExistente(String numeroMatricula, EmpresaEntity empresa) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("MATRICULA", numeroMatricula);
		args.put("ID_EMPRESA", empresa.getId());
		args.put("ID_CLIENTE", empresa.getCliente().getId());

		try {
			List<PedestreEntity> listaPedestres = (List<PedestreEntity>) pesquisaArgFixos(PedestreEntity.class,
					"findByMatriculaAndIdEmpresaAndIdCliente", args);
			
			if (listaPedestres != null && !listaPedestres.isEmpty()) {
				return Optional.of(listaPedestres.get(0));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	private Optional<EmpresaEntity> buscaEmpresaExistente(String numEmp, Long idCliente) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("COD_EMPRESA_SENIOR", numEmp);
		args.put("ID_CLIENTE", idCliente);

		try {
			List<EmpresaEntity> listaEmpresas = (List<EmpresaEntity>) pesquisaArgFixos(EmpresaEntity.class,
					"findByCodSeniorAndIdCliente", args);
			
			if (listaEmpresas != null && !listaEmpresas.isEmpty()) {
				return Optional.of(listaEmpresas.get(0));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}

	private List<EmpresaSeniorDto> buscaTodasEmpresasSenior(final ClienteEntity cliente) {
		// IntegracaoSeniorService integracaoSeniorService = new IntegracaoSeniorService("smartwsintegra", "Sm4rt@s3n10r#");
		IntegracaoSeniorService integracaoSeniorService = new IntegracaoSeniorService(
				cliente.getIntegracaoSenior().getUsuario(), cliente.getIntegracaoSenior().getSenha());

		return integracaoSeniorService.buscarEmpresas();
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
	public void importarSOC() throws Exception {
		List<ClienteEntity> clientes = (List<ClienteEntity>) pesquisaSimples(ClienteEntity.class,
				"findAllComIntegracaoSOC", new HashMap<>());
		List<ParametroEntity> configs = (List<ParametroEntity>) pesquisaSimples(ParametroEntity.class,
				"findClienteIdPreencherCartao", new HashMap<>());

		if (clientes == null || clientes.isEmpty()) {
			System.out.println("Não existem clientes com integração SOC");
			return;
		}

		List<EmpresaResult> empresasSoc = getEmpresasSoc();

		if (empresasSoc == null || empresasSoc.isEmpty()) {
			System.out.println("Nenhuma empresa SOC foi encontrada");
			return;
		}

		System.out.println("Empresas SOC encontradas: " + empresasSoc.size());

		for (EmpresaResult empresa : empresasSoc) {

			List<FuncionarioResult> funcionarios = getFuncionariosEmpresaSoc(empresa.CODIGO);

			if (funcionarios == null || funcionarios.isEmpty()) {
				continue;
			}

			for (FuncionarioResult funcionario : funcionarios) {

				ClienteEntity clienteFromFuncionario = getClienteFromFuncionario(funcionario.CCUSTO, clientes);

				if (clienteFromFuncionario != null) {
					System.out.println(String.format("Gravando funcionario: %s, %s, %s", funcionario.NOME,
							funcionario.NOMEEMPRESA, funcionario.CCUSTO));
					salvarFuncionario(funcionario, clienteFromFuncionario, configs);
					System.out.println("salvou no cliente : " + clienteFromFuncionario.getNomeUnidadeOrganizacional()
							+ "iD :" + clienteFromFuncionario.getId());
				}
			}

		}
	}

	private static Object createEmpresa(String json) throws JsonProcessingException {
		try {
			return mapper.readValue(json, new TypeReference<List<EmpresaResult>>() {
			});

		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	// criar outro importaSOC, com os mesmos parametros, retornando empresa

	@SuppressWarnings("unchecked")
	private List<EmpresaResult> getEmpresasSoc() throws Exception {
		ExportaDadosWsService service1 = new ExportaDadosWsService();
		ExportaDadosWs port1 = service1.getExportaDadosWsPort();

		ExportaDadosWsVo parans = new ExportaDadosWsVo();

		try {
			parans.setParametros(createJSON(new FuncionarioParam("312493", "162292", "fbb9fe4c00b583f37bee")));
			ExportaDadosWsVo dados = port1.exportaDadosWs(parans);

			if (!dados.isErro()) {
				return (List<EmpresaResult>) createEmpresa(dados.getRetorno());
			}

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private List<FuncionarioResult> getFuncionariosEmpresaSoc(final String codigoEmpresa) {
		try {
			ExportaDadosWsService service1 = new ExportaDadosWsService();
			ExportaDadosWs port1 = service1.getExportaDadosWsPort();

			ExportaDadosWsVo parans = new ExportaDadosWsVo();

			parans.setParametros(createJSON(new FuncionarioParam(codigoEmpresa, "158145", "1529604199704dc79b48")));

			ExportaDadosWsVo dados = port1.exportaDadosWs(parans);

			if (!dados.isErro()) {
				return (List<FuncionarioResult>) createFuncionario(dados.getRetorno());

			} else {
				System.out.println("Erro na chamada do WebService SOC: " + dados.getMensagemErro());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unused")
	private void salvarFuncionario(FuncionarioResult funcionario, ClienteEntity cliente,
			List<ParametroEntity> configs) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (funcionario.CPF == null || "".equals(funcionario.CPF)) {
			return;
		}

		boolean facialHabilitado = false;
//	    boolean cartaoNuloOuVazio = false;

		for (ParametroEntity paramtro : configs) {
			if (paramtro.getCliente().getId() == cliente.getId()) {
				facialHabilitado = true;
				break; // Saia do loop, já que encontramos o ID
			}
		}

		PedestreEntity pedestre = recuperaPedestrePorCPF(funcionario.CPF, cliente.getId());

		if (pedestre == null) {
			pedestre = new PedestreEntity();
			pedestre.setTipo(TipoPedestre.PEDESTRE);
			pedestre.setNome(funcionario.NOME);
			pedestre.setCliente(cliente);
			pedestre.setCpf(funcionario.CPF);

			if (facialHabilitado == true) {
				pedestre.setCodigoCartaoAcesso(removeNaoNumeros(funcionario.CPF));
			}

			try {
				pedestre.setDataNascimento(sdf.parse(funcionario.DATA_NASCIMENTO));
			} catch (ParseException e) {
			}

			pedestre.setObservacoes("Admissão em: " + funcionario.DATA_ADMISSAO);
			pedestre.setCodigoExterno(funcionario.CODIGO);

			pedestre.setEndereco(criaEndereco(funcionario));
			pedestre.setEmpresa(recuperaEmpresa(funcionario.NOMEEMPRESA, funcionario.CODIGOEMPRESA, cliente));
			pedestre.setDepartamento(recuperaDepartamento(funcionario.NOMESETOR, pedestre.getEmpresa()));
			pedestre.setCargo(recuperaCargo(funcionario.NOMECARGO, pedestre.getEmpresa()));
			// pedestre.setCentroCusto(null)
			pedestre.setSempreLiberado(Boolean.TRUE);

		} else {
			// se pedestre existe mas não tem
			/*
			 * if(pedestre.getCodigoCartaoAcesso() == null ||
			 * pedestre.getCodigoCartaoAcesso().equals("") ||
			 * pedestre.getCodigoCartaoAcesso().isEmpty()) { cartaoNuloOuVazio = true; }
			 */
			if (pedestre.getCodigoExterno() == null || "".equals(pedestre.getCodigoExterno())) {
				pedestre.setCodigoExterno(funcionario.CODIGO);
			}

			if (facialHabilitado) {
				pedestre.setCodigoCartaoAcesso(removeNaoNumeros(funcionario.CPF));
			}

		}

		// verifica situação
		boolean inativo = false;
		if (!funcionario.SITUACAO.equalsIgnoreCase("Ativo")) {
			if (Status.ATIVO.equals(pedestre.getStatus())) {
				pedestre.setStatus(Status.INATIVO);
				pedestre.setObservacoes("Situação indicada: " + funcionario.SITUACAO);
			}

			inativo = true;
		}

		// verifica demissão
		if (funcionario.DATA_DEMISSAO != null && !"".equals(funcionario.DATA_DEMISSAO)) {
			if (pedestre.getObservacoes() == null || !pedestre.getObservacoes().contains("Demitido")) {
				pedestre.setStatus(Status.INATIVO);
				pedestre.setObservacoes("Demitido em: " + funcionario.DATA_DEMISSAO);
			}
			inativo = true;
		}

		// indica que está ativo
		if (!inativo) {
			pedestre.setStatus(Status.ATIVO);
		}

		// grava e atualiza dados
		try {
			pedestre = (PedestreEntity) gravaObjeto(pedestre)[0];

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String removeNaoNumeros(String matriculaFuncionario) {
		String numeroCartao = "";

		if (matriculaFuncionario.isEmpty() || matriculaFuncionario == null) {
			return numeroCartao;
		}

		numeroCartao = matriculaFuncionario.replaceAll("[^0-9]", "");

		if (numeroCartao.length() > 8) {
			numeroCartao = numeroCartao.substring(0, 8);
		}
		return numeroCartao;
	}

	private ClienteEntity getClienteFromFuncionario(String centroDeCusto, List<ClienteEntity> clientes) {
		/* String CentroDeCustoCorrigido = centroDeCusto.replaceAll("\\t$", ""); */
		String CentroDeCustoCorrigido = centroDeCusto.replaceAll("\\t+$", "").replaceAll("^0+", "");

		for (ClienteEntity cliente : clientes) {
			if (CentroDeCustoCorrigido.equals(cliente.getIntegracaoSoc().getEmpresa())) {
				return cliente;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private EmpresaEntity recuperaEmpresa(String nomeEmpresa, String codigoExterno, ClienteEntity cliente) {
		if (nomeEmpresa == null || "".equals(nomeEmpresa)) {
			return null;
		}

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("nome_equals", nomeEmpresa);
			args.put("cliente.id", cliente.getId());

			List<EmpresaEntity> listaEmpresa = (List<EmpresaEntity>) pesquisaSimplesLimitado(EmpresaEntity.class,
					"findAll", args, 0, 1);

			EmpresaEntity empresa = null;
			if (listaEmpresa != null && !listaEmpresa.isEmpty()) {
				empresa = listaEmpresa.get(0);
			}

			if (empresa == null) {
				// cria empresa
				empresa = new EmpresaEntity();
				empresa.setNome(nomeEmpresa);
				empresa.setCliente(cliente);
				empresa.setStatus(Status.ATIVO);
				empresa.setCodigoExterno(codigoExterno);

				empresa = (EmpresaEntity) gravaObjeto(empresa)[0];
				empresa = (EmpresaEntity) recuperaObjeto(EmpresaEntity.class, empresa.getId());
			}

			return empresa;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private CargoEntity recuperaCargo(String nomeCargo, EmpresaEntity empresa) {

		if (nomeCargo == null || "".equals(nomeCargo)) {
			return null;
		}

		if (empresa == null) {
			return null;
		}

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("nome_equals", nomeCargo);
			args.put("empresa.id", empresa.getId());

			List<CargoEntity> lista = (List<CargoEntity>) pesquisaSimplesLimitado(CargoEntity.class, "findAll", args, 0,
					1);

			CargoEntity cargo = null;
			if (lista != null && !lista.isEmpty()) {
				cargo = lista.get(0);
			}

			if (cargo == null) {
				// cria empresa
				cargo = new CargoEntity();
				cargo.setNome(nomeCargo);
				cargo.setEmpresa(empresa);
				cargo.setStatus(Status.ATIVO);

				cargo = (CargoEntity) gravaObjeto(cargo)[0];
				cargo = (CargoEntity) recuperaObjeto(CargoEntity.class, cargo.getId());

			}

			return cargo;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private DepartamentoEntity recuperaDepartamento(String nomeSetor, EmpresaEntity empresa) {

		if (nomeSetor == null || "".equals(nomeSetor)) {
			return null;
		}

		if (empresa == null) {
			return null;
		}

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("nome_equals", nomeSetor);
			args.put("empresa.id", empresa.getId());

			List<DepartamentoEntity> lista = (List<DepartamentoEntity>) pesquisaSimplesLimitado(
					DepartamentoEntity.class, "findAll", args, 0, 1);

			DepartamentoEntity departamento = null;
			if (lista != null && !lista.isEmpty()) {
				departamento = lista.get(0);
			}

			if (departamento == null) {
				// cria empresa
				departamento = new DepartamentoEntity();
				departamento.setNome(nomeSetor);
				departamento.setEmpresa(empresa);
				departamento.setStatus(Status.ATIVO);

				departamento = (DepartamentoEntity) gravaObjeto(departamento)[0];
				departamento = (DepartamentoEntity) recuperaObjeto(DepartamentoEntity.class, departamento.getId());

			}

			return departamento;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private EnderecoEntity criaEndereco(FuncionarioResult funcionario) {

		EnderecoEntity endereco = new EnderecoEntity();
		endereco.setLogradouro(funcionario.ENDERECO);
		endereco.setNumero(funcionario.NUMERO_ENDERECO);
		endereco.setBairro(funcionario.BAIRRO);
		endereco.setEstado(funcionario.UF);

		return endereco;
	}

	@SuppressWarnings("unchecked")
	private PedestreEntity recuperaPedestrePorCPF(String cpf, Long idCliente) {
		if (cpf == null || "".equals(cpf)) {
			return null;
		}

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("cpf_equals", cpf);
			args.put("cliente.id", idCliente);

			List<PedestreEntity> pedestres = (List<PedestreEntity>) pesquisaSimplesLimitado(PedestreEntity.class,
					"findAll", args, 0, 1);

			if (pedestres != null && !pedestres.isEmpty()) {
				return pedestres.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
	public void exportaSOC(ClienteEntity cliente) throws Exception {
		System.out.println("Iniciando rotina de exportação: idClient = " + cliente.getId());

		ImportacaoEntity log = new ImportacaoEntity();
		log.setData(new Date());
		log.setTipoImportacao("SOCGED");
		log.setCliente(cliente);

		// executa pesquisa para relatório em CSV
		try {
			List<AcessoEntity> acessos = recuperaAcessosDoDia(cliente);
			if (acessos != null && !acessos.isEmpty()) {
				IntegracaoSOCEntity integracaoSOC = cliente.getIntegracaoSoc();
				String nomeArquivo = "RelatorioAcesso_Obra_" + integracaoSOC.getCodigoExterno() + ".csv";
				byte[] arquivo = criaCSVRelatorioPedestres(acessos);

				UploadArquivosWsVo up = new UploadArquivosWsVo();
				// informações gerais
				up.setCodigoEmpresa(integracaoSOC.getCodigoEmpresa());
				up.setCodigoSequencialFicha(integracaoSOC.getCodigoSequencialFicha());
				up.setCodigoGed(integracaoSOC.getCodigoGed());
				up.setCodigoFuncionario(integracaoSOC.getCodigoFuncionario());

				// informaçõe sdo arquivo
				up.setArquivo(arquivo);
				up.setNomeArquivo(nomeArquivo);
				up.setExtensaoArquivo(RegrasArquivosGed.CSV);

				IdentificacaoUsuarioWsVo identificacaoUsuarioWsVo = new IdentificacaoUsuarioWsVo();
				identificacaoUsuarioWsVo.setChaveAcesso(integracaoSOC.getChaveAcesso());
				identificacaoUsuarioWsVo.setCodigoEmpresaPrincipal("312493");
				identificacaoUsuarioWsVo.setCodigoResponsavel(integracaoSOC.getCodigoResponsavel());
				identificacaoUsuarioWsVo.setHomologacao(false);
				identificacaoUsuarioWsVo.setCodigoUsuario(integracaoSOC.getCodigoUsuario());

				up.setIdentificacaoVo(identificacaoUsuarioWsVo);
				// Informações do GED
				up.setClassificacao(TipoClassificacaoUploadArquivoGedWs.GED);
				up.setCodigoEmpresa(integracaoSOC.getEmpresa());
				up.setNomeGed(nomeArquivo.replace(".csv", ""));
				up.setCodigoTipoGed("40");
				up.setNomeTipoGed("PASTA 09 - LISTA PORTARIA");
				up.setCodigoUnidadeGed(cliente.getIntegracaoSoc().getCodigoExterno());

				UploadArquivosWsService service1 = new UploadArquivosWsService();
				HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver();
				service1.setHandlerResolver(handlerResolver);

				UploadArquivosWs port1 = service1.getUploadArquivosWsPort(new MTOMFeature());

				Boolean retorno = port1.uploadArquivo(up);

				if (!retorno) {
					log.setObservacao("SOC retornou erro ao receber o arquivo, verificar no mesmo.");

				} else {
					log.setObservacao("Arquivo enviado com sucesso!");
				}

				// arquivo enviado
				log.setArquivo(arquivo);
				log.setNomeArquivo(nomeArquivo);
			} else {
				log.setObservacao("Nenhum log de acesso para o dia");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.setObservacao("Erro inesperado ao gerar log de acesso diário: ");
		}

		if (log.getArquivo() == null) {
			log.setArquivo(new byte[1]);
		}

		// finaliza exportação
		log.setDataFim(new Date());
		gravaObjeto(log);
	}

	@SuppressWarnings("unchecked")
	private List<AcessoEntity> recuperaAcessosDoDia(ClienteEntity cliente) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();

		LocalDateTime lastDayAcessIni = LocalDate.now().minusDays(1).atStartOfDay();
		LocalDateTime lastDayAcessFim = LocalDate.now().minusDays(1).atTime(23, 59, 59);

		// args.put("data_maior_data",
		// DateUtils.getInstance().ajustaDataIni(currentDate).getTime());
		// args.put("data_menor_data",
		// DateUtils.getInstance().ajustaDataFim(currentDate).getTime());

		args.put("data_maior_data", Date.from(lastDayAcessIni.atZone(ZoneId.systemDefault()).toInstant()));
		args.put("data_menor_data", Date.from(lastDayAcessFim.atZone(ZoneId.systemDefault()).toInstant()));

		args.put("cliente.id", cliente.getId());
		args.put("pedestre.tipo", TipoPedestre.PEDESTRE);

		List<AcessoEntity> acessos = (List<AcessoEntity>) pesquisaSimples(AcessoEntity.class,
				"findAllComPedestreEmpresaECargo", args);

		if (acessos != null && !acessos.isEmpty()) {
			return acessos;
		}

		return null;
	}

	private byte[] criaCSVRelatorioPedestres(List<AcessoEntity> acessos) {
		SimpleDateFormat sdfNascimento = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfAcesso = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		// cabeçalho
		String csv = "MATRICULA;" + "NUMERO CARTAO ACESSO;" + "NOME;" + "EMPRESA;" + "CARGO;" + "DATA NASCIMENTO;"
				+ "DATA ACESSO;" + "EQUIPAMENTO;" + "TIPO ACESSO;" + "SENTIDO\n";

		// colunas
		for (AcessoEntity acesso : acessos) {
			csv += (acesso.getPedestre().getMatricula() == null ? "" : acesso.getPedestre().getMatricula()) + ";";
			csv += (acesso.getPedestre().getCodigoCartaoAcesso() == null ? ""
					: acesso.getPedestre().getCodigoCartaoAcesso()) + ";";
			csv += (acesso.getPedestre().getNome() == null ? "" : acesso.getPedestre().getNome()) + ";";
			csv += (acesso.getPedestre().getEmpresa() == null ? "" : acesso.getPedestre().getEmpresa().getNome()) + ";";
			csv += (acesso.getPedestre().getCargo() == null ? "" : acesso.getPedestre().getCargo().getNome()) + ";";
			csv += (acesso.getPedestre().getDataNascimento() == null ? ""
					: sdfNascimento.format(acesso.getPedestre().getDataNascimento())) + ";";
			csv += (acesso.getData() == null ? "" : sdfAcesso.format(acesso.getData().getTime())) + ";";

			csv += (acesso.getEquipamento() == null ? "" : acesso.getEquipamento()) + ";";
			csv += (acesso.getTipo() == null ? ""
					: (acesso.getTipo().equals("ATIVO") ? "LIBERADO" : "BLOQUEADO")
							+ (acesso.getRazao() == null && !"".equals(acesso.getRazao()) ? ""
									: " - " + acesso.getRazao()))
					+ ";";
			csv += (acesso.getSentido() == null ? "" : acesso.getSentido()) + ";";
			csv += "\n";
		}

		return csv.getBytes();
	}

	@SuppressWarnings("unchecked")
	private PedestreEntity buscaPedestrePorNome(String nome) {
		PedestreEntity atualizaPedestre = null;

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("NOME", nome);

			List<PedestreEntity> listaPedestre = (List<PedestreEntity>) pesquisaArgFixos(PedestreEntity.class,
					"findByNomePedestre", args);

			if (listaPedestre != null && !listaPedestre.isEmpty()) {
				atualizaPedestre = listaPedestre.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return atualizaPedestre;
	}

}
