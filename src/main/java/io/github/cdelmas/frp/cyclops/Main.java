package io.github.cdelmas.frp.cyclops;

import cyclops.async.Future;
import cyclops.async.adapters.Topic;
import cyclops.collections.mutable.ListX;
import cyclops.control.Try;
import cyclops.function.Fn1;
import cyclops.stream.ReactiveSeq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        //reactive();
        reactiveStreams();
    }

    private static Try<ListX<String>, IOException> readFile(Path path) {
        return Try.withCatch(() -> Files.readAllLines(path))
                .map(ListX::fromIterable);
    }

    private static void reactive() {
        readBatchFile()
                .map(lines -> lines.map(line -> process.apply(line)))
                .map(Future::sequence)
                .forEach(f -> f.forEach(ps -> ps.forEach(p -> System.out.println("Processed " + p))));
    }

    private static Try<ListX<String>, Exception> readBatchFile() {
        return findBatchFile()
                .flatMap(Main::readFile);
    }

    private static Try<Path, Exception> findBatchFile() {
        return Try.withCatch(() -> {
            URI data = Main.class.getResource("/data").toURI();
            return Paths.get(data);
        });
    }

    private static Fn1<String, Future<Parcel>> process = input -> {

//        return Future.narrowK(Comprehensions.of(Future.Instances.monad())
//                .forEach2(
//                                  locate(input),
//                        __     -> computePrice(input),
//                        (a, p) -> new Parcel(p, a)));

        /* **** OU ********/
        Future<Price> price = computePrice(input);
        Future<Address> address = locate(input);
        return price.flatMap(p -> address.map(a -> new Parcel(p, a)));
    };

    @AllArgsConstructor
    @Getter
    @ToString
    private static class Parcel {
        private Price price;
        private Address address;
    }

    @AllArgsConstructor
    @ToString
    private static class Price {
        @Getter
        private long price;
    }

    @AllArgsConstructor
    @ToString
    private static class Address {
        @Getter
        private String address;
    }

    private static Future<Address> locate(String id) {
        return Future.of(() -> locateEager(id));
    }

    private static Address locateEager(String id) {
        System.out.println("Locating " + id);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
        }
        return new Address("Address$" + id);
    }


    private static Future<Price> computePrice(String id) {
        return Future.of(() -> computePriceEager(id));
    }

    private static Price computePriceEager(String id) {
        System.out.println("Computing price");
        return new Price(Long.parseLong(id) / 1000000);
    }

    /******************************************/
    /******************************************/
    /******************************************/
    /******************************************/
    /******************************************/


    private static void reactiveStreams() {
        System.out.println("REACTIVE");
        ExecutorService mainEs = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        readBatchFile()
                .forEach(content -> {
                    ReactiveSeq<String> idStream = content.stream();
                    Topic<String> topic = idStream.broadcast();
                    ReactiveSeq<String> notifs = topic.stream();
                    ReactiveSeq<String> process = topic.stream();
                    ReactiveSeq<Parcel> parcels = process.fanOutZipIn(
                            seq -> seq.map(Main::locateEager),
                            seq -> seq.map(Main::computePriceEager),
                            (a, p) -> new Parcel(p,a));
                    mainEs.submit(() -> notifs.forEach(x -> System.out.println("notif to customer for " + x)));
                    mainEs.submit(() -> parcels.forEach(System.out::println));
                });
    }

    /* ***************************/
    /* ***************************/
    /* ***************************/
    /* ***************************/

    private static void integrations() {
        // Use Vavr Try as a monad

        // create a Flux from a SortedSetX

        // create an Observable from a ReactiveSeq
    }
}
