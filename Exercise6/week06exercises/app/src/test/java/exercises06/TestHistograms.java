// For week 6
// raup@itu.dk * 2026-09-23

package exercises06;

// Very likely you will need some imports here

public class TestHistograms {
    // The imports above are just for convenience, feel free add or remove imports

    // TODO: 6.1.3





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
