// For week 6
// raup@itu.dk * 2026-09-23

package exercises06;

// Very likely you will need some imports here

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHistograms {
    // The imports above are just for convenience, feel free add or remove imports

    // TODO: 6.1.3
    @Test
    public void testCasHistogram() throws InterruptedException {
        final int RANGE = 5000;
        final int SPAN = 20;


        Histogram casHistogram = new CasHistogram(SPAN);
        Thread[] threads = new Thread[RANGE];
        for (int i = 0; i < RANGE; i++) {
            final int number = i;
            threads[i] = new Thread(() -> {
                int factors = countFactors(number);
                casHistogram.increment(factors); }
            );
            threads[i].start();
        }

        for (Thread thread : threads) { thread.join(); }

        Histogram histogram1 = new Histogram1(SPAN);
        for (int i = 0; i < RANGE; i++) {
            int factors = countFactors(i);
            histogram1.increment(factors);
        }

        for (int bin = 0; bin < SPAN; bin++) {
            assertEquals(
                    histogram1.getCount(bin),
                    casHistogram.getCount(bin),
                    "Incorrect count in bin " + bin );
        }
    }

    // Function to count the number of prime factors of a number `p`
    private static int countFactors(int p) {
        if (p < 2) return 0;
        int factorCount = 1, k = 2;
        while (p >= k * k) {
            if (p % k == 0) {
                factorCount++;
                p= p/k;
            } else
                k= k+1;
        }
        return factorCount;
    }

}
