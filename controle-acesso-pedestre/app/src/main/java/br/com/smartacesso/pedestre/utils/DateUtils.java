package br.com.smartacesso.pedestre.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.smartacesso.pedestre.R;

/**
 *  Classe Util para Datas
 * 
 * @author: Gustavo Diniz
 * @since 04/03/2013
 */
@SuppressLint({ "DefaultLocale", "SimpleDateFormat" })
public final class DateUtils {

	private static final int ZERO = 0;
	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int THREE = 3;
	private static final int SEVEN = 7;
	public static final Locale PT_BR = new Locale("pt","BR");
	public static final int MILI_SEG_TO_HOURS = 3600000;
	public static final int HORS_TO_DAY = 24;

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
			Calendar dataFinal = Calendar.getInstance(PT_BR);
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
				.getTimeInMillis()) / MILI_SEG_TO_HOURS) / HORS_TO_DAY);
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

		Calendar dtIni = Calendar.getInstance(PT_BR);
		dtIni.setTime(dataInicio);
		Calendar dtFim = Calendar.getInstance(PT_BR);
		dtFim.setTime(dataFim);

		return calculaDiferencaDias(dtIni, dtFim);
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

		Calendar dtIni = Calendar.getInstance(PT_BR);
		dtIni.setTime(dataInicio);
		dtIni.set(Calendar.HOUR_OF_DAY, 0);
		dtIni.set(Calendar.SECOND, 0);
		dtIni.set(Calendar.MINUTE, 0);
		dtIni.set(Calendar.MILLISECOND, 0);
		Calendar dtFim = Calendar.getInstance(PT_BR);
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
		Calendar calendar = Calendar.getInstance(PT_BR);
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

		Calendar cPeriodo = Calendar.getInstance(PT_BR);
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
		Calendar calendar = Calendar.getInstance(PT_BR);
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
		Calendar calendar = Calendar.getInstance(PT_BR);
		
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
		Calendar calendar = Calendar.getInstance(PT_BR);
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
	@SuppressLint("UseValueOf")
	public Date mmyyyyToDate(String dateString) {
		Calendar calendar = Calendar.getInstance(PT_BR);

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
		
		Calendar cal1 = Calendar.getInstance(PT_BR);
		Calendar cal2 = Calendar.getInstance(PT_BR);
		
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
		
		Calendar c = Calendar.getInstance(PT_BR);
		Calendar b = Calendar.getInstance(PT_BR);
		Calendar aniversario = Calendar.getInstance(PT_BR);
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
		
		Calendar c = Calendar.getInstance(PT_BR);
		c.setTime(dataReferencia);
		Calendar b = Calendar.getInstance(PT_BR);
		Calendar aniversario = Calendar.getInstance(PT_BR);
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

        if(hour > 0)
            return String.format("%02d:%02d:%02d", hour, minute, second);
        else
            return String.format("%02d:%02d", minute, second);
	}
	
	public long getTimeMillis(String time){
		
		String[] times = time.split(":");
		
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


	public String getWeekDay(int weekDay){

	    switch (weekDay){
            case 1:
                return "Domingo";
            case 2:
                return "Segunda-feira";
            case 3:
                return "Terça-feira";
            case 4:
                return "Quarta-feira";
            case 5:
                return "Quinta-feira";
            case 6:
                return "Sexta-feira";
            case 7:
                return "Sabádo";
        }

	    return "Não definido";
    }

    public String dateToDetailString(Date data, Context context){

		if(data == null)
			return "--/--/---- --:--";

	    String dataStr = "";
	    Calendar cData = Calendar.getInstance();
	    cData.setTime(data);
	    Calendar cNow  = Calendar.getInstance();
	    Calendar cYesterday = Calendar.getInstance();
	    cYesterday.add(Calendar.DAY_OF_MONTH, -1);

	    if(cData.get(Calendar.DAY_OF_MONTH) == cNow.get(Calendar.DAY_OF_MONTH)
                && cData.get(Calendar.MONTH) == cNow.get(Calendar.MONTH)
                && cData.get(Calendar.YEAR) == cNow.get(Calendar.YEAR)){
	        //mesmo dia
            return context.getString(R.string.label_hoje) + " " + new SimpleDateFormat("HH:mm").format(data);
        }else if(cData.after(cYesterday) && cData.before(cNow)){
	        return context.getString(R.string.label_ontem);//"Ontem";
        }else{
            return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(data);
        }
    }

    public String dateToLessDetailString(Date data){

        String dataStr = "";
        Calendar cData = Calendar.getInstance();
        cData.setTime(data);
        Calendar cNow  = Calendar.getInstance();
        Calendar cYesterday = Calendar.getInstance();
        cYesterday.add(Calendar.DAY_OF_MONTH, -1);

        if(cData.get(Calendar.DAY_OF_MONTH) == cNow.get(Calendar.DAY_OF_MONTH)
                && cData.get(Calendar.MONTH) == cNow.get(Calendar.MONTH)
                && cData.get(Calendar.YEAR) == cNow.get(Calendar.YEAR)){
            //mesmo dia
            return "Hoje";
        }else if(cData.after(cYesterday) && cData.before(cNow)){
            return "Ontem";
        }else{
            return new SimpleDateFormat("dd/MM/yyyy").format(data);
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

}
