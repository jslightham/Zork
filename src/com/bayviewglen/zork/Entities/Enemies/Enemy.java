package com.bayviewglen.zork.Entities.Enemies;

import com.bayviewglen.zork.Entities.Entity;
/*
 * This is the enemy class, which extends the entity class. 
 * The enemy class is instantiated in the initiate enemies method of the game class, and stores information such as damage given, name, description, room, loot, and whether or not it is blinded. 
 */
public class Enemy extends Entity{
	private int damageGiven; 
	private String name;
	private String description;
	private String room;
	private String loot;
	private boolean blinded;
	
	public Enemy(){
		// Call super constructor to store health and hunger. 
		super(100.0, 100.0); 
		damageGiven = 10; 
		blinded = false;
	}
	
	public Enemy(int damageGiven){
		super(100.0, 100.0); 
		this.damageGiven = damageGiven; 
		blinded = false;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	public void setRoom(String room) {
		this.room = room;
	}
	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getRoom() {
		return room;
	}
	public void setDamageGiven(int damageGiven) {
		this.damageGiven = damageGiven;
	}
	public int getDamage() {
		return this.damageGiven;
	}
	public boolean getBlinded() {
		return blinded;
	}
	public void setBlinded(boolean blinded) {
		this.blinded = blinded;
	}
	public void setLoot(String loot) {
		this.loot = loot;
	}
	public String getLoot() {
		return loot;
	}
}
