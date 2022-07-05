package com.fetchrewards.fetchrewardsexercise.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@DiscriminatorColumn
@RequiredArgsConstructor
@NoArgsConstructor
public abstract class Account extends BasicEntity {
    @NonNull
    protected String name;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    protected List<Payer> payers = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    protected List<Transaction> transactions = new ArrayList<>();
}
