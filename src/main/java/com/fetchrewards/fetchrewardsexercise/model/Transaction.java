package com.fetchrewards.fetchrewardsexercise.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@ToString(exclude = {"account", "timestamp"})
@DiscriminatorColumn
@RequiredArgsConstructor
@NoArgsConstructor
public abstract class Transaction extends BasicEntity {
    @NonNull
    protected Calendar timestamp;

    @NonNull
    @ManyToOne
    @JsonIgnoreProperties(value = {"account", "points"})
    protected Payer payer;

    @NonNull
    @ManyToOne
    protected Account account;

    @JsonAlias(value = "timestamp")
    public String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(timestamp.getTime());
    }
}
