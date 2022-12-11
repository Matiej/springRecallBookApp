package com.testaarosa.springRecallBookApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class BaseTest {
    private static final String TEST_ADMIN_USER = "admintest@testadmin.org";

    @BeforeAll
    static void init(TestInfo testInfo) {
        log.info("Start test suite: {}.", testInfo.getDisplayName());
    }

    @AfterAll
    static void afterAllTests(TestInfo testInfo) {
        log.info("Finished running the tests suit: {}." + testInfo.getDisplayName());
    }

    protected String jsonInString(Object object) {

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);

        try {
            jsonInString = mapper.writeValueAsString(object);
//            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonInString;
    }

    protected List<Book> prepareBooks() {

        Book effective_java = new Book(
                "Effective Java",
                2005,
                new BigDecimal("101.82"),
                12L);

        Book mama_mia = new Book(
                "Mama mia",
                2015,
                new BigDecimal(10),
                12L);
        return Arrays.asList(effective_java, mama_mia);
    }

    protected String getADMIN_USER() {
        return TEST_ADMIN_USER;
    }

    protected String allowedMethods(HttpMethod... methods) {
        return Arrays.toString(methods);
    }
}
