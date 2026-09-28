# Exercise 5.1 

## 5.1.1
All four threads concurrently add the integers 0–999. Since a set cannot contain duplicate elements, the expected final size is 1000. However, HashSet is not thread-safe and add() is not atomic. Concurrent modifications can interleave while threads are reading and updating the internal hash table and size information. This can corrupt the internal state of the HashSet, resulting in an incorrect size such as 1088. Thus, the test demonstrates that ConcurrentIntegerSetBuggy does not satisfy the sequential specification when add() is called concurrently.

size = 10

Thread 1: read 10
Thread 2: read 10
Thread 1: write 11
Thread 2: write 11

size = 11   // should have been 12

## 5.1.2 

Since the remove method is not atomic multiple threads can find and remove the same element leading to more decrements than expected

Initial size = 1

Thread 1: remove(x)
Thread 2: remove(x)

Thread 1: finds x
Thread 2: finds x

Thread 1: removes x
Thread 2: removes x

Thread 1: decrements size
Thread 2: decrements size

Final size = -1

## 5.1.3 

Adding synchornized keyword the add and remove method modifiers fixes the issue. 
All repititions of the test use the same instance of the set variable so the intrinsic lock is enough to fix the interleavings 

## 5.1.4

It works no bug report

## 5.1.5

Since the tests written test interleavings of threads a failure does indicate that the tested collection is not thread safe. 
Since the faults in the threads can only be explained by thread-safety errors.

## 5.1.6
No since we are only testing multiple iterations it is technically possible that the threads interleave in a way that 
doesnt create errors. With many repititions this chance becomes lower, but since we are not formally disproving the interleaving 
we cannot definitively say that the tested collection is thread safe. 

# Exercise 5.2 

## 5.2.1

There is no check in release that prevents the state to go below zero which means that the capacity can drop to negative.
This allows more than c threads to enter the critical section at a time. 

If we make a new semaphore with capacity two: SemaphoreImp(1) then we can have an inter leaving 

cap = 1
state = 0

m(release)
state = -1
t1(acquire)
state = 0

t2(acquire) // succeeds
state = 1




In the interleaving above the state becomes negative which allows threads to enter the critical section. 
This violates the semaphore property since t2 should be blocked since state==cap at the point of its call to acquire.


## 5.2.2

The test belows shows this. Since it is able to reach the assert true, even though the semaphore should block 2 acquires.

``` java 
@Test
    @Test
    public void testSemaphoreCapacity() throws Exception {
        Semaphore semaphore = new Semaphore(1);
        CyclicBarrier barrier = new CyclicBarrier(4);
        semaphore.release();

        new Thread(() -> {
            try {
                semaphore.acquire();
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                semaphore.acquire();
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                semaphore.acquire();
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }).start();

        barrier.await();

        assertTrue(true);
    }
```
