# Exercise 9.1 

## Exercise 9.1.1 & 9.1.2 Threads 
OS:   Linux; 6.16.3-76061603-generic; amd64
JVM:  Oracle Corporation; 24.0.1
CPU:  null; 8 "cores"
Date: 2026-10-26T10:24:34+0100
countSequential                  245699.1 us   16889.90          2
countParallelPlatf      1        246561.4 us    1757.24          2
countParallelVirtu      1        246791.8 us    3990.65          2
countParallelPlatf      2        164339.8 us   19164.66          2
countParallelVirtu      2        160781.4 us    8087.50          2
countParallelPlatf      3        120890.0 us   10364.69          4
countParallelVirtu      3        151671.3 us   43615.12          4
countParallelPlatf      4        122494.8 us   15119.33          4
countParallelVirtu      4        138378.4 us   12844.43          2
countParallelPlatf      5        107095.4 us    3677.85          4
countParallelVirtu      5        118126.0 us   16971.09          4
countParallelPlatf      6         97534.3 us    4863.90          4
countParallelVirtu      6         94833.4 us    9812.44          4
countParallelPlatf      7         83827.4 us    1234.29          4
countParallelVirtu      7         89462.5 us    6722.09          4
countParallelPlatf      8         79097.6 us     508.02          4
countParallelVirtu      8         81797.9 us    2883.10          4
countParallelPlatf     16         78189.5 us    2704.58          4
countParallelVirtu     16         86042.9 us   12490.02          4
countParallelPlatf     32         75873.5 us    1074.40          4
countParallelVirtu     32         74803.4 us    1720.55          4
countParallelPlatf     64         77962.9 us    1070.82          4
countParallelVirtu     64         71029.6 us     561.16          4
countParallelPlatf    128        114578.1 us   26958.74          4
countParallelVirtu    128         84020.3 us   24434.36          4
countParallelPlatf    256         97303.4 us    7347.50          4
countParallelVirtu    256         73518.4 us    5406.92          4
countParallelPlatf    512         99808.6 us    1193.13          4
countParallelVirtu    512         71416.9 us    1097.52          4
countParallelPlatf   1024        125204.4 us   12985.32          4
countParallelVirtu   1024         81107.8 us    8498.27          4
countParallelPlatf   2048        162911.7 us   32848.99          2
countParallelVirtu   2048         72347.5 us     633.10          4

## Exercise 9.1.3 ExecutorTasks 

# OS:   Linux; 6.16.3-76061603-generic; amd64
# JVM:  Oracle Corporation; 24.0.1
# CPU:  null; 8 "cores"
# Date: 2026-10-26T10:33:10+0100
countParallelTask       1        260791.3 us   11461.03          2
countParallelTask       2        175221.3 us   10750.18          2
countParallelTask       3        136943.2 us    6715.96          2
countParallelTask       4        105802.0 us   14344.48          2
countParallelTask       5         93561.6 us    2834.93          4
countParallelTask       6         90201.1 us    3319.91          4
countParallelTask       7         86407.4 us    4357.81          4
countParallelTask       8         79354.5 us    1670.96          4
countParallelTask      16         85097.4 us   12445.45          4
countParallelTask      32         80903.6 us    4042.82          4
countParallelTask      64         87220.2 us    8421.60          4
countParallelTask     128        100456.7 us   12850.96          4
countParallelTask     256         78906.2 us   15335.30          4
countParallelTask     512         61543.9 us    2783.00          8
countParallelTask    1024         63207.9 us    2412.43          8
countParallelTask    2048         79979.9 us    8858.71          4

## Exercise 9.1.4 

They are plausible but a wierd thing that happens is that we see the Tasks are actually running slower than the 
virtual threads. This could be due other things interfeering with the CPU. But in general we would expect the Tasks 
to run faster than the virtual threads. But we do see platform threads slower than virtual threads which alligns with theory. 

Pc has 8 cores so for ExecutorTasks it also makes sense that we do not see many improvements after 8 cores. 

# Exercise 9.2 

## Exercise 9.2.1
class exercises09.Histogram1      53412.6 ms
class exercises09.SynchronizedHistogram    44380.2 ms


## Exercise 9.2.2
class exercises09.CasHistogram    53401.3 ms

Runs about the same times as Synchronizedhistogram, a bit slower on my machine. 

## Exercise 9.2.3 
class exercises09.AtomicHistogram    56254.1 ms


## Exercise 9.2.4 
class exercises09.StripedHistogram    56870.0 ms

## Exercise 9.2.5
It resembles most the StripedMap without padding. Here the AtomicIntegers serve the same role as the 
locks protecting the concurrent operation. 

## Exercise 9.2.6 
class exercises09.LongAdderHistogram    51493.1 ms

The LongAdder class uses more space that the other implementations because of the padding in the LongAdder implementing



# Exercise 9.3 
## Exercise 9.3.1 
OS:   Linux; 6.16.3-76061603-generic; amd64
JVM:  Oracle Corporation; 24.0.1
CPU:  null; 8 "cores"
Date: 2026-10-26T11:27:34+0100
SyncLongAdder              20        468480.5 us   24991.32          2
SyncLongArrayAdder         20        290429.9 us   20564.83          2
SyncLongArrayAdderPadded   20         55210.6 us    5348.36          8
WrappedAtomicLong          20        285298.1 us   31178.99          2
MyAtomicLong               20       1326989.5 us   91061.53          2
ThreadLocalAtomicLong      20         26945.5 us    1859.65         16
LongAdderArray             20        138279.3 us   11755.80          2
LongAdderArrayPadded       20         33408.9 us    1639.97          8
WrappedLongAdder           20         28179.2 us     590.52         16


My pc has 2 threads per core with 4 cores so we are working with 16 threads in total so there could be 
an issue of the cpu having to wait for a thread to free up since there aren't enough 
availiable to run 20 threads.

## Exercise 9.3.2 
The Padded version is faster since the padding helps to prevent false sharing meaning that the chance of a cache getting 
references they dont need is lowered. This increases the chance that the caches only fetch exactly the address they need 
decreasing false sharing and increasing performance. 

## Exercise 9.3.3
SyncLongArrayAdderPadded   20         53126.3 us    2818.29          8
SyncLongArrayAdderLessPad  20        174168.2 us   16972.69          2

The Less padded version runs alot slower which could be explained by the fact that when we dont add all the locks 
then on the heap all the locks are located right next to eachother this means that when the cache reads from the heap 
then more locks can be pulled in leading to false sharing ruining performance of the program. 

# Exercise 9.4
Memory latency: 134217728 identical operations k = arr[k]
memory cover        8192 B        197724.8 us   21895.08          2
memory cover       11584 B        185877.0 us    3202.98          2
memory cover       16384 B        187616.6 us    6608.79          2
memory cover       23172 B        185206.2 us    1463.20          2
memory cover       32772 B        185479.0 us    1054.59          2
memory cover       46348 B        265101.1 us    1332.35          2
memory cover       65544 B        318248.7 us    2362.72          2
memory cover       92692 B        355706.2 us    3132.05          2
memory cover      131088 B        383021.2 us    1451.55          2
memory cover      185388 B        449022.2 us    3116.19          2
memory cover      262180 B        544204.3 us    3940.72          2
memory cover      370780 B        887664.6 us   31319.41          2
memory cover      524364 B       1172030.6 us  136255.87          2
memory cover      741564 B       1330827.4 us   46025.05          2
memory cover     1048728 B       1541408.3 us  104979.55          2
memory cover     1483124 B       1633344.6 us  146858.55          2

Given the jump in latency indicates the cache