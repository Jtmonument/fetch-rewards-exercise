package com.fetchrewards.fetchrewardsexercise.model;

import lombok.*;

import javax.persistence.*;

@MappedSuperclass
@Getter
@Setter
@ToString
public abstract class BasicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NonNull
    protected Integer points = 0;
}
