package com.fetchrewards.fetchrewardsexercise.services;

import com.fetchrewards.fetchrewardsexercise.controllers.FetchRewardsController;
import com.fetchrewards.fetchrewardsexercise.model.Payer;
import com.fetchrewards.fetchrewardsexercise.model.Transaction;
import com.fetchrewards.fetchrewardsexercise.model.TransactionImpl;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class AccountServiceImpl implements AccountService {

    @Override
    public Transaction addTransaction(Payer payer, Integer points, Date timestamp) {
        Calendar time = new Calendar.Builder().setInstant(timestamp).build();
        return new TransactionImpl(points, time, payer, payer.getAccount());
    }

    @Override
    public CollectionModel<Payer> checkBalance(Long id, List<Payer> payers) {
        return CollectionModel.of(payers,
                linkTo(methodOn(FetchRewardsController.class).checkBalance(id)).withSelfRel());
    }

    @Override
    public List<Transaction> spend(Integer spend, List<Transaction> transactions, List<Payer> payers) {
        HashMap<Payer, Transaction> newTransactions = new HashMap<>();
        for (Transaction transaction : transactions) {
            Payer payer = transaction.getPayer();

            /* Reduce time complexity by adding new transactions as they are necessary */
            Transaction newTransaction = newTransactions.getOrDefault(payer, new TransactionImpl(payer, payer.getAccount()));
            if (spend >= transaction.getPoints()) {
                if (payer.getPoints() - transaction.getPoints() >= 0) {
                    spend -= transaction.getPoints();
                    payer.setPoints(payer.getPoints() - transaction.getPoints());
                    newTransaction.setPoints(newTransaction.getPoints() - transaction.getPoints());
                } else {

                    /* If payer's points is zero, points to spend are not subtracted here */
                    spend -= transaction.getPoints() + (payer.getPoints() - transaction.getPoints());
                    Integer points = transaction.getPoints() + (payer.getPoints() - transaction.getPoints());
                    newTransaction.setPoints(newTransaction.getPoints() - points);

                    /* If payer's points is zero, don't let it go negative */
                    payer.setPoints((payer.getPoints() == 0) ? 0 : transaction.getPoints() - payer.getPoints());
                }
                newTransactions.put(payer, newTransaction);
            } else {
                if (payer.getPoints() - spend >= 0) {

                    /* Algorithm finished */
                    newTransaction.setPoints(newTransaction.getPoints() - spend);
                    payer.setPoints(payer.getPoints() - spend);
                    spend = 0;
                } else {

                    /* Last points to spend distributed between payers in if statement below */
                    spend -= payer.getPoints();
                    newTransaction.setPoints(newTransaction.getPoints() - payer.getPoints());
                    payer.setPoints(0);
                }
                newTransactions.put(payer, newTransaction);
                break;
            }
        }

        /* Distribute last points between payers */
        if (spend > 0) {
            for (Payer payer : payers) {

                /* In case payers are still not in hashmap */
                Transaction newTransaction = newTransactions.getOrDefault(payer, new TransactionImpl(payer, payer.getAccount()));

                /* Prevent error with transactions of zero points from adding to payer's points */
                if (payer.getPoints() == 0) {
                    newTransactions.remove(payer);
                } else if (payer.getPoints() >= spend) {
                    payer.setPoints(payer.getPoints() - spend);
                    newTransaction.setPoints(newTransaction.getPoints() - spend);
                    newTransactions.put(payer, newTransaction);
                    break; // spend = 0
                } else {
                    spend -= payer.getPoints();
                    newTransaction.setPoints(newTransaction.getPoints() - payer.getPoints());
                    newTransactions.put(payer, newTransaction);
                    payer.setPoints(0); // spend - payer.getPoints()
                }
            }
        }

        /* Remove any remaining transactions of zero points and add necessary data */
        return newTransactions.values().stream().filter(transaction -> {
            transaction.setTimestamp(Calendar.getInstance());
            return transaction.getPoints() != 0;    // return lambda
        }).toList();
    }
}
