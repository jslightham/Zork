package com.bayviewglen.zork.Entities;

import com.bayviewglen.zork.Items.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList; 

public class Player extends Entity{
	private ArrayList<Item> inventory = new ArrayList<Item>(); 
	private final int INVENTORY_CAPACITY = 120;
	private int currentInventoryWeight;
	
	public Player() {
		super();
	}

	public boolean addToInventory(Item item){
		if(currentInventoryWeight + item.getWeight() < INVENTORY_CAPACITY){
			inventory.add(item);
			return true;
		}
		return false;
	}

	public void removeFromInventory(Item item){
		for(int i =0; i<inventory.size(); i++) {
			if(item.equals(inventory.get(i))) {
				inventory.remove(i);
				return;
			}
		}
		

	}
	
	public ArrayList<Item> getInventory() {
		return inventory;
	}

}
