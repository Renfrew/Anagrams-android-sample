/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Modified by Liang Chen in 2020 August */

package com.google.engedu.anagrams;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private ArrayList<String> wordList = new ArrayList<>();
    private HashSet<String> wordSet = new HashSet<>();
    private HashMap<String, LinkedList<String>> lettersToWord = new HashMap<>();
    private HashMap<Integer, ArrayList<String>> sizeToWord = new HashMap<>();
    private int worldLength = DEFAULT_WORD_LENGTH;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        List<String> referenceList;
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            String sortedWord = sortLetters(word);
            wordSet.add(word);
            wordList.add(word);

            if ((referenceList = lettersToWord.get(sortedWord)) != null) {
                referenceList.add(word);
            } else {
                referenceList = new LinkedList<>();
                referenceList.add(word);
                lettersToWord.put(sortedWord, (LinkedList<String>) referenceList);
            }

            if ((referenceList = sizeToWord.get(word.length())) != null) {
                referenceList.add(word);
            } else {
                referenceList = new ArrayList<>();
                referenceList.add(word);
                sizeToWord.put(word.length(), (ArrayList<String>) referenceList);
            }
        }
    }

    public boolean isGoodWord(String word, String base) {
        if (!wordSet.contains(word))
            return false;

        if (word.contains(base))
            return false;

        return true;
    }

    public List<String> getAnagrams(String targetWord) {
        String sortedWord = sortLetters(targetWord);
        List<String> result;
        if ((result = lettersToWord.get(sortedWord)) != null)
            return result;
        else
            return new LinkedList<>();
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder strBuilder = new StringBuilder(word);

        for (char c = 'a'; c <= 'z'; ++c) {
            strBuilder.insert(0, c);

            List<String> anagrams = getAnagrams(strBuilder.toString());
            result.addAll(anagrams);

            strBuilder.deleteCharAt(0);
        }

        return result;
    }

    public String pickGoodStarterWord() {
        while (true) {
            // Get the word list that has wordLength length
            ArrayList<String> words = sizeToWord.get(worldLength);

            // Get a random word
            assert words != null;
            int rand = random.nextInt(words.size());
            String tempWord = words.remove(rand);

            // Check if the word is a good starter word
            List<String> anagrams = getAnagramsWithOneMoreLetter(tempWord);
            if (anagrams.size() >= MIN_NUM_ANAGRAMS) {
                if (worldLength < MAX_WORD_LENGTH)
                    ++worldLength;

                return tempWord;
            }
        }
    }

    private String sortLetters(String word) {
        char[] charArray = word.toCharArray();
        Arrays.sort(charArray);
        return new String(charArray);
    }
}
