## 2.1
### 1
```java
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
```

### 2
The solution is fair because the readLock() and writeLock() functions have the same restriction for aquiring the lock checking the writer boolean. If the (readers > 0 || writer) condition would have been used, the writers would have a stricter condition for acquiring the lock, leading to starvation.

### 3
Yes the solution uses the underlying Condition variable associated with the Object class in java.
Here all the methods uses the condition on the instance of the monitor object. Indicated by the calls to 
this.wait()
this.notifyAll()

## 2.2
### 1
the two threads have there own memory, and they don't synchronize.
to java, it doesn't look like that the variable needs to be shared between threads, so for optimization reasons, the thread doesn't check if the main thread has updated the variables.

### 2
```java
class MutableInteger {
    // WARNING: Not ready for usage by concurrent programs
    private int value = 0;
    public synchronized void set(int value) {
        this.value = value;
    }
    public synchronized int get() {
        return value;
    }
}
```
The setter being synchronized ensures that when we call it, the updated value gets flushed to lower level cache.
The getter being synchronized ensures that when getting the value it looks for updates in lower level cach

### 3
No, see above. If the getter is missing synchronized, it would assume that it could just read its core memory, and therefore not read the updated value

### 4
yes, because volatile variables are only stored in shared memory

## 2.3
### 1
Sum is 1055199.000000 and should be 2000000.000000
Sum is 1053680.000000 and should be 2000000.000000
yes

### 2
The Mystery object has both a static and non static way to increment the sum. This means that they are not sharing the lock since the static method will use the lock on the Mystery.class obj andect the non static method will use the lock associated with the instance of the mystery object.

### 3
```java
class Mystery {
    private static double sum = 0;
    private static ReentrantLock lock = new ReentrantLock(); //added

    public static synchronized void addStatic(double x) {
        lock.lock(); //added
        sum += x;
        lock.unlock(); //added
    }

    public synchronized void addInstance(double x) {
        lock.lock(); //added
        sum += x;
        lock.unlock(); //added
    }
    ...
}
```
Introduced a lock, so now only one of the method can execute their read and updated one at a time

### 4
No, its not necessary.  Because the main thread wait for the other threads to join, which guarantee that its done executing, and because it dosnet have the variable in its own memory, it has to go down and fetch it from main memory 
