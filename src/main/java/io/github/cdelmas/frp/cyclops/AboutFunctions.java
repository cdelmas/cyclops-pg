package io.github.cdelmas.frp.cyclops;

import com.aol.cyclops2.types.mixins.Printable;
import cyclops.collections.mutable.SetX;
import cyclops.function.FluentFunctions;
import cyclops.function.FluentFunctions.FluentFunction;
import cyclops.function.Fn1;
import cyclops.function.Fn2;
import cyclops.function.Lambda;
import lombok.val;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.aol.cyclops2.types.mixins.AsMappable.asMappable;
import static cyclops.function.Lambda.λ;

public class AboutFunctions {

    public static void main(String[] args) {
        new AboutFunctions().fn();

        new AboutFunctions().fluent();
    }

    private void fn() {
        SetX.of(3, 4, 5, 6, 7, 12, 23)
                .map(i -> Integer.sum(i, 3))
                .forEach(i -> System.out.println(i));

        BiFunction<Integer,Integer,Integer> sumJ = λ(Integer::sum);

        Fn2<Integer, Integer, Integer> sum = λ(Integer::sum);
    }

    private void fluent() {
        FluentFunction<Integer, Integer> f = FluentFunctions.ofChecked((Integer x) -> {
            if (x < 0) throw new IllegalArgumentException("x<0");
            else return x + 1;
        }).recover(IllegalArgumentException.class, i -> 42);

        Integer res = f.apply(3); // 4
        Integer universalAnswer = f.apply(-12); // 42
    }

}
