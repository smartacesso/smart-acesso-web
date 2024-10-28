package com.senior.services;


import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import br.com.startjob.acesso.modelo.entity.EquipamentoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEquipamentoEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PedestreEquipamentoService {

    
	@PersistenceContext(unitName = "ControleAcesso-pu")
	protected EntityManager em;

	@Transactional
	public void adicionaPedestreEquipamentoSenior(PedestreEquipamentoEntity pedestreEquipamento, PedestreEntity pedestre, String nomeEquipamento) {
	    if (em == null) {
	        throw new IllegalStateException("EntityManager não foi injetado corretamente.");
	    }
	    
	    try {
	        List<EquipamentoEntity> equipamentos = em.createNamedQuery("EquipamentoEntity.findAll", EquipamentoEntity.class)
	                                                 .getResultList();
	        EquipamentoEntity equipamentoEncontrado = null;

	        for (EquipamentoEntity equipamento : equipamentos) {
	            if (equipamento.getNome().equals(nomeEquipamento)) {
	                equipamentoEncontrado = equipamento;    
	                break;
	            }
	        }

	        if (equipamentoEncontrado != null) {
	            pedestreEquipamento.setEquipamento(equipamentoEncontrado);    
	            pedestreEquipamento.setPedestre(pedestre);
	            pedestreEquipamento.setExistente(true);
	            em.persist(pedestreEquipamento);
	        } else {
	            throw new IllegalArgumentException("Equipamento com nome " + nomeEquipamento + " não encontrado.");
	        }

	    } catch (IllegalArgumentException e) {
	        throw e; 
	    } catch (Exception e) {
	        throw new RuntimeException("Erro ao adicionar o pedestre ao equipamento: " + e.getMessage(), e);
	    }
	}

}
