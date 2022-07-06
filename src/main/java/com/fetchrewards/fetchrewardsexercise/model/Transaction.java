package com.fetchrewards.fetchrewardsexercise.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@DiscriminatorColumn
@RequiredArgsConstructor
@NoArgsConstructor
public abstract class Transaction extends BasicEntity {
    @NonNull
    @JsonIgnore
    protected Calendar timestamp;

    @NonNull
    @ManyToOne
    @JsonIgnoreProperties(value = "points")
    protected Payer payer;

    @NonNull
    @ManyToOne
    @JsonIgnore
    protected Account account;

    @JsonAlias(value = "payer")
    public String jsonPayer() {
        return payer.name;
    }
}
