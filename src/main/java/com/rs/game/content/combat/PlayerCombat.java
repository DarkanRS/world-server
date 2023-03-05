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
package com.rs.game.content.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.rs.Settings;
import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.combat.special_attacks.SpecialAttacks;
import com.rs.game.content.skills.dungeoneering.DungeonController;
import com.rs.game.content.skills.dungeoneering.KinshipPerk;
import com.rs.game.content.combat.special_attacks.SpecialAttack.Type;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.interactions.PlayerCombatInteraction;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.entity.player.managers.AuraManager.Aura;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class PlayerCombat extends PlayerAction {

	private Entity target;

	public static ItemClickHandler handleDFS = new ItemClickHandler(new Object[]{"Dragonfire shield"}, new String[]{"Inspect", "Activate", "Empty"}, e -> {
		if (e.getOption().equals("Inspect")) {
			if (e.getItem().getId() == 11284)
				e.getPlayer().sendMessage("The shield is empty and unresponsive.");
			else
				e.getPlayer().sendMessage("The shield contains " + e.getItem().getMetaDataI("dfsCharges") + " charges.");
		} else if (e.getOption().equals("Activate")) {
			if (e.getItem().getMetaDataI("dfsCharges") > 0) {
				if (World.getServerTicks() > e.getPlayer().getTempAttribs().getL("dfsCd")) {
					e.getPlayer().getTempAttribs().setB("dfsActive", !e.getPlayer().getTempAttribs().getB("dfsActive"));
					e.getPlayer().sendMessage("You have " + (e.getPlayer().getTempAttribs().getB("dfsActive") ? "activated" : "deactivated") + " the shield.");
				} else
					e.getPlayer().sendMessage("The dragonfire shield is still pretty hot from its last activation.");
			} else
				e.getPlayer().sendMessage("The shield is empty and unable to be activated.");
		} else if (e.getOption().equals("Empty"))
			if (e.getItem().getId() == 11284 || e.getItem().getMetaDataI("dfsCharges") < 0)
				e.getPlayer().sendMessage("The shield is already empty.");
			else
				e.getPlayer().sendOptionDialogue("Are you sure you would like to empty the " + e.getItem().getMetaDataI("dfsCharges") + " charges?", ops -> {
					ops.add("Yes, I understand the shield will lose all its stats.", () -> {
						e.getItem().deleteMetaData();
						e.getItem().setId(11284);
						e.getPlayer().getInventory().refresh();
					});
					ops.add("No, I want to keep them.");
				});
	});

	public PlayerCombat(Entity target) {
		this.target = target;
	}

	@Override
	public boolean start(Player player) {
		player.getActionManager().forceStop();
		player.setNextFaceEntity(target);
		if (!player.getControllerManager().canAttack(target))
			return false;
		if (target instanceof Player p2) {
			if (!player.isCanPvp() || !p2.isCanPvp()) {
				player.sendMessage("You can only attack players in a player-vs-player area.");
				return false;
			}
		}
		if (target instanceof Familiar familiar) {
			if (familiar == player.getFamiliar()) {
				player.sendMessage("You can't attack your own familiar.");
				return false;
			}
			if (!familiar.canAttack(player)) {
				player.sendMessage("You can't attack them.");
				return false;
			}
		}
		if (!player.canAttackMulti(target))
			return false;
		if (!checkAll(player))
			return false;
		if (target instanceof NPC n)
			player.setLastNpcInteractedName(n.getDefinitions().getName());
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		if (target instanceof Player opp)
			if ((!opp.attackedBy(player.getUsername())) && (!player.attackedBy(opp.getUsername())))
				opp.addToAttackedBy(player.getUsername());

		double multiplier = 1.0;
		if (player.hasEffect(Effect.MIASMIC_SLOWDOWN))
			multiplier = 1.5;
		if (!player.getControllerManager().keepCombating(target))
			return -1;
		addAttackedByDelay(player, target);
		player.getTempAttribs().setO("combatTarget", target);

		CombatSpell spell = player.getCombatDefinitions().getSpell();
		if (player.getTempAttribs().getB("dfsActive")) {
			Item shield = player.getEquipment().get(Equipment.SHIELD);
			player.setNextFaceEntity(target);
			if (shield == null || shield.getMetaDataI("dfsCharges") < 0) {
				player.getTempAttribs().setB("dfsActive", false);
				player.sendMessage("Your shield was unable to be activated.");
				return 3;
			}
			player.setNextSpotAnim(new SpotAnim(1165));
			player.setNextAnimation(new Animation(6696));
			WorldProjectile p = World.sendProjectile(player, target, 1166, 32, 32, 50, 2, 15, 0);
			delayMagicHit(target, p.getTaskDelay(), new Hit(player, Utils.random(100, 250), HitLook.TRUE_DAMAGE), () -> {
				target.setNextSpotAnim(new SpotAnim(1167, 0, 96));
			}, null, null);
			player.getTempAttribs().setB("dfsActive", false);
			player.getTempAttribs().setL("dfsCd", World.getServerTicks() + 200);
			shield.addMetaData("dfsCharges", shield.getMetaDataI("dfsCharges") - 1);
			player.getCombatDefinitions().refreshBonuses();
			return 3;
		}
		if (spell == null && PolyporeStaff.isWielding(player)) {
			player.setNextFaceEntity(target);
			player.setNextSpotAnim(new SpotAnim(2034));
			player.setNextAnimation(new Animation(15448));
			PolyporeStaff.drainCharge(player);
			WorldProjectile p = World.sendProjectile(player, target, 2035, 60, 32, 50, 2, 0, 0);
			Hit hit = calculateMagicHit(player, target, (5 * player.getSkills().getLevel(Constants.MAGIC)) - 180, false);
			delayMagicHit(target, p.getTaskDelay(), hit, () -> {
				if (hit.getDamage() > 0)
					target.setNextSpotAnim(new SpotAnim(2036, 0, 96));
				else {
					target.setNextSpotAnim(new SpotAnim(85, 0, 96));
					playSound(227, player, target);
				}
			}, null, null);
			return 4;
		}
		if (spell != null) {
			if (player.getCombatDefinitions().isUsingSpecialAttack())
				return SpecialAttacks.execute(Type.MAGIC, player, target);

			boolean manualCast = player.getCombatDefinitions().hasManualCastQueued();
			Item gloves = player.getEquipment().getItem(Equipment.HANDS);
			if (gloves != null && gloves.getDefinitions().getName().contains("Spellcaster glove") && player.getEquipment().getWeaponId() == -1 && new Random().nextInt(30) == 0)
				player.getTempAttribs().setO("spellcasterProc", spell);
			int delay = mageAttack(player, spell, !manualCast);
			if (player.getNextAnimation() != null && player.getTempAttribs().getO("spellcasterProc") != null) {
				player.setNextAnimation(new Animation(14339));
				player.getTempAttribs().removeO("spellcasterProc");
			}
			return delay;
		} else if (isRanging(player)) {
			RangedWeapon weapon = RangedWeapon.forId(player.getEquipment().getWeaponId());
			if (weapon.properAmmo(player, true))
				return (int) (rangeAttack(player) * multiplier);
			player.faceTile(target.getTile());
			return -1;
		} else
			return (int) (meleeAttack(player) * multiplier);
	}

	public static void addAttackedByDelay(Entity player, Entity target) {
		target.setAttackedBy(player);
		target.setAttackedByDelay(System.currentTimeMillis() + 8000); // 8seconds
	}

	public static int getRangeCombatDelay(RangedWeapon weapon, AttackStyle attackStyle) {
		int delay = weapon.getAttackDelay();
		if (attackStyle.getAttackType() == AttackType.RAPID)
			delay--;
		else if (attackStyle.getAttackType() == AttackType.LONG_RANGE)
			delay++;
		return delay - 1;
	}

	public static Entity[] getMultiAttackTargets(Player player, Entity target) {
		return getMultiAttackTargets(player, target, 1, 9);
	}

	public static Entity[] getMultiAttackTargets(Player player, Tile tile, int maxDistance, int maxAmtTargets) {
		List<Entity> possibleTargets = new ArrayList<>();
		if (!player.isAtMultiArea()) {
			Entity target = player.getTempAttribs().getO("last_target");
			if (target != null && !target.isDead() && !target.hasFinished() && target.withinDistance(tile, maxDistance) && (!(target instanceof NPC n) || n.getDefinitions().hasAttackOption()))
				possibleTargets.add(target);
			return possibleTargets.toArray(new Entity[possibleTargets.size()]);
		}

		for (Player p2 : player.queryNearbyPlayersByTileRange(maxDistance, p2 -> p2 != player && !p2.isDead() && p2.isCanPvp() && p2.isAtMultiArea() && p2.withinDistance(tile, maxDistance) && player.getControllerManager().canHit(p2))) {
			possibleTargets.add(p2);
			if (possibleTargets.size() >= maxAmtTargets)
				break;
		}
		if (possibleTargets.size() < maxAmtTargets) {
			for (NPC n : player.queryNearbyNPCsByTileRange(maxDistance, n -> n != player.getFamiliar() && !n.isDead() && n.getDefinitions().hasAttackOption() && n.isAtMultiArea() && n.withinDistance(tile, maxDistance) && player.getControllerManager().canHit(n))) {
				possibleTargets.add(n);
				if (possibleTargets.size() >= maxAmtTargets)
					break;
			}
		}
		return possibleTargets.toArray(new Entity[possibleTargets.size()]);
	}

	public static Entity[] getMultiAttackTargets(Player player, Entity target, int maxDistance, int maxAmtTargets) {
		return getMultiAttackTargets(player, target, maxDistance, maxAmtTargets, true);
	}

	public static Entity[] getMultiAttackTargets(Player player, Entity target, int maxDistance, int maxAmtTargets, boolean includeOriginalTarget) {
		List<Entity> possibleTargets = new ArrayList<>();
		if (includeOriginalTarget)
			possibleTargets.add(target);
		if (!target.isAtMultiArea())
			return possibleTargets.toArray(new Entity[possibleTargets.size()]);
		for (Player p2 : target.queryNearbyPlayersByTileRange(maxDistance, p2 -> p2 != player && !p2.isDead() && p2.isCanPvp() && p2.isAtMultiArea() && p2.withinDistance(target.getTile(), maxDistance) && player.getControllerManager().canHit(p2))) {
			possibleTargets.add(p2);
			if (possibleTargets.size() >= maxAmtTargets)
				break;
		}
		if (possibleTargets.size() < maxAmtTargets) {
			for (NPC n : target.queryNearbyNPCsByTileRange(maxDistance, n -> n != player.getFamiliar() && !n.isDead() && n.getDefinitions().hasAttackOption() && n.isAtMultiArea() && n.withinDistance(target.getTile(), maxDistance) && player.getControllerManager().canHit(n))) {
				possibleTargets.add(n);
				if (possibleTargets.size() >= maxAmtTargets)
					break;
			}
		}
		return possibleTargets.toArray(new Entity[possibleTargets.size()]);
	}

	public int mageAttack(final Player player, CombatSpell spell, boolean autoCast) {
		if (!autoCast) {
			player.getCombatDefinitions().resetSpells(false);
			player.stopAll(false);
		}
		if (spell == null)
			return -1;
		if (!spell.canCast(player, target)) {
			if (autoCast)
				player.getCombatDefinitions().resetSpells(true);
			return -1;
		}
		switch (player.getEquipment().getWeaponId()) {
			case 15486:
			case 15502:
			case 22207:
			case 22209:
			case 22211:
			case 22213:
				if (Utils.random(8) != 0)
					spell.getRuneSet().deleteRunes(player);
				else
					player.sendMessage("Your spell draws its power completely from your weapon.", true);
				break;
			default:
				spell.getRuneSet().deleteRunes(player);
				break;
		}
		int delay = spell.cast(player, target);
		int baseDamage = spell.getBaseDamage(player);
		player.getSkills().addXp(Constants.MAGIC, spell.getSplashXp());
		if (baseDamage < 0) {
			Hit hit = calculateMagicHit(player, target, 1000);
			if (hit.getDamage() > 0)
				spell.onHit(player, target, null);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					if (hit.getDamage() > 0) {
						if (spell.getHitSpotAnim() != null) {
							target.setNextSpotAnim(spell.getHitSpotAnim());
							if (spell.landSound != -1)
								playSound(spell.landSound, player, target);
						}
					} else {
						target.setNextSpotAnim(new SpotAnim(85, 0, 96));
						if (spell.splashSound != -1)
							playSound(spell.landSound, player, target);
						else
							playSound(227, player, target);
					}
				}
			}, delay);
		} else {
			boolean hit = castSpellAtTarget(player, target, spell, delay);
			if (spell.isAOE() && hit)
				attackTarget(target, getMultiAttackTargets(player, target), new MultiAttack() {
					private boolean nextTarget;

					@Override
					public boolean attack(Entity next) {
						if (!nextTarget)
							nextTarget = true;
						else
							castSpellAtTarget(player, next, spell, delay);
						return nextTarget;
					}
				});
		}
		return spell.getCombatDelay(player);
	}

	public boolean castSpellAtTarget(Player player, Entity target, CombatSpell spell, int hitDelay) {
		Hit hit = calculateMagicHit(player, target, spell.getBaseDamage(player));
		if (spell == CombatSpell.STORM_OF_ARMADYL && hit.getDamage() > 0) {
			int minHit = (player.getSkills().getLevelForXp(Constants.MAGIC) - 77) * 5;
			if (hit.getDamage() < minHit)
				hit.setDamage(hit.getDamage() + minHit);
		}
		hit.setData("combatSpell", spell);
		boolean sparkle = target.getSize() >= 2 || target.hasEffect(Effect.FREEZE) || target.hasEffect(Effect.FREEZE_BLOCK);
		delayMagicHit(target, hitDelay, hit, () -> {
			if (hit.getDamage() > 0)
				switch (spell) {
					case ICE_RUSH:
					case ICE_BURST:
					case ICE_BLITZ:
					case ICE_BARRAGE:
						if (sparkle)
							target.setNextSpotAnim(new SpotAnim(1677, 0, 96));
						else
							target.setNextSpotAnim(spell.getHitSpotAnim());
						if (spell.landSound != -1)
							playSound(spell.landSound, player, target);
						break;
					default:
						if (spell.getHitSpotAnim() != null)
							target.setNextSpotAnim(spell.getHitSpotAnim());
						if (spell.landSound != -1)
							playSound(spell.landSound, player, target);
						break;
				}
			else {
				target.setNextSpotAnim(new SpotAnim(85, 0, 96));
				if (spell.splashSound != -1)
					playSound(spell.landSound, player, target);
				else
					playSound(227, player, target);
			}
		}, () -> {
			spell.onHit(player, target, hit);
		}, null);
		return hit.getDamage() > 0;
	}

	public interface MultiAttack {
		public boolean attack(Entity nextTarget);
	}

	public static void attackTarget(Entity target, Entity[] targets, MultiAttack perform) {
		for (Entity t : targets) {
			if (!perform.attack(t))
				break;
		}
	}

	public static int getRangeCombatDelay(Player player) {
		RangedWeapon weapon = RangedWeapon.forId(player.getEquipment().getWeaponId());
		if (weapon == null)
			return 4;
		return getRangeCombatDelay(weapon, player.getCombatDefinitions().getAttackStyle());
	}

	private int rangeAttack(final Player player) {
		final int weaponId = player.getEquipment().getWeaponId();
		final AttackStyle attackStyle = player.getCombatDefinitions().getAttackStyle();
		int soundId = getSoundId(weaponId, attackStyle);
		RangedWeapon weapon = RangedWeapon.forId(weaponId);
		AmmoType ammo = AmmoType.forId(player.getEquipment().getAmmoId());
		int combatDelay = getRangeCombatDelay(weapon, attackStyle);

		if (player.getCombatDefinitions().isUsingSpecialAttack())
			return SpecialAttacks.execute(Type.RANGE, player, target);
		WorldProjectile p = weapon.getProjectile(player, target);
		switch (weapon) {
			case DEATHTOUCHED_DART -> {
				player.setNextAnimation(weapon.getAttackAnimation());
				target.setNextSpotAnim(new SpotAnim(44));
				target.resetWalkSteps();
				if (target instanceof NPC npc) {
					WorldTasks.delay(p.getTaskDelay(), () -> {
						npc.setCapDamage(-1);
						target.applyHit(new Hit(player, target.getHitpoints(), HitLook.TRUE_DAMAGE));
					});
					dropAmmo(player, target, Equipment.WEAPON, 1);
					return 8;
				} else
					return 0;
			}
			case CHINCHOMPA, RED_CHINCHOMPA -> { //TODO validate the logic here
				attackTarget(target, getMultiAttackTargets(player, target), new MultiAttack() {
					private boolean nextTarget;

					@Override
					public boolean attack(Entity next) {
						Hit hit = calculateHit(player, next, weaponId, attackStyle, true, true, 1.0, weaponId == 10034 ? 1.2 : 1.0);
						player.setNextAnimation(new Animation(2779));
						WorldTasks.delay(p.getTaskDelay(), () -> next.setNextSpotAnim(new SpotAnim(2739, 0, 96 << 16)));
						delayHit(next, p.getTaskDelay(), weaponId, attackStyle, hit);
						if (!nextTarget) {
							if (hit.getDamage() <= 0)
								return false;
							nextTarget = true;
						}
						return nextTarget;
					}
				});
				dropAmmo(player, target, Equipment.WEAPON, 1);
			}
			case SWAMP_LIZARD, ORANGE_SALAMANDER, RED_SALAMANDER, BLACK_SALAMANDER -> {
				Hit hit = switch(attackStyle.getName()) {
					//TODO use proper combat style combat formula for each damage
					case "Flare" -> calculateHit(player, target, weaponId, attackStyle, true).setLook(HitLook.RANGE_DAMAGE);
					case "Blaze" -> calculateHit(player, target, weaponId, attackStyle, true).setLook(HitLook.MAGIC_DAMAGE);
					default -> calculateHit(player, target, weaponId, attackStyle, true).setLook(HitLook.MELEE_DAMAGE); //Scorch
				};
				delayHit(target, p.getTaskDelay(), weaponId, attackStyle, hit);
				dropAmmo(player, target, Equipment.AMMO, 1);
				if (attackStyle.getName().equals("Flare"))
					combatDelay = 3;
			}
			case CROSSBOW, BRONZE_CROSSBOW, BLURITE_CROSSBOW, IRON_CROSSBOW, STEEL_CROSSBOW, BLACK_CROSSBOW, MITH_CROSSBOW, ADAMANT_CROSSBOW, RUNE_CROSSBOW, ARMADYL_CROSSBOW, CHAOTIC_CROSSBOW, ZANIKS_CROSSBOW -> {
				Hit hit;
				boolean specced = false;
				if (player.getEquipment().getAmmoId() == 9241 && Utils.random(100) <= 55 && !target.getPoison().isPoisoned()) {
					target.setNextSpotAnim(new SpotAnim(752));
					target.getPoison().makePoisoned(50);
					specced = true;
				}
				if (player.getEquipment().getAmmoId() != -1 && Utils.getRandomInclusive(10) == 0) {
					switch (player.getEquipment().getAmmoId()) {
						case 9237:
							hit = calculateHit(player, target, weaponId, attackStyle, true);
							target.setNextSpotAnim(new SpotAnim(755));
							if (target instanceof Player p2)
								p2.stopAll();
							else if (target instanceof NPC n)
								n.setTarget(null);
							soundId = 2914;
							break;
						case 9242:
							hit = Hit.range(player, (int) (target.getHitpoints() * 0.2));
							target.setNextSpotAnim(new SpotAnim(754));
							player.applyHit(new Hit(target, player.getHitpoints() > 20 ? (int) (player.getHitpoints() * 0.1) : 1, HitLook.REFLECTED_DAMAGE));
							soundId = 2912;
							break;
						case 9243:
							hit = calculateHit(player, target, weaponId, attackStyle, true, false, 1.0, 1.15);
							target.setNextSpotAnim(new SpotAnim(751));
							soundId = 2913;
							break;
						case 9244:
							hit = calculateHit(player, target, weaponId, attackStyle, true, false, 1.0, PlayerCombat.getAntifireLevel(target, true) > 0 ? 1.45 : 1.0);
							target.setNextSpotAnim(new SpotAnim(756));
							soundId = 2915;
							break;
						case 9245:
							hit = calculateHit(player, target, weaponId, attackStyle, true, false, 1.0, 1.15);
							target.setNextSpotAnim(new SpotAnim(753));
							player.heal((int) (player.getMaxHitpoints() * 0.25));
							soundId = 2917;
							break;
						default:
							hit = calculateHit(player, target, weaponId, attackStyle, true);
					}
					specced = true;
				} else {
					hit = calculateHit(player, target, weaponId, attackStyle, true);
					checkSwiftGlovesEffect(player, p.getTaskDelay(), attackStyle, weaponId, hit, p);
				}
				delayHit(target, p.getTaskDelay(), weaponId, attackStyle, hit);
				if (specced)
					player.getEquipment().removeAmmo(Equipment.AMMO, 1);
				else
					dropAmmo(player, target, Equipment.AMMO, 1);
			}
			case ROYAL_CROSSBOW -> {
				if (target instanceof Player)
					player.sendMessage("The Royal crossbow seems unresponsive against this target.", true);
				else {
					int stacks = player.getTempAttribs().getI("rcbStacks", 0);
					if (World.getServerTicks() < player.getTempAttribs().getL("rcbLockOnTimer"))
						stacks++;
					else {
						stacks = 1;
						player.sendMessage("Your crossbow loses focus on your target.");
					}
					if (stacks == 9)
						player.sendMessage("Your crossbow locks onto your target.");
					boolean lockedOn = stacks >= 9;
					player.getTempAttribs().setI("rcbStacks", stacks);
					player.getTempAttribs().setL("rcbLockOnTimer", World.getServerTicks() + 28);
					Hit hit = calculateHit(player, target, weaponId, attackStyle, true);
					delayHit(target, p.getTaskDelay(), weaponId, attackStyle, hit, null, () -> {
						if (weaponId == 24339 || target.isDead() || target.hasFinished())
							return;
						int maxHit = getMaxHit(player, target, weaponId, attackStyle, true, 1.0);
						int minBleed = (int) (maxHit * (lockedOn ? 0.25 : 0.20));
						int maxBleed = (int) (minBleed * 0.70);
						WorldTasks.delay(14, () -> {
							if (target == null || target.isDead() || target.hasFinished())
								return;
							delayHit(target, 0, weaponId, attackStyle, Hit.range(player, Utils.random(minBleed, maxBleed)));
						});
						WorldTasks.delay(28, () -> {
							if (target == null || target.isDead() || target.hasFinished())
								return;
							delayHit(target, 0, weaponId, attackStyle, Hit.range(player, Utils.random(minBleed, maxBleed)));
						});
					}, null);
					checkSwiftGlovesEffect(player, p.getTaskDelay(), attackStyle, weaponId, hit, p);
				}
				player.getEquipment().removeAmmo(Equipment.AMMO, 1);
			}
			case HAND_CANNON -> {
				if (Utils.getRandomInclusive(player.getSkills().getLevel(Constants.FIREMAKING) << 1) == 0) {
					player.setNextSpotAnim(new SpotAnim(2140));
					player.getEquipment().deleteSlot(Equipment.WEAPON);
					player.getAppearance().generateAppearanceData();
					player.applyHit(new Hit(player, Utils.getRandomInclusive(150) + 10, HitLook.TRUE_DAMAGE));
					player.setNextAnimation(new Animation(12175));
					return combatDelay;
				}
				delayHit(target, p.getTaskDelay(), weaponId, attackStyle, calculateHit(player, target, weaponId, attackStyle, true));
				dropAmmo(player, target, Equipment.AMMO, 1);
			}
			case SAGAIE -> {
				double damageMod = Utils.clampD((Utils.getDistanceI(player.getTile(), target.getMiddleTile()) / (double) getAttackRange(player)) * 0.70, 0.01, 1.0);
				Hit hit = calculateHit(player, target, weaponId, attackStyle, true, true, 1.0D - (damageMod * 0.95), 1.0D + damageMod);
				delayHit(target, p.getTaskDelay(), weaponId, attackStyle, hit);
				checkSwiftGlovesEffect(player, p.getTaskDelay(), attackStyle, weaponId, hit, p);
				dropAmmo(player, target, Equipment.WEAPON, 1);
			}
			case BOLAS -> {
				int delay = Ticks.fromSeconds(15);
				if (target instanceof Player t) {
					boolean slashBased = t.getEquipment().getItem(3) != null;
					if (weapon != null) {
						int slash = t.getCombatDefinitions().getBonus(Bonus.SLASH_ATT);
						for (int i = Bonus.STAB_ATT.ordinal(); i <= Bonus.RANGE_ATT.ordinal(); i++)
							if (t.getCombatDefinitions().getBonus(Bonus.values()[i]) > slash) {
								slashBased = false;
								break;
							}
					}
					if (t.getInventory().containsItem(946, 1) || slashBased)
						delay /= 2;
					if (t.getPrayer().isProtectingRange())
						delay /= 2;
					if (delay < Ticks.fromSeconds(5))
						delay = Ticks.fromSeconds(5);
				}
				if (calculateHit(player, target, weaponId, attackStyle, true).getDamage() > 0) {
					target.freeze(delay, true);
					WorldTasks.schedule(2, () -> target.setNextSpotAnim(new SpotAnim(469, 0, 96)));
				}
				playSound(soundId, player, target);
				player.getEquipment().removeAmmo(Equipment.WEAPON, 1);
			}
			case DARK_BOW -> {
				Hit hit = calculateHit(player, target, weaponId, attackStyle, true);
				delayHit(target, p.getTaskDelay(), weaponId, attackStyle, hit);
				checkSwiftGlovesEffect(player, p.getTaskDelay(), attackStyle, weaponId, hit, p);
				WorldProjectile p2 = World.sendProjectile(player, target, AmmoType.forId(player.getEquipment().getAmmoId()).getProjAnim(player.getEquipment().getAmmoId()), 30, 50, 1);
				delayHit(target, p2.getTaskDelay(), weaponId, attackStyle, calculateHit(player, target, weaponId, attackStyle, true));
				dropAmmo(player, target, Equipment.AMMO, 2);
			}
			default -> {
				if (weapon.isThrown()) {
					Hit hit = calculateHit(player, target, weaponId, attackStyle, true);
					delayHit(target, p.getTaskDelay(), weaponId, attackStyle, hit);
					checkSwiftGlovesEffect(player, p.getTaskDelay(), attackStyle, weaponId, hit, p);
					dropAmmo(player, target, Equipment.WEAPON, 1);
				} else {
					Hit hit = calculateHit(player, target, weaponId, attackStyle, true);
					delayHit(target, p.getTaskDelay(), weaponId, attackStyle, hit);
					checkSwiftGlovesEffect(player, p.getTaskDelay(), attackStyle, weaponId, hit, p);
					if (weapon.getAmmos() != null)
						dropAmmo(player, target);
				}
			}
		}
		player.setNextAnimation(weapon.getAttackAnimation());
		SpotAnim attackSpotAnim = weapon.getAttackSpotAnim(player, ammo);
		if (attackSpotAnim != null)
			player.setNextSpotAnim(attackSpotAnim);
		playSound(soundId, player, target);
		return combatDelay;
	}

	private void checkSwiftGlovesEffect(Player player, int hitDelay, AttackStyle attackStyle, int weaponId, Hit hit, WorldProjectile p) {
		Item gloves = player.getEquipment().getItem(Equipment.HANDS);
		if (gloves == null || !gloves.getDefinitions().getName().contains("Swift glove"))
			return;
		if (hit.getDamage() != 0 && hit.getDamage() < ((hit.getMaxHit() / 3) * 2) || new Random().nextInt(3) != 0)
			return;
		player.sendMessage("You fired an extra shot.");
		World.sendProjectile(player, target, p.getSpotAnimId(), p.getStartHeight() - 5, p.getEndHeight() - 5, p.getStartTime(), 2, p.getAngle() - 5 < 0 ? 0 : p.getAngle() - 5, p.getSlope());
		delayHit(target, hitDelay, weaponId, attackStyle, calculateHit(player, target, weaponId, attackStyle, true));
		if (hit.getDamage() > (hit.getMaxHit() - 10)) {
			target.freeze(Ticks.fromSeconds(10), false);
			target.setNextSpotAnim(new SpotAnim(181, 0, 96));
		}
	}

	public static void dropAmmo(Player player, Entity target, int slot, int quantity) {
		if (player.getEquipment().getItem(slot) == null)
			return;
		int ammoId = player.getEquipment().getItem(slot).getId();
		switch (ammoId) {
			case 15243: //hand cannon shot
			case 25202: //DT darts
			case 10033: //Chins
			case 10034:
			case 19152: //god arrows
			case 19157:
			case 19162:
			case 10142: //Salamanders
			case 10143:
			case 10144:
			case 10145:
				player.getEquipment().removeAmmo(slot, quantity); //delete 100% of the time
				return;
		}
		/*
		 * Ava's Attractor
		 * Drop onto floor: 1/3
		 * Recovered automatically: 2/3
		 *
		 * Ava's Accumulator
		 * Drop onto floor: 1/9
		 * Recovered automatically: 8/9
		 */
		switch (player.getEquipment().getCapeId()) {
			case 10498:
				if (Utils.random(3) != 0)
					return;
				break;
			case 10499:
			case 20068:
			case 20769:
			case 20771:
				if (Utils.random(9) != 0)
					return;
				break;
			default:
				break;
		}
		player.getEquipment().removeAmmo(slot, quantity);
		if (Utils.random(5) == 0) //1/5 chance to just break the ammo entirely
			return;
		World.addGroundItem(new Item(ammoId, quantity), Tile.of(target.getCoordFaceX(target.getSize()), target.getCoordFaceY(target.getSize()), target.getPlane()), player);
	}

	public static void dropAmmo(Player player, Entity target) {
		dropAmmo(player, target, Equipment.AMMO, 1);
	}

	@SuppressWarnings("unused")
	private int getRangeHitDelay(Player player) {
		return Utils.getDistance(player.getX(), player.getY(), target.getX(), target.getY()) >= 5 ? 2 : 1;
	}

	private int meleeAttack(final Player player) {
		if (player.hasEffect(Effect.FREEZE) && target.getSize() == 1) {
			Direction dir = Direction.forDelta(target.getX() - player.getX(), target.getY() - player.getY());
			if (dir != null)
				switch (dir) {
					case NORTH:
					case SOUTH:
					case EAST:
					case WEST:
						break;
					default:
						return 0;
				}
		}
		int weaponId = player.getEquipment().getWeaponId();
		AttackStyle attackStyle = player.getCombatDefinitions().getAttackStyle();
		int combatDelay = getMeleeCombatDelay(player, weaponId);
		int soundId = getSoundId(weaponId, attackStyle);
		if (weaponId == -1) {
			Item gloves = player.getEquipment().getItem(Equipment.HANDS);
			if (gloves != null && gloves.getDefinitions().getName().contains("Goliath gloves"))
				weaponId = -2;
		}

		if (player.getCombatDefinitions().isUsingSpecialAttack())
			return SpecialAttacks.execute(Type.MELEE, player, target);

		if (weaponId == -2) {
			int randomSeed = 25;

			if (target instanceof NPC n)
				randomSeed -= (n.getBonus(Bonus.CRUSH_DEF) / 100) * 1.3;

			if (new Random().nextInt(randomSeed) == 0) {
				player.setNextAnimation(new Animation(14417));
				final AttackStyle attack = attackStyle;
				attackTarget(target, getMultiAttackTargets(player, target, 5, Integer.MAX_VALUE), new MultiAttack() {
					private boolean nextTarget;

					@Override
					public boolean attack(Entity next) {
						next.freeze(Ticks.fromSeconds(10), true);
						next.setNextSpotAnim(new SpotAnim(181, 0, 96));
						final Entity t = next;
						WorldTasks.schedule(1, () -> t.applyHit(calculateHit(player, next, -2, attack, false, false, 1.0, 1.0).setLook(HitLook.TRUE_DAMAGE)));
						if (next instanceof Player p) {
							for (int i = 0; i < 7; i++)
								if (i != 3 && i != 5)
									p.getSkills().drainLevel(i, 7);
							p.sendMessage("Your stats have been drained!");
						} else if (next instanceof NPC n)
							n.lowerDefense(0.05, 0.0);
						if (!nextTarget)
							nextTarget = true;
						return nextTarget;

					}
				});
				return combatDelay;
			}
		}
		delayNormalHit(target, weaponId, attackStyle, calculateHit(player, target, weaponId, attackStyle, false));
		player.setNextAnimation(new Animation(getWeaponAttackEmote(weaponId, attackStyle)));
		playSound(soundId, player, target);
		return combatDelay;
	}

	public static void playSound(int soundId, Player player, Entity target) {
		if (soundId == -1)
			return;
		player.soundEffect(soundId);
		if (target instanceof Player p2)
			p2.soundEffect(soundId);
	}

	public static Hit calculateMagicHit(Player player, Entity target, int baseDamage) {
		return calculateMagicHit(player, target, baseDamage, true);
	}

	public static Hit calculateMagicHit(Player player, Entity target, int baseDamage, boolean applyMageLevelBoost) {
		Hit hit = getMagicMaxHit(player, target, baseDamage, applyMageLevelBoost);
		hit.setDamage(Utils.random(hit.getDamage() + 1));
		if (hit.getDamage() > 0)
			if (target instanceof NPC n)
				if (n.getId() == 9463 && hasFireCape(player))
					hit.setDamage(hit.getDamage() + 40);
		return hit;
	}

	public static Hit getMagicMaxHit(Player player, Entity target, int spellBaseDamage, boolean applyMageLevelBoost) {
		double lvl = Math.floor(player.getSkills().getLevel(Constants.MAGIC) * player.getPrayer().getMageMultiplier());
		lvl += 8;
		if (fullVoidEquipped(player, 11663, 11674))
			lvl *= 1.3;
		lvl *= player.getAuraManager().getMagicAcc();
		double atkBonus = player.getCombatDefinitions().getBonus(Bonus.MAGIC_ATT);

		double atk = Math.floor(lvl * (atkBonus + 64));
		int maxHit = spellBaseDamage;

		if (player.hasSlayerTask())
			if (target instanceof NPC n && player.getSlayer().isOnTaskAgainst(n))
				if (player.getEquipment().wearingHexcrest() || player.getEquipment().wearingSlayerHelmet())
					atk *= 1.15;

		double def;
		if (target instanceof Player p2) {
			double defLvl = Math.floor(p2.getSkills().getLevel(Constants.DEFENSE) * p2.getPrayer().getDefenceMultiplier());
			defLvl += p2.getCombatDefinitions().getAttackStyle().getAttackType() == AttackType.LONG_RANGE || p2.getCombatDefinitions().getAttackStyle().getXpType() == XPType.DEFENSIVE ? 3 : p2.getCombatDefinitions().getAttackStyle().getXpType() == XPType.CONTROLLED ? 1 : 0;
			defLvl += 8;
			defLvl *= 0.3;
			double magLvl = Math.floor(p2.getSkills().getLevel(Constants.MAGIC) * p2.getPrayer().getMageMultiplier());
			magLvl *= 0.7;

			double totalDefLvl = defLvl + magLvl;

			double defBonus = p2.getCombatDefinitions().getBonus(Bonus.MAGIC_DEF);

			def = Math.floor(totalDefLvl * (defBonus + 64));
		} else {
			NPC n = (NPC) target;
			if (n.getName().startsWith("Vyre"))
				if (player.getEquipment().getWeaponId() == 21580) {
					atk *= 1.5;
					maxHit *= 1.5;
				} else
					maxHit = 0;
			if (n.getName().equals("Turoth") || n.getName().equals("Kurask"))
				if (player.getEquipment().getWeaponId() != 4170)
					maxHit = 0;
			double defLvl = n.getMagicLevel();
			double defBonus = n.getDefinitions().getMagicDef();
			defLvl += 8;
			def = Math.floor(defLvl * (defBonus + 64));
		}

		double prob = atk > def ? (1 - (def + 2) / (2 * (atk + 1))) : (atk / (2 * (def + 1)));
		if (Settings.getConfig().isDebug() && player.getNSV().getB("hitChance"))
			player.sendMessage("Your hit chance: " + Utils.formatDouble(prob * 100.0) + "%");
		if (prob <= Math.random())
			return new Hit(player, 0, HitLook.MAGIC_DAMAGE);

		if (applyMageLevelBoost) {
			double boostedMageLevelBonus = 1 + ((player.getSkills().getLevel(Constants.MAGIC) - player.getSkills().getLevelForXp(Constants.MAGIC)) * 0.03);
			if (boostedMageLevelBonus > 1)
				maxHit *= boostedMageLevelBonus;
		}
		double magicPerc = player.getCombatDefinitions().getBonus(Bonus.MAGIC_STR);
		if (player.getTempAttribs().getO("spellcasterProc") != null) {
			if (spellBaseDamage > 60) {
				magicPerc += 17;
				if (target instanceof Player p) {
					p.getSkills().drainLevel(0, p.getSkills().getLevel(0) / 10);
					p.getSkills().drainLevel(1, p.getSkills().getLevel(1) / 10);
					p.getSkills().drainLevel(2, p.getSkills().getLevel(2) / 10);
					p.sendMessage("Your melee skills have been drained.");
					player.sendMessage("Your spell weakened your enemy.");
				}
				player.sendMessage("Your magic surged with extra power.");
			}
		}
		double mageBonusBoost = magicPerc / 100 + 1;
		maxHit *= mageBonusBoost;

		if (player.hasSlayerTask())
			if (target instanceof NPC n && player.getSlayer().isOnTaskAgainst(n))
				if (player.getEquipment().wearingHexcrest() || player.getEquipment().wearingSlayerHelmet())
					maxHit *= 1.15;
		int finalMaxHit = (int) Math.floor(maxHit);
		if (Settings.getConfig().isDebug() && player.getNSV().getB("hitChance"))
			player.sendMessage("Your max hit: " + finalMaxHit);
		return new Hit(player, finalMaxHit, HitLook.MAGIC_DAMAGE).setMaxHit(finalMaxHit);
	}

	public static Hit calculateHit(Player player, Entity target, int weaponId, AttackStyle attackStyle, boolean ranging, boolean calcDefense, double accuracyModifier, double damageModifier) {
		return calculateHit(player, target, 1, getMaxHit(player, target, weaponId, attackStyle, ranging, damageModifier), weaponId, attackStyle, ranging, calcDefense, accuracyModifier);
	}

	public static Hit calculateHit(Player player, Entity target, boolean ranging, boolean calcDefense, double accuracyModifier, double damageModifier) {
		return calculateHit(player, target, 1, getMaxHit(player, target, player.getEquipment().getWeaponId(), player.getCombatDefinitions().getAttackStyle(), ranging, damageModifier), player.getEquipment().getWeaponId(), player.getCombatDefinitions().getAttackStyle(), ranging, calcDefense, accuracyModifier);
	}

	public static Hit calculateHit(Player player, Entity target, int weaponId, AttackStyle attackStyle, boolean ranging) {
		return calculateHit(player, target, weaponId, attackStyle, ranging, true, 1.0D, 1.0D);
	}

	public static Hit calculateHit(Player player, Entity target, boolean ranging) {
		return calculateHit(player, target, player.getEquipment().getWeaponId(), player.getCombatDefinitions().getAttackStyle(), ranging, true, 1.0D, 1.0D);
	}

	public static Hit calculateHit(Player player, Entity target, int minHit, int maxHit, boolean ranging, boolean calcDefense, double accuracyModifier) {
		return calculateHit(player, target, minHit, maxHit, player.getEquipment().getWeaponId(), player.getCombatDefinitions().getAttackStyle(), ranging, calcDefense, accuracyModifier);
	}

	public static Hit calculateHit(Player player, Entity target, int minHit, int maxHit, int weaponId, AttackStyle attackStyle, boolean ranging, boolean calcDefense, double accuracyModifier) {
		Hit hit = new Hit(player, 0, ranging ? HitLook.RANGE_DAMAGE : HitLook.MELEE_DAMAGE);
		boolean veracsProc = false;
		if (calcDefense) {
			double atkLvl = Math.floor(player.getSkills().getLevel(ranging ? Constants.RANGE : Constants.ATTACK) * (ranging ? player.getPrayer().getRangeMultiplier() : player.getPrayer().getAttackMultiplier()));
			atkLvl += attackStyle.getAttackType() == AttackType.ACCURATE || attackStyle.getXpType() == XPType.ACCURATE ? 3 : attackStyle.getXpType() == XPType.CONTROLLED ? 1 : 0;
			atkLvl += 8;
			if (fullVoidEquipped(player, ranging ? (new int[]{11664, 11675}) : (new int[]{11665, 11676})))
				atkLvl *= 1.1;
			if (ranging)
				atkLvl *= player.getAuraManager().getRangeAcc();
			double atkBonus = player.getCombatDefinitions().getAttackBonusForStyle();
			if (weaponId == -2)
				//goliath gloves
				atkBonus += 82;

			double atk = Math.floor(atkLvl * (atkBonus + 64));
			atk *= accuracyModifier;

			if (!ranging && attackStyle.getXpType() == XPType.ACCURATE && player.getDungManager().getActivePerk() == KinshipPerk.TACTICIAN && player.getControllerManager().isIn(DungeonController.class))
				atk = Math.floor(atk * 1.1 + (player.getDungManager().getKinshipTier(KinshipPerk.TACTICIAN) * 0.01));

			if (player.hasSlayerTask())
				if (target instanceof NPC n)
					if (player.getSlayer().isOnTaskAgainst(n))
						if (ranging) {
							if (player.getEquipment().wearingFocusSight() || player.getEquipment().wearingSlayerHelmet()) {
								atk *= (7.0 / 6.0);
								maxHit *= (7.0 / 6.0);
							}
						} else {
							if (player.getEquipment().wearingBlackMask() || player.getEquipment().wearingSlayerHelmet()) {
								atk *= (7.0 / 6.0);
								maxHit *= (7.0 / 6.0);
							}
							if (player.getEquipment().getSalveAmulet() != -1 && n.getDefinitions().isUndead())
								switch (player.getEquipment().getSalveAmulet()) {
									case 0:
										atk *= 1.15;
										maxHit *= 1.15;
										break;
									case 1:
										atk *= 1.20;
										maxHit *= 1.20;
										break;
								}
						}

			double def;
			if (target instanceof Player p2) {
				double defLvl = Math.floor(p2.getSkills().getLevel(Constants.DEFENSE) * p2.getPrayer().getDefenceMultiplier());
				defLvl += p2.getCombatDefinitions().getAttackStyle().getAttackType() == AttackType.LONG_RANGE || p2.getCombatDefinitions().getAttackStyle().getXpType() == XPType.DEFENSIVE ? 3 : p2.getCombatDefinitions().getAttackStyle().getXpType() == XPType.CONTROLLED ? 1 : 0;
				defLvl += 8;
				double defBonus = p2.getCombatDefinitions().getDefenseBonusForStyle(player.getCombatDefinitions().getAttackStyle());

				def = Math.floor(defLvl * (defBonus + 64));

				if (!ranging)
					if (p2.getFamiliarPouch() == Pouch.STEEL_TITAN)
						def *= 1.15;
			} else {
				NPC n = (NPC) target;
				if (n.getName().startsWith("Vyre")) {
					int wId = player.getEquipment().getWeaponId();
					if (wId == 21581 || wId == 21582) {
						atk *= 2;
						maxHit *= 2;
					} else if (!(wId == 6746 || wId == 2961 || wId == 2963 || wId == 2952 || wId == 2402 || (wId >= 7639 && wId <= 7648) || (wId >= 13117 && wId <= 13146)))
						maxHit = 0;
				}
				if (n.getName().equals("Turoth") || n.getName().equals("Kurask")) {
					int wId = player.getEquipment().getWeaponId();
					if (!(wId == 4158 || wId == 13290) && !(player.getEquipment().getWeaponName().indexOf("bow") > -1 && ItemDefinitions.getDefs(player.getEquipment().getAmmoId()).name.toLowerCase().indexOf("broad") > -1))
						maxHit = 0;
				}
				double defLvl = n.getDefenseLevel();
				double defBonus = player.getCombatDefinitions().getAttackStyle().getAttackType().getDefenseBonus(n);
				defLvl += 8;
				def = Math.floor(defLvl * (defBonus + 64));
			}
			if (maxHit != 0 && fullVeracsEquipped(player) && Utils.random(4) == 0)
				veracsProc = true;
			double prob = atk > def ? (1 - (def + 2) / (2 * (atk + 1))) : (atk / (2 * (def + 1)));
			if (Settings.getConfig().isDebug() && player.getNSV().getB("hitChance"))
				player.sendMessage("Your hit chance: " + Utils.formatDouble(prob * 100.0) + "%");
			if (prob <= Math.random() && !veracsProc)
				return hit.setDamage(0);
		}
		int finalHit = Utils.random(minHit, maxHit);
		if (veracsProc)
			finalHit += 1.0;
		if (target instanceof NPC n)
			if (n.getId() == 9463 && hasFireCape(player))
				finalHit += 40;
		if (player.getAuraManager().isActivated(Aura.EQUILIBRIUM)) {
			int perc25MaxHit = (int) (maxHit * 0.25);
			finalHit -= perc25MaxHit;
			maxHit -= perc25MaxHit;
			if (finalHit < 0)
				finalHit = 0;
			if (finalHit < perc25MaxHit)
				finalHit += perc25MaxHit;
		}
		hit.setMaxHit(maxHit);
		hit.setDamage(finalHit);
		return hit;
	}

	public static final int getMaxHit(Player player, Entity target, boolean ranging, double damageMultiplier) {
		return getMaxHit(player, target, player.getEquipment().getWeaponId(), player.getCombatDefinitions().getAttackStyle(), ranging, damageMultiplier);
	}

	public static final int getMaxHit(Player player, Entity target, int weaponId, AttackStyle attackStyle, boolean ranging, double damageMultiplier) {
		if (ranging) {
			if (target != null && weaponId == 24338 && target instanceof Player) {
				player.sendMessage("The royal crossbow feels weak and unresponsive against other players.");
				return 60;
			}
			double lvl = Math.floor(player.getSkills().getLevel(Constants.RANGE) * player.getPrayer().getRangeMultiplier());
			lvl += attackStyle.getAttackType() == AttackType.ACCURATE ? 3 : 0;
			lvl += 8;
			if (fullVoidEquipped(player, 11664, 11675))
				lvl = Math.floor(lvl * 1.1);
			if (attackStyle.getAttackType() == AttackType.RAPID && player.getDungManager().getActivePerk() == KinshipPerk.DESPERADO && player.getControllerManager().isIn(DungeonController.class))
				lvl = Math.floor(lvl * 1.1 + (player.getDungManager().getKinshipTier(KinshipPerk.DESPERADO) * 0.01));
			double str = player.getCombatDefinitions().getBonus(Bonus.RANGE_STR);
			double baseDamage = 5 + lvl * (str + 64) / 64;
			int maxHit = (int) Math.floor(baseDamage * damageMultiplier);
			if (Settings.getConfig().isDebug() && player.getNSV().getB("hitChance"))
				player.sendMessage("Your max hit: " + maxHit);
			return maxHit;
		}
		double lvl = Math.floor(player.getSkills().getLevel(Constants.STRENGTH) * player.getPrayer().getStrengthMultiplier());
		lvl += attackStyle.getXpType() == XPType.AGGRESSIVE ? 3 : attackStyle.getXpType() == XPType.CONTROLLED ? 1 : 0;
		lvl += 8;
		if (fullVoidEquipped(player, 11665, 11676))
			lvl = Math.floor(lvl * 1.1);
		if (attackStyle.getXpType() == XPType.AGGRESSIVE && player.getDungManager().getActivePerk() == KinshipPerk.BERSERKER && player.getControllerManager().isIn(DungeonController.class))
			lvl = Math.floor(lvl * 1.1 + (player.getDungManager().getKinshipTier(KinshipPerk.BERSERKER) * 0.01));
		double str = player.getCombatDefinitions().getBonus(Bonus.MELEE_STR);
		if (weaponId == -2)
			str += 82;
		double baseDamage = 5 + lvl * (str + 64) / 64;

		//int multiplier = PluginManager.handle()

		switch (weaponId) {
			case 6523:
			case 6525:
			case 6527:
			case 6528:
				if (player.getEquipment().getAmuletId() == 11128)
					baseDamage *= 1.2;
				break;
			case 4718:
			case 4886:
			case 4887:
			case 4888:
			case 4889:
				if (fullDharokEquipped(player)) {
					double mul = 1.0 + (player.getMaxHitpoints() - player.getHitpoints()) / 1000.0 * (player.getMaxHitpoints() / 1000.0);
					baseDamage *= mul;
				}
				break;
			case 10581:
			case 10582:
			case 10583:
			case 10584:
				if (target != null && target instanceof NPC n)
					if (n.getName().startsWith("Kalphite"))
						if (Utils.random(51) == 0)
							baseDamage *= 3.0;
						else
							baseDamage *= (4.0 / 3.0);
				break;
			case 15403:
			case 22405:
				if (target != null && target instanceof NPC n)
					if (n.getName().equals("Dagannoth") || n.getName().equals("Wallasalki") || n.getName().equals("Dagannoth Supreme"))
						baseDamage *= 2.75;
				break;
			case 6746:
				if (target != null && target instanceof NPC n)
					if (n.getName().toLowerCase().contains("demon"))
						baseDamage *= 1.6;
				break;
			default:
				break;
		}
		int maxHit = (int) Math.floor(baseDamage * damageMultiplier);
		if (Settings.getConfig().isDebug() && player.getNSV().getB("hitChance"))
			player.sendMessage("Your max hit: " + maxHit);
		return maxHit;
	}

	public static boolean hasFireCape(Player player) {
		int capeId = player.getEquipment().getCapeId();
		return capeId == 6570 || capeId == 20769 || capeId == 20771;
	}

	public static final boolean fullVanguardEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		int bootsId = player.getEquipment().getBootsId();
		int glovesId = player.getEquipment().getGlovesId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1 || bootsId == -1 || glovesId == -1)
			return false;
		return ItemDefinitions.getDefs(helmId).getName().contains("Vanguard") && ItemDefinitions.getDefs(chestId).getName().contains("Vanguard") && ItemDefinitions.getDefs(legsId).getName().contains("Vanguard")
				&& ItemDefinitions.getDefs(weaponId).getName().contains("Vanguard") && ItemDefinitions.getDefs(bootsId).getName().contains("Vanguard")
				&& ItemDefinitions.getDefs(glovesId).getName().contains("Vanguard");
	}

	public static final boolean usingGoliathGloves(Player player) {
		String name = player.getEquipment().getItem(Equipment.SHIELD) != null ? player.getEquipment().getItem(Equipment.SHIELD).getDefinitions().getName().toLowerCase() : "";
		if (player.getEquipment().getItem((Equipment.HANDS)) != null)
			if (player.getEquipment().getItem(Equipment.HANDS).getDefinitions().getName().toLowerCase().contains("goliath") && player.getEquipment().getWeaponId() == -1) {
				if (name.contains("defender") && name.contains("dragonfire shield"))
					return true;
				return true;
			}
		return false;
	}

	public static final boolean fullVeracsEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getDefs(helmId).getName().contains("Verac's") && ItemDefinitions.getDefs(chestId).getName().contains("Verac's") && ItemDefinitions.getDefs(legsId).getName().contains("Verac's")
				&& ItemDefinitions.getDefs(weaponId).getName().contains("Verac's");
	}

	public static final boolean fullDharokEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getDefs(helmId).getName().contains("Dharok's") && ItemDefinitions.getDefs(chestId).getName().contains("Dharok's") && ItemDefinitions.getDefs(legsId).getName().contains("Dharok's")
				&& ItemDefinitions.getDefs(weaponId).getName().contains("Dharok's");
	}

	public static final boolean fullVoidEquipped(Player player, int... helmid) {
		boolean hasDeflector = player.getEquipment().getShieldId() == 19712;
		if (player.getEquipment().getGlovesId() != 8842) {
			if (!hasDeflector)
				return false;
			hasDeflector = false;
		}
		int legsId = player.getEquipment().getLegsId();
		boolean hasLegs = legsId != -1 && (legsId == 8840 || legsId == 19786 || legsId == 19788 || legsId == 19790);
		if (!hasLegs) {
			if (!hasDeflector)
				return false;
			hasDeflector = false;
		}
		int torsoId = player.getEquipment().getChestId();
		boolean hasTorso = torsoId != -1 && (torsoId == 8839 || torsoId == 10611 || torsoId == 19785 || torsoId == 19787 || torsoId == 19789);
		if (!hasTorso) {
			if (!hasDeflector)
				return false;
			hasDeflector = false;
		}
		int helmId = player.getEquipment().getHatId();
		if (helmId == -1)
			return false;
		boolean hasHelm = false;
		for (int id : helmid)
			if (helmId == id) {
				hasHelm = true;
				break;
			}
		if (!hasHelm)
			return false;
		return true;
	}

	public static void delayNormalHit(Entity target, Hit hit) {
		delayNormalHit(target, hit.getSource() instanceof Player p ? p.getEquipment().getWeaponId() : -1, hit.getSource() instanceof Player p ? p.getCombatDefinitions().getAttackStyle() : null, hit, null, null, null);
	}

	public static void delayNormalHit(Entity target, int weaponId, AttackStyle attackStyle, Hit hit) {
		delayNormalHit(target, weaponId, attackStyle, hit, null, null, null);
	}

	public static void delayNormalHit(Entity target, int weaponId, AttackStyle attackStyle, Hit hit, Runnable afterDelay, Runnable hitSucc, Runnable hitFail) {
		delayHit(target, 0, weaponId, attackStyle, hit, afterDelay, hitSucc, hitFail);
	}

	public static void delayMagicHit(Entity target, int delay, Hit hit, Runnable afterDelay, Runnable hitSucc, Runnable hitFail) {
		delayHit(target, delay, -1, null, hit, afterDelay, hitSucc, hitFail);
	}

	public static void delayHit(Entity target, int delay, int weaponId, AttackStyle attackStyle, Hit hit) {
		delayHit(target, delay, weaponId, attackStyle, hit, null, null, null);
	}

	public static void delayHit(Entity target, int delay, Hit hit) {
		delayHit(target, delay, hit.getSource() instanceof Player p ? p.getEquipment().getWeaponId() : -1, hit.getSource() instanceof Player p ? p.getCombatDefinitions().getAttackStyle() : null, hit, null, null, null);
	}

	public static void delayHit(Entity target, int delay, int weaponId, AttackStyle attackStyle, Hit hit, Runnable afterDelay, Runnable hitSucc, Runnable hitFail) {
		Player player = (Player) hit.getSource();
		addAttackedByDelay(player, target);
		target.applyHit(hit, delay, () -> {
			if (afterDelay != null)
				afterDelay.run();
			target.setNextAnimationNoPriority(new Animation(PlayerCombat.getDefenceEmote(target)));
			if (target instanceof NPC n)
				n.soundEffect(n.getCombatDefinitions().getDefendSound());
			if (target instanceof Player p2) {
				p2.closeInterfaces();
				if (!p2.isLocked() && p2.getCombatDefinitions().isAutoRetaliate() && !p2.getActionManager().hasSkillWorking() && p2.getInteractionManager().getInteraction() == null && !p2.hasWalkSteps())
					p2.getInteractionManager().setInteraction(new PlayerCombatInteraction(p2, player));
			} else {
				NPC n = (NPC) target;
				if (!n.isUnderCombat() || n.canBeAutoRetaliated())
					n.setTarget(player);
			}
		});
		int damage = hit.getDamage() > target.getHitpoints() ? target.getHitpoints() : hit.getDamage();
		if (hit.getMaxHit() > 0 && (damage >= hit.getMaxHit() * 0.90) && (hit.getLook() == HitLook.MAGIC_DAMAGE || hit.getLook() == HitLook.RANGE_DAMAGE || hit.getLook() == HitLook.MELEE_DAMAGE))
			hit.setCriticalMark();
		if (damage > 0) {
			if (hitSucc != null)
				hitSucc.run();
		} else if (hitFail != null)
			hitFail.run();
		addXp(player, target, attackStyle == null ? null : attackStyle.getXpType(), hit);
		checkPoison(player, target, weaponId, hit);
	}

	public static int getSoundId(int weaponId, AttackStyle attackStyle) {
		if (weaponId != -1) {
			String weaponName = ItemDefinitions.getDefs(weaponId).getName().toLowerCase();
			if (weaponName.contains("dart") || weaponName.contains("blisterwood stake") || weaponName.contains("knife"))
				return 2707;
			if (weaponName.contains("crossbow"))
				return (Utils.randomInclusive(0, 1) == 1 ? 2695 : 2696);
			if (weaponName.contains("longbow"))
				return (Utils.randomInclusive(0, 1) == 1 ? 2699 : 2700);
			if (weaponName.contains("shortbow"))
				return (Utils.randomInclusive(0, 1) == 1 ? 2693 : 2699);
		}
		return -1;
	}

	public static void checkPoison(Player player, Entity target, int weaponId, Hit hit) {
		if (hit.getLook() == HitLook.RANGE_DAMAGE || hit.getLook() == HitLook.MELEE_DAMAGE)
			if (hit.getDamage() > 0)
				if (hit.getLook() == HitLook.RANGE_DAMAGE) {
					if (weaponId != -1) {
						String name = ItemDefinitions.getDefs(weaponId).getName();
						if (name.contains("(p++)")) {
							if (Utils.getRandomInclusive(8) == 0)
								target.getPoison().makePoisoned(48);
						} else if (name.contains("(p+)")) {
							if (Utils.getRandomInclusive(8) == 0)
								target.getPoison().makePoisoned(38);
						} else if (name.contains("(p)"))
							if (Utils.getRandomInclusive(8) == 0)
								target.getPoison().makePoisoned(28);
					}
				} else if (weaponId != -1) {
					String name = ItemDefinitions.getDefs(weaponId).getName();
					if (name.contains("(p++)")) {
						if (Utils.getRandomInclusive(8) == 0)
							target.getPoison().makePoisoned(68);
					} else if (name.contains("(p+)")) {
						if (Utils.getRandomInclusive(8) == 0)
							target.getPoison().makePoisoned(58);
					} else if (name.contains("(p)"))
						if (Utils.getRandomInclusive(8) == 0)
							target.getPoison().makePoisoned(48);
				}
	}

	public static void addXpFamiliar(Player player, Entity target, XPType xpType, Hit hit) {
		if (hit.getLook() != HitLook.MAGIC_DAMAGE || hit.getLook() != HitLook.RANGE_DAMAGE || hit.getLook() != HitLook.MELEE_DAMAGE)
			return;
		double combatXp;
		int damage = Utils.clampI(hit.getDamage(), 0, target.getHitpoints());
		double hpXp = (damage / 7.5);
		if (hpXp > 0)
			player.getSkills().addXp(Constants.HITPOINTS, hpXp);
		switch(xpType) {
			case ACCURATE:
				combatXp = (damage / 2.5);
				player.getSkills().addXp(Constants.ATTACK, combatXp);
				break;
			case AGGRESSIVE:
				combatXp = (damage / 2.5);
				player.getSkills().addXp(Constants.STRENGTH, combatXp);
				break;
			case CONTROLLED:
				combatXp = (damage / 2.5);
				player.getSkills().addXp(Constants.ATTACK, combatXp / 3);
				player.getSkills().addXp(Constants.STRENGTH, combatXp / 3);
				player.getSkills().addXp(Constants.DEFENSE, combatXp / 3);
				break;
			case DEFENSIVE:
				combatXp = (damage / 2.5);
				player.getSkills().addXp(Constants.DEFENSE, combatXp);
				break;
			case MAGIC:
				combatXp = (damage / 2.5);
				if (combatXp > 0)
					player.getSkills().addXp(Constants.MAGIC, combatXp);
				break;
			case RANGED:
			case RANGED_DEFENSIVE:
				combatXp = (damage / 2.5);
				if (xpType == XPType.RANGED_DEFENSIVE) {
					player.getSkills().addXp(Constants.RANGE, combatXp / 2);
					player.getSkills().addXp(Constants.DEFENSE, combatXp / 2);
				} else
					player.getSkills().addXp(Constants.RANGE, combatXp);
				break;
			case PRAYER:
				combatXp = (damage / 10.0);
				player.getSkills().addXp(Constants.PRAYER, combatXp);
				break;
			default:
				break;
		}
	}

	public static void addXp(Player player, Entity target, XPType xpType, Hit hit) {
		double combatXp;
		int damage = Utils.clampI(hit.getDamage(), 0, target.getHitpoints());
		switch (hit.getLook()) {
			case MAGIC_DAMAGE:
				combatXp = (damage / 5.0);
				if (combatXp > 0) {
					if (player.getCombatDefinitions().isDefensiveCasting() || (PolyporeStaff.isWielding(player) && player.getCombatDefinitions().getAttackStyle().getAttackType() == AttackType.POLYPORE_LONGRANGE)) {
						double defenceXp = (damage / 7.5);
						if (defenceXp > 0.0) {
							combatXp -= defenceXp;
							player.getSkills().addXp(Constants.DEFENSE, defenceXp);
						}
					}
					if (combatXp > 0.0)
						player.getSkills().addXp(Constants.MAGIC, combatXp);
					//				double hpXp = (hit.getDamage() / 7.5);
					//				if (hpXp > 0)
					//					player.getSkills().addXp(Constants.HITPOINTS, hpXp);
				}
				break;
			case MELEE_DAMAGE:
				combatXp = (damage / 2.5);
				switch (xpType) {
					case ACCURATE:
						player.getSkills().addXp(Constants.ATTACK, combatXp);
						break;
					case AGGRESSIVE:
						player.getSkills().addXp(Constants.STRENGTH, combatXp);
						break;
					case CONTROLLED:
						player.getSkills().addXp(Constants.ATTACK, combatXp / 3);
						player.getSkills().addXp(Constants.STRENGTH, combatXp / 3);
						player.getSkills().addXp(Constants.DEFENSE, combatXp / 3);
						break;
					case DEFENSIVE:
						player.getSkills().addXp(Constants.DEFENSE, combatXp);
						break;
					default:
						break;
				}
				break;
			case RANGE_DAMAGE:
				combatXp = (damage / 2.5);
				if (xpType == XPType.RANGED_DEFENSIVE) {
					player.getSkills().addXp(Constants.RANGE, combatXp / 2);
					player.getSkills().addXp(Constants.DEFENSE, combatXp / 2);
				} else
					player.getSkills().addXp(Constants.RANGE, combatXp);
				break;
			default:
				break;
		}
		double hpXp = (damage / 7.5);
		if (hpXp > 0)
			player.getSkills().addXp(Constants.HITPOINTS, hpXp);
	}

	public static int getWeaponAttackEmote(int weaponId, AttackStyle attackStyle) {
		if (weaponId != -1) {
			if (weaponId == -2) {
				switch (attackStyle.getIndex()) {
					case 1:
						return 14307;
					default:
						return 14393;
				}
			}
			String weaponName = ItemDefinitions.getDefs(weaponId).getName().toLowerCase();
			if (weaponName != null && !weaponName.equals("null")) {
				if (weaponName.contains("boxing gloves"))
					return 3678;
				if (weaponName.contains("staff of light"))
					switch (attackStyle.getIndex()) {
						case 0:
							return 15072;
						case 1:
							return 15071;
						case 2:
							return 414;
					}
				if (weaponName.contains("battleaxe"))
					return 395;
				if (weaponName.contains("staff") || weaponName.contains("wand"))
					return 419;
				if (weaponName.contains("scimitar") || weaponName.contains("korasi's sword") || weaponName.contains("brine sabre"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 15072;
						default:
							return 15071;
					}
				if (weaponName.contains("granite mace"))
					return 400;
				if (weaponName.contains("mace"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 400;
						default:
							return 401;
					}
				if (weaponName.contains("hatchet"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 401;
						default:
							return 395;
					}
				if (weaponName.contains("warhammer"))
					switch (attackStyle.getIndex()) {
						default:
							return 401;
					}
				if (weaponName.contains("claws"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 1067;
						default:
							return 393;
					}
				if (weaponName.contains("whip"))
					switch (attackStyle.getIndex()) {
						case 1:
							return 11969;
						case 2:
							return 11970;
						default:
							return 11968;
					}
				if (weaponName.contains("anchor"))
					switch (attackStyle.getIndex()) {
						default:
							return 5865;
					}
				if (weaponName.contains("tzhaar-ket-em"))
					switch (attackStyle.getIndex()) {
						default:
							return 401;
					}
				if (weaponName.contains("tzhaar-ket-om"))
					switch (attackStyle.getIndex()) {
						default:
							return 13691;
					}
				if (weaponName.contains("halberd") || weaponName.contains("blisterwood polearm"))
					switch (attackStyle.getIndex()) {
						case 1:
							return 440;
						default:
							return 428;
					}
				if (weaponName.contains("zamorakian spear"))
					switch (attackStyle.getIndex()) {
						case 1:
							return 12005;
						case 2:
							return 12009;
						default:
							return 12006;
					}
				if (weaponName.contains("spear"))
					switch (attackStyle.getIndex()) {
						case 1:
							return 440;
						case 2:
							return 429;
						default:
							return 428;
					}
				if (weaponName.contains("flail"))
					return 2062;
				if (weaponName.contains("pickaxe"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 400;
						default:
							return 401;
					}
				if (weaponName.contains("dragon dagger"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 377;
						default:
							return 376;
					}
				if (weaponName.contains("dagger") || weaponName.contains("wolfbane"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 390;
						default:
							return 400;
					}
				if (weaponName.contains("2h sword") || weaponName.equals("dominion sword") || weaponName.equals("thok's sword") || weaponName.contains("saradomin sword") || weaponName.contains("keenblade"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 7048;
						case 3:
							return 7049;
						default:
							return 7041;
					}
				if (weaponName.contains(" sword"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 12311;
						default:
							return 12310;
					}
				if (weaponName.contains("saber") || weaponName.contains("longsword") || weaponName.contains("light") || weaponName.contains("excalibur"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 12310;
						default:
							return 12311;
					}
				if (weaponName.contains("rapier") || weaponName.contains("brackish"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 13048;
						default:
							return 13049;
					}
				if (weaponName.contains("katana"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 1882;
						default:
							return 1884;
					}
				if (weaponName.contains("godsword"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 11980;
						case 3:
							return 11981;
						default:
							return 11979;
					}
				if (weaponName.contains("greataxe") || weaponName.contains("balmung"))
					switch (attackStyle.getIndex()) {
						case 2:
							return 12003;
						default:
							return 12002;
					}
				if (weaponName.contains("granite maul"))
					switch (attackStyle.getIndex()) {
						default:
							return 1665;
					}

				if (weaponName.contains(" maul"))
					return 2661;

			}
		}
		switch (weaponId) {
			default:
				switch (attackStyle.getIndex()) {
					case 1:
						return 423;
					default:
						return 422; // todo default emote
				}
		}
	}

	public static int getMeleeCombatDelay(Player player, int weaponId) {
		if (weaponId != -1) {
			String weaponName = ItemDefinitions.getDefs(weaponId).getName().toLowerCase();

			// Interval 2.4
			if (weaponName.equals("zamorakian spear") || weaponName.equals("korasi's sword") || weaponName.contains("saradomin sword") || weaponName.contains("keenblade"))
				return 3;
			// Interval 3.6
			if (weaponName.contains("godsword") || weaponName.contains("warhammer") || weaponName.contains("battleaxe") || weaponName.contains("maul") || weaponName.equals("dominion sword"))
				return 5;
			// Interval 4.2
			if (weaponName.contains("greataxe") || weaponName.contains("halberd") || weaponName.contains("2h sword") || weaponName.contains("two handed sword") || weaponName.contains("katana") || weaponName.equals("thok's sword"))
				return 6;
			// Interval 3.0
			if (weaponName.contains("spear") || weaponName.contains(" sword") || weaponName.contains("longsword") || weaponName.contains("light") || weaponName.contains("hatchet") || weaponName.contains("pickaxe") || weaponName.contains("mace")
					|| weaponName.contains("hasta") || weaponName.contains("warspear") || weaponName.contains("flail") || weaponName.contains("hammers"))
				return 4;
		}
		switch (weaponId) {
			case 6527:// tzhaar-ket-em
				return 4;
			case 10887:// barrelchest anchor
				return 5;
			case 15403:// balmung
			case 6528:// tzhaar-ket-om
				return 6;
			default:
				return 3;
		}
	}

	@Override
	public void stop(Player player) {
		player.setNextFaceEntity(null);
		player.getInteractionManager().forceStop();
		player.getActionManager().forceStop();
		player.getTempAttribs().removeO("combatTarget");
	}

	public boolean checkAll(Player player) {
		if (target.isDead())
			return false;
		if (!player.canAttackMulti(target) || !target.canAttackMulti(player))
			return false;
		if (target instanceof Player p2) {
			if (!player.isCanPvp() || !p2.isCanPvp())
				return false;
		} else {
			NPC n = (NPC) target;
			if (n.isCantInteract())
				return false;
			if (n instanceof Familiar familiar) {
				if (!familiar.canAttack(target))
					return false;
			} else if (isAttackExeption(player, n))
				return false;
		}
		if (player.hasEffect(Effect.STAFF_OF_LIGHT_SPEC) && !(player.getEquipment().getWeaponId() == 15486 || player.getEquipment().getWeaponId() == 22207 || player.getEquipment().getWeaponId() == 22209 || player.getEquipment().getWeaponId() == 22211 || player.getEquipment().getWeaponId() == 22213))
			player.removeEffect(Effect.STAFF_OF_LIGHT_SPEC);
		player.getTempAttribs().setO("last_target", target);
		if (target != null)
			target.getTempAttribs().setO("last_attacker", player);
		return true;
	}

	public static boolean isRanging(Player player) {
		int weaponId = player.getEquipment().getWeaponId();
		if (player.getTempAttribs().getB("dfsActive") || (player.getCombatDefinitions().getSpell() == null && PolyporeStaff.isWielding(player)))
			return true;
		if (weaponId == -1 && player.getCombatDefinitions().getSpell() == null)
			return false;
		return RangedWeapon.forId(weaponId) != null;
	}

	public static boolean isMeleeing(Player player) {
		return !isRanging(player) && player.getCombatDefinitions().getSpell() == null;
	}

	public static int getAttackRange(Player player) {
		if (player.getCombatDefinitions().getSpell() != null)
			return 10;
		if (isRanging(player)) {
			int atkRange = 8;
			String weaponName = player.getEquipment().getWeaponName().toLowerCase();
			if (weaponName.contains("swamp lizard") || weaponName.contains("salamander"))
				return 1;
			if (weaponName.contains(" dart") || weaponName.contains("blisterwood stake"))
				atkRange = 3;
			else if (weaponName.contains(" knife"))
				atkRange = 4;
			else if (weaponName.contains(" thrownaxe"))
				atkRange = 4;
			else if (weaponName.contains(" comp ogre bow"))
				atkRange = 5;
			else if (weaponName.contains("dorgeshuun c'bow"))
				atkRange = 6;
			else if (weaponName.contains(" crossbow"))
				atkRange = 7;
			else if (weaponName.contains(" shortbow"))
				atkRange = 7;
			else if (weaponName.contains(" karil"))
				atkRange = 8;
			else if (weaponName.contains("seercull"))
				atkRange = 8;
			else if (weaponName.contains(" longbow"))
				atkRange = 9;
			else if (weaponName.contains("chinchompa"))
				atkRange = 9;
			else if (weaponName.contains("ogre bow"))
				atkRange = 10;
			else if (weaponName.contains("composite bow"))
				atkRange = 10;
			else if (weaponName.contains("crystal bow"))
				atkRange = 10;
			else if (weaponName.contains("dark bow"))
				atkRange = 10;

			if (player.getCombatDefinitions().getAttackStyle().getAttackType() == AttackType.LONG_RANGE)
				atkRange += 2;
			return Utils.clampI(atkRange, 0, 10);
		}
		if (player.getEquipment().getWeaponId() != -1 && ItemDefinitions.getDefs(player.getEquipment().getWeaponId()).name.contains("halberd"))
			return 1;
		return 0;
	}

	private boolean isAttackExeption(Player player, NPC n) {
		return !n.canBeAttackedBy(player);
	}

	public Entity getTarget() {
		return target;
	}

	private static void chargeDragonfireShield(Entity target) {
		if (target instanceof Player p) {
			int shield = p.getEquipment().getShieldId();
			if (shield == 11283 || shield == 11284) {
				if (shield == 11284) {
					p.getEquipment().replace(p.getEquipment().getItem(Equipment.SHIELD), new Item(11283, 1).addMetaData("dfsCharges", 1));
					p.getAppearance().generateAppearanceData();
					p.getEquipment().refresh(Equipment.SHIELD);
				}
				Item dfs = p.getEquipment().getItem(Equipment.SHIELD);
				if (dfs != null) {
					int charges = dfs.getMetaDataI("dfsCharges");
					if (charges < 50) {
						p.getEquipment().getItem(Equipment.SHIELD).addMetaData("dfsCharges", charges + 1);
						p.setNextAnimation(new Animation(6695));
						p.setNextSpotAnim(new SpotAnim(1164));
						p.sendMessage("Your shield becomes a little stronger as it absorbs the dragonfire.", true);
						p.getCombatDefinitions().refreshBonuses();
					}
				}
			}
		}
	}

	public static int getDefenceEmote(Entity target) {
		if (target instanceof NPC n)
			return n.getCombatDefinitions().getDefenceEmote();
		Player p = (Player) target;
		int shieldId = p.getEquipment().getShieldId();
		String shieldName = shieldId == -1 ? null : ItemDefinitions.getDefs(shieldId).getName().toLowerCase();
		if (shieldId == -1 || (shieldName.contains("book") && shieldId != 18346)) {
			int weaponId = p.getEquipment().getWeaponId();
			if (weaponId == -1)
				return 424;
			String weaponName = ItemDefinitions.getDefs(weaponId).getName().toLowerCase();
			if (weaponName != null && !weaponName.equals("null")) {
				if (weaponName.contains("boxing gloves"))
					return 3679;
				if (weaponName.contains("scimitar") || weaponName.contains("korasi sword"))
					return 15074;
				if (weaponName.contains("whip"))
					return 11974;
				if (weaponName.contains("staff of light"))
					return 12806;
				if (weaponName.contains("longsword") || weaponName.contains("darklight") || weaponName.contains("silverlight") || weaponName.contains("excalibur"))
					return 388;
				if (weaponName.contains("dagger"))
					return 378;
				if (weaponName.contains("rapier"))
					return 13038;
				if (weaponName.contains("pickaxe"))
					return 397;
				if (weaponName.contains("mace"))
					return 403;
				if (weaponName.contains("claws"))
					return 404;
				if (weaponName.contains("hatchet"))
					return 397;
				if (weaponName.contains("greataxe"))
					return 12004;
				if (weaponName.contains("wand"))
					return 415;
				if (weaponName.contains("chaotic staff"))
					return 13046;
				if (weaponName.contains("staff"))
					return 420;
				if (weaponName.contains("warhammer") || weaponName.contains("tzhaar-ket-em"))
					return 403;
				if (weaponName.contains("maul") || weaponName.contains("tzhaar-ket-om"))
					return 1666;
				if (weaponName.contains("zamorakian spear"))
					return 12008;
				if (weaponName.contains("spear") || weaponName.contains("halberd") || weaponName.contains("hasta"))
					return 430;
				if (weaponName.contains("2h sword") || weaponName.contains("godsword") || weaponName.equals("saradomin sword"))
					return 7050;
			}
			return 424;
		}
		if (shieldName != null) {
			if (shieldName.contains("shield") || shieldName.contains("-ket-xil"))
				return 1156;
			if (shieldName.contains("defender"))
				return 4177;
		}
		switch (shieldId) {
			case -1:
			default:
				return 424;
		}
	}

	public static int getSlayerLevelForNPC(int id) {
		switch (id) {
			case 9463:
				return 93;
			default:
				return 0;
		}
	}

	public static int getAntifireLevel(Entity target, boolean prayerWorks) {
		if (!(target instanceof Player))
			return 0;
		int protection = 0;
		Player p2 = (Player) target;
		if (p2.hasEffect(Effect.SUPER_ANTIFIRE)) {
			p2.sendMessage("Your potion heavily protects you from the dragon's fire.", true);
			protection = 2;
			return protection;
		}
		int shieldId = p2.getEquipment().getShieldId();
		if (shieldId == 1540 || shieldId == 11283 || shieldId == 11284 || shieldId == 16079 || shieldId == 16933) {
			protection++;
			p2.sendMessage("Your shield manages to block some of the dragon's breath.", true);
		}
		if (protection == 0 && prayerWorks ? p2.getPrayer().isProtectingMage() : false) {
			p2.sendMessage("Your prayers help resist some of the dragonfire!", true);
			protection++;
		}
		if (p2.hasEffect(Effect.ANTIFIRE)) {
			p2.sendMessage("Your potion slightly protects you from the heat of the dragon's breath.", true);
			protection++;
		}
		if (protection > 2)
			protection = 2;
		if (protection == 0)
			p2.sendMessage("You are hit by the dragon's fiery breath.", true);
		chargeDragonfireShield(target);
		return protection;
	}
}
