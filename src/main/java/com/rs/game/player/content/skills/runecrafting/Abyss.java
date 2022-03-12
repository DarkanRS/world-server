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
package com.rs.game.player.content.skills.runecrafting;

import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.content.skills.mining.Pickaxe;
import com.rs.game.player.content.skills.woodcutting.Hatchet;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Abyss {

	protected static final int[][] ABYSS_TELEPORT_OUTER = {
			{ 3059, 4817 }, { 3062, 4812 }, { 3052, 4810 }, { 3041, 4807 }, { 3035, 4811 }, { 3030, 4808 }, { 3026, 4810 }, { 3021, 4811 },
			{ 3015, 4810 }, { 3020, 4818 }, { 3018, 4819 }, { 3016, 4824 }, { 3013, 4827 }, { 3017, 4828 }, { 3015, 4837 }, { 3017, 4843 },
			{ 3014, 4849 }, { 3021, 4847 }, { 3022, 4852 }, { 3027, 4849 }, { 3031, 4856 }, { 3035, 4854 }, { 3043, 4855 }, { 3045, 4852 },
			{ 3050, 4857 }, { 3054, 4855 }, { 3055, 4848 }, { 3060, 4848 }, { 3059, 4844 }, { 3065, 4841 }, { 3061, 4836 }, { 3063, 4832 },
			{ 3064, 4828 }, { 3060, 4824 }, { 3063, 4821 }, { 3041, 4808 }, { 3030, 4810 }, { 3018, 4816 }, { 3015, 4829 }, { 3017, 4840 },
			{ 3020, 4849 }, { 3031, 4855 }, { 3020, 4854 }, { 3035, 4855 }, { 3047, 4854 }, { 3060, 4846 }, { 3062, 4836 }, { 3060, 4828 },
			{ 3063, 4820 }, { 3028, 4806 }
	};

	public static ObjectClickHandler handleAltarEntries = new ObjectClickHandler(new Object[] { 7137, 7139, 7140, 7131, 7130, 7129, 7136, 7135, 7133, 7132, 7141, 7134, 7138 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			switch(e.getObjectId()) {
				case 7137 -> e.getPlayer().setNextWorldTile(RunecraftingAltar.Altar.WATER.getInside());
				case 7139 -> e.getPlayer().setNextWorldTile(RunecraftingAltar.Altar.AIR.getInside());
				case 7140 -> e.getPlayer().setNextWorldTile(RunecraftingAltar.Altar.MIND.getInside());
				case 7131 -> e.getPlayer().setNextWorldTile(RunecraftingAltar.Altar.BODY.getInside());
				case 7130 -> e.getPlayer().setNextWorldTile(RunecraftingAltar.Altar.EARTH.getInside());
				case 7129 -> e.getPlayer().setNextWorldTile(RunecraftingAltar.Altar.FIRE.getInside());
				case 7136 -> e.getPlayer().setNextWorldTile(RunecraftingAltar.Altar.DEATH.getInside());
				case 7135 -> e.getPlayer().setNextWorldTile(RunecraftingAltar.Altar.LAW.getInside());
				case 7133 -> e.getPlayer().setNextWorldTile(RunecraftingAltar.Altar.NATURE.getInside());
				case 7132 -> e.getPlayer().setNextWorldTile(RunecraftingAltar.Altar.COSMIC.getInside());
				case 7141 -> e.getPlayer().setNextWorldTile(RunecraftingAltar.Altar.BLOOD.getInside());
				case 7134 -> e.getPlayer().setNextWorldTile(RunecraftingAltar.Altar.CHAOS.getInside());
				case 7138 -> e.getPlayer().sendMessage("A strange power blocks your exit..");

			}
		}
	};

	public static ObjectClickHandler handleShortcuts = new ObjectClickHandler(new Object[] { 7143, 7153, 7152, 7144, 7150, 7146, 7147, 7148, 7149, 7151, 7145 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			switch(e.getObjectId()) {
				case 7143, 7153 -> clearRocks(e.getPlayer(), e.getObject());
				case 7152, 7144 -> clearTendrils(e.getPlayer(), e.getObject(), new WorldTile(e.getObjectId() == 7144 ? 3028 : 3051, 4824, 0));
				case 7150, 7146 -> clearEyes(e.getPlayer(), e.getObject(), new WorldTile(e.getObject().getX() == 3021 ? 3028 : 3050, 4839, 0));
				case 7147 -> clearGap(e.getPlayer(), e.getObject(), new WorldTile(3030, 4843, 0), false);
				case 7148 -> clearGap(e.getPlayer(), e.getObject(), new WorldTile(3040, 4845, 0), true);
				case 7149 -> clearGap(e.getPlayer(), e.getObject(), new WorldTile(3048, 4842, 0), false);
				case 7151 -> burnGout(e.getPlayer(), e.getObject(), new WorldTile(3053, 4831, 0));
				case 7145 -> burnGout(e.getPlayer(), e.getObject(), new WorldTile(3024, 4834, 0));
			}
		}
	};

	public static void clearRocks(final Player player, final GameObject object) {
		Pickaxe pick = Pickaxe.getBest(player);
		if (pick == null) {
			player.sendMessage("You need a pickaxe in order to clear this obstacle.");
			return;
		}
		player.lock();
		WorldTasks.scheduleTimer(1, 1, ticks -> {
			if (ticks == 1)
				player.faceObject(object);
			else if (ticks == 2)
				player.setNextAnimation(pick.getAnimation());
			else if (ticks == 4) {
				if (!success(player, Constants.MINING)) {
					player.unlock();
					player.setNextAnimation(new Animation(-1));
					return false;
				}
			} else if (ticks >= 5 && ticks <= 7)
				demolish(7158 + (ticks - 5), object);
			else if (ticks == 9) {
				player.setNextWorldTile(new WorldTile(object.getX(), object.getY() + 13, 0));
				player.resetReceivedHits();
				player.unlock();
				return false;
			}
			return true;
		});
	}

	public static void clearTendrils(final Player player, final GameObject object, final WorldTile tile) {
		Hatchet hatchet = Hatchet.getBest(player);
		if (hatchet == null) {
			player.sendMessage("You need a hatchet in order to clear this obstacle.");
			return;
		}
		player.lock();
		WorldTasks.schedule(new WorldTask() {
			int ticks = 0;

			@Override
			public void run() {
				ticks++;
				if (ticks == 1)
					player.faceObject(object);
				else if (ticks == 2)
					player.setNextAnimation(hatchet.getAnim());
				else if (ticks == 3) {
					if (!success(player, Constants.WOODCUTTING)) {
						player.unlock();
						player.setNextAnimation(new Animation(-1));
						stop();
						return;
					}
				} else if (ticks >= 4 && ticks <= 6)
					demolish(7161 + (ticks - 4), object);
				else if (ticks == 7) {
					player.setNextWorldTile(tile);
					player.unlock();
					stop();
					return;
				}
			}
		}, 1, 1);
		return;
	}

	public static void clearEyes(final Player player, final GameObject object, final WorldTile tile) {
		player.lock();
		WorldTasks.schedule(new WorldTask() {
			int ticks = 0;

			@Override
			public void run() {
				ticks++;
				if (ticks == 1)
					player.faceObject(object);
				else if (ticks == 2)
					player.setNextAnimation(new Animation(866));
				else if (ticks == 3) {
					if (!success(player, Constants.THIEVING)) {
						player.unlock();
						player.setNextAnimation(new Animation(-1));
						stop();
						return;
					}
				} else if (ticks >= 4 && ticks <= 6)
					demolish(7168 + (ticks - 4), object);
				else if (ticks == 7) {
					player.setNextWorldTile(tile);
					player.unlock();
					stop();
					return;
				}
			}
		}, 1, 1);
		return;
	}

	public static void clearGap(final Player player, final GameObject object, final WorldTile tile, final boolean quick) {
		player.lock();
		WorldTasks.schedule(new WorldTask() {
			int ticks = 0;

			@Override
			public void run() {
				ticks++;
				if (ticks == 1)
					player.faceObject(object);
				else if (ticks == 3) {
					player.setNextAnimation(new Animation(844));
					if (!quick)
						if (!success(player, Constants.AGILITY)) {
							player.sendMessage("You cannot seem to slip through the gap.");
							player.unlock();
							player.setNextAnimation(new Animation(-1));
							stop();
							return;
						}
				} else if (ticks == 4) {
					player.setNextWorldTile(tile);
					player.unlock();
					stop();
					return;
				}
			}
		}, 1, 1);
		return;
	}

	public static void burnGout(final Player player, final GameObject object, final WorldTile tile) {
		if (!player.getInventory().containsItem(590, 1)) {
			player.sendMessage("You need a tinderbox in order to burn the boil.");
			return;
		}
		player.lock();
		WorldTasks.schedule(new WorldTask() {
			int ticks = 0;

			@Override
			public void run() {
				ticks++;
				if (ticks == 1)
					player.faceObject(object);
				else if (ticks == 2)
					player.setNextAnimation(new Animation(733));
				else if (ticks == 3) {
					if (!success(player, Constants.THIEVING)) {
						player.unlock();
						player.setNextAnimation(new Animation(-1));
						stop();
						return;
					}
				} else if (ticks >= 4 && ticks <= 6)
					demolish(7165 + (ticks - 4), object);
				else if (ticks == 7) {
					player.setNextWorldTile(tile);
					player.unlock();
					stop();
					return;
				}
			}
		}, 1, 1);
		return;
	}

	private static void demolish(int objectId, GameObject object) {
		GameObject o = new GameObject(object);
		o.setId(objectId);
		World.spawnObjectTemporary(o, Ticks.fromSeconds(10));
	}

	private static boolean success(Player player, int requestedSkill) {
		if (((double) player.getSkills().getLevel(requestedSkill) / 99.0) > Math.random())
			return true;
		return false;
	}

	public static void teleport(final Player player, NPC npc) {
		player.lock(2);
		npc.setNextForceTalk(new ForceTalk("Veniens! Sallkar! Rinnesset!"));
		npc.setNextSpotAnim(new SpotAnim(343));
		player.setNextSpotAnim(new SpotAnim(342));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				int index = Utils.random(ABYSS_TELEPORT_OUTER.length);
				player.useStairs(-1, new WorldTile(ABYSS_TELEPORT_OUTER[index][0], ABYSS_TELEPORT_OUTER[index][1], 0), 0, 1);
				Magic.teleControllersCheck(player, new WorldTile(ABYSS_TELEPORT_OUTER[index][0], ABYSS_TELEPORT_OUTER[index][1], 0));
				player.getPrayer().drainPrayer(player.getPrayer().getPoints());
				player.setWildernessSkull();
			}
		}, 2);
	}
}

