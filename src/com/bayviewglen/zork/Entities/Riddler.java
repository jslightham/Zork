package com.bayviewglen.zork.Entities;
import com.bayviewglen.zork.Items.Item;

public class Riddler extends Entity {
	/*
	 * Create the riddler character, which is an entity. 
	 * Holds a riddle, and has an item as a prize. 
	 * Riddlers are instantiated by the room loading method at the begining of the game class. 
	 */
	Riddle riddle; 
	String message;
	Item prize; 

	public Riddler(double health, double hunger, Riddle riddle, String message, Item prize) {
		super(health, hunger);
		this.riddle = riddle; 
		this.message = message; 
		this.prize = prize; 
	}

	public Riddle getRiddle() {
		return riddle;
	}

	public String getMessage() {
		return message;
	}
	
	public Item getPrize() {
		return prize;
	}
	

}
