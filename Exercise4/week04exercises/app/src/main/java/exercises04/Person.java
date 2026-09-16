package exercises04;

public class Person {
    private static long id_counter = 0;
    private final long id;
    private String name;
    private int zip;
    private String address;

    public Person() {
        synchronized (Person.class) {
            id_counter++;
            id = id_counter;
        }

    }

    public Person(int id) {
        synchronized (Person.class) {
            if (id_counter != 0) {
                id_counter++;
                this.id = id_counter;
            } else {
                id_counter = id;
                this.id = id;
            }
        }
    }
    public synchronized void setName(String name) {
        this.name = name;

    public synchronized void UpdateWhereYouLive(String address, int zip) {
        this.address = address;
        this.zip = zip;
    }

    public synchronized int getZip() {
        return zip;
    }
    public synchronized long getId() {
        return id;
    }
    public synchronized String getName() {
        return name;
    }
    public synchronized String getAddress() {
        return address;
    }


}
