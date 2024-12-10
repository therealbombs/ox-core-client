package com.ox.core.client.repository;

import com.ox.core.client.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    @Query("SELECT DISTINCT a FROM Account a " +
           "JOIN a.accountHolders ah " +
           "WHERE ah.client.abi = :abi AND ah.client.clientId = :clientId")
    List<Account> findByAbiAndClientId(String abi, String clientId);

    Optional<Account> findByIban(String iban);
    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("SELECT COUNT(DISTINCT a) FROM Account a " +
           "JOIN a.accountHolders ah " +
           "WHERE ah.client.clientId = :clientId")
    int countAccountsByClientId(String clientId);
}
