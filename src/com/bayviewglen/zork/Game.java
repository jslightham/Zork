package com.bayviewglen.zork;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Scanner;

import com.bayviewglen.zork.Entities.Player;
import com.bayviewglen.zork.Entities.Riddle;
import com.bayviewglen.zork.Entities.Riddler;
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
				// Read the boarded state
				boolean boarded = Boolean.parseBoolean(roomScanner.nextLine().split(":")[1].replaceAll("<br>", "\n").trim());
				room.setBoarded(boarded);
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
				//Initialize the riddle in the room, if it exists
				String riddle = roomScanner.nextLine().split(":", 2)[1].trim();
				try {
					int comma1 = riddle.indexOf(","); 
					int comma2 = riddle.indexOf(",", riddle.indexOf(",") + 1); 
					String message = riddle.substring(1, comma1 - 1).replaceAll("<comma>", ",");
					String question = riddle.substring(comma1 + 3, comma2 - 1).replaceAll("<comma>", ",");
					String answer = riddle.substring(comma2 + 3, riddle.length() - 1).replaceAll("<comma>", ",");
					Riddle riddleObj = new Riddle(question, answer);
					Riddler butler = new Riddler(100, 100, riddleObj, message); 
					room.addRiddler(butler);
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

				// This puts the room we created (Without the exits in the masterMap)
				masterRoomMap.put(roomName.toUpperCase().substring(10).trim().replaceAll(" ", "_"), room);
				
				if(roomScanner.hasNextLine()) {
					roomScanner.nextLine(); 
				}
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
			if(i.toLowerCase().equals("play") || i.toLowerCase().equals("p")) {
				return true;
			}else if(i.toLowerCase().equals("quit") || i.toLowerCase().equals("q")) { 
				in.close(); 
				return false; 
			}
			System.out.println("That is not a valid response. Type \"play\" to play the game. If you wish to close the game, type \"quit\".");
		}
		in.close(); 
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
			System.out.println("\n\nEscape Casa Loma");
			System.out.println("---------------------\n");
			System.out.print(currentRoom.longDescription()); 
			System.out.println(currentRoom.exitString());
			System.out.println(currentRoom.itemString());
			boolean finished = false;
			while (!finished) {
				if(currentCombat != null) {
					if(currentCombat.getEnemy().getHealth() <= 0.0) {
						System.out.println("You destroyed " + currentCombat.getEnemy().getName());
						masterEnemyMap.values().remove(currentRoom.getRoomName());
						currentCombat = null;
					}
					else if(currentCombat.getTurn() == 1) {
						currentCombat.enemyAttack();
						if(currentCombat.getPlayer().getHealth() <=0.0) {
							System.out.println("You were destroyed by " + currentCombat.getEnemy().getName());
							for(int i =0; i<player.getInventory().size(); i++) {
								currentRoom.addItem(player.getInventory().get(i));
								player.removeFromInventory(player.getInventory().get(i));
							}
							currentRoom = masterRoomMap.get("CIRCLE_ROOM");
							System.out.println("Poof! You looked pretty banged up there, so I brought you back to the circle room. Your items are where you died.");
							player.setHealth(100.0);
							currentCombat.getEnemy().setHealth(100.0);
							currentCombat = null;
						}
					}
				}
				
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
				try {
				if(nextRoom.getLocked()) {
					nextRoom.setLocked(false);
					player.removeFromInventory(new Lockpick());
					System.out.println("After a little bit of picking, a click is heard and the door opens slightly!");
				}else{
					System.out.println("That door is already unlocked!");
				}
				}catch(Exception e) {
					System.out.println("There is no door there!");
				}
			}else if(!command.hasDirection()){
				System.out.println("In what direction do you want to go in?");
			}else {
				System.out.println("What do you want to open the door with?");
			}
			boolean hasCrowbar = false;
			for(int i =0; i<player.getInventory().size(); i++) {
				if(player.getInventory().get(i).equals(new Crowbar())) {
					hasCrowbar = true;
					break;
				}
			}
			if(command.hasDirection() && hasCrowbar) {
				Room nextRoom = currentRoom.nextRoom(command.getDirection());
				try {
				if(nextRoom.getBoarded()) {
					nextRoom.setBoarded(false);
					player.removeFromInventory(new Crowbar());
					System.out.println("With great effort, you pry the boards off the door with the crowbar! However, it breaks and is no longer useable.");
				}else{
					System.out.println("That door is already unlocked!");
				}
				}catch(Exception e) {
					System.out.println("There is no door there!");
				}
			}else if(!command.hasDirection()){
				
			}else {
				
			}
			break;	
		case "go": case "n": case "s": case "e": case "w": case "north": case "south": case "west": case "east": case "up": case "down": case "d": case "u":
			if(currentCombat == null)
				goRoom(command);
			else
				System.out.println("You can't leave the room during combat!");
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
				System.out.println("If you insist... \nPoof! You're gone. You're out of the castle now, but now a new, grand new adventure begins...");
				return true; 
			case "eat":
				
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
							System.out.println("Nom Nom Nom...");
							player.eat();
							System.out.println("Your health is now at " + player.getHealth() + "%");
							player.removeFromInventory(object);
							if(currentCombat != null)
								currentCombat.setEnemyTurn();
						}else if(object.isConsumable()) {
							System.out.println("You don't have a " + command.getItem());
						}else {
							System.out.println("Sorry, you can't eat a " + command.getItem());
						}
					} catch(Exception e) {
						System.out.println("Sorry, you can't eat a " + command.getItem());
					}
				}else {
					System.out.println("Eat what?");
				}
				break;
			case "talk":
				if(currentRoom.hasRiddler()) {
					Scanner rScanner = new Scanner(System.in); 
					String message = currentRoom.getRiddler().getMessage(); 
					String riddle = currentRoom.getRiddler().getRiddle().getQuestion(); 
					String answer = currentRoom.getRiddler().getRiddle().getAnswer(); 
					System.out.println(message + "\n" + riddle + "\nWhat is your answer?:");
					String guess = rScanner.nextLine(); 
					if(guess.toLowerCase().equals(answer.toLowerCase())) {
						System.out.println("Good Job!");
					}else {
						System.out.println("Sorry, that isn't the answer. Think about it, then try again.");
					}
				}else {
					System.out.println("Talk to who?");
				}
				break; 
			case "take":
				boolean hasAll = false;
				for(String a : command.getOtherWords()) {
					if(a.equals("all"))
						hasAll = true;
				}
				if(hasAll){
					for(int i =0; i<currentRoom.getItems().size(); i++) {
						if(player.addToInventory(currentRoom.getItem(i))) {
							currentRoom.removeItem(i);
							i--;
						}else {
							System.out.println("You can't carry any more stuff!");
							break;
						}
						System.out.println("Taken");
					}
				}
				else if(command.hasItem()) {
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
							System.out.println("You can't carry any more stuff!");
						}
					} catch(Exception e) {
						
					}
				}else {
					System.out.println("Take what?");
				}
					
				break;
					
			case "look":
				System.out.print(currentRoom.longDescription()); 
				System.out.println(currentRoom.exitString());
				System.out.println(currentRoom.itemString());
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
						Enemy enemy = null;
						for (Enemy i : masterEnemyMap.keySet()) {
							  if(masterEnemyMap.get(i).equals(currentRoom.getRoomName())) {
								  enemy = i;
							  }
						}
						if(enemy != null) {
							if(command.hasItem()) {
								boolean has = false;
								for(Item i : player.getInventory()) {
									if(i.getName().toLowerCase().equals(command.getItem())) {
										has = true;
									}
								}
								if(has) {
									currentCombat = new Combat(player, enemy);
									currentCombat.playerAttack(command.getItem());
								}else {
									System.out.println("You do not have that weapon!");
								}
							}else {
								System.out.println("Attack with what?");
							}
							}else {
								System.out.println("That enemy is not in this room!");
							}
				}else {
					System.out.println("Attack what?");
				}
				} else {
					if(command.hasItem()) {
						boolean has = false;
						for(Item i : player.getInventory()) {
							if(i.getName().toLowerCase().equals(command.getItem())) {
								has = true;
							}
						}
						if(has) {
							currentCombat.playerAttack(command.getItem());
						}else {
							System.out.println("You do not have that weapon!");
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
		else if(nextRoom.getLocked() && nextRoom.getBoarded()) {
			System.out.println("The door is locked and boarded shut. You need to find a key and crowbar to open it.");
		}
		else if(nextRoom.getLocked()) {
			System.out.println("The door is locked. You need a key to open it.");
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
	
	public void removeEnemy(String r) {
		masterEnemyMap.values().remove(r);
	}
}