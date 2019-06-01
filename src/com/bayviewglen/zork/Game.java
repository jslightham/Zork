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
	// Stores where all enemies are currently 
	private HashMap<Enemy, String> masterEnemyMap;
	/*
	 * Stores the current combat that is taking place. 
	 * If there is a combat, this contains an instance of the combat class, if not it is null.
	 */
	private Combat currentCombat = null;

	private void initRooms(String fileName) throws Exception {
		masterRoomMap = new HashMap<String, Room>();
		Scanner roomScanner;
		try {
			HashMap<String, HashMap<String, String>> exits = new HashMap<String, HashMap<String, String>>();
			roomScanner = new Scanner(new File(fileName));
			while (roomScanner.hasNext()) {
				Room room = new Room();
				// Read the Name
				String roomName = roomScanner.nextLine();
				room.setRoomName(roomName.split(": ")[1].trim());
				// Read the Description
				String roomDescription = roomScanner.nextLine();
				room.setDescription(roomDescription.split(": ")[1].replaceAll("<br>", "\n").trim());
				//Read room description after riddler is removed.
				String newRoomDescription = roomScanner.nextLine(); 
				room.setNewRoomDescription(newRoomDescription.substring(newRoomDescription.indexOf(":") + 1,newRoomDescription.length()).replaceAll("<br>", "\n").trim());
				// Read the locked state
				boolean locked = Boolean.parseBoolean(roomScanner.nextLine().split(": ")[1].replaceAll("<br>", "\n").trim());
				room.setLocked(locked);
				// Read the boarded state
				boolean boarded = Boolean.parseBoolean(roomScanner.nextLine().split(": ")[1].replaceAll("<br>", "\n").trim());
				room.setBoarded(boarded);
				// Read the Items
				String items = roomScanner.nextLine();
				try {
					String[] itemArr = items.split(": ")[1].split(",");
					for (String s : itemArr) {
						Class<?> clazz = Class.forName("com.bayviewglen.zork.Items." + s.trim());
						Constructor<?> ctor = clazz.getConstructor();
						Item object = (Item) ctor.newInstance();
						room.addItem(object);
					}
				} catch (Exception e) {
				}
				// Initialize the riddle in the room, if it exists
				String riddlerInfo = roomScanner.nextLine().split(":", 2)[1].trim();
				try {
					int comma1 = riddlerInfo.indexOf(",");
					int comma2 = riddlerInfo.indexOf(",", riddlerInfo.indexOf(",") + 1);
					int comma3 = riddlerInfo.indexOf(",", comma2 + 1);
					String message = riddlerInfo.substring(1, comma1 - 1).replaceAll("<comma>", ",").replaceAll("<br>","\n");
					String question = riddlerInfo.substring(comma1 + 3, comma2 - 1).replaceAll("<comma>", ",").replaceAll("<br>", "\n");
					String answer = riddlerInfo.substring(comma2 + 3, comma3 - 1).replaceAll("<comma>", ",").replaceAll("<br>", "\n");
					Riddle riddleObj = new Riddle(question, answer);
					String item = riddlerInfo.substring(comma3 + 2, riddlerInfo.length());
					// Initializes prize object
					Class<?> clazz = Class.forName("com.bayviewglen.zork.Items." + item.trim());
					Constructor<?> ctor = clazz.getConstructor();
					Item prize = (Item) ctor.newInstance();
					Riddler butler = new Riddler(100, 100, riddleObj, message, prize);
					room.addRiddler(butler);
				} catch (Exception e) {
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

				if (roomScanner.hasNextLine())
					roomScanner.nextLine();

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
/*
 * Initiate enemies from the data file into the hashmap.
 */
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
				// Read the Loot
				String loot = enemyScanner.nextLine().split(":")[1].trim();
				e.setLoot(loot);
				masterEnemyMap.put(e, e.getRoom());
			}
		} catch (Exception ex) {
		}
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
		while (isNotValid) {
			System.out.print("> ");
			String i = in.nextLine();
			if (i.toLowerCase().equals("play") || i.toLowerCase().equals("p")) {
				return true;
			} else if (i.toLowerCase().equals("quit") || i.toLowerCase().equals("q")) {
				in.close();
				return false;
			}
			System.out.println(
					"That is not a valid response. Type \"play\" to play the game. If you wish to close the game, type \"quit\".");
		}
		in.close();
		return false;

	}

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() {
		if (printWelcome()) {
			// Enter the main command loop. Here we repeatedly read commands and
			// execute them until the game is over.
			System.out.println("\nType 'help' if you need help, consult the wiki \non GitHub if you are confused and enjoy the game!\n");
			System.out.println("\n\nEscape Casa Loma");
			System.out.println("---------------------\n");
			System.out.print(currentRoom.longDescription());
			System.out.println(currentRoom.itemString());
			System.out.println(currentRoom.exitString());
			player.addToInventory(new Lockpick()); 
			player.addToInventory(new Key()); 
			player.addToInventory(new Key()); 
			player.addToInventory(new Crowbar()); 
			player.addToInventory(new Batteringram());  
			
			boolean finished = false;
			while (!finished) {
				// Checks if there is a current combat, if so perform the enemy's action, and check for deaths.
				if (currentCombat != null) {
					// If enemy dies
					if (currentCombat.getEnemy().getHealth() <= 0.0) {
						System.out.print("You destroyed " + currentCombat.getEnemy().getName() + "! ");
						System.out.println(currentCombat.getEnemy().getName() + " seems to have dropped a " + currentCombat.getEnemy().getLoot());
						Class<?> clazz;
						Item object = null;
						try {
							clazz = Class.forName("com.bayviewglen.zork.Items." + currentCombat.getEnemy().getLoot().substring(0, 1).toUpperCase().trim() + currentCombat.getEnemy().getLoot().substring(1).trim());
							Constructor<?> ctor = clazz.getConstructor();
							object = (Item) ctor.newInstance();
						}catch (Exception e) {

						}
						currentRoom.addItem(object);
						masterEnemyMap.values().remove(currentRoom.getRoomName());
						currentCombat = null;
					// If enemy's turn - signified by turn = 1
					} else if (currentCombat.getTurn() == 1) {
						currentCombat.enemyAttack();
						if (currentCombat.getPlayer().getHealth() <= 0.0) {
							System.out.println("You were destroyed by " + currentCombat.getEnemy().getName());
							for (int i = 0; i < player.getInventory().size(); i++) {
								currentRoom.addItem(player.getInventory().get(i));
								player.removeFromInventory(player.getInventory().get(i));
								i--;
							}
							// On player death, reset everything
							currentRoom = masterRoomMap.get("CIRCLE_ROOM");
							System.out.println(
									"Poof! You looked pretty banged up there, so I brought you back to the circle room. Your items are where you died.");
							player.setHealth(100.0);
							player.setBleeding(false);
							try {
							currentCombat.getEnemy().setHealth(100.0);
							currentCombat = null;
							}catch (Exception e) {
								
							}
						}
					}
				}
				// Checks if the player is bleeding, if they are subtract 2 from health each turn
				if(player.getBleeding()) {
					player.setHealth(player.getHealth()-2);
					System.out.println("You are bleeding. Find, and use bandages to stop bleeding.");
					System.out.println("Your health is now " + player.getHealth() + "%");
				}
				// Checks if the player dies, if so reset all values, and drop items
				if(player.getHealth() <= 0) {
					for (int i = 0; i < player.getInventory().size(); i++) {
						currentRoom.addItem(player.getInventory().get(i));
						player.removeFromInventory(player.getInventory().get(i));
						i--;
					}
					currentRoom = masterRoomMap.get("CIRCLE_ROOM");
					System.out.println(
							"Poof! You looked pretty banged up there, so I brought you back to the circle room. Your items are where you died.");
					player.setHealth(100.0);
					player.setBleeding(false);
				}
				Command command = parser.getCommand();
				finished = processCommand(command);
			}
		}
		// Printed when game ends
		System.out.println("Thank you for playing. Goodbye!");
	}

	/**
	 * Given a command, process (that is: execute) the command. If this command ends
	 * the game, true is returned, otherwise false is returned.
	 */
	private boolean processCommand(Command command) {

		if (command.isUnknown()) {
			System.out.println("I don't know what you mean...");
			return false;
		}
		String commandWord = command.getCommandWord();
		// Switch that handles all commands 
		switch (commandWord) {
		case "open":
		case "unlock":
			// Command to open or unlock a door
			boolean hasLockPick = false;
			boolean hasKey = false;
			// Loop to check if player's inventory contains key or lockpick
			for (int i = 0; i < player.getInventory().size(); i++) {
				if (player.getInventory().get(i).equals(new Lockpick())) {
					hasLockPick = true;
					break;
				}
				if (player.getInventory().get(i).equals(new Key())) {
					hasKey = true;
					break;
				}
			}
			// Check if the room can be unlocked, if so unlock it 
			if (command.hasDirection() && (hasLockPick || hasKey)) {
				Room nextRoom = currentRoom.nextRoom(command.getDirection());
				try {
					if(nextRoom.getLocked()) {
					nextRoom.setLocked(false);
					if(hasLockPick) {
						player.removeFromInventory(new Lockpick());
						System.out.println("After a little bit of picking, a click is heard and the door opens slightly!");
					}
					if(hasKey) {
						player.removeFromInventory(new Key());
						System.out.println("With great force, you turn the key in the keyhole and the door unlocks! However, the key breaks in the keyhole and is now unusable.");
					}
					if(!nextRoom.getBoarded())
						break;
				}else{
					System.out.println("That door is already unlocked!");
					if(!nextRoom.getBoarded())
						break;
				}
				}catch(Exception e) {
					System.out.println("There is no door there!");
				}
			} else if (!command.hasDirection()) {
				System.out.println("In what direction do you want to go in?");
			} else {
				System.out.println("What do you want to open the door with?");
			}
			
			// Similar with the above key and lockpick process, but with crowbars and battering rams for boarded doors.
			boolean hasCrowbar = false;
			boolean hasBatteringRam = false;
			for (int i = 0; i < player.getInventory().size(); i++) {
				if (player.getInventory().get(i).equals(new Crowbar())) {
					hasCrowbar = true;
					break;
				}
				if (player.getInventory().get(i).equals(new Batteringram())) {
					hasBatteringRam = true;
					break;
				}
			}
			if (command.hasDirection() && (hasCrowbar || hasBatteringRam)) {
				Room nextRoom = currentRoom.nextRoom(command.getDirection());
				try {
					if (nextRoom.getBoarded()) {
						nextRoom.setBoarded(false);
						if(hasCrowbar) {
						player.removeFromInventory(new Crowbar());
						System.out.println("With great effort, you pry the boards off the door with the crowbar! However, it breaks and is no longer useable.");
						}
						if(hasBatteringRam) {
							System.out.println("With the battering ram, you smash through the boards on the door!");
						}
					} else {
						System.out.println("That door is already unboarded!");
					}
				} catch (Exception e) {
					System.out.println("There is no door there!");
				}
			} else if (!command.hasDirection()) {

			} else {
					
			}
			break;
		case "go":
		case "n":
		case "s":
		case "e":
		case "w":
		case "north":
		case "south":
		case "west":
		case "east":
		case "up":
		case "down":
		case "d":
		case "u":
			// if player is not in combat, go in direction given
			if (currentCombat == null)
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
			// convert the item string in the command (if existent) into an item to get info about the item, and to remove from inventory
			if (command.hasItem()) {
				Class<?> clazz;
				Item object;
				try {
					clazz = Class.forName(
							"com.bayviewglen.zork.Items." + command.getItem().substring(0, 1).toUpperCase().trim()
									+ command.getItem().substring(1).trim());
					Constructor<?> ctor = clazz.getConstructor();
					object = (Item) ctor.newInstance();
					boolean hasItem = false;
					// check if player has item
					for (int i = 0; i < player.getInventory().size(); i++) {
						if (object.equals(player.getInventory().get(i))) {
							hasItem = true;
						}
					}
					// If item can be eaten, and player has, eat item.
					if (object.isConsumable() && hasItem) {
						System.out.println("Nom Nom Nom...");
						player.eat();
						System.out.println("Your health is now at " + player.getHealth() + "%");
						player.removeFromInventory(object);
						if (currentCombat != null)
							currentCombat.setEnemyTurn();
					} else if (object.isConsumable()) {
						System.out.println("You don't have a " + command.getItem());
					} else {
						System.out.println("Sorry, you can't eat a " + command.getItem());
					}
				} catch (Exception e) {
					System.out.println("Sorry, you can't eat a " + command.getItem());
				}
			} else {
				System.out.println("Eat what?");
			}
			break;
		case "talk":
			// Talking with riddler command
			if (currentCombat == null) {
				if (currentRoom.hasRiddler()) {
					Scanner rScanner = new Scanner(System.in);
					String message = currentRoom.getRiddler().getMessage();
					String riddle = currentRoom.getRiddler().getRiddle().getQuestion();
					String answer = currentRoom.getRiddler().getRiddle().getAnswer();
					System.out.println(message + "\n\nHere's my riddle: " + riddle);
					System.out.print("Enter your guess here: ");
					// wait for riddle response
					String guess = rScanner.nextLine();
					// check if guess has any part of answer in it. And if so administer prize. 
					if (guess.toLowerCase().indexOf(answer.toLowerCase()) >= 0) {
						Item prize = currentRoom.getRiddler().getPrize();
						String prizeName = prize.getName();
						System.out.println("Congratulations! You solved my riddle! As your reward, you get a " + prizeName + "!");
						if (player.addToInventory(prize)) {
							System.out.println("A " + prizeName + " has been added to your inventory.");
							System.out.println("I've got to go find Mr. Pellatt now. Good luck with your escape!");
							currentRoom.removeRiddler();
						} else {
							System.out.println("Sorry, you can't carry any more, but a " + prize.getName() + " has been added to your room.");
							currentRoom.addItem(prize);
							System.out.println("I've got to go find Mr. Pellatt now. Good luck with your escape!");
							currentRoom.removeRiddler();
						}
					} else {
						System.out.println("Sorry, that isn't the answer. Think about it, then try again.");
					}
				} else {
					System.out.println("Talk to who?");
				}
			}else {
				System.out.println("You can't talk to someone while in battle!");
			}
			break;
		case "exercise":
			System.out.println("You break out your High-Knees and Burpees from that one free-trial HIIT class you tried at your\ncommunity centre and feel quite refreshed afterward.");
			break; 
		case "play": 
			System.out.println("Probably not the best time to play right now...");
			break;
		case "give":
			System.out.println("There aren't any cookies in Casa Loma. I guess Rattenbury's mentor group\nwon't be getting their Mentor Kindness gift...");
			break; 
		case "scream":
			System.out.println("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaahhhhhhhhhhhhhhhhhhhhh!");
			break;
		case "read": 
			if(currentRoom.containsNotebook()) {
				for(Item i : currentRoom.getItems()) {
					if(i.equals(new Notebook())) {
						System.out.println("In elegant cursive, the page reads:\n" + ((Notebook)i).getHint());  
					}
				}
					
			}else if(player.getInventory().contains(new Notebook())) {
				for(Item i : player.getInventory()) {
					if(i.equals(new Notebook())) {
						System.out.println("In elegant cursive, the page reads:\n" + ((Notebook)i).getHint());  
					}
				}
			}else {
				System.out.println("Read what?");
			}
			break; 
		case "take":
			// Take the given item, or take all items
			boolean hasAll = false;
			// check if player used word all, if so take all. 
			for (String a : command.getOtherWords()) {
				if (a.equals("all"))
					hasAll = true;
			}
			if (hasAll) {
				// Iterate through all items in room, remove from room inventory and add to player inventory.
				for (int i = 0; i < currentRoom.getItems().size(); i++) {
					if (player.addToInventory(currentRoom.getItem(i))) {
						currentRoom.removeItem(i);
						i--;
					} else {
						System.out.println("You can't carry any more stuff!");
						break;
					}
					System.out.println("Taken");
				}
			} else if (command.hasItem()) {
				Class<?> clazz;
				Item object;
				try {
					clazz = Class.forName(
							"com.bayviewglen.zork.Items." + command.getItem().substring(0, 1).toUpperCase().trim()
									+ command.getItem().substring(1).trim());
					Constructor<?> ctor = clazz.getConstructor();
					object = (Item) ctor.newInstance();
					if (!currentRoom.hasItem(object)) {
						System.out.println("This room has no " + command.getItem() + "!");
					} else if (player.addToInventory(object)) {
						currentRoom.removeItem(object);
						System.out.println("Taken");
					} else {
						System.out.println("You can't carry any more stuff!");
					}
				} catch (Exception e) {

				}
			} else {
				System.out.println("Take what?");
			}

			break;

		case "look":
			// Print out descriptions of rooms
			System.out.print(currentRoom.longDescription());
			System.out.println(currentRoom.itemString());
			System.out.println(currentRoom.exitString());
			break;

		case "inventory":
		case "i":
			// Check if player has items, if so display items, if not display message
			boolean hasPlayerItems = false;
			String itemsP = "";
			// Iterate through to see if player has items, and add items to string to be printed
			for (Item i : player.getInventory()) {
				hasPlayerItems = true;
				itemsP += i.getName() + "   ";
			}
			if (hasPlayerItems) {
				System.out.print("Inventory: ");
				System.out.print(itemsP);
				System.out.println();
			} else {
				System.out.println("You have nothing on you. Try and find some items.");
			}
			break;
		case "drop":
			// if an item is given, convert string into item so that it can be removed from inventory
			if (command.hasItem()) {
				Class<?> clazz;
				Item object;
				try {
					clazz = Class.forName(
							"com.bayviewglen.zork.Items." + command.getItem().substring(0, 1).toUpperCase().trim()
									+ command.getItem().substring(1).trim());
					Constructor<?> ctor = clazz.getConstructor();
					object = (Item) ctor.newInstance();
					boolean has = false;
					for (int i = 0; i < player.getInventory().size(); i++) {
						if (player.getInventory().get(i).equals(object)) {
							has = true;
						}
					}
					if (has) {
						player.removeFromInventory(object);
						currentRoom.addItem(object);
						System.out.println("You dropped your " + object.getName());
					} else {
						System.out.println("You do not have a " + object.getName());
					}
				} catch (Exception e) {

				}
			} else {
				System.out.println("Drop what?");
			}
			break;
		case "attack":
			// If there is currently no combat
			if (currentCombat == null) {
				if (command.hasEnemy()) {
					Enemy enemy = null;
					// Using hashmap backwards
					for (Enemy i : masterEnemyMap.keySet()) {
						if (masterEnemyMap.get(i).equals(currentRoom.getRoomName())) {
							enemy = i;
						}
					}
					if (enemy != null) {
						if (command.hasItem()) {
							boolean has = false;
							for (Item i : player.getInventory()) {
								// Removes all spaces, capital letters from name
								if (i.getName().toLowerCase().replaceAll("\\s+", "").equals(command.getItem())) {
									has = true;
								}
							}
							if (has) {
								// if the player has the specified item, and the enemy exists in the room set currentCombat to be the combat between the given enemy and player, and perform the first player attack
								currentCombat = new Combat(player, enemy);
								currentCombat.playerAttack(command.getItem());
							} else {
								System.out.println("You do not have that weapon!");
							}
						} else {
							System.out.println("Attack with what?");
						}
					} else {
						System.out.println("That enemy is not in this room!");
					}
				} else {
					System.out.println("Attack what?");
				}
			} else {
				// Ran when combat is already taking place so that a new combat is not created each time an attack is made
				if (command.hasItem()) {
					boolean has = false;
					for (Item i : player.getInventory()) {
						if (i.getName().toLowerCase().replaceAll("\\s+", "").equals(command.getItem())) {
							has = true;
						}
					}
					if (has) {
						// player attack on enemy.
						currentCombat.playerAttack(command.getItem());
					} else {
						System.out.println("You do not have that weapon!");
					}
				}else {
					System.out.println("Attack with what?");
				}
			}
			break;
			case "craft":
				// if the item is craftable, and the player has the required components give the crafted item to the player, and remove the materials
				if(command.hasItem()) {
					Class<?> clazz;
					CraftableItem object;
					try {
						clazz = Class.forName("com.bayviewglen.zork.Items." + command.getItem().substring(0, 1).toUpperCase().trim() + command.getItem().substring(1).trim());
						Constructor<?> ctor = clazz.getConstructor();
						object = (CraftableItem) ctor.newInstance();
						if(object.isCraftable()) {
							boolean playerHasItems = true;
							boolean hasItem = false;
							// Nested for loops to check if player has all required items
							for(Item i : object.getMaterials()) {
								hasItem = false;
								for(Item pi : player.getInventory()) {
									if(i.equals(pi)) {
										hasItem = true;
									}
								}
								if(playerHasItems) {
									playerHasItems = hasItem;
								}
							}
							if(playerHasItems) {
								if(player.addToInventory(object)) {
									for(Item i : object.getMaterials()) {
										player.removeFromInventory(i);
									}
									System.out.println("You have crafted a " + object.getName());
								}else {
									System.out.println("You cannot carry any more!");
								}
							}else {
								System.out.println("You do not have the nessecary parts to make a " + object.getName() + "!");
							}
							
						}else {
							System.out.println("You cannot make that item!");
						}
					}catch(Exception e) {
						System.out.println("You cannot make that item!");
					}
				}else {
					System.out.println("Craft what?");
				}
				break;
			case "use":
				if(command.hasItem()) {
					Class<?> clazz;
					Item object;
					try {
						clazz = Class.forName(
								"com.bayviewglen.zork.Items." + command.getItem().substring(0, 1).toUpperCase().trim()
										+ command.getItem().substring(1).trim());
						Constructor<?> ctor = clazz.getConstructor();
						object = (Item) ctor.newInstance();
						// If the item is not a bandage, run what is in the catch part of the try catch 
						if(!object.equals(new Bandage()))
							throw new Exception();
						boolean hasBandage = false;
						// iterate through player inventory to see if bandage exists
						for(Item i : player.getInventory()) {
							if(i.equals(new Bandage())) {
								hasBandage = true;
							}
						}
						if(hasBandage) {
							System.out.println("You are no longer bleeding.");
							player.setBleeding(false);
							player.removeFromInventory(new Bandage());
						}else {
							System.out.println("You do not have a bandage!");
						}
					}catch (Exception e) {
						System.out.println("You cannot use that item!");
					}
				}else {
					System.out.println("Use what?");
				}
		default:
			return false;
		}
		return false;
	}

	// implementations of user commands:
	/**
	 * Print out some help information. Here we print some stupid, cryptic message
	 * and a list of the command words.
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
		else if (nextRoom.getLocked() && nextRoom.getBoarded()) { // check if the room is boarded or locked, if so do not allow the player to enter
			System.out.println("The door is locked and boarded shut. You need to find a key and crowbar to open it.");
		} else if (nextRoom.getLocked()) {
			System.out.println("The door is locked. You need a key to open it.");
		} else {
			// This is run when there are no problems leaving the room
			currentRoom = nextRoom;
			// print description and enemies in the room from the hashmap
			System.out.print(currentRoom.longDescription());
			boolean hasEnemy = false;
			Enemy enemy = null;
			String room = "";
			for (String i : masterEnemyMap.values()) {
				if (currentRoom.getRoomName().equals(i)) {
					hasEnemy = true;
					room = i;
				}
			}
			for (Enemy i : masterEnemyMap.keySet()) {
				if (masterEnemyMap.get(i).equals(room)) {
					enemy = i;
				}
			}
			// if the room has an enemy, display it. 
			if (hasEnemy) {
				System.out.println(enemy.getName() + ", " + enemy.getDescription() + " has appeared!");
				System.out.println(currentRoom.itemString());
				System.out.println(currentRoom.exitString());
			} else {
				System.out.println(currentRoom.itemString());
				System.out.println(currentRoom.exitString());
			}
		}
	}

	public void removeEnemy(String r) {
		masterEnemyMap.values().remove(r);
	}
}