    package exercises06;

    import java.util.Arrays;
    import java.util.concurrent.atomic.AtomicInteger;

    public class CasHistogram implements Histogram {
        private final AtomicInteger[] counts;

        public CasHistogram(int span) {
            counts = new AtomicInteger[span];
            Arrays.setAll(counts, _ -> new AtomicInteger(0));
        }

        @Override
        public void increment(int bin) {
            int old_val;
            int new_val;
            do {
                old_val = counts[bin].get();
                new_val = old_val + 1;
            } while (!counts[bin].compareAndSet(old_val, new_val));
        }

        @Override
        public int getCount(int bin) {
            return counts[bin].get();
        }

        @Override
        public int getSpan() {
            return counts.length;
        }

        @Override
        public int getAndClear(int bin) {
            int old_val;
            do {
                old_val = counts[bin].get();
            } while (!counts[bin].compareAndSet(old_val, 0));
            return old_val;
        }
    }
