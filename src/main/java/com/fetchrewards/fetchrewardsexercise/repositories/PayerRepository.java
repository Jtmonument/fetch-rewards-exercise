package com.fetchrewards.fetchrewardsexercise.repositories;

import com.fetchrewards.fetchrewardsexercise.model.Account;
import com.fetchrewards.fetchrewardsexercise.model.Payer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PayerRepository extends CrudRepository<Payer, Long> {
    Optional<Payer> findByName(String name);

    List<Payer> findAllByAccount_Id(Long id);
    @Modifying
    @Transactional
    @Query("Update Payer p set p.points=p.points+:points where p.name=:name and p.account=:account")
    void addPoints(@Param("account") Account account, @Param("name") String name, @Param("points") Integer points);
}
