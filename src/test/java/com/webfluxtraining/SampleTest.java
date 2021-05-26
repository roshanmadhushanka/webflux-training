package com.webfluxtraining;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class SampleTest {

    @Test
    public void testFlux() {
        Flux<String> flux = Flux.just("Hello", "World", "Roshan");
        flux.subscribe(System.out::println);
    }

    @Test
    public void fluxErrorHandling1() {
        Flux<String> flux = Flux.just("Hello", "World", "Roshan").concatWith(Mono.error(new RuntimeException("Test Exception")));
        flux.subscribe(System.out::println, e -> {
            System.err.println(e.getMessage());
        });
    }

    @Test
    public void fluxErrorHandling2() {
        Flux<Integer> flux = Flux.just(3, 2, 1, 0);
        flux.map(e -> 6 / e).subscribe(System.out::println, e -> {
            System.err.println(e.getMessage());
        });
    }

    @Test
    public void fluxErrorHandling3() {
        Flux<String> flux = Flux.just("Hello", "World", "Roshan")
                .concatWith(Mono.error(new RuntimeException("Test Exception")))
                .concatWith(Flux.just("After Error"))
                .log();
        flux.subscribe(System.out::println, e -> {
            System.err.println(e.getMessage());
        });
    }

    @Test
    public void fluxErrorHandling4() {
        Flux<String> flux = Flux.just("Hello", "World", "Roshan")
                .concatWith(Mono.error(new RuntimeException("Test Exception")))
                .concatWith(Flux.just("After Error"))
                .log();
        flux.subscribe(System.out::println, e -> {
            System.err.println(e.getMessage());
        }, () -> System.out.println("Completed"));
    }

    @Test
    public void fluxTestElements_whenNoExceptionOccurs() {
        Flux<String> flux = Flux.just("Hello", "World", "Roshan");
        StepVerifier.create(flux)
                .expectNext("Hello")
                .expectNext("World")
                .expectNext("Roshan")
                .verifyComplete();
    }

    @Test
    public void fluxTestElements_whenExceptionOccurs() {
        Flux<String> flux = Flux.just("Hello", "World", "Roshan").concatWith(Flux.error(new RuntimeException("Test Exception")));
        StepVerifier.create(flux)
                .expectNext("Hello")
                .expectNext("World")
                .expectNext("Roshan")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void fluxTestElementsCount_whenNoExceptionOccurs() {
        Flux<String> flux = Flux.just("Hello", "World", "Roshan");
        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void monoElementTest_whenNoExceptionOccurs() {
        Mono<String> stringMono = Mono.just("Spring");
        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoElementTest_whenExceptionOccurs() {
        StepVerifier.create(Mono.error(new RuntimeException("Test Exception")))
                .expectError(RuntimeException.class)
                .verify();
    }
}
