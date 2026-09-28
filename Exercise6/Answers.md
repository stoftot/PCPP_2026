# Exercise 6.1 

## 6.1.1

```java 

package exercises06;

import java.util.concurrent.atomic.AtomicInteger;

public class CasHistogram implements Histogram {
    private final AtomicInteger[] counts;

    CasHistogram(int span) {
        this.counts = new AtomicInteger[span];
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

```

Class State does not escape 
The class state does not escape since a reference to the counts array is never returned or accessible outside the class


Safe Publication 
This is ensured by making the counts field final 


## 6.1.2 Does getAndClear() operate atomically? 

Yes since it uses compareAndSet to verify the state before setting it to 0 and returning the value

