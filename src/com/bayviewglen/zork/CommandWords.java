package com.bayviewglen.zork;

import java.util.HashMap;

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
	// a constant array that holds all valid command words
	private static HashMap<String, String> m_words = new HashMap<String, String>();;
	/**
	 * Constructor - initialise the command words.
	 */
	public CommandWords() {
		m_words.put("go", "verb");
		m_words.put("quit", "verb");
		m_words.put("help", "verb");
		m_words.put("jump", "verb");
		m_words.put("north", "direction");
		m_words.put("south", "direction");
		m_words.put("west", "direction");
		m_words.put("east", "direction");
		m_words.put("n", "direction");
		m_words.put("w", "direction");
		m_words.put("s", "direction");
		m_words.put("e", "direction");
		m_words.put("up", "direction");
		m_words.put("down", "direction");
	}

	/**
	 * Check whether a given String is a valid command word. Return true if it
	 * is, false if it isn't.
	 **/
	public static boolean isCommand(String aString) {
		try {
			return (m_words.get(aString).equals("verb") || m_words.get(aString).equals("direction"));
		}catch(Exception e) {
			return false;
		}
		/*
		for (int i = 0; i < validCommands.length; i++) {
			if (validCommands[i].equals(aString))
				return true;
		}
		// if we get here, the string was not found in the commands
		return false;
		*/
	}
	public static boolean isDirection(String aString) {
		return m_words.get(aString).equals("direction");
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
		
		/*
		for (int i = 0; i < validCommands.length; i++) {
			System.out.print(validCommands[i] + "  ");
		}
		System.out.println();
		*/
	}

	/*
	public boolean isVerb(String string) {
		try {
		if(m_words.get(string).equals("verb")) 
			return true;
		} catch(Exception e) {
		return false;
		}
		return false;
	}
	*/
}
