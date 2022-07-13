package com.fetchrewards.fetchrewardsexercise;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FetchRewardsExerciseApplicationTests {

    /*
    * IntelliJ incorrectly complains "Could not autowire. No beans of 'MockMvc' type found."
    * Whether the annotation is commented out or not, the code works
    * */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    private final Long ID = 1L;


    @DisplayName("account not found")
    @Test
    void test1() throws Exception {
        mockMvc.perform(get("/api/{id}/balance/", 2L)).andExpect(status().isNotFound());
        mockMvc.perform(put("/api/{id}/spend/", (-2L))
                .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/{id}/transaction/", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isNotFound());
    }
    @DisplayName("bad json parse")
    @Test
    void test2() throws Exception {
        mockMvc.perform(post("/api/{id}/transaction/", ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/{id}/transaction/", ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"payer\": \"DANNON\"}")).andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/{id}/spend/", ID)
                .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/{id}/spend/", ID)
                .contentType(MediaType.APPLICATION_JSON).content("{\"spen\": 1000}")).andExpect(status().isBadRequest());
    }
    @DisplayName("insufficient points")
    @Test
    void test3() throws Exception {
        spend(1000).andExpect(status().isNotAcceptable());
        addTransaction("DANNON", 1000, "2020-11-02T14:00:00Z");
        spend(2000).andExpect(status().isNotAcceptable());
    }
    @DisplayName("bad timestamp")
    @Test
    void test4() throws Exception {
        addTransaction("DANNON", 1000, "2020-11-02T14:00:00").andExpect(status().isBadRequest());
        addTransaction("DANNON", 1000, "").andExpect(status().isBadRequest());
    }
    @DisplayName("null payer in transaction")
    @Test
    void test5() throws Exception {
        String json = "{\"payer\": null, \"points\": 1000, \"timestamp\": \"2020-11-02T14:00:00Z\"}";
        mockMvc.perform(post("/api/{id}/transaction/", ID).content(json)).andExpect(status().isBadRequest());
        addTransaction(null, 1000, "2020-11-02T14:00:00Z").andExpect(status().isBadRequest());
    }
    @DisplayName("empty payer in transaction")
    @Test
    void test6() throws Exception {
        String json = "{\"payer\": , \"points\": 1000, \"timestamp\": \"2020-11-02T14:00:00Z\"}";
        mockMvc.perform(post("/api/{id}/transaction/", ID).content(json)).andExpect(status().isBadRequest());
        addTransaction("", 1000, "2020-11-02T14:00:00Z").andExpect(status().isBadRequest());
    }
    @DisplayName("blank payer in transaction")
    @Test
    void test7() throws Exception {
        addTransaction("   ", 1000, "2020-11-02T14:00:00Z").andExpect(status().isBadRequest());
    }
    @DisplayName("null points in transaction")
    @Test
    void test8() throws Exception {
        String json = "{\"payer\": \"DANNON\", \"points\": null, \"timestamp\": \"2020-11-02T14:00:00Z\"}";
        mockMvc.perform(post("/api/{id}/transaction/", ID).content(json)).andExpect(status().isBadRequest());
        json = "{\"payer\": \"DANNON\", \"points\": \"null\", \"timestamp\": \"2020-11-02T14:00:00Z\"}";
        mockMvc.perform(post("/api/{id}/transaction/", ID).content(json)).andExpect(status().isBadRequest());
    }
    @DisplayName("zero points in transaction")
    @Test
    void test9() throws Exception {
        String json = "{\"payer\": \"DANNON\", \"points\": 0, \"timestamp\": \"2020-11-02T14:00:00Z\"}";
        mockMvc.perform(post("/api/{id}/transaction/", ID).content(json)).andExpect(status().isBadRequest());
        addTransaction("DANNON", 0, "2020-11-02T14:00:00Z").andExpect(status().isBadRequest());
    }
    @DisplayName("timestamp in the future")
    @Test
    void test10() throws Exception {
        addTransaction("DANNON", 1000, "2075-11-02T14:00:00Z").andExpect(status().isBadRequest());
    }
    @DisplayName("points are negative")
    @Test
    void test11() throws Exception {
        spend(-1000).andExpect(status().isBadRequest());
        addTransaction("DANNON", 1000, "2020-11-02T14:00:00Z");
        spend(-1000).andExpect(status().isBadRequest());
    }
    @DisplayName("spend 5000 (5 Transactions, 3 Payers)")
    @Test
    void test12() throws Exception {
        exampleTransactions();
        spend(5000).andExpect(sumOfPoints(-5000))
                .andExpect(spendCheck("DANNON", -100))
                .andExpect(spendCheck("MILLER COORS", -4700))
                .andExpect(spendCheck("UNILEVER", -200));
        balance().andExpect(sumOfPoints(6300))
                .andExpect(balanceCheck("DANNON", 1000))
                .andExpect(balanceCheck("MILLER COORS", 5300))
                .andExpect(balanceCheck("UNILEVER", 0));
    }

    @DisplayName("Spend 1000 (5 Transactions, 3 Payers)")
    @Test
    void test13() throws Exception {
        exampleTransactions();
        spend(1000).andExpect(sumOfPoints(-1000))
                .andExpect(spendCheck("DANNON", -100))
                .andExpect(spendCheck("MILLER COORS", -700))
                .andExpect(spendCheck("UNILEVER", -200));
        balance().andExpect(sumOfPoints(10300))
                .andExpect(balanceCheck("DANNON", 1000))
                .andExpect(balanceCheck("MILLER COORS", 9300))
                .andExpect(balanceCheck("UNILEVER", 0));
    }

    @DisplayName("Spend all (1 payer)")
    @Test
    void test14() throws Exception {
        addTransaction("DANNON", 1000, "2020-11-02T14:00:00Z");
        spend(1000).andExpect(sumOfPoints(-1000)).andExpect(spendCheck("DANNON", -1000));
    }
    @DisplayName("Spend 5000, 3000 (5 Transactions, 3 Payers)")
    @Test
    void test15() throws Exception {
        exampleTransactions();
        spend(5000);
        spend(3000).andExpect(sumOfPoints(-3000))
                .andExpect(spendCheck("DANNON", (-100)))
                .andExpect(spendCheck("MILLER COORS", (-2900)));
        balance().andExpect(sumOfPoints(3300))
                .andExpect(balanceCheck("DANNON", 900))
                .andExpect(balanceCheck("MILLER COORS", 2400))
                .andExpect(balanceCheck("UNILEVER", 0));
    }
    @DisplayName("Spend 5000, 5000 (5 Transactions, 3 Payers)")
    @Test
    void test16() throws Exception {
        exampleTransactions();
        spend(5000);
        spend(5000).andExpect(sumOfPoints(-5000))
                .andExpect(spendCheck("DANNON", (-100)))
                .andExpect(spendCheck("MILLER COORS", (-4900)));
        balance().andExpect(sumOfPoints(1300))
                .andExpect(balanceCheck("DANNON", 900))
                .andExpect(balanceCheck("MILLER COORS", 400))
                .andExpect(balanceCheck("UNILEVER", 0));
    }

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(delete("/api/{id}/delete/", ID));
    }

    private void exampleTransactions() throws Exception {
        addTransaction("DANNON", 1000, "2020-11-02T14:00:00Z");
        addTransaction("UNILEVER", 200, "2020-10-31T11:00:00Z");
        addTransaction("DANNON", -200, "2020-10-31T15:00:00Z");
        addTransaction("MILLER COORS", 10000, "2020-11-01T14:00:00Z");
        addTransaction("DANNON", 300, "2020-10-31T10:00:00Z");
    }

    private ResultMatcher sumOfPoints(int points) {
        return jsonPath("$..points.sum()").value(points);
    }

    private ResultMatcher balanceCheck(String payer, int points) {
        return jsonPath(String.format("$..payerImplList[?(@.name == '%s')].points", payer)).value(points);
    }

    private ResultMatcher spendCheck(String payer, int points) {
        return jsonPath(String.format("$..transactionImplList[?(@.payer == '%s')].points", payer)).value(points);
    }

    private ResultActions balance() throws Exception {
        return mockMvc.perform(get("/api/{id}/balance/", ID));
    }

    private ResultActions spend(int points) throws Exception {
        final String spendJSON = "{\"points\": %d}";
        return mockMvc.perform(put("/api/{id}/spend/", ID)
                .contentType(MediaType.APPLICATION_JSON).content(String.format(spendJSON, points)));
    }

    private ResultActions addTransaction(String payer, int points, String timestamp) throws Exception {
        final String transactionJSON = "{\"payer\": \"%s\", \"points\": %d, \"timestamp\": \"%s\"}";
        return mockMvc.perform(post("/api/{id}/transaction/", ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(transactionJSON, payer, points, timestamp)));
    }
}
