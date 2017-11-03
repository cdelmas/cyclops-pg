package io.github.cdelmas.frp.cyclops;

import java.util.Optional;

import static cyclops.function.Lambda.λ;

import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Xor;
import cyclops.control.lazy.Either;
import cyclops.function.Fn1;

public class AboutMonads {

    public static void main(String[] args) {
        new AboutMonads().aboutMonads();
    }

    private void aboutMonads() {
        maybe();
        eval();
        either();
        xor();
    }

    private void xor() {
        Xor<String, Integer> xor = Xor.primary(42);
        xor.map(i -> Integer.divideUnsigned(5, i))
                .forEach(System.out::println);
    }

    private void either() {
        Either<String, Integer> iOrS = Either.rightEval(Eval.later(() -> 42));
        iOrS.map(i -> i + 12)
                .forEach(System.out::println);
    }

    private void eval() {
        Eval<Integer> nw = Eval.now(42);// now, once and for all
        Eval.later(() -> 42); // on use, once and for all
        Eval.always(() -> 42); // on each use of data
    }

    private void maybe() {
        int value = 42;
        System.out.println("Optional is eager");
        Optional.of(value).map(inc);
        System.out.println("You should see this message after the inc message");
        System.out.println("Maybe is lazy");
        Maybe<Integer> maybeInc = Maybe.of(value).map(inc);
        System.out.println("You should see this message before the inc message");
        maybeInc.forEach(System.out::println);
    }

    private Fn1<Integer, Integer> inc = (i) -> {
        System.out.println("incrementing i=" + i);
        return i + 1;
    };
}
