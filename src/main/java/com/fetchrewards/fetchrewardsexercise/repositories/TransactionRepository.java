package com.fetchrewards.fetchrewardsexercise.repositories;

import com.fetchrewards.fetchrewardsexercise.model.Account;
import com.fetchrewards.fetchrewardsexercise.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    @Query("select t from Transaction t where t.account.id=:id order by t.timestamp asc")
    List<Transaction> findAllByAccount_Id(@Param("id") Long id);
}
