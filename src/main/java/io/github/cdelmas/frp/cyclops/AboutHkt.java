package io.github.cdelmas.frp.cyclops;

import static cyclops.control.Maybe.just;
import static cyclops.function.Lambda.λ;

import com.aol.cyclops.vavr.hkt.ArrayKind;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.anyM.AnyMSeq;
import com.aol.cyclops2.types.anyM.AnyMValue;
import cyclops.async.Future;
import cyclops.companion.vavr.Arrays;
import cyclops.control.Maybe;
import cyclops.monads.AnyM;
import cyclops.monads.Vavr;
import cyclops.monads.VavrWitness;
import cyclops.monads.Witness;
import cyclops.monads.WitnessType;
import cyclops.typeclasses.Kleisli;
import cyclops.typeclasses.monad.Monad;
import cyclops.typeclasses.monad.MonadPlus;
import cyclops.typeclasses.monad.MonadZero;
import io.vavr.collection.Array;
import lombok.Value;

public class AboutHkt {

    public static void main(String[] args) {

        new AboutHkt().kleisli();

        new AboutHkt().anyM();

        new AboutHkt().higher();
    }

    // kleisli

    private void kleisli() {
        Kleisli<Witness.maybe, String, Address> k2 = Kleisli.of(Maybe.Instances.monad(), this::findAddress);
        Kleisli<Witness.maybe, Address, Parcel> k1 = Kleisli.of(Maybe.Instances.monad(), this::sendParcel);
        Maybe<Parcel> maybeAParcel = Maybe.narrowK(k2.then(k1).apply("Toto"));
        maybeAParcel.forEach(System.out::println);
    }

    // String -> Maybe Address
    private Maybe<Address> findAddress(String name) {
        return just(new Address(name + "@ 1337 n00b Road, Geek City"));
    }

    // :: Address -> Maybe Parcel
    private Maybe<Parcel> sendParcel(Address address) {
        return just(new Parcel(address));
    }

    @Value
    private static class Address {
        String data;
    }

    @Value
    private static class Parcel {
        Address address;
    }

    // AnyM

    private void anyM() {
        AnyMService<Witness.future, VavrWitness.array> service = new AnyMService<>();
        service.repository = new AnyMAsyncRepo();
        service.compute().forEach(a -> System.out.println("Computed " + a));
    }

    interface AnyMRepository<A, W extends WitnessType<W>, C extends WitnessType<C>> {

        AnyMValue<W, A> read();

        AnyMValue<W, AnyMSeq<C, A>> readAll();
    }

    private class AnyMService<W extends WitnessType<W>, C extends WitnessType<C>> {

        AnyMRepository<Integer, W, C> repository;

        AnyMValue<W, AnyMSeq<C, Integer>> compute() {
            return repository.readAll()
                    .map(integers -> integers.map(λ(Integer::sum).apply(42)))
                    .map(integers -> integers.filter(x -> x % 2 == 1));
        }
    }

    class AnyMAsyncRepo implements AnyMRepository<Integer, Witness.future, VavrWitness.array> {

        @Override
        public AnyMValue<Witness.future, Integer> read() {
            return AnyM.fromFuture(Future.ofResult(42));
        }

        @Override
        public AnyMValue<Witness.future, AnyMSeq<VavrWitness.array, Integer>> readAll() {
            return AnyM.fromFuture(Future.ofResult(Vavr.array(Array.of(42, 33, 25))));
        }
    }

    // Higher

    private void higher() {
        Service<Witness.future, VavrWitness.array> service = new Service<>();
        service.cMonadZero = Arrays.Instances.monadZero();
        service.cMonadPlus = Arrays.Instances.monadPlus();
        service.wMonad = Future.Instances.monad();
        service.repository = new AsyncRepo();

        final Future<Higher<VavrWitness.array, Integer>> computedValue = Future.narrowK(service.compute());
        computedValue.forEach(a -> System.out.println("Computed: " + ArrayKind.narrow(a)));
    }

    interface Repository<A, W extends WitnessType<W>, C extends WitnessType<C>> {

        Higher<W, A> read();

        Higher<W, Higher<C, A>> readAll();
    }

    private class Service<W extends WitnessType<W>, C extends WitnessType<C>> {

        Repository<Integer, W, C> repository;
        Monad<W> wMonad;
        MonadZero<C> cMonadZero;
        MonadPlus<C> cMonadPlus;

        Higher<W, Higher<C, Integer>> compute() {
            final Higher<W, Higher<C, Integer>> allValues = repository.readAll();
            System.out.println("all values: " + allValues);
            return wMonad.map(
                    integers -> {
                        final Higher<C, Integer> mapped = cMonadZero.map(λ(Integer::sum).apply(42),
                                integers);
                        System.out.println("After map: " + mapped);
                        final Higher<C, Integer> filtered = cMonadPlus.filter(x -> x % 2 == 1, mapped);
                        System.out.println("After filter and map with MonadPlus: " + filtered);
                        final Higher<C, Integer> list = cMonadZero.filter(x -> x % 2 == 1, mapped);
                        System.out.println("After filter and map with MonadZero: " + list);
                        return list;
                    },
                    allValues);
        }
    }

    class AsyncRepo implements Repository<Integer, Witness.future, VavrWitness.array> {

        @Override
        public Higher<Witness.future, Integer> read() {
            return Future.ofResult(42);
        }

        @Override
        public Higher<Witness.future, Higher<VavrWitness.array, Integer>> readAll() {
            return Future.ofResult(ArrayKind.widen(Array.of(123, 4332, 37289)));
        }
    }
}
