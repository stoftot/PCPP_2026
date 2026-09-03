package exercises02;

class MonitorTest {
    public static void main(String[] args) {
        Monitor m = new Monitor();
        for (int i = 0; i < 10; i++) {
            // start a reader
            new Thread(() -> {
                try {
                    m.readLock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(" Reader " + Thread.currentThread().getId() + " started reading");
                // read
                System.out.println(" Reader " + Thread.currentThread().getId() + " stopped reading");
                m.readUnlock();
            }).start();

            // start a writer
            new Thread(() -> {
                try {
                    m.writeLock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(" Writer " + Thread.currentThread().getId() + " started writing");
                // write
                System.out.println(" Writer " + Thread.currentThread().getId() + " stopped writing");
                m.writeUnlock();
            }).start();
        }
    }
}

public class Monitor {
    private boolean writer = false;
    private int readers = 0;

    public synchronized void readLock() throws InterruptedException {
        while (writer)
            this.wait();
        readers++;
    }

    public synchronized void readUnlock() {
        readers--;
        if (readers == 0)
            this.notifyAll();
    }

    public synchronized void writeLock() throws InterruptedException {
        while (writer)
            this.wait();
        writer = true;
        while (readers > 0)
            this.wait();
    }

    public synchronized void writeUnlock() {
        writer = false;
        this.notifyAll();
    }
}
