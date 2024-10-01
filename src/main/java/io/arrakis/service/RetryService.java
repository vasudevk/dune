package io.arrakis.service;

import io.arrakis.exception.InvalidOptimizationException;
import io.arrakis.exception.InvalidTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class RetryService {

    private static final Logger log = LoggerFactory.getLogger(RetryService.class);

    @Retryable(retryFor = InvalidOptimizationException.class,
            noRetryFor = InvalidTransactionException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000)
    )
    public String retry(String message) {
        if (message.isBlank()) {
            throw new InvalidOptimizationException("Retry with null message");
        } else if (message.equalsIgnoreCase("retry")) {
            throw new InvalidTransactionException("Message cannot be retried");
        }
        return message;
    }

    // Recover is needed for all RuntimeException's thrown by the method even if included in noRetryFor
    // OR use generic method w/ RuntimeException like below
    /*
        @Recover
        public String recover(RuntimeException ex, String message) {
            log.error("Retried", ex);
            throw ex;
        }
    */
    @Recover
    public String recover(InvalidOptimizationException ex, String message) {
        log.error("Retried", ex);
        throw ex;
    }

    @Recover
    public String recover(InvalidTransactionException ex, String message) {
        log.error(ex.getMessage(), ex);
        throw ex;
    }
}