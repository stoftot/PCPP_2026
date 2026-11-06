// PCPP 
// sestoft@itu.dk * 2026-10-26

package exercises10; 

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestSpellingBee {
  public static void main(String[] args) {
    String filename = "words.txt";
    // 10.2.1:
    List<String> cleanWords = readWords(filename)
            .map(String::toLowerCase)
            .filter(s -> s.length() >= 4 && onlyaz(s))
            .distinct()
            .toList();

    System.out.println("Number of clean words " + cleanWords.size());
    System.out.println("----------------------------------------");    
    // 10.2.4:
    System.out.println(new Puzzle('h', letterSet("chikmnu"))
            .solutions(cleanWords)
            .count());

    System.out.println("----------------------------------------");
    // 10.2.5:
    List<Puzzle> puzzles = cleanWords.stream()
            .map(TestSpellingBee::letterSet)
            .filter(s -> s.size() == 7)
            .distinct()
            .flatMap(s -> s.stream()
                    .map(c -> new Puzzle(c, s)))
            .toList();

    System.out.println("Number of puzzles " + puzzles.size());
    System.out.println("----------------------------------------");
    // 10.2.6:
    puzzles.stream().limit(20).forEach(System.out::println);

    System.out.println("----------------------------------------");
    // 10.2.7: Warning: SLOW
//    List<Puzzle> goodPuzzles = null;  // replace with solution
//
//    System.out.println("Number of good puzzles " + goodPuzzles.size());
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
  
  public static Set<Character> letterSet(String s) {
    return s.chars().mapToObj(i -> (char)i).collect(Collectors.toSet());
  }

  static class Puzzle {
    final char m;
    final Set<Character> W;
    public Puzzle(char m, Set<Character> W) {
      this.m = m;
      this.W = W;
    }

    // 10.2.2:
    public boolean hasSolution(String s) {
      return (s.indexOf(m) != -1) && W.containsAll(letterSet(s));
    }

    // 10.2.3:
    public Stream<String> solutions(List<String> cleanWords) {
      return cleanWords.stream()
              .filter(this::hasSolution);
    }

    public String toString() {
      return String.format("(%c, %s)", m, W.stream().map(String::valueOf).collect(Collectors.joining(",", "{", "}")));
    }
  }
}
