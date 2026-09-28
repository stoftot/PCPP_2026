// For week 6
// raup@itu.dk * 2024-09-22

package exercises06;

// Very likely you will need some imports here

import java.io.Reader;
import java.util.concurrent.atomic.AtomicReference;

class ReadWriteCASLock implements SimpleRWTryLockInterface {

    private final AtomicReference<Holders> holders =
            new AtomicReference<>(null);

    public boolean readerTryLock() {
        Thread currentThread = Thread.currentThread();

        Holders current;
        ReaderList newList;

        do {
            current = holders.get();
            
            if (current instanceof Writer) {
                return false;
            }

            ReaderList oldList = (ReaderList) current;
            newList = new ReaderList(currentThread, oldList);

        } while (!holders.compareAndSet(current, newList));

        return true;
    }

    public void readerUnlock() {
        Thread currentThread = Thread.currentThread();

        Holders current;
        ReaderList newList;

        do {
            current = holders.get();

            if (!(current instanceof ReaderList)) {
                throw new IllegalStateException();
            }

            ReaderList readers = (ReaderList) current;

            if (!readers.contains(currentThread)) {
                throw new IllegalStateException();
            }

            newList = readers.remove(currentThread);

        } while (!holders.compareAndSet(current, newList));
    }

    public boolean writerTryLock() {
        return holders.compareAndSet(
                null,
                new Writer(Thread.currentThread())
        );
    }

    public void writerUnlock() {
        Holders current = holders.get();

        if (!(current instanceof Writer)
                || ((Writer) current).thread != Thread.currentThread()) {
            throw new IllegalStateException();
        }

        holders.set(null);
    }

    private static abstract class Holders { }

    private static class ReaderList extends Holders {
        private final Thread thread;
        private final ReaderList next;

        // TODO: Constructor
        public ReaderList(Thread thread, ReaderList next) {
            this.thread = thread;
            this.next = next;
        }

        // TODO: contains
        public boolean contains(Thread thread) {
            ReaderList current = this;

            while (current != null) {
                if (current.thread == thread) {
                    return true;
                }

                current = current.next;
            }

            return false;
        }

        // TODO: remove
        public ReaderList remove(Thread thread) {
            if (this.thread == thread) {
                return this.next;
            }

            if (this.next == null) {
                return this;
            }

            return new ReaderList(
                    this.thread,
                    this.next.remove(thread)
            );
        }
    }

    private static class Writer extends Holders {
        public final Thread thread;

        // TODO: Constructor
        public Writer(Thread thread) {
            this.thread = thread;
        }

    }
}
