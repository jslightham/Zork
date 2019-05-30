package com.bayviewglen.zork.Entities;
import com.bayviewglen.zork.Items.Item;

public class Riddler extends Entity {
	Riddle riddle; 
	String message;
	Item prize; 

	public Riddler(double health, double hunger, Riddle riddle, String message) {
		super(health, hunger);
		this.riddle = riddle; 
		this.message = message; 
	}

	public Riddle getRiddle() {
		return riddle;
	}

	public String getMessage() {
		return message;
	}
	

}
