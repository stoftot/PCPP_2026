package exercises04;

public class PersonDemo {
    public static void main(String[] args) {
        new Thread(() -> {
            Person p = new Person(15);
            System.out.println("Thread 1 id " + p.getId());
        }).start();

        Person pm = new Person(5);

        new Thread(() -> {
            Person p = new Person(10);
            System.out.println("Thread 2 id " + p.getId());
            pm.setName("george");
            System.out.println("Thread 2 name " + pm.getName());
        }).start();

        new Thread(() -> {
            Person p = new Person(20);
            System.out.println("Thread 3 id " + p.getId());
            pm.setName("theorgina");
            System.out.println("Thread 3 name " + pm.getName());
        }).start();
    }
}
