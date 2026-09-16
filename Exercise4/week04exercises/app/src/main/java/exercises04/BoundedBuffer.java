package exercises04;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BoundedBuffer<T> implements BoundedBufferInteface<T>{
    private final Semaphore takeSemaphore = new Semaphore(0, true);
    private final Semaphore insertSemaphore;
    private final Semaphore mutexSemaphore = new Semaphore(1, true);
    private final List<T> buffer;

    public BoundedBuffer(int bufferSize){
        buffer = new ArrayList<>(bufferSize);
        insertSemaphore = new Semaphore(bufferSize, true);
    }


    @Override
    public T take() throws Exception {
        takeSemaphore.acquire();
        try {
            mutexSemaphore.acquire();
        } catch (Exception e) {
            takeSemaphore.release();
            throw e;
        }

        T elem;

        try {
            elem = buffer.getFirst();
            buffer.removeFirst();
        } catch (Exception e){
            mutexSemaphore.release();
            takeSemaphore.release();
            throw e;
        }

        mutexSemaphore.release();
        insertSemaphore.release();

        return elem;
    }

    @Override
    public void insert(T elem) throws Exception {
        insertSemaphore.acquire();
        try {
            mutexSemaphore.acquire();
        } catch (Exception e) {
            insertSemaphore.release();
            throw  e;
        }

        try {
            buffer.add(elem);
        } catch (Exception e){
            mutexSemaphore.release();
            insertSemaphore.release();
            throw  e;
        }

        mutexSemaphore.release();
        takeSemaphore.release();
    }
}
