package com.fetchrewards.fetchrewardsexercise.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.persistence.*;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    protected Calendar timestamp;

    @NonNull
    @ManyToOne
    @JsonIgnore
    protected Payer payer;

    @NonNull
    @ManyToOne
    @JsonIgnore
    protected Account account;

    @JsonProperty("payer")
    public String jsonPayer() {
        return payer.name;
    }
}
