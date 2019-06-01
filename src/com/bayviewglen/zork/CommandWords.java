package com.bayviewglen.zork;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

/*
 * Author:  Michael Kolling.
 * Version: 1.0
 * Date:    July 1999
 * 
 * This class holds an enumeration of all command words known to the game.
 * It is used to recognise commands as they are typed in.
 *
 * This class is part of the "Zork" game.
 */
class CommandWords {
	// Hashmaps that store known words and their types, as well as synonyms to the known words
	private static HashMap<String, String> m_words = new HashMap<String, String>();
	private static HashMap<String, String> m_synonyms = new HashMap<String, String>();
	/**
	 * Constructor - initialise the command words.
	 */
	public CommandWords() {
		// Import words from words.dat into HashMap
		try {
			Scanner in = new Scanner(new File("data/words.dat"));
			while(in.hasNext()){
				String text = in.nextLine();
				String[] textarr = text.split(",");
				m_words.put(textarr[0], textarr[1].substring(1));
			}
            in.close();
		}catch (Exception e) {
                  e.printStackTrace();
		}	
		// import synonyms into hashmap
		try {
			Scanner in = new Scanner(new File("data/synonyms.dat"));
			while(in.hasNext()){
				String text = in.nextLine();
				String[] textarr = text.split(",");
				m_synonyms.put(textarr[0], textarr[1].substring(1));
			}
            in.close();
		}catch (Exception e) {
                  e.printStackTrace();
		}	
	}

	/**
	 * Check whether a given String is a valid command word. Return true if it
	 * is, false if it isn't.
	 **/
	// Check if given string is verb or direction
	public static boolean isCommand(String aString) {
		try {
			return (m_words.get(aString).equals("verb") || m_words.get(aString).equals("direction"));
		}catch(Exception e) {
			return false;
		}
	}
	// Check if given string is a direction
	public static boolean isDirection(String aString) {
		try {
		return m_words.get(aString).equals("direction");
		}catch(Exception e) {
			return false;
		}
	}
	// check if given string is item
	public static boolean isItem(String aString){
		try {
		return m_words.get(aString).equals("item");
		} catch(Exception e) {
			return false;
		}
	}
	// check if given string is enemy
	public static boolean isEnemy(String aString) {
		try {
			return m_words.get(aString).equals("enemy");
			} catch(Exception e) {
				return false;
			}
	}
	// check if given string is riddler
	public static boolean isRiddler(String aString) {
		try {
			return m_words.get(aString).equals("riddler"); 
		}catch(Exception e) {
			return false;
		}
	}
	/*
	 * Print all valid commands to System.out.
	 */
	public void showAll() {
		for (String i : m_words.keySet()) {
			if(m_words.get(i).equals("verb")){
				System.out.print(i + "  ");
			}
		}
		System.out.println();
	}
	/*
	 * If a known word exists for the synonym of the word given, return it. Otherwise return the given word. 
	 */
	public static String replaceSynonym(String word) {
		try {
			String words = m_synonyms.get(word);
			if(words == null) {
				throw new Exception();
			}else
				return words;
			} catch(Exception e) {
				return word;
			}
	}

	
}