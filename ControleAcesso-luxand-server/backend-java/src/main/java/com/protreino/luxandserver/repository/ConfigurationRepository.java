package com.protreino.luxandserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.protreino.luxandserver.entity.ConfigurationEntity;

@Repository
public interface ConfigurationRepository extends JpaRepository<ConfigurationEntity, Long> {
	
	ConfigurationEntity findByName(String name);
	
}
