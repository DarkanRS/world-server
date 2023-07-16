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
package com.rs.game.content.skills.herblore;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@PluginEventHandler
public class Herblore extends PlayerAction {

	private CraftablePotion potion;
	private int ticks;

	public Herblore(CraftablePotion potion, int amount) {
		this.potion = potion;
		ticks = amount;
	}

	@Override
	public boolean start(Player player) {
		if (player == null || potion == null)
			return false;
		if (player.getSkills().getLevel(Constants.HERBLORE) < potion.getReq()) {
			player.simpleDialogue("You need a herblore level of " + potion.getReq() + " to combine these ingredients.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (!player.getInventory().containsItem(potion.getPrimary()) || !player.getInventory().containsItems(potion.getSecondaries())) {
			player.sendMessage("You don't have enough ingredients left.", true);
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		player.setNextAnimation(new Animation(363));
		ticks--;
		player.getInventory().deleteItem(potion.getPrimary());
		List<Item> secondaries = new LinkedList<>(Arrays.asList(potion.getSecondaries()));
		boolean cleansingProc = player.hasScrollOfCleansing && Utils.random(100) <= 10;
		if (cleansingProc)
			player.sendMessage("Your scroll of cleansing saves " + Utils.addArticle(secondaries.remove(Utils.random(secondaries.size())).getName().toLowerCase()) + ".");
		if (secondaries.size() > 0)
			player.getInventory().removeItems(secondaries);
		player.getInventory().addItemDrop(potion.getProduct().clone());
		player.incrementCount(potion.getProduct().getName() + " mixed");
		String potName = potion.getProduct().getName().toLowerCase();
		player.sendMessage("You mix " + Utils.addArticle(potName) + ".", true);
		player.getSkills().addXp(Constants.HERBLORE, potion.getXp());
		if (ticks > 0)
			return cleansingProc ? 0 : 1;
		return -1;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}

	public static ItemOnItemHandler craftPotion = new ItemOnItemHandler(true, CraftablePotion.MAP.keySet().toArray(), e -> {
		CraftablePotion potion = CraftablePotion.forCombo(e.getItem1().getId(), e.getItem2().getId());
		if (potion == null)
			return;
		if (potion == CraftablePotion.ANCHOVY_OIL && !e.getPlayer().getInventory().containsItem(6097)) {
			e.getPlayer().sendMessage("You need a sieve to make anchovy oil.");
			return;
		}
		e.getPlayer().startConversation(new Conversation(e.getPlayer())
			.addNext(new MakeXStatement(new int[]{potion.getProduct().getId()}, 28))
			.addNext(() -> e.getPlayer().getActionManager().setAction(new Herblore(potion, MakeXStatement.getQuantity(e.getPlayer())))));
	});
}