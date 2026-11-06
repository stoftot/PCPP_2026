// PCPP 
// sestoft@itu.dk * 2015-09-08, 2026-10-26

package exercises10; 

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestWordStream {
  public static void main(String[] args) {
    String filename = "words.txt";
    // 10.1.1:
    System.out.println(readWords(filename).count());

    System.out.println("----------------------------------------");
    // 10.1.2:
    readWords(filename)
            .limit(20)
            .forEach(System.out::println);

    System.out.println("----------------------------------------");
    // 10.1.3:
    readWords(filename)
            .filter(s -> s.length() > 22)
            .forEach(System.out::println);

    System.out.println("----------------------------------------");
    // 10.1.4:
    readWords(filename)
            .map(String::toLowerCase)
            .limit(20)
            .forEach(System.out::println);

    System.out.println("----------------------------------------");
    // 10.1.5:
    System.out.println
    (readWords(filename)
            .map(String::toLowerCase)
            .filter(s -> s.length() != 1 && onlyaz(s))
            .count());

    System.out.println("----------------------------------------");
    // 10.1.6:
    List<String> cleanWords = readWords(filename)
            .map(String::toLowerCase)
            .filter(s -> s.length() != 1 && onlyaz(s))
            .distinct()
            .collect(Collectors.toList());

    System.out.println("Number of clean words without duplicates " + cleanWords.size());
    System.out.println("----------------------------------------");
    // 10.1.7:
    cleanWords.stream()
            .filter(TestWordStream::isPalindrome)
            .forEach(System.out::println);


    System.out.println("----------------------------------------");
    // 10.1.8 and 10.1.9:
    Map<Integer, List<String>> lengthGroups = cleanWords.stream()
            .collect(Collectors.groupingBy(String::length));

    lengthGroups.get(19).forEach(System.out::println);

    System.out.println("----------------------------------------");
    // 10.1.10:
    cleanWords.stream()
            .map(TestWordStream::counts)
            .limit(20)
            .forEach(System.out::println);

    System.out.println("----------------------------------------");
    // 10.1.11:
    Map<Map<Character,Long>, List<String>> anagram1 =
            cleanWords.stream()
                    .collect(Collectors.groupingBy(TestWordStream::counts));

    anagram1.entrySet().stream()
            .filter(kv -> kv.getValue().size()>1)
            .forEach(System.out::println);

    System.out.println(anagram1.entrySet().stream()
            .filter(kv -> kv.getValue().size()>1).count());

    System.out.println("----------------------------------------");
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


    System.out.println("----------------------------------------");
    // 10.1.13:

  }

  // 10.1.7:
  public static boolean isPalindrome(String s) {
    for(int i = 0; i < s.length() / 2; i++) {
      if (s.charAt(i) != s.charAt(s.length() - 1 - i))
        return false;
    }
    return true;
  }

  // 10.1.14:
  public static Map<Character,Long> counts2(String s) {
    return null;  // replace with solution
  }

  public static Stream<String> readWords(String filename) {
    try {      
      return new BufferedReader(new FileReader(filename)).lines();
    } catch (IOException exn) { 
      return Stream.<String>empty();
    }
  }

  public static boolean onlyaz(String s) {
    return s.chars().allMatch(ch -> 'a' <= ch && ch <= 'z');  
  }
    
  public static Map<Character,Long> counts(String s) {
    Map<Character,Long> res = new TreeMap<>();
    for (char c : s.toCharArray()) {
      res.putIfAbsent(c, 0L);
      res.put(c, res.get(c) + 1);
    }
    return res;
  }
}
