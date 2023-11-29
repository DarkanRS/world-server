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
package com.rs.game.content.skills.dungeoneering.rooms.puzzles;

import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.content.skills.fletching.Fletching;
import com.rs.game.content.skills.smithing.Smithing;
import com.rs.game.content.world.unorganized_dialogue.FremennikScoutD;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class FremennikCampRoom extends PuzzleRoom {

	public static final int FREMENNIK_SCOUT = 11001;
	private static final int[] RAW_FISH = { 49522, 49523, 49524, 49524, 49524 };
	private static final int[] COOKED_FISH = { 49525, 49526, 49527, 49527, 49527 };
	private static final int[] BARS = { 49528, 49529, 49530, 49530, 49530 };
	private static final int[] BATTLE_AXES = { 49531, 49532, 49533, 49533, 49533 };
	private static final int[] LOGS = { 49534, 49535, 49536, 49536, 49536 };
	private static final int[] BOWS = { 49537, 49538, 49539, 49539, 49539 };

	private int stage = 0;

	@Override
	public void openRoom() {
		manager.spawnNPC(reference, FREMENNIK_SCOUT, 8, 5, false, DungeonConstants.NORMAL_NPC);
	}

	@Override
	public boolean processObjectClick1(Player player, GameObject object) {
		if (object.getId() == RAW_FISH[type]) {
			if (!hasRequirement(player, Constants.COOKING)) {
				player.sendMessage("You need a cooking level of " + getRequirement(Constants.COOKING) + " to cook these fish.");
				return false;
			}
			giveXP(player, Constants.COOKING);
			replaceObject(object, COOKED_FISH[type]);
			advance(player);
			player.setNextAnimation(new Animation(897));
			return false;
		}
		if (object.getId() == BARS[type]) {
			if (!hasRequirement(player, Constants.SMITHING)) {
				player.sendMessage("You need a smithing level of " + getRequirement(Constants.SMITHING) + " to smith these battle axes.");
				return false;
			}
			if (!player.getInventory().containsOneItem(Smithing.DUNG_HAMMER)) {
				player.sendMessage("You need a hammer to smith battle axes.");
				return false;
			}
			giveXP(player, Constants.SMITHING);
			replaceObject(object, BATTLE_AXES[type]);
			advance(player);
			player.setNextAnimation(new Animation(898));
			player.setNextSpotAnim(new SpotAnim(2123));
			return false;
		}
		if (object.getId() == LOGS[type]) {
			if (!hasRequirement(player, Constants.FLETCHING)) {
				player.sendMessage("You need a fletching level of " + getRequirement(Constants.FLETCHING) + " to fletch these bows.");
				return false;
			}
			if (!player.getInventory().containsOneItem(Fletching.DUNGEONEERING_KNIFE)) {
				player.sendMessage("You need a knife to fletch bows.");
				return false;
			}
			giveXP(player, Constants.FLETCHING);
			replaceObject(object, BOWS[type]);
			advance(player);
			player.setNextAnimation(new Animation(1248));
			return false;
		}
		return true;
	}

	public void advance(Player player) {
		if (++stage == 3) {
			setComplete();
			player.startConversation(new FremennikScoutD(player, this));
		}
	}

	@Override
	public boolean processNPCClick1(Player player, NPC npc) {
		if (npc.getId() == FREMENNIK_SCOUT) {
			player.startConversation(new FremennikScoutD(player, this));
			return false;
		}
		return true;
	}

	@Override
	public String getCompleteMessage() {
		return null;
	}

}
