package com.bayviewglen.zork.Items;

public class Bandage extends CraftableItem{

	public Bandage() {
		super(98, "Bandage", "A bandage to stop bleeding", false, 100, 1);
		super.addMaterial(new Robes());
	}
}
