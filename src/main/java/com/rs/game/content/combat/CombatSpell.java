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

import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.skills.dungeoneering.DungeonController;
import com.rs.game.content.skills.dungeoneering.KinshipPerk;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.magic.Rune;
import com.rs.game.content.skills.magic.RuneSet;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

import java.util.HashMap;
import java.util.Map;

public enum CombatSpell {
	WIND_RUSH(1, 2.7, 10, new Animation(10546), new SpotAnim(457), 458, new SpotAnim(463), 7866, 7867, new RuneSet(Rune.AIR, 2)),
	WIND_STRIKE(1, 5.5, 20, new Animation(10546), new SpotAnim(457), 458, new SpotAnim(464), 220, 221, new RuneSet(Rune.AIR, 1, Rune.MIND, 1)),
	WIND_BOLT(17, 13.5, 90, new Animation(10546), new SpotAnim(457), 459, new SpotAnim(2699), 218, 219, new RuneSet(Rune.AIR, 2, Rune.CHAOS, 1)) {
		@Override
		public int getBaseDamage(Entity caster) {
			return caster instanceof Player p && p.getEquipment().getGlovesId() == 777 ? 120 : baseDamage;
		}
	},
	WIND_BLAST(41, 25.5, 130, new Animation(10546), new SpotAnim(457), 460, new SpotAnim(2699), 216, 217, new RuneSet(Rune.AIR, 3, Rune.DEATH, 1)),
	WIND_WAVE(62, 36, 170, new Animation(10546), new SpotAnim(457), 461, new SpotAnim(2700), 222, 223, new RuneSet(Rune.AIR, 5, Rune.BLOOD, 1)),
	WIND_SURGE(81, 75, 220, new Animation(10546), new SpotAnim(457), 462, new SpotAnim(2700), 7866, 7867, new RuneSet(Rune.AIR, 7, Rune.BLOOD, 1, Rune.DEATH, 1)),

	WATER_STRIKE(5, 7.5, 40, new Animation(14220), new SpotAnim(2701), 2703, new SpotAnim(2708), 211, 212, new RuneSet(Rune.AIR, 1, Rune.WATER, 1, Rune.MIND, 1)),
	WATER_BOLT(23, 16.5, 100, new Animation(14220), new SpotAnim(2701), 2704, new SpotAnim(2709), 209, 210, new RuneSet(Rune.AIR, 2, Rune.WATER, 2, Rune.CHAOS, 1)) {
		@Override
		public int getBaseDamage(Entity caster) {
			return caster instanceof Player p && p.getEquipment().getGlovesId() == 777 ? 130 : baseDamage;
		}
	},
	WATER_BLAST(47, 28.5, 140, new Animation(14220), new SpotAnim(2701), 2705, new SpotAnim(2710), 207, 208, new RuneSet(Rune.AIR, 3, Rune.WATER, 3, Rune.DEATH, 1)),
	WATER_WAVE(65, 37.5, 180, new Animation(14220), new SpotAnim(2701), 2706, new SpotAnim(2711), 213, 214, new RuneSet(Rune.AIR, 5, Rune.WATER, 7, Rune.BLOOD, 1)),
	WATER_SURGE(85, 80, 240, new Animation(14220), new SpotAnim(2701), 2707, new SpotAnim(2712), 7834, 7822, new RuneSet(Rune.AIR, 7, Rune.WATER, 10, Rune.DEATH, 1, Rune.BLOOD, 1)),

	EARTH_STRIKE(9, 9.5, 60, new Animation(14209), new SpotAnim(2713), 2718, new SpotAnim(2723), 132, 133, new RuneSet(Rune.AIR, 1, Rune.EARTH, 2, Rune.MIND, 1)),
	EARTH_BOLT(29, 19.5, 110, new Animation(14209), new SpotAnim(2714), 2719, new SpotAnim(2724), 130, 131, new RuneSet(Rune.AIR, 2, Rune.EARTH, 3, Rune.CHAOS, 1)) {
		@Override
		public int getBaseDamage(Entity caster) {
			return caster instanceof Player p && p.getEquipment().getGlovesId() == 777 ? 140 : baseDamage;
		}
	},
	EARTH_BLAST(53, 31.5, 150, new Animation(14209), new SpotAnim(2715), 2720, new SpotAnim(2725), 128, 129, new RuneSet(Rune.AIR, 3, Rune.EARTH, 4, Rune.DEATH, 1)),
	EARTH_WAVE(70, 40, 190, new Animation(14209), new SpotAnim(2716), 2721, new SpotAnim(2726), 134, 135, new RuneSet(Rune.AIR, 5, Rune.EARTH, 7, Rune.BLOOD, 1)),
	EARTH_SURGE(90, 85, 260, new Animation(14209), new SpotAnim(2717), 2722, new SpotAnim(2727), 7914, 7919, new RuneSet(Rune.AIR, 7, Rune.EARTH, 10, Rune.DEATH, 1, Rune.BLOOD, 1)),

