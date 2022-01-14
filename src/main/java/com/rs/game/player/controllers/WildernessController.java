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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.controllers;

import com.rs.Settings;
import com.rs.game.Entity;
import com.rs.game.ForceMovement;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.skills.thieving.Thieving;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class WildernessController extends Controller {

	private transient boolean showingSkull;

	@Override
	public void start() {
		checkBoosts(player);
	}

	public static void checkBoosts(Player player) {
		boolean changed = false;
		int level = player.getSkills().getLevelForXp(Constants.ATTACK);
		int maxLevel = (int) (level + 5 + (level * 0.15));
		if (maxLevel < player.getSkills().getLevel(Constants.ATTACK)) {
			player.getSkills().set(Constants.ATTACK, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Constants.STRENGTH);
		maxLevel = (int) (level + 5 + (level * 0.15));
		if (maxLevel < player.getSkills().getLevel(Constants.STRENGTH)) {
			player.getSkills().set(Constants.STRENGTH, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Constants.DEFENSE);
		maxLevel = (int) (level + 5 + (level * 0.15));
		if (maxLevel < player.getSkills().getLevel(Constants.DEFENSE)) {
			player.getSkills().set(Constants.DEFENSE, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Constants.RANGE);
		maxLevel = (int) (level + 5 + (level * 0.1));
		if (maxLevel < player.getSkills().getLevel(Constants.RANGE)) {
			player.getSkills().set(Constants.RANGE, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Constants.MAGIC);
		maxLevel = level + 5;
		if (maxLevel < player.getSkills().getLevel(Constants.MAGIC)) {
			player.getSkills().set(Constants.MAGIC, maxLevel);
			changed = true;
		}
		if (changed)
			player.sendMessage("Your extreme potion bonus has been reduced.");
	}

	@Override
	public boolean login() {
		moved();
		return false;
	}

	@Override
	public boolean keepCombating(Entity target) {
		if (target instanceof NPC)
			return true;
		if (!canAttack(target))
			return false;
		if (target instanceof Player opp)
			if (!player.attackedBy(opp.getUsername()))
				player.setWildernessSkull();
		if (player.getCombatDefinitions().getSpell() == null && Utils.inCircle(new WorldTile(3105, 3933, 0), target, 24)) {
			player.sendMessage("You can only use magic in the arena.");
			return false;
		}
		return true;
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof Player p2) {
			if (player.isCanPvp() && !p2.isCanPvp()) {
				player.sendMessage("That player is not in the wilderness.");
				return false;
			}
			if (canHit(target))
				return true;
			return false;
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		if (target instanceof NPC)
			return true;
		Player p2 = (Player) target;
		if (Math.abs(player.getSkills().getCombatLevel() - p2.getSkills().getCombatLevel()) > getWildLevel())
			return false;
		return true;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		if ((getWildLevel() > 20) || player.hasEffect(Effect.TELEBLOCK)) {
			player.sendMessage("A mysterious force prevents you from teleporting.");
			return false;
		}
		return true;

	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		if (player.hasEffect(Effect.TELEBLOCK)) {
			player.sendMessage("A mysterious force prevents you from teleporting.");
			return false;
		}
		if (getWildLevel() <= 30 && player.getTempAttribs().getB("glory")) {
			player.getTempAttribs().setB("glory", false);
			return true;
		}
		if (getWildLevel() > 20) {
			player.getTempAttribs().setB("glory", false);
			player.sendMessage("A mysterious force prevents you from teleporting.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		if (player.hasEffect(Effect.TELEBLOCK)) {
			player.sendMessage("A mysterious force prevents you from teleporting."); //10
			return false;
		}
		return true;
	}

	public void showSkull() {
		player.getInterfaceManager().setOverlay(381);
	}

	public static boolean isDitch(int id) {
		return id >= 1440 && id <= 1444 || id >= 65076 && id <= 65087;
	}

	@Override
	public boolean processObjectClick1(final GameObject object) {
		if (isDitch(object.getId())) {
			player.lock();
			player.setNextAnimation(new Animation(6132));
			final WorldTile toTile = new WorldTile(object.getRotation() == 1 || object.getRotation() == 3 ? object.getX() + 2 : player.getX(), object.getRotation() == 0 || object.getRotation() == 2 ? object.getY() - 1 : player.getY(),
					object.getPlane());

			player.setNextForceMovement(new ForceMovement(new WorldTile(player), 1, toTile, 2, object.getRotation() == 0 || object.getRotation() == 2 ? Direction.SOUTH : Direction.EAST));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(toTile);
					player.faceObject(object);
					removeIcon();
					removeController();
					player.resetReceivedDamage();
					player.unlock();
				}
			}, 2);
			return false;
		}
		if (object.getId() == 2557 || object.getId() == 65717) {
			player.sendMessage("It seems it is locked, maybe you should try something else.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(final GameObject object) {
		if (object.getId() == 2557 || object.getId() == 65717) {
			Thieving.pickDoor(player, object);
			return false;
		}
		return true;
	}

	@Override
	public void sendInterfaces() {
		if (isAtWild(player))
			showSkull();
	}

	@Override
	public boolean sendDeath() {
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					player.setNextAnimation(new Animation(836));
				else if (loop == 1)
					player.sendMessage("Oh dear, you have died.");
				else if (loop == 3) {
					Player killer = player.getMostDamageReceivedSourcePlayer();
					if (killer != null) {
						killer.removeDamage(player);
						killer.increaseKillCount(player);
					}
					player.sendItemsOnDeath(killer);
					player.getEquipment().init();
					player.getInventory().init();
					player.reset();
					if (player.get("customspawn") instanceof WorldTile spawn)
						player.setNextWorldTile(spawn);
					else
						player.setNextWorldTile(new WorldTile(Settings.getConfig().getPlayerRespawnTile()));
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					removeIcon();
					removeController();
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void moved() {
		boolean isAtWild = isAtWild(player);
		boolean isAtWildSafe = isAtWildSafe();
		if (!isAtWildSafe && !isAtWild) {
			player.setCanPvp(false);
			removeIcon();
			removeController();
		} else if (!showingSkull && isAtWild && !isAtWildSafe) {
			showingSkull = true;
			player.setCanPvp(true);
			showSkull();
			player.getAppearance().generateAppearanceData();
		} else if (showingSkull && (isAtWildSafe || !isAtWild))
			removeIcon();
	}

	public void removeIcon() {
		if (showingSkull) {
			showingSkull = false;
			player.setCanPvp(false);
			player.getInterfaceManager().removeOverlay();
			player.getAppearance().generateAppearanceData();
			player.getEquipment().refresh(null);
		}
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public void forceClose() {
		removeIcon();
	}

	public static final boolean isAtWild(WorldTile tile) {// TODO fix this
		return (tile.getX() >= 3011 && tile.getX() <= 3132 && tile.getY() >= 10052 && tile.getY() <= 10175) // fortihrny
				// dungeon
				|| (tile.getX() >= 2940 && tile.getX() <= 3395 && tile.getY() >= 3525 && tile.getY() <= 4000)
				|| (tile.getX() >= 3078 && tile.getX() <= 3139 && tile.getY() >= 9923 && tile.getY() <= 10002)
				|| (tile.getX() >= 3264 && tile.getX() <= 3279 && tile.getY() >= 3279 && tile.getY() <= 3672)
				|| (tile.getX() >= 2756 && tile.getX() <= 2875 && tile.getY() >= 5512 && tile.getY() <= 5627)
				|| (tile.getX() >= 3158 && tile.getX() <= 3181 && tile.getY() >= 3679 && tile.getY() <= 3697)
				|| (tile.getX() >= 3280 && tile.getX() <= 3183 && tile.getY() >= 3885 && tile.getY() <= 3888)
				|| (tile.getX() >= 3012 && tile.getX() <= 3059 && tile.getY() >= 10303 && tile.getY() <= 10351)
				|| (tile.getX() >= 3061 && tile.getX() <= 3071 && tile.getY() >= 10251 && tile.getY() <= 10262);
	}

	public boolean isAtWildSafe() {
		return (player.getX() >= 2940 && player.getX() <= 3395 && player.getY() <= 3524 && player.getY() >= 3523) ||
				(player.getX() >= 3129 && player.getX() <= 3135 && player.getY() <= 9922 && player.getY() >= 9918);
	}

	public int getWildLevel() {
		if (player.getY() > 9900)
			return (player.getY() - 9920) / 8 + 1;
		return (player.getY() - 3520) / 8 + 1;
	}

}
