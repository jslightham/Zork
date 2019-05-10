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

	public boolean addToInventory(String item){
		Class<?> clazz;
		Item object;
		try {
			clazz = Class.forName("com.bayviewglen.zork.Items." + item.trim());
			Constructor<?> ctor = clazz.getConstructor();
			object = (Item) ctor.newInstance();
			if(currentInventoryWeight + object.getWeight() < INVENTORY_CAPACITY){
				inventory.add(object);
				
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public void removeFromInventory(Item item){
		inventory.remove(item);

	}

}
