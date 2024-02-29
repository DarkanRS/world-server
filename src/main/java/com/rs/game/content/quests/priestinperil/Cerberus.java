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
package com.rs.game.content.quests.priestinperil;

import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Cerberus extends NPC {
	public Cerberus(int id, Tile tile, boolean permaDeath) {
		super(id, tile, permaDeath);
	}

	@Override
	public boolean canBeAttackedBy(Player player) {
		if(player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) >= 3 || player.getQuestManager().isComplete(Quest.PRIEST_IN_PERIL)) {
			player.playerDialogue(HeadE.SHAKING_HEAD,"I'd better not make the King mad at me again!");
			return false;
		}
		return true;
	}

	@Override
	public void handlePreHit(Hit hit) {
		super.handlePreHit(hit);
		if (hit.getLook() == Hit.HitLook.MAGIC_DAMAGE)
			hit.setDamage(0);
	}

	@Override
	public void sendDeath(final Entity source) {
		if(source instanceof Player player && player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 2) {
			player.playerDialogue(HeadE.HAPPY_TALKING,"There we go, one dead dog. I should go and tell Drezel.");
			player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 3);
        }
		super.sendDeath(source);
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[]{ 15255, 7711 }, (npcId, tile) -> new Cerberus(npcId, tile, false));

}
