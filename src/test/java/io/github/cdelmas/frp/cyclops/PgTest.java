package io.github.cdelmas.frp.cyclops;

import com.aol.cyclops2.data.collections.extensions.CollectionX;
import com.aol.cyclops2.hkt.Higher;
import cyclops.async.Future;
import cyclops.collections.mutable.ListX;
import cyclops.companion.CompletableFutures;
import cyclops.companion.Monoids;
import cyclops.companion.Optionals;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Try;
import cyclops.control.Xor;
import cyclops.control.lazy.Either;
import cyclops.function.Fn1;
import cyclops.function.Reducer;
import cyclops.monads.AnyM;
import cyclops.monads.Witness;
import cyclops.monads.transformers.MaybeT;
import cyclops.typeclasses.Kleisli;
import cyclops.typeclasses.free.Coyoneda;
import cyclops.typeclasses.free.Free;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static cyclops.control.Maybe.just;
import static cyclops.function.Lambda.λ;
import static java.util.function.Function.identity;

public class PgTest {

    public static int inc(int i) {
        System.out.println("incrementing i=" + i);
        return i + 1;
    }

    @Test
    public void aboutCollections() {
        ListX<Integer> ints = ListX.of(1, 2, 33, 4, 3, 5, 6, 2, 3, 6, 43, 10);
        ints
                .map(i -> Integer.toString(i))
                .filter(s -> s.contains("3"))
                .reverse()
                .forEach(System.out::println);

        ints.forEach2(i -> ListX.range(0, i),
                (i, j) -> i.toString() + "#" + j.toString())

                .forEach(System.out::println);
        /*
          Scala equivalent

          for {
            i <- ints
            j <- 0 to i
          } yield (i.toString ++ "#" ++ j.toString)

         */

        ints.visit((i, __) -> i, () -> 42); // head
    }

    @Test
    public void aboutOptionals() {
        CollectionX<Optional<String>> opts = ListX.of(Optional.of("Hello"),
                Optional.of("World"),
                Optional.empty());
        Optional<String> result = Optionals.accumulatePresent(Monoids.stringConcat, opts);
        System.out.println("Accumulate");
        result.ifPresent(System.out::println);

        Optional<ListX<String>> sequence = Optionals.sequencePresent(opts);
        System.out.println("Sequence");
        sequence.ifPresent(System.out::println);

        int l = Optionals.visit(Optional.of("AERT"),
                String::length,
                () -> 42);
        /*
            Scala equivalent
            Some("AERT") match {
              case Some(s) => s.length
              case _ => 42
            }
        */

        Optional<String> s1 = Optional.of("Rest");
        Optional<String> s2 = Optional.of("Best");
        Optional<String> combined = Optionals.combine(s1, s2, Monoids.stringConcat);
        combined.ifPresent(System.out::println);
    }

    @Test
    public void aboutFutures() {
        CompletableFuture<Integer> total = CompletableFutures.accumulate(Monoids.intSum,
                ListX.of(CompletableFuture.completedFuture(4),
                        CompletableFuture.completedFuture(-16),
                        CompletableFuture.completedFuture(1),
                        CompletableFuture.completedFuture(10)));
        total.thenAccept(System.out::println);
    }

