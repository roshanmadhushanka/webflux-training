package com.webfluxtraining;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = Arrays.asList("Roshan", "Madhushanka", "Alwis");

    @Test
    public void filterTest() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(e -> e.startsWith("R"))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Roshan")
                .verifyComplete();
    }
}
