package com.bayviewglen.zork.Entities.Enemies;

import com.bayviewglen.zork.Entities.Entity;

public class Enemy extends Entity{
	private int damageGiven; 
	private String name;
	private String description;
	private String room;
	
	public Enemy(){
		super(100.0, 100.0); 
		damageGiven = 10; 
	}
	
	public Enemy(int damageGiven){
		super(100.0, 100.0); 
		this.damageGiven = damageGiven; 
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
}
