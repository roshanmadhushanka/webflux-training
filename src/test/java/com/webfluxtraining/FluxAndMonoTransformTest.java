package com.webfluxtraining;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("Roshan", "Madhushanka", "Alwis");

    @Test
    public void transformUsingMap() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .map(String::toUpperCase)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("ROSHAN")
                .expectNext("MADHUSHANKA")
                .expectNext("ALWIS")
                .verifyComplete();
    }

    @Test
    public void fluxRepeat() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .map(String::toUpperCase)
                .repeat(2)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("ROSHAN", "MADHUSHANKA", "ALWIS", "ROSHAN", "MADHUSHANKA", "ALWIS", "ROSHAN",
                        "MADHUSHANKA", "ALWIS")
                .thenCancel()
                .verify();
    }

    @Test
    public void transformUsingFlatMap() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> Flux.fromIterable(convertToList(s))).log();
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_usingParallel() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMap(s -> s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(Flux::fromIterable)
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_usingParallelOrdered() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .concatMap(s -> s.map(this::convertToList).subscribeOn(parallel())) // Using concat map does not improve the execution time.
                .flatMap(Flux::fromIterable)
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_usingParallelOrderedOptimized() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMapSequential(s -> s.map(this::convertToList).subscribeOn(parallel())) // Improved the execution time.
                .flatMap(Flux::fromIterable)
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        return Arrays.asList(s, "new value");
    }
}
