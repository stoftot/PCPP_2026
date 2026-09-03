## 1.1
### 1
19754192
19671928
No, since there is no access management to the shared resource the two thread end up creating a race condition.

### 2
There is a startup cost for starting a thread, and since the number is so small its very likely that the first thread finishes before the second one starts
No it is not

### 3
No, I would guess that all 3 would be compiled to the same thing.
No

### 4
```java
class LongCounter {
	//Added shared lock
	private static final ReentrantLock LOCK = new ReentrantLock();
	...
	public void increment() {
		//lock before accsesing the critical section
		LOCK.lock();
		count++;
		//Unlock when done with the critical section
		LOCK.unlock();
	}
	...
}
```
The added lock makes sure that only one "count++" operation can be made at a time, meaning even if multiple threads where calling increment(), they would have to wait to access the shared resources, and hereby avoiding the race condition leading to a not "full" count.

### 5
Yes it does, it contains one line, which is the reading and incrementing of the shared resource count, it cant be smaller than that, each thread has to make sure that when they are reading count no other thread is gonna update it after the read, and have to make sure that when they update count no other thread has read the "old count"

## 1.2
```java
public void print(){
	System.out.print("-"); //1
	try { Thread.sleep(50); } catch (InterruptedException _) { } //2
	System.out.print("|"); //3
}
```

### 2
t1(1) t2(1) t1(2) t2(2) t1(3) t2(3)
It happens when the second thread overtakes the first in execution

### 3
```java
public class Printer {
    private static final ReentrantLock LOCK = new ReentrantLock(); //added
    ...
	public void print() {
		LOCK.lock(); //added
        System.out.print("-");
        try { Thread.sleep(50); } catch (InterruptedException _) { }
        System.out.print("|");
        LOCK.unlock(); //added
    }
}
```
The reason is that the when the the thread wants to write a dash, it has to acquire a lock, and that lock is only releases once a pip has been written.

## 1.3
### 1
```java
public class Turnstile extends Thread {
	private static final ReentrantLock LOCK = new ReentrantLock(); //added

	public void run() {
		for (int i = 0; i < PEOPLE; i++) {
			LOCK.lock(); //added
			if (counter < 15_000) //added
				counter++;
			LOCK.unlock(); //added
		}
	}
}
```

### 2
There is a lock around the critical section, as well as a check to ensure that the counter dosent go above 15_000


## 1.4
### 1
Resource Utilization / Inherent:
Web applications with user interfaces combine both of these. Waiting for the retrieval of information takes time so you can free up space by allowing for processes to run while waiting for retrieval of packages. 

Resource utilization ↔ Exploitation:
A web server handling thousands of requests.
While one request is waiting for a database response, another request can use the CPU.
This improves hardware utilization.

Convenience ↔ Inherent:
A chat application.
One thread listens for incoming messages.
Another thread handles the user interface.
It is easier to write as two cooperating tasks than one large program.

### 2
#### Inherent
GUI: Handle user input while updating the interface.
Chat: Receive messages while the user is typing.
Games: Handle input, physics, graphics, and sound simultaneously.

#### Convenience
Worker threads: Handle user input separately from long calculations.
Producer-consumer: One thread produces data while another processes it.
Parallel processing: Multiple threads process independent images/tasks simultaneously.

#### Hidden
Databases: Handle multiple users accessing the same data concurrently.
Operating system: Manage multiple programs accessing shared files/resources.
Web servers: Handle thousands of requests concurrently without the client managing threads.

## 1.5
### 1
**Fedora Linux 44 (KDE)** - **x86-64** system.
Its not emulated

### 2
- **Operating system:** Fedora Linux 44 (KDE)
- **Processor:** Intel Core i7-8750H @ 2.20 GHz
- **CPU architecture:** x86-64
- **Physical CPU cores:** 6
- **Threads per core:** 2
- **Logical CPUs / hardware threads:** 12
- **Main memory:** approximately 15 GiB usable RAM (16 GB installed)
#### Cache architecture
The processor has three cache levels:

| Cache | Type        | Size per instance | Number of instances | Total size | Associativity |
| ----- | ----------- | ----------------- | ------------------- | ---------- | ------------- |
| L1d   | Data        | 32 KiB            | 6                   | 192 KiB    | 8-way         |
| L1i   | Instruction | 32 KiB            | 6                   | 192 KiB    | 8-way         |
| L2    | Unified     | 256 KiB           | 6                   | 1.5 MiB    | 4-way         |
| L3    | Unified     | 9 MiB             | 1                   | 9 MiB      | 12-way        |

### 3
1263
