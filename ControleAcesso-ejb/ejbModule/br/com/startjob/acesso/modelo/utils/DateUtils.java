package br.com.startjob.acesso.modelo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.startjob.acesso.modelo.BaseConstant;

/**
 *  Classe Util para Datas 
 * 
 * @author: Gustavo Diniz
 * @since 04/03/2013
 */
public final class DateUtils {

	/**
	 * Constante com o valor 0.
	 */
	private static final int ZERO = 0;
	/**
	 * Constante com o valor 1.
	 */
	private static final int ONE = 1;
	/**
	 * Constante com o valor 2.
	 */
	private static final int TWO = 2;
	/**
	 * Constante com o valor 3.
	 */
	private static final int THREE = 3;
	/**
	 * Constante com o valor 7.
	 */
	private static final int SEVEN = 7;

	/**
	 * instancia
	 */
	private static DateUtils instance;
	
	/**
	 * 
	 * Retorna um date com a mesma data, porem, com a a hora "23:59:99" 
	 *
	 * @author: Paulo Porto
	 * @param date - Date
	 * @return - Date
	 */
	public Date setLastHour (Date date) {
		
		if (date != null) {
			Calendar dataFinal = Calendar.getInstance(BaseConstant.PT_BR);
			dataFinal.setTime(date);
			
			dataFinal.set(Calendar.HOUR_OF_DAY, 23);
			dataFinal.set(Calendar.MINUTE, 59);
			dataFinal.set(Calendar.MILLISECOND, 99);
			
			return dataFinal.getTime();
		}
		
		return null;
	}

	/**
	 * 
	 * Construtor private 
	 * 
	 * @author: Gustavo Diniz 
	 * 
	 */
	private DateUtils() {

	}

	/**
	 * 
	 * Retorna instancia 
	 * Projeto/Requisição:  MESFlorestal/Sprint 1
	 * 
	 * @author: Gustavo Diniz 
	 * @return - Static instance of DateUtils
	 */
	public static DateUtils getInstance() {
		if (instance == null) {
			instance = new DateUtils();
		}
		return instance;
	}

	/**
	 * 
	 * Retorna a data informada em String 
	 * Projeto/Requisição:  MESFlorestal/Sprint 1
	 * 
	 * @author: Paulo Porto 
	 * @param pattern
	 *            - Formato da data EX: dd/MM/yyyy
	 * @param calendar
	 *            - Data
	 * @return - Data no formato String
	 */
	public String dateToString(String pattern, Calendar calendar) {
		return dateToString(pattern, calendar.getTime());
	}

	/**
	 * 
	 * Retorna a data informada em String 
	 * 
	 * @author: Paulo Porto 
	 * @param pattern
	 *            - Formato da data EX: dd/MM/yyyy
	 * @param date
	 *            - Data
	 * @return - Data no formato String
	 */
	public String dateToString(String pattern, Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(date);
	}

	/**
	 * Calcula diferenca entre duas datas 
	 * 
	 * @author: Paulo Porto 
	 * @param dataInicio
	 *            - data inicio
	 * @param dataFim
	 *            - data fim
	 * @return - resultado em dias
	 */
	public int calculaDiferencaDias(Calendar dataInicio, Calendar dataFim) {
		return (int) (((dataFim.getTimeInMillis() - dataInicio
				.getTimeInMillis()) / BaseConstant.MILI_SEG_TO_HOURS) / BaseConstant.HORS_TO_DAY);
	}

	/**
	 * Calcula diferenca entre duas datas 
	 * 
	 * @author: Paulo Porto 
	 * @param dataInicio
	 *            - data inicio
	 * @param dataFim
	 *            - data fim
	 * @return - resultado em dias
	 */
	public int calculaDiferencaDias(Date dataInicio, Date dataFim) {

		Calendar dtIni = Calendar.getInstance(BaseConstant.PT_BR);
		dtIni.setTime(dataInicio);
		Calendar dtFim = Calendar.getInstance(BaseConstant.PT_BR);
		dtFim.setTime(dataFim);

		return calculaDiferencaDias(dtIni, dtFim);
	}
	
	public int calculaDiferencaEntreMeses(Date dataInicio, Date dataFim){
		Calendar inicio = Calendar.getInstance();
		inicio.setTime(dataInicio);
		Calendar fim = Calendar.getInstance();
		fim.setTime(dataFim);
		int totalMeses = 0;
		
		while (inicio.before(fim)){
			totalMeses++;
			inicio.add(Calendar.MONTH, 1);
		}
		
		return totalMeses;
	}
	
	/**
	 * Checa se a data passada cai no final de semana
	 * Se cair ao sabado adiciona mais dois dias a data
	 * Se cair no domingo adiona mais um dia a data
	 * 
	 * @param data
	 * @return
	 */
	public Calendar checaDataFimDeSemana(Calendar data){

		if (data.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            data.add(Calendar.DATE, 1);

		} else if (data.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            data.add(Calendar.DATE, 2);
        }
        
        return data;
    }
	
