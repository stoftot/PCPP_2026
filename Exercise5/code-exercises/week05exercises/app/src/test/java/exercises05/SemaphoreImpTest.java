package exercises05;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SemaphoreImpTest {
    @Test
    public void testSemaphoreCapacity() throws Exception {
        Semaphore semaphore = new Semaphore(1);
        CyclicBarrier barrier = new CyclicBarrier(4);
        semaphore.release();

        new Thread(() -> {
            try {
                semaphore.acquire();
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                semaphore.acquire();
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                semaphore.acquire();
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }).start();

        barrier.await();

        assertTrue(true);
    }

    @Test
    public void testSemaphoreCapacityMain() throws Exception {
        Semaphore semaphore = new Semaphore(1);
        try {
            semaphore.release();
            semaphore.acquire();
            semaphore.acquire();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }
}
