package io.github.cdelmas.frp.cyclops;

import java.util.Arrays;

import cyclops.function.Fn1;

public class AboutMemoize {

    public static void main(String[] args) {
        new AboutMemoize().aboutMemoize();
    }

    private void aboutMemoize() {
        System.out.println("Computing fibos using classic fibo");
        long start = System.currentTimeMillis();
        long x1 = fibo(7000000L);
        long x2 = fibo(5000000L);
        System.out.println("Result obtained within " + (System.currentTimeMillis() - start) + " ms: " + Arrays.asList(x1, x2));

        System.out.println("Computing fibos using memoized fibo");
        start = System.currentTimeMillis();
        x1 = mFibo.apply(7000000L);
        x2 = mFibo.apply(5000000L);
        System.out.println("Result obtained within " + (System.currentTimeMillis() - start) + " ms: " + Arrays.asList(x1, x2));
    }

    private Fn1<Long, Long> mFibo = cyclops.function.Memoize.memoizeFunction(this::fibo);

    private long fibo(long n) {
        int m = 1;
        long fibPrev = 0;
        long fibCurrent = 1;
        while (n != m) {
            m = m + 1;
            long current = fibCurrent;
            fibCurrent = fibPrev + fibCurrent;
            fibPrev = current;
        }
        return fibCurrent;
    }
}

