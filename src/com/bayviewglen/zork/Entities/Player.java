package com.bayviewglen.zork.Entities;

import com.bayviewglen.zork.Items.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList; 

public class Player extends Entity{
	/*
	 * Player class that extends the main entity class.
	 * Takes care of inventory and health of the player as well as bleeding status.
	 */
	private ArrayList<Item> inventory = new ArrayList<Item>(); 
	private final int INVENTORY_CAPACITY = 120;
	private int currentInventoryWeight;
	private boolean isBleeding;
	
	public Player() {
		super(100.0, 100.0);
	}

	public boolean addToInventory(Item item){
		if(currentInventoryWeight + item.getWeight() < INVENTORY_CAPACITY){
			currentInventoryWeight+= item.getWeight();
			inventory.add(item);
			return true;
		}
		return false;
	}

	public void removeFromInventory(Item item){
		for(int i =0; i<inventory.size(); i++) {
			if(item.equals(inventory.get(i))) {
				inventory.remove(i);
				currentInventoryWeight-= item.getWeight();
				return;
			}
		}
		

	}
	
	public ArrayList<Item> getInventory() {
		return inventory;
	}
	
	public void eat() {
		health+=25;
		if(health > 100.0) {
			health = 100.0;
		}
	}
	
	public void setBleeding(boolean bleeding) {
		this.isBleeding = bleeding;
	}
	
	public boolean getBleeding() {
		return isBleeding;
	}

}
