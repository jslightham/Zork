package com.bayviewglen.zork.Entities;

public class Riddler extends Entity {
	Riddle riddle; 
	String message; 

	public Riddler(double health, double hunger, Riddle riddle) {
		super(health, hunger);
		this.riddle = riddle; 
		this.message = "I can help you in your quest, but only if you answer my riddle."; 
	}

}
