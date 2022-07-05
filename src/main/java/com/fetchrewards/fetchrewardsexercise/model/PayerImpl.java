package com.fetchrewards.fetchrewardsexercise.model;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@DiscriminatorValue("default")
public class PayerImpl extends Payer {
    public PayerImpl(@NonNull String name, @NonNull Account account) {
        super(name, account);
    }
}
