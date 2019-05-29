
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
		String inputLine = ""; // will hold the full input line
		String verb = "";
		String direction = "";
		String item = "";
		String enemy = "";
		String riddler = ""; 
		boolean open = false;
		//String word2;
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> otherWords = new ArrayList<String>();
 		System.out.print("> "); // print prompt
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			inputLine = reader.readLine();
		} catch (java.io.IOException exc) {
			System.out.println("There was an error during reading: " + exc.getMessage());
		}
		StringTokenizer tokenizer = new StringTokenizer(inputLine.toLowerCase());
		while(tokenizer.hasMoreTokens()) {
			words.add(tokenizer.nextToken());
		}
		for(int i=0; i<words.size(); i++) {
			words.set(i, CommandWords.replaceSynonym(words.get(i)));
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
		//System.out.println(verb);
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
