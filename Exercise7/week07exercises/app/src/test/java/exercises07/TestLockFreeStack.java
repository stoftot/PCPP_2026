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

    @RepeatedTest(1000)
    void TestLockFreePush() throws InterruptedException {
        LockFreeStack<Integer> stack = new LockFreeStack<>();

        for (int i = 0; i < 10; i++) {
            Thread t1 = new Thread(() -> {
                stack.push(1);
            });
            Thread t2 = new Thread(() -> {
                stack.push(1);
            });

            t1.start(); t2.start(); t1.join(); t2.join();
        }

        int sum = 0;
        var v = stack.pop();
        while (v != null) {
            sum += v;
            v = stack.pop();
        }
        assertEquals(20, sum);
    }



    // TODO: 7.2.3 - Test pop
    @RepeatedTest(1000)
    void TestLockFreePop() throws InterruptedException {
        LockFreeStack<Integer> stack = new LockFreeStack<>();

        for (int i = 0; i < 30; i++) {
            stack.push(1);
        }
        for (int i = 0; i < 10; i++) {
            Thread t1 = new Thread(stack::pop);
            Thread t2 = new Thread(stack::pop);
            t1.start(); t2.start();
            t1.join(); t2.join();
        }

        int sum = 0;
        var v = stack.pop();
        while (v != null) {
            sum += v;
            v = stack.pop();
        }
        assertEquals(10, sum);
    }

    //part 4
    @RepeatedTest(1000)
    public void concurrentPopIsFunctionallyCorrect() throws InterruptedException {
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        stack.push(1);
        Set<Integer> results = Collections.synchronizedSet(new HashSet<>());

        Thread thread1 = new Thread(() -> {
            results.add(stack.pop());
        });

        Thread thread2 = new Thread(() -> {
            results.add(stack.pop());
        });

        thread1.start(); thread2.start(); thread1.join(); thread2.join();

        assertTrue(results.contains(1));
        assertTrue(results.contains(null));
    }

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
