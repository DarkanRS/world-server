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

import com.rs.cache.loaders.ObjectType;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.content.combat.AttackStyle;
import com.rs.game.player.content.combat.AttackType;
import com.rs.game.player.content.combat.XPType;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.statements.NPCStatement;
import com.rs.game.player.content.minigames.wguild.AnimatedArmor;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.player.dialogues.KamfreendaDefender;
import com.rs.game.player.dialogues.Shanomi;
import com.rs.game.player.dialogues.ShotputD;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.game.player.dialogues.SimpleNPCMessage;
import com.rs.game.player.managers.InterfaceManager.Tab;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;

public class WarriorsGuild extends Controller {

	public transient static WarriorTimer timer;
	public transient static int killedCyclopses;
	public transient static int amountOfPlayers;

	public static void init() {
		if (timer == null)
			WorldTasks.schedule(WarriorsGuild.timer = new WarriorTimer(), 1, 1);
	}

	public static class WarriorTimer extends WorldTask {
		private int ticks;
		private double lastDummy;
		public byte projectileType;

		@Override
		public void run() {
			ticks++;
			if (ticks % 14 == 0) {
				switchDummyAction();
				lastDummy += 0.000000001D;
				World.sendObjectAnimation(CATAPULT, new Animation(4164));
				projectileType = (byte) Utils.random(4);
				World.sendProjectile(CATAPULT_PROJECTILE_BASE, CATAPULT_TARGET, 679 + projectileType, 85, 15, 15, 0.2, 15, 0);
			}
		}

		private void switchDummyAction() {
			int index = Utils.random(DUMMY_LOCATIONS.length);
			World.spawnObjectTemporary(new GameObject(Utils.random(15624, 15630), ObjectType.SCENERY_INTERACT, DUMMY_ROTATIONS[index], DUMMY_LOCATIONS[index]), 10);
		}
	}

	public static final int[] ARMOR_POINTS = { 5, 10, 15, 20, 50, 60, 80 };
	public static final int[][] ARMOUR_SETS = { { 1155, 1117, 1075 }, { 1153, 1115, 1067 }, { 1157, 1119, 1069 }, { 1165, 1125, 1077 }, { 1159, 1121, 1071 }, { 1161, 1123, 1073 }, { 1163, 1127, 1079 } };
	private static final String[] ARMOUR_TYPE = { "Bronze", "Iron", "Steel", "Black", "Mithril", "Adamant", "Rune" };

	private static final WorldTile[] DUMMY_LOCATIONS = { new WorldTile(2860, 3549, 0), new WorldTile(2860, 3547, 0), new WorldTile(2859, 3545, 0), new WorldTile(2857, 3545, 0), new WorldTile(2855, 3546, 0), new WorldTile(2855, 3548, 0), new WorldTile(2856, 3550, 0), new WorldTile(2858, 3550, 0) };
	private static final int[] DUMMY_ROTATIONS = { 1, 1, 2, 2, 3, 3, 0, 0 };

	private static final WorldTile CATAPULT_TARGET = new WorldTile(2842, 3541, 1);
	private static final NPC CATAPULT_PROJECTILE_BASE = new NPC(1957, new WorldTile(2842, 3550, 1));
	private static final Animation[] DEFENSIVE_ANIMATIONS = { new Animation(4169), new Animation(4168), new Animation(4171), new Animation(4170) };
	private static final GameObject CATAPULT = new GameObject(15616, ObjectType.SCENERY_INTERACT, 0, 2840, 3548, 1);

	private static final WorldTile SHOTPUT_FACE_18LB = new WorldTile(2876, 3549, 1), SHOTPUT_FACE_22LB = new WorldTile(2876, 3543, 1);

	public static final int[] DEFENDERS = { 20072, 8850, 8849, 8848, 8847, 8846, 8845, 8844 };
	public static final WorldTile CYCLOPS_LOBBY = new WorldTile(2843, 3535, 2);

	public static final int STRENGTH = 0, DEFENCE = 1, ATTACK = 2, COMBAT = 3, BARRELS = 4, ALL = 5;

	private transient byte defensiveStyle;
	private transient double lastDummy;
	private transient byte kegCount;
	private transient int kegTicks;

	public boolean inCyclopse;
	private int cyclopseOption;

	public static boolean canEnter(Player player) {
		if (player.getSkills().getLevelForXp(Constants.STRENGTH) + player.getSkills().getLevelForXp(Constants.ATTACK) < 130) {
			player.sendMessage("You need atleast your attack level + strength level to be above 130 to enter this door.");
			return false;
		}
		player.getControllerManager().startController(new WarriorsGuild());
		return true;
	}

