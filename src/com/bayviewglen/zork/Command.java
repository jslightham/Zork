package com.bayviewglen.zork;

import java.util.ArrayList;

/**
 * Class Command - Part of the "Zork" game.
 * 
 * author: Michael Kolling version: 1.0 date: July 1999
 *
 * This class holds information about a command that was issued by the user. A
 * command currently consists of two strings: a command word and a second word
 * (for example, if the command was "take map", then the two strings obviously
 * are "take" and "map").
 * 
 * The way this is used is: Commands are already checked for being valid command
 * words. If the user entered an invalid command (a word that is not known) then
 * the command word is <null>.
 *
 * If the command had only one word, then the second word is <null>.
 *
 * The second word is not checked at the moment. It can be anything. If this
 * game is extended to deal with items, then the second part of the command
 * should probably be changed to be an item rather than a String.
 * test
 */
class Command {
	private String commandWord;
	private String direction;
	private ArrayList<String> otherWords;
	private String item;
	private String enemy;
	private String riddler; 

	/**
	 * Create a command object. First and second word must be supplied, but
	 * either one (or both) can be null. The command word should be null to
	 * indicate that this was a command that is not recognised by this game.
	 */
	public Command(String firstWord, ArrayList<String> otherWords, String direction, String item, String enemy, String riddler) {
		commandWord = firstWord;
		this.otherWords = otherWords;
		this.direction = direction;
		if(direction.equals("e"))
			this.direction = "east";
		if(direction.equals("w"))
			this.direction = "west";
		if(direction.equals("s"))
			this.direction = "south";
		if(direction.equals("n"))
			this.direction = "north";
		if(direction.equals("u"))
			this.direction = "up";
		if(direction.equals("d"))
			this.direction = "down";
		this.item = item;
		this.enemy = enemy;
		this.riddler = riddler; 
	}

	/**
	 * Return the command word (the first word) of this command. If the command
	 * was not understood, the result is null.
	 */
	public String getCommandWord() {
		return commandWord;
	}

	/**
	 * Return the second word of this command. Returns null if there was no
	 * second word.
	 */
	public ArrayList<String> getOtherWords() {
		return otherWords;
	}
	/*
	public String getSecondWord() {
		return otherWords.get(0);
	}
	*/

	/**
	 * Return true if this command was not understood.
	 */
	public boolean isUnknown() {
		return (commandWord == null);
	}

	/**
	 * Return true if the command has a second word.
	 */
	public boolean hasSecondWord() {
		return otherWords.size() > 0;
	}
	
	public boolean hasItem(){
		return !item.equals("");
	}
	public boolean hasDirection() {
		return CommandWords.isDirection(direction);
	}
	public String getDirection() {
		return direction;
	}
	public String getItem() {
		return item;
	}
	public String getEnemy() {
		return enemy;
	}
	public boolean hasRiddler() {
		return CommandWords.isRiddler(riddler); 
	}
	public boolean hasEnemy() {
		return !enemy.equals("");
	}
	
	
}
