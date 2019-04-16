package pl.potera;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class ReactorTests {

    @Test
    public void simpleFluxTest() {
        String[] numbers = new String[] {
                "one", "two", "three", "four"
        };

        Flux<String> numbersFlux = Flux.fromArray(numbers);

        StepVerifier.create(numbersFlux)
                .expectNext("one")
                .expectNext("two")
                .expectNext("three")
                .expectNext("four")
                .verifyComplete();
    }

    @Test
    public void intervalFluxTest() {
        Flux<Long> intervalFlux = Flux.interval(Duration.ofMillis(400)).take(5);

        intervalFlux.map(n -> "printing: " + n).subscribe(System.out::println);

        intervalFlux
                .reduce((aLong, aLong2) -> aLong + aLong2)
                .subscribe(sum -> System.out.println("Sum: " + sum));

        List<Long> collect = intervalFlux.collect(Collectors.toList()).block();
        collect.forEach(System.out::print);

        StepVerifier.create(intervalFlux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .expectNext(4L)
                .verifyComplete();
    }

    @Test
    public void mergeFluxesTest() {
        Flux<String> firstFlux = Flux.just("one", "two", "three")
                .delayElements(Duration.ofMillis(100));
        Flux<String> secondFlux = Flux.just("jeden", "dwa", "trzy")
                .delayElements(Duration.ofMillis(100))
                .delaySubscription(Duration.ofMillis(50));


        Flux<String> mergedFlux = Flux.merge(firstFlux, secondFlux);

        StepVerifier.create(mergedFlux)
                .expectNext("one")
                .expectNext("jeden")
                .expectNext("two")
                .expectNext("dwa")
                .expectNext("three")
                .expectNext("trzy")
                .verifyComplete();


        Flux<String> zippedFluxes = Flux.zip(firstFlux, secondFlux, (first, second) -> first + " = " + second);

        StepVerifier.create(zippedFluxes)
                .expectNext("one = jeden")
                .expectNext("two = dwa")
                .expectNext("three = trzy")
                .verifyComplete();
    }
}
