package exercises04;

public class boundedBufferDemo {
    public static void main(String[] args) throws Exception {
        BoundedBuffer boundedBuffer = new BoundedBuffer<Integer>(2);
        Thread t1 = new Thread(()->{
            try {
                boundedBuffer.insert(1);
                boundedBuffer.insert(2);
                boundedBuffer.insert(3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Thread t2 = new Thread(()->{
            try {
                System.out.println(boundedBuffer.take());
                System.out.println(boundedBuffer.take());
                System.out.println(boundedBuffer.take());
                System.out.println(boundedBuffer.take());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Thread t3 = new Thread(()->{
            try {
                boundedBuffer.insert(1);
                boundedBuffer.insert(2);
                boundedBuffer.insert(3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        t1.start();
        Thread.sleep(2000);
        t2.start();
        Thread.sleep(2000);
        t3.start();
        t1.join();
        t2.join();
        t3.join();
        System.out.println(boundedBuffer.take());
        System.out.println(boundedBuffer.take());
    }
}
