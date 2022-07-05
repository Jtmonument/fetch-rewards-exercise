package com.fetchrewards.fetchrewardsexercise.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@ToString(exclude = "account")
@DiscriminatorColumn
@RequiredArgsConstructor
@NoArgsConstructor
public abstract class Payer extends BasicEntity {
    @NonNull
    protected String name;

    @NonNull
    @ManyToOne
    @JsonIgnore
    protected Account account;
}
