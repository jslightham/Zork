package com.bayviewglen.zork;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Scanner;

import com.bayviewglen.zork.Entities.Player;
import com.bayviewglen.zork.Items.*;

/**
 * Class Game - the main class of the "Zork" game.
 *
 * Author: Michael Kolling Version: 1.1 Date: March 2000
 * 
 * This class is the main class of the "Zork" application. Zork is a very
 * simple, text based adventure game. Users can walk around some scenery. That's
 * all. It should really be extended to make it more interesting!
 * 
 * To play this game, create an instance of this class and call the "play"
 * routine.
 * 
 * This main class creates and initialises all the others: it creates all rooms,
 * creates the parser and starts the game. It also evaluates the commands that
 * the parser returns.
 */
class Game {
	private Parser parser;
	private Player player;
	private Room currentRoom;
	// This is a MASTER object that contains all of the rooms and is easily
	// accessible.
	// The key will be the name of the room -> no spaces (Use all caps and
	// underscore -> Great Room would have a key of GREAT_ROOM
	// In a hashmap keys are case sensitive.
	// masterRoomMap.get("GREAT_ROOM") will return the Room Object that is the
	// Great Room (assuming you have one).
	private HashMap<String, Room> masterRoomMap;
	//private HashMap<Item, String> itemsInRooms = new HashMap<Item, String>();

	private void initRooms(String fileName) throws Exception {
		//itemsInRooms.put(new Candlestick(), "Candlestick");
		masterRoomMap = new HashMap<String, Room>();
		Scanner roomScanner;
		try {
			HashMap<String, HashMap<String, String>> exits = new HashMap<String, HashMap<String, String>>();
			roomScanner = new Scanner(new File(fileName));
			while (roomScanner.hasNext()) {
				Room room = new Room();
				// Read the Name
				String roomName = roomScanner.nextLine();
				room.setRoomName(roomName.split(":")[1].trim());
				// Read the Description
				String roomDescription = roomScanner.nextLine();
				room.setDescription(roomDescription.split(":")[1].replaceAll("<br>", "\n").trim());
				// Read the Items
				String items = roomScanner.nextLine();
				try {
				String[] itemArr = items.split(":")[1].split(",");
				for(String s : itemArr) {
					Class<?> clazz = Class.forName("com.bayviewglen.zork.Items." + s.trim());
					Constructor<?> ctor = clazz.getConstructor();
					Item object = (Item) ctor.newInstance();
					room.addItem(object);
				}
				}catch(Exception e) {
				}
				
				// Read the Exits
				String roomExits = roomScanner.nextLine();
				// An array of strings in the format E-RoomName
				String[] rooms = roomExits.split(":")[1].split(",");
				HashMap<String, String> temp = new HashMap<String, String>();
				for (String s : rooms) {
					temp.put(s.split("-")[0].trim(), s.split("-")[1]);
				}

				exits.put(roomName.substring(10).trim().toUpperCase().replaceAll(" ", "_"), temp);

				// This puts the room we created (Without the exits in the
				// masterMap)
				masterRoomMap.put(roomName.toUpperCase().substring(10).trim().replaceAll(" ", "_"), room);

				// Now we better set the exits.
			}

			for (String key : masterRoomMap.keySet()) {
				Room roomTemp = masterRoomMap.get(key);
				HashMap<String, String> tempExits = exits.get(key);
				for (String s : tempExits.keySet()) {
					// s = direction
					// value is the room.

					String roomName2 = tempExits.get(s.trim());
					Room exitRoom = masterRoomMap.get(roomName2.toUpperCase().replaceAll(" ", "_"));
					roomTemp.setExit(s.trim().charAt(0), exitRoom);

				}

			}

			roomScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the game and initialise its internal map.
	 */
	public Game() {
		try {
			initRooms("data/rooms.dat");
			currentRoom = masterRoomMap.get("CIRCLE_ROOM");
		} catch (Exception e) {
			e.printStackTrace();
		}
		parser = new Parser();
		player = new Player();
	}

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() {
		printWelcome();
		// Enter the main command loop. Here we repeatedly read commands and
		// execute them until the game is over.

		boolean finished = false;
		while (!finished) {
			Command command = parser.getCommand();
			finished = processCommand(command);
		}
		System.out.println("Thank you for playing.  Good bye.");
	}

	/**
	 * Print out the opening message for the player.
	 */
	private void printWelcome() {
		System.out.println("Welcome to ESCAPE CASA LOMA!\n-----");
		System.out.println("A new, fresh take on the escape-room franchise,\nby Johnathon, Luca, Victoria and Evan ");
		System.out.println("Type 'help' if you need help, and enjoy the game!");
		System.out.println("\n---------------------\n");
		System.out.println(currentRoom.longDescription());
	}

	/**
	 * Given a command, process (that is: execute) the command. If this command
	 * ends the game, true is returned, otherwise false is returned.
	 */
	private boolean processCommand(Command command) {
		
		if (command.isUnknown()) {
			System.out.println("I don't know what you mean...");
			return false;
		}
		String commandWord = command.getCommandWord();
		switch(commandWord) {
			case "go": case "n": case "s": case "e": case "w": case "north": case "south": case "west": case "east": case "up": case "down":
				goRoom(command);
				break;
			case "help":
				printHelp();
				break;
			case "jump":
				System.out.println("Good Job!");
				break;
			case "quit":
				return true;
			case "eat":
				System.out.println("Do you really think you should be eating at a time like this?");
				break;
			case "take":
				if(command.hasItem()) {
					if(player.addToInventory(command.getItem())) {
						System.out.println("Taken");
					}else {
						System.out.println("You cannot carry any more!");
					}
					
				}else {
					System.out.println("Take what?");
				}
					
				break;
					
			case "look":
				System.out.print("Items: ");
				for(Item i : currentRoom.getItems()) {
					System.out.print(i.getName() + "   ");
				}
				System.out.println();
				/*
				for (Item i : itemsInRooms.keySet()) {
					System.out.print(i.getName() + "  ");
				}
				System.out.println();
				*/
				break;
			default:
				return false;
		}
		return false;
	}

	// implementations of user commands:
	/**
	 * Print out some help information. Here we print some stupid, cryptic
	 * message and a list of the command words.
	 */
	private void printHelp() {
		System.out.println("You are lost. You are alone. You wander");
		System.out.println("around at Monash Uni, Peninsula Campus.");
		System.out.println();
		System.out.println("Your command words are:");
		parser.showCommands();
	}

	/**
	 * Try to go to one direction. If there is an exit, enter the new room,
	 * otherwise print an error message.
	 */
	private void goRoom(Command command) {
		if (!command.hasDirection()) {
			// if there is no second word, we don't know where to go...
			System.out.println("Go where?");
			return;
		}
		String direction = command.getDirection();
		// Try to leave current room.
		Room nextRoom = currentRoom.nextRoom(direction);
		if (nextRoom == null)
			System.out.println("There is no door!");
		else {
			currentRoom = nextRoom;
			System.out.println(currentRoom.longDescription());
		}
	}

}