package com.ox.core.client.repository;

import com.ox.core.client.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByAbiAndClientId(String abi, String clientId);
    Optional<Client> findByFiscalCode(String fiscalCode);
    boolean existsByClientId(String clientId);
}
