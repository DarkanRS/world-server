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
package com.rs.game.content.skills.slayer;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class BossTask {

	private int amount;
	private BossTasks task;

	public BossTask(BossTasks task, int amount) {
		this.task = task;
		this.amount = amount;
	}

	public static enum BossTasks {
		GENERAL_GRAARDOR	(6260, 5, 12, 10000),
		COMMANDER_ZILYANA	(6247, 5, 12, 10000),
		KRIL_TSUTAROTH		(6203, 5, 12, 10000),
		KREE_ARRA			(6222, 5, 12, 10000),
		NEX					(13447, 2, 4, 20000),
		KING_BLACK_DRAGON	(50, 10, 15, 6000),
		KALPHITE_QUEEN		(1158, 10, 12, 7000),
		CORPOREAL_BEAST		(8133, 2, 5, 15000),
		DAGANNOTH_REX		(2883, 8, 12, 10000),
		DAGANNOTH_PRIME		(2882, 8, 12, 10000),
		DAGANNOTH_SUPREME	(2881, 8, 12, 10000),
		CHAOS_ELEMENTAL		(3200, 8, 12, 10000),
		TORMENTED_DEMON		(8349, 8, 12, 10000);


		private static Map<Integer, BossTasks> monsters = new HashMap<>();

		public static BossTasks forId(int id) {
			return monsters.get(id);
		}

		static {
			for (BossTasks monster : BossTasks.values())
				monsters.put(monster.id, monster);
		}

		private int id;
		private int minAmount;
		private int maxAmount;
		private int xp;

		private BossTasks(int id, int minAmount, int maxAmount, int xp) {
			this.id = id;
			this.minAmount = minAmount;
			this.maxAmount = maxAmount;
			this.xp = xp;
		}

		public int getId() {
			return id;
		}

		public int getMinAmount() {
			return minAmount;
		}

		public int getMaxAmount() {
			return maxAmount;
		}

		public int getXp() {
			return xp;
		}

		public static BossTasks getRandomTask() {
			return (BossTasks) monsters.values().toArray()[Utils.random(monsters.values().size()-1)];
		}
	}

	public String getName() {
		return NPCDefinitions.getDefs(task.getId()).getName();
	}

	public void finishTask(Player player) {
		player.incrementCount("Reaper assignments completed");
		player.sendMessage("You have completed your reaper assignment. You are rewarded with "+task.getXp()+" Slayer experience and 15 Reaper points.");
		player.getSkills().addXp(Constants.SLAYER, task.getXp());
		player.reaperPoints += 15;
		player.setDailyB("bossTaskCompleted", true);
		player.setBossTask(null);
	}

	public void sendKill(Player player, NPC npc) {
		World.sendProjectile(npc, player, 3060, 18, 18, 15, 0, 20, 0);
		if (amount >= 1)
			amount--;
		if (amount <= 0)
			finishTask(player);
		else
			player.sendMessage("<col=ff0000>As "+npc.getName()+" dies, you absorb the soul. You now need "+amount+" more souls.");
	}

	public static BossTask create() {
		BossTasks info = BossTasks.getRandomTask();
		BossTask task = new BossTask(info, Utils.random(info.getMinAmount(), info.getMaxAmount()));
		return task;
	}

	public BossTasks getTask() {
		return task;
	}

	public void setTask(BossTasks task) {
		this.task = task;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getMessage() {
		return "You are currently assigned to collect souls from: " + getName() + ". You must still retrieve " + amount + " to complete your assignment.";
	}
}