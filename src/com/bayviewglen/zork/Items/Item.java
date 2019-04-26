package com.bayviewglen.zork.Items;

public class Item {
	private int id;
	private String name;
	private String description;
	private boolean isConsumable;
	private int health;
	private int weight;
	
	public Item(int id, String name, String description, boolean isConsumable, int health, int weight) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.isConsumable = isConsumable;
		this.health = health;
		this.weight = weight;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isConsumable() {
		return isConsumable;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getWeight() {
		return weight;
	}
	public boolean equals(Item item){
		return this.id == item.id && this.name.equals(item.name) && this.description.equals(item.description) && this.isConsumable == item.isConsumable && this.health == item.health && this.weight == item.weight;
	}
	
}
