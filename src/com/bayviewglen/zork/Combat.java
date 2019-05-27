package com.bayviewglen.zork;

import java.lang.reflect.Constructor;

import com.bayviewglen.zork.Entities.Entity;
import com.bayviewglen.zork.Entities.Enemies.Enemy;
import com.bayviewglen.zork.Items.Item;

public class Combat {
	private Entity player;
	private Enemy enemy;
	// if turn is 0 it is player's turn, if 1 it is enemy's turn
	private int turn; 
	public Combat(Entity player, Enemy enemy) {
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
			if(rand>0.1) {
				enemy.setHealth(enemy.getHealth()-object.getDamage());
				System.out.println("You did " + object.getDamage() + " damage! " + enemy.getName() + " is now at " + enemy.getHealth() + "% health.");
			}else {
				System.out.println("You missed!");
			}
			
		}catch(Exception e) {
			
		}
		turn = 1;
		return enemy.getHealth();
	}
	
	public double enemyAttack() {
		double rand = Math.random();
		if(rand>0.1) {
			player.setHealth(player.getHealth()-enemy.getDamage());
			System.out.println(enemy.getName() + " did " + enemy.getDamage() + " damage to you! Your health is now " + player.getHealth() + "%");
			
		}else {
			System.out.println(enemy.getName() + " missed!");
		}
		turn = 0;
		return player.getHealth();
	}
	
	public int getTurn() {
		return this.turn;
	}
}
