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
package com.rs.game.content.minigames.sorcgarden;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class SqirkFruitSqueeze extends Conversation {

	public static final int BEER_GLASS = 1919;

	public static enum SqirkFruit {
		WINTER(10847, 5, 10851),
		SPRING(10844, 4, 10848),
		AUTUMM(10846, 3, 10850),
		SUMMER(10845, 2, 10849);

		private int fruitId, amtRequired, resultId;

		private SqirkFruit(int fruitId, int amtRequired, int resultId) {
			this.fruitId = fruitId;
			this.amtRequired = amtRequired;
			this.resultId = resultId;
		}

		public int getFruitId() {
			return fruitId;
		}
	}

	public SqirkFruitSqueeze(Player player, SqirkFruit fruit) {
		super(player);
		if (!player.getInventory().containsItem(BEER_GLASS, 1))
			addItem(BEER_GLASS, "I should get an empty beer glass to hold the juice before I squeeze the fruit.");
		else if (!player.getInventory().containsItem(233, 1))
			addItem(BEER_GLASS, "I should get a pestle and mortal before I squeeze the fruit.");
		else if (!player.getInventory().containsItem(fruit.fruitId, fruit.amtRequired))
			addPlayer(HeadE.CONFUSED, "I think I should wait until I have enough fruit to make a full glass.");
		else {
			player.getInventory().deleteItem(fruit.fruitId, fruit.amtRequired);
			player.getInventory().deleteItem(BEER_GLASS, 1);
			player.getInventory().addItem(fruit.resultId, 1);
			player.getSkills().addXp(Constants.HERBLORE, 5);
			addItem(fruit.resultId, "You squeeze " + fruit.amtRequired + " sq'irks into an empty glass.");
		}
	}

	public static ItemOnItemHandler handleSquirks = new ItemOnItemHandler(new int[] { 233 }, new int[] { 10844, 10845, 10846, 10847 }, e -> {
		SqirkFruit fruit = null;
		for (SqirkFruit f : SqirkFruit.values())
			if (f.getFruitId() == e.getUsedWith(233).getId())
				fruit = f;
		e.getPlayer().startConversation(new SqirkFruitSqueeze(e.getPlayer(), fruit));
	});

}