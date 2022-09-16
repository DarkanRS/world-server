// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.holidayevents.halloween.hw07;

import java.util.HashSet;
import java.util.Set;

import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class Halloween2007Controller extends Controller {

	private static int[] DEAD_END_WEBS = { 27955946, 28005096, 27661029, 27775726, 27726573, 27628265 };

	private int[] webPath;
	private Set<Integer> returnedItems = new HashSet<>();
	private boolean rodeSlide;

	public Halloween2007Controller() {
		randomizePath();
	}

	@Override
	public void start() {
		player.startConversation(new Dialogue().addPlayer(HeadE.CONFUSED, "Well, I'm still in one peice. A good start.."));
		player.setNextWorldTile(Halloween2007.START_LOCATION);
	}

	@Override
	public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
		if (Halloween2007.isPitfall(lastX, lastY)) {
			player.setNextAnimation(new Animation(1950));
			player.lock();

			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(new WorldTile(1698, 4822, 0));
					player.setNextAnimation(new Animation(3640));
					player.fadeScreen(() -> {
						player.unlock();
						player.startConversation(new Dialogue()
								.addNPC(8867, HeadE.CALM_TALK, "That, you will see, was a pitfall trap. You need to walk around them.")
								.addPlayer(HeadE.ANGRY, "But I didn't even see the trap!")
								.addNPC(8867, HeadE.CALM_TALK, "Look closely and you should see the joins of the doors on the ground.")
								.addPlayer(HeadE.SAD, "Ah, okay. Back upstairs it is, then."));
					});
				}
			}, 5);
			return false;
		}
		return true;
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					player.setNextAnimation(new Animation(836));
				else if (loop == 1)
					player.sendMessage("Oh dear, you have died.");
				else if (loop == 3) {
					player.setNextWorldTile(player.getHw07Stage() < 10 ? Halloween2007.START_LOCATION : new WorldTile(3211, 3424, 0));
					player.reset();
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					if (player.getHw07Stage() >= 10)
						player.getControllerManager().forceStop();
					player.jingle(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void process() {
		refreshSkull();
	}

	@Override
	public boolean login() {
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		removeItems();
	}

	@Override
	public void forceClose() {
		removeItems();
	}

	public void removeItems() {
		for (int itemId : Halloween2007.ALL_ITEMS)
			if (player.getInventory().containsItem(itemId))
				player.getInventory().deleteItem(itemId, 28);
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.sendMessage("A mysterious force prevents you from teleporting.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.sendMessage("A mysterious force prevents you from teleporting.");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.sendMessage("A mysterious force prevents you from teleporting.");
		return false;
	}

	public int[] getWebPath() {
		return webPath;
	}

	public void randomizePath() {
		webPath = getRandomWebPath();
	}

	private static int[] getRandomWebPath() {
		return Halloween2007.SPIDER_PATHS[Utils.random(Halloween2007.SPIDER_PATHS.length)];
	}

	public void refreshSkull() {
		player.getVars().setVarBit(4086, player.getHw07Stage() >= 4 || player.getInventory().containsItem(Halloween2007.SERVANT_SKULL) ? 1 : 0);
	}

	public boolean checkWeb(int tileHash) {
		for (int check : DEAD_END_WEBS)
			if (check == tileHash)
				return true;
		for (int check : webPath)
			if (check == tileHash)
				return true;
		return false;
	}

	public boolean isItemsCorrect() {
		return returnedItems.size() >= 7;
	}

	public boolean isItemReturned(int itemId) {
		return returnedItems.contains(itemId);
	}

	public void resetReturnedItems() {
		returnedItems = new HashSet<>();
	}

	public void returnItem(int itemId) {
		player.getInventory().deleteItem(itemId, 1);
		returnedItems.add(itemId);
	}

	public boolean isRodeSlide() {
		return rodeSlide;
	}

	public void setRodeSlide(boolean rodeSlide) {
		this.rodeSlide = rodeSlide;
	}
}
