/**
 * 
 */
package br.com.startjob.acesso.modelo.exception;

/**
 * Identifica concorrência de alteração
 * de registros
 *
 * @author: Gustavo Diniz
 * @since 03/02/2013 
 */
public class ConcurrenceException extends Exception {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -6399401885673741911L;
	
	/**
	 * Mensagem padrão
	 */
	public static final String MSG_PADRAO = "msg.generica.erro.resgitro.alterado";

	/**
	 * 
	 * Construtor que configura mensagem padrão
	 *
	 * @author: Gustavo Diniz
	 */
	public ConcurrenceException(){
		super(MSG_PADRAO);
	}
	
}
