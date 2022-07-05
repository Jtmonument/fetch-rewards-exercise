package com.fetchrewards.fetchrewardsexercise.repositories;

import com.fetchrewards.fetchrewardsexercise.model.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
}
