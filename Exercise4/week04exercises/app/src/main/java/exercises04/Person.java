package exercises04;

public class Person {
    private static int lastID;
    private static boolean firstPersonCreated = false;

    private final long id;
    private String name;
    private int zip;
    private String address;

    public Person() {
        id = CreateID(0);
    }

    public Person(int initialID) {
        id = CreateID(initialID);
    }

    private static synchronized long CreateID(int initialID) {
        if (!firstPersonCreated) {
            lastID = initialID;
            firstPersonCreated = true;
        } else {
            lastID++;
        }

        return lastID;
    }

    public synchronized void UpdateWhereYouLive(String address, int zip) {
        this.address = address;
        this.zip = zip;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getZip() {
        return zip;
    }

    public String getAddress() {
        return address;
    }
}
