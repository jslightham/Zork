package com.bayviewglen.zork.Items;

public class Batteringram extends CraftableItem{
	public Batteringram() {
		super(99, "Battering Ram", "Description", false, 1, 1);
		super.addMaterial(new Base());
		super.addMaterial(new Cylinder());
		super.addMaterial(new Point());
	}
}