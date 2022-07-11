package com.fetchrewards.fetchrewardsexercise.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetchrewards.fetchrewardsexercise.exceptions.*;
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
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        Account account = accounts.findById(id).orElseThrow(() -> new AccountNotFoundException(id));  //  handle 404 exception
        return EntityModel.of(account,
                linkTo(methodOn(FetchRewardsController.class).getAccount(id)).withSelfRel());
    }

    @PostMapping(value = "/api/{id}/transaction/")
    public EntityModel<Transaction> addTransaction(@PathVariable Long id, @RequestBody String json) {
        Account account = accounts.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
        JsonNode parser;
        try {
            parser = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new JsonParseException(json); //  handle bad json parse
        }
        String payer = parser.get("payer").asText();
        Integer points = parser.get("points").asInt();
        String timestamp = parser.get("timestamp").asText();

        /* Validate JSON data */
        if (validate(payer)) {
            throw new BadInputException("payer", payer);    //  handle if null, blank, or empty
        } else if (validate(points)) {
            throw new BadInputException("points", points);  //  handle if null or zero
        } else if (validate(timestamp) || validateTimestamp(timestamp)) {
            throw new BadTimestampFormatException(timestamp);   //  handle if null, blank, empty, or of bad format
        }

        Payer payerEntity = payers.findByNameAndAccount(payer, account).orElse(null);

        /* If this payer is not known, add this payer */
        if (payerEntity == null) {
            payers.save((payerEntity = new PayerImpl(payer, account)));
        }

        Transaction transaction;

        transactions.save(transaction = service.addTransaction(payerEntity, points, timestamp));
        payers.addPoints(account, payer, points);   // update points for payer
        accounts.addPoints(id, points); // update points for account

        return EntityModel.of(transaction,
                linkTo(methodOn(FetchRewardsController.class).addTransaction(id, json)).withSelfRel());
    }

    @PutMapping(value = "/api/{id}/spend/")
    public CollectionModel<Transaction> spend(@PathVariable Long id, @RequestBody String json) {
        Account account = accounts.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
        JsonNode parser;
        try {
            parser = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new JsonParseException(json); //  handle bad json parse
        }
        Integer points = parser.get("points").asInt();

        /* Validate JSON data */
        if (validate(points)) {
            throw new BadInputException("points", points);  //  handle if zero or null
        }

        /* Check for insufficient points */
        if (account.getPoints() < points) {
            throw new InsufficientPointsException(account.getPoints(), points);
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
        Account account = accounts.findById(id).orElseThrow(() -> new AccountNotFoundException(id));  //  handle 404 exception
        return service.checkBalance(id, payers.findAllByAccount(account));
    }

    private <T> boolean validate(T obj) {
        return ((obj == null)
                || ((obj instanceof String) && ((((String) obj).isBlank()) || (((String) obj).isEmpty()))
                || ((obj instanceof Integer) && ((Integer) obj == 0))));
    }

    private boolean validateTimestamp(String timestamp) {
        try {
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(timestamp);
        } catch (ParseException e) {
            return true;
        }
        return false;
    }
}
