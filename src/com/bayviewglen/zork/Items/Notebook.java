package com.bayviewglen.zork.Items;
public class Notebook extends Item{
	
	private String hint;
	public Notebook(){
		super(15, "Notebook", "A book filled with diagrams and descriptions, belonging to Henry Pellatt", false, 50, 1);
		hint = "Fire escape plan for Casa Loma:\n1. Acquire key to front door\n2. Make battering ram from point, cylinder, and base\n3. Use both to force front door open.\n4. P.S. Don't forget Shaving Cream!";
	}
	public String getHint() {
		return hint; 
	}

}
