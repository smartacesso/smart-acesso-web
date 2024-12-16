package br.com.startjob.acesso.modelo.useCase.importacaoclubessesi;

import br.com.startjob.acesso.modelo.entity.ImportacaoEntity;

public class ImportaPedestreClubesSesiUseCase {

	public void execute(final ImportacaoEntity dadosImportacao) {
		// criar dois campos na tabela TB_CLIENTE
		//ID_FILIAL e ID_REGIONAL e preencher de acordo com cada cliente
		
		//Para cada aluno exportado
		// pegar o campo id_regional e inserir ou atualizar esse aluno em todos os clientes que pertecem a mesma regional.

		// para cada cliente da regional
		// buscar o aluno no cliente pra ver se ja existe. Isso deverá ser feito pelo campo id_associado que na tabela TB_PEDESTRE é 
		// representado pelo campo MATRICULA
		// se não existe salvar o aluno na tabela TB_PEDESTRE
		// A coluna ID deve ser gerado pelo autoincrement
		// NOME -> deve ser prenchido com o valor da coluna {nome} gerado na exportação
		// MATRICULA -> deve ser prenchido com o valor da coluna {id_associado} gerado na exportação
		// CARTAO_ACESSO -> deve ser prenchido com o valor da coluna {carterinha} gerado na exportação
		// STATUS -> deve ser prenchido com o valor da coluna {liberado} gerado na exportação (Se S -> ATIVO, Se N -> INATIVO)
		// A coluna TIPO_PEDESTRE deve ser preenchida com o valor PEDESTRE
		// A coluna SEMPRE_LIBERADO que é do tipo booleano deve ser preenchida com o valor true
		// A coluna ID_CLIENTE deve ser preenchida com o ID do cliente que estiver sendo cadastrado
		// As colunas DATA_ALTERACAO e DATA_CRIACAO devem ser prenchidas com a data atual
		// A coluna VERSION deve ser preenchida com o valor 0
		
		// se o aluno já existe atualizar os campos 
		// STATUS do Aluno na tabela TB_PEDESTRE STATUS(Se S -> ATIVO, Se N -> Inativo)
		// A coluna DATA_ALTERACAO deve ser prencida com a data atual
	}
}
