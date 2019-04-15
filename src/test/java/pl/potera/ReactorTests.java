package pl.potera;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

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
    
}
