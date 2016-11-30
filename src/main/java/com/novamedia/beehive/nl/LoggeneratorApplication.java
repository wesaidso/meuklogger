package com.novamedia.beehive.nl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class LoggeneratorApplication implements CommandLineRunner {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoggeneratorApplication.class);

    public void logForEver() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() -> {
            functionLoggerTask("Start", 2);
        });

        executor.submit(() -> {
            functionLoggerTask("End", 7);
        });
    }

    private void functionLoggerTask(String text, int sleep) {
        long count = 0;
        while (count <= 9999) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                LOGGER.error("Could not sleep task {}", text, e);
            }

            MDC.put("processType", "SALES");
            MDC.put("processId", String.valueOf(count));

            LOGGER.info("{} of Function-{}", text, count++);
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        logForEver();
    }

    public static void main(String[] args) {
        SpringApplication.run(LoggeneratorApplication.class, args);
    }

}
