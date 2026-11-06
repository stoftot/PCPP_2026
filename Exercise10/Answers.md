# 10.1
## 10.1.1
```java
System.out.println(readWords(filename).count());
```
## 10.1.2
```java
readWords(filename)
            .limit(20)
            .forEach(System.out::println);
```
A
a
AA
AAA
Aachen
Aachen's
Aaliyah
Aaliyah's
aardvark
aardvark's
aardvarks
Aaron
Aaron's
AA's
AB
abaci
aback
abacus
abacuses
abacus's
## 10.1.3
```java
readWords(filename)
            .filter(s -> s.length() > 22)
            .forEach(System.out::println);
```
electroencephalograph's
## 10.1.4
```java
readWords(filename)
            .map(String::toLowerCase)
            .limit(20)
            .forEach(System.out::println);
```
a
a
aa
aaa
aachen
aachen's
aaliyah
aaliyah's
aardvark
aardvark's
aardvarks
aaron
aaron's
aa's
ab
abaci
aback
abacus
abacuses
abacus's
## 10.1.5
```java
System.out.println
    (readWords(filename)
            .map(String::toLowerCase)
            .filter(s -> s.length() != 1 && onlyaz(s))
            .count());
```
## 10.1.6
```java
List<String> cleanWords = readWords(filename)
            .map(String::toLowerCase)
            .filter(s -> s.length() != 1 && onlyaz(s))
            .distinct()
            .collect(Collectors.toList());

    System.out.println("Number of clean words without duplicates " + cleanWords.size());
```
Number of clean words without duplicates 73419
## 10.1.7
```java
cleanWords.stream()
            .filter(TestWordStream::isPalindrome)
            .forEach(System.out::println);
```
aa
aaa
ada
aha
ala
ama
ana
anna
ara
ava
bb
bbb
bib
bob
boob
cc
cfc
civic
dad
dd
deced
deed
deified
did
dud
dvd
eke
ere
eve
ewe
eye
fsf
gag
gig
gog
hah
hannah
hrh
huh
ii
iii
kayak
kkk
kook
laval
level
madam
malayalam
mgm
minim
mm
mom
mum
nan
non
noon
nun
oho
ono
otto
pap
peep
pep
pgp
php
pip
poop
pop
pp
pup
radar
redder
refer
rotor
rr
sagas
salas
sees
sexes
shahs
sis
solos
sos
ss
stats
tat
tenet
tet
tevet
tit
tnt
toot
tot
tut
wow
www
xix
xx
xxx
## 10.1.8-9
```java
Map<Integer, List<String>> lengthGroups = cleanWords.stream()
            .collect(Collectors.groupingBy(String::length));

    lengthGroups.get(19).forEach(System.out::println);
```
chlorofluorocarbons
counterintelligence
electrocardiographs
interdenominational
nonrepresentational
oversimplifications
## 10.1.10
```java
cleanWords.stream()
            .map(TestWordStream::counts)
            .limit(20)
            .forEach(System.out::println);
```
{a=2}
{a=3}
{a=2, c=1, e=1, h=1, n=1}
{a=3, h=1, i=1, l=1, y=1}
{a=3, d=1, k=1, r=2, v=1}
{a=3, d=1, k=1, r=2, s=1, v=1}
{a=2, n=1, o=1, r=1}
{a=1, b=1}
{a=2, b=1, c=1, i=1}
{a=2, b=1, c=1, k=1}
{a=2, b=1, c=1, s=1, u=1}
{a=2, b=1, c=1, e=1, s=2, u=1}
{a=2, b=1, f=1, t=1}
{a=2, b=1, e=1, l=1, n=1, o=1}
{a=2, b=1, e=1, l=1, n=1, o=1, s=1}
{a=2, b=1, d=1, n=2, o=1}
{a=2, b=1, d=2, e=1, n=2, o=1}
{a=2, b=1, d=1, g=1, i=1, n=3, o=1}
{a=2, b=1, d=1, e=1, m=1, n=3, o=1, t=1}
{a=2, b=1, d=1, n=2, o=1, s=1}
## 10.1.11
```java
Map<Map<Character,Long>, List<String>> anagram1 =
            cleanWords.stream()
                    .collect(Collectors.groupingBy(TestWordStream::counts));

    anagram1.entrySet().stream()
            .filter(kv -> kv.getValue().size()>1)
            .forEach(System.out::println);

    System.out.println(anagram1.entrySet().stream()
            .filter(kv -> kv.getValue().size()>1).count());
```
4712
## 10.1.12
```java
// 10.1.12:
    var timer = new Timer();
    timer.play();
    System.out.println(
            cleanWords.stream()
                    .collect(Collectors.groupingBy(TestWordStream::counts)).size());
    timer.pause();
    System.out.println("sequential :" + timer.check());

    timer.play();
    System.out.println(
            cleanWords.parallelStream()
                    .collect(Collectors.groupingBy(TestWordStream::counts)).size());
    timer.pause();
    System.out.println("parallel :" + timer.check());

```
67434
sequential :3.00127354
67434
parallel :3.245773859

# 10.2
## 10.2.1
```java
List<String> cleanWords = readWords(filename)
            .map(String::toLowerCase)
            .filter(s -> s.length() >= 4 && onlyaz(s))
            .distinct()
            .toList();

    System.out.println("Number of clean words " + cleanWords.size());   
```
## 10.2.2
```java
public boolean hasSolution(String s) {
      return (s.indexOf(m) != -1) && W.containsAll(letterSet(s));
    }
```
## 10.2.3
```java
public Stream<String> solutions(List<String> cleanWords) {
      return cleanWords.stream()
              .filter(this::hasSolution);
    }
```
## 10.2.4
```java
System.out.println(new Puzzle('h', letterSet("chikmnu"))
            .solutions(cleanWords)
            .count());
```
## 10.2.5
```java
List<Puzzle> puzzles = cleanWords.stream()
            .map(TestSpellingBee::letterSet)
            .filter(s -> s.size() == 7)
            .distinct()
            .flatMap(s -> s.stream()
                    .map(c -> new Puzzle(c, s)))
            .toList();

    System.out.println("Number of puzzles " + puzzles.size());
```
## 10.2.6
```java
puzzles.stream().limit(20).forEach(System.out::println);
```
(a, {a,b,s,e,l,n,o})
(b, {a,b,s,e,l,n,o})
(s, {a,b,s,e,l,n,o})
(e, {a,b,s,e,l,n,o})
(l, {a,b,s,e,l,n,o})
(n, {a,b,s,e,l,n,o})
(o, {a,b,s,e,l,n,o})
(a, {a,b,d,g,i,n,o})
(b, {a,b,d,g,i,n,o})
(d, {a,b,d,g,i,n,o})
(g, {a,b,d,g,i,n,o})
(i, {a,b,d,g,i,n,o})
(n, {a,b,d,g,i,n,o})
(o, {a,b,d,g,i,n,o})
(a, {a,b,s,t,e,m,n})
(b, {a,b,s,t,e,m,n})
(s, {a,b,s,t,e,m,n})
(t, {a,b,s,t,e,m,n})
(e, {a,b,s,t,e,m,n})
(m, {a,b,s,t,e,m,n})

