package com.webfluxtraining;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

/**
 * @author roshanmadhushanka
 **/
public class VirtualTimeTest {

    @Test
    public void testWithoutVirtualTime() {
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.create(longFlux.log())
                .expectSubscription()
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    public void testWithVirtualTime() {
        VirtualTimeScheduler.getOrSet();
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);
        StepVerifier.withVirtualTime(() -> longFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }
}
