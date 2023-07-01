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
package com.rs.game.content.miniquests.huntforsurok.bork;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.achievements.AchievementDef;
import com.rs.game.content.achievements.AchievementReqsMisc;
import com.rs.game.content.achievements.AchievementSetRewards;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.minigames.treasuretrails.TreasureTrailsManager;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.EffigyDrop;
import com.rs.utils.NPCClueDrops;
import com.rs.utils.Ticks;
import com.rs.utils.drop.ClueDrop;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class Bork extends NPC {

	public Bork(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		setLureDelay(0);
		setForceAgressive(true);
	}

	public boolean blocksOtherNpcs() {
		return false;
	}

	@Override
	public void sendDeath(Entity source) {
		for(NPC npc : World.getNPCsInChunkRange(source.getChunkId(), 3))
			if(npc.getId() == 7135)
				npc.sendDeath(source);
		resetWalkSteps();
		getCombat().removeTarget();
		if (source instanceof Player player) {
			player.resetReceivedHits();
			player.getInterfaceManager().sendForegroundInterfaceOverGameWindow(693);
			WorldTasks.schedule(8, () -> {
				player.getInterfaceManager().closeInterfacesOverGameWindow();
				//player.startConversation(new DagonHai(), 7137);
				setNextAnimation(new Animation(getCombatDefinitions().getDeathEmote()));
				WorldTasks.schedule(4, () -> {
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
				});
			});
		}
	}

	@Override
	public void drop() {
		Player killer = getMostDamageReceivedSourcePlayer();
		if (killer == null)
			return;
		if (!killer.getDailyB("borkKilled")) {
			boolean diaryReward = SetReward.VARROCK_ARMOR.hasRequirements(killer, AchievementDef.Area.VARROCK, AchievementDef.Difficulty.HARD, false);
			boolean row = killer.getEquipment().getRingId() != -1 && ItemDefinitions.getDefs(killer.getEquipment().getRingId()).getName().toLowerCase().contains("ring of wealth");
			killer.setDailyB("borkKilled", true);
			killer.getSkills().addXp(Skills.SLAYER, diaryReward ? 3000 : 1500);

			List<Item> drops = new ArrayList<>();
			drops.add(new Item(532, 1));
			drops.add(new Item(12159, 2 * (diaryReward ? 2 : 1) + (row ? 1 : 0)));
			drops.add(new Item(12160, 7 * (diaryReward ? 2 : 1) + (row ? 3 : 0)));
			drops.add(new Item(12163, 5 * (diaryReward ? 2 : 1)));
			drops.add(new Item(995, Utils.random(2, 20000) * (diaryReward ? 2 : 1)));
			drops.add(new Item(1623, 1 * (diaryReward ? 2 : 1)));
			drops.add(new Item(1621, 1 * (diaryReward ? 2 : 1) + (row ? 2 : 0)));
			drops.add(new Item(1619, 1 * (diaryReward ? 2 : 1) + (row ? 1 : 0)));
			if (Utils.random(64) == 0)
				sendDrop(killer, new Item(18778, 1));
			if (Utils.random(32) == 0)
				sendDrop(killer, new Item(TreasureTrailsManager.SCROLL_BOXES[3], 1));
			if (Utils.random(16) == 0)
				sendDrop(killer, new Item(TreasureTrailsManager.SCROLL_BOXES[2], 1));
			for (Item item : drops)
				sendDrop(killer, item);
		}
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(7134, (npcId, tile) -> new Bork(npcId, tile, false));
}
