package com.bayviewglen.zork;

import com.bayviewglen.zork.Entities.Entity;

public class Combat {
	private Entity player;
	private Entity enemy;
	public Combat(Entity player, Entity enemy) {
		this.player = player;
		this.enemy = enemy;
	}
}
