package com.bayviewglen.zork;

/*
 * Class Room - a room in an adventure game.
 *
 * Author:  Michael Kolling
 * Version: 1.1
 * Date:    August 2000
 * 
 * This class is part of Zork. Zork is a simple, text based adventure game.
 *
 * "Room" represents one location in the scenery of the game.  It is 
 * connected to at most four other rooms via exits.  The exits are labelled
 * north, east, south, west.  For each direction, the room stores a reference
 * to the neighbouring room, or null if there is no exit in that direction.
 */
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.bayviewglen.zork.Entities.*;
import com.bayviewglen.zork.Items.*;

class Room {
	private String roomName;
	private String description;
	private HashMap<String, Room> exits; // stores exits of this room.
	private ArrayList<Item> items;
	private Riddler riddler; //needs to altered outside of the class so that riddler can be set to null.
	private boolean locked; // Otherwise you can repeatedly solve the riddle and get unlimited items 
	private boolean boarded;

	/**
	 * Create a room described "description". Initially, it has no exits.
	 * "description" is something like "a kitchen" or "an open court yard".
	 */
	public Room(String description) {
		this.description = description;
		exits = new HashMap<String, Room>();
		items = new ArrayList<Item>();
	}

	public Room() {
		// default constructor.
		roomName = "DEFAULT ROOM";
		description = "DEFAULT DESCRIPTION";
		exits = new HashMap<String, Room>();
		items = new ArrayList<Item>();
	}

	public void setLocked(boolean b) {
		locked = b;
	}
	public void setBoarded(boolean b) {
		boarded = b;
	}
	
	public boolean getLocked() {
		return locked;
	}
	public boolean getBoarded() {
		return boarded;
	}
	
	public void setExit(char direction, Room r) throws Exception {
		String dir = "";
		switch (direction) {
		case 'E':
			dir = "east";
			break;
		case 'W':
			dir = "west";
			break;
		case 'S':
			dir = "south";
			break;
		case 'N':
			dir = "north";
			break;
		case 'U':
			dir = "up";
			break;
		case 'D':
			dir = "down";
			break;
		default:
			throw new Exception("Invalid Direction");

		}

		exits.put(dir, r);
	}
	
	/*
	 * Assigns a Riddle object to the Room
	 */
	public void addRiddler(Riddler riddler) {
		this.riddler = riddler; 
	}
	
	/*
	 * Add passed in item to item array list
	 */
	public void addItem(Item item) {
		items.add(item);
	}
	
	/*
	 * Remove first instance of item passed in
	 * Return item if removed, otherwise return null
	 */
	public Item removeItem(Item item) {
		for(int i=0; i<items.size(); i++) {
			if(item.equals(items.get(i))) {
				items.remove(i);
				return item;
			}
		}
		return null;
	}
	
	/*
	 * Remove item at index
	 * Return item if removed, otherwise return null
	 */
	public Item removeItem(int i) {
		if(i >= items.size())
			return null;
		Item temp = items.get(i);
		items.remove(i);
		return temp;
	}
	
	public ArrayList<Item> getItems() {
		return items;
	}
	
	public Item getItem(int i) {
		return items.get(i);
	}

	/**
	 * Define the exits of this room. Every direction either leads to another
	 * room or is null (no exit there).
	 */
	public void setExits(Room north, Room east, Room south, Room west, Room up, Room down) {
		if (north != null)
			exits.put("north", north);
		if (east != null)
			exits.put("east", east);
		if (south != null)
			exits.put("south", south);
		if (west != null)
			exits.put("west", west);
		if (up != null)
			exits.put("up", up);
		if (down != null)
			exits.put("down", down);

	}

	/**
	 * Return the description of the room (the one that was defined in the
	 * constructor).
	 */
	public String shortDescription() {
		return "Room: " + roomName + "\n\n" + description;
	}

	/**
	 * Return a long description of this room, on the form: You are in the
	 * kitchen. Exits: north west
	 */
	public String longDescription() {

		return "Room: " + roomName + "\n\n" + description + "\n";
	}

	/**
	 * Return a string describing the room's exits, for example "Exits: north
	 * west ".
	 */
	public String exitString() {
		String returnString = "Exits:";
		String curr = ""; 
		Set keys = exits.keySet();
		 
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			curr = (String) iter.next(); 
			returnString += " " + curr.substring(0,1).toUpperCase() + curr.substring(1); 
		}
		return returnString;
	}
	
	public String itemString(){
		boolean hasItems = false; 
		String items = "";
		String itemString = "Items in room: "; 
		if(this.getItems().size() > 0) {
			hasItems = true; 
			for(int i = 0; i < this.getItems().size() - 1; i++) {
				hasItems = true;
				items += this.getItems().get(i).getName() + ", ";
			}
			items += this.getItems().get(this.getItems().size() - 1).getName();
		}
		if(hasItems) {
			return itemString + items; 
		}else {
			return itemString + "none"; 
		} 
	}

	/**
	 * Return the room that is reached if we go from this room in direction
	 * "direction". If there is no room in that direction, return null.
	 */
	public Room nextRoom(String direction) {
		return (Room) exits.get(direction);
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public boolean hasItem(Item item) {
		boolean hasItem = false;
		for(Item i : items) {
			if(i.equals(item))
				hasItem = true;
		}
		return hasItem;
	}
	public boolean hasRiddler() {
		return !(this.riddler == null);
	}

	public Riddler getRiddler() {
		return riddler; 
	}

	public void removeRiddler() {
		riddler = null; 
		
	}

	public boolean containsNotebook() {
		for(Item i : this.getItems()) {
			if(i.equals(new Notebook()))
				return true; 
		}
		return false; 
	}
	
}
