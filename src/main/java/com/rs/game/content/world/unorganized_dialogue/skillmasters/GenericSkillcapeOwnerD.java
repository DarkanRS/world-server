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
package com.rs.game.content.world.unorganized_dialogue.skillmasters;

import com.rs.engine.dialogue.Conversation;
import com.rs.game.content.Skillcapes;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GenericSkillcapeOwnerD extends Conversation {

	public static NPCClickHandler skillcapeSkill = new NPCClickHandler(new Object[]{8269, 705, 961, 1685, 682, 802, 847, 4906, 575, 308, 4946, 805, 3295, 437, 2270, 3299, 13632, 5113, 9713}, new String[]{"Talk-to"}, e -> {
				String skill = null;
				switch (e.getNPCId()) {
					case 8269 -> skill = "Strength";
					case 705 -> skill = "Defence";
					case 961 -> skill = "Constitution";
					case 682 -> skill = "Ranging";
					case 802 -> skill = "Prayer";
					case 1658 -> skill = "Magic";
					case 847 -> skill = "Cooking";
					case 4906 -> skill = "Woodcutting";
					case 575 -> skill = "Fletching";
					case 308 -> skill = "Fishing";
					case 4946 -> skill = "Firemaking";
					case 805 -> skill = "Crafting";
					case 3295 -> skill = "Mining";
					case 437 -> skill = "Agility";
					case 2270 -> skill = "Thieving";
					case 3299 -> skill = "Farming";
					case 13632 -> skill = "Runecrafting";
					case 5113 -> skill = "Hunter";
					case 9713 -> skill = "Dungeoneering";
				}
				e.getPlayer().startConversation(new GenericSkillcapeOwnerD(e.getPlayer(), e.getNPCId(), Skillcapes.valueOf(skill)));
			});

public GenericSkillcapeOwnerD(Player player, int npcId, Skillcapes cape) {
		super(player);
		addOption("Choose an option", "What is that cape you're wearing?", "Bye.")
		.addNext(() -> cape.getOffer99CapeDialogue(player, npcId));
		create();
	}
}
