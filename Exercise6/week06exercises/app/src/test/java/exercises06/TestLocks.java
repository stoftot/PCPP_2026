// For week 6
// raup@itu.dk * 2026-09-23

package exercises06;

// Very likely you will need some imports here

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestLocks {
    // The imports above are just for convenience, feel free add or remove imports

    // TODO: 6.2.5

    @Test
    void cannotTakeReadLockWhileHoldingWriteLock() {
        ReadWriteCASLock lock = new ReadWriteCASLock();

        assertTrue(lock.writerTryLock());
        assertFalse(lock.readerTryLock());

        lock.writerUnlock();
    }

    @Test
    void cannotTakeWriteLockWhileHoldingReadLock() {
        ReadWriteCASLock lock = new ReadWriteCASLock();

        assertTrue(lock.readerTryLock());
        assertFalse(lock.writerTryLock());

        lock.readerUnlock();
    }

    @Test
    void cannotUnlockWriteLockThatYouDoNotHold() {
        ReadWriteCASLock lock = new ReadWriteCASLock();

        assertThrows(
                IllegalStateException.class,
                lock::writerUnlock
        );
    }

    @Test
    void cannotUnlockReadLockThatYouDoNotHold() {
        ReadWriteCASLock lock = new ReadWriteCASLock();

        assertThrows(
                IllegalStateException.class,
                lock::readerUnlock
        );
    }

    // TODO: 6.2.6
    @Test
    void multipleWritersCannotHoldLockAtSameTime()
            throws InterruptedException {

        ReadWriteCASLock lock = new ReadWriteCASLock();

        int numberOfThreads = 20;
        int repetitions = 1000;

        AtomicInteger writersInside = new AtomicInteger(0);
        AtomicInteger maxWritersInside = new AtomicInteger(0);

        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {

                for (int j = 0; j < repetitions; j++) {
                    while (!lock.writerTryLock()) {
                        Thread.yield();
                    }

                    int inside = writersInside.incrementAndGet();

                    maxWritersInside.accumulateAndGet(
                            inside,
                            Math::max
                    );

                    Thread.yield();
                    writersInside.decrementAndGet();
                    lock.writerUnlock();
                }
            });

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(1, maxWritersInside.get());
    }

}
