package com.wesaidso.log.generator;

import com.wesaidso.log.ActionType;
import com.wesaidso.log.ProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class LogGeneratorApplication implements CommandLineRunner {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogGeneratorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LogGeneratorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        int repeat = 10000;

        repeat = getFirstArgumentIfPresentOrDefault(repeat, args);

        startLogging(repeat);
    }

    private void startLogging(int repeat) {
        LOGGER.info("About to repeat log lines {} times", repeat);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        executor.submit(() -> functionLoggerTask(ProcessType.SALES, ActionType.START, repeat, 2));
        executor.submit(() -> functionLoggerTask(ProcessType.SALES, ActionType.FINISH, repeat, 4));
        executor.submit(() -> functionLoggerTask(ProcessType.PAYMENT, ActionType.START, repeat, 1));
        executor.submit(() -> functionLoggerTask(ProcessType.PAYMENT, ActionType.ERROR, repeat / 100, repeat / 1000, 40));
        executor.submit(() -> functionLoggerTask(ProcessType.PAYMENT, ActionType.FINISH, repeat, 4));

        executor.shutdown();
    }

    private void functionLoggerTask(ProcessType process, ActionType action, int repeat, int sleep) {
        functionLoggerTask(process, action, repeat, 1, sleep);

    }

    private void functionLoggerTask(ProcessType process, ActionType action, int repeat, int step, int sleep) {
        for (int index = 1; index < repeat; index += step) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                LOGGER.error("Could not sleep task {}", action, e);
            }

            MDC.put("processType", process.name());
            MDC.put("actionType", action.name());
            MDC.put("processId", String.valueOf(index));

            LOGGER.info("{} of Function-{}", action.name(), index);
        }
    }


    private int getFirstArgumentIfPresentOrDefault(int repeat, String[] args) {
        if (args != null && args.length > 0) {
            repeat = Integer.parseInt(args[0]);
        }
        return repeat;
    }


}
