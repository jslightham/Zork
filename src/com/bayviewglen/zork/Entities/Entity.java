package com.bayviewglen.zork.Entities;

public class Entity {
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
