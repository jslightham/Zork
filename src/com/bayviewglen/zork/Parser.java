
package com.bayviewglen.zork;

/*
 * Author:  Michael Kolling
 * Version: 1.0
 * Date:    July 1999
 * 
 * This class is part of Zork. Zork is a simple, text based adventure game.
 *
 * This parser reads user input and tries to interpret it as a "Zork"
 * command. Every time it is called it reads a line from the terminal and
 * tries to interpret the line as a two word command. It returns the command
 * as an object of class Command.
 *
 * The parser has a set of known command words. It checks user input against
 * the known commands, and if the input is not one of the known commands, it
 * returns a command object that is marked as an unknown command.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import com.bayviewglen.zork.CommandWords;

class Parser {
	private CommandWords commands; // holds all valid command words

	public Parser() {
		commands = new CommandWords();
	}

	public Command getCommand() {
		// If the following are blank, this means that the input line did not contain that specific type of word. 
		String inputLine = ""; // will hold the full input line
		String verb = ""; // holds verb of command
		String direction = ""; // holds direction of command
		String item = ""; // holds item of command
		String enemy = ""; // holds enemies in command
		String riddler = ""; // holds riddler in command
		boolean open = false;
		//Store all the words in the input line in an arraylist
		ArrayList<String> words = new ArrayList<String>();
		// Where unknown words are stored
		ArrayList<String> otherWords = new ArrayList<String>();
 		System.out.print("> "); // print prompt
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			inputLine = reader.readLine();
		} catch (java.io.IOException exc) {
			System.out.println("There was an error during reading: " + exc.getMessage());
		}
		// convert inputLine into tokens, and put this into the ArrayList of words
		StringTokenizer tokenizer = new StringTokenizer(inputLine.toLowerCase());
		while(tokenizer.hasMoreTokens()) {
			words.add(tokenizer.nextToken());
		}
		// For each word, check what type it is, and store it in its respective String variable if it matches. 
		for(int i=0; i<words.size(); i++) {
			// replace all known synonyms
			words.set(i, CommandWords.replaceSynonym(words.get(i)));
			// Special case - check if contains open or unlock as a verb, otherwise these will be overwritten by the directions. 
			if(words.get(i).equals("open") || words.get(i).equals("unlock")) {
				open = true;
			}
			if(CommandWords.isCommand(words.get(i))) {
				verb = words.get(i);
				if(CommandWords.isDirection(words.get(i))) {
					direction = words.get(i);
				}
			}else if(CommandWords.isItem(words.get(i))){
				item = words.get(i);
			}else if(CommandWords.isEnemy(words.get(i))){
				enemy = words.get(i);
			}else if(CommandWords.isRiddler(words.get(i))) {
				riddler = words.get(i); 
			}else{
				otherWords.add(words.get(i));
			}
		}
		// Create the command
		if (CommandWords.isCommand(verb))
			if(!open)
				return new Command(verb, otherWords, direction, item, enemy, riddler);
			else
				return new Command("open", otherWords, direction, item, enemy, riddler);
		else
			return new Command(null, otherWords, direction, item, enemy, riddler);
	}

	/**
	 * Print out a list of valid command words.
	 */
	public void showCommands() {
		commands.showAll();
	}
}
