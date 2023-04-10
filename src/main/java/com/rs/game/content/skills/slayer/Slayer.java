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

import java.util.List;

import com.rs.game.World;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnPlayerHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Slayer {

	public static final int BUY_INTERFACE = 164;
	public static final int LEARN_INTERFACE = 378;
	public static final int ASSIGNMENT_INTERFACE = 161;

	public static ItemClickHandler handleKillsLeft = new ItemClickHandler(new String[] { "Kills-left" }, e -> e.getPlayer().sendMessage(e.getPlayer().getSlayer().getTaskString()));
	
	public static ItemOnPlayerHandler groupSlayer = new ItemOnPlayerHandler(new Object[] { 4155 }, e -> {
		e.getPlayer().sendMessage("Group slayer is in progress.");
//		if (other.getCoopSlayerPartner() != null) {
//			player.sendMessage("This player is already in a slayer group with: " + other.getCoopSlayerPartner().getDisplayName());
//			return;
//		}
//		if (player.getCoopSlayerPartner() != null) {
//			player.sendMessage("You are already in a slayer group with: " + player.getCoopSlayerPartner().getDisplayName());
//			return;
//		}
//		player.sendMessage("Sending co-op slayer request...");
//		other.getPackets().sendCoOpSlayerRequestMessage(player);
//		player.getTemporaryAttributtes().put("coopSlayerRequest", other);
	});

	public static void openBuyInterface(Player player) {
		player.getInterfaceManager().sendInterface(BUY_INTERFACE);
		player.getPackets().setIFText(BUY_INTERFACE, 20, "" + player.slayerPoints);
	}

	public static void openLearnInterface(Player player) {
		player.getInterfaceManager().sendInterface(LEARN_INTERFACE);
		player.getPackets().setIFText(LEARN_INTERFACE, 79, "" + player.slayerPoints);
		if (player.isSlayerHelmCreation())
			player.getPackets().setIFText(LEARN_INTERFACE, 100, "Learned");
		if (player.isBroadFletching())
			player.getPackets().setIFText(LEARN_INTERFACE, 90, "Learned");
		if (player.hasCraftROS())
			player.getPackets().setIFText(LEARN_INTERFACE, 99, "Learned");
		if (player.aquanitesUnlocked())
			player.getPackets().setIFText(LEARN_INTERFACE, 91, "Learned");
		if (player.iceStrykeNoCape())
			player.getPackets().setIFText(LEARN_INTERFACE, 98, "Learned");
		if (player.hasLearnedQuickBlows())
			player.getPackets().setIFText(LEARN_INTERFACE, 97, "Learned");
	}

	public static void openAssignmentInterface(Player player) {
		player.getInterfaceManager().sendInterface(ASSIGNMENT_INTERFACE);
		player.getPackets().setIFText(ASSIGNMENT_INTERFACE, 19, "" + player.slayerPoints);
		refreshBlockedTasks(player);
	}

	public static void refreshBlockedTasks(Player player) {
		for(int i = 0;i < player.getBlockedTasks().length;i++)
			if (player.getBlockedTasks()[i] != null)
				player.getPackets().setIFText(ASSIGNMENT_INTERFACE, i+31, player.getBlockedTasks()[i].getName());
			else
				player.getPackets().setIFText(ASSIGNMENT_INTERFACE, i+31, "Empty");
	}

	public static void refreshPoints(Player player, int interfaceId) {
		switch (interfaceId) {
		case BUY_INTERFACE:
			player.getPackets().setIFText(BUY_INTERFACE, 20, "" + player.slayerPoints);
			break;
		case LEARN_INTERFACE:
			player.getPackets().setIFText(LEARN_INTERFACE, 18, "" + player.slayerPoints);
			break;
		case ASSIGNMENT_INTERFACE:
			player.getPackets().setIFText(ASSIGNMENT_INTERFACE, 19, "" + player.slayerPoints);
			break;
		default:
			break;
		}
	}

	public static ButtonClickHandler handleButtons = new ButtonClickHandler(new Object[] { BUY_INTERFACE, LEARN_INTERFACE, ASSIGNMENT_INTERFACE }, e -> {
		switch (e.getInterfaceId()) {
		case BUY_INTERFACE:
			if (e.getComponentId() == 16)
				openLearnInterface(e.getPlayer());
			if (e.getComponentId() == 17)
				openAssignmentInterface(e.getPlayer());
			if (e.getComponentId() == 32 || e.getComponentId() == 24)
				if (e.getPlayer().slayerPoints >= 400) {
					e.getPlayer().getSkills().addXp(Constants.SLAYER, 10000);
					e.getPlayer().slayerPoints -= 400;
					refreshPoints(e.getPlayer(), e.getInterfaceId());
				} else
					e.getPlayer().sendMessage("You need 400 points for 10,000 Slayer experience.");
			if (e.getComponentId() == 36 || e.getComponentId() == 28)
				if (e.getPlayer().slayerPoints >= 35) {
					e.getPlayer().getInventory().addItem(558, 1000);
					e.getPlayer().getInventory().addItem(560, 250);
					e.getPlayer().slayerPoints -= 35;
					refreshPoints(e.getPlayer(), e.getInterfaceId());
				} else
					e.getPlayer().sendMessage("You need 35 points for 250 slayer dart runes.");
			break;

		case LEARN_INTERFACE:
			if (e.getComponentId() == 15)
				openBuyInterface(e.getPlayer());
			if (e.getComponentId() == 76) {
				if (e.getPlayer().slayerPoints < 300) {
					e.getPlayer().sendMessage("You need 300 points to buy broad fletching.");
					return;
				}
				if (!e.getPlayer().isBroadFletching()) {
					e.getPlayer().slayerPoints -= 300;
					e.getPlayer().setBroadFletching(true);
					openLearnInterface(e.getPlayer());
				} else
					e.getPlayer().sendMessage("You already learned broad fletching.");
			}
			if (e.getComponentId() == 77) {
				if (e.getPlayer().slayerPoints < 300) {
					e.getPlayer().sendMessage("You need 300 points to buy the ability to craft the ring of slaying.");
					return;
				}
				if (!e.getPlayer().hasCraftROS()) {
					e.getPlayer().slayerPoints -= 300;
					e.getPlayer().setCraftROS(true);
					openLearnInterface(e.getPlayer());
				} else
					e.getPlayer().sendMessage("You already learned how to craft the ring of slaying.");
			}
			if (e.getComponentId() == 78) {
				if (e.getPlayer().slayerPoints < 400) {
					e.getPlayer().sendMessage("You need 400 points to buy that ability.");
					return;
				}
				if (!e.getPlayer().isSlayerHelmCreation()) {
					e.getPlayer().slayerPoints -= 400;
					e.getPlayer().setSlayerHelmCreation(true);
					openLearnInterface(e.getPlayer());
				} else
					e.getPlayer().sendMessage("You already learned how to craft slayer helmets.");
			}
			if (e.getComponentId() == 74) {
				if (e.getPlayer().slayerPoints < 400) {
					e.getPlayer().sendMessage("You need 400 points to buy that ability.");
					return;
				}
				if (!e.getPlayer().hasLearnedQuickBlows()) {
					e.getPlayer().slayerPoints -= 400;
					e.getPlayer().setHasLearnedQuickBlows(true);
					openLearnInterface(e.getPlayer());
				} else
					e.getPlayer().sendMessage("You already learned how to deliver quick killing blows.");
			}
			if (e.getComponentId() == 73) {
				if (e.getPlayer().slayerPoints < 50) {
					e.getPlayer().sendMessage("You need 50 points to buy that ability.");
					return;
				}
				if (!e.getPlayer().aquanitesUnlocked()) {
					e.getPlayer().slayerPoints -= 50;
					e.getPlayer().setAquanitesUnlocked(true);
					openLearnInterface(e.getPlayer());
				} else
					e.getPlayer().sendMessage("You already persuaded Kuradal to assign aquanites.");
			}
			if (e.getComponentId() == 75) {
				if (e.getPlayer().slayerPoints < 2000) {
					e.getPlayer().sendMessage("You need 2000 points to buy that ability.");
					return;
				}
				if (!e.getPlayer().iceStrykeNoCape()) {
					e.getPlayer().slayerPoints -= 2000;
					e.getPlayer().setIceStrykeNoCape(true);
					openLearnInterface(e.getPlayer());
				} else
					e.getPlayer().sendMessage("You already learned how to fight ice strykewyrms without a fire cape.");
			}
			if (e.getComponentId() == 14)
				openAssignmentInterface(e.getPlayer());
			break;

		case ASSIGNMENT_INTERFACE:
			if (e.getComponentId() == 15)
				openBuyInterface(e.getPlayer());
			if (e.getComponentId() == 14)
				openLearnInterface(e.getPlayer());

			if (e.getComponentId() == 26) {
				if (e.getPlayer().slayerPoints < 30) {
					e.getPlayer().sendMessage("You need 30 points to cancel your task.");
					return;
				}
				if (e.getPlayer().hasSlayerTask()) {
					e.getPlayer().slayerPoints -= 30;
					e.getPlayer().getSlayer().removeTask();
					e.getPlayer().updateSlayerTask();
					refreshPoints(e.getPlayer(), e.getInterfaceId());
				} else
					e.getPlayer().sendMessage("You don't have a slayer task to cancel.");
			}

			if (e.getComponentId() == 27)
				if (e.getPlayer().slayerPoints >= 100) {
					if (e.getPlayer().hasSlayerTask()) {
						if (e.getPlayer().getBlockedTaskNumber() < 6) {
							e.getPlayer().slayerPoints -= 100;
							e.getPlayer().blockTask(e.getPlayer().getSlayer().getTask().getMonster());
							e.getPlayer().getSlayer().removeTask();
							refreshBlockedTasks(e.getPlayer());
							refreshPoints(e.getPlayer(), e.getInterfaceId());
						} else
							e.getPlayer().sendMessage("You are not able to block more than 6 tasks.");
					} else
						e.getPlayer().sendMessage("You don't have a slayer task to block.");
				} else
					e.getPlayer().sendMessage("You need 100 points to block a task.");

			if (e.getComponentId() >= 37 && e.getComponentId() <= 42) {
				int index = e.getComponentId() - 37;
				e.getPlayer().unblockTask(index);
				refreshBlockedTasks(e.getPlayer());
			}
			break;
		}
	});

	public static boolean hasNosepeg(Entity target) {
		if (!(target instanceof Player targetPlayer))
			return true;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 4168 || hasSlayerHelmet(target);
	}

	public static boolean hasEarmuffs(Entity target) {
		if (!(target instanceof Player targetPlayer))
			return true;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 4166 || hat == 13277 || hasSlayerHelmet(target);
	}

	public static boolean hasMask(Entity target) {
		if (!(target instanceof Player targetPlayer))
			return true;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 1506 || hat == 4164 || hat == 13277 || hasSlayerHelmet(target);
	}

	public static boolean hasWitchWoodIcon(Entity target) {
		if (!(target instanceof Player targetPlayer))
			return true;
		int hat = targetPlayer.getEquipment().getAmuletId();
		return hat == 8923;
	}

	public static boolean hasSlayerHelmet(Entity target) {
		if (!(target instanceof Player targetPlayer))
			return true;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 13263 || hat == 14636 || hat == 14637 || hasFullSlayerHelmet(target);
	}

	public static boolean hasFullSlayerHelmet(Entity target) {
		if (!(target instanceof Player targetPlayer))
			return true;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 15492 || hat == 15496 || hat == 15497 || (hat >= 22528 && hat <= 22550);
	}

	public static boolean hasReflectiveEquipment(Entity target) {
		if (!(target instanceof Player targetPlayer))
			return true;
		int shieldId = targetPlayer.getEquipment().getShieldId();
		return shieldId == 4156;
	}

	public static boolean hasSpinyHelmet(Entity target) {
		if (!(target instanceof Player targetPlayer))
			return true;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 4551 || hasSlayerHelmet(target);
	}

	public static boolean isUsingBell(final Player player) {
		player.lock(3);
		player.setNextAnimation(new Animation(6083));
		List<GameObject> objects = ChunkManager.getChunk(player.getChunkId()).getAllObjects();
		if (objects == null)
			return false;
		for (final GameObject object : objects) {
			if (!object.getTile().withinDistance(player.getTile(), 3) || object.getId() != 22545)
				continue;
			player.sendMessage("The bell re-sounds loudly throughout the cavern.");
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					NPC npc = World.spawnNPC(5751, Tile.of(player.getTile()), -1, true);
					npc.getCombat().setTarget(player);
					GameObject o = new GameObject(object);
					o.setId(22544);
					World.spawnObjectTemporary(o, Ticks.fromSeconds(30));
				}
			}, 1);
			return true;
		}
		return false;
	}

	public static boolean isBlackMask(int requestedId) {
		return requestedId >= 8901 && requestedId <= 8920;
	}

	public static boolean isSlayerHelmet(Item item) {
		return item.getName().toLowerCase().contains("slayer helm");
	}

}