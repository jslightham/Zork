package com.bayviewglen.zork;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Scanner;

import com.bayviewglen.zork.Entities.Player;
import com.bayviewglen.zork.Entities.Enemies.Enemy;
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
	private HashMap<Enemy, String> masterEnemyMap;
	private Combat currentCombat = null;
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
				// Read the locked state
				boolean locked = Boolean.parseBoolean(roomScanner.nextLine().split(":")[1].replaceAll("<br>", "\n").trim());
				room.setLocked(locked);
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
	
	private void initEnemies(String fileName) throws Exception {
		masterEnemyMap = new HashMap<Enemy, String>();
		Scanner enemyScanner = null;
		Enemy e = null;
		try {
			enemyScanner = new Scanner(new File(fileName));
			
			while (enemyScanner.hasNext()) {
				 e = new Enemy();
				// Read the Name
				String enemyName = enemyScanner.nextLine();
				e.setName(enemyName.split(":")[1].trim());
				// Read the Description
				String enemyDescription = enemyScanner.nextLine();
				e.setDescription(enemyDescription.split(":")[1].replaceAll("<br>", "\n").trim());
				// Read the Room
				String startingRoom = enemyScanner.nextLine();
				e.setRoom(startingRoom.split(":")[1].trim());
				// Read the Damage Given
				int damageGiven = Integer.parseInt(enemyScanner.nextLine().split(":")[1].trim());
				e.setDamageGiven(damageGiven);
				}
			}catch(Exception ex) {
			}
			masterEnemyMap.put(e, e.getRoom());
			enemyScanner.close();
		} 

	/**
	 * Create the game and initialise its internal map.
	 */
	public Game() {
		try {
			initRooms("data/rooms.dat");
			initEnemies("data/enemies.dat");
			currentRoom = masterRoomMap.get("CIRCLE_ROOM");
		} catch (Exception e) {
			e.printStackTrace();
		}
		parser = new Parser();
		player = new Player();
	}
	
	/**
	 * Print out the opening message for the player.
	 */
	private boolean printWelcome() {
		Scanner in = new Scanner(System.in); 
		boolean isNotValid = true; 
		System.out.println("Welcome to ESCAPE CASA LOMA!\n-----");
		System.out.println("A new, fresh take on the escape-room,\nby Johnathon, Luca, Victoria and Evan ");
		System.out.println("Type \"play\" to play the game. If you wish to close the game at any time, type \"quit\".");
		while(isNotValid) {
			System.out.print("> ");
			String i = in.nextLine();
			if(i.toLowerCase().equals("play")) {
				return true; 
			}else if(i.toLowerCase().equals("quit")) { 
				return false; 
			}
			System.out.println("That is not a valid response. Type \"play\" to play the game. If you wish to close the game, type \"quit\".");
		}
		return false; 
		

	}

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() {
			if(printWelcome()) {
		// Enter the main command loop. Here we repeatedly read commands and
		// execute them until the game is over.
		System.out.println("\nType 'help' if you need help, consult the wiki \non GitHub if you are confused and enjoy the game!\n");
			System.out.println("\n\nEscape Casa Loma: A text-based adventure game");
			System.out.println("---------------------\n");
			System.out.print(currentRoom.longDescription()); 
			System.out.println(currentRoom.exitString());
			boolean finished = false;
			while (!finished) {
				Command command = parser.getCommand();
				finished = processCommand(command);
			}
		}
		System.out.println("Thank you for playing. Goodbye!");
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
		case "open": case "unlock":
			boolean hasLockPick = false;
			for(int i =0; i<player.getInventory().size(); i++) {
				if(player.getInventory().get(i).equals(new Lockpick())) {
					hasLockPick = true;
					break;
				}
			}
			
			if(command.hasDirection() && hasLockPick) {
				Room nextRoom = currentRoom.nextRoom(command.getDirection());
				if(nextRoom.getLocked()) {
					nextRoom.setLocked(false);
					player.removeFromInventory(new Lockpick());
					System.out.println("With great effort, you unlocked the door!");
				}else{
					System.out.println("That door is already unlocked!");
				}
			}else if(!command.hasDirection()){
				System.out.println("You must specify a direction!");
			}else {
				System.out.println("What do you want to open the door with?");
			}
			break;	
		case "go": case "n": case "s": case "e": case "w": case "north": case "south": case "west": case "east": case "up": case "down": case "d": case "u":
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
			case "die":
				System.out.println("If you insist... \n Poof! You're gone. You're out of the castle now, but now a new, grand new adventure begins...");
				return true; 
			case "eat":
				//System.out.println("Do you really think you should be eating at a time like this?");
				if(command.hasItem()) {
					Class<?> clazz;
					Item object;
					try {
						clazz = Class.forName("com.bayviewglen.zork.Items." + command.getItem().substring(0, 1).toUpperCase().trim() + command.getItem().substring(1).trim());
						Constructor<?> ctor = clazz.getConstructor();
						object = (Item) ctor.newInstance();
						boolean hasItem = false;
						for(int i=0; i<player.getInventory().size(); i++) {
							if(object.equals(player.getInventory().get(i))) {
								hasItem = true;
							}
						}
						if(object.isConsumable() && hasItem) {
							System.out.println("Yum!");
							System.out.println("Your health is now " + player.getHealth() + "%");
							player.eat();
							player.removeFromInventory(object);
						}else if(object.isConsumable()) {
							System.out.println("You do not have a " + command.getItem());
						}else {
							System.out.println("You cannot eat a " + command.getItem());
						}
					} catch(Exception e) {
						System.out.println("You cannot eat a " + command.getItem());
					}
				}else {
					System.out.println("Eat what?");
				}
				break;
			case "take":
				if(command.hasItem()) {
					Class<?> clazz;
					Item object;
					try {
						clazz = Class.forName("com.bayviewglen.zork.Items." + command.getItem().substring(0, 1).toUpperCase().trim() + command.getItem().substring(1).trim());
						Constructor<?> ctor = clazz.getConstructor();
						object = (Item) ctor.newInstance();
						if(!currentRoom.hasItem(object)) {
							System.out.println("This room has no " + command.getItem() + "!");
						}
						else if(player.addToInventory(object)) {
							currentRoom.removeItem(object);
							System.out.println("Taken");
						}else {
							System.out.println("You cannot carry any more!");
						}
					} catch(Exception e) {
						
					}
				}else {
					System.out.println("Take what?");
				}
					
				break;
					
			case "look":
				boolean hasItems = false;
				String items = "";
				for(Item i : currentRoom.getItems()) {
					hasItems = true;
					items += i.getName() + "   ";
				}
				if(hasItems) {
					System.out.println(currentRoom.longDescription());
					System.out.print("Items: ");
					System.out.print(items);
					System.out.println();
				}else {
					System.out.println(currentRoom.longDescription());
					System.out.println("There are no items.");
				}
				break;
				
			case "inventory": case "i":
				boolean hasPlayerItems = false;
				String itemsP = "";
				for(Item i : player.getInventory()) {
					hasPlayerItems = true;
					itemsP += i.getName() + "   ";
				}
				if(hasPlayerItems) {
					System.out.print("Inventory: ");
					System.out.print(itemsP);
					System.out.println();
				}else {
					System.out.println("You have nothing on you. Try and find some items.");
				}
				break;
			case "drop":
				if(command.hasItem()) {
					Class<?> clazz;
					Item object;
					try {
						clazz = Class.forName("com.bayviewglen.zork.Items." + command.getItem().substring(0, 1).toUpperCase().trim() + command.getItem().substring(1).trim());
						Constructor<?> ctor = clazz.getConstructor();
						object = (Item) ctor.newInstance();
						boolean has = false;
						for(int i =0; i<player.getInventory().size(); i++) {
							if(player.getInventory().get(i).equals(object)) {
								has = true;
							}
						}
						if(has) {
							player.removeFromInventory(object);
							currentRoom.addItem(object);
							System.out.println("You dropped your " + object.getName());
						}else {
							System.out.println("You do not have a " + object.getName());
						}
					} catch(Exception e) {
						
					}
				}else {
					System.out.println("Drop what?");
				}
				break;
			case "attack":
				if(currentCombat == null) {
					if(command.hasEnemy()) {
						Class<?> clazz;
						Enemy object;
						try {
							clazz = Class.forName("com.bayviewglen.zork.Enemies." + command.getEnemy().substring(0, 1).toUpperCase().trim() + command.getEnemy().substring(1).trim());
							Constructor<?> ctor = clazz.getConstructor();
							object = (Enemy) ctor.newInstance();
						currentCombat = new Combat(player, object);
					}catch(Exception e) {
						
					}
				}
					
				}
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
		System.out.println("Here's what you can do:");
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
		else if(nextRoom.getLocked()) {
			System.out.println("The door is locked. You need to find a key to open it.");
		}
		else {
			currentRoom = nextRoom;
			System.out.print(currentRoom.longDescription());
			boolean hasEnemy = false;
			Enemy enemy = null;
			String room = "";
			for (String i : masterEnemyMap.values()) {
				if(currentRoom.getRoomName().equals(i)) {
					hasEnemy = true;
					room = i;
				}
			}
			for (Enemy i : masterEnemyMap.keySet()) {
				  if(masterEnemyMap.get(i).equals(room)) {
					  enemy = i;
				  }
			}
			if(hasEnemy) {
				System.out.println(enemy.getName() + ", " + enemy.getDescription() + " has appeared!");
				System.out.println(currentRoom.exitString());
			}else {
				System.out.println(currentRoom.exitString());
			}
		}
	}
	
}