package com.webfluxtraining;

import com.webfluxtraining.exception.CustomException;
import org.junit.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D")
                .concatWith(Flux.error(new RuntimeException("Test Exception")))
                .concatWith(Flux.just("E"))
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_withOnErrorResume() {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D")
                .concatWith(Flux.error(new RuntimeException("Test Exception")))
                .concatWith(Flux.just("E"))
                .onErrorResume(e -> {
                    System.err.println(e.getMessage());
                    return Flux.just("Default");
                })
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D")
                .expectNext("Default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_withOnErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D")
                .concatWith(Flux.error(new RuntimeException("Test Exception")))
                .concatWith(Flux.just("E"))
                .onErrorReturn("Default")
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D")
                .expectNext("Default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_withOnErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D")
                .concatWith(Flux.error(new RuntimeException("Test Exception")))
                .concatWith(Flux.just("E"))
                .onErrorMap((e) -> new CustomException(e.getMessage()))
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_withOnErrorMapAndRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D")
                .concatWith(Flux.error(new RuntimeException("Test Exception")))
                .concatWith(Flux.just("E"))
                .onErrorMap((e) -> new CustomException(e.getMessage()))
                .retry(2)
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D")
                .expectNext("A", "B", "C", "D")
                .expectNext("A", "B", "C", "D")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_withOnErrorMapAndRetryBackoff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D")
                .concatWith(Flux.error(new RuntimeException("Test Exception")))
                .concatWith(Flux.just("E"))
                .onErrorMap((e) -> new CustomException(e.getMessage()))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(2)))
                .onErrorMap(Exceptions::isRetryExhausted, (e) -> new CustomException("Retry exhausted")) // Filter exception type and map to an own exception
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D")
                .expectNext("A", "B", "C", "D")
                .expectNext("A", "B", "C", "D")
                .expectError(CustomException.class)
                .verify();
    }
}
