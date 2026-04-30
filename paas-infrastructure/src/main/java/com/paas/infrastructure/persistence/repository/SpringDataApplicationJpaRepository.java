package com.paas.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import com.paas.infrastructure.persistence.entity.ApplicationEntity;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SpringDataApplicationJpaRepository extends JpaRepository<ApplicationEntity, UUID> {

}
