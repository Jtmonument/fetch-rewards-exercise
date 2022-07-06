package com.fetchrewards.fetchrewardsexercise.services;

import com.fetchrewards.fetchrewardsexercise.model.Payer;
import com.fetchrewards.fetchrewardsexercise.model.Transaction;
import org.springframework.hateoas.CollectionModel;

import java.util.List;

public interface AccountService {
    Transaction addTransaction(Payer payer, Integer points, String timestamp);
    CollectionModel<Payer> checkBalance(Long id, List<Payer> payers);
    List<Transaction> spend(Integer spend, List<Transaction> transactions, List<Payer> payers);
}
