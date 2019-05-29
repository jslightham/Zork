package com.bayviewglen.zork.Entities;

import com.bayviewglen.zork.Items.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList; 

public class Player extends Entity{
	private ArrayList<Item> inventory = new ArrayList<Item>(); 
	private final int INVENTORY_CAPACITY = 120;
	private int currentInventoryWeight;
	
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
		// TODO Do we want health or hunger?
		health+=5;
		hunger+=5;
		if(health > 100.0) {
			health = 100.0;
		}
		if(hunger > 100.0) {
			hunger = 100.0;
		}
	}

}
