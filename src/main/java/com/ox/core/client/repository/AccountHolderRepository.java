package com.ox.core.client.repository;

import com.ox.core.client.model.entity.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, String> {
    List<AccountHolder> findByAccountAccountId(String accountId);
    List<AccountHolder> findByClientClientId(String clientId);
}
