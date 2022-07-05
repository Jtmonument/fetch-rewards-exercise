package com.fetchrewards.fetchrewardsexercise.repositories;

import com.fetchrewards.fetchrewardsexercise.model.Payer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PayerRepository extends CrudRepository<Payer, Long> {
    Optional<Payer> findPayerByName(String name);

    @Modifying
    @Transactional
    @Query("Update Payer p set p.points=p.points+:points where p.id=:id")
    void addPoints(@Param("id") Long id, @Param("points") Integer points);
}
