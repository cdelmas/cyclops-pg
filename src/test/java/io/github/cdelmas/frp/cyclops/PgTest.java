package io.github.cdelmas.frp.cyclops;

import com.aol.cyclops2.data.collections.extensions.CollectionX;
import cyclops.collections.mutable.ListX;
import cyclops.companion.CompletableFutures;
import cyclops.companion.Monoids;
import cyclops.companion.Optionals;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Try;
import cyclops.monads.Witness;
import cyclops.monads.WitnessType;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PgTest {

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
    public void additions() {
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

        Try.withCatch()

        // Maybe
        // Eval
        // Try
        // FutureW
        // transformers
    }

    public static int inc(int i) {
        System.out.println("incrementing i=" + i);
        return i + 1;
    }

    public void hkt() {
        // HKT examples
    }

    public void reactive() {
        // examples of reactive programming
    }
}
