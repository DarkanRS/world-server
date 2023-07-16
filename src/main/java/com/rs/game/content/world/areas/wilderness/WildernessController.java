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
package com.rs.game.content.world.areas.wilderness;

import com.rs.Settings;
import com.rs.game.content.Effect;
import com.rs.game.content.Potions;
import com.rs.game.content.skills.thieving.Thieving;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class WildernessController extends Controller {

	private transient boolean showingSkull;

	@Override
	public void start() {
		Potions.checkOverloads(player);
		player.addEffect(Effect.OVERLOAD_PVP_REDUCTION, Integer.MAX_VALUE);
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
		if (player.getCombatDefinitions().getSpell() == null && Utils.inCircle(Tile.of(3105, 3933, 0), target.getTile(), 24)) {
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
		if (Math.abs(player.getSkills().getCombatLevel() - p2.getSkills().getCombatLevel()) > Math.min(player.getPvpCombatLevelThreshhold(), p2.getPvpCombatLevelThreshhold())) {
			player.sendMessage("Your level difference is too great!<br>You need to move deeper into the Wilderness.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		if (getWildLevel() > 20 || player.hasEffect(Effect.TELEBLOCK)) {
			player.sendMessage("A mysterious force prevents you from teleporting.");
			return false;
		}
		return true;

	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
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
	public boolean processObjectTeleport(Tile toTile) {
		if (player.hasEffect(Effect.TELEBLOCK)) {
			player.sendMessage("A mysterious force prevents you from teleporting."); //10
			return false;
		}
		return true;
	}

	public void showSkull() {
		player.getInterfaceManager().sendOverlay(381);
	}

	public static boolean isDitch(int id) {
		return id >= 1440 && id <= 1444 || id >= 65076 && id <= 65087;
	}

	@Override
	public boolean processObjectClick1(final GameObject object) {
		if (isDitch(object.getId())) {
			final Tile toTile = Tile.of(object.getRotation() == 1 || object.getRotation() == 3 ? object.getX() + 2 : player.getX(), object.getRotation() == 0 || object.getRotation() == 2 ? object.getY() - 1 : player.getY(), object.getPlane());
			player.forceMove(toTile, 6132, 30, 60, () -> {
				player.faceObject(object);
				removeIcon();
				removeController();
				player.resetReceivedDamage();
			});
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
			if (player.getSkills().getLevel(Skills.THIEVING) < 23) {
				player.sendMessage("You need at least 23 thieving to pick this lock.");
				return false;
			}
			if (!player.getInventory().containsItem(1523, 1)) {
				player.sendMessage("You cannot seem to pick this lock. Perhaps a lock pick would help.");
				return false;
			}
			Thieving.pickDoor(player, object);
			return false;
		}
		return true;
	}

	@Override
	public void sendInterfaces() {
		if (isAtWild(player.getTile()))
			showSkull();
	}

	@Override
	public boolean sendDeath() {
		WorldTasks.scheduleTimer(0, 1, loop -> {
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
				player.sendPVPItemsOnDeath(killer);
				player.getEquipment().init();
				player.getInventory().init();
				player.reset();
				if (player.get("customspawn") instanceof Tile spawn)
					player.setNextTile(spawn);
				else
					player.setNextTile(Tile.of(Settings.getConfig().getPlayerRespawnTile()));
				player.setNextAnimation(new Animation(-1));
			} else if (loop == 4) {
				removeIcon();
				removeController();
				player.jingle(90);
				return false;
			}
			return true;
		});
		return false;
	}

	@Override
	public void moved() {
		boolean isAtWild = isAtWild(player.getTile());
		boolean isAtWildSafe = isAtWildSafe();
		if (!isAtWildSafe && !isAtWild) {
			player.setPvpCombatLevelThreshhold(-1);
			player.setCanPvp(false);
			removeIcon();
			removeController();
		} else if (!showingSkull && isAtWild && !isAtWildSafe) {
			showingSkull = true;
			player.setCanPvp(true);
			showSkull();
			player.setPvpCombatLevelThreshhold(getWildLevel());
		} else if (showingSkull && (isAtWildSafe || !isAtWild))
			removeIcon();
	}

	public void removeIcon() {
		if (showingSkull) {
			showingSkull = false;
			player.setCanPvp(false);
			player.getInterfaceManager().removeOverlay();
			player.setPvpCombatLevelThreshhold(-1);
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
		player.removeEffect(Effect.OVERLOAD_PVP_REDUCTION);
	}
	
	@Override
	public void onRemove() {
		player.removeEffect(Effect.OVERLOAD_PVP_REDUCTION);
	}

	public static final boolean isAtWild(Tile tile) {// TODO fix this
		return (tile.getX() >= 3011 && tile.getX() <= 3132 && tile.getY() >= 10052 && tile.getY() <= 10175)
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