	@Override
	public void start() {
		if (amountOfPlayers == 0)
			init();
		sendInterfaces();
		amountOfPlayers++;
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof AnimatedArmor npc)
			if (player != npc.getCombat().getTarget())
				return false;
		return true;
	}

	@Override
	public void sendInterfaces() {
		if (inCatapultArea(player) && player.getEquipment().getShieldId() == 8856)
			sendShieldInterfaces();
		player.getInterfaceManager().setOverlay(1057);
		for (int i = 0; i < player.getWarriorPoints().length; i++)
			player.refreshWarriorPoints(i);
	}

	@Override
	public void process() {
		if (player.withinDistance(CATAPULT_TARGET, 0)) {
			if (timer.ticks % 14 == 0)
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						if (!player.withinDistance(CATAPULT_TARGET, 0))
							return;
						if (defensiveStyle == timer.projectileType) {
							player.getSkills().addXp(Constants.DEFENSE, 15);
							player.setWarriorPoints(DEFENCE, 5);
							player.setNextAnimation(DEFENSIVE_ANIMATIONS[timer.projectileType]);
							player.sendMessage("You deflect the incomming attack.");
						} else {
							player.sendMessage("You fail to deflect the incomming attack.");
							player.applyHit(new Hit(player, Utils.random(10, 50), HitLook.TRUE_DAMAGE));
						}
					}
				}, 12);
		} else if (kegCount >= 1) {
			if (kegCount == 5)
				kegTicks++;
			if (timer.ticks % 15 == 0)
				player.setRunEnergy(player.getRunEnergy() - 9);
			player.drainRunEnergy(1);
			if ((player.getRunEnergy() / 2.0) <= Math.random() || player.hasWalkSteps() && player.getRun()) {
				loseBalance();
				return;
			}
		} else if (cyclopseOption != -1 && inCyclopse)
			if (timer.ticks % 96 == 0)
				if (cyclopseOption == ALL)
					for (int index = 0; index < player.getWarriorPoints().length; index++)
						player.setWarriorPoints(index, -3);
				else
					player.setWarriorPoints(cyclopseOption, -10);
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == 4290)
			player.startConversation(new Shanomi(player));
		else if (npc.getId() == 4287)
			player.startConversation(new Conversation(new Dialogue(new NPCStatement(4287, HeadE.HAPPY_TALKING, player.getInventory().containsItem(8856, 1) ? "You already have a shield. Good luck!" : "Good luck in there!"), () -> {
				if (!player.getInventory().containsItem(8856, 1))
					player.getInventory().addItem(8856, 1);
			})));
		return true;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() >= 15624 && object.getId() <= 15630) {
			if (lastDummy == timer.lastDummy) {
				player.sendMessage("You have already tagged a dummy.");
				return false;
			}
			submitDummyHit(object);
			return false;
		}
		if (object.getId() == 15656) {
			player.getInterfaceManager().sendInterface(412);
			return false;
		} else if (object.getId() == 66604) {
			player.getInterfaceManager().sendInterface(410);
			return false;
		} else if (object.getId() == 15664 || object.getId() == 15665) {
			if (player.getTempAttribs().getB("thrown_delay")) {
				int random = Utils.random(3);
				player.getDialogueManager().execute(new SimpleNPCMessage(), 4300, random == 0 ? "Just a moment, I dropped my hanky." : random == 1 ? "Pace yourself." : "Sorry, I'm not ready yet.");
				return false;
			} else if (!hasEmptyHands()) {
				player.getDialogueManager().execute(new SimpleMessage(), "You must have both your hands free in order to throw a shotput.");
				return false;
			}
			player.getDialogueManager().execute(new ShotputD(), object.getId() == 15664);
			return false;
		} else if (object.getId() == 15647 || object.getId() == 15641 || object.getId() == 15644) {
			player.lock(2);
			boolean inLobby = player.getY() == object.getY();
			if (object.getId() == 15647)
				if (!inLobby)
					if (player.getEquipment().getShieldId() == 8856) {
						Equipment.sendRemove(player, Equipment.SHIELD);
						closeShieldInterfaces();
					}
			player.addWalkSteps(object.getX(), inLobby ? object.getY() + (object.getId() == 15647 ? 1 : -1) : object.getY(), 1, false);
			return false;
		} else if (object.getId() == 15658 || object.getId() == 15660 || object.getId() == 15653 || object.getId() == 66758 && object.getX() == 2861 && object.getY() == 3538 && object.getPlane() == 1) {
			if (World.isSpawnedObject(object))
				return false;
			if (object.getId() == 15653)
				player.getControllerManager().forceStop();
			else if (object.getId() == 66758 && player.getX() == object.getX())
				resetKegBalance();
			player.lock(2);
			player.addWalkSteps(player.getX() == object.getX() ? object.getX() + (object.getId() == 66758 ? -1 : 1) : object.getX(), object.getY(), 1, false);
			return false;
		} else if (object.getId() >= 15669 && object.getId() <= 15673) {
			if (hasEmptyHands() && (player.getEquipment().getHatId() == -1 || kegCount >= 1))
				balanceKeg(object);
			else if (kegCount == 0)
				player.getDialogueManager().execute(new SimpleMessage(), "You must have both your hands and head free to balance kegs.");
			return false;
		} else if (object.getId() == 66599 || object.getId() == 66601) {
			player.setNextFaceWorldTile(CYCLOPS_LOBBY);
			boolean withinArea = player.getX() == object.getX();
			if (!withinArea) {
				Doors.handleDoubleDoor(player, object);
				inCyclopse = false;
			} else
				player.getDialogueManager().execute(new KamfreendaDefender());
			return false;
		} else if (object.getId() == 56887) {
			player.getDialogueManager().execute(new SimpleMessage(), "Kamfreena reports that " + killedCyclopses + " cyclopes have been slain in the guild today. She hopes that warriors will step up and kill more!");
			return false;
		}
		return true;
	}

	@Override
	public boolean processItemOnObject(final GameObject object, final Item item) {
		if (object.getId() == 15621) {
			if (player.getTempAttribs().getB("animator_spawned")) {
				player.sendMessage("You are already in combat with an animation.");
				return false;
			}
			int realIndex = getIndex(item.getId());
			if (realIndex == -1)
				return false;
			for (int armor : ARMOUR_SETS[realIndex])
				player.getInventory().deleteItem(armor, 1);
			player.setNextAnimation(new Animation(827));
			player.lock();
			final int finalIndex = realIndex;
			WorldTasks.schedule(new WorldTask() {
				int ticks;

				@Override
				public void run() {
					ticks++;
					if (ticks == 0)
						player.faceObject(object);
					else if (ticks == 1)
						player.getDialogueManager().execute(new SimpleMessage(), "The animator hums, something appears to be working.");
					else if (ticks == 2) {
						player.getDialogueManager().execute(new SimpleMessage(), "You stand back.");
						player.addWalkSteps(player.getX(), player.getY() + 3);
					} else if (ticks == 3) {
						player.faceObject(object);
						player.getDialogueManager().finishDialogue();
					} else if (ticks == 5) {
						AnimatedArmor npc = new AnimatedArmor(player, 4278 + finalIndex, object, -1, true);
						npc.setRun(false);
						npc.setNextForceTalk(new ForceTalk("IM ALIVE!"));
						npc.setNextAnimation(new Animation(4166));
						npc.addWalkSteps(player.getX(), player.getY() + 2);
						player.getTempAttribs().setB("animator_spawned", true);
						npc.getCombat().setTarget(player);
						player.unlock();
						player.getHintIconsManager().addHintIcon(npc, 0, -1, false);
					} else if (ticks == 6) {
						stop();
						return;
					}
				}
			}, 1, 1);
			return false;
		}
		return true;
	}

	private int getIndex(int checkedId) {
		for (int i = 0; i < ARMOUR_SETS.length; i++)
			for (int j = 0; j < ARMOUR_SETS[i].length; j++)
				if (ARMOUR_SETS[i][j] == checkedId) {
					for (int k = 0; k < 3; k++)
						if (!player.getInventory().containsItem(ARMOUR_SETS[i][k], 1)) {
							player.sendMessage("You need a full set of " + ARMOUR_TYPE[i] + " to use the animator.");
							return -1;
						}
					return i;
				}
		return -1;
	}

	private void submitDummyHit(final GameObject object) {
		player.setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(player.getEquipment().getWeaponId(), player.getCombatDefinitions().getAttackStyle())));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				if (isProperHit(object)) {
					player.lock(2);
					player.getSkills().addXp(Constants.ATTACK, 15);
					player.setWarriorPoints(ATTACK, 5);
					player.sendMessage("You whack the dummy sucessfully!");
					lastDummy = timer.lastDummy;
				} else {
					player.lock(5);
					player.applyHit(new Hit(player, 10, HitLook.TRUE_DAMAGE));
					player.setNextAnimation(new Animation(424));
					player.setNextSpotAnim(new SpotAnim(80, 5, 60));
					player.sendMessage("You whack the dummy whistle using the wrong attack style.");
				}
			}
		});
	}

	private boolean isProperHit(GameObject object) {
		AttackStyle style = player.getCombatDefinitions().getAttackStyle();
		if (object.getId() == 15624)
			return style.getXpType() == XPType.ACCURATE;
		if (object.getId() == 15625)
			return style.getAttackType() == AttackType.SLASH;
		else if (object.getId() == 15626)
			return style.getXpType() == XPType.AGGRESSIVE;
		else if (object.getId() == 15627)
			return style.getXpType() == XPType.CONTROLLED;
		else if (object.getId() == 15628)
			return style.getAttackType() == AttackType.CRUSH;
		else if (object.getId() == 15629)
			return style.getAttackType() == AttackType.STAB;
		else if (object.getId() == 15630)
			return style.getXpType() == XPType.DEFENSIVE;
		return false;
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		if (interfaceId == 411) {
			if (componentId == 13)
				defensiveStyle = 0;
			else if (componentId == 22)
				defensiveStyle = 1;
			else if (componentId == 31)
				defensiveStyle = 2;
			else if (componentId == 40)
				defensiveStyle = 3;
		} else if (interfaceId == 1058) {
			if (componentId == 22) {
				cyclopseOption = BARRELS;
				player.getVars().setVarBit(8668, 5);
			} else if (componentId == 23) {
				cyclopseOption = STRENGTH;
				player.getVars().setVarBit(8668, 3);
			} else if (componentId == 24) {
				cyclopseOption = COMBAT;
				player.getVars().setVarBit(8668, 4);
			} else if (componentId == 26) {
				cyclopseOption = DEFENCE;
				player.getVars().setVarBit(8668, 2);
			} else if (componentId == 25) {
				cyclopseOption = ATTACK;
				player.getVars().setVarBit(8668, 1);
			} else if (componentId == 3)
				cyclopseOption = ALL;
			else if (componentId == 22) {
				player.getVars().setVarBit(8668, 0);
				cyclopseOption = -1;
			}
			if (componentId == 44) {
				boolean failure = false;
				if (cyclopseOption == -1) {
					player.sendMessage("You must select an option before proceeding to the cyclopes room.");
					return false;
				}
				if (cyclopseOption == ALL) {
					for (int i = 0; i < player.getWarriorPoints().length; i++)
						if (player.getWarriorPoints()[i] < 30) {
							failure = true;
							break;
						}
				} else if (player.getWarriorPoints()[cyclopseOption] < 200)
					failure = true;
				if (failure) {
					player.sendMessage("You don't have enough points to complete this option.");
					return false;
				}
				if (World.getSpawnedObject(new WorldTile(2846, 3535, 2)) != null)
					return false;
				Doors.handleDoubleDoor(player, World.getObject(new WorldTile(2846, 3535, 2)));
				player.closeInterfaces();
				inCyclopse = true;
			}
		} else if (interfaceId == 387 && kegCount >= 1) {
			if (componentId == 6) {
				player.sendMessage("You can't remove the kegs off your head.");
				return false;
			}
		} else if (interfaceId == 750 && kegCount >= 1) {
			if (componentId == 4) {
				player.sendMessage("You cannot do this action while balancing the kegs on your head.");
				return false;
			}
		} else if (interfaceId == 271 || interfaceId == 749 && componentId == 4)
			if (player.getPrayer().isCurses()) {
				player.sendMessage("Harllaak frowns upon using curses in the Warrior's Guild.");
				return false;
			}
		return true;
	}

	@Override
	public boolean canEquip(int slot, int itemId) {
		if (itemId == 8856) {
			if (!inCatapultArea(player)) {
				player.sendMessage("You need to be near the target before you can equip this.");
				return false;
			}
			sendShieldInterfaces();
		} else if (slot == Equipment.HEAD && kegCount >= 1)
			return false;
		return true;
	}

	private void closeShieldInterfaces() {
		player.getInterfaceManager().sendTabs(Tab.values());
	}

	private void sendShieldInterfaces() {
		player.getInterfaceManager().sendTab(Tab.QUEST, 411);
		player.getInterfaceManager().closeTabs(Tab.COMBAT, Tab.ACHIEVEMENT, Tab.SKILLS, Tab.PRAYER, Tab.MAGIC, Tab.EMOTES);
		player.getInterfaceManager().openGameTab(Tab.QUEST);
	}

	public static boolean inCatapultArea(Player player) {
		return player.withinArea(2837, 3538, 2847, 3552) && player.getPlane() == 1;
	}

	private boolean hasEmptyHands() {
		return player.getEquipment().getGlovesId() == -1 && player.getEquipment().getWeaponId() == -1 && player.getEquipment().getShieldId() == -1;
	}

	@Override
	public boolean login() {
		start();
		return false;
	}

	@Override
	public boolean logout() {
		resetKegBalance();
		amountOfPlayers--;
		return false;
	}

	@Override
	public void magicTeleported(int teleType) {
		player.getControllerManager().forceStop();
	}

	@Override
	public void forceClose() {
		resetKegBalance();
		inCyclopse = false;
		cyclopseOption = -1;
		player.getInterfaceManager().removeOverlay(false);
		amountOfPlayers--;
	}

	public void prepareShotput(final byte stage, final boolean is18LB) {
		player.lock(7);
		player.setNextFaceWorldTile(is18LB ? SHOTPUT_FACE_18LB : SHOTPUT_FACE_22LB);
		if (stage == 0 || stage == 2)
			player.sendMessage("You take a deep breath and prepare yourself.");
		else if (stage == 1)
			player.sendMessage("You take a step and throw the shot as hard as you can.");
		if ((player.getSkills().getLevel(Constants.STRENGTH) / 100) > Math.random()) {
			player.sendMessage("You fumble and drop the shot onto your toe. Ow!");
			player.applyHit(new Hit(player, 10, HitLook.TRUE_DAMAGE));
			player.unlock();
			return;
		}
		WorldTasks.schedule(new WorldTask() {

			int ticks;

			@Override
			public void run() {
				ticks++;
				int distance = Utils.random(1, (player.getSkills().getLevel(Constants.STRENGTH) / 10) + (is18LB ? 5 : 3));

				if (ticks == 3) {
					WorldTile tile = new WorldTile(player.getX() + distance, player.getY(), 1);
					World.sendProjectile(player, tile, 690, 50, 0, 30, 1, 15, 0);
				} else if (ticks == ((distance / 2) + 4)) {
					player.getSkills().addXp(Constants.STRENGTH, distance);
					player.getTempAttribs().setB("thrown_delay", true);
				} else if (ticks >= ((distance / 2) + 5)) {
					int random = Utils.random(3);
					if (random == 0)
						player.sendMessage("The shot is perfectly thrown and gently drops to the floor.");
					else if (random == 1)
						player.sendMessage("The shot drops to the floor.");
					else
						player.sendMessage("The shot falls from the air like a brick, landing with a sickening thud.");
					int base = random == 0 ? distance * 7 : random == 1 ? distance * 4 : distance;
					player.setWarriorPoints(STRENGTH, base + Utils.random(2));
					player.getTempAttribs().removeB("thrown_delay");
					stop();
					return;
				}
			}
		}, 0, 0);
	}

	private void balanceKeg(final GameObject object) {
		player.lock(4);
		player.setNextAnimation(new Animation(4180));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				if (kegCount == 0)
					player.getAppearance().setBAS(2671);
				kegCount++;
				player.getVars().setVarBit(object.getDefinitions().varpBit, 1);
				player.getEquipment().set(Equipment.HEAD, new Item(8859 + kegCount));
				player.getEquipment().refresh(Equipment.HEAD);
				player.getAppearance().generateAppearanceData();
			}

		}, 2);
	}

	private void loseBalance() {
		player.setNextSpotAnim(new SpotAnim(689 - kegCount));
		player.lock(2);
		player.applyHit(new Hit(null, Utils.random(20, 40), HitLook.TRUE_DAMAGE));
		player.sendMessage("You lose balance and the kegs fall onto your head.");
		player.setNextForceTalk(new ForceTalk("Ouch!"));
		if (kegCount != 1) {
			player.getSkills().addXp(Constants.STRENGTH, 10 * kegCount);
			player.setWarriorPoints(BARRELS, (10 * kegCount) + (kegTicks / 2));
		}
		resetKegBalance();
	}

	private void resetKegBalance() {
		if (kegCount >= 1) {
			player.getEquipment().set(Equipment.HEAD, null);
			player.getEquipment().refresh(Equipment.HEAD);
			player.getAppearance().generateAppearanceData();
			player.getAppearance().setBAS(-1);
		}
		kegCount = 0;
		kegTicks = 0;
		for (int i = 0; i < 6; i++)
			player.getVars().setVarBit(2252 + i, 0);
	}

	public static int getBestDefender(Player player) {
		for (int index = 0; index < DEFENDERS.length; index++)
			if (player.getEquipment().getShieldId() == DEFENDERS[index] || player.getInventory().containsItem(DEFENDERS[index], 1))
				return DEFENDERS[index - 1 < 0 ? 0 : index - 1];
		return DEFENDERS[7];
	}
}
