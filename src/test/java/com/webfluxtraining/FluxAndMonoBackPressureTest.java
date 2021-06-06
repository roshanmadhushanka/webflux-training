package com.webfluxtraining;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void blackPressureTest() {

        Flux<Integer> integerFlux = Flux.range(1, 10);

        StepVerifier.create(integerFlux.log())
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {

        Flux<Integer> integerFlux = Flux.range(1, 10).log();
        integerFlux.subscribe(System.out::println,
                System.err::println,
                () -> System.out.println("Done"),
                (subscription) -> subscription.request(2));
    }

    @Test
    public void backPressure_withCancel() {

        Flux<Integer> integerFlux = Flux.range(1, 10).log();
        integerFlux.subscribe(System.out::println,
                System.err::println,
                () -> System.out.println("Done"),
                Subscription::cancel);

    }

    @Test
    public void customizedBackPressure() {
        Flux<Integer> integerFlux = Flux.range(1, 10).log();
        integerFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
//                request(2);
                System.out.println("Value received " + value);
                if (value == 4) {
                    cancel();
                }
            }
        });
    }
}