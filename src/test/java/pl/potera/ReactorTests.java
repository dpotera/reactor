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
    }

    @Test
    public void zipFluxesTest() {
        Flux<String> firstFlux = Flux.just("one", "two", "three");
        Flux<String> secondFlux = Flux.just("jeden", "dwa", "trzy");

        Flux<String> zippedFluxes = Flux.zip(firstFlux, secondFlux, (first, second) -> first + " = " + second);

        StepVerifier.create(zippedFluxes)
                .expectNext("one = jeden")
                .expectNext("two = dwa")
                .expectNext("three = trzy")
                .verifyComplete();
    }

    @Test
    public void firstFluxTest() {
        Flux<String> fastFlux = Flux.just("fast", "x").delaySubscription(Duration.ofMillis(10));
        Flux<String> slowFlux = Flux.just("slow", "y").delaySubscription(Duration.ofMillis(500));

        Flux<String> firstFlux = Flux.first(fastFlux, slowFlux);

        StepVerifier.create(firstFlux)
                .expectNext("fast")
                .expectNext("x")
                .verifyComplete();
    }

    @Test
    public void takeFluxTest() {
        Flux<String> flux = Flux.just("1", "2", "3", "4", "5").take(3);

        StepVerifier.create(flux)
                .expectNext("1")
                .expectNext("2")
                .expectNext("3")
                .verifyComplete();

        flux = Flux.just("1", "2", "3", "4", "5")
                .delayElements(Duration.ofMillis(20))
                .take(Duration.ofMillis(70));

        StepVerifier.create(flux)
                .expectNext("1")
                .expectNext("2")
                .expectNext("3")
                .verifyComplete();
    }

    @Test
    public void filterFluxExample() {
        Flux<Integer> flux = Flux.range(0, 5)
                .filter(integer -> integer % 2 == 0);

        StepVerifier.create(flux)
                .expectNext(0)
                .expectNext(2)
                .expectNext(4)
                .verifyComplete();
    }

    @Test
    public void distinctFluxExample() {
        Flux<Integer> flux = Flux.just(1, 2, 3, 1, 4, 5, 6, 2).distinct();

        StepVerifier.create(flux).expectNext(1, 2, 3, 4, 5, 6).verifyComplete();
    }

    @Test
    public void mapFlux() {
        Flux<String> stringFlux = Flux.just("1", "2", "3");

        Flux<Integer> intFlux = stringFlux.map(Integer::parseInt);

        StepVerifier.create(intFlux).expectNext(1, 2, 3).verifyComplete();

        Flux<Long> longFlux = intFlux.map(number -> new Long(number + number));

        StepVerifier.create(longFlux).expectNext(2L, 4L, 6L).verifyComplete();
    }
}
