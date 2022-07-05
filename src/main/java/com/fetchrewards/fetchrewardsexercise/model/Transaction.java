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

    @JsonAlias(value = "timestamp")
    public String jsonTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(timestamp.getTime());
    }
}
