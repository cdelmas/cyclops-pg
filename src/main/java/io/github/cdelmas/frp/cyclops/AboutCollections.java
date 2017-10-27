package io.github.cdelmas.frp.cyclops;

import cyclops.collections.mutable.ListX;


public class AboutCollections {

    public static void main(String[] args) {
        new AboutCollections().aboutCollections();
    }

    private void aboutCollections() {
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
}
