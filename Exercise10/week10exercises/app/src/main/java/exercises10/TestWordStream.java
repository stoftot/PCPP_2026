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

    System.out.println("----------------------------------------");    
    // 10.1.2:

    System.out.println("----------------------------------------");
    // 10.1.3:

    System.out.println("----------------------------------------");
    // 10.1.4:

    System.out.println("----------------------------------------");
    // 10.1.5:

    System.out.println("----------------------------------------");
    // 10.1.6:
    List<String> cleanWords = null;  // replace with solution

    System.out.println("Number of clean words without duplicates " + cleanWords.size());
    System.out.println("----------------------------------------");
    // 10.1.7:

    System.out.println("----------------------------------------");
    // 10.1.8 and 10.1.9:
   
    System.out.println("----------------------------------------");
    // 10.1.10:

    System.out.println("----------------------------------------");
    // 10.1.11:

    System.out.println("----------------------------------------");
    // 10.1.12: 

    System.out.println("----------------------------------------");
    // 10.1.13:

  }

  // 10.1.7:
  public static boolean isPalindrome(String s) {
    return true; // replace with solution
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
