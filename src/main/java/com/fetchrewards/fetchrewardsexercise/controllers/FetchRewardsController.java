package com.fetchrewards.fetchrewardsexercise.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetchrewards.fetchrewardsexercise.model.Account;
import com.fetchrewards.fetchrewardsexercise.model.Payer;
import com.fetchrewards.fetchrewardsexercise.model.PayerImpl;
import com.fetchrewards.fetchrewardsexercise.model.Transaction;
import com.fetchrewards.fetchrewardsexercise.repositories.AccountRepository;
import com.fetchrewards.fetchrewardsexercise.repositories.PayerRepository;
import com.fetchrewards.fetchrewardsexercise.repositories.TransactionRepository;
import com.fetchrewards.fetchrewardsexercise.services.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@AllArgsConstructor
@RestController
public class FetchRewardsController {

    private final AccountRepository accounts;
    private final PayerRepository payers;
    private final TransactionRepository transactions;
    private final AccountService service;

    /*
    * Used to help parse JSON
    * */
    private final ObjectMapper mapper;

    /*
    * Return a JSON of account info, such as name, points, payers, and transactions
    * */
    @GetMapping(value = "/api/{id}/account/")
    public EntityModel<Account> getAccount(@PathVariable Long id) {
        Account account = accounts.findById(id).orElseThrow();  //  handle 404 exception
        return EntityModel.of(account,
                linkTo(methodOn(FetchRewardsController.class).getAccount(id)).withSelfRel());
    }

    @PostMapping(value = "/api/{id}/addTransaction/")
    public ResponseEntity<String> addTransaction(@PathVariable Long id, @RequestBody String json) throws JsonProcessingException {
        Account account = accounts.findById(id).orElseThrow();  // handle 404 exception
        JsonNode parser = mapper.readTree(json);    // handle json exception
        String payer = parser.get("payer").asText();    //  handle if null
        Integer points = parser.get("points").asInt();  //  handle if null or zero
        String timestamp = parser.get("timestamp").asText();    //  handle if null or bad format
        Payer payerEntity = payers.findByNameAndAccount(payer, account).orElse(null);

        /* If this payer is not known, add this payer */
        if (payerEntity == null) {
            payers.save((payerEntity = new PayerImpl(payer, account)));
        }

        transactions.save(service.addTransaction(payerEntity, points, timestamp));
        payers.addPoints(account, payer, points);   // update points for payer
        accounts.addPoints(id, points); // update points for account

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PutMapping(value = "/api/{id}/spend/")
    public CollectionModel<Transaction> spend(@PathVariable Long id, @RequestBody String json) throws JsonProcessingException {
        Account account = accounts.findById(id).orElseThrow();  //  handle 404 exception
        JsonNode parser = mapper.readTree(json);    //  handle json exception
        Integer points = parser.get("points").asInt();  //  handle if zero or null

        if (account.getPoints() < points) { //  handle if not enough points
            throw new RuntimeException("Not enough points");
        }

        List<Transaction> collection = service.spend(points, transactions.findAllByAccount_Id(id),
                payers.findAllByAccount(account));
        transactions.saveAll(collection);
        accounts.addPoints(id, (-points));  //  subtract points

        return CollectionModel.of(collection,
                linkTo(methodOn(FetchRewardsController.class).spend(id, json)).withSelfRel());
    }

    @GetMapping(value = "/api/{id}/balance/")
    public CollectionModel<Payer> checkBalance(@PathVariable Long id) {
        Account account = accounts.findById(id).orElseThrow();  //  handle 404 exception
        return service.checkBalance(id, payers.findAllByAccount(account));
    }
}
