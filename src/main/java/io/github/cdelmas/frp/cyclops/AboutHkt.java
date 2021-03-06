package io.github.cdelmas.frp.cyclops;

import com.aol.cyclops.vavr.hkt.ArrayKind;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.anyM.AnyMSeq;
import com.aol.cyclops2.types.anyM.AnyMValue;
import cyclops.async.Future;
import cyclops.companion.vavr.Arrays;
import cyclops.control.Maybe;
import cyclops.function.Fn1;
import cyclops.monads.AnyM;
import cyclops.monads.Vavr;
import cyclops.monads.VavrWitness;
import cyclops.monads.Witness;
import cyclops.monads.WitnessType;
import cyclops.typeclasses.Kleisli;
import cyclops.typeclasses.monad.Monad;
import cyclops.typeclasses.monad.MonadZero;
import io.vavr.collection.Array;
import lombok.Value;

import static cyclops.control.Maybe.just;
import static cyclops.function.Lambda.λ;

public class AboutHkt {

    public static void main(String[] args) {

        new AboutHkt().kleisli();

        new AboutHkt().anyM();

        new AboutHkt().higher();
    }

    // kleisli

    private void kleisli() {
        Kleisli<Witness.maybe, String, Address> k2 = Kleisli.of(
                Maybe.Instances.monad(),
                this::findAddress);
        Kleisli<Witness.maybe, Address, Parcel> k1 = Kleisli.of(
                Maybe.Instances.monad(),
                this::sendParcel);
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
        AnyMService<Integer, Witness.future, VavrWitness.array> service = new AnyMService<>();
        service.repository = new AnyMAsyncRepo();
        service.compute(λ(Integer::sum).apply(42))
                .forEach(a -> System.out.println("Computed " + a));
    }

    interface AnyMRepository<A, F extends WitnessType<F>, C extends WitnessType<C>> {

        AnyMValue<F, A> read();

        AnyMValue<F, AnyMSeq<C, A>> readAll();
    }

    private class AnyMService<A, F extends WitnessType<F>, C extends WitnessType<C>> {

        AnyMRepository<A, F, C> repository;

        AnyMValue<F, AnyMSeq<C, A>> compute(Fn1<A, A> mapper) {
            return repository.readAll()
                    .map(as -> as.map(mapper));
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
        Service<Integer, Witness.future, VavrWitness.array> service = new Service<>();
        service.cMonadZero = Arrays.Instances.monadZero();
        service.wMonad = Future.Instances.monad();
        service.repository = new AsyncRepo();

        final Future<Higher<VavrWitness.array, Integer>> computedValue = Future.narrowK(
                service.compute(λ(Integer::sum).apply(42)));
        computedValue.forEach(a ->
                System.out.println("Computed: " + ArrayKind.narrow(a)));
    }

    interface Repository<A, F extends WitnessType<F>, C extends WitnessType<C>> {

        Higher<F, A> read();

        Higher<F, Higher<C, A>> readAll();
    }

    private class Service<A, F extends WitnessType<F>, C extends WitnessType<C>> {

        Repository<A, F, C> repository;
        Monad<F> wMonad;
        MonadZero<C> cMonadZero;

        Higher<F, Higher<C, A>> compute(Fn1<A, A> mapper) {
            return wMonad.map(
                    integers -> cMonadZero.map(mapper, integers),
                    repository.readAll()
            );
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
