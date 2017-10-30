package io.github.cdelmas.frp.cyclops;


import java.util.concurrent.Executors;
import java.util.stream.Stream;

import cyclops.VavrConverters;
import cyclops.collections.mutable.ListX;
import cyclops.collections.mutable.SortedSetX;
import cyclops.companion.rx2.Observables;
import cyclops.monads.Vavr;
import cyclops.stream.ReactiveSeq;
import cyclops.stream.Spouts;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.vavr.collection.List;
import io.vavr.collection.Vector;

public class AboutIntegrations {

    public static void main(String[] args) {
        new AboutIntegrations().vavr();

        new AboutIntegrations().rxJava();
    }

    private void vavr() {
        // cyclops -> vavr
        final Vector<Integer> vavrVector = SortedSetX.of(1, 3, 5, 7, 7, 8, 9)
                .filter(x -> x % 2 == 1)
                .to(VavrConverters::Vector);
        System.out.println(vavrVector);

        // vavr -> cyclops
        List<String> vavrList = List.of("hello", "my", "name", "is", "bob");
        final ListX<String> cyclopsList = Vavr.list(vavrList).toListX();
        System.out.println(cyclopsList);
    }

    private void rxJava() {
        // cyclops -> rxjava
        final Stream<Integer> infinite = Stream.generate(() -> 42);

        final ReactiveSeq<Integer> asyncNo = Spouts.async(infinite, Executors.newFixedThreadPool(3));

        final Double result = Observables.fromStream(asyncNo)
                .map(x -> x * 0.3230284)
                .toFlowable(BackpressureStrategy.BUFFER)
                .blockingFirst();
        System.out.println(result);

        // rxjava -> cyclops

        final Observable<Integer> integerObservable = Observable.fromCallable(() -> 42);
        final Integer theInt = Observables.reactiveSeq(integerObservable).firstValue();
        System.out.println(theInt);
    }
}
