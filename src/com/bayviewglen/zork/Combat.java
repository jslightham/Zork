package com.bayviewglen.zork;

import java.lang.reflect.Constructor;

import com.bayviewglen.zork.Entities.Entity;
import com.bayviewglen.zork.Entities.Player;
import com.bayviewglen.zork.Entities.Enemies.Enemy;
import com.bayviewglen.zork.Items.Item;
import com.bayviewglen.zork.Items.Shavingcream;
/*
 * This combat class stores information, and handles turns for both the player and enemy that are in combat.
 * Whenever combat takes place, this class is instantiated and stored in the Game class.
 */
public class Combat {
	private Player player;
	private Enemy enemy;
	// if turn is 0 it is player's turn, if 1 it is enemy's turn
	private int turn; 
	public Combat(Player player, Enemy enemy) {
		this.player = player;
		this.enemy = enemy;
	}
	/*
	 * Handles the attack made by a player. Called whenever a player uses the command word "attack"
	 * This method takes the item from the command as a string, and turns it into an object so that it can find info about the item.
	 * When the player attacks, there is a 10% chance of missing, 10% chance of a critical hit, and an 80% chance of a normal hit.
	 * Returns the new health of the enemy.
	 */
	public double playerAttack(String item) {
		Class<?> clazz;
		Item object;
		try {
			clazz = Class.forName("com.bayviewglen.zork.Items." + item.substring(0, 1).toUpperCase().trim() + item.substring(1).trim());
			Constructor<?> ctor = clazz.getConstructor();
			object = (Item) ctor.newInstance();
			
			double rand = Math.random();
			// Special case when weapon is shaving cream, blind enemy
			if(object.equals(new Shavingcream())) {
				System.out.println("You blinded " + enemy.getName());
				player.removeFromInventory(new Shavingcream());
				enemy.setBlinded(true);
			}
			else if(rand<0.1) {
				System.out.println("You missed!");
				
				
			}else if(rand<0.20) {
				enemy.setHealth(enemy.getHealth()-object.getDamage()*1.5);
				if(enemy.getHealth() < 0)
					enemy.setHealth(0);
				System.out.println("You hit " + enemy.getName() + " with a critical hit, doing " + object.getDamage()*1.5 + " damage! " + enemy.getName() + "'s health is now " + enemy.getHealth() + "%");
			}
			else {
				enemy.setHealth(enemy.getHealth()-object.getDamage());
				// Ensure health is not negative
				if(enemy.getHealth() < 0)
					enemy.setHealth(0);
				System.out.println("You did " + object.getDamage() + " damage! " + enemy.getName() + " is now at " + enemy.getHealth() + "% health.");
			}
			
		}catch(Exception e) {
			
		}
		turn = 1;
		return enemy.getHealth();
	}
	/* 
	 * Much like the playerAttack() method, this method handles attacks for the enemy.
	 * Same attack probabilities as player.
	 * Returns new health of the player.
	 */
	public double enemyAttack() {
		double rand = Math.random();
		// If the enemy is blind, there is a 40% chance of the enemy beocoming unblinded.
		if(enemy.getBlinded()) {
			if(rand <0.4) {
				System.out.println(enemy.getName() + " is no longer blinded!");
				enemy.setBlinded(false);
			}
			else
				System.out.println(enemy.getName() + " is blinded!");
		}
		else if(rand<0.1) {
			System.out.println(enemy.getName() + " missed!");
		}else if(rand < 0.20) {
			player.setHealth(player.getHealth()-enemy.getDamage()*1.5);
			// ensure health does not drop below 0
			if(player.getHealth() < 0)
				player.setHealth(0);
			System.out.println(enemy.getName() + " hit you with a critical hit, doing " + enemy.getDamage()*1.5 + " damage! Your health is now " + player.getHealth() + "%");
			// Set the player's bleeding status to true, when the enemy makes a critical hit
			System.out.println("You are now bleeding.");
			player.setBleeding(true);
		}
		else {
			player.setHealth(player.getHealth()-enemy.getDamage());
			// ensure health does not drop below 0
			if(player.getHealth() < 0)
				player.setHealth(0);
			System.out.println(enemy.getName() + " did " + enemy.getDamage() + " damage to you! Your health is now " + player.getHealth() + "%");
		}
		turn = 0;
		return player.getHealth();
	}
	
	/*
	 * Getters and setters for this class.
	 */
	public int getTurn() {
		return this.turn;
	}
	
	public void setEnemyTurn() {
		turn = 1;
	}
	
	public Enemy getEnemy() {
		return enemy;
	}
	
	public Entity getPlayer() {
		return player;
	}
	
}
