package com.fetchrewards.fetchrewardsexercise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@DiscriminatorColumn
@RequiredArgsConstructor
@NoArgsConstructor
public abstract class Account extends BasicEntity {
    @NonNull
    @ToString.Include
    protected String name;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnoreProperties(value = {"account"})
    protected List<Payer> payers = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnoreProperties(value = {"account"})
    protected List<Transaction> transactions = new ArrayList<>();
}
