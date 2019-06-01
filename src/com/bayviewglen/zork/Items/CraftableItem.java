package com.bayviewglen.zork.Items;

import java.util.ArrayList;

public class CraftableItem extends Item{
	private ArrayList<Item> materials;
	public CraftableItem(int id, String name, String description, boolean isConsumable, int health, int weight) {
		super(id, name, description, isConsumable, health, weight);
		materials = new ArrayList<Item>();
	}
	public boolean isCraftable() {
		return true;
	}
	public void addMaterial(Item item) {
		materials.add(item);
	}
	public ArrayList<Item> getMaterials(){
		return materials;
	}
}
