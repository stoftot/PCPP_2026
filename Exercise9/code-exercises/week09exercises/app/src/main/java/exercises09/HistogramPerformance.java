// Various threadsafe histogram implementations and a workload
// sestoft@itu.dk * 2014-09-10, 2026-10-21

package exercises09;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.LongAdder;

class HistogramPerformance {
  public static void main(String[] args) {
    // exerciseHistogram(new SynchronizedHistogram(30));
    // exerciseHistogram(new CasHistogram(30));
    // exerciseHistogram(new AtomicHistogram(30));
    // exerciseHistogram(new StripedHistogram(30));
    // exerciseHistogram(new LongAdderHistogram(30));
  }

  public static void exerciseHistogram(Histogram histogram) {
    final int range = 50_000_000;
    final int threadCount = 64, perThread = range / threadCount;
    final Thread[] threads = new Thread[threadCount];
    Timer timer = new Timer();
    for (int t=0; t<threadCount; t++) {
      final int from = perThread * t, 
	          to = (t+1 == threadCount) ? range : perThread * (t+1); 
      threads[t] = Thread.ofVirtual().start(() -> {
	  for (int p=from; p<to; p++)
	    histogram.increment(countFactors(p));
	});
    }
    try {
      for (int t=0; t<threadCount; t++) 
	threads[t].join();
    } catch (InterruptedException exn) {
      System.out.println("Some thread was interrupted");
    }
    System.out.printf("%-30s %10.1f ms%n", histogram.getClass(), timer.check() * 1e3);
    // dump(histogram);
  }
  
  public static void dump(Histogram histogram) {
    int totalCount = 0;
    for (int bin=0; bin<histogram.getSpan(); bin++) {
      System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
      totalCount += histogram.getCount(bin);
    }
    System.out.printf("      %9d%n", totalCount);
  }

  public static int countFactors(int p) {
    if (p < 2) 
      return 0;
    int factorCount = 1, k = 2;
    while (p >= k * k) {
      if (p % k == 0) {
	factorCount++;
	p /= k;
      } else 
	k++;
    }
    return factorCount;
  }
}

class Histogram1 implements Histogram {
  private int[] counts;
  public Histogram1(int span) {
    this.counts = new int[span];
  }
  public void increment(int bin) {
    counts[bin] = counts[bin] + 1;
  }
  public int getCount(int bin) {
    return counts[bin];
  }
  public int getSpan() {
    return counts.length;
  }
  public int getAndClear(int bin) {
    int result = counts[bin];
    counts[bin] = 0;
    return result;
  }
}

// Classes to implement:

// class SynchronizedHistogram implements Histogram { ... }

// class CasHistogram implements Histogram { ... }

// class AtomicHistogram implements Histogram { ... }

// class StripedHistogram implements Histogram { ... }

// class LongAdderHistogram implements Histogram { ... }
