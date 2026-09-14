package exercises04;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BoundedBuffer<Integer> implements BoundedBufferInteface<Object> {

    private final Semaphore sm_empty;
    private final Semaphore sm_full;
    private final Semaphore sm_mutex;
    private final Queue<Object> buffer;

    public BoundedBuffer(int size) {
        sm_empty = new Semaphore(size);
        sm_full = new Semaphore(0);
        sm_mutex = new Semaphore(1);
        buffer = new LinkedList<>();

    }
    @Override
    public Object take() throws Exception {
        sm_full.acquire();
        sm_mutex.acquire();

        Object elem;

        elem = buffer.remove();

        sm_mutex.release();
        sm_empty.release();

        return elem;
    }

    @Override
    public void insert(Object elem) throws Exception {
        sm_empty.acquire();
        sm_mutex.acquire();

        buffer.add(elem);

        sm_mutex.release();
        sm_full.release();
    }
}
