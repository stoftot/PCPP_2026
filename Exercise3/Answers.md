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

(t1(2),t1(3))
(t2(2), t2(3))

With the program order rule the set is always the same, even though the actual execution may not be. 

## Exercise 1.3 Thread start and termination rules 
Thread start rule states that the first call to start() happens before the first action in the thread 
Thread termination rule states that the last action of the thread happens before join()

With this, one possible execution of the program is the set below, this is 
not the only way the program could execute but what is defined with these rules.


// Thread one execution
m(start(t1), t1(2))
m(t1(3), join(t1))

// Thread two execution 

m(start(t2), t2(2))
m(t2(3), join(t2))

## Exercise 1.4  

The set of all synchronizations is only as written, because the thread start/join must happen in program order. 


S = {
    <start(t1), start(t2), join(t1), join(t2)>
}

## Exercise 1.5 

For example in the execution below there is no happens before between the read and writes of t1 and t2
so t1 does its read then t2 does the same before t1 writes. So there is a conflict. 

There are the following conflicting actions, and they are not defined in the happens beofre so there is a datarace. 

t1(2), t2(3)
t2(2), t1(3)
t1(3), t2(3)
t2(3), t1(3)



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
public TestStringSet() throws InterruptedException {
    s = new StringSet(); //inti(s)
    
    Thread t1 = new Thread(() -> {
      s.findOrAdd("PCPP");
    });
    Thread t2 = new Thread(() -> {
      s.find("PCPP");
    });
    
    t1.start(); //start(t1)
    t2.start(); //start(t2)
}

public class StringSet {
    private final List<String> list = new ArrayList<String>();
    
    public synchronized int findOrAdd(String s) {
      int ret = list.indexOf(s); //1
      if (ret == -1) { 
        list.add(s); //2
      }
      return ret;
    }
    
    public int find(String s) {
      return list.indexOf(s); //3
    }
}


$HB{po}^{m}$ = {m(init(s)) -> m(start(t1)) -> m(start(t2))}
$HB{po}^{t2}$ = {t1(1) -> t1(2)}
$HB{po}^{t2}$ = {t2(3)}
$HB{init}$ = {m(start(t1)) -> t1(1), m(start(t2)) -> t2(3)}

The conflicting acctions are the following
t1(2), t2(3)
Since they access the same non volatile variable, and at least one of them is a write

A corretley synchronized program is defined by none of its executions containing a data race, and there exsists a data race between two actions a and b in an execution of actions a and b are conflictin and are not ordered by happens before

t1(2),t2(3) are conflicting and are not ordered by happens berfore, therefore the program is not correctley synchronized

## Exercise 2.2 
Adding synchronized we now have proper synchronization since we get the ordering of the locking 
and unlocking in the happens before relation. 

T1 program order + thread: (t1(lock) -> t1(1) -> t2(2) -> t1(3) -> t1(unlock))
T2 program order + thread (t2(lock) -> t2(1) -> t2(unlock))

we also get the synchronization orders t1(unlock) -> t2(lock) or t2(unlock) -> t1(lock)

This means we can make a total order transitive closure with proper sychronization between the threads

# Exercise 3 

## Exercise 3.1. Required HB ordering:

The important part is that the main threads write on x must happen-before the read on x in the worker thread
This will solve the visibility problem. 

m(3) →HB t1(1)

where:

m(3) = x = 42
t1(1) = read x in while(x == 0)

## Exercise 3.2. JMM:

$HB{po}^{m}$ = {m(init(x)) -> m(start(t1)) -> m(3) -> (4)}
$HB{po}^{t1}$ = {t1(1) -> t1(2)}
$HB_{init}$ = {m(start(t1)) -> t1(1)}