	FIRE_STRIKE(13, 11.5, 80, new Animation(2791), new SpotAnim(2728), 2729, new SpotAnim(2737), 160, 161, new RuneSet(Rune.AIR, 2, Rune.FIRE, 3, Rune.MIND, 1)),
	FIRE_BOLT(35, 22.5, 120, new Animation(2791), new SpotAnim(2728), 2731, new SpotAnim(2738), 157, 158, new RuneSet(Rune.AIR, 3, Rune.FIRE, 4, Rune.CHAOS, 1)) {
		@Override
		public int getBaseDamage(Entity caster) {
			return caster instanceof Player p && p.getEquipment().getGlovesId() == 777 ? 150 : baseDamage;
		}
	},
	FIRE_BLAST(59, 34.5, 160, new Animation(2791), new SpotAnim(2728), 2733, new SpotAnim(2739), 155, 156, new RuneSet(Rune.AIR, 4, Rune.FIRE, 5, Rune.DEATH, 1)),
	FIRE_WAVE(75, 42.5, 200, new Animation(2791), new SpotAnim(2728), 2735, new SpotAnim(2740), 162, 163, new RuneSet(Rune.AIR, 5, Rune.FIRE, 7, Rune.BLOOD, 1)),
	FIRE_SURGE(95, 90, 280, new Animation(2791), new SpotAnim(2728), 2735, new SpotAnim(2741), 7932, 7933, new RuneSet(Rune.AIR, 7, Rune.FIRE, 10, Rune.DEATH, 1, Rune.BLOOD, 1)) {
		@Override
		public void onCast(Entity caster, Entity target) {
			World.sendProjectile(caster, target, 2736, -25, 50, 1);
			World.sendProjectile(caster, target, 2736, 25, 50, 1);
		}
	},
	CRUMBLE_UNDEAD(39, 24.5, 150, new Animation(724), new SpotAnim(145, 0, 96), 146, new SpotAnim(147, 0, 96), 122, 124, new RuneSet(Rune.AIR, 2, Rune.EARTH, 2, Rune.CHAOS, 1)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (!(target instanceof NPC n) || !n.getDefinitions().isUndead()) {
				caster.sendMessage("This spell only affects skeletons, zombies, ghosts and shades.");
				return false;
			}
			return true;
		}
	},
	IBAN_BLAST(50, 30, 250, new Animation(708), new SpotAnim(87, 0, 96), 88, new SpotAnim(89, 0, 96), 162, 1341, new RuneSet(Rune.DEATH, 1, Rune.FIRE, 5)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (caster.getEquipment().getWeaponId() != 1409) {
				caster.sendMessage("You need Iban's staff to cast this spell.");
				return false;
			}
			return true;
		}
	},
	MAGIC_DART(50, 31.5, 100, new Animation(1575), new SpotAnim(327, 0, 96), 328, new SpotAnim(329, 0, 96), 1718, 174, new RuneSet(Rune.MIND, 4, Rune.DEATH, 1)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (!Magic.isSlayerStaff(caster.getEquipment().getWeaponId())) {
				caster.sendMessage("You need a Slayer's staff to cast this spell.");
				return false;
			}
			if (caster.getSkills().getLevel(Constants.SLAYER) < 55) {
				caster.sendMessage("You need at least 55 Slayer to cast this spell.");
				return false;
			}
			return true;
		}

		@Override
		public int getBaseDamage(Entity caster) {
			if (caster instanceof Player p)
				return (int) (100.0 + Math.floor(p.getSkills().getLevel(Constants.MAGIC)));
			return 100;
		}
	},
	SARADOMIN_STRIKE(50, 34.5, 200, new Animation(811), null, -1, new SpotAnim(76, 0, 96), 1659, -1, new RuneSet(Rune.AIR, 4, Rune.FIRE, 1, Rune.BLOOD, 2)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (caster.getEquipment().getWeaponId() != 2415) {
				caster.sendMessage("You need a Saradomin staff to cast this spell.");
				return false;
			}
			return true;
		}

		@Override
		public int getBaseDamage(Entity caster) {
			if (caster instanceof Player p)
				return p.hasEffect(Effect.CHARGED) && p.getEquipment().getCapeId() == 2412 ? 300 : baseDamage;
			return baseDamage;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			if (target instanceof Player p)
				p.getPrayer().drainPrayer(1.0);
		}
	},
	CLAWS_OF_GUTHIX(50, 34.5, 200, new Animation(811), null, -1, new SpotAnim(77, 0, 96), 1653, -1, new RuneSet(Rune.AIR, 4, Rune.FIRE, 1, Rune.BLOOD, 2)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (caster.getEquipment().getWeaponId() != 2416) {
				caster.sendMessage("You need a Guthix staff to cast this spell.");
				return false;
			}
			return true;
		}

		@Override
		public int getBaseDamage(Entity caster) {
			if (caster instanceof Player p)
				return p.hasEffect(Effect.CHARGED) && p.getEquipment().getCapeId() == 2413 ? 300 : baseDamage;
			return baseDamage;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.DEFENSE, 0.05, 0.0);
		}
	},
	FLAMES_OF_ZAMORAK(50, 34.5, 200, new Animation(811), null, -1, new SpotAnim(78, 0, 96), 1655, -1, new RuneSet(Rune.AIR, 4, Rune.FIRE, 1, Rune.BLOOD, 2)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (caster.getEquipment().getWeaponId() != 2417) {
				caster.sendMessage("You need a Zamorak staff to cast this spell.");
				return false;
			}
			return true;
		}

		@Override
		public int getBaseDamage(Entity caster) {
			if (caster instanceof Player p)
				return p.hasEffect(Effect.CHARGED) && p.getEquipment().getCapeId() == 2414 ? 300 : baseDamage;
			return baseDamage;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.MAGIC, 0.05, 0.0);
		}
	},
	STORM_OF_ARMADYL(77, 70, 160, new Animation(10546), new SpotAnim(457), 1019, new SpotAnim(1019), 7866, 7867, new RuneSet(Rune.ARMADYL, 1)) {
		@Override
		public int getCombatDelay(Entity caster) {
			return caster instanceof Player p && p.getEquipment().getWeaponId() == 21777 ? 3 : 4;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.DEFENSE, 1, 0.0);
		}
		
		@Override
		public int getBaseDamage(Entity caster) {
			if (caster instanceof Player player)
				return baseDamage + (player.getSkills().getLevelForXp(Constants.MAGIC) - 77) * 5;
			return baseDamage;
		}
	},
	CONFUSE(3, 13, -1, new Animation(711), new SpotAnim(102, 0, 96), 103, new SpotAnim(104, 0, 96), 119, 121, new RuneSet(Rune.BODY, 1, Rune.WATER, 3, Rune.EARTH, 2)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (target instanceof NPC npc) {
				if (npc.getAttackLevel() < npc.getCombatDefinitions().getAttackLevel()) {
					caster.sendMessage("That target is already weakened.");
					return false;
				}
			} else if (target instanceof Player player)
				if (player.getSkills().getLevel(Constants.ATTACK) < player.getSkills().getLevelForXp(Constants.ATTACK)) {
					caster.sendMessage("That target is already weakened.");
					return false;
				}
			return true;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.ATTACK, 0.05, 0.95);
		}
	},
	WEAKEN(11, 21, -1, new Animation(711), new SpotAnim(105, 0, 96), 106, new SpotAnim(107, 0, 96), 3011, 3010, new RuneSet(Rune.BODY, 1, Rune.WATER, 3, Rune.EARTH, 2)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (target instanceof NPC npc) {
				if (npc.getStrengthLevel() < npc.getCombatDefinitions().getStrengthLevel()) {
					caster.sendMessage("That target is already weakened.");
					return false;
				}
			} else if (target instanceof Player player)
				if (player.getSkills().getLevel(Constants.STRENGTH) < player.getSkills().getLevelForXp(Constants.STRENGTH)) {
					caster.sendMessage("That target is already weakened.");
					return false;
				}
			return true;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.STRENGTH, 0.05, 0.95);
		}
	},
	CURSE(19, 29, -1, new Animation(711), new SpotAnim(108, 0, 96), 109, new SpotAnim(110, 0, 96), 127, 126, new RuneSet(Rune.BODY, 1, Rune.WATER, 2, Rune.EARTH, 3)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (target instanceof NPC npc) {
				if (npc.getDefenseLevel() < npc.getCombatDefinitions().getDefenseLevel()) {
					caster.sendMessage("That target is already weakened.");
					return false;
				}
			} else if (target instanceof Player player)
				if (player.getSkills().getLevel(Constants.DEFENSE) < player.getSkills().getLevelForXp(Constants.DEFENSE)) {
					caster.sendMessage("That target is already weakened.");
					return false;
				}
			return true;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.DEFENSE, 0.05, 0.95);
		}
	},
	VULNERABILITY(66, 76, -1, new Animation(711), new SpotAnim(167, 0, 96), 168, new SpotAnim(169, 0, 96), 3009, 3008, new RuneSet(Rune.SOUL, 1, Rune.WATER, 5, Rune.EARTH, 5)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (target instanceof NPC npc) {
				if (npc.getDefenseLevel() < npc.getCombatDefinitions().getDefenseLevel()) {
					caster.sendMessage("That target is already weakened.");
					return false;
				}
			} else if (target instanceof Player player)
				if (player.getSkills().getLevel(Constants.DEFENSE) < player.getSkills().getLevelForXp(Constants.DEFENSE)) {
					caster.sendMessage("That target is already weakened.");
					return false;
				}
			return true;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.DEFENSE, 0.1, 0.9);
		}
	},
	ENFEEBLE(73, 83, -1, new Animation(711), new SpotAnim(170, 0, 96), 171, new SpotAnim(172, 0, 96), 148, 150, new RuneSet(Rune.SOUL, 1, Rune.WATER, 8, Rune.EARTH, 8)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (target instanceof NPC npc) {
				if (npc.getStrengthLevel() < npc.getCombatDefinitions().getStrengthLevel()) {
					caster.sendMessage("That target is already weakened.");
					return false;
				}
			} else if (target instanceof Player player)
				if (player.getSkills().getLevel(Constants.STRENGTH) < player.getSkills().getLevelForXp(Constants.STRENGTH)) {
					caster.sendMessage("That target is already weakened.");
					return false;
				}
			return true;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.STRENGTH, 0.1, 0.9);
		}
	},
	STUN(80, 90, -1, new Animation(711), new SpotAnim(173, 0, 96), 174, new SpotAnim(245, 0, 96), 3004, 3005, new RuneSet(Rune.SOUL, 1, Rune.WATER, 12, Rune.EARTH, 12)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (target instanceof NPC npc) {
				if (npc.getAttackLevel() < npc.getCombatDefinitions().getAttackLevel()) {
					caster.sendMessage("That target is already weakened.");
					return false;
				}
			} else if (target instanceof Player player)
				if (player.getSkills().getLevel(Constants.ATTACK) < player.getSkills().getLevelForXp(Constants.ATTACK)) {
					caster.sendMessage("That target is already weakened.");
					return false;
				}
			return true;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.ATTACK, 0.1, 0.9);
		}
	},
	BIND(20, 30, 20, new Animation(710), new SpotAnim(177, 0, 96), 178, new SpotAnim(181, 0, 96), 101, 99, new RuneSet(Rune.NATURE, 2, Rune.WATER, 3, Rune.EARTH, 3)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.freeze(Ticks.fromSeconds(5), true);
		}
	},
	SNARE(50, 60, 30, new Animation(710), new SpotAnim(177, 0, 96), 178, new SpotAnim(180, 0, 96), 3003, 3002, new RuneSet(Rune.NATURE, 3, Rune.WATER, 4, Rune.EARTH, 4)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.freeze(Ticks.fromSeconds(10), true);
		}
	},
	ENTANGLE(79, 89, 50, new Animation(710), new SpotAnim(177, 0, 96), 178, new SpotAnim(179, 0, 96), 151, 153, new RuneSet(Rune.NATURE, 4, Rune.WATER, 5, Rune.EARTH, 5)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.freeze(Ticks.fromSeconds(20), true);
		}
	},
	TELEPORT_BLOCK(85, 80, 30, new Animation(10503), new SpotAnim(1841), 1842, new SpotAnim(1843), 202, 203, new RuneSet(Rune.CHAOS, 1, Rune.LAW, 1, Rune.DEATH, 1)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			if (target instanceof Player p2) {
				if (p2.getPrayer().isProtectingMage())
					p2.addEffect(Effect.TELEBLOCK, Ticks.fromMinutes(2.5));
				else
					p2.addEffect(Effect.TELEBLOCK, Ticks.fromMinutes(5));
				p2.sendMessage("You have been teleport blocked!");
			}
		}

		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (!(target instanceof Player player)) {
				caster.sendMessage("This spell is only effective against other players.");
				return false;
			}
			if (player.hasEffect(Effect.TELEBLOCK)) {
				caster.sendMessage("That player is already affected by this spell.");
				return false;
			}
			return true;
		}
	},

	SMOKE_RUSH(50, 30, 130, new Animation(1978), null, 384, new SpotAnim(385, 0, 96), 183, 185, new RuneSet(Rune.DEATH, 2, Rune.CHAOS, 2, Rune.FIRE, 1, Rune.AIR, 1)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			if (Utils.random(5) == 0)
				target.getPoison().makePoisoned(20);
		}
	},
	SMOKE_BURST(62, 36, 170, new Animation(1979), null, -1, new SpotAnim(389, 0, 96), 183, 182, new RuneSet(Rune.DEATH, 2, Rune.CHAOS, 4, Rune.FIRE, 2, Rune.AIR, 2)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			if (Utils.random(5) == 0)
				target.getPoison().makePoisoned(20);
		}
	},
	SMOKE_BLITZ(74, 42, 230, new Animation(1978), null, 386, new SpotAnim(387, 0, 96), 183, 181, new RuneSet(Rune.BLOOD, 2, Rune.DEATH, 2, Rune.FIRE, 2, Rune.AIR, 2)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			if (Utils.random(5) == 0)
				target.getPoison().makePoisoned(40);
		}
	},
	SMOKE_BARRAGE(86, 48, 270, new Animation(1979), null, -1, new SpotAnim(391, 0, 96), 183, 180, new RuneSet(Rune.BLOOD, 2, Rune.DEATH, 4, Rune.FIRE, 4, Rune.AIR, 4)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			if (Utils.random(5) == 0)
				target.getPoison().makePoisoned(40);
		}
	},
	SHADOW_RUSH(52, 31, 140, new Animation(1978), null, 378, new SpotAnim(379), 178, 179, new RuneSet(Rune.SOUL, 1, Rune.DEATH, 2, Rune.CHAOS, 2, Rune.AIR, 1)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.ATTACK, 0.1, 0.9);
		}
	},
	SHADOW_BURST(64, 37, 180, new Animation(1979), null, -1, new SpotAnim(382), 178, 177, new RuneSet(Rune.SOUL, 2, Rune.DEATH, 2, Rune.CHAOS, 4, Rune.AIR, 1)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.ATTACK, 0.1, 0.9);
		}
	},
	SHADOW_BLITZ(76, 43, 240, new Animation(1978), null, 380, new SpotAnim(381), 178, 176, new RuneSet(Rune.SOUL, 2, Rune.BLOOD, 2, Rune.DEATH, 2, Rune.AIR, 2)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.ATTACK, 0.15, 0.85);
		}
	},
	SHADOW_BARRAGE(88, 48, 280, new Animation(1979), null, -1, new SpotAnim(383), 178, 175, new RuneSet(Rune.SOUL, 3, Rune.BLOOD, 2, Rune.DEATH, 4, Rune.AIR, 4)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.lowerStat(Constants.ATTACK, 0.15, 0.85);
		}
	},
	BLOOD_RUSH(56, 33, 150, new Animation(1978), null, 374, new SpotAnim(373), 106, 110, new RuneSet(Rune.BLOOD, 1, Rune.DEATH, 2, Rune.CHAOS, 2)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			caster.heal((int) (hit.getDamage() * 0.25));
		}
	},
	BLOOD_BURST(68, 39, 210, new Animation(1979), null, -1, new SpotAnim(376), 106, 105, new RuneSet(Rune.BLOOD, 2, Rune.DEATH, 2, Rune.CHAOS, 4)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			caster.heal((int) (hit.getDamage() * 0.25));
		}
	},
	BLOOD_BLITZ(80, 45, 250, new Animation(1978), null, 374, new SpotAnim(375), 106, 104, new RuneSet(Rune.BLOOD, 4, Rune.DEATH, 2)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			caster.heal((int) (hit.getDamage() * 0.25));
		}
	},
	BLOOD_BARRAGE(92, 51, 290, new Animation(1979), null, -1, new SpotAnim(377), 106, 102, new RuneSet(Rune.SOUL, 1, Rune.BLOOD, 4, Rune.DEATH, 4)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			caster.heal((int) (hit.getDamage() * 0.25));
		}
	},
	ICE_RUSH(58, 34, 160, new Animation(1978), null, 360, new SpotAnim(361), 171, 173, new RuneSet(Rune.DEATH, 2, Rune.CHAOS, 2, Rune.WATER, 2)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.freeze(Ticks.fromSeconds(5), true);
		}
	},
	ICE_BURST(70, 40, 220, new Animation(1979), null, -1, new SpotAnim(363), 171, 170, new RuneSet(Rune.DEATH, 2, Rune.CHAOS, 4, Rune.WATER, 4)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.freeze(Ticks.fromSeconds(10), true);
		}
	},
	ICE_BLITZ(82, 46, 260, new Animation(1978), new SpotAnim(366, 0, 96), 362, new SpotAnim(367), 171, 169, new RuneSet(Rune.BLOOD, 2, Rune.DEATH, 2, Rune.WATER, 3)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.freeze(Ticks.fromSeconds(15), true);
		}
	},
	ICE_BARRAGE(94, 52, 300, new Animation(1979), new SpotAnim(368, 0, 96), -1, new SpotAnim(369), 171, 168, new RuneSet(Rune.BLOOD, 2, Rune.DEATH, 4, Rune.WATER, 6)) {
		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			target.freeze(Ticks.fromSeconds(20), true);
		}
	},
	MIASMIC_RUSH(61, 36, 180, new Animation(10513), new SpotAnim(1845), 1846, new SpotAnim(1847), 5368, 5365, new RuneSet(Rune.SOUL, 1, Rune.CHAOS, 2, Rune.EARTH, 1)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (caster.getEquipment().getWeaponId() != 13867 && caster.getEquipment().getWeaponId() != 13869 && caster.getEquipment().getWeaponId() != 13941 && caster.getEquipment().getWeaponId() != 13943) {
				caster.sendMessage("You need a Zuriel's staff to cast this spell.");
				return false;
			}
			return true;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			if (target instanceof Player p)
				p.refreshMiasmicTimer(Ticks.fromSeconds(12));
		}
	},
	MIASMIC_BURST(73, 42, 240, new Animation(10516), new SpotAnim(1848), -1, new SpotAnim(1849), 5366, 5372, new RuneSet(Rune.SOUL, 2, Rune.CHAOS, 4, Rune.EARTH, 2)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (caster.getEquipment().getWeaponId() != 13867 && caster.getEquipment().getWeaponId() != 13869 && caster.getEquipment().getWeaponId() != 13941 && caster.getEquipment().getWeaponId() != 13943) {
				caster.sendMessage("You need a Zuriel's staff to cast this spell.");
				return false;
			}
			return true;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			if (target instanceof Player p)
				p.refreshMiasmicTimer(Ticks.fromSeconds(24));
		}
	},
	MIASMIC_BLITZ(85, 48, 280, new Animation(10524), new SpotAnim(1850), 1852, new SpotAnim(1851), 5370, 5367, new RuneSet(Rune.SOUL, 3, Rune.BLOOD, 2, Rune.EARTH, 1)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (caster.getEquipment().getWeaponId() != 13867 && caster.getEquipment().getWeaponId() != 13869 && caster.getEquipment().getWeaponId() != 13941 && caster.getEquipment().getWeaponId() != 13943) {
				caster.sendMessage("You need a Zuriel's staff to cast this spell.");
				return false;
			}
			return true;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			if (target instanceof Player p)
				p.refreshMiasmicTimer(Ticks.fromSeconds(36));
		}
	},
	MIASMIC_BARRAGE(97, 54, 320, new Animation(10518), new SpotAnim(1853), -1, new SpotAnim(1854), 5371, 5369, new RuneSet(Rune.SOUL, 4, Rune.BLOOD, 4, Rune.EARTH, 4)) {
		@Override
		public boolean extraReqs(Player caster, Entity target) {
			if (caster.getEquipment().getWeaponId() != 13867 && caster.getEquipment().getWeaponId() != 13869 && caster.getEquipment().getWeaponId() != 13941 && caster.getEquipment().getWeaponId() != 13943) {
				caster.sendMessage("You need a Zuriel's staff to cast this spell.");
				return false;
			}
			return true;
		}

		@Override
		public void onHit(Entity caster, Entity target, Hit hit) {
			if (target instanceof Player p)
				p.refreshMiasmicTimer(Ticks.fromSeconds(48));
		}
	};

	private static Map<Integer, Map<Integer, CombatSpell>> SPELL_MAP = new HashMap<>();

	static {
		Map<Integer, CombatSpell> MODERN = new HashMap<>();

		MODERN.put(98, CombatSpell.WIND_RUSH);
		MODERN.put(25, CombatSpell.WIND_STRIKE);
		MODERN.put(26, CombatSpell.CONFUSE);
		MODERN.put(28, CombatSpell.WATER_STRIKE);
		MODERN.put(30, CombatSpell.EARTH_STRIKE);
		MODERN.put(31, CombatSpell.WEAKEN);
		MODERN.put(32, CombatSpell.FIRE_STRIKE);
		MODERN.put(34, CombatSpell.WIND_BOLT);
		MODERN.put(35, CombatSpell.CURSE);
		MODERN.put(36, CombatSpell.BIND);
		MODERN.put(39, CombatSpell.WATER_BOLT);
		MODERN.put(42, CombatSpell.EARTH_BOLT);
		MODERN.put(45, CombatSpell.FIRE_BOLT);
		MODERN.put(47, CombatSpell.CRUMBLE_UNDEAD);
		MODERN.put(49, CombatSpell.WIND_BLAST);
		MODERN.put(52, CombatSpell.WATER_BLAST);
		MODERN.put(54, CombatSpell.IBAN_BLAST);
		MODERN.put(55, CombatSpell.SNARE);
		MODERN.put(56, CombatSpell.MAGIC_DART);
		MODERN.put(58, CombatSpell.EARTH_BLAST);
		MODERN.put(63, CombatSpell.FIRE_BLAST);
		MODERN.put(66, CombatSpell.SARADOMIN_STRIKE);
		MODERN.put(67, CombatSpell.CLAWS_OF_GUTHIX);
		MODERN.put(68, CombatSpell.FLAMES_OF_ZAMORAK);
		MODERN.put(70, CombatSpell.WIND_WAVE);
		MODERN.put(73, CombatSpell.WATER_WAVE);
		MODERN.put(75, CombatSpell.VULNERABILITY);
		MODERN.put(77, CombatSpell.EARTH_WAVE);
		MODERN.put(78, CombatSpell.ENFEEBLE);
		MODERN.put(80, CombatSpell.FIRE_WAVE);
		MODERN.put(99, CombatSpell.STORM_OF_ARMADYL);
		MODERN.put(81, CombatSpell.ENTANGLE);
		MODERN.put(82, CombatSpell.STUN);
		MODERN.put(84, CombatSpell.WIND_SURGE);
		MODERN.put(86, CombatSpell.TELEPORT_BLOCK);
		MODERN.put(87, CombatSpell.WATER_SURGE);
		MODERN.put(89, CombatSpell.EARTH_SURGE);
		MODERN.put(91, CombatSpell.FIRE_SURGE);

		SPELL_MAP.put(192, MODERN);

		Map<Integer, CombatSpell> ANCIENT = new HashMap<>();

		ANCIENT.put(28, CombatSpell.SMOKE_RUSH);
		ANCIENT.put(32, CombatSpell.SHADOW_RUSH);
		ANCIENT.put(24, CombatSpell.BLOOD_RUSH);
		ANCIENT.put(20, CombatSpell.ICE_RUSH);
		ANCIENT.put(36, CombatSpell.MIASMIC_RUSH);
		ANCIENT.put(30, CombatSpell.SMOKE_BURST);
		ANCIENT.put(34, CombatSpell.SHADOW_BURST);
		ANCIENT.put(26, CombatSpell.BLOOD_BURST);
		ANCIENT.put(22, CombatSpell.ICE_BURST);
		ANCIENT.put(38, CombatSpell.MIASMIC_BURST);
		ANCIENT.put(29, CombatSpell.SMOKE_BLITZ);
		ANCIENT.put(33, CombatSpell.SHADOW_BLITZ);
		ANCIENT.put(25, CombatSpell.BLOOD_BLITZ);
		ANCIENT.put(21, CombatSpell.ICE_BLITZ);
		ANCIENT.put(37, CombatSpell.MIASMIC_BLITZ);
		ANCIENT.put(31, CombatSpell.SMOKE_BARRAGE);
		ANCIENT.put(35, CombatSpell.SHADOW_BARRAGE);
		ANCIENT.put(27, CombatSpell.BLOOD_BARRAGE);
		ANCIENT.put(23, CombatSpell.ICE_BARRAGE);
		ANCIENT.put(39, CombatSpell.MIASMIC_BARRAGE);

		SPELL_MAP.put(193, ANCIENT);

		Map<Integer, CombatSpell> LUNAR = new HashMap<>();
		SPELL_MAP.put(430, LUNAR);

		Map<Integer, CombatSpell> DUNG = new HashMap<>();

		DUNG.put(25, CombatSpell.WIND_STRIKE);
		DUNG.put(26, CombatSpell.CONFUSE);
		DUNG.put(27, CombatSpell.WATER_STRIKE);
		DUNG.put(28, CombatSpell.EARTH_STRIKE);
		DUNG.put(29, CombatSpell.WEAKEN);
		DUNG.put(30, CombatSpell.FIRE_STRIKE);
		DUNG.put(32, CombatSpell.WIND_BOLT);
		DUNG.put(33, CombatSpell.CURSE);
		DUNG.put(34, CombatSpell.BIND);
		DUNG.put(36, CombatSpell.WATER_BOLT);
		DUNG.put(37, CombatSpell.EARTH_BOLT);
		DUNG.put(41, CombatSpell.FIRE_BOLT);
		DUNG.put(42, CombatSpell.WIND_BLAST);
		DUNG.put(43, CombatSpell.WATER_BLAST);
		DUNG.put(44, CombatSpell.SNARE);
		DUNG.put(45, CombatSpell.EARTH_BLAST);
		DUNG.put(47, CombatSpell.FIRE_BLAST);
		DUNG.put(48, CombatSpell.WIND_WAVE);
		DUNG.put(49, CombatSpell.WATER_WAVE);
		DUNG.put(50, CombatSpell.VULNERABILITY);
		DUNG.put(54, CombatSpell.EARTH_WAVE);
		DUNG.put(56, CombatSpell.ENFEEBLE);
		DUNG.put(58, CombatSpell.FIRE_WAVE);
		DUNG.put(59, CombatSpell.ENTANGLE);
		DUNG.put(60, CombatSpell.STUN);
		DUNG.put(61, CombatSpell.WIND_SURGE);
		DUNG.put(62, CombatSpell.WATER_SURGE);
		DUNG.put(63, CombatSpell.EARTH_SURGE);
		DUNG.put(67, CombatSpell.FIRE_SURGE);

		SPELL_MAP.put(950, DUNG);
	}

	public static CombatSpell forId(int spellBook, int spellId) {
		return SPELL_MAP.get(spellBook).get(spellId);
	}

	protected int req;
	protected double splashXp;
	protected int castSound;
	protected int landSound;
	protected int splashSound;
	protected int baseDamage;
	protected Animation castAnim;
	protected SpotAnim castSpotAnim;
	protected int projAnim;
	protected SpotAnim hitSpotAnim;
	protected RuneSet runes;
	
