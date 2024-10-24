package br.com.startjob.acesso.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação usada para configurar o comportamento 
 * do controller. 
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface UseCase {
	
	/**
	 * Nome do caso de uso que o Controller se refere
	 * Deve ser configurado no Construtor
	 */
	public String funcionalidade();

	/**
	 * Classe da entidade do ManagedBean
	 */
	@SuppressWarnings("rawtypes")
	public Class classEntidade();
	
	/**
	 * NamedQuery padrão para uso nas pesquisas
	 */
	public String namedQueryPesquisa() default "findAll";

	/**
	 * Quantidade de resgistros que a pesquisa deve devolver
	 */
	public int quantPorPagina() default 10;
	
	/**
	 * Defini URL da tela para novo registro
	 */
	public String urlNovoRegistro() default "";
	
	/**
	 * Query para edição dos registros
	 */
	public String queryEdicao() default "";
	
	/**
	 * Query para edição dos registros
	 */
	public boolean lazyLoad() default false;
	
	/**
	 * Query para edição dos registros
	 */
	public boolean useLazyDataModel() default false;
	
	/**
	 * Indica se os parametros serão salvos na sessão.
	 */
	public boolean saveParans() default true;
	
	/**
	 * Indica se os parametros serão salvos na sessão.
	 */
	public boolean logicalRemove() default false;
	
	
}
