package pl.potera.reactor;

import reactor.core.publisher.Mono;

public class Main {

    public static void main(String[] args) {
        String name = "Dominik";
        String capitalName = name.toUpperCase();
        String greeting = "Hello " + capitalName + "!";
        System.out.println(greeting);


        Mono.just("Dominik")
                .map(String::toUpperCase)
                .map(str -> "Hello " + str + "!")
                .subscribe(System.out::println);
    }
}
