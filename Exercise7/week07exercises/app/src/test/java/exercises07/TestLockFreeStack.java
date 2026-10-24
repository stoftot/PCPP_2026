// raup@itu.dk * 2023-10-20
package exercises07;

// Very likely you will need some imports here

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class TestLockFreeStack {
    CyclicBarrier cyclicBarrier;
    LockFreeStack<Integer> lockFreeStack;
    // The imports above are just for convenience, feel free add or remove imports
    @BeforeEach
    public void beforeEach(){
        lockFreeStack = new LockFreeStack<Integer>();
    }

    // TODO: 7.2.2 - Test push

    @RepeatedTest(1_000)
    public void testPush(){
        int n = 10;
        int x = 1_000;
        cyclicBarrier = new CyclicBarrier(n+1);
        for (int i = 0; i < n; i++){
            new Thread(() -> {
                try {
                    cyclicBarrier.await();
                    for(int j = 0; j < x; j++){
                        lockFreeStack.push(j);
                    }
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        try {
            cyclicBarrier.await();
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        int sum = 0;
        for (int i = 0; i < (n*x); i++){
            sum += lockFreeStack.pop();
        }
        assertEquals(sum,  (((x-1) * (x)) / 2) * n);
    }



    // TODO: 7.2.3 - Test pop
    @RepeatedTest(10)
    public void popTest(){
        int n = 1_000;
        cyclicBarrier = new CyclicBarrier(n+1);
        AtomicInteger ai_sum = new AtomicInteger();
        for (int i = 0; i < n; i++){
            lockFreeStack.push(i);
            new Thread(() -> {
                try {
                    cyclicBarrier.await();
                    ai_sum.addAndGet(lockFreeStack.pop());
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        try {
            cyclicBarrier.await();
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        assertEquals(ai_sum.get(), (n-1) * (n) / 2);
    }

    //part 4
    @RepeatedTest(10)
    public void popTest_empty(){
        int n = 1_000;
        cyclicBarrier = new CyclicBarrier(n+1);
        AtomicInteger ai_sum = new AtomicInteger();
        int expected_sum = 0;
        for (int i = 0; i < n; i++){
            if (i % 100 != 0) {
                lockFreeStack.push(i);
                expected_sum += i;
            }
            new Thread(() -> {
                try {
                    cyclicBarrier.await();
                    var val = lockFreeStack.pop();
                    if (val != null)
                        ai_sum.addAndGet(val);
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        try {
            cyclicBarrier.await();
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        assertEquals(ai_sum.get(), expected_sum);
    }

}
