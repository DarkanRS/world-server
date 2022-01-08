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

import java.util.List;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.others.CastleWarBarricade;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Equipment;
import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.game.player.content.minigames.CastleWars;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;

public class CastleWarsPlayingController extends Controller {

	private int team;

	public CastleWarsPlayingController(int team) {
		this.team = team;
	}

	@Override
	public void start() {
		sendInterfaces();
	}

	@Override
	public boolean canMove(Direction dir) {
		WorldTile toTile = new WorldTile(player.getX() + dir.getDx(), player.getY() + dir.getDy(), player.getPlane());
		return !CastleWars.isBarricadeAt(toTile);
	}

	@Override
	public boolean processNPCClick2(NPC n) {
		if (n.getId() == 1532 && n instanceof CastleWarBarricade barricade) {
			if (!player.getInventory().containsItem(590, 1)) {
				player.sendMessage("You do not have the required items to light this.");
				return false;
			}
			barricade.litFire();
			return false;
		}
		return true;
	}

	/*
	 * return process normaly
	 */
	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		if (interfaceId == 387) {
			if (componentId == 9 || componentId == 6) {
				player.sendMessage("You can't remove your team's colours.");
				return false;
			}
			if (componentId == 15) {
				int weaponId = player.getEquipment().getWeaponId();
				if (weaponId == 4037 || weaponId == 4039) {
					player.sendMessage("You can't remove enemy's flag.");
					return false;
				}
			}
		} else if (interfaceId == Inventory.INVENTORY_INTERFACE) {
			Item item = player.getInventory().getItem(slotId);
			if (item != null) {
				if (item.getId() == 4053) {
					if (player.getX() == 2422 && player.getY() == 3076 || player.getX() == 2426 && player.getY() == 3080 || player.getX() == 2423 && player.getY() == 3076 || player.getX() == 2426 && player.getY() == 3081 || player.getX() == 2373
							&& player.getY() == 3127 || player.getX() == 2373 && player.getY() == 3126 || player.getX() == 2376 && player.getY() == 3131 || player.getX() == 2377 && player.getY() == 3131 || !CastleWars.isBarricadeAt(player)) {
						player.sendMessage("You cannot place a barracade here!");
						return false;
					}
					CastleWars.addBarricade(team, player);
					return false;
				}
				if (item.getId() == 4049 || item.getId() == 4050 || item.getId() == 12853 || item.getId() == 14640 || item.getId() == 14648) {
					doBandageEffect(item);
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean canDropItem(Item item) {
		if (item.getDefinitions().getName().toLowerCase().contains("flag")) {
			player.sendMessage("You cannot just drop the flag!");
			return false;
		}
		return true;
	}

	private void doBandageEffect(Item item) {
		int gloves = player.getEquipment().getGlovesId();
		player.heal((int) (player.getMaxHitpoints() * (gloves >= 11079 && gloves <= 11084 ? 0.15 : 0.10)));
		int restoredEnergy = (int) (player.getRunEnergy() * 1.3);
		player.setRunEnergy(restoredEnergy > 100 ? 100 : restoredEnergy);
		player.getInventory().deleteItem(item);
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		if (slotId == Equipment.CAPE || slotId == Equipment.HEAD) {
			player.sendMessage("You can't remove your team's colours.");
			return false;
		}
		if (slotId == Equipment.WEAPON || slotId == Equipment.SHIELD) {
			int weaponId = player.getEquipment().getWeaponId();
			if (weaponId == 4037 || weaponId == 4039) {
				player.sendMessage("You can't remove enemy's flag.");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof Player) {
			if (canHit(target))
				return true;
			player.sendMessage("You can't attack your team.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processItemOnNPC(NPC npc, Item item) {
		if (npc.getId() == 1532 && npc instanceof CastleWarBarricade barricade) {
			if (item.getId() == 590) {
				barricade.litFire();
				return false;
			}
			if (item.getId() == 4045) {
				player.getInventory().deleteItem(item);
				barricade.explode();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		if (target instanceof NPC)
			return true;
		Player p2 = (Player) target;
		if (p2.getEquipment().getCapeId() == player.getEquipment().getCapeId())
			return false;
		return true;
	}

	// You can't leave just like that!

	public void leave() {
		player.getInterfaceManager().removeOverlay();
		CastleWars.removePlayingPlayer(player, team);
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().setOverlay(58);
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
					int weaponId = player.getEquipment().getWeaponId();
					if (weaponId == 4037 || weaponId == 4039) {
						CastleWars.setWeapon(player, null);
						CastleWars.dropFlag(player, weaponId == 4037 ? CastleWars.SARADOMIN : CastleWars.ZAMORAK);
					} else {
						Player killer = player.getMostDamageReceivedSourcePlayer();
						if (killer != null)
							killer.increaseKillCount(player);
					}

					player.reset();
					player.setNextWorldTile(new WorldTile(team == CastleWars.ZAMORAK ? CastleWars.ZAMO_BASE : CastleWars.SARA_BASE, 1));
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean logout() {
		player.setLocation(new WorldTile(CastleWars.LOBBY, 2));
		return true;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager().execute(new SimpleMessage(), "You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager().execute(new SimpleMessage(), "You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getDialogueManager().execute(new SimpleMessage(), "You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		int id = object.getId();
		if (id == 4406 || id == 4407) {
			removeController();
			leave();
			return false;
		}
		if ((id == 4469 && team == CastleWars.SARADOMIN) || (id == 4470 && team == CastleWars.ZAMORAK)) {
			passBarrier(object);
			return false;
		} else if (id == 4377 || id == 4378) { // no flag anymore
			if (id == 4377 && team == CastleWars.SARADOMIN) {
				if (player.getEquipment().getWeaponId() == 4039) {
					CastleWars.addScore(player, team, CastleWars.ZAMORAK);
					return false;
				}
			} else if (id == 4378 && team == CastleWars.ZAMORAK)
				if (player.getEquipment().getWeaponId() == 4037) {
					CastleWars.addScore(player, team, CastleWars.SARADOMIN);
					return false;
				}
			player.sendMessage("You need to bring a flag back here!");
			return false;
		} else if (id == 4902 || id == 4903) { // take flag
			if (id == 4902 && team == CastleWars.SARADOMIN) {
				if (player.getEquipment().getWeaponId() == 4039) {
					CastleWars.addScore(player, team, CastleWars.ZAMORAK);
					return false;
				}
				player.sendMessage("Saradomin won't let you take his flag!");
			} else if (id == 4903 && team == CastleWars.ZAMORAK) {
				if (player.getEquipment().getWeaponId() == 4037) {
					CastleWars.addScore(player, team, CastleWars.SARADOMIN);
					return false;
				}
				player.sendMessage("Zamorak won't let you take his flag!");
			} else
				// take flag
				CastleWars.takeFlag(player, team, id == 4902 ? CastleWars.SARADOMIN : CastleWars.ZAMORAK, object, false);
			return false;
		} else if (id == 4900 || id == 4901) { // take dropped flag
			CastleWars.takeFlag(player, team, id == 4900 ? CastleWars.SARADOMIN : CastleWars.ZAMORAK, object, true);
			return false;
		} else if (id == 36579 || id == 36586) {
			player.getInventory().addItem(new Item(4049));
			return false;
		} else if (id == 36575 || id == 36582) {
			player.getInventory().addItem(new Item(4053));
			return false;
		} else if (id == 36577 || id == 36584) {
			player.getInventory().addItem(new Item(4045));
			return false;
			// under earth from basess
		} else if (id == 4411) {// stepping stone
			if (object.getX() == player.getX() && object.getY() == player.getY())
				return false;
			player.lock(2);
			player.setNextAnimation(new Animation(741));
			player.addWalkSteps(object.getX(), object.getY(), -1, false);
		} else if (id == 36693) {
			player.useStairs(827, new WorldTile(2430, 9483, 0), 1, 2);
			return false;
		} else if (id == 36694) {
			player.useStairs(827, new WorldTile(2369, 9524, 0), 1, 2);
			return false;
		} else if (id == 36645) {
			player.useStairs(828, new WorldTile(2430, 3081, 0), 1, 2);
			return false;
		} else if (id == 36646) {
			player.useStairs(828, new WorldTile(2369, 3126, 0), 1, 2);
			return false;
		} else if (id == 4415) {
			if (object.getX() == 2417 && object.getY() == 3075 && object.getPlane() == 1)
				player.useStairs(-1, new WorldTile(2417, 3078, 0), 0, 1);
			else if (object.getX() == 2419 && object.getY() == 3080 && object.getPlane() == 1)
				player.useStairs(-1, new WorldTile(2419, 3077, 0), 0, 1);
			else if (object.getX() == 2430 && object.getY() == 3081 && object.getPlane() == 2)
				player.useStairs(-1, new WorldTile(2427, 3081, 1), 0, 1);
			else if (object.getX() == 2425 && object.getY() == 3074 && object.getPlane() == 3)
				player.useStairs(-1, new WorldTile(2425, 3077, 2), 0, 1);
			else if (object.getX() == 2380 && object.getY() == 3127 && object.getPlane() == 1)
				player.useStairs(-1, new WorldTile(2380, 3130, 0), 0, 1);
			else if (object.getX() == 2382 && object.getY() == 3132 && object.getPlane() == 1)
				player.useStairs(-1, new WorldTile(2382, 3129, 0), 0, 1);
			else if (object.getX() == 2369 && object.getY() == 3126 && object.getPlane() == 2)
				player.useStairs(-1, new WorldTile(2372, 3126, 1), 0, 1);
			else if (object.getX() == 2374 && object.getY() == 3133 && object.getPlane() == 3)
				player.useStairs(-1, new WorldTile(2374, 3130, 2), 0, 1);
			return false;
		} else if (id == 36481) {
			player.useStairs(-1, new WorldTile(2417, 3075, 0), 0, 1);
			return false;
		} else if (id == 36495 && object.getPlane() == 0) {
			player.useStairs(-1, new WorldTile(2420, 3080, 1), 0, 1);
			return false;
		} else if (id == 36480 && object.getPlane() == 1) {
			player.useStairs(-1, new WorldTile(2430, 3080, 2), 0, 1);
			return false;
		} else if (id == 36484 && object.getPlane() == 2) {
			player.useStairs(-1, new WorldTile(2426, 3074, 3), 0, 1);
			return false;
		} else if (id == 36532 && object.getPlane() == 0) {
			player.useStairs(-1, new WorldTile(2379, 3127, 1), 0, 1);
			return false;
		} else if (id == 36540) {
			player.useStairs(-1, new WorldTile(2383, 3132, 0), 0, 1);
			return false;
		} else if (id == 36521 && object.getPlane() == 1) {
			player.useStairs(-1, new WorldTile(2369, 3127, 2), 0, 1);
			return false;
		} else if (id == 36523 && object.getPlane() == 2) {
			player.useStairs(-1, new WorldTile(2373, 3133, 3), 0, 1);
			return false;
		} else if (id == 36644) {
			if (object.getY() == 9508)
				player.useStairs(828, new WorldTile(2400, 3106, 0), 1, 2);
			else if (object.getY() == 9499)
				player.useStairs(828, new WorldTile(2399, 3100, 0), 1, 2);
			player.freeze(0);
			return false;
		} else if (id == 36691) {
			if (object.getY() == 3099)
				player.useStairs(827, new WorldTile(2399, 9500, 0), 1, 2);
			else if (object.getY() == 3108)
				player.useStairs(827, new WorldTile(2400, 9507, 0), 1, 2);
			player.freeze(0);
			return false;
		}/*
		 * else if (id == 4438) player.getActionManager().setSkill(new
		 * Mining(object, RockDefinitions.SMALLER_ROCKS)); else if (id == 4437)
		 * player.getActionManager().setSkill(new Mining(object,
		 * RockDefinitions.ROCKS ));
		 */
		else if (id == 4448) {
			for (List<Player> players : CastleWars.getPlaying())
				for (Player player : players)
					if (player.withinDistance(object, 1))
						player.applyHit(new Hit(player, player.getHitpoints(), HitLook.TRUE_DAMAGE));
			World.spawnObject(new GameObject(4437, object.getType(), object.getRotation(), object.getX(), object.getY(), object.getPlane()));
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(GameObject object) {
		int id = object.getId();
		if (id == 36579 || id == 36586) {
			player.getInventory().addItem(new Item(4049, 5));
			return false;
		}
		if (id == 36575 || id == 36582) {
			player.getInventory().addItem(new Item(4053, 5));
			return false;
		} else if (id == 36577 || id == 36584) {
			player.getInventory().addItem(new Item(4045, 5));
			return false;
		}
		return true;
	}

	public void passBarrier(GameObject object) {
		if (object.getRotation() == 0 || object.getRotation() == 2) {
			if (player.getY() != object.getY())
				return;
			player.lock(2);
			player.addWalkSteps(object.getX() == player.getX() ? object.getX() + (object.getRotation() == 0 ? -1 : +1) : object.getX(), object.getY(), -1, false);
		} else if (object.getRotation() == 1 || object.getRotation() == 3) {
			if (player.getX() != object.getX())
				return;
			player.lock(2);
			player.addWalkSteps(object.getX(), object.getY() == player.getY() ? object.getY() + (object.getRotation() == 3 ? -1 : +1) : object.getY(), -1, false);
		}
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
		leave();
	}

	@Override
	public void forceClose() {
		leave();
	}
}
