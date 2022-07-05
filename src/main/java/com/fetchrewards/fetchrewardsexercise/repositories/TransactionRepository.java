package com.fetchrewards.fetchrewardsexercise.repositories;

import com.fetchrewards.fetchrewardsexercise.model.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}
