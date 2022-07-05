package com.fetchrewards.fetchrewardsexercise.model;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@DiscriminatorValue("default")
public class AccountImpl extends Account {
    public AccountImpl(@NonNull String name) {
        super(name);
    }
}
