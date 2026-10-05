# Exercise 7.1 

## 7.1.1
Yes a possible execution that satisfies the specification could be 
q.enq(x) q.deq(x) q.enq(y)


## 7.1.2

No since in linearizable we cannot move the executions around then the order shown of 
q.deq(x) q.enq(x) q.enq(y)

breaks since we cannot retrieve x without it being put in the q first 

## 7.1.3
yes becuase we can pick a linearization point in the q.enq(x) block that is before 
a linearization point in q.deq(x) and then it is a valid execution 

## 7.1.4
No because it is not possible to enqueue y before enqueuing x so therefore the deque of y is not possible 
as with the Fifo program specification x should be the item to be dequeued 


# Exercise 7.2

## 7.2.1

```java 
public void push(T value) {
        Node<T> newHead = new Node<T>(value);            //pu1
        Node<T> oldHead;                                 //pu2         
        do {
            oldHead      = top.get();                    //pu3
            newHead.next = oldHead;                      //pu4
        } while (!top.compareAndSet(oldHead,newHead));   //pu5 <-- this one 

    }

    public T pop() {
        Node<T> newHead;
        Node<T> oldHead;
        do {
            oldHead = top.get();                        //po1
            if(oldHead == null) { return null; }        //po2 <-- branch returning 
            newHead = oldHead.next;                     //po3
        } while (!top.compareAndSet(oldHead,newHead));  //po4 <-- update

        return oldHead.value;                           //po5 
    }

```


Linearization point for push 
PU5 this is the point where the compare and set is done and other threads are notified 

If two threads execute push concurrently then the CAS operation ensures that the call when only complete 
whe the stack opject is in the correct state


Linearization for pop
PO4 is the point where pop is completed if the stack is non empty
PO2 is the point where pop is completed if the stack is empty


If two threads execute pop concurrently then the CAS operation ensures that the call to a non empty stack
will only update the oldhead when the stack in in the correct state. 

If a thread executes a pop after another thread has popped and set the old head to null then the 
calling thread will return null at PO3


## 7.2.2

```java 
    @RepeatedTest(1000)
    void TestLockFreePush() throws InterruptedException {
        LockFreeStack<Integer> stack = new LockFreeStack<>();

        for (int i = 0; i < 10; i++) {
            Thread t1 = new Thread(() -> {
                stack.push(1);
            });
            Thread t2 = new Thread(() -> {
                stack.push(1);
            });

            t1.start(); t2.start(); t1.join(); t2.join();
        }

        int sum = 0;
        var v = stack.pop();
        while (v != null) {
            sum += v;
            v = stack.pop();
        }
        Assertions.assertEquals(20, sum);
    }
``` 

## 7.2.3
```java
 @RepeatedTest(1000)
    void TestLockFreePop() throws InterruptedException {
        LockFreeStack<Integer> stack = new LockFreeStack<>();

        for (int i = 0; i < 30; i++) {
            stack.push(1);
        }
        for (int i = 0; i < 10; i++) {
            Thread t1 = new Thread(stack::pop);
            Thread t2 = new Thread(stack::pop);
            t1.start(); t2.start();
            t1.join(); t2.join();
        }

        int sum = 0;
        var v = stack.pop();
        while (v != null) {
            sum += v;
            v = stack.pop();
        }
        assertEquals(10, sum);
    }

    @RepeatedTest(1000)
    public void concurrentPopIsFunctionallyCorrect() throws InterruptedException {
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        stack.push(1);
        Set<Integer> results = ConcurrentHashMap.newKeySet();

        Thread thread1 = new Thread(() -> {
            results.add(stack.pop());
        });

        Thread thread2 = new Thread(() -> {
            results.add(stack.pop());
        });

        thread1.start(); thread2.start(); thread1.join(); thread2.join();
        
        assertTrue(results.contains(1));
        assertTrue(results.contains(null));
    }
```

## 7.2.4 

Yes they are testing the linearization points. For 7.2.2 we are just interested in the total sum after execution of the threads. 
And for 7.2.3 we need to check that the stack contains the correct values after concurrent execution and that in the case where there is 
one element in the list, then one of the threads need return null and one should return the value. 



# 7.2.3

## 7.2.1

readerUnlock & readerTryLock  - lockfree
We do not now how many steps it will take because of the do while but we know they will finish because of the CAS 


writerUnlock & writerTryLock - waitfree
they are wait free because there is no loop but only a set amount of operations