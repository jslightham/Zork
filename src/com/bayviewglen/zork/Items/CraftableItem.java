package com.bayviewglen.zork.Items;

import java.util.ArrayList;
/*
 * Extends the item class, and is extended by all items that can be crafted.
 * Stores the items required to craft the specific item. 
 */
public class CraftableItem extends Item{
	private ArrayList<Item> materials;
	public CraftableItem(int id, String name, String description, boolean isConsumable, int health, int weight) {
		super(id, name, description, isConsumable, health, weight);
		materials = new ArrayList<Item>();
	}
	// override the isCraftable method from the item class, stating that craftable items are craftable. 
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
