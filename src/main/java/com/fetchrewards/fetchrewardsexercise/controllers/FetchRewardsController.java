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
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
    * Return a JSON of account info, such as name, points, payers, and transactions (for debugging purposes)
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
        String payer;
        Integer points;
        String timestamp;
        try {
            parser = mapper.readTree(json);
            payer = parser.get("payer").asText();
            points = parser.get("points").asInt();
            timestamp = parser.get("timestamp").asText();
        } catch (JsonProcessingException | NullPointerException e) {
            throw new JsonParseException(json); //  handle bad json parse
        }

        /* Validate JSON data */
        if (validate(payer)) {
            throw new BadInputException("payer", payer);    //  handle if null, blank, or empty
        } else if (validate(points)) {
            throw new BadInputException("points", points);  //  handle if null or zero
        } else if (validate(timestamp)) {
            throw new BadTimestampFormatException(timestamp);   //  handle if null, blank, empty, or of bad format
        }
        Date time = validateTimestamp(timestamp);

        Payer payerEntity = payers.findByNameAndAccount(payer, account).orElse(new PayerImpl(payer, account));
        if (payerEntity.getId() == null) {
            payers.save(payerEntity);
        }

        Transaction transaction = transactions.save(service.addTransaction(payerEntity, points, time));
        payers.addPoints(account, payer, points);   // update points for payer
        accounts.addPoints(id, points); // update points for account

        return EntityModel.of(transaction,
                linkTo(methodOn(FetchRewardsController.class).addTransaction(id, json)).withSelfRel());
    }

    @PutMapping(value = "/api/{id}/spend/")
    public CollectionModel<Transaction> spend(@PathVariable Long id, @RequestBody String json) {
        Account account = accounts.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
        JsonNode parser;
        Integer points;
        try {
            parser = mapper.readTree(json);
            points = parser.get("points").asInt();
        } catch (JsonProcessingException | NullPointerException e) {
            throw new JsonParseException(json); //  handle bad json parse
        }

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

    /*
    * Remove all transactions and payers from account (for unit testing purposes)
    * */
    @DeleteMapping(value = "/api/{id}/delete/")
    public EntityModel<Account> deleteEverything(@PathVariable Long id) {
        accounts.findById(id).orElseThrow(() -> new AccountNotFoundException(id)).setPoints(0);
        accounts.deleteTransactions(id);
        accounts.deletePayers(id);
        return getAccount(id);
    }

    private <T> boolean validate(T obj) {
        return ((obj == null)
                || ((obj instanceof String) && ((((String) obj).isBlank()) || (((String) obj).isEmpty()))
                || ((obj instanceof Integer) && ((Integer) obj == 0))));
    }

    private Date validateTimestamp(String timestamp) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date time = formatter.parse(timestamp);
            if (time.after(Date.from(Instant.now()))) {
                throw new BadTimestampFormatException(timestamp);
            }
            return time;
        } catch (ParseException e) {
            throw new BadTimestampFormatException(timestamp);
        }
    }
}
