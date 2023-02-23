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
package com.rs.game.content;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class AncientEffigies {

	public static final int[] SKILL_1 = { Constants.AGILITY, Constants.CONSTRUCTION, Constants.COOKING, Constants.FISHING, Constants.FLETCHING, Constants.HERBLORE, Constants.MINING, Constants.SUMMONING };
	public static final int[] SKILL_2 = { Constants.CRAFTING, Constants.THIEVING, Constants.FIREMAKING, Constants.FARMING, Constants.WOODCUTTING, Constants.HUNTER, Constants.SMITHING, Constants.RUNECRAFTING };
	public static final int STARVED_ANCIENT_EFFIGY = 18778, NOURISHED_ANCIENT_EFFIGY = 18779, SATED_ANCIENT_EFFIGY = 18780, GORGED_ANCIENT_EFFIGY = 18781, DRAGONKIN_LAMP = 18782;

	public static ItemClickHandler handleEffigies = new ItemClickHandler(new Object[] { SATED_ANCIENT_EFFIGY, GORGED_ANCIENT_EFFIGY, NOURISHED_ANCIENT_EFFIGY, STARVED_ANCIENT_EFFIGY }, e -> {
		int type = -1;
		if(e.getItem().getMetaData("effigyType") != null) {
			type = e.getItem().getMetaDataI("effigyType");
			if (((int) Math.floor(type)) >= SKILL_1.length) {
				type = Utils.getRandomInclusive(7);
				e.getPlayer().getInventory().replace(e.getItem(), new Item(e.getItem().getId(), e.getItem().getAmount()).addMetaData("effigyType", type));
			}
		}
		if (e.getItem().getMetaData("effigyType") == null) {
			type = Utils.getRandomInclusive(7);
			e.getPlayer().getInventory().replace(e.getItem(), new Item(e.getItem().getId(), e.getItem().getAmount()).addMetaData("effigyType", type));
		}
		
		if (type == -1)
			return;

		final int skill1 = SKILL_1[type];
		final int skill2 = SKILL_2[type];
		e.getPlayer().startConversation(new Dialogue()
				.addSimple("As you inspect the ancient effigy you begin to feel a strange sensation of the relic searching your mind, drawing on your knowledge.")
				.addSimple("Images from your experiences of " + getMessage(skill1) + " fill your mind.")
				.addOptions("Which images do you wish to focus on?", new Options() {
					@Override
					public void create() {
						if(e.getPlayer().getSkills().getLevel(skill1) < getRequiredLevel(e.getItem().getId()))
							option(Constants.SKILL_NAME[skill1], new Dialogue()
									.addSimple("The images in your mind fade; the ancient effigy seems to desire knowledge of experiences you have not yet had.", () -> {
										e.getPlayer().sendMessage("You require at least level " + getRequiredLevel(e.getItem().getId()) + " " + Constants.SKILL_NAME[skill1] + " to investigate the ancient effigy further.");
										e.getPlayer().setNextAnimation(new Animation(4067));
									})
							);
						else
							option(Constants.SKILL_NAME[skill1], new Dialogue()
									.addSimple("As you focus on your memories, you can almost hear a voice in the back of your mind whispering to you...")
									.addSimple("The ancient effigy glows briefly; it seems changed somehow and no longer responds to the same memories as before.", ()->{
										e.getPlayer().getSkills().addXpLamp(skill1, getExp(e.getItem().getId()));
										e.getPlayer().sendMessage("You have gained " + getExp(e.getItem().getId()) + " " + Constants.SKILL_NAME[skill1] + " experience!");
										effigyInvestigation(e.getPlayer(), e.getItem());
									})
									.addSimple("A sudden bolt of inspiration flashes through your mind, revealing new insight into your experiences!")
							);
						if(e.getPlayer().getSkills().getLevel(skill2) < getRequiredLevel(e.getItem().getId()))
							option(Constants.SKILL_NAME[skill2], new Dialogue()
									.addSimple("The images in your mind fade; the ancient effigy seems to desire knowledge of experiences you have not yet had.", () -> {
										e.getPlayer().sendMessage("You require at least level " + getRequiredLevel(e.getItem().getId()) + Constants.SKILL_NAME[skill2] + " to investigate the ancient effigy further.");
										e.getPlayer().setNextAnimation(new Animation(4067));
									})
							);
						else
							option(Constants.SKILL_NAME[skill2], new Dialogue()
									.addSimple("As you focus on your memories, you can almost hear a voice in the back of your mind whispering to you...")
									.addSimple("The ancient effigy glows briefly; it seems changed somehow and no longer responds to the same memories as before.", ()->{
										e.getPlayer().getSkills().addXpLamp(skill2, getExp(e.getItem().getId()));
										e.getPlayer().sendMessage("You have gained " + getExp(e.getItem().getId()) + " " + Constants.SKILL_NAME[skill2] + " experience!");
										effigyInvestigation(e.getPlayer(), e.getItem());
									})
									.addSimple("A sudden bolt of inspiration flashes through your mind, revealing new insight into your experiences!")
							);
					}
				}));
	});

	public static int getRequiredLevel(int id) {
		switch (id) {
		case STARVED_ANCIENT_EFFIGY:
			return 91;
		case NOURISHED_ANCIENT_EFFIGY:
			return 93;
		case SATED_ANCIENT_EFFIGY:
			return 95;
		case GORGED_ANCIENT_EFFIGY:
			return 97;
		}
		return -1;
	}

	public static String getMessage(int skill) {
		switch (skill) {
		case Constants.AGILITY:
			return "deftness and precision";
		case Constants.CONSTRUCTION:
			return "buildings and security";
		case Constants.COOKING:
			return "fire and preparation";
		case Constants.FISHING:
			return "life and cultivation";
		case Constants.FLETCHING:
			return "lumber and woodworking";
		case Constants.HERBLORE:
			return "flora and fuana";
		case Constants.MINING:
			return "metalwork and minerals";
		case Constants.SUMMONING:
			return "binding essence and spirits";
		}
		return null;
	}

	public static int getExp(int itemId) {
		switch (itemId) {
		case STARVED_ANCIENT_EFFIGY:
			return 15000;
		case NOURISHED_ANCIENT_EFFIGY:
			return 20000;
		case SATED_ANCIENT_EFFIGY:
			return 25000;
		case GORGED_ANCIENT_EFFIGY:
			return 30000;
		}
		return -1;
	}

	public static void effigyInvestigation(Player player, Item item) {
		Inventory inv = player.getInventory();
		if (item.getId() == STARVED_ANCIENT_EFFIGY)
			inv.replace(item, new Item(NOURISHED_ANCIENT_EFFIGY, 1).addMetaData("effigyType", Utils.random(7)+1.0));
		else if (item.getId() == NOURISHED_ANCIENT_EFFIGY)
			inv.replace(item, new Item(SATED_ANCIENT_EFFIGY, 1).addMetaData("effigyType", Utils.random(7)+1.0));
		else if (item.getId() == SATED_ANCIENT_EFFIGY)
			inv.replace(item, new Item(GORGED_ANCIENT_EFFIGY, 1).addMetaData("effigyType", Utils.random(7)+1.0));
		else if (item.getId() == GORGED_ANCIENT_EFFIGY) {
			player.incrementCount("Ancient Effigies opened");
			inv.replace(item, new Item(DRAGONKIN_LAMP, 1));
		}
		player.setNextAnimation(new Animation(item.getId() == GORGED_ANCIENT_EFFIGY ? 14177 : 4068));
		if (item.getId() == GORGED_ANCIENT_EFFIGY)
			player.setNextSpotAnim(new SpotAnim(2692));
	}
}