    @Test
    public void monads() {
        int value = 42;
        System.out.println("Optional is eager");
        Optional.of(value).map(PgTest::inc);
        System.out.println("You should see this message after the inc message");
        System.out.println("Maybe is lazy");
        Maybe<Integer> maybeInc = Maybe.of(value).map(PgTest::inc);
        System.out.println("You should see this message before the inc message");
        maybeInc.forEach(System.out::println);

        Eval<Integer> nw = Eval.now(42);// now, once and for all
        Eval.later(() -> 42); // on use, once and for all
        Eval.always(() -> 42); // on each use of data

        Try.withCatch(this::aMethodThatThrowsAnException)
                .recover(() -> "42")
                .map(String::toUpperCase)
                .forEach(System.out::println);
        Try.catchExceptions(Exception.class)
                .init(() -> new BufferedReader(new FileReader("/tmp/toto")))
                .tryWithResources(BufferedReader::lines)
                .reduce(Monoids.combineStream())
                .forEach(System.out::println);

        // Either
        Either<String, Integer> iOrS = Either.rightEval(Eval.later(() -> 42));
        iOrS.map(λ(Integer::sum).apply(12)).forEach(System.out::println);

        // Xor
        Xor<String, Integer> xor = Xor.primary(42);
        xor.map(λ(Integer::divideUnsigned).apply(5)).forEach(System.out::println);

        // Future
        System.out.println(Future.sequence(ListX.of(Future.ofResult(42), Future.ofResult(17), Future.of(() -> 35)))
                .mapReduce(Reducer.fromMonoid(Monoids.listXConcat(), identity())));


        Future<Maybe<String>> delivery =
                Future.of(just(42))
                        .map(this::findUser)
                        .flatMap(opt -> Future.ofResult(opt.flatMap(this::findAddress)))
                        .flatMap(opt -> Future.ofResult(opt.flatMap(this::sendParcel)));
        delivery.get().forEach(System.out::println);

        // transformers
        MaybeT.fromAnyM(AnyM.fromFuture(Future.of(just(42))))
                .flatMapT(wrapT(this::findUser))
                .flatMapT(wrapT(this::findAddress))
                .flatMapT(wrapT(this::sendParcel))
                .forEach(System.out::println);
    }

    private <T> Fn1<T, MaybeT<Witness.future, String>> wrapT(Fn1<T, Maybe<String>> f) {
        return i -> MaybeT.fromAnyM(AnyM.fromFuture(Future.of(f.apply(i))));
    }

    private Maybe<String> sendParcel(String s) {
        return just("parcel sent to " + s);
    }

    private Maybe<String> findAddress(String name) {
        return just("1337 n00b Road, Geek City");
    }

    private Maybe<String> findUser(Integer id) {
        return Maybe.of("User#" + id);
    }

    private String aMethodThatThrowsAnException() {
        throw new RuntimeException("You cannot say I didn't tell you!");
    }

    public void hkt() {
        Kleisli<Witness.maybe, String, String> k1 = Kleisli.of(Maybe.Instances.monad(), this::sendParcel);
        Kleisli<Witness.maybe, String, String> k2 = Kleisli.of(Maybe.Instances.monad(), this::findAddress);
        Maybe<String> maybeAString = Maybe.narrowK(k2.then(k1).apply("Toto"));
        System.out.println(maybeAString.orElse("Youpi"));

        // free monad
        Request<Integer> ri = new Re
        Coyoneda.of(c -> 42, Higher<F, T> higher);

    }

    public void reactive() {
        // examples of reactive programming
    }

    // free monad example

    interface Service<T> {

    }

    @AllArgsConstructor
    final static class Tweet {
        @Getter
        private Integer userId;
        @Getter
        private String msg;
    }

    @AllArgsConstructor
    final static class User {
        @Getter
        private Integer userId;
        @Getter
        private String name;
        @Getter
        private String photo;
    }

    @AllArgsConstructor
    final static class GetTweets implements Service<List<Tweet>> {
        @Getter
        private Integer userId;
    }

    @AllArgsConstructor
    final static class GetUserName implements Service<String> {
        @Getter
        private Integer userId;
    }

    @AllArgsConstructor
    final static class GetUserPhoto implements Service<String> {
        @Getter
        private Integer userId;
    }

    abstract static class Request<T> implements Higher<Request.μ, T> {

        static class μ {
        }

        static <T> Request<T> narrowK(Higher<Request.μ, T> higher) {
            return (Request<T>) higher;
        }
    }
    
    final static class Pure<T> extends Request<T> {
        @Getter
        private T t;

        Pure(T t) {
            this.t = t;
        }
    }

    @AllArgsConstructor
    final static class Fetch<T> extends Request<T> {
        @Getter
        private Service<T> service;
    }
    /*
    type Requestable[A] = Coyoneda[Request, A] // this is described below

    static class RequestModule {
        static <T> Free<Coyoneda<>, T> pure(T t) {
            Free.liftF(, Coyoneda.of(Pure::new));
        }
                Free.liftFC(Pure(a) : Request[A])

        def fetch[A](service: Service[A]): Free[Requestable, A] =
                Free.liftFC(Fetch(service) : Request[A])
    }
    */
}
