package com.webfluxtraining;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    @Test
    public void fluxUsingIterables() {
        List<String> names = Arrays.asList("Roshan", "Madhushanka", "Alwis");
        Flux<String> nameFlux = Flux.fromIterable(names);
        nameFlux.subscribe(System.out::println);
    }

    @Test
    public void fluxUsingArray() {
        String[] names = new String[]{"Roshan", "Madhushanka", "Alwis"};
        Flux<String> nameFlux = Flux.fromArray(names);
        nameFlux.subscribe(System.out::println);
    }

    @Test
    public void fluxFromStream() {
        List<String> names = Arrays.asList("Roshan", "Madhushanka", "Alwis");
        Flux<String> nameFlux = Flux.fromStream(names.stream());
        nameFlux.subscribe(System.out::println);
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5);
        StepVerifier.create(integerFlux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        Mono<Object> objectMono = Mono.justOrEmpty(null);
        StepVerifier.create(objectMono.log()).verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        Supplier<String> stringSupplier = () -> "Roshan";
        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);
        StepVerifier.create(stringMono.log()).expectNext("Roshan").verifyComplete();
    }
}
