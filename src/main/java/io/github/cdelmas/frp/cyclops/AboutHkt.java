package io.github.cdelmas.frp.cyclops;

import static cyclops.control.Maybe.just;

import cyclops.control.Maybe;
import cyclops.monads.Witness;
import cyclops.typeclasses.Kleisli;
import lombok.Value;

public class AboutHkt {

    public static void main(String[] args) {
        new AboutHkt().aboutHkt();
    }

    private void aboutHkt() {
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
}