//	private CombatSpell(int req, double splashXp, int baseDamage, Animation castAnim, SpotAnim castSpotAnim, int projAnim, SpotAnim hitSpotAnim, RuneSet runes) {
//		this(req, splashXp, baseDamage, castAnim, castSpotAnim, projAnim, hitSpotAnim, -1, -1, -1, runes);
//	}
	
	private CombatSpell(int req, double splashXp, int baseDamage, Animation castAnim, SpotAnim castSpotAnim, int projAnim, SpotAnim hitSpotAnim, int castSound, int landSound, RuneSet runes) {
		this(req, splashXp, baseDamage, castAnim, castSpotAnim, projAnim, hitSpotAnim, castSound, landSound, -1, runes);
	}

	private CombatSpell(int req, double splashXp, int baseDamage, Animation castAnim, SpotAnim castSpotAnim, int projAnim, SpotAnim hitSpotAnim, int castSound, int landSound, int splashSound, RuneSet runes) {
		this.req = req;
		this.splashXp = splashXp;
		this.baseDamage = baseDamage;
		this.castAnim = castAnim;
		this.castSpotAnim = castSpotAnim;
		this.projAnim = projAnim;
		this.hitSpotAnim = hitSpotAnim;
		this.castSound = castSound;
		this.landSound = landSound;
		this.splashSound = splashSound;
		this.runes = runes;
	}

	public int getReq() {
		return req;
	}

	public double getSplashXp() {
		return splashXp;
	}

	public RuneSet getRuneSet() {
		return runes;
	}

	public Animation getCastAnim(Entity caster) {
		switch(this) {
		case EARTH_BLAST:
		case EARTH_BOLT:
		case EARTH_STRIKE:
		case EARTH_SURGE:
		case EARTH_WAVE:
			return caster instanceof Player p && p.getEquipment().getWeaponName().toLowerCase().contains("staff") ? new Animation(14222) : castAnim;
		case WIND_BLAST:
		case WIND_BOLT:
		case WIND_RUSH:
		case WIND_STRIKE:
		case WIND_SURGE:
		case WIND_WAVE:
			return caster instanceof Player p && p.getEquipment().getWeaponName().toLowerCase().contains("staff") ? new Animation(14221) : castAnim;
		default:
			break;
		}
		return castAnim;
	}

	public boolean isFireSpell() {
		switch(this) {
		case FIRE_STRIKE:
		case FIRE_BOLT:
		case FIRE_BLAST:
		case FIRE_WAVE:
		case FIRE_SURGE:
			return true;
		default:
			return false;
		}
	}

	public boolean isWaterSpell() {
		switch(this) {
		case WATER_STRIKE:
		case WATER_BOLT:
		case WATER_BLAST:
		case WATER_WAVE:
		case WATER_SURGE:
			return true;
		default:
			return false;
		}
	}

	public boolean isEarthSpell() {
		switch(this) {
		case EARTH_STRIKE:
		case EARTH_BOLT:
		case EARTH_BLAST:
		case EARTH_WAVE:
		case EARTH_SURGE:
			return true;
		default:
			return false;
		}
	}

	public boolean isAirSpell() {
		switch(this) {
		case WIND_STRIKE:
		case WIND_BOLT:
		case WIND_BLAST:
		case WIND_WAVE:
		case WIND_SURGE:
			return true;
		default:
			return false;
		}
	}

	public boolean isFireBlast() {
		switch(this) {
		case FIRE_BLAST -> {return true;}
		default -> { return false; }
		}
	}

	public boolean isWaterBlast() {
		switch(this) {
		case WATER_BLAST -> {return true;}
		default -> { return false; }
		}
	}

	public boolean isEarthBlast() {
		switch(this) {
		case EARTH_BLAST -> {return true;}
		default -> { return false; }
		}
	}

	public boolean isAirBlast() {
		switch(this) {
		case WIND_BLAST -> {return true;}
		default -> { return false; }
		}
	}


	public SpotAnim getCastSpotAnim(Entity caster) {
		switch(this) {
		case WATER_BLAST:
		case WATER_BOLT:
		case WATER_STRIKE:
		case WATER_SURGE:
		case WATER_WAVE:
			return caster instanceof Player p && p.getEquipment().getWeaponName().toLowerCase().contains("staff") ? new SpotAnim(2702) : castSpotAnim;
		default:
			break;
		}
		return castSpotAnim;
	}

	public SpotAnim getHitSpotAnim() {
		return hitSpotAnim;
	}

	public int getBaseDamage(Entity caster) {
		return baseDamage;
	}

	public final int cast(Entity caster, Entity target) {
		Animation castAnim = getCastAnim(caster);
		SpotAnim castSpotAnim = getCastSpotAnim(caster);
		if (castAnim != null)
			caster.setNextAnimation(castAnim);
		if (castSpotAnim != null)
			caster.setNextSpotAnim(castSpotAnim);
		if (castSound != -1 && caster instanceof Player player)
			PlayerCombat.playSound(castSound, player, target);
		onCast(caster, target);
		return World.sendProjectile(caster, target, projAnim, castSpotAnim != null && castSpotAnim.getHeight() > 50 ? 20 : 0, 50, 1).getTaskDelay();
	}

	public void onCast(Entity caster, Entity target) {

	}

	public void onHit(Entity caster, Entity target, Hit hit) {

	}

	public int getCombatDelay(Entity caster) {
		if (caster instanceof Player player)
			if (player.getControllerManager().isIn(DungeonController.class) && player.getDungManager().getActivePerk() == KinshipPerk.BLITZER) {
				int chance = 10 * player.getDungManager().getKinshipTier(KinshipPerk.BLITZER);
				if (Utils.random(100) < chance)
					return 3;
			}
		return 4;
	}

	public boolean isAOE() {
		switch(this) {
		case BLOOD_BARRAGE:
		case BLOOD_BURST:
		case ICE_BARRAGE:
		case ICE_BURST:
		case MIASMIC_BARRAGE:
		case MIASMIC_BURST:
		case SHADOW_BARRAGE:
		case SHADOW_BURST:
		case SMOKE_BARRAGE:
		case SMOKE_BURST:
			return true;
		default:
			return false;
		}
	}

	public boolean extraReqs(Player player, Entity target) {
		return true;
	}

	public final boolean canCast(Player player, Entity target) {
		if (player.getSkills().getLevel(Constants.MAGIC) < req) {
			player.sendMessage("You need a magic level of " + req + " to cast this spell.");
			return false;
		}
		if (!extraReqs(player, target))
			return false;
		return runes.meetsRequirements(player);
	}
}
