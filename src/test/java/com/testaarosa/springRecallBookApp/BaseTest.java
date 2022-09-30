package com.testaarosa.springRecallBookApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;

@Slf4j
public abstract class BaseTest<T> {

    @BeforeAll
    static void init(TestInfo testInfo) {
        log.info("Start test suite: {}.", testInfo.getDisplayName());
    }

    @AfterAll
    static void afterAllTests(TestInfo testInfo) {
        log.info("Finished running the tests suit: {}." + testInfo.getDisplayName());
    }

    protected String jsonInString(T object) {

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
}
