package com.bayviewglen.zork;

import java.lang.reflect.Constructor;

import com.bayviewglen.zork.Entities.Entity;
import com.bayviewglen.zork.Entities.Player;
import com.bayviewglen.zork.Entities.Enemies.Enemy;
import com.bayviewglen.zork.Items.Item;
import com.bayviewglen.zork.Items.Shavingcream;

public class Combat {
	private Player player;
	private Enemy enemy;
	// if turn is 0 it is player's turn, if 1 it is enemy's turn
	private int turn; 
	public Combat(Player player, Enemy enemy) {
		this.player = player;
		this.enemy = enemy;
	}
	// return new health of enemy
	public double playerAttack(String item) {
		Class<?> clazz;
		Item object;
		try {
			clazz = Class.forName("com.bayviewglen.zork.Items." + item.substring(0, 1).toUpperCase().trim() + item.substring(1).trim());
			Constructor<?> ctor = clazz.getConstructor();
			object = (Item) ctor.newInstance();
			
			double rand = Math.random();
			if(object.equals(new Shavingcream())) {
				System.out.println("You blinded " + enemy.getName());
				player.removeFromInventory(new Shavingcream());
				enemy.setBlinded(true);
			}
			else if(rand<0.1) {
				System.out.println("You missed!");
				
				
			}else if(rand<0.15) {
				enemy.setHealth(enemy.getHealth()-object.getDamage()*1.5);
				if(enemy.getHealth() < 0)
					enemy.setHealth(0);
				System.out.println("You hit " + enemy.getName() + " with a critical hit, doing " + object.getDamage()*1.5 + " damage! His health is now " + enemy.getHealth() + "%");
			}
			else {
				enemy.setHealth(enemy.getHealth()-object.getDamage());
				if(enemy.getHealth() < 0)
					enemy.setHealth(0);
				System.out.println("You did " + object.getDamage() + " damage! " + enemy.getName() + " is now at " + enemy.getHealth() + "% health.");
			}
			
		}catch(Exception e) {
			
		}
		turn = 1;
		return enemy.getHealth();
	}
	
	public double enemyAttack() {
		double rand = Math.random();
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
		}else if(rand < 0.15) {
			player.setHealth(player.getHealth()-enemy.getDamage()*1.5);
			if(player.getHealth() < 0)
				player.setHealth(0);
			System.out.println(enemy.getName() + " hit you with a critical hit, doing " + enemy.getDamage()*1.5 + " damage! Your health is now " + player.getHealth() + "%");
		}
		else {
			player.setHealth(player.getHealth()-enemy.getDamage());
			if(player.getHealth() < 0)
				player.setHealth(0);
			System.out.println(enemy.getName() + " did " + enemy.getDamage() + " damage to you! Your health is now " + player.getHealth() + "%");
		}
		turn = 0;
		return player.getHealth();
	}
	
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
