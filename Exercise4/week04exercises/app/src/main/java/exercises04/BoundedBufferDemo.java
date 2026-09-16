package exercises04;

public class BoundedBufferDemo {
    public static void main(String[] args) {

        BoundedBuffer<Integer> bb = new BoundedBuffer<Integer>(2);
        new Thread(() -> {
            while (true) {
                try {
                    bb.insert(5);
                    Thread.sleep(100);
                } catch (Exception e) {}

        }
        }).start();
        new Thread(() -> {
            while (true) {
                try {
                    bb.insert(3);
                    Thread.sleep(100);
                } catch (Exception e) {}

            }
        }).start();
        new Thread(() -> {
            while (true) {
                try {
                    System.out.println(bb.take());
                    Thread.sleep(100);
                } catch (Exception e) {}

            }
        }).start();


    }
}
