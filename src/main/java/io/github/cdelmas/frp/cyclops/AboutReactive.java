package io.github.cdelmas.frp.cyclops;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.out;

import cyclops.async.Future;
import cyclops.async.adapters.Topic;
import cyclops.collections.mutable.ListX;
import cyclops.control.Try;
import cyclops.function.Fn1;
import cyclops.stream.ReactiveSeq;
import lombok.Value;

public class AboutReactive {

    public static void main(String[] args) {
        new AboutReactive().reactive();

        new AboutReactive().reactiveStreams();
    }

    private Try<ListX<String>, IOException> readFile(Path path) {
        return Try.withCatch(() -> Files.readAllLines(path))
                .map(ListX::fromIterable);
    }

    private void reactive() {
        readBatchFile()
                .map(lines -> lines.map(line -> process.apply(line)))
                .map(Future::sequence)
                .forEach(f -> f.forEach(ps -> ps.forEach(p -> out.println("Processed " + p))));
    }

    private Try<ListX<String>, Exception> readBatchFile() {
        return findBatchFile()
                .flatMap(this::readFile);
    }

    private Try<Path, Exception> findBatchFile() {
        return Try.withCatch(() -> {
            URI data = this.getClass().getResource("/data").toURI();
            return Paths.get(data);
        });
    }

    private Fn1<String, Future<Parcel>> process = input -> {

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

    @Value
    private static class Parcel {
        Price price;
        Address address;
    }

    @Value
    private static class Price {
        long price;
    }

    @Value
    private static class Address {
        String address;
    }

    private Future<Address> locate(String id) {
        return Future.of(() -> locateEager(id));
    }

    private Address locateEager(String id) {
        out.println("Locating " + id);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
        }
        return new Address("Address$" + id);
    }

    private Future<Price> computePrice(String id) {
        return Future.of(() -> computePriceEager(id));
    }

    private Price computePriceEager(String id) {
        out.println("Computing price");
        return new Price(Long.parseLong(id) / 1000000);
    }

    private void reactiveStreams() {
        out.println("REACTIVE STREAMS");
        ExecutorService mainEs = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        readBatchFile()
                .forEach(content -> {
                    ReactiveSeq<String> idStream = content.stream();
                    Topic<String> topic = idStream.broadcast();
                    ReactiveSeq<String> notifs = topic.stream();
                    ReactiveSeq<String> process = topic.stream();
                    ReactiveSeq<Parcel> parcels = process.fanOutZipIn(
                            seq -> seq.map(this::locateEager),
                            seq -> seq.map(this::computePriceEager),
                            (a, p) -> new Parcel(p, a));
                    mainEs.submit(() -> notifs.forEach(x -> out.println("notif to customer for " + x)));
                    mainEs.submit(() -> parcels.forEach(out::println));
                });
    }
}
