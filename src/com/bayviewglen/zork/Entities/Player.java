package com.bayviewglen.zork.Entities;

import com.bayviewglen.zork.Items.Item;
import java.util.ArrayList; 

public class Player extends Entity{
	private ArrayList<Item> inventory = new ArrayList<Item>(); 
	private final int INVENTORY_CAPACITY = 120;
	private int currentInventoryWeight;
	
	public Player() {
		super();
	}

	private boolean addToInventory(Item item){
		if(currentInventoryWeight + item.getWeight() < INVENTORY_CAPACITY){
			inventory.add(item);
			System.out.println(item.getName() + " add");
			return true;
		}
		return false;
	}

	private void removeFromInventory(Item item){
		inventory.remove(item);

	}

}
