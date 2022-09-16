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
package com.rs.game.content.minigames.creations;

import com.rs.game.content.Effect;
import com.rs.game.content.combat.AttackStyle;
import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class StealingCreationController extends Controller {

	private transient StealingCreationGameController game;
	private boolean team;

	public StealingCreationController(StealingCreationGameController game, boolean team) {
		this.game = game;
		this.team = team;
	}

	@Override
	public void start() {
		sendInterfaces();
	}

	@Override
	public boolean logout() {
		player.getTile().setLocation(Helper.EXIT);
		Helper.reset(player);
		return true;
	}

	@Override
	public boolean login() {
		getPlayer().sendMessage("How did you manage to remain here eh? REPORTED!!");
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendOverlay(809, false);
		player.getPackets().sendVarc(558, (int) ((game.getEndTime() - System.currentTimeMillis()) / 600)); // sync
		player.getVars().setVarBit(5493, getTeam() ? 2 : 1);
	}

	public void sendScore(Score score) {
		player.getVars().setVar(1332, score.getGathering());
		player.getVars().setVar(1333, score.getDepositing());
		player.getVars().setVar(1334, score.getProcessing());
		player.getVars().setVar(1335, score.getWithdrawing());
		player.getVars().setVar(1337, score.getDamaging());
	}

	@Override
	public void moved() {
		GameArea area = game.getArea();
		if (area.getFlags() != null) {
			int flagX = player.getChunkX() - (area.getMinX() >> 3);
			int flagY = player.getChunkY() - (area.getMinY() >> 3);
			if (flagX >= 8 || flagY >= 8 || flagX < 0 || flagY < 0)
				return;
			if (area.getType(flagX, flagY) == 5) {
				if (Helper.withinArea2(player, area, flagX, flagY, new int[] { 1, 1, 4, 4 })) {
					if (!player.getAppearance().isNPC()) {
						player.getAppearance().transformIntoNPC(1957);
						player.getAppearance().setHidden(true);
						player.setRunHidden(false);
						player.getNextHits().clear();
					}
				} else
					resetFOG();
			} else
				resetFOG();
		}
	}

	private void resetFOG() {
		if (player.getAppearance().isNPC()) {
			player.getAppearance().transformIntoNPC(-1);
			player.getAppearance().setHidden(false);
			if (!player.getRun())
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						player.setRunHidden(true);
					}
				});
		}
	}

	@Override
	public void process() {
		if (game == null || (getPlayer().getX() < game.getArea().getMinX() || getPlayer().getX() > game.getArea().getMaxX() || player.getY() < game.getArea().getMinY() || player.getY() > game.getArea().getMaxY())) {
			getPlayer().sendMessage("An error has occured, please submit bug report.");
			player.getControllerManager().forceStop();
			return;
		}
	}

	@Override
	public boolean canEquip(int slot, int item) {
		if (slot == Equipment.CAPE) {
			player.sendMessage("You can't remove your team's colours.");
			return false;
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		if (target instanceof Player other) {
			if (other.getEquipment().getCapeId() == player.getEquipment().getCapeId() || Helper.withinSafeArea(other, game.getArea(), !getTeam()))
				return false;
		} else if (target instanceof Familiar familiar) {
			Player owner = familiar.getOwner();
			if (owner.getEquipment().getCapeId() == player.getEquipment().getCapeId())
				return false;
		}
		return true;
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof Player other) {
			if (other.getEquipment().getCapeId() == player.getEquipment().getCapeId()) {
				player.sendMessage("You cannot attack player's on the same team!");
				return false;
			}
			if (target.getTempAttribs().getL("in_kiln") >= System.currentTimeMillis()) {
				player.sendMessage("The power of the creation kiln is protecting the player.");
				return false;
			}
		} else if (target instanceof Familiar familiar) {
			Player owner = familiar.getOwner();
			if (owner.getEquipment().getCapeId() == player.getEquipment().getCapeId()) {
				player.sendMessage("You cannot attack a familiar on the same side as you!");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean processItemOnPlayer(Player target, Item item, int slot) {
		if (player.withinDistance(target.getTile(), 3)) {
			if (target.isDead() || player.isDead())
				return false;
			if (target.getEquipment().getCapeId() != player.getEquipment().getCapeId()) {
				player.sendMessage("You cannot give an item to a player on the opposite team!");
				return false;
				//			} else if (!target.isAcceptingAid()) {
				//				player.sendMessage("That player currently does not want your aid.");
				//				return false;
			}
			if (target.getInventory().addItem(item)) {
				player.getInventory().deleteItem(item);
				target.sendMessage(Utils.formatPlayerNameForDisplay(player.getDisplayName()) + " has given you an item.");
				return false;
			} else {
				player.sendMessage(Utils.formatPlayerNameForDisplay(player.getDisplayName()) + " has insufficient room in their inventory.");
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean processItemOnObject(GameObject object, Item item) {
		if (object.getId() == 39533) {
			game.sendItemToBase(player, item, getTeam(), false, false);
			return true;
		}
		return true;
	}

	@Override
	public boolean processNPCClick1(NPC n) {
		for (int element : Helper.MANAGER_NPCS)
			if (n.getId() == element) {
				n.setNextFaceEntity(player);
				player.startConversation(new StealingCreationManagerD(player, getGame(), n));
				return false;
			}
		return true;
	}

	@Override
	public boolean processNPCClick2(NPC n) {
		for (int element : Helper.MANAGER_NPCS)
			if (n.getId() == element) {
				n.setNextFaceEntity(player);
				Helper.displayClayStatus(game.getArea(), player);
				return false;
			}
		return true;
	}

	@Override
	public boolean canPlayerOption2(Player target) {
		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile tile) {
		player.simpleDialogue("You can't leave just like that!");
		return false;
	}

	@Override
	public boolean canPlayerOption4(Player target) {
		if (target.getEquipment().getCapeId() != player.getEquipment().getCapeId()) {
			player.sendMessage("You cannot give an item to a player on the opposite team!");
			return false;
		}
		//		else if (!target.isAcceptingAid()) {
		//			player.sendMessage("That player currently does not want your aid.");
		//			return false;
		//		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		player.getControllerManager().forceStop();
	}

	@Override
	public boolean canPlayerOption3(final Player target) {
		final int thievingLevel = player.getSkills().getLevel(Constants.THIEVING);
		if ((player.getTempAttribs().getL("PICKPOCK_DELAY") + 1500 > System.currentTimeMillis()) || Helper.withinSafeArea(target, game.getArea(), !getTeam()) || Helper.withinSafeArea(player, game.getArea(), getTeam()))
			return false;
		if (player.getAttackedBy() != null && player.inCombat()) {
			player.sendMessage("You can't do this while you're under combat.");
			return false;
		} else if (target.getEquipment().getCapeId() == player.getEquipment().getCapeId()) {
			player.sendMessage("You cannot pickpocket player that is on the same team!");
			return false;
		} else if (target.getTempAttribs().getL("in_kiln") >= System.currentTimeMillis()) {
			player.sendMessage("The power of the creation kiln is protecting the player.");
			return false;
		} else if (target.getSkills().getLevel(Constants.THIEVING) - thievingLevel >= 20) {
			player.sendMessage("You need a theiving level of at least " + (target.getSkills().getLevel(Constants.THIEVING) - 20) + " to pickpocket " + Utils.formatPlayerNameForDisplay(target.getDisplayName() + "."));
			return false;
		} else if (target.getInventory().getFreeSlots() == 28) {
			player.sendMessage(Utils.formatPlayerNameForDisplay(target.getDisplayName() + " appears to have nothing in his pockets."));
			return false;
		} else if (player.getInventory().getFreeSlots() == 0) {
			player.sendMessage("You don't have enough space in your inventory to steal from your target.");
			return false;
		} else if (target.isDead()) {
			player.sendMessage("Too late.");
			return false;
		}
		player.setRouteEvent(new RouteEvent(target, () -> {
			player.setNextFaceEntity(target);
			player.setNextAnimation(new Animation(881));
			player.sendMessage("You attempt to pickpocket from " + Utils.formatPlayerNameForDisplay(target.getDisplayName()) + "'s pockets.");
			player.sendMessage("You pick " + Utils.formatPlayerNameForDisplay(target.getDisplayName()) + "'s pocket.");
			player.getTempAttribs().setL("PICKPOCK_DELAY", System.currentTimeMillis());
			int level = Utils.getRandomInclusive(thievingLevel);
			double ratio = level / (Utils.random(target.getSkills().getLevel(Constants.THIEVING)) + 6);
			if (!(Math.round(ratio * thievingLevel) > target.getSkills().getLevel(Constants.THIEVING)))
				player.sendMessage("You fail to pickpocket " + Utils.formatPlayerNameForDisplay(target.getDisplayName()) + ".");
			else {
				Item caughtItem = getCalculatedItem(target);
				itemLoop: for (int i = 0; i < 100; i++) {
					if (caughtItem != null) {
						if (player.getInventory().addItem(caughtItem))
							target.getInventory().deleteItem(caughtItem);
						break itemLoop;
					}
					caughtItem = getCalculatedItem(target);
				}
				player.sendMessage("You sucessfully pickpocket an item from " + Utils.formatPlayerNameForDisplay(target.getDisplayName()) + "'s pockets!");
			}
		}));
		return false;
	}

	public Item getCalculatedItem(Player target) {
		return target.getInventory().getItem(Utils.random(target.getInventory().getItemsContainerSize()));
	}

	@Override
	public boolean keepCombating(Entity target) {
		if (target instanceof Player other) {
			if (other.getAppearance().isNPC()) {
				player.sendMessage("Your target is nowhere to be found.");
				return false;
			}
			if (Helper.withinSafeArea(other, game.getArea(), !getTeam()) || Helper.withinSafeArea(player, game.getArea(), getTeam()))
				return false;
		}
		if (player.getAppearance().isNPC()) {
			player.sendMessage("You cannot attack while you are hidden.");
			return false;
		}
		return true;
	}

	@Override
	public void trackXP(int skillId, int addedXp) {
		if (skillId == 3) {
			Score score = game.getScore(player);
			if (score == null)
				return;
			score.updateDamaging((int) (addedXp * 7.2148148148148148148148148148148));
			sendScore(score);
		}
	}

	@Override
	public boolean canTakeItem(GroundItem item) {
		Score score = game.getScore(player);
		String name = item.getName().toLowerCase();
		int nameIndex = name.indexOf("(class");
		int clayQuality = 1;
		if (nameIndex != -1)
			clayQuality = name.contains("potion") ? 1 : Integer.parseInt(name.substring(nameIndex).replace("(class ", "").replace(")", ""));
		int pointsSubtracted = 2 * (((item.getDefinitions().isStackable() ? 1 : 15) * clayQuality) * item.getAmount());
		if (score.getWithdrawing() - pointsSubtracted <= -3000) {
			player.sendMessage("You cannot take this amount of items as your score is too low.");
			return false;
		}
		if (!item.isPrivate()) {
			score.updateWithdrawing(pointsSubtracted);
			sendScore(score);
		}
		return true;
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		if (interfaceId == 813) {
			if (componentId >= 37 && componentId <= 71)
				processKilnExchange(componentId, packet);
			else if (componentId >= 99 && componentId <= 107) {
				int index = (componentId - 99) / 2;
				if (player.getInventory().containsItem(Helper.SACRED_CLAY[index], 1)) {
					player.getTempAttribs().setI("sc_kiln_quality", index);
					Helper.refreshKiln(player);
				}
			}
			return true;
		}
		if (interfaceId == 387) {
			if (packet == ClientPacket.IF_OP1 && componentId == 9) {
				player.sendMessage("You can't remove your team's colours.");
				return false;
			}
		} else if (interfaceId == Inventory.INVENTORY_INTERFACE) {
			Item item = player.getInventory().getItem(slotId);
			if (item != null) {
				String itemName = item.getName().toLowerCase();
				if (itemName.contains("food (class")) {
					doFoodEffect(item, Integer.parseInt(item.getName().substring(item.getName().indexOf("(class")).replace("(class ", "").replace(")", "")));
					return false;
				}
				if (itemName.contains("potion (") || itemName.contains("super")) {
					boolean superPotion = itemName.contains("super");
					int index = 0;
					for (String name : Constants.SKILL_NAME) {
						int doses = Integer.parseInt(itemName.substring(itemName.indexOf("(")).replace("(", "").replace(")", ""));
						String skill = superPotion ? item.getName().toLowerCase().replace("super ", "").replace(" (" + doses + ")", "") : item.getName().toLowerCase().replace(" potion (" + doses + ")", "");
						if (!name.toLowerCase().equals(skill)) {
							index++;
							continue;
						}
						player.sendMessage("You drink a dose of the " + item.getName().toLowerCase().replace("(" + doses + ")", "") + ".");
						if (doses == 1) {
							player.getInventory().deleteItem(item);
							player.sendMessage("The glass shatters as you drink the last dose of the potion.");
						} else {
							player.getInventory().getItems().set(slotId, new Item(item.getId() + 2, 1));
							player.getInventory().refresh(slotId);
						}
						int actualLevel = player.getSkills().getLevel(index);
						int realLevel = player.getSkills().getLevelForXp(index);
						if (!skill.equals("prayer")) {
							int level = actualLevel > realLevel ? realLevel : actualLevel;
							player.getSkills().set(index, level + (superPotion ? 7 : 4));
						} else
							player.getPrayer().restorePrayer((int) (Math.floor(player.getSkills().getLevelForXp(Constants.PRAYER) * .5 + (superPotion ? 250 : 200))));
						player.setNextAnimation(new Animation(829));
						player.soundEffect(4580);
					}
					return false;
				}
			}
		}
		return true;
	}

	private void doFoodEffect(Item item, int itemTier) {
		if (!player.canEat())
			return;
		player.setNextAnimation(new Animation(829));
		player.addFoodDelay(1800);
		player.sendMessage("You eat the food.");
		int hp = player.getHitpoints();
		player.heal(40 * itemTier);
		if (player.getHitpoints() > hp)
			player.sendMessage("It heals some health.");
		player.getActionManager().setActionDelay(player.getActionManager().getActionDelay() + 3);
		player.getInventory().deleteItem(item);
	}

	@Override
	public boolean processObjectClick1(final GameObject object) {
		final GameArea area = game.getArea();
		final int flagX = player.getChunkX() - (area.getMinX() >> 3);
		final int flagY = player.getChunkY() - (area.getMinY() >> 3);

		boolean isEnemySCGate = false;
		boolean isEnemySCWall = false;

		gateLoop: for (int[] wallIDS : (getTeam() ? Helper.BLUE_BARRIER_GATES : Helper.RED_BARRIER_GATES))
			for (int id : wallIDS)
				if (object.getId() == id) {
					isEnemySCGate = true;
					break gateLoop;
				}
		wallLoop: for (int[] wallIDS : (getTeam() ? Helper.BLUE_BARRIER_WALLS : Helper.RED_BARRIER_WALLS))
			for (int id : wallIDS)
				if (object.getId() == id) {
					isEnemySCWall = true;
					break wallLoop;
				}

		if (object.getId() == Helper.KILN) {
			Helper.displayKiln(player);
			return false;
		}
		if (object.getId() == 39533) {
			for (Item item : player.getInventory().getItems().array()) {
				if (item == null)
					continue;
				game.sendItemToBase(player, item, getTeam(), false, true);
			}
			return false;
		}
		if ((!getTeam() && (object.getId() == Helper.BLUE_DOOR_1 || object.getId() == Helper.BLUE_DOOR_2)) || (getTeam() && (object.getId() == Helper.RED_DOOR_1 || object.getId() == Helper.RED_DOOR_2))) {
			passWall(player, object, getTeam());
			return false;
		} else if (isEnemySCGate || isEnemySCWall) {
			final int x = object.getChunkX() - (game.getArea().getMinX() >> 3);
			final int y = object.getChunkY() - (game.getArea().getMinY() >> 3);
			final int weaponId = player.getEquipment().getWeaponId();
			AttackStyle attackStyle = player.getCombatDefinitions().getAttackStyle();
			final int combatDelay = PlayerCombat.getMeleeCombatDelay(player, weaponId);
			if (player.getActionManager().getAction() != null || player.getActionManager().getActionDelay() > 0)
				return false;
			player.getActionManager().addActionDelay(combatDelay);
			player.setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(weaponId, attackStyle)));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					game.damageBarrier(x, y);
				}
			});
			return false;
		} else if (object.getId() == Helper.PRAYER_ALTAR) {
			boolean runEnergy = Utils.getRandomInclusive(1) == 0;
			if (runEnergy)
				player.setRunEnergy(100);
			return !runEnergy;
		} else if (object.getId() >= 39534 && object.getId() <= 39545) {
			player.getTempAttribs().setO("sc_object", object);
			if (object.getId() == 39541)
				player.startConversation(new StealingCreationMagic(player));
			else if (object.getId() == 39539)
				player.startConversation(new StealingCreationRange(player));
			else if (object.getId() == 39534)
				player.startConversation(new StealingCreationClay(player));
			return false;
		} else if (object.getId() == 39602 || object.getId() == 39613 || object.getId() == 39612 || object.getId() == 39611) {
			boolean isWall = object.getId() == 39613 || object.getId() == 39612 || object.getId() == 39611;
			if (isWall)
				if (player.getSkills().getLevel(Constants.AGILITY) < 60) {
					player.sendMessage("You need to have an Agility level of 60 to clim over the wall.");
					return false;
				}
			int rotation = object.getRotation();
			int xExtra = 0, yExtra = 0, totalDistance = isWall ? 2 : 3;
			Direction direction = Direction.NORTH;
			int DX = object.getX() - player.getX();
			int DY = object.getY() - player.getY();
			if (!isWall && (rotation == 1 || rotation == 3) || isWall && (rotation == 0 || rotation == 2)) {
				if (DX >= 0) {
					xExtra += totalDistance;
					direction = Direction.EAST;
				} else if (DX < 0) {
					xExtra -= totalDistance;
					direction = Direction.WEST;
				}
			} else if (DY >= 0) {
				yExtra += totalDistance;
				direction = Direction.NORTH;
			} else if (DY < 0) {
				yExtra -= totalDistance;
				direction = Direction.SOUTH;
			}
			final WorldTile toTile = new WorldTile(player.getX() + xExtra, player.getY() + yExtra, player.getPlane());
			ForceMovement nextForceMovement;
			if (isWall)
				nextForceMovement = new ForceMovement(toTile, 2, direction);
			else
				nextForceMovement = new ForceMovement(player.getTile(), 0, toTile, 2, direction);
			player.setNextForceMovement(nextForceMovement);
			player.setNextAnimation(new Animation(object.getId() == 39602 ? 6132 : 10590));
			final Direction finalDirection = direction;
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					player.setFaceAngle(WorldUtil.getAngleTo(finalDirection));
					player.setNextWorldTile(toTile);
				}
			}, 1);
		} else {
			final String name = object.getDefinitions().getName().toLowerCase();
			final int clayQuality = Integer.parseInt(name.substring(name.indexOf("(class")).replace("(class ", "").replace(")", "")) - 1;
			final CreationChunks skill = CreationChunks.valueOf(name.replace(" (class " + (clayQuality + 1) + ")", "").toUpperCase());

			player.getActionManager().setAction(new PlayerAction() {

				Item bestItem;
				int itemTier;

				@Override
				public boolean start(Player player) {
					if (name.contains(("empty")))
						return false;
					else if (player.getSkills().getLevel(skill.getRequestedSkill()) < clayQuality * 20) {
						player.sendMessage("You need a " + Constants.SKILL_NAME[skill.getRequestedSkill()] + " level of " + clayQuality * 20 + " to collect this level of clay.");
						return false;
					}
					itemLoop: for (int index = 4; index >= 0; index--) {
						int baseItem = skill.getBaseItem();
						if (baseItem == -1)
							break itemLoop;
						Item bestItem = new Item(baseItem + (index * 2), 1);
						if (player.getEquipment().getWeaponId() == bestItem.getId() || player.getInventory().containsItem(bestItem.getId(), bestItem.getAmount()))
							if (player.getSkills().getLevel(skill.getRequestedSkill()) >= index * 20) {
								this.bestItem = bestItem;
								itemTier = index;
								break itemLoop;
							}
					}
					setActionDelay(player, getActionDelay());
					return true;
				}

				@Override
				public boolean process(Player player) {
					if (game.isEmpty(flagX, flagY) || player.getInventory().getFreeSlots() == 0)
						return false;
					player.setNextAnimation(bestItem != null ? new Animation(skill.getBaseAnimation() + itemTier) : new Animation(10602));
					player.setNextFaceWorldTile(object);
					// player.getInventory().addItem(new Item(Helper.SACRED_CLAY[clayQuality], 1));

					return true;
				}

				@Override
				public int processWithDelay(Player player) {
					Score score = game.getScore(player);
					// if (score == null)
					// return -1;
					if (Utils.getRandomInclusive(clayQuality + 1) == 0)
						game.useSkillPlot(flagX, flagY);
					player.getInventory().addItem(new Item(Helper.SACRED_CLAY[clayQuality], 1));
					if (score != null) {
						score.updateGathering(15 * (clayQuality + 1));
						sendScore(score);
					}
					return getActionDelay();
				}

				private int getActionDelay() {
					if (clayQuality == 0)
						return 2;
					int baseTime = Helper.OBJECT_TIERS[clayQuality];
					int mineTimer = baseTime - player.getSkills().getLevel(skill.getRequestedSkill()) - (bestItem == null ? 1 : Helper.TOOL_TIERS[itemTier]);
					if (mineTimer < 2)
						mineTimer = 2;
					return mineTimer;
				}

				@Override
				public void stop(Player player) {
					setActionDelay(player, 3);
				}
			});
		}
		return false;
	}

	enum CreationChunks {

		FRAGMENTS(10602, -1, Constants.HUNTER),

		TREE(10603, 14132, Constants.WOODCUTTING),

		ROCK(10608, 14122, Constants.MINING),

		POOL(10613, 14142, Constants.FISHING),

		SWARM(10618, 14152, Constants.HUNTER);

		private int baseAnimation, baseItem, skillRequested;

		private CreationChunks(int baseAnimation, int baseItem, int skillRequested) {
			this.baseAnimation = baseAnimation;
			this.baseItem = baseItem;
			this.skillRequested = skillRequested;
		}

		public int getBaseAnimation() {
			return baseAnimation;
		}

		public int getBaseItem() {
			return baseItem;
		}

		public int getRequestedSkill() {
			return skillRequested;
		}
	}

	@Override
	public boolean processObjectClick2(GameObject object) {
		boolean isFriendlySCGate = false;

		gateLoop: for (int[] gateIDS : (getTeam() ? Helper.RED_BARRIER_GATES : Helper.BLUE_BARRIER_GATES))
			for (int id : gateIDS)
				if (object.getId() == id) {
					isFriendlySCGate = true;
					break gateLoop;
				}

		if (object.getId() == Helper.EMPTY_BARRIER1 || object.getId() == Helper.EMPTY_BARRIER2 || object.getId() == Helper.EMPTY_BARRIER3) {
			boolean redTeam = getTeam();
			int tier = -1;
			for (int i = 4; i >= 0; i--)
				if (player.getInventory().containsItem(Helper.BARRIER_ITEMS[i], 4)) {
					tier = i;
					break;
				}
			if (tier == -1) {
				player.sendMessage("You don't have enough barrier items to build.");
				return false;
			}
			final int t = tier;
			final int x = object.getChunkX() - (game.getArea().getMinX() >> 3);
			final int y = object.getChunkY() - (game.getArea().getMinY() >> 3);
			for (Player otherPlayer : redTeam ? game.getBlueTeam() : game.getRedTeam()) {
				if (otherPlayer == null || !otherPlayer.withinDistance(object, 6))
					continue;
				if (Helper.withinArea(otherPlayer, game.getArea(), x, y, new int[] { 2, 2 })) {
					player.sendMessage("You cannot build a barrier while players from the other team are near the pallet.");
					return false;
				}
				otherPlayer.resetWalkSteps();
				otherPlayer.lock(3);
			}
			player.lock(2);
			WorldTasks.schedule(new WorldTask() {
				private int step = 0;

				@Override
				public void run() {
					if (step == 0) {
						player.setNextAnimation(new Animation(10589));
						step++;
					} else if (step == 1) {
						if (player.getInventory().containsItem(new Item(Helper.BARRIER_ITEMS[t], 4)) && !game.buildBarrier(getTeam(), t + 1, x, y)) {
							player.getInventory().removeItems(new Item(Helper.BARRIER_ITEMS[t], 4));
							player.getInventory().addItem(new Item(Helper.BARRIER_ITEMS[t], 4));
						}
						player.unlock();
						stop();
					}
				}

			}, 0, 0);
			return false;
		}
		if (isFriendlySCGate) {
			passWall(player, object, getTeam());
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick3(GameObject object) {
		boolean isFriendlySCGate = false;
		boolean isFriendlySCWall = false;

		gateLoop: for (int[] gateIDS : (getTeam() ? Helper.RED_BARRIER_GATES : Helper.BLUE_BARRIER_GATES))
			for (int id : gateIDS)
				if (object.getId() == id) {
					isFriendlySCGate = true;
					break gateLoop;
				}
		wallLoop: for (int[] gateIDS : (getTeam() ? Helper.RED_BARRIER_WALLS : Helper.BLUE_BARRIER_WALLS))
			for (int id : gateIDS)
				if (object.getId() == id) {
					isFriendlySCWall = true;
					break wallLoop;
				}

		if (isFriendlySCGate || isFriendlySCWall) {
			final int x = object.getChunkX() - (game.getArea().getMinX() >> 3);
			final int y = object.getChunkY() - (game.getArea().getMinY() >> 3);
			synchronized (game.getLock()) {
				final int team = game.getArea().getWallTeam(x, y);
				final int tier = game.getArea().getWallTier(x, y);
				final int health = game.getArea().getWallStatus(x, y);
				if (team != (getTeam() ? 2 : 1))
					return false;
				if (health <= 0 || health >= (tier * 4)) {
					player.sendMessage("This barrier doesn't need any repairing.");
					return false;
				}
				if (!player.getInventory().containsItem(Helper.BARRIER_ITEMS[tier - 1], 1)) {
					player.sendMessage("You don't have enough barriers of required type to repair this barrier.");
					return false;
				}
				player.lock(2);
				WorldTasks.schedule(new WorldTask() {
					private int step = 0;

					@Override
					public void run() {
						if (step == 0) {
							player.setNextAnimation(new Animation(10589));
							step++;
						} else if (step == 1) {
							if (player.getInventory().containsItem(new Item(Helper.BARRIER_ITEMS[tier - 1], 1)) && !game.repairBarrier(x, y)) {
								player.getInventory().removeItems(new Item(Helper.BARRIER_ITEMS[tier - 1], 1));
								player.getInventory().addItem(new Item(Helper.BARRIER_ITEMS[tier - 1], 1));
							}
							player.unlock();
							stop();
						}
					}

				}, 0, 0);
			}

			return false;
		}
		return true;
	}

	public static void passWall(Player player, GameObject object, final boolean red) {
		if (player.hasEffect(Effect.FREEZE)) {
			player.sendMessage("A mysterious force prevents you from moving.");
			return;
		}
		player.lock(3);
		if (!Helper.setWalkToGate(object, player))
			player.unlock();
		final Player p = player;
		final GameObject o = object;
		WorldTasks.schedule(new WorldTask() {
			private int step = 0;

			@Override
			public void run() {
				if (step == 0 && !Helper.isAtGate(o, p)) {
					if (!p.hasWalkSteps() && p.getNextWalkDirection() == null) {
						// unstuck
						stop();
						p.unlock();
					}
					return;
				}
				if (step == 0) {
					WorldTile fromTile = new WorldTile(p.getX(), p.getY(), p.getPlane());
					WorldTile faceTile = Helper.getFaceTile(o, p);
					p.sendMessage("You pass through the barrier.");
					p.setNextWorldTile(faceTile);
					p.setNextForceMovement(new ForceMovement(fromTile, 0, faceTile, 1, Helper.getFaceDirection(faceTile, p)));
					p.setNextAnimation(new Animation(10584));
					p.setNextSpotAnim(new SpotAnim(red ? 1871 : 1870));
					step++;
				} else if (step == 1) {
					stop();
					p.unlock();
				}
			}
		}, 0, 0);
	}

	@Override
	public boolean sendDeath() {
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					player.setNextAnimation(new Animation(836));
				else if (loop == 1) {
					if (player.getFamiliar() != null)
						player.getFamiliar().sendDeath(player);
				} else if (loop == 3) {
					Score score = game.getScore(player);
					Player killer = player.getMostDamageReceivedSourcePlayer();
					if (killer != null) {
						Score killerScore = game.getScore(killer);
						if (killerScore != null)
							killerScore.updateKilled(1);
						killer.removeDamage(player);
						if (killerScore != null)
							killer.sendMessage("You have killed " + player.getDisplayName() + ", you now have " + killerScore.getKilled() + " kills.");
						player.sendMessage("You have been killed by " + killer.getDisplayName());
					}
					player.getEquipment().deleteSlot(Equipment.CAPE);
					player.sendItemsOnDeath(killer, true);
					player.setNextWorldTile(Helper.getNearestRespawnPoint(player, game.getArea(), getTeam()));
					player.stopAll();
					player.reset();
					if (score != null) {
						score.updateDied(1);
						sendScore(score);
					}
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					Helper.giveCape(player, getTeam());
					player.jingle(90);
					player.resetWalkSteps();
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	public void processKilnExchange(int componentId, ClientPacket packet) {
		int quality = player.getTempAttribs().getI("sc_kiln_quality", 0);
		int clayId = Helper.SACRED_CLAY[quality];
		int amount = 0;
		if (packet == ClientPacket.IF_OP1)
			amount = 1;
		else if (packet == ClientPacket.IF_OP2)
			amount = 5;
		else if (packet == ClientPacket.IF_OP3) {
			player.getTempAttribs().setI("sc_component", componentId);
			player.getTempAttribs().setB("kilnX", true);
			player.getPackets().sendRunScriptReverse(108, "Enter Amount:");
		} else if (packet == ClientPacket.IF_OP4)
			amount = player.getInventory().getAmountOf(clayId);
		else
			amount = player.getTempAttribs().getI("sc_amount_making");
		if (Helper.checkSkillRequriments(player, Helper.getRequestedKilnSkill(componentId - 37), quality))
			if ((amount != 0 && Helper.proccessKilnItems(player, componentId, quality, clayId, amount))) {
				Score score = game.getScore(player);
				if (score == null)
					return;
				score.updateProcessing(15 * quality);
				sendScore(score);
				return;
			}
	}

	@Override
	public void forceClose() {
		if (game != null)
			game.remove(player);
		else
			Helper.sendHome(player);
	}

	public StealingCreationGameController getGame() {
		return game;
	}

	public boolean getTeam() {
		return team;
	}
}
