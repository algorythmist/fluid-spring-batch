package com.tecacet.fluidbatch.berka.repository;

import com.tecacet.fluidbatch.berka.entity.ClientEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
}
