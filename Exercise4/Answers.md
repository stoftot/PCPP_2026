# Exercise 4 

## Exercise 4.1

### Exercise 4.1.1
### Exercise 4.1.2
### Exercise 4.1.3

## Exercise 4.2

### Exercise 4.1.1
```java 

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
    }

    public synchronized int getZip() { return zip; }
    public synchronized long getId() { return id;  }
    public synchronized String getName() { return name; }
    public synchronized String getAddress() { return address; }
}

```

### Exercise 4.1.2
**Class State**
The state of the class is safe since the fields of the class are only 
exposed through the synchronized getters. It is not possible to have a data
race on the fields since most of the fields only do reads and the name, which has 
a write operation is protected by the intrinsic lock on the object. 

**Escaping**
The class state deos not escape since the values of the fields are only accessed through synchronized getters 
and no object references of the class state are leaked to calling threads.

**Safe publication**
The Objects in safely instantiated by using the class lock to make sure only one 
thread can instantiate the class and modifying the setting of the ids. This ensures 
that there are no visibility problems after instantiation. 

**Immutablilty**
The id that should not be changed is declared as final ensuring the immutability of the field

**Mutual exclusion**
The writes to the name field are protected by the intrinsic lock of the object ensuring mutex



The constructor is using the static id_counter which is shared across instances of the object. 
In cases where it is altered it is protected by the lock on Person.class ensuring that there 
will not be problems on subsequent calls to the constructor.

### Exercise 4.1.3
```java 
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
```
### Exercise 4.1.4

The program above is instatiating different person instances. One output of the program is 
Thread 3 id 8
Thread 2 id 7
Thread 1 id 6
Thread 2 name george
Thread 3 name theorgina

here we can see that even though the Person constructor with an id is called multiple times it respects the 
original call and doesnt override it. 

This doesn't test all interleavings of the setting/getting of the name, but since it is protected by the mutex 
this should be fine.

##
