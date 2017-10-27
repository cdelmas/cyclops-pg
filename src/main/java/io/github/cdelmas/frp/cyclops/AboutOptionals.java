package io.github.cdelmas.frp.cyclops;

import java.util.Optional;

import com.aol.cyclops2.data.collections.extensions.CollectionX;
import cyclops.collections.mutable.ListX;
import cyclops.companion.Monoids;

public class AboutOptionals {

    public static void main(String[] args) {
        new AboutOptionals().aboutOptionals();
    }

    private void aboutOptionals() {
        CollectionX<Optional<String>> opts = ListX.of(Optional.of("Hello"),
                Optional.of("World"),
                Optional.empty());
        Optional<String> result = cyclops.companion.Optionals.accumulatePresent(Monoids.stringConcat, opts);
        System.out.println("Accumulate");
        result.ifPresent(System.out::println);

        Optional<ListX<String>> sequence = cyclops.companion.Optionals.sequencePresent(opts);
        System.out.println("Sequence");
        sequence.ifPresent(System.out::println);

        int l = cyclops.companion.Optionals.visit(Optional.of("AERT"),
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
        Optional<String> combined = cyclops.companion.Optionals.combine(s1, s2, Monoids.stringConcat);
        combined.ifPresent(System.out::println);
    }
}
