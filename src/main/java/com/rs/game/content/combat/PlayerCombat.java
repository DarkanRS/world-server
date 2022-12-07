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
import java.lang.SuppressWarnings;

import com.rs.Settings;
import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.skills.dungeoneering.DungeonController;
import com.rs.game.content.skills.dungeoneering.KinshipPerk;
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
import com.rs.game.region.Region;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class PlayerCombat extends PlayerAction {

	private Entity target;
	private int max_hit;
	private CombatSpell spellcasterGloveSpell;

	public static ItemClickHandler handleDFS = new ItemClickHandler(new Object[]{"Dragonfire shield"}, new String[]{"Inspect", "Activate", "Empty"}) {
		@Override
		public void handle(ItemClickEvent e) {
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
		}
	};

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
		addAttackedByDelay(player);
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
			delayMagicHit(p.getTaskDelay(), new Hit(player, Utils.random(100, 250), HitLook.TRUE_DAMAGE), () -> {
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
			Hit hit = getMagicHit(player, getRandomMagicMaxHit(player, (5 * player.getSkills().getLevel(Constants.MAGIC)) - 180, false));
			delayMagicHit(p.getTaskDelay(), hit, () -> {
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
			final int weaponId = player.getEquipment().getWeaponId();
			if (player.getCombatDefinitions().isUsingSpecialAttack()) {
				int specAmt = getSpecialAmmount(weaponId);
				if (specAmt == 0) {
					player.sendMessage("This weapon has no special Attack, if you still see special bar please relogin.");
					player.getCombatDefinitions().drainSpec(0);
					return 3;
				}
				if (player.getCombatDefinitions().hasRingOfVigour())
					specAmt *= 0.9;
				if (player.getCombatDefinitions().getSpecialAttackPercentage() < specAmt) {
					player.sendMessage("You don't have enough power left.");
					player.getCombatDefinitions().drainSpec(0);
					return 3;
				}
				player.getCombatDefinitions().drainSpec(specAmt);
				switch (weaponId) {
					case 23044, 23045, 23046, 23047 -> {
						delayMagicHit(CombatSpell.WIND_RUSH.cast(player,  target), getMagicHit(player, 50), () -> target.setNextSpotAnim(CombatSpell.WIND_RUSH.getHitSpotAnim()), null, null);
						return 3;
					}
				}
			}
			boolean manualCast = player.getCombatDefinitions().hasManualCastQueued();
			Item gloves = player.getEquipment().getItem(Equipment.HANDS);
			spellcasterGloveSpell = gloves != null && gloves.getDefinitions().getName().contains("Spellcaster glove") && player.getEquipment().getWeaponId() == -1 && new Random().nextInt(30) == 0 ? spell : null;
			int delay = mageAttack(player, spell, !manualCast);
			if (player.getNextAnimation() != null && spellcasterGloveSpell != null) {
				player.setNextAnimation(new Animation(14339));
				spellcasterGloveSpell = null;
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

	private void addAttackedByDelay(Entity player) {
		target.setAttackedBy(player);
		target.setAttackedByDelay(System.currentTimeMillis() + 8000); // 8seconds
	}

	private int getRangeCombatDelay(RangedWeapon weapon, AttackStyle attackStyle) {
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
	
	public static Entity[] getMultiAttackTargets(Player player, WorldTile tile, int maxDistance, int maxAmtTargets) {
		List<Entity> possibleTargets = new ArrayList<>();
		if (!player.isAtMultiArea()) {
			Entity target = player.getTempAttribs().getO("last_target");
			if (target != null && !target.isDead() && !target.hasFinished() && target.withinDistance(tile, maxDistance) && (!(target instanceof NPC n) || n.getDefinitions().hasAttackOption()))
				possibleTargets.add(target);
			return possibleTargets.toArray(new Entity[possibleTargets.size()]);
		}
		y: for (int regionId : player.getMapRegionsIds()) {
			Region region = World.getRegion(regionId);
			Set<Integer> playerIndexes = region.getPlayerIndexes();
			if (playerIndexes == null)
				continue;
			for (int playerIndex : playerIndexes) {
				Player p2 = World.getPlayers().get(playerIndex);
				if (p2 == null || p2 == player || p2.isDead() || !p2.hasStarted() || p2.hasFinished() || !p2.isCanPvp() || !p2.isAtMultiArea() || !p2.withinDistance(tile, maxDistance) || !player.getControllerManager().canHit(p2))
					continue;
				possibleTargets.add(p2);
				if (possibleTargets.size() == maxAmtTargets)
					break y;
			}
			Set<Integer> npcIndexes = region.getNPCsIndexes();
			if (npcIndexes == null)
				continue;
			for (int npcIndex : npcIndexes) {
				NPC n = World.getNPCs().get(npcIndex);
				if (n == null || n == player.getFamiliar() || n.isDead() || n.hasFinished() || !n.isAtMultiArea() || !n.withinDistance(tile, maxDistance) || !n.getDefinitions().hasAttackOption() || !player.getControllerManager().canHit(n) || !n.isAtMultiArea())
					continue;
				possibleTargets.add(n);
				if (possibleTargets.size() == maxAmtTargets)
					break y;
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
		if (target.isAtMultiArea())
			y:for (int regionId : target.getMapRegionsIds()) {
				Region region = World.getRegion(regionId);
				if (target instanceof Player) {
					Set<Integer> playerIndexes = region.getPlayerIndexes();
					if (playerIndexes == null)
						continue;
					for (int playerIndex : playerIndexes) {
						Player p2 = World.getPlayers().get(playerIndex);
						if (p2 == null || p2 == player || p2 == target || p2.isDead() || !p2.hasStarted() || p2.hasFinished() || !p2.isCanPvp() || !p2.isAtMultiArea() || !p2.withinDistance(target.getTile(), maxDistance)
								|| !player.getControllerManager().canHit(p2))
							continue;
						possibleTargets.add(p2);
						if (possibleTargets.size() == maxAmtTargets)
							break y;
					}
				} else {
					Set<Integer> npcIndexes = region.getNPCsIndexes();
					if (npcIndexes == null)
						continue;
					for (int npcIndex : npcIndexes) {
						NPC n = World.getNPCs().get(npcIndex);
						if (n == null || n == target || n == player.getFamiliar() || n.isDead() || n.hasFinished() || !n.isAtMultiArea() || !n.withinDistance(target.getTile(), maxDistance) || !n.getDefinitions().hasAttackOption()
								|| !player.getControllerManager().canHit(n))
							continue;
						possibleTargets.add(n);
						if (possibleTargets.size() == maxAmtTargets)
							break y;
					}
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
			int hit = calculateMagicHit(player, 1000);
			if (hit > 0)
				spell.onHit(player, target, null);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					if (hit > 0) {
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
				attackTarget(getMultiAttackTargets(player, target), new MultiAttack() {
					private boolean nextTarget;

					@Override
					public boolean attack() {
						if (!nextTarget)
							nextTarget = true;
						else
							castSpellAtTarget(player, target, spell, delay);
						return nextTarget;
					}
				});
		}
		return spell.getCombatDelay(player);
	}

	public boolean castSpellAtTarget(Player player, Entity target, CombatSpell spell, int hitDelay) {
		Hit hit = getMagicHit(player, getRandomMagicMaxHit(player, spell.getBaseDamage(player)));
		if (spell == CombatSpell.STORM_OF_ARMADYL && hit.getDamage() > 0) {
			int minHit = (player.getSkills().getLevelForXp(Constants.MAGIC) - 77) * 5;
			if (hit.getDamage() < minHit)
				hit.setDamage(hit.getDamage() + minHit);
		}
		hit.setData("combatSpell", spell);
		boolean sparkle = target.getSize() >= 2 || target.hasEffect(Effect.FREEZE) || target.hasEffect(Effect.FREEZE_BLOCK);
		delayMagicHit(hitDelay, hit, () -> {
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
		public boolean attack();
	}

	public void attackTarget(Entity[] targets, MultiAttack perform) {
		Entity realTarget = target;
		for (Entity t : targets) {
			target = t;
			if (!perform.attack())
				break;
		}
		target = realTarget;
	}

	private int rangeAttack(final Player player) {
		final int weaponId = player.getEquipment().getWeaponId();
		final AttackStyle attackStyle = player.getCombatDefinitions().getAttackStyle();
		int soundId = getSoundId(weaponId, attackStyle);
		RangedWeapon weapon = RangedWeapon.forId(weaponId);
		AmmoType ammo = AmmoType.forId(player.getEquipment().getAmmoId());
		int combatDelay = getRangeCombatDelay(weapon, attackStyle);
		if (player.getCombatDefinitions().isUsingSpecialAttack()) {
			int specAmt = getSpecialAmmount(weaponId);
			if (specAmt == 0) {
				player.sendMessage("This weapon has no special Attack, if you still see special bar please relogin.");
				player.getCombatDefinitions().drainSpec(0);
				return combatDelay;
			}
			if (player.getCombatDefinitions().hasRingOfVigour())
				specAmt *= 0.9;
			if (player.getCombatDefinitions().getSpecialAttackPercentage() < specAmt) {
				player.sendMessage("You don't have enough power left.");
				player.getCombatDefinitions().drainSpec(0);
				return combatDelay;
			}
			player.getCombatDefinitions().drainSpec(specAmt);
			switch (weapon) {
				case QUICK_BOW -> {
					player.setNextAnimation(new Animation(426));
					player.setNextSpotAnim(new SpotAnim(97));
					WorldProjectile p = World.sendProjectile(player, target, 1099, 20, 50, 1, proj -> target.setNextSpotAnim(new SpotAnim(1100, 0, 100)));
					WorldProjectile p2 = World.sendProjectile(player, target, 1099, 30, 50, 1.5, proj -> target.setNextSpotAnim(new SpotAnim(1100, 0, 100)));
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, 25));
					delayHit(p2.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, 25));
				}
				case ZAMORAK_BOW -> {
					player.setNextAnimation(new Animation(426));
					player.setNextSpotAnim(new SpotAnim(97));
					WorldProjectile p = World.sendProjectile(player, target, 100, 20, 50, 1.5);
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
					dropAmmo(player, Equipment.AMMO, 1);
				}
				case GUTHIX_BOW -> {
					player.setNextAnimation(new Animation(426));
					player.setNextSpotAnim(new SpotAnim(95));
					WorldProjectile p = World.sendProjectile(player, target, 98, 20, 50, 1.5);
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
					dropAmmo(player, Equipment.AMMO, 1);
				}
				case SARADOMIN_BOW -> {
					player.setNextAnimation(new Animation(426));
					player.setNextSpotAnim(new SpotAnim(96));
					WorldProjectile p = World.sendProjectile(player, target, 99, 20, 50, 1.5);
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
					dropAmmo(player, Equipment.AMMO, 1);
				}
				case RUNE_THROWNAXE -> {
					player.setNextAnimation(new Animation(9055));
					WorldProjectile p1 = World.sendProjectile(player, target, 258, 20, 50, 1);
					delayHit(p1.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
					WorldTasks.schedule(p1.getTaskDelay(), () -> {
						for (Entity next : getMultiAttackTargets(player, target, 5, 1, false)) {
							WorldProjectile p2 = World.sendProjectile(target, next, 258, 20, 50, 1);
							WorldTasks.schedule(p2.getTaskDelay(), () -> {
								next.applyHit(getRangeHit(player, getRandomMaxHit(player, next, weaponId, attackStyle, true)));
								for (Entity next2 : getMultiAttackTargets(player, next, 5, 1, false)) {
									WorldProjectile p3 = World.sendProjectile(next, next2, 258, 20, 50, 1);
									WorldTasks.schedule(p3.getTaskDelay(), () -> {
										next2.applyHit(getRangeHit(player, getRandomMaxHit(player, next2, weaponId, attackStyle, true)));
										for (Entity next3 : getMultiAttackTargets(player, next2, 5, 1, false)) {
											WorldProjectile p4 = World.sendProjectile(next2, next3, 258, 20, 50, 1);
											WorldTasks.schedule(p4.getTaskDelay(), () -> {
												next3.applyHit(getRangeHit(player, getRandomMaxHit(player, next3, weaponId, attackStyle, true)));
												for (Entity next4 : getMultiAttackTargets(player, next3, 5, 1, false)) {
													WorldProjectile p5 = World.sendProjectile(next3, next4, 258, 20, 50, 1);
													WorldTasks.schedule(p5.getTaskDelay(), () -> {
														next4.applyHit(getRangeHit(player, getRandomMaxHit(player, next4, weaponId, attackStyle, true)));	
													});
												}
											});
										}
									});
								}
							});
						}
					});
				}
				case MAGIC_BOW, MAGIC_LONGBOW, MAGIC_COMP_BOW -> {
					player.setNextAnimation(new Animation(1074));
					player.setNextSpotAnim(new SpotAnim(250, 10, 100));
					WorldProjectile p = World.sendProjectile(player, target, 249, 20, 20, 2);
					WorldProjectile p2 = World.sendProjectile(player, target, 249, 15, 50, 1.6);
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
					delayHit(p2.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
					dropAmmo(player, Equipment.AMMO, 2);
				}
				case HAND_CANNON -> {
					player.setNextAnimation(new Animation(12175));
					player.setNextSpotAnim(new SpotAnim(2138));
					WorldProjectile p = World.sendProjectile(player, target, 2143, 0, 50, 1.5);
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
					combatDelay = 1;
				}
				case DORGESHUUN_CBOW -> {
					player.setNextAnimation(weapon.getAttackAnimation());
					SpotAnim attackSpotAnim = weapon.getAttackSpotAnim(player, ammo);
					if (attackSpotAnim != null)
						player.setNextSpotAnim(attackSpotAnim);
					int damage = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, 1.3);
					WorldProjectile p = World.sendProjectile(player, target, 698, 20, 50, 1);
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, damage));
					dropAmmo(player, Equipment.AMMO, 1);
				}
				case DARK_BOW -> {
					int ammoId = player.getEquipment().getAmmoId();
					player.setNextAnimation(weapon.getAttackAnimation());
					SpotAnim attackSpotAnim = weapon.getAttackSpotAnim(player, ammo);
					if (attackSpotAnim != null)
						player.setNextSpotAnim(attackSpotAnim);
					if (ammoId == 11212) {
						int damage = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, 1.5);
						if (damage < 80)
							damage = 80;
						int damage2 = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, 1.5);
						if (damage2 < 80)
							damage2 = 80;
						WorldProjectile p = World.sendProjectile(player, target, 1099, 20, 50, 1, proj -> target.setNextSpotAnim(new SpotAnim(1100, 0, 100)));
						WorldProjectile p2 = World.sendProjectile(player, target, 1099, 30, 50, 1.5, proj -> target.setNextSpotAnim(new SpotAnim(1100, 0, 100)));
						delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, damage));
						delayHit(p2.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, damage2));
					} else {
						int damage = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, 1.3);
						if (damage < 50)
							damage = 50;
						int damage2 = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, 1.3);
						if (damage2 < 50)
							damage2 = 50;
						WorldProjectile p = World.sendProjectile(player, target, 1101, 20, 50, 1);
						WorldProjectile p2 = World.sendProjectile(player, target, 1101, 30, 50, 1.5);
						delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, damage));
						delayHit(p2.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, damage2));
					}
					dropAmmo(player, Equipment.AMMO, 2);
				}
				case ZANIKS_CROSSBOW -> {
					player.setNextAnimation(new Animation(getWeaponAttackEmote(weaponId, attackStyle)));
					player.setNextSpotAnim(new SpotAnim(1714));
					WorldProjectile p = World.sendProjectile(player, target, 2001, 20, 50, 1.5);
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, 1.0) + 30 + Utils.getRandomInclusive(120)));
					dropAmmo(player);
				}
				case MORRIGANS_JAVELIN -> {
					player.setNextSpotAnim(new SpotAnim(1836));
					player.setNextAnimation(new Animation(10501));
					WorldProjectile p = World.sendProjectile(player, target, 1837, 20, 50, 1.5);
					final int hit = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, 1.0);
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, hit));
					if (hit > 0) {
						final Entity finalTarget = target;
						WorldTasks.schedule(new WorldTask() {
							int damage = hit;

							@Override
							public void run() {
								if (finalTarget.isDead() || finalTarget.hasFinished()) {
									stop();
									return;
								}
								if (damage > 50) {
									damage -= 50;
									finalTarget.applyHit(new Hit(player, 50, HitLook.TRUE_DAMAGE));
								} else {
									finalTarget.applyHit(new Hit(player, damage, HitLook.TRUE_DAMAGE));
									stop();
								}
							}
						}, 4, 2);
					}
					dropAmmo(player, Equipment.WEAPON, 1);
				}
				case MORRIGANS_THROWING_AXE -> {
					player.setNextSpotAnim(new SpotAnim(1838));
					player.setNextAnimation(new Animation(10504));
					WorldProjectile p = World.sendProjectile(player, target, 1839, 20, 50, 1.5);
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, 1.0)));
					dropAmmo(player, Equipment.WEAPON, 1);
				}
				default -> {
					player.sendMessage("This weapon has no special Attack, if you still see special bar please relogin.");
					return combatDelay;
				}
			}
		} else {
			WorldProjectile p = weapon.getProjectile(player, target);
			switch (weapon) {
				case DEATHTOUCHED_DART:
					player.setNextAnimation(weapon.getAttackAnimation());
					target.setNextSpotAnim(new SpotAnim(44));
					target.resetWalkSteps();
					if (target instanceof NPC npc) {
						WorldTasks.delay(p.getTaskDelay(), () -> {
							npc.setCapDamage(-1);
							target.applyHit(new Hit(player, target.getHitpoints(), HitLook.TRUE_DAMAGE));
						});
						dropAmmo(player, Equipment.WEAPON, 1);
						return 8;
					} else
						return 0;
				case CHINCHOMPA:
				case RED_CHINCHOMPA:
					attackTarget(getMultiAttackTargets(player, target), new MultiAttack() {
						private boolean nextTarget;

						@Override
						public boolean attack() {
							int damage = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, weaponId == 10034 ? 1.2 : 1.0);
							player.setNextAnimation(new Animation(2779));
							WorldTasks.delay(p.getTaskDelay(), () -> target.setNextSpotAnim(new SpotAnim(2739, 0, 96 << 16)));
							delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, damage));
							if (!nextTarget) {
								if (damage == -1)
									return false;
								nextTarget = true;
							}
							return nextTarget;
						}
					});
					dropAmmo(player, Equipment.WEAPON, 1);
					break;
				case CROSSBOW:
				case BRONZE_CROSSBOW:
				case BLURITE_CROSSBOW:
				case IRON_CROSSBOW:
				case STEEL_CROSSBOW:
				case BLACK_CROSSBOW:
				case MITH_CROSSBOW:
				case ADAMANT_CROSSBOW:
				case RUNE_CROSSBOW:
				case ARMADYL_CROSSBOW:
				case CHAOTIC_CROSSBOW:
				case ZANIKS_CROSSBOW:
					int damage = 0;
					boolean specced = false;
					if (player.getEquipment().getAmmoId() == 9241 && Utils.random(100) <= 55 && !target.getPoison().isPoisoned()) {
						target.setNextSpotAnim(new SpotAnim(752));
						target.getPoison().makePoisoned(50);
						specced = true;
					}
					if (player.getEquipment().getAmmoId() != -1 && Utils.getRandomInclusive(10) == 0) {
						switch (player.getEquipment().getAmmoId()) {
							case 9237:
								damage = getRandomMaxHit(player, weaponId, attackStyle, true);
								target.setNextSpotAnim(new SpotAnim(755));
								if (target instanceof Player p2)
									p2.stopAll();
								else if (target instanceof NPC n)
									n.setTarget(null);
								soundId = 2914;
								break;
							case 9242:
								max_hit = Short.MAX_VALUE;
								damage = (int) (target.getHitpoints() * 0.2);
								target.setNextSpotAnim(new SpotAnim(754));
								player.applyHit(new Hit(target, player.getHitpoints() > 20 ? (int) (player.getHitpoints() * 0.1) : 1, HitLook.REFLECTED_DAMAGE));
								soundId = 2912;
								break;
							case 9243:
								damage = getRandomMaxHit(player, weaponId, attackStyle, true, false, 1.0, 1.15);
								target.setNextSpotAnim(new SpotAnim(751));
								soundId = 2913;
								break;
							case 9244:
								damage = getRandomMaxHit(player, weaponId, attackStyle, true, false, 1.0, PlayerCombat.getAntifireLevel(target, true) > 0 ? 1.45 : 1.0);
								target.setNextSpotAnim(new SpotAnim(756));
								soundId = 2915;
								break;
							case 9245:
								damage = getRandomMaxHit(player, weaponId, attackStyle, true, false, 1.0, 1.15);
								target.setNextSpotAnim(new SpotAnim(753));
								player.heal((int) (player.getMaxHitpoints() * 0.25));
								soundId = 2917;
								break;
							default:
								damage = getRandomMaxHit(player, weaponId, attackStyle, true);
						}
						specced = true;
					} else {
						damage = getRandomMaxHit(player, weaponId, attackStyle, true);
						checkSwiftGlovesEffect(player, p.getTaskDelay(), attackStyle, weaponId, damage, p);
					}
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, damage));
					if (specced)
						player.getEquipment().removeAmmo(Equipment.AMMO, 1);
					else
						dropAmmo(player, Equipment.AMMO, 1);
					break;
				case ROYAL_CROSSBOW:
					if (target instanceof Player) {
						damage = Utils.random(0, 60);
						player.sendMessage("The Royal crossbow seems unresponsive against this target.", true);
					} else {
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
						damage = getRandomMaxHit(player, weaponId, attackStyle, true);
						delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, damage), null, () -> {
							if (weaponId == 24339 || target.isDead() || target.hasFinished())
								return;
							int maxHit = getMaxHit(player, target, weaponId, attackStyle, true, 1.0);
							int minBleed = (int) (maxHit * (lockedOn ? 0.25 : 0.20));
							int maxBleed = (int) (minBleed * 0.70);
							WorldTasks.delay(14, () -> {
								if (target == null || target.isDead() || target.hasFinished())
									return;
								delayHit(0, weaponId, attackStyle, getRangeHit(player, Utils.random(minBleed, maxBleed)));
							});
							WorldTasks.delay(28, () -> {
								if (target == null || target.isDead() || target.hasFinished())
									return;
								delayHit(0, weaponId, attackStyle, getRangeHit(player, Utils.random(minBleed, maxBleed)));
							});
						}, null);
						checkSwiftGlovesEffect(player, p.getTaskDelay(), attackStyle, weaponId, damage, p);
					}
					player.getEquipment().removeAmmo(Equipment.AMMO, 1);
					break;
				case HAND_CANNON:
					if (Utils.getRandomInclusive(player.getSkills().getLevel(Constants.FIREMAKING) << 1) == 0) {
						player.setNextSpotAnim(new SpotAnim(2140));
						player.getEquipment().deleteSlot(Equipment.WEAPON);
						player.getAppearance().generateAppearanceData();
						player.applyHit(new Hit(player, Utils.getRandomInclusive(150) + 10, HitLook.TRUE_DAMAGE));
						player.setNextAnimation(new Animation(12175));
						return combatDelay;
					}
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
					dropAmmo(player, Equipment.AMMO, 1);
					break;
				case SAGAIE:
					double damageMod = Utils.clampD((Utils.getDistanceI(player.getTile(), target.getMiddleWorldTile()) / (double) getAttackRange(player)) * 0.70, 0.01, 1.0);
					damage = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0D - (damageMod * 0.95), 1.0D + damageMod);
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, damage));
					checkSwiftGlovesEffect(player, p.getTaskDelay(), attackStyle, weaponId, damage, p);
					dropAmmo(player, Equipment.WEAPON, 1);
					break;
				case BOLAS:
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
					if (getRandomMaxHit(player, weaponId, attackStyle, true) > 0) {
						target.freeze(delay, true);
						WorldTasks.schedule(2, () -> target.setNextSpotAnim(new SpotAnim(469, 0, 96)));
					}
					playSound(soundId, player, target);
					player.getEquipment().removeAmmo(Equipment.WEAPON, 1);
					break;
				case DARK_BOW:
					int hit = getRandomMaxHit(player, weaponId, attackStyle, true);
					delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, hit));
					checkSwiftGlovesEffect(player, p.getTaskDelay(), attackStyle, weaponId, hit, p);
					WorldProjectile p2 = World.sendProjectile(player, target, AmmoType.forId(player.getEquipment().getAmmoId()).getProjAnim(player.getEquipment().getAmmoId()), 30, 50, 1);
					delayHit(p2.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
					dropAmmo(player, Equipment.AMMO, 2);
					break;
				default:
					if (weapon.isThrown()) {
						hit = getRandomMaxHit(player, weaponId, attackStyle, true);
						delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, hit));
						checkSwiftGlovesEffect(player, p.getTaskDelay(), attackStyle, weaponId, hit, p);
						dropAmmo(player, Equipment.WEAPON, 1);
					} else {
						hit = getRandomMaxHit(player, weaponId, attackStyle, true);
						delayHit(p.getTaskDelay(), weaponId, attackStyle, getRangeHit(player, hit));
						checkSwiftGlovesEffect(player, p.getTaskDelay(), attackStyle, weaponId, hit, p);
						if (weapon.getAmmos() != null)
							dropAmmo(player);
					}
					break;
			}
			player.setNextAnimation(weapon.getAttackAnimation());
			SpotAnim attackSpotAnim = weapon.getAttackSpotAnim(player, ammo);
			if (attackSpotAnim != null)
				player.setNextSpotAnim(attackSpotAnim);
		}
		playSound(soundId, player, target);
		return combatDelay;
	}

	private void checkSwiftGlovesEffect(Player player, int hitDelay, AttackStyle attackStyle, int weaponId, int hit, WorldProjectile p) {
		Item gloves = player.getEquipment().getItem(Equipment.HANDS);
		if (gloves == null || !gloves.getDefinitions().getName().contains("Swift glove"))
			return;
		if (hit != 0 && hit < ((max_hit / 3) * 2) || new Random().nextInt(3) != 0)
			return;
		player.sendMessage("You fired an extra shot.");
		World.sendProjectile(player, target, p.getSpotAnimId(), p.getStartHeight() - 5, p.getEndHeight() - 5, p.getStartTime(), 2, p.getAngle() - 5 < 0 ? 0 : p.getAngle() - 5, p.getSlope());
		delayHit(hitDelay, weaponId, attackStyle, getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
		if (hit > (max_hit - 10)) {
			target.freeze(Ticks.fromSeconds(10), false);
			target.setNextSpotAnim(new SpotAnim(181, 0, 96));
		}

	}

	public void dropAmmo(Player player, int slot, int quantity) {
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
		World.addGroundItem(new Item(ammoId, quantity), WorldTile.of(target.getCoordFaceX(target.getSize()), target.getCoordFaceY(target.getSize()), target.getPlane()), player);
	}

	public void dropAmmo(Player player) {
		dropAmmo(player, Equipment.AMMO, 1);
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
		if (player.getCombatDefinitions().isUsingSpecialAttack()) {
			if (!specialExecute(player))
				return combatDelay;
			switch (weaponId) {
				case 21371:
				case 21372:
				case 21373:
				case 21374:
				case 21375: {
					final WorldTile tile = WorldTile.of(target.getX(), target.getY(), target.getPlane());
					player.setNextAnimation(new Animation(11971));
					player.setNextSpotAnim(new SpotAnim(476));
					WorldTasks.scheduleTimer(tick -> {
						if (player == null || player.hasFinished())
							return false;
						if (tick % 5 == 0) {
							World.sendSpotAnim(player, new SpotAnim(478), tile);
							for (Entity entity : getMultiAttackTargets(player, WorldTile.of(target.getTile()), 1, 9)) {
								Hit hit = getMeleeHit(player, getRandomMaxHit(player, entity, 0, getMaxHit(player, target, 21371, attackStyle, false, 0.33), 21371, attackStyle, false, true, 1.25));
								addXp(player, entity, attackStyle.getXpType(), hit);
								if (hit.getDamage() > 0 && Utils.getRandomInclusive(8) == 0)
									target.getPoison().makePoisoned(48);
								entity.applyHit(hit);
							}
						}
						if (tick >= 55)
							return false;
						return true;
					});
				}
				break;
				case 15442:// whip start
				case 15443:
				case 15444:
				case 15441:
				case 4151:
				case 23691:
					player.setNextAnimation(new Animation(11971));
					target.setNextSpotAnim(new SpotAnim(2108, 0, 100));
					if (target instanceof Player p2)
						p2.setRunEnergy(p2.getRunEnergy() > 25 ? p2.getRunEnergy() - 25 : 0);
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.25, 1.0)));
					break;
				case 11730: // sara sword
				case 23690:
					player.setNextAnimation(new Animation(11993));
					target.setNextSpotAnim(new SpotAnim(1194));
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, 50 + Utils.getRandomInclusive(100)));
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 2.0, 1.1)));
					soundId = 3853;
					break;
				case 1249:// d spear
				case 1263:
				case 3176:
				case 5716:
				case 5730:
				case 13770:
				case 13772:
				case 13774:
				case 13776:
					player.setNextAnimation(new Animation(12017));
					player.stopAll();
					target.setNextSpotAnim(new SpotAnim(80, 5, 60));

					if (!target.addWalkSteps(target.getX() - player.getX() + target.getX(), target.getY() - player.getY() + target.getY(), 1))
						player.setNextFaceEntity(target);
					target.setNextFaceEntity(player);
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							target.setNextFaceEntity(null);
							player.setNextFaceEntity(null);
						}
					});
					if (target instanceof Player other) {
						other.lock();
						other.addFoodDelay(3000);
						other.setDisableEquip(true);
						WorldTasks.schedule(new WorldTask() {
							@Override
							public void run() {
								other.setDisableEquip(false);
								other.unlock();
							}
						}, 5);
					} else {
						NPC n = (NPC) target;
						n.freeze(Ticks.fromSeconds(3), false);
					}
					break;
				case 23042:
					player.setNextAnimation(new Animation(12019));
					player.setNextSpotAnim(new SpotAnim(2109));
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, 50));
					break;
				case 11698: // sgs
				case 23681:
					player.setNextAnimation(new Animation(12019));
					player.setNextSpotAnim(new SpotAnim(2109));
					int sgsdamage = getRandomMaxHit(player, weaponId, attackStyle, false, true, 2.0, 1.1);
					player.heal(sgsdamage / 2);
					player.getPrayer().restorePrayer((sgsdamage / 4) * 10);
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, sgsdamage));
					break;
				case 11696: // bgs
				case 23680:
					player.setNextAnimation(new Animation(11991));
					player.setNextSpotAnim(new SpotAnim(2114));
					Hit hit2 = getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 2.0, 1.1));
					delayNormalHit(weaponId, attackStyle, hit2);

					if (target instanceof Player other) {
						int amountLeft;
						if ((amountLeft = other.getSkills().drainLevel(Constants.DEFENSE, hit2.getDamage() / 10)) > 0)
							if ((amountLeft = other.getSkills().drainLevel(Constants.STRENGTH, amountLeft)) > 0)
								if ((amountLeft = other.getSkills().drainLevel(Constants.PRAYER, amountLeft)) > 0)
									if ((amountLeft = other.getSkills().drainLevel(Constants.ATTACK, amountLeft)) > 0)
										if ((amountLeft = other.getSkills().drainLevel(Constants.MAGIC, amountLeft)) > 0)
											if (other.getSkills().drainLevel(Constants.RANGE, amountLeft) > 0)
												break;
					} else if (target instanceof NPC n)
						if (hit2.getDamage() != 0)
							n.lowerDefense(hit2.getDamage() / 10, 0.0);
					break;
				case 11061: // ancient mace
					player.setNextAnimation(new Animation(6147));
					player.setNextSpotAnim(new SpotAnim(1052));
					int maceDMG = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, 1.0);
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, maceDMG));
					if (target instanceof Player other)
						other.getPrayer().drainPrayer(maceDMG);
					break;
				case 11694: // ags
				case 23679:
					player.setNextAnimation(new Animation(11989));
					player.setNextSpotAnim(new SpotAnim(2113));
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 2.0, 1.25)));
					break;
				case 13899: // vls
				case 13901:
					player.setNextAnimation(new Animation(10502));
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 2.0, 1.20)));
					break;
				case 13902: // statius hammer
				case 13904:
					Hit hit1 = getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, 1.25));
					player.setNextAnimation(new Animation(10505));
					player.setNextSpotAnim(new SpotAnim(1840));
					delayNormalHit(weaponId, attackStyle, hit1);

					if (hit1.getDamage() != 0)
						if (target instanceof NPC n)
							n.lowerDefense(0.30, 0.0);
						else if (target instanceof Player p)
							p.getSkills().adjustStat(0, -0.30, Constants.DEFENSE);

					break;
				case 13905: // vesta spear
				case 13907:
					player.setNextAnimation(new Animation(10499));
					player.setNextSpotAnim(new SpotAnim(1835));
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, 1.1)));
					break;
				case 7158: //d 2h
					player.setNextAnimation(new Animation(7078));
					player.setNextSpotAnim(new SpotAnim(1225));
					attackTarget(getMultiAttackTargets(player, target, 1, 20), () -> {
						int damage = getRandomMaxHit(player, 7158, attackStyle, true, true, 1.0, 1.2);
						delayHit(1, 7158, attackStyle, getMeleeHit(player, damage));
						return true;
					});
					break;
				case 19784: // korasi sword
				case 18786:
					player.setNextAnimation(new Animation(14788));
					player.setNextSpotAnim(new SpotAnim(1729));
					int korasiDamage = getMaxHit(player, target, weaponId, attackStyle, false, 1.0);
					double multiplier = 0.5 + Math.random();
					max_hit = (int) (korasiDamage * 1.5);
					korasiDamage *= multiplier;
					delayNormalHit(weaponId, attackStyle, getMagicHit(player, korasiDamage));
					WorldTasks.schedule(0, () -> target.setNextSpotAnim(new SpotAnim(1730)));
					break;
				case 11700:
					int zgsdamage = getRandomMaxHit(player, weaponId, attackStyle, false, true, 2.0, 1.1);
					player.setNextAnimation(new Animation(7070));
					player.setNextSpotAnim(new SpotAnim(1221));
					if (zgsdamage != 0 && target.getSize() <= 1) {
						target.setNextSpotAnim(new SpotAnim(2104));
						target.freeze(Ticks.fromSeconds(18), false);
					}
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, zgsdamage));
					break;
				case 3101: //rune claws
					//spotanim 274
					break;
				case 14484: // d claws
				case 23695:
					player.setNextAnimation(new Animation(10961));
					player.setNextSpotAnim(new SpotAnim(1950));
					int[] hits = { 0, 1 };
					max_hit = getMaxHit(player, target, weaponId, attackStyle, false, 1.0);
					int hit = getRandomMaxHit(player, target, max_hit / 2, max_hit, weaponId, attackStyle, false, true, 1.0);
					if (hit > 0)
						hits = new int[] { hit, hit / 2, (hit / 2) / 2, (hit / 2) - ((hit / 2) / 2) };
					else {
						hit = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, 1.0);
						if (hit > 0)
							hits = new int[] { 0, hit, hit / 2, hit - (hit / 2) };
						else {
							hit = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, 1.0);
							if (hit > 0)
								hits = new int[] { 0, 0, hit / 2, (hit / 2) + 10 };
							else {
								hit = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, 1.0);
								if (hit > 0)
									hits = new int[] { 0, 0, 0, (int) (hit * 1.5) };
								else
									hits = new int[] { 0, 0, 0, Utils.getRandomInclusive(7) };
							}
						}
					}
					for (int i = 0; i < hits.length; i++)
						if (i > 1)
							delayHit(1, weaponId, attackStyle, getMeleeHit(player, hits[i]));
						else
							delayNormalHit(weaponId, attackStyle, getMeleeHit(player, hits[i]));
					break;
				case 10887: // anchor
					player.setNextAnimation(new Animation(5870));
					player.setNextSpotAnim(new SpotAnim(1027));
					Hit hitt = getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 2.0, 1.1));
					delayNormalHit(weaponId, attackStyle, hitt);
					if (target instanceof Player other)
						other.getSkills().drainLevel(Constants.DEFENSE, hitt.getDamage() / 10);
					break;
				case 1305: // dragon long
					player.setNextAnimation(new Animation(12033));
					player.setNextSpotAnim(new SpotAnim(2117));
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, 1.25)));
					break;
				case 3204: // d hally
					player.setNextAnimation(new Animation(1665));
					player.setNextSpotAnim(new SpotAnim(282));
					if (target.getSize() > 3) {
						target.setNextSpotAnim(new SpotAnim(254, 0, 100));
						target.setNextSpotAnim(new SpotAnim(80));
					}
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, 1.1)));
					if (target.getSize() > 1)
						delayHit(1, weaponId, attackStyle, getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, 1.1)));
					break;
				case 4587: // dragon sci
					player.setNextAnimation(new Animation(12031));
					player.setNextSpotAnim(new SpotAnim(2118));
					Hit hit3 = getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.25, 1.0));
					if (target instanceof Player p2)
						if (hit3.getDamage() > 0)
							p2.setProtectionPrayBlock(10);
					delayNormalHit(weaponId, attackStyle, hit3);
					soundId = 2540;
					break;
				case 1215: // dragon dagger
				case 1231:
				case 5680: //dds p+
				case 5698: // dds p++
					player.setNextAnimation(new Animation(1062));
					player.setNextSpotAnim(new SpotAnim(252, 0, 100));
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.15, 1.15)));
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.15, 1.15)));
					soundId = 2537;
					break;
				case 1434: // dragon mace
					player.setNextAnimation(new Animation(1060));
					player.setNextSpotAnim(new SpotAnim(251));
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.25, 1.5)));
					soundId = 2541;
					break;
				default:
					player.sendMessage("This weapon has no special Attack, if you still see special bar please relogin.");
					return combatDelay;
			}
		} else {
			if (weaponId == -2) {
				int randomSeed = 25;

				if (target instanceof NPC n)
					randomSeed -= (n.getBonus(Bonus.CRUSH_DEF) / 100) * 1.3;

				if (new Random().nextInt(randomSeed) == 0) {
					player.setNextAnimation(new Animation(14417));
					final AttackStyle attack = attackStyle;
					attackTarget(getMultiAttackTargets(player, target, 5, Integer.MAX_VALUE), new MultiAttack() {
						private boolean nextTarget;

						@Override
						public boolean attack() {
							target.freeze(Ticks.fromSeconds(10), true);
							target.setNextSpotAnim(new SpotAnim(181, 0, 96));
							final Entity t = target;
							WorldTasks.schedule(1, () -> t.applyHit(new Hit(player, getRandomMaxHit(player, -2, attack, false, false, 1.0, 1.0), HitLook.TRUE_DAMAGE)));
							if (target instanceof Player p) {
								for (int i = 0; i < 7; i++)
									if (i != 3 && i != 5)
										p.getSkills().drainLevel(i, 7);
								p.sendMessage("Your stats have been drained!");
							} else if (target instanceof NPC n)
								n.lowerDefense(0.05, 0.0);
							if (!nextTarget)
								nextTarget = true;
							return nextTarget;

						}
					});
					return combatDelay;
				}
			}
			delayNormalHit(weaponId, attackStyle, getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false)));
			player.setNextAnimation(new Animation(getWeaponAttackEmote(weaponId, attackStyle)));
		}
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

	public static int getSpecialAmmount(int weaponId) {
		switch (weaponId) {
			case 4587: // dragon sci
			case 859: // magic longbow
			case 861: // magic shortbow
			case 10284: // Magic composite bow
			case 18332: // Magic longbow (sighted)
			case 19149:// zamorak bow
			case 19151:
			case 19143:// saradomin bow
			case 19145:
			case 19146:
			case 19148:// guthix bow
				return 55;
			case 11235: // dark bows
			case 15701:
			case 15702:
			case 15703:
			case 15704:
				return 65;
			case 8880:
			case 23043:
			case 23042:
			case 23044:
			case 23045:
			case 23046:
			case 23047:
				return 75;
			case 13899: // vls
			case 13901:
			case 1305: // dragon long
			case 1215: // dragon dagger
			case 1231:
			case 5698: // dds
			case 1434: // dragon mace
			case 1249:// d spear
			case 1263:
			case 3176:
			case 5716:
			case 5730:
			case 13770:
			case 13772:
			case 13774:
			case 13776:
				return 25;
			case 805:
				return 20;
			case 15442:// whip start
			case 15443:
			case 15444:
			case 15441:
			case 4151:
			case 23691:
			case 11698: // sgs
			case 23681:
			case 11694: // ags
			case 23679:
			case 13902: // statius hammer
			case 13904:
			case 13905: // vesta spear
			case 13907:
			case 14484: // d claws
			case 23695:
			case 10887: // anchor
			case 4153: // granite maul
			case 14679:
			case 14684: // zanik cbow
			case 15241: // hand cannon
			case 13908:
			case 13954:// morrigan javelin
			case 13955:
			case 13956:
			case 13879:
			case 13880:
			case 13881:
			case 13882:
			case 13883:// morigan thrown axe
			case 13957:
				return 50;
			case 11730: // ss
			case 23690:
			case 11696: // bgs
			case 23680:
			case 11700: // zgs
			case 23682:
			case 35:// Excalibur
			case 8280:
			case 14632:
			case 1377:// dragon battle axe
			case 13472:
			case 15486:// staff of lights
			case 22207:
			case 22209:
			case 22211:
			case 22213:
			case 11061:
				return 100;
			case 19784: // korasi sword
			case 21371:
			case 7158:
				return 60;
			case 3204: // d hally
				return 30;
			default:
				return 0;
		}
	}
	
	public int getRandomMagicMaxHit(Player player, int baseDamage) {
		return getRandomMagicMaxHit(player, baseDamage, true);
	}

	public int calculateMagicHit(Player player, int maxHit) {
		return calculateMagicHit(player, maxHit, true);
	}
	
	public int getRandomMagicMaxHit(Player player, int baseDamage, boolean applyMageLevelBoost) {
		int current = calculateMagicHit(player, baseDamage, applyMageLevelBoost);
		if (current <= 0) // Splash.
			return 0;

		int hit = Utils.random(current + 1);
		if (hit > 0)
			if (target instanceof NPC n)
				if (n.getId() == 9463 && hasFireCape(player))
					hit += 40;
		return hit;
	}

	private int calculateMagicHit(Player player, int maxHit, boolean applyMageLevelBoost) {
		double lvl = Math.floor(player.getSkills().getLevel(Constants.MAGIC) * player.getPrayer().getMageMultiplier());
		lvl += 8;
		if (fullVoidEquipped(player, 11663, 11674))
			lvl *= 1.3;
		lvl *= player.getAuraManager().getMagicAcc();
		double atkBonus = player.getCombatDefinitions().getBonus(Bonus.MAGIC_ATT);

		double atk = Math.floor(lvl * (atkBonus + 64));

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
					lvl *= 1.5;
					max_hit *= 1.5;
				} else
					max_hit = 0;
			if (n.getName().equals("Turoth") || n.getName().equals("Kurask"))
				if (player.getEquipment().getWeaponId() != 4170)
					max_hit = 0;
			double defLvl = n.getMagicLevel();
			double defBonus = n.getDefinitions().getMagicDef();
			defLvl += 8;
			def = Math.floor(defLvl * (defBonus + 64));
		}

		double prob = atk > def ? (1 - (def + 2) / (2 * (atk + 1))) : (atk / (2 * (def + 1)));
		if (Settings.getConfig().isDebug() && player.getNSV().getB("hitChance"))
			player.sendMessage("Your hit chance: " + Utils.formatDouble(prob * 100.0) + "%");
		if (prob <= Math.random())
			return 0;

		max_hit = maxHit;
		if (applyMageLevelBoost) {
			double boostedMageLevelBonus = 1 + ((player.getSkills().getLevel(Constants.MAGIC) - player.getSkills().getLevelForXp(Constants.MAGIC)) * 0.03);
			if (boostedMageLevelBonus > 1)
				max_hit *= boostedMageLevelBonus;
		}
		double magicPerc = player.getCombatDefinitions().getBonus(Bonus.MAGIC_STR);
		if (spellcasterGloveSpell != null)
			if (maxHit > 60) {
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
		double mageBonusBoost = magicPerc / 100 + 1;
		max_hit *= mageBonusBoost;

		if (player.hasSlayerTask())
			if (target instanceof NPC n && player.getSlayer().isOnTaskAgainst(n))
				if (player.getEquipment().wearingHexcrest() || player.getEquipment().wearingSlayerHelmet())
					max_hit *= 1.15;
		int finalMaxHit = (int) Math.floor(max_hit);
		if (Settings.getConfig().isDebug() && player.getNSV().getB("hitChance"))
			player.sendMessage("Your max hit: " + finalMaxHit);
		return finalMaxHit;
	}

	public int getRandomMaxHit(Player player, int weaponId, AttackStyle attackStyle, boolean ranging) {
		return getRandomMaxHit(player, weaponId, attackStyle, ranging, true, 1.0D, 1.0D);
	}

	public int getRandomMaxHit(Player player, int weaponId, AttackStyle attackStyle, boolean ranging, boolean calcDefense, double accuracyModifier, double damageModifier) {
		max_hit = getMaxHit(player, target, weaponId, attackStyle, ranging, damageModifier);
		return getRandomMaxHit(player, target, 1, max_hit, weaponId, attackStyle, ranging, calcDefense, accuracyModifier);
	}
	
	public static int getRandomMaxHit(Player player, Entity target, int weaponId, AttackStyle attackStyle, boolean ranging) {
		return getRandomMaxHit(player, target, weaponId, attackStyle, ranging, true, 1.0D, 1.0D);
	}

	public static int getRandomMaxHit(Player player, Entity target, int weaponId, AttackStyle attackStyle, boolean ranging, boolean calcDefense, double accuracyModifier, double damageModifier) {
		int maxHit = getMaxHit(player, target, weaponId, attackStyle, ranging, damageModifier);
		return getRandomMaxHit(player, target, 1, maxHit, weaponId, attackStyle, ranging, calcDefense, accuracyModifier);
	}

	public static int getRandomMaxHit(Player player, Entity target, int minHit, int maxHit, int weaponId, AttackStyle attackStyle, boolean ranging, boolean calcDefense, double accuracyModifier) {
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
				return 0;
		}
		int hit = Utils.random(minHit, maxHit);
		if (veracsProc)
			hit += 1.0;
		if (target instanceof NPC n)
			if (n.getId() == 9463 && hasFireCape(player))
				hit += 40;
		if (player.getAuraManager().isActivated(Aura.EQUILIBRIUM)) {
			int perc25MaxHit = (int) (maxHit * 0.25);
			hit -= perc25MaxHit;
			maxHit -= perc25MaxHit;
			if (hit < 0)
				hit = 0;
			if (hit < perc25MaxHit)
				hit += perc25MaxHit;
		}
		return hit;
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
//			case 11694:
//			case 23679:
//			case 11696:
//			case 23680:
//			case 11698:
//			case 23681:
//			case 11700:
//			case 23682:
//				baseDamage *= 1.1;
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

	public void delayNormalHit(int weaponId, AttackStyle attackStyle, Hit hit) {
		delayNormalHit(weaponId, attackStyle, hit, null, null, null);
	}

	public void delayNormalHit(int weaponId, AttackStyle attackStyle, Hit hit, Runnable afterDelay, Runnable hitSucc, Runnable hitFail) {
		delayHit(0, weaponId, attackStyle, hit, afterDelay, hitSucc, hitFail);
	}

	public static Hit getMeleeHit(Player player, int damage) {
		return new Hit(player, damage, HitLook.MELEE_DAMAGE);
	}

	public static Hit getRangeHit(Player player, int damage) {
		return new Hit(player, damage, HitLook.RANGE_DAMAGE);
	}

	public static Hit getMagicHit(Player player, int damage) {
		return new Hit(player, damage, HitLook.MAGIC_DAMAGE);
	}

	private void delayMagicHit(int delay, Hit hit, Runnable afterDelay, Runnable hitSucc, Runnable hitFail) {
		delayHit(delay, -1, null, hit, afterDelay, hitSucc, hitFail);
	}

	private void delayHit(int delay, int weaponId, AttackStyle attackStyle, Hit hit) {
		delayHit(delay, weaponId, attackStyle, hit, null, null, null);
	}

	private void delayHit(int delay, int weaponId, AttackStyle attackStyle, Hit hit, Runnable afterDelay, Runnable hitSucc, Runnable hitFail) {
		addAttackedByDelay(hit.getSource());

		final Entity target = this.target;

		Player player = (Player) hit.getSource();
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
		if ((damage >= max_hit * 0.90) && (hit.getLook() == HitLook.MAGIC_DAMAGE || hit.getLook() == HitLook.RANGE_DAMAGE || hit.getLook() == HitLook.MELEE_DAMAGE))
			hit.setCriticalMark();
		if (damage > 0) {
			if (hitSucc != null)
				hitSucc.run();
		} else if (hitFail != null)
			hitFail.run();
		addXp(player, target, attackStyle == null ? null : attackStyle.getXpType(), hit);
		checkPoison(player, target, weaponId, hit);
	}

	private int getSoundId(int weaponId, AttackStyle attackStyle) {
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
		if (player.getTempAttribs().getL("SOL_SPEC") >= System.currentTimeMillis() && !(player.getEquipment().getWeaponId() == 15486 || player.getEquipment().getWeaponId() == 22207 || player.getEquipment().getWeaponId() == 22209 || player.getEquipment().getWeaponId() == 22211 || player.getEquipment().getWeaponId() == 22213))
			player.getTempAttribs().setL("SOL_SPEC", 0);
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
			if (weaponName.contains("salamander"))
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

	public static boolean specialExecute(Player player) {
		int weaponId = player.getEquipment().getWeaponId();
		player.getCombatDefinitions().switchUsingSpecialAttack();
		int specAmt = getSpecialAmmount(weaponId);
		if (specAmt == 0) {
			player.sendMessage("This weapon has no special attack added yet.");
			player.getCombatDefinitions().drainSpec(0);
			return false;
		}
		if (player.getCombatDefinitions().hasRingOfVigour())
			specAmt *= 0.9;
		if (player.getCombatDefinitions().getSpecialAttackPercentage() < specAmt) {
			player.sendMessage("You don't have enough power left.");
			player.getCombatDefinitions().drainSpec(0);
			return false;
		}
		player.getCombatDefinitions().drainSpec(specAmt);
		return true;
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
