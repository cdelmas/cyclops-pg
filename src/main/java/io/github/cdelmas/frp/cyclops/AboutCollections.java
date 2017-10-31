package io.github.cdelmas.frp.cyclops;

import cyclops.collections.mutable.ListX;
import cyclops.companion.Monoids;


public class AboutCollections {

    public static void main(String[] args) {
        new AboutCollections().aboutCollections();
    }

    private void aboutCollections() {
        ListX<Integer> ints = ListX.of(0, 1, 2, 3, 4, 5);
        final String str = ints
                .map(i -> Integer.toString(i))
                .filterNot(s -> s.contains("3"))
                .reverse()
                .reduce(Monoids.stringConcat);
        System.out.println(str);

        ints.forEach2(
                i      -> ListX.range(0, i),
                (i, j) -> i.toString() + "#" + j.toString()
        ).forEach(System.out::println);
        /*
          Scala equivalent

          for {
            i <- ints
            j <- 0 to i
          } yield (i.toString ++ "#" ++ j.toString)

         */

        ints.visit((i, __) -> i, () -> 42); // head
    }
}
