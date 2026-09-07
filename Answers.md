# Exercise 1 

## Exercise 1.1 
``` java 
public class CountingThreads {
    int count; // Variable access 

  public CountingThreads() throws InterruptedException {
    count = 0; // Variable access (1)

    CountingThread t1 = new CountingThread(); // Variable access
    CountingThread t2 = new CountingThread(); // Variable access

    t1.start(); // Syncronization 
    t2.start(); // Syncronization 

    t1.join(); // Syncronization 
    t2.join(); // Syncronization 

    System.out.println("count="+count); // Variable access (4)
  }

  public class CountingThread extends Thread {
    public void run() {
      int temp = count; // Variable access (2)
      count = temp + 1; // Vaiable access (3)
    }
  }

  public static void main(String[] args) throws InterruptedException {
    new CountingThreads(); // Variable access
  }
}

```
## Exercise 1.2 Total order rule 

Because we only use the program order rule, we can only know the happens before relation of the main thread 
which could happen as follows. But this is the only execution as defined by the program order rule ,
which states that within a single thread the calls should happen sequentially as written. 

m(init(CountingThreads), init(count))
m(init(count), m(1))
m(m(1), start(t1))
m(start(t1), start(t2))
m(start(t2), join(t1))
m(join(t1), join(t2))
m(joint(t2), 4)

## Exercise 1.3 Thread start and termination rules 
Thread start rule states that the first call to start() happens before the first action in the thread 
Thread termination rule states that the last action of the thread happens before join()

With this, one possible execution of the program could be the set below, this is 
not the only way the program could execute but it is one possibility. 


main thread total order: (init(CountingThreads), init(count))

m(init(count), m(1))
m(1, start(t1))
m(start(t1), start(t2))

// Thread one execution
m(start(t1), t1(2))
m(t1(2), t1(3)) // this line may not be definable only using the thread start and termination rules 
m(t1(3), join(t1))

// Thread two execution 

m(start(t2), t2(2))
m(t2(2), t2(3)) // this line may not be definable only using the thread start and termination rules 
m(t2(3), join(t2))

m(join(t1), join(t2))
m(join(t2), 4)

## Exercise 1.4  

The set of all synchronizations is only as written, because the thread start/join must happen in program order. 


S = {
    <start(t1), start(t2), join(t1), join(t2)>
}

## Exercise 1.5 

For example in the execution below there is no happens before between the read and writes of t1 and t2
so t1 does its read then t2 does the same before t1 writes. So there is a conflict. 

So because t1(2) and t2(2) are not in the happens before order and are conflicting since they are accessing the shared variable.
m(init(CountingThreads), init(count))

m(init(count), start(t1))
m(start(t1), start(t2))

m(start(t1), t1(2))
m(start(t2), t2(2))

m(t1(2), t1(3))
m(t2(2), t2(3))

m(t1(3), join(t1))
m(t2(3), join(t2))

m(join(t1), join(t2))
m(join(t2), 4)

## Exercise 1.6 
Adding a synchronization over the lock of the shared CountingThreads class instance solves the synchronization issue.

```java 

    public class CountingThread extends Thread {
    public void run() {
      synchronized (CountingThreads.this) {
        int temp = count;
        count = temp + 1;
      }

    }
  }

```
There are now additional pairings in the happens before order as within the threads we now have. These orderings for what happens inside the individual threads 

Because of the locks there are now additional entries into the sets 

t1 monitor (t1(lock) -> t1(2) -> t1(3) -> t1(unlock))
t2 monitor (t2(lock) -> t(2) -> t2(3) -> t1(unlock))
HB {t2(unlock -> t1(lock))}
HB {t1(unlock -> t2(lock))}

As the program is now using locks there are additional synchronization actions for the lock and unlock actions. 
This also means that there are two possible cases. That t1 aquires the lock first and executes  and the t2 aquires the 
lock first. This will give the orders. 

T1 gets lock: t1(2) → t1(3) → t2(2) → t2(3)
T2 gets lock: t1(2) → t2(3) → t1(2) → t1(3)

It is possible for t1 to aquire its lock before t2 starts and vice versa. 

## Exercise 1.7

Because of the new ordered pairs defining the execution order before locks and unlocks 
the conflicting pairs are now in the happens before relation so there is no datarace. 

## Exercise 1.8 
The volatile keywords adds 
HB (t1(write), t2(read))
or 
HB (t2(write), t1(read))

This does ensure happens before, because the conflicting acesses are in the happens before closure. 
This prevents the data race but not the race condition since the two threads can still interleave 
and have t1(read) -> t2(read)

# Exercise 2 
 
## Exercise 2.1
They are not properly synchronized since the find() method is not synchonized only the findOrAdd()

There is is a data race because find is not performing lock(StringSet.this) we have the happens before relations 
HB t1/po = {
    t1(1) → t1(2),
    t1(2) → t1(3),
    t1(3) → t1(4)
}

and:

HB t2/po = {
    // only t2(1)
}

For the main thread:

HB m/po = {
    m(1) → m(2),
    m(2) → m(3)
}

HB = {
    m(1) → m(2),
    m(2) → m(3),

    t1(1) → t1(2),
    t1(2) → t1(3),
    t1(3) → t1(4),

    m(2) → t1(1),
    m(3) → t2(1)
}

Since there is no t2(1) -> t1(3) or t1(3) -> t1(1)
the conflicting pairs are not in the happens before relation so a data race is present
This by definition prevents the program from being synchronized 

## Exercise 2.2 
Adding synchronized we now have proper synchronization since we get the ordering of the locking 
and unlocking in the happens before relation. 

T1 program order + thread: (t1(lock) -> t1(1) -> t2(2) -> t1(3) -> t1(unlock))
T2 program order + thread (t2(lock) -> t2(1) -> t2(unlock))

we also get the synchronization orders t1(unlock) -> t2(lock) or t2(unlock) -> t1(lock)

This means we can make a total order transitive closure with proper sychronization between the threads

# Exercise 3 

## Exercise 3.1. Required HB ordering:

The important part is that the main threads write on x must be in happens-before the read on x in the worker thread
This will solve the visibility problem. 

m(3) →HB t1(1)

where:

m(3) = x = 42
t1(1) = read x in while(x == 0)

## Exercise 3.2. JMM:

HB m/po = {
    m(1) → m(2),
    m(2) → m(3),
    ...
}

HB start = {
    m(2) → t1(1)
}

There is no HB path from m(3) to t1(1):

m(3) HB t1(1)s

nor in the opposite direction:

t1(1) HB m(3)

Thus:

m(3) || t1(1)

The write and read are conflicting accesses with no HB ordering → data race, so the JMM permits t1 to repeatedly read 0 and loop forever.
