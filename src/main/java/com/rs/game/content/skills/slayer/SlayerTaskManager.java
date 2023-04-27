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

import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.util.Utils;
import com.rs.utils.shop.ShopsHandler;

import java.util.ArrayList;
import java.util.Collections;

public class SlayerTaskManager {
	private TaskMonster lastMonster = TaskMonster.ABYSSAL_DEMONS;
	private Task task;
	private int killsLeft = 0;

	public Master getMaster() {
		return task == null ? null : task.getMaster();
	}

	public Task getTask() {
		return task;
	}

	public int getTaskMonstersLeft() {
		return killsLeft;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public void removeTask() {
		if (task != null)
			lastMonster = task.getMonster();
		setTask(null);
		killsLeft = 0;
	}

	public void setMonstersLeft(int i) {
		killsLeft = i;
	}

	public boolean isOnTaskAgainst(NPC npc) {
		if (task != null) {
			String npcName = npc.getDefinitions().getName().toLowerCase();
			for (String slayable : task.getMonster().getMonsterNames())
				if (slayable != null)
					if ((npcName.startsWith(" ") && npcName.contains(slayable.replace(" ", ""))) || npcName.contains(slayable))
						return true;
		}
		return false;
	}

	public void sendKill(Player player, NPC n) {
		player.getSkills().addXp(Constants.SLAYER, n.getCombatDefinitions().getHitpoints() / 10);
		killsLeft--;
		if (killsLeft % 10 == 0 && killsLeft != 0)
			player.sendMessage("You're doing great, only " + killsLeft + " " + getTask().getMonster().getName() + " left to slay.");
		if (killsLeft <= 0) {
			player.consecutiveTasks++;
			int amount = 0;
			if (player.consecutiveTasks != 0) {
				if (player.consecutiveTasks % 50 == 0)
					amount = player.getSlayer().getMaster().getPoints50();
				else if (player.consecutiveTasks % 10 == 0)
					amount = player.getSlayer().getMaster().getPoints10();
				else
					amount = player.getSlayer().getMaster().getPoints();
			} else
				amount = player.getSlayer().getMaster().getPoints();
			player.addSlayerPoints(amount);
			player.sendMessage("You have completed " + player.consecutiveTasks + " tasks in a row and receive "+amount+" slayer points!");
			player.sendMessage("You have finished your slayer task, talk to a slayer master for a new one.");
			player.incrementCount("Slayer tasks completed");
			player.jingle(61);
			removeTask();
			player.updateSlayerTask();
		}
	}

	public String getTaskString() {
		if (task == null)
			return "You need something new to hunt; return to a Slayer master.";
		return "Your current assignment is "+task.getMonster().getName()+"; only "+killsLeft+" more to go.";
	}

	public void speakToMaster(Player player, Master master) {
		if (master == null) {
			if (player.hasSlayerTask())
				player.startConversation(new EnchantedGemD(player, player.getSlayer().getMaster()));
			else
				player.sendMessage("You have no task currently.");
		} else
			player.startConversation(new SlayerMasterD(player, master));
	}

	public void generateNewTask(Player player, Master master) {
		if (player.hasSlayerTask()) {
			player.sendMessage("You are trying to get a task with a task already in progress.");
			return;
		}

		ArrayList<Task> possibleTasks = new ArrayList<>();
		for (Task task : Task.values()) {
			if ((task.getMaster() != master) || player.blockedTaskContains(task.getMonster()) || lastMonster == task.getMonster() || (player.getSkills().getLevelForXp(Constants.SLAYER) < task.getMonster().getLevel()))
				continue;
			if (task.getMonster().getQuestReq() != null && !player.isQuestComplete(task.getMonster().getQuestReq()))
				continue;
			if ((task.getMonster() == TaskMonster.AQUANITES && !player.aquanitesUnlocked()) || (task.getMonster() == TaskMonster.CYCLOPES && !((player.getSkills().getLevelForXp(Constants.ATTACK) + player.getSkills().getLevelForXp(Constants.STRENGTH)) >= 130)))
				continue;
			for (int i = 0;i < task.getWeighting();i++)
				possibleTasks.add(task);
		}
		if (possibleTasks.size() <= 0) {
			player.sendMessage("Strange problem. No task available lol.");
			return;
		}
		Collections.shuffle(possibleTasks);
		Task chosenTask = possibleTasks.get(Utils.random(possibleTasks.size()-1));
		setTask(chosenTask);
		player.updateSlayerTask();
		killsLeft = Utils.random(chosenTask.getMin(), chosenTask.getMax());
	}

	public void getTaskFrom(Player player, final Master master) {
		if (player.hasSlayerTask()) {
			if ((master == Master.Turael) && (player.getSlayer().getTask().getMaster().name().indexOf("Turael") == -1)) {
				player.sendOptionDialogue("You already have a task, would you like me to assign you something easier?", ops -> {
					ops.add("Yes, please give me an easier task, " + master.name(), () -> {
						player.consecutiveTasks = 0;
						player.getSlayer().removeTask();
						player.getSlayer().generateNewTask(player, master);
						player.updateSlayerTask();
						player.npcDialogue(master.npcId, HeadE.CHEERFUL_EXPOSITION, "You're doing okay, I suppose. Your new task is to kill " + player.getSlayer().getTaskMonstersLeft() + " " + player.getSlayer().getTask().getMonster().getName());
					});
					ops.add("No, not now.");
				});
			} else
				player.npcDialogue(master.npcId, HeadE.CHEERFUL_EXPOSITION, "You already have a task of " + player.getSlayer().getTask().getMonster().getName() + "" + (master == getMaster() ? "" : (" from "+ player.getSlayer().getTask().getMaster().name())) + ".");
		} else {
			if (player.getSkills().getLevelForXp(Constants.SLAYER) < master.reqSlayerLevel || player.getSkills().getCombatLevelWithSummoning() < master.requiredCombatLevel) {
				player.npcDialogue(master.npcId, HeadE.CHEERFUL_EXPOSITION, "You are not yet experienced enough to recieve my tasks. Come back when you're stronger.");
				return;
			}
			player.sendOptionDialogue("Get a task from " + master.name()+"?", ops -> {
				ops.add("Yes, give me a new task from " + master.name(), () -> {
					player.getSlayer().generateNewTask(player, master);
					player.npcDialogue(master.npcId, HeadE.CHEERFUL_EXPOSITION, "You're doing okay, I suppose. Your new task is to kill " + player.getSlayer().getTaskMonstersLeft() + " " + player.getSlayer().getTask().getMonster().getName());
				});
				ops.add("No, not now.");
			});
		}
	}

	public void openShop(Player player, Master master) {
		switch(master) {
		case Turael:
			ShopsHandler.openShop(player, "turaels_slayer_equipment");
			break;
		case Mazchna:
			ShopsHandler.openShop(player, "mazchnas_slayer_equipment");
			break;
		case Vannaka:
			ShopsHandler.openShop(player, "vannakas_slayer_equipment");
			break;
		case Chaeldar:
			ShopsHandler.openShop(player, "chaeldars_slayer_equipment");
			break;
		case Sumona:
			ShopsHandler.openShop(player, "sumonas_slayer_equipment");
			break;
		case Duradel:
			ShopsHandler.openShop(player, "duradels_slayer_equipment");
			break;
		case Kuradal:
			ShopsHandler.openShop(player, "kuradals_slayer_equipment");
			break;
		default:
			break;
		}
	}
}