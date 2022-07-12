package com.fetchrewards.fetchrewardsexercise.repositories;

import com.fetchrewards.fetchrewardsexercise.model.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    @Modifying
    @Transactional
    @Query("Update Account a set a.points=a.points+:points where a.id=:id")
    void addPoints(@Param("id") Long id, @Param("points") Integer points);

    @Modifying
    @Transactional
    @Query("Delete from Payer p where p.account.id=:id")
    void deletePayers(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("Delete from Transaction t where t.account.id=:id")
    void deleteTransactions(@Param("id") Long id);
}
