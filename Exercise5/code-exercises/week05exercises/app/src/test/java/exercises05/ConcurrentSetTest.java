package exercises05;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.assertEquals;
// TODO: Very likely you need to expand the list of imports

public class ConcurrentSetTest {

    // Variable with set under test
    private ConcurrentIntegerSet set;
    private CyclicBarrier barrier;
    // TODO: Very likely you should add more variables here
    // Uncomment the appropriate line below to choose the class to
    // test
    // Remember that @BeforeEach is executed before each test
    @BeforeEach
    public void initialize() {
        // init set
        //set = new ConcurrentIntegerSetBuggy();
        //set = new ConcurrentIntegerSetSync();
         set = new ConcurrentIntegerSetLibrary();
    }

    // TODO: Define your tests below

    @RepeatedTest(5000)
    public void testConcurrentIntegerSet() {
        int nrThreads = 8;

        barrier = new CyclicBarrier(nrThreads + 1);

        for (int i = 0; i < nrThreads; i++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    set.add(1);
                    set.add(2);
                    set.add(3);
                    set.add(4);
                    set.add(5);

                    barrier.await();
                } catch (InterruptedException |  BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            barrier.await();
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        assertEquals(5, set.size());

    }

    @RepeatedTest(5000)
    public void testConcurrentIntegerSetRemove() {
        int nrThreads = 8;

        barrier = new CyclicBarrier(nrThreads + 1);

        for (int i = 0; i < 5; i++) {
            set.add(i);
        }
        for (int i = 0; i < nrThreads; i++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    set.remove(0);
                    set.remove(1);
                    set.remove(2);
                    set.remove(3);
                    set.remove(4);

                    barrier.await();
                } catch (InterruptedException |  BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            barrier.await();
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        assertEquals(0, set.size());

    }
}