	/**
	 * Calcula diferenca entre duas datas 
	 * 
	 * @author: Paulo Porto 
	 * @param dataInicio
	 *            - data inicio
	 * @param dataFim
	 *            - data fim
	 * @return - resultado em dias
	 */
	public int calculaDiferencaDiasAbsoluto(Date dataInicio, Date dataFim) {

		Calendar dtIni = Calendar.getInstance(BaseConstant.PT_BR);
		dtIni.setTime(dataInicio);
		dtIni.set(Calendar.HOUR_OF_DAY, 0);
		dtIni.set(Calendar.SECOND, 0);
		dtIni.set(Calendar.MINUTE, 0);
		dtIni.set(Calendar.MILLISECOND, 0);
		Calendar dtFim = Calendar.getInstance(BaseConstant.PT_BR);
		dtFim.setTime(dataFim);
		dtFim.set(Calendar.HOUR_OF_DAY, 0);
		dtFim.set(Calendar.SECOND, 0);
		dtFim.set(Calendar.MINUTE, 0);
		dtFim.set(Calendar.MILLISECOND, 0);

		return calculaDiferencaDias(dtIni, dtFim);
	}

	/**
	 * Soma dias em uma data. 
	 * 
	 * @author: Paulo Porto 
	 * @param date
	 *            - Data a ser somado os dias
	 * @param qtdDias
	 *            - quantidade de dias a somar
	 * @return - Data com os dias somados
	 */
	public Date somarDias(Date date, int qtdDias) {
		Calendar calendar = Calendar.getInstance(BaseConstant.PT_BR);
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, qtdDias);

