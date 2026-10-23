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
	Node<T> newHead = new Node<T>(value);
	Node<T> oldHead;
	do {
		oldHead      = top.get();
		newHead.next = oldHead;
	} while (!top.compareAndSet(oldHead,newHead)); //PU1

}

public T pop() {
	Node<T> newHead;
	Node<T> oldHead;
	do {
		oldHead = top.get(); //PO1
		if(oldHead == null) { return null; } //PO2
		newHead = oldHead.next;
	} while (!top.compareAndSet(oldHead,newHead)); //PO3

	return oldHead.value;
}
```
**Push has one linerization point**
PU1 -  if its sucssefully executed the element has been pushed to the stack.
**Correctness:**
- If two threads execute push concurrentley, then only one of them succeeds in executing PU1, the other fails and repeats the push
- If a thread executes push after another thread updated top, then PU1 fails and repeates the push 

**Pop has two linerization points,**
PO1 - if the stack is empty, this determines the evaluation in PO2, and the method will then return null
PO3 - if sucessfully executed the element has been poped from the stack
**Correctness:**
- If two threads execute pop concurrentley, and the stack is not emptey, then PO3 succeeds for only one of them and the other fails and repeats pop
- If a thread executes pop after another thread updates top then PO3 fails and it restarts the pop


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

No
Part 2 do, because that only have one linerization point, which is related to pushing to the stack.
Part 3 dosn't, one of it's linearization points regard popping from an empty list, and the test pops equivilant to the amount of elements in the list.



# 7.2.3

## 7.2.1

readerUnlock & readerTryLock  - lockfree
We do not now how many steps it will take because of the do while but we know they will finish because of the CAS 


writerUnlock & writerTryLock - waitfree
they are wait free because there is no loop but only a set amount of operations