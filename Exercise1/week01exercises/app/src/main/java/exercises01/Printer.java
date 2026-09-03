package exercises01;

import java.util.concurrent.locks.ReentrantLock;

public class Printer {
    private static final ReentrantLock LOCK = new ReentrantLock();

    public static void main(String[] args) {
        var p = new Printer();
        var t1 = new Thread(() -> {
            while (true) {
                p.print();
            }
        });
        var t2 = new Thread(() -> {
            while (true) {
                p.print();
            }
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException exn) {
            System.out.println("Some thread was interrupted");
        }
    }

    public void print() {
        LOCK.lock();
        System.out.print("-");
        try {
            Thread.sleep(50);
        } catch (InterruptedException _) {
        }
        System.out.print("|");
        LOCK.unlock();
    }
}
