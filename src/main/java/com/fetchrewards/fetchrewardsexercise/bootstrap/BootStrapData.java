package com.fetchrewards.fetchrewardsexercise.bootstrap;

import com.fetchrewards.fetchrewardsexercise.model.Account;
import com.fetchrewards.fetchrewardsexercise.model.AccountImpl;
import com.fetchrewards.fetchrewardsexercise.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BootStrapData implements CommandLineRunner {

    private final AccountRepository accounts;

    @Override
    public void run(String... args) {
        Account account = new AccountImpl("root");
        accounts.save(account);
    }
}