		return calendar.getTime();
	}

	/**
	 * Calcula último dia do mês da data e retorna
	 * Projeto/Requisição:  MESFlorestal/Sprint 1
	 * 
	 * @author: Gustavo Diniz 
	 * @param periodo
	 *            - periodo para calculo
	 * @return ultimo dia do mês
	 * @throws ParseException
	 *             e
	 */
	public Date calculaUltimoDiaDoMes(Date periodo) throws ParseException {

		// para meses que terminam com 31
		int ultimoDia = Integer.parseInt("31");

		Calendar cPeriodo = Calendar.getInstance(BaseConstant.PT_BR);
		cPeriodo.setTime(periodo);

		if (cPeriodo.get(Calendar.MONTH) == Calendar.FEBRUARY) {
			// para fevereito
			ultimoDia = Integer.parseInt("28");
		} else if (cPeriodo.get(Calendar.MONTH) == Calendar.APRIL
				|| cPeriodo.get(Calendar.MONTH) == Calendar.JUNE
				|| cPeriodo.get(Calendar.MONTH) == Calendar.SEPTEMBER
				|| cPeriodo.get(Calendar.MONTH) == Calendar.NOVEMBER) {
			// para meses que terminam em 30
			ultimoDia = Integer.parseInt("30");
		}

		cPeriodo.set(Calendar.DAY_OF_MONTH, ultimoDia);

		return cPeriodo.getTime();
	}
	
	/**
	 * Calcula último dia do mês dado um mês e um ano
	 * 
	 * @author: Juscelino Oliveira
	 * @param mes
	 * @param ano
	 * @return ultimo dia do mês
	 */
	public int calculaUltimoDiaDoMes(int mes, int ano) {
		int ultimoDia = 31;
		if (mes == 2) {
			if (isBissexto(ano))
				ultimoDia = 29;
			else
				ultimoDia = 28;
		}
		else if (mes == 4 || mes == 6 || mes == 9 || mes == 11){
			ultimoDia = 30;
		}
		return ultimoDia;
	}
	
	/**
	 * Verifica se o ano é bissexto
	 * 
	 * @author: Juscelino Oliveira
	 * @param ano
	 * @return
	 */
	public boolean isBissexto(int ano) {
		if ( ( ano % 4 == 0 && ano % 100 != 0 ) || ( ano % 400 == 0 ) )
			return true;
		return false;
	}
	
	/**
	 * Verifica se a data é coerente e retorna a data corrigida.
	 * Evita, por exemplo, dia 31 de Fevereiro
	 * 
	 * @author: Juscelino Oliveira
	 * @param ano
	 * @return
	 */
	public Calendar verificaCoerenciaData(Calendar data) {
		int dia = data.get(Calendar.DAY_OF_MONTH);
		int mes = data.get(Calendar.MONTH);
		int ano = data.get(Calendar.YEAR);
		int ultimoDiaDoMes = calculaUltimoDiaDoMes(mes, ano);
		if (dia > ultimoDiaDoMes) 
			data.set(Calendar.DAY_OF_MONTH, ultimoDiaDoMes);;
		return data;
	}

	/**
	 * Cria um Date com a data ajustada de acordo com os parametros
	 * informados 
	 * 
	 * @author: Paulo Porto 
	 * @param year
	 *            - Ano
	 * @param month
	 *            - Mês (0 a 11) ou Calendar.MES
	 * @param day
	 *            - dia
	 * @return - Date
	 */
	public Date createDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance(BaseConstant.PT_BR);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, ZERO);
		calendar.set(Calendar.MINUTE, ZERO);
		calendar.set(Calendar.SECOND, ZERO);
		calendar.set(Calendar.MILLISECOND, ZERO);

		return calendar.getTime();
	}
	
	/**
	 * 
	 * Retorna um objeto do tipo calendar com HH:mm:SS preenchidos

	 * @author: Paulo Porto
	 * @param timeInMillis - java.lang.Long
	 * @return - Calendar
	 */
	public Calendar getTime (long timeInMillis) {
		Calendar calendar = Calendar.getInstance(BaseConstant.PT_BR);
		
		calendar.setTimeInMillis(timeInMillis);		
		calendar.set(Calendar.YEAR, ZERO);
		calendar.set(Calendar.MONTH, ZERO);
		calendar.set(Calendar.DAY_OF_MONTH, ZERO);
		
		return calendar;
	}
	
	/**
	 * 
	 * Retorna um objeto do tipo calendar com dd:MM:yyyy preenchidos
	 *
	 * @author: Paulo Porto
	 * @param timeInMillis - java.lang.Long
	 * @return - Calendar
	 */
	public Calendar getDate (long timeInMillis) {
		Calendar calendar = Calendar.getInstance(BaseConstant.PT_BR);
		calendar.setTimeInMillis(timeInMillis);
		calendar.setLenient(false);
		
		calendar.set(Calendar.HOUR, ZERO);
		calendar.set(Calendar.MINUTE, ZERO);
		calendar.set(Calendar.SECOND, ZERO);
		calendar.set(Calendar.MILLISECOND, ZERO);
		
		return calendar;
	}
	
	/**
	 * 
	 * Converte uma String para date.
	 *
	 * @author: Paulo Porto
	 * @param pattern - java.lang.String. Ex: dd/MM/yyyy
	 * @param dateString - java.lang.String.
	 * @return - java.util.Date
	 * @throws ParseException - e
	 */
	public Date stringToDate (String pattern, String dateString) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern(pattern);
		simpleDateFormat.setLenient(false);
		return new Date(simpleDateFormat.parse(dateString).getTime());
	}

	/**
	 * Cria um Date com a data ajustada de acordo com os parametros
	 * informados
	 * 
	 * @author: Paulo Porto 
	 * @param dateString
	 *            - dateString
	 * @return - Date
	 */
	public Date mmyyyyToDate(String dateString) {
		Calendar calendar = Calendar.getInstance(BaseConstant.PT_BR);

		String yearString = dateString.substring(THREE, SEVEN);
		calendar.set(Calendar.YEAR, new Integer(yearString).intValue());
		String monthString = dateString.substring(ZERO, TWO);
		calendar.set(Calendar.MONTH, new Integer(monthString).intValue() - ONE);
		calendar.set(Calendar.DAY_OF_MONTH, ONE);
		calendar.set(Calendar.HOUR_OF_DAY, ZERO);
		calendar.set(Calendar.MINUTE, ZERO);
		calendar.set(Calendar.SECOND, ZERO);
		calendar.set(Calendar.MILLISECOND, ZERO);

		return calendar.getTime();
	}

	/**
	 * Metodo generico que transforma uma string em um objeto do tipo
	 * java.lang.Date.
	 * 
	 * @author: Gustavo Sousa
	 * @param string
	 *            - dateString
	 * @return - Date
	 */
	public Date stringToDate(String string) {
		return mmyyyyToDate(string);
	}

	/**
	 * 
	 * Compara a data informada com a data atual e retorna verdadeiro
	 * se a data informada e maior que a data atual 
	 * 
	 * @author: Paulo Porto Fornecedor: Stefanini IT Solutions 
	 * @param data - java.util.Date
	 * @return - boolean
	 */
	public boolean dataMaiorDataAtual(Date data) {

		if (data != null && new Date().getTime() < data.getTime()) {
			return true;
		}

		return false;
	}
	
	/**
	 * 
	 * Compara a data1 informada com a data2. Se resultado maior que 
	 * 			  zero se data1 maior que data2 e menor que zero se data1 menor.
	 * 
	 * @author: Paulo Porto Fornecedor: Stefanini IT Solutions 
	 * @param data1 - java.util.Date
	 * @param data2 - java.util.Date
	 * @return - int
	 */
	public int compareDate(Date data1, Date data2) {
		
		Calendar cal1 = Calendar.getInstance(BaseConstant.PT_BR);
		Calendar cal2 = Calendar.getInstance(BaseConstant.PT_BR);
		
		cal1.setTime(data1);
		cal2.setTime(data2);
		
		cal1.set(Calendar.HOUR_OF_DAY, ZERO);
		cal1.set(Calendar.MINUTE, ZERO);
		cal1.set(Calendar.SECOND, ZERO);
		cal1.set(Calendar.MILLISECOND, ZERO);
		
		cal2.set(Calendar.HOUR_OF_DAY, ZERO);
		cal2.set(Calendar.MINUTE, ZERO);
		cal2.set(Calendar.SECOND, ZERO);
		cal2.set(Calendar.MILLISECOND, ZERO);
		
		return cal1.getTime().compareTo(cal2.getTime());
		
	}
	
	/**
	 * 
	 * Compara a data1 com a data2. Se a data1 for maior q a data2 retorna TRUE.
	 * 
	 * @author:Paulo Porto 
	 * @param data1 - java.util.Date
	 * @param data2 - java.util.Date
	 * @return - boolean
	 */
	public boolean verificaDataMaior (Date data1, Date data2) {
		if (data1.getTime() > data2.getTime()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Calcula a idade em anos com base no nascimento
	 * @param nascimento
	 * @return
	 */
	public Long calculaIdade(Date nascimento){
		
		Long idade = 0l;
		
		Calendar c = Calendar.getInstance(BaseConstant.PT_BR);
		Calendar b = Calendar.getInstance(BaseConstant.PT_BR);
		Calendar aniversario = Calendar.getInstance(BaseConstant.PT_BR);
		b.setTime(nascimento);
		aniversario.setTime(nascimento);
		aniversario.set(Calendar.YEAR, c.get(Calendar.YEAR));
		
		idade += c.get(Calendar.YEAR) - b.get(Calendar.YEAR);
		
		if(c.getTime().getTime() < aniversario.getTime().getTime()){
			idade--;
		}
		
		return idade;
		
	}
	
	/**
	 * Calcula a idade em anos com base no nascimento
	 * @param nascimento
	 * @param dataReferencia
	 * @return
	 */
	public Long calculaIdade(Date nascimento, Date dataReferencia){
		
		Long idade = 0l;
		
		Calendar c = Calendar.getInstance(BaseConstant.PT_BR);
		c.setTime(dataReferencia);
		Calendar b = Calendar.getInstance(BaseConstant.PT_BR);
		Calendar aniversario = Calendar.getInstance(BaseConstant.PT_BR);
		b.setTime(nascimento);
		aniversario.setTime(nascimento);
		aniversario.set(Calendar.YEAR, c.get(Calendar.YEAR));
		
		idade += c.get(Calendar.YEAR) - b.get(Calendar.YEAR);
		
		if(c.getTime().getTime() < aniversario.getTime().getTime()){
			idade--;
		}
		
		return idade;
		
	}
	
	public String getTimeFormat(long millis){
		
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;

		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
	
	public String getTimeMinutsFormat(long millis){
		
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60));

		return String.format("%02d:%02d", minute, second);
	}
	
	public long getTimeMillis(String time){
		
		String [] times = time.split(":");
		
		if(times.length == 3){
			long hourMillis = ((Long.parseLong(times[0]) * 60) * 60) * 1000;
			long minuteMillis = (Long.parseLong(times[1]) * 60) * 1000;
			long secondMillis = Long.parseLong(times[2]) * 1000;
	
			return hourMillis + minuteMillis + secondMillis;
		}else{
			long minuteMillis = (Long.parseLong(times[0]) * 60) * 1000;
			long secondMillis = Long.parseLong(times[1]) * 1000;
	
			return minuteMillis + secondMillis;
		}
	}
	
	public Calendar ajustaDataIni(Calendar c) {
		
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c;
		
	}

	
	public Calendar ajustaDataFim(Calendar c) {
		
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		
		return c;
		
	}
	
	public static void main(String [] args){
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -2);
	}
	
	
	public static Date zeraHora(Date date) {
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
	}
	
	public static Date zeraHora(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
	}
	
	public static void ajustaDiaUtil(Calendar vencimento) {
		
		if(vencimento.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY 
				&& vencimento.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY 
				&& FeriadoUtils.isFeriado(vencimento))
			vencimento.add(Calendar.DAY_OF_YEAR, 1);
		
		if(vencimento.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
			vencimento.add(Calendar.DAY_OF_YEAR, 2);
		
		if(vencimento.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			vencimento.add(Calendar.DAY_OF_YEAR, 1);
		
		if(FeriadoUtils.isFeriado(vencimento))
			vencimento.add(Calendar.DAY_OF_YEAR, 1);
		
	}
	
}
