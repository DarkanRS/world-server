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
package com.rs.game.content.bosses.godwars;

import com.rs.game.World;
import com.rs.game.content.bosses.godwars.zaros.NexArena;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.world.unorganized_dialogue.NexEntrance;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class GodwarsController extends Controller {

	public static int SARADOMIN = 0;
	public static int ARMADYL = 1;
	public static int ZAROS = 2;
	public static int BANDOS = 3;
	public static int ZAMORAK = 4;

	private int[] killcount = new int[5];
	private long lastPrayerRecharge;

	@Override
	public void process() {
		updateKillcount();
	}

	@Override
	public void start() {
		sendInterfaces();
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public boolean login() {
		sendInterfaces();
		return false; // so doesnt remove script
	}

	public static ObjectClickHandler handleZamorakEnter = new ObjectClickHandler(false, new Object[] { 26439 }, e -> {
		e.getPlayer().setRouteEvent(new RouteEvent(Tile.of(e.getObject().getTile()), () -> {
			if (e.getPlayer().withinDistance(e.getObject().getTile(), 3)) {
				if (e.getPlayer().getY() < 5334) {
					if (e.getPlayer().getSkills().getLevel(Constants.HITPOINTS) >= 70) {
						e.getPlayer().useStairs(6999, Tile.of(2885, 5347, 2), 1, 1);
						e.getPlayer().getPrayer().drainPrayer(e.getPlayer().getPrayer().getPoints());
						e.getPlayer().sendMessage("You jump over the broken bridge. You feel the power of Zamorak take sap away at your prayer points.");
					} else
						e.getPlayer().sendMessage("You need a Constitution level of 70 to enter this area.");
				} else
					e.getPlayer().useStairs(6999, Tile.of(2885, 5330, 2), 1, 1);
				return;
			}
		}, true));
	});

	@Override
	public boolean processObjectClick1(final GameObject object) {
		if (object.getId() == 26293) {
			player.useStairs(828, Tile.of(2916, 3746, 0), 0, 0);
			player.getControllerManager().forceStop();
			return false;
		}
		if (object.getId() == 26287 || object.getId() == 26286 || object.getId() == 26288 || object.getId() == 26289) {
			if (lastPrayerRecharge >= System.currentTimeMillis()) {
				player.sendMessage("You must wait a total of 10 minutes before being able to recharge your prayer points.");
				return false;
			} else if (player.inCombat()) {
				player.sendMessage("You cannot recharge your prayer while engaged in combat.");
				return false;
			}
			player.getPrayer().restorePrayer(player.getSkills().getLevelForXp(Constants.PRAYER) * 10);
			player.setNextAnimation(new Animation(645));
			player.sendMessage("Your prayer points feel rejuvinated.");
			lastPrayerRecharge = 600000 + System.currentTimeMillis();
			return false;
		}
		if (object.getId() == 26444) {
			if (player.getSkills().getLevel(Constants.AGILITY) >= 70)
				player.useStairs(828, Tile.of(2914, 5300, 1), 1, 2);
			else
				player.sendMessage("You need an Agility level of 70 to maneuver this obstacle.");
			return false;
		}

		if (object.getId() == 26445) {
			if (player.getSkills().getLevel(Constants.AGILITY) >= 70)
				player.useStairs(828, Tile.of(2920, 5274, 0), 1, 2);
			else
				player.sendMessage("You need an Agility level of 70 to maneuver this obstacle.");
			return false;
		}

		if (object.getId() == 26427 && player.getX() >= 2908) {
			if (killcount[SARADOMIN] >= 40) {
				player.setNextTile(Tile.of(2907, 5265, 0));
				killcount[SARADOMIN] -= 40;
				updateKillcount();
			} else
				player.sendMessage("This door is locked by Saradomin and requires that you kill 40 of his minions before it is unlocked.");
			return false;
		}

		if (object.getId() == 26303) {
			if (player.getSkills().getLevel(Skills.RANGE) >= 70) {
				boolean withinArmadyl = player.getY() < 5276;
				final Tile tile = Tile.of(2871, withinArmadyl ? 5279 : 5269, 2);
				player.lock();
				player.getTasks().scheduleTimer(tick -> {
					switch(tick) {
					case 1 -> {
						player.setNextFaceTile(tile);
						player.setNextAnimation(new Animation(385));
					}
					case 3 -> player.setNextAnimation(new Animation(16635));
					case 4 -> {
						player.getAppearance().setHidden(true);
						World.sendProjectile(Tile.of(player.getTile()), tile, 605, 18, 18, 20, 0.6, 30, 0).getTaskDelay();
						player.forceMove(tile, 0, 180, false, () -> {
							player.getAppearance().setHidden(false);
							player.setNextAnimation(new Animation(16672));
							player.unlock();
							player.resetReceivedHits();
						});
						return false;
					}
					}
					return true;
				});
			} else
				player.sendMessage("You need a Ranged level of 70 to cross this obstacle.");
			return false;
		}


		if (object.getId() == 26426 && player.getY() <= 5295) {
			if (killcount[ARMADYL] >= 40) {
				player.setNextTile(Tile.of(2839, 5296, 2));
				killcount[ARMADYL] -= 40;
				updateKillcount();
			} else
				player.sendMessage("This door is locked by Armadyl and requires that you kill 40 of his minions before it is unlocked.");
			return false;
		}

		if (object.getId() == 26428 && player.getY() >= 5332) {
			if (killcount[ZAMORAK] >= 40) {
				player.setNextTile(Tile.of(2925, 5331, 2));
				killcount[ZAMORAK] -= 40;
				updateKillcount();
			} else
				player.sendMessage("This door is locked by Zamorak and requires that you kill 40 of his minions before it is unlocked.");
			return false;
		}

		if (object.getId() == 26425 && player.getX() <= 2863) {
			if (killcount[BANDOS] >= 40) {
				player.setNextTile(Tile.of(2864, 5354, 2));
				killcount[BANDOS] -= 40;
				updateKillcount();
			} else
				player.sendMessage("This door is locked by Bandos and requires that you kill 40 of his minions before it is unlocked.");
			return false;
		}

		if (object.getId() == 26384) {
			if (player.getSkills().getLevel(Constants.STRENGTH) >= 70) {
				if (player.getInventory().containsItem(2347, 1)) {
					if (player.getX() == 2851) {
						player.sendMessage("You bang on the door with your hammer.");
						player.useStairs(11033, Tile.of(2850, 5333, 2), 1, 2);
					} else if (player.getX() == 2850) {
						player.sendMessage("You bang on the door with your hammer.");
						player.useStairs(11033, Tile.of(2851, 5333, 2), 1, 2);
					}
				} else
					player.sendMessage("You need a hammer to be able to hit the gong to request entry.");
			} else
				player.sendMessage("You need a Strength level of 70 to enter this area.");
			return false;
		}

		if (object.getId() == 57211) {
			if (player.getY() == 5279) {
				Item key = player.getInventory().getItemById(20120);
				if (key != null && key.getMetaData("frozenKeyCharges") != null && key.getMetaDataI("frozenKeyCharges") > 1) {
					player.useStairs(828, Tile.of(2885, 5275, 2), 1, 2);
					key.addMetaData("frozenKeyCharges", key.getMetaDataI("frozenKeyCharges")-1);
					if ((int) key.getMetaData("frozenKeyCharges") == 1)
						player.sendMessage("Your frozen key breaks. You will have to repair it on a repair stand.");
					else
						player.sendMessage("A part of your key chips off. It looks like it will still work.");
				} else
					player.sendMessage("You require a frozen key with enough charges to enter.");
			} else
				player.useStairs(828, Tile.of(2885, 5279, 2), 1, 2);
			return false;
		}

		if (object.getId() == 57260) {
			player.useStairs(828, Tile.of(2886, 5274, 2), 1, 2);
			return false;
		}

		if (object.getId() == 57254) {
			player.useStairs(828, Tile.of(2855, 5221, 0), 1, 2);
			return false;
		}

		if (object.getId() == 57234) {
			if (player.getX() == 2859)
				player.setNextTile(Tile.of(player.getX() + 3, player.getY(), player.getPlane()));
			else if (player.getX() == 2862)
				player.setNextTile(Tile.of(player.getX() - 3, player.getY(), player.getPlane()));
			return false;
		}

		if (object.getId() == 57258) {
			if (killcount[ZAROS] >= 40 || player.getEquipment().wearingFullCeremonial()) {
				if (player.getEquipment().wearingFullCeremonial())
					player.sendMessage("The door somehow recognizes your relevance to the area and allows you to pass through.");
				player.setNextTile(Tile.of(2900, 5204, 0));
			} else
				player.sendMessage("This door is locked by the power of Zaros. You will need to kill at least 40 of his followers before the door will open.");
			return false;
		}

		if (object.getId() == 57225) {
			player.startConversation(new NexEntrance(NexArena.getGlobalInstance(), player));
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(GameObject object) {
		if (object.getId() == 26286)
			Magic.sendNormalTeleportNoType(player, Tile.of(2922, 5345, 2));
		else if (object.getId() == 26287)
			Magic.sendNormalTeleportNoType(player, Tile.of(2912, 5268, 0));
		else if (object.getId() == 26288)
			Magic.sendNormalTeleportNoType(player, Tile.of(2842, 5266, 2));
		else if (object.getId() == 26289)
			Magic.sendNormalTeleportNoType(player, Tile.of(2837, 5355, 2));
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendOverlay(601, true);
		player.getPackets().sendRunScriptReverse(1171);
	}

	@Override
	public boolean sendDeath() {
		remove();
		removeController();
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		remove();
		removeController();
	}

	@Override
	public void forceClose() {
		remove();
	}

	public void updateKillcount() {
		player.getVars().setVarBit(3938, killcount[SARADOMIN]);
		player.getVars().setVarBit(3939, killcount[ARMADYL]);
		player.getVars().setVarBit(8725, killcount[ZAROS]);
		player.getVars().setVarBit(3941, killcount[BANDOS]);
		player.getVars().setVarBit(3942, killcount[ZAMORAK]);
	}

	public void sendKill(int index) {
		killcount[index]++;
		updateKillcount();
	}

	public void remove() {
		player.getInterfaceManager().removeOverlay(true);
		player.sendMessage("The souls of those you have slain leave you as you exit the dungeon.");
	}

	public static boolean isAtGodwars(Tile teleTile) {
		if (teleTile.getX() >= 2816 && teleTile.getY() >= 5185 && teleTile.getX() <= 2943 && teleTile.getY() <= 5375)
			return true;
		return false;
	}

}
