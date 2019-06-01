package com.bayviewglen.zork.Entities;

public class Entity {
	/*
	 * The base class of any character in the game. 
	 * All characters have a health, which is taken care of by this class. Hunger is not used. 
	 */
	protected double hunger; 
	protected double health; 
	
	public Entity(double health, double hunger) {
		this.health = health;
		this.hunger = hunger;
	}
	
	public double getHealth() {
		return this.health;
	}
	public void setHealth(double health) {
		this.health = health;
	}
}
