package com.fetchrewards.fetchrewardsexercise.model;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Calendar;

@Entity
@NoArgsConstructor
@DiscriminatorValue("default")
public class TransactionImpl extends Transaction {
    public TransactionImpl(@NonNull Integer points, @NonNull Calendar timestamp, @NonNull Payer payer, @NonNull Account account) {
        super(timestamp, payer, account);
        setPoints(points);
    }

    public TransactionImpl(@NonNull Payer payer, @NonNull Account account) {
        this.payer = payer;
        this.account = account;
    }
}
