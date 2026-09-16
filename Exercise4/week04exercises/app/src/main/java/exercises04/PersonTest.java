package exercises04;

public class PersonTest {
    public static void main(String[] args) throws InterruptedException {

        int numberOfThreads = 5;
        int personsPerThread = 3;

        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {

            threads[i] = new Thread(() -> {

                for (int j = 0; j < personsPerThread; j++) {

                    Person person = new Person();

                    person.UpdateWhereYouLive(
                            "Address " + person.getId(),
                            1000 + (int) person.getId()
                    );

                    System.out.println(
                            "Thread: " + Thread.currentThread().getName()
                                    + ", Person ID: " + person.getId()
                                    + ", Address: " + person.getAddress()
                                    + ", Zip: " + person.getZip()
                    );
                }

            }, "Thread-" + i);

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("All threads finished.");
    }
}
