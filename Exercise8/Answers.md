# Exercise 1

# Exercise 1.1 
# OS:   Linux; 6.16.3-76061603-generic; amd64
# JVM:  Oracle Corporation; 24.0.1
# CPU:  null; 8 "cores"
# Date: 2026-10-19T09:45:42+0200
pow                                  19.4 ns       0.44   16777216
exp                                   9.0 ns       0.08   33554432
log                                  12.6 ns       0.32   33554432
sin                                  15.0 ns       0.18   16777216
cos                                  17.2 ns       1.15   16777216
tan                                  23.7 ns       0.27   16777216
asin                                 15.0 ns       1.03   33554432
acos                                 15.0 ns       0.13   33554432
atan                                 15.5 ns       0.09   16777216


second run: 
pow                                  24.2 ns       7.32   16777216
exp                                   8.9 ns       0.27   33554432
log                                  11.7 ns       0.79   33554432
sin                                  14.5 ns       0.35   33554432
cos                                  15.2 ns       0.70   33554432
tan                                  20.9 ns       0.55   16777216
asin                                 12.3 ns       0.10   33554432
acos                                 12.5 ns       0.60   33554432
atan                                 12.7 ns       0.07   33554432
 

Numbers are quite similar differences could be from running inside and outside IDE 

## Exercise 1.2 
# Date: 2026-10-19T10:08:06+0200
sqrt                                  1.8 ns       0.31  268435456
random                               18.8 ns       0.11   16777216


## Exercise 1.3 
# Date: 2026-10-19T10:10:30+0200
sqrt of rand                         19.8 ns       0.10   16777216



# Exercise 2 

## Exercise 2.1 
For all of the benchmarks the average and deviation times are very high for small iterations counts and decreases as interation counts increase 
This could be explained execution either being interupted by other threads. The chance of the happening decreases with more threads so the average 
exeution time decreases. This could also be due to the JVM having to boot up for low iterations but this has already happened for larger iterations 
decreasing the average. 

For virtual threads we see lower creation and lifecycle overhead than the platform threads, which is consistent to what we expect. 

## Exercise 2.2

# Date: 2026-10-19T10:14:25+0200
hashCode()                            2.4 ns       0.01  134217728
Point creation                       36.8 ns       0.13    8388608
Thread's work                      4951.2 ns     147.13      65536
Thread pltf cre                     105.7 ns       4.94    4194304
Thread pltf cre strt              52218.9 ns    2028.96       8192
Thread pltf cre strt join         73948.0 ns    4432.69       4096
Thread virt cre                      46.7 ns       2.43    8388608
Thread virt cre strt join          8683.6 ns     179.52      32768
Uncontended lock                     19.4 ns       0.11   16777216


these resulst are pretty plausible we see virtual threads with less overhead than platform threads and the create step being cheap 
with start and join taking more overhead. 

## Exercise 2.3 

We  can get the star and join steps by subtracting the thread work from the measurements including starting and joining costs: 

Creating: 105 ns 
Starting: cre start - work = 52218.9 ns - 4951.2 ns = 47 us
Joining:  cre start join - cre strt - work = 73948.0 ns - 52218.9 ns - 4951.2 ns = 16 us

This seems to make sense as we would expect the start to take more time the than the joining step. 


# Exercise 3 
## Excercise 3.1 
# OS:   Linux; 6.16.3-76061603-generic; amd64
# JVM:  Oracle Corporation; 24.0.1
# CPU:  null; 8 "cores"
# Date: 2026-10-19T10:35:24+0200
quicksort                          7802.6 us      62.05         32


## Exercise 3.2 
arrays sort                         981.9 us      41.21        256


Yes javas own sorting algorithm is faster then the exercises implemented quicksort algorithm 

## Exercise 3.3 

arrays parallelsort                         443.7 us       5.77       1024
Parallel is faster than regular this is pausible since we with clever concurrency should be able to speed up performance

## Exercise 3.4 
quicksort parallel                62631.3 us    2673.20          8

It is slower than the non parallel due to alot of thread startup time.

## Exercise 3.5 
quicksort parallel imp             3707.5 us     385.08         64

Improved version is now faster due to the optimization of not spawning execesive threads during the recursive calls


# Exercise 4 


## Exercise 4.1 
long holder                           2.4 ns       0.01  134217728

## Exercise 4.2 
# Date: 2026-10-19T11:15:42+0200
long holder                           2.4 ns       0.02  134217728
long holder vol                       2.6 ns       0.13  134217728

The average time doesnt increase that much but the deviation does. One could expect the time to increase more with volatile 
variables since it needs to flush to higher cash levels increasing the execution time.