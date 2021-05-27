package com.webfluxtraining;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {
        Flux<Long> interval = Flux.interval(Duration.ofMillis(200)).log();
        interval.subscribe(System.out::println);
        Thread.sleep(5000);
    }

    @Test
    public void infiniteSequenceTest() {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log();

        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap_withDelay() {
        Flux<Integer> infiniteFlux = Flux.interval(Duration.ofMillis(1000)) // Downstream delay should not exceed 1 second
                .delayElements(Duration.ofSeconds(1))
                .map(Long::intValue)
                .take(3)
                .log();

        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }
}
