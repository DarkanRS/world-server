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
package com.rs.game.content.combat

import com.rs.game.World
import com.rs.game.content.Effect
import com.rs.game.content.skills.dungeoneering.DungeonController
import com.rs.game.content.skills.dungeoneering.KinshipPerk
import com.rs.game.content.skills.magic.Magic
import com.rs.game.content.skills.magic.Rune
import com.rs.game.content.skills.magic.RuneSet
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.SpotAnim
import com.rs.lib.util.Utils
import com.rs.utils.Ticks
import java.util.*

enum class CombatSpell(
    val req: Int,
    @JvmField val splashXp: Double,
    protected val baseDamage: Int,
    protected val castAnim: Animation,
    protected val castSpotAnim: SpotAnim?,
    protected val projAnim: Int,
    @JvmField val hitSpotAnim: SpotAnim,
    protected val castSound: Int,
    @JvmField val landSound: Int,
    @JvmField val splashSound: Int,
    val runeSet: RuneSet
) {
    WIND_RUSH(1, 2.7, 10, Animation(10546), SpotAnim(457), 458, SpotAnim(463), 7866, 7867, RuneSet(Rune.AIR, 2)),
    WIND_STRIKE(1, 5.5, 20, Animation(10546), SpotAnim(457), 458, SpotAnim(464), 220, 221, RuneSet(Rune.AIR, 1, Rune.MIND, 1)),
    WIND_BOLT(17, 13.5, 90, Animation(10546), SpotAnim(457), 459, SpotAnim(2699), 218, 219, RuneSet(Rune.AIR, 2, Rune.CHAOS, 1)) {
        override fun getBaseDamage(caster: Entity): Int {
            return if (caster is Player && caster.equipment.glovesId == 777) 120 else baseDamage
        }
    },
    WIND_BLAST(41, 25.5, 130, Animation(10546), SpotAnim(457), 460, SpotAnim(2699), 216, 217, RuneSet(Rune.AIR, 3, Rune.DEATH, 1)),
    WIND_WAVE(62, 36.0, 170, Animation(10546), SpotAnim(457), 461, SpotAnim(2700), 222, 223, RuneSet(Rune.AIR, 5, Rune.BLOOD, 1)),
    WIND_SURGE(81, 75.0, 220, Animation(10546), SpotAnim(457), 462, SpotAnim(2700), 7866, 7867, RuneSet(Rune.AIR, 7, Rune.BLOOD, 1, Rune.DEATH, 1)),
    WATER_STRIKE(5, 7.5, 40, Animation(14220), SpotAnim(2701), 2703, SpotAnim(2708), 211, 212, RuneSet(Rune.AIR, 1, Rune.WATER, 1, Rune.MIND, 1)),
    WATER_BOLT(23, 16.5, 100, Animation(14220), SpotAnim(2701), 2704, SpotAnim(2709), 209, 210, RuneSet(Rune.AIR, 2, Rune.WATER, 2, Rune.CHAOS, 1)) {
        override fun getBaseDamage(caster: Entity): Int {
            return if (caster is Player && caster.equipment.glovesId == 777) 130 else baseDamage
        }
    },
    WATER_BLAST(47, 28.5, 140, Animation(14220), SpotAnim(2701), 2705, SpotAnim(2710), 207, 208, RuneSet(Rune.AIR, 3, Rune.WATER, 3, Rune.DEATH, 1)),
    WATER_WAVE(65, 37.5, 180, Animation(14220), SpotAnim(2701), 2706, SpotAnim(2711), 213, 214, RuneSet(Rune.AIR, 5, Rune.WATER, 7, Rune.BLOOD, 1)),
    WATER_SURGE(85, 80.0, 240, Animation(14220), SpotAnim(2701), 2707, SpotAnim(2712), 7834, 7822, RuneSet(Rune.AIR, 7, Rune.WATER, 10, Rune.DEATH, 1, Rune.BLOOD, 1)),
    EARTH_STRIKE(9, 9.5, 60, Animation(14209), SpotAnim(2713), 2718, SpotAnim(2723), 132, 133, RuneSet(Rune.AIR, 1, Rune.EARTH, 2, Rune.MIND, 1)),
    EARTH_BOLT(29, 19.5, 110, Animation(14209), SpotAnim(2714), 2719, SpotAnim(2724), 130, 131, RuneSet(Rune.AIR, 2, Rune.EARTH, 3, Rune.CHAOS, 1)) {
        override fun getBaseDamage(caster: Entity): Int {
            return if (caster is Player && caster.equipment.glovesId == 777) 140 else baseDamage
        }
    },
    EARTH_BLAST(53, 31.5, 150, Animation(14209), SpotAnim(2715), 2720, SpotAnim(2725), 128, 129, RuneSet(Rune.AIR, 3, Rune.EARTH, 4, Rune.DEATH, 1)),
    EARTH_WAVE(70, 40.0, 190, Animation(14209), SpotAnim(2716), 2721, SpotAnim(2726), 134, 135, RuneSet(Rune.AIR, 5, Rune.EARTH, 7, Rune.BLOOD, 1)),
    EARTH_SURGE(90, 85.0, 260, Animation(14209), SpotAnim(2717), 2722, SpotAnim(2727), 7914, 7919, RuneSet(Rune.AIR, 7, Rune.EARTH, 10, Rune.DEATH, 1, Rune.BLOOD, 1)),
    FIRE_STRIKE(13, 11.5, 80, Animation(2791), SpotAnim(2728), 2729, SpotAnim(2737), 160, 161, RuneSet(Rune.AIR, 2, Rune.FIRE, 3, Rune.MIND, 1)),
    FIRE_BOLT(35, 22.5, 120, Animation(2791), SpotAnim(2728), 2731, SpotAnim(2738), 157, 158, RuneSet(Rune.AIR, 3, Rune.FIRE, 4, Rune.CHAOS, 1)) {
        override fun getBaseDamage(caster: Entity): Int {
            return if (caster is Player && caster.equipment.glovesId == 777) 150 else baseDamage
        }
    },
    FIRE_BLAST(59, 34.5, 160, Animation(2791), SpotAnim(2728), 2733, SpotAnim(2739), 155, 156, RuneSet(Rune.AIR, 4, Rune.FIRE, 5, Rune.DEATH, 1)),
    FIRE_WAVE(75, 42.5, 200, Animation(2791), SpotAnim(2728), 2735, SpotAnim(2740), 162, 163, RuneSet(Rune.AIR, 5, Rune.FIRE, 7, Rune.BLOOD, 1)),
    FIRE_SURGE(95, 90.0, 280, Animation(2791), SpotAnim(2728), 2735, SpotAnim(2741), 7932, 7933, RuneSet(Rune.AIR, 7, Rune.FIRE, 10, Rune.DEATH, 1, Rune.BLOOD, 1)) {
        override fun onCast(caster: Entity, target: Entity) {
            World.sendProjectile(caster, target, 2736, -25, 50, 1.0)
            World.sendProjectile(caster, target, 2736, 25, 50, 1.0)
        }
    },
    CRUMBLE_UNDEAD(39, 24.5, 150, Animation(724), SpotAnim(145, 0, 96), 146, SpotAnim(147, 0, 96), 122, 124, RuneSet(Rune.AIR, 2, Rune.EARTH, 2, Rune.CHAOS, 1)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (target !is NPC || !target.definitions.isUndead) {
                player.sendMessage("This spell only affects skeletons, zombies, ghosts and shades.")
                return false
            }
            return true
        }
    },
    IBAN_BLAST(50, 30.0, 250, Animation(708), SpotAnim(87, 0, 96), 88, SpotAnim(89, 0, 96), 162, 1341, RuneSet(Rune.DEATH, 1, Rune.FIRE, 5)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (player.equipment.weaponId != 1409) {
                player.sendMessage("You need Iban's staff to cast this spell.")
                return false
            }
            return true
        }
    },
    MAGIC_DART(50, 31.5, 100, Animation(1575), SpotAnim(327, 0, 96), 328, SpotAnim(329, 0, 96), 1718, 174, RuneSet(Rune.MIND, 4, Rune.DEATH, 1)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (!Magic.isSlayerStaff(player.equipment.weaponId)) {
                player.sendMessage("You need a Slayer's staff to cast this spell.")
                return false
            }
            if (player.skills.getLevel(Constants.SLAYER) < 55) {
                player.sendMessage("You need at least 55 Slayer to cast this spell.")
                return false
            }
            return true
        }

        override fun getBaseDamage(caster: Entity): Int {
            if (caster is Player) return (100.0 + caster.skills.getLevel(Constants.MAGIC).toDouble()).toInt()
            return 100
        }
    },
    SARADOMIN_STRIKE(50, 34.5, 200, Animation(811), null, -1, SpotAnim(76, 0, 96), 1659, -1, RuneSet(Rune.AIR, 4, Rune.FIRE, 1, Rune.BLOOD, 2)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (player.equipment.weaponId != 2415) {
                player.sendMessage("You need a Saradomin staff to cast this spell.")
                return false
            }
            return true
        }

        override fun getBaseDamage(caster: Entity): Int {
            if (caster is Player) return if (caster.hasEffect(Effect.CHARGED) && caster.equipment.capeId == 2412) 300 else baseDamage
            return baseDamage
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            if (target is Player) target.prayer.drainPrayer(1.0)
        }
    },
    CLAWS_OF_GUTHIX(50, 34.5, 200, Animation(811), null, -1, SpotAnim(77, 0, 96), 1653, -1, RuneSet(Rune.AIR, 4, Rune.FIRE, 1, Rune.BLOOD, 2)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (player.equipment.weaponId != 2416) {
                player.sendMessage("You need a Guthix staff to cast this spell.")
                return false
            }
            return true
        }

        override fun getBaseDamage(caster: Entity): Int {
            if (caster is Player) return if (caster.hasEffect(Effect.CHARGED) && caster.equipment.capeId == 2413) 300 else baseDamage
            return baseDamage
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.DEFENSE, 0.05, 0.0)
        }
    },
    FLAMES_OF_ZAMORAK(50, 34.5, 200, Animation(811), null, -1, SpotAnim(78, 0, 96), 1655, -1, RuneSet(Rune.AIR, 4, Rune.FIRE, 1, Rune.BLOOD, 2)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (player.equipment.weaponId != 2417) {
                player.sendMessage("You need a Zamorak staff to cast this spell.")
                return false
            }
            return true
        }

        override fun getBaseDamage(caster: Entity): Int {
            if (caster is Player) return if (caster.hasEffect(Effect.CHARGED) && caster.equipment.capeId == 2414) 300 else baseDamage
            return baseDamage
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.MAGIC, 0.05, 0.0)
        }
    },
    STORM_OF_ARMADYL(77, 70.0, 160, Animation(10546), SpotAnim(457), 1019, SpotAnim(1019), 7866, 7867, RuneSet(Rune.ARMADYL, 1)) {
        override fun getCombatDelay(caster: Entity): Int {
            return if (caster is Player && caster.equipment.weaponId == 21777) 3 else 4
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.DEFENSE, 1, 0.0)
        }

        override fun getBaseDamage(caster: Entity): Int {
            if (caster is Player) return baseDamage + (caster.skills.getLevelForXp(Constants.MAGIC) - 77) * 5
            return baseDamage
        }
    },
    CONFUSE(3, 13.0, -1, Animation(711), SpotAnim(102, 0, 96), 103, SpotAnim(104, 0, 96), 119, 121, RuneSet(Rune.BODY, 1, Rune.WATER, 3, Rune.EARTH, 2)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (target is NPC) {
                if (target.attackLevel < target.combatDefinitions.attackLevel) {
                    player.sendMessage("That target is already weakened.")
                    return false
                }
            } else if (target is Player)
                if (target.skills.getLevel(Constants.ATTACK) < target.skills.getLevelForXp(Constants.ATTACK)) {
                    player.sendMessage("That target is already weakened.")
                    return false
                }
            return true
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.ATTACK, 0.05, 0.95)
        }
    },
    WEAKEN(11, 21.0, -1, Animation(711), SpotAnim(105, 0, 96), 106, SpotAnim(107, 0, 96), 3011, 3010, RuneSet(Rune.BODY, 1, Rune.WATER, 3, Rune.EARTH, 2)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (target is NPC) {
                if (target.strengthLevel < target.combatDefinitions.strengthLevel) {
                    player.sendMessage("That target is already weakened.")
                    return false
                }
            } else if (target is Player)
                if (target.skills.getLevel(Constants.STRENGTH) < target.skills.getLevelForXp(Constants.STRENGTH)) {
                    player.sendMessage("That target is already weakened.")
                    return false
                }
            return true
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.STRENGTH, 0.05, 0.95)
        }
    },
    CURSE(19, 29.0, -1, Animation(711), SpotAnim(108, 0, 96), 109, SpotAnim(110, 0, 96), 127, 126, RuneSet(Rune.BODY, 1, Rune.WATER, 2, Rune.EARTH, 3)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (target is NPC) {
                if (target.defenseLevel < target.combatDefinitions.defenseLevel) {
                    player.sendMessage("That target is already weakened.")
                    return false
                }
            } else if (target is Player)
                if (target.skills.getLevel(Constants.DEFENSE) < target.skills.getLevelForXp(Constants.DEFENSE)) {
                    player.sendMessage("That target is already weakened.")
                    return false
                }
            return true
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.DEFENSE, 0.05, 0.95)
        }
    },
    VULNERABILITY(66, 76.0, -1, Animation(711), SpotAnim(167, 0, 96), 168, SpotAnim(169, 0, 96), 3009, 3008, RuneSet(Rune.SOUL, 1, Rune.WATER, 5, Rune.EARTH, 5)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (target is NPC) {
                if (target.defenseLevel < target.combatDefinitions.defenseLevel) {
                    player.sendMessage("That target is already weakened.")
                    return false
                }
            } else if (target is Player)
                if (target.skills.getLevel(Constants.DEFENSE) < target.skills.getLevelForXp(Constants.DEFENSE)) {
                    player.sendMessage("That target is already weakened.")
                    return false
                }
            return true
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.DEFENSE, 0.1, 0.9)
        }
    },
    ENFEEBLE(73, 83.0, -1, Animation(711), SpotAnim(170, 0, 96), 171, SpotAnim(172, 0, 96), 148, 150, RuneSet(Rune.SOUL, 1, Rune.WATER, 8, Rune.EARTH, 8)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (target is NPC) {
                if (target.strengthLevel < target.combatDefinitions.strengthLevel) {
                    player.sendMessage("That target is already weakened.")
                    return false
                }
            } else if (target is Player)
                if (target.skills.getLevel(Constants.STRENGTH) < target.skills.getLevelForXp(Constants.STRENGTH)) {
                    player.sendMessage("That target is already weakened.")
                    return false
                }
            return true
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.STRENGTH, 0.1, 0.9)
        }
    },
    STUN(80, 90.0, -1, Animation(711), SpotAnim(173, 0, 96), 174, SpotAnim(245, 0, 96), 3004, 3005, RuneSet(Rune.SOUL, 1, Rune.WATER, 12, Rune.EARTH, 12)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (target is NPC) {
                if (target.attackLevel < target.combatDefinitions.attackLevel) {
                    player.sendMessage("That target is already weakened.")
                    return false
                }
            } else if (target is Player)
                if (target.skills.getLevel(Constants.ATTACK) < target.skills.getLevelForXp(Constants.ATTACK)) {
                    player.sendMessage("That target is already weakened.")
                    return false
                }
            return true
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.ATTACK, 0.1, 0.9)
        }
    },
    BIND(20, 30.0, 20, Animation(710), SpotAnim(177, 0, 96), 178, SpotAnim(181, 0, 96), 101, 99, RuneSet(Rune.NATURE, 2, Rune.WATER, 3, Rune.EARTH, 3)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.freeze(Ticks.fromSeconds(5), true)
        }
    },
    SNARE(50, 60.0, 30, Animation(710), SpotAnim(177, 0, 96), 178, SpotAnim(180, 0, 96), 3003, 3002, RuneSet(Rune.NATURE, 3, Rune.WATER, 4, Rune.EARTH, 4)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.freeze(Ticks.fromSeconds(10), true)
        }
    },
    ENTANGLE(79, 89.0, 50, Animation(710), SpotAnim(177, 0, 96), 178, SpotAnim(179, 0, 96), 151, 153, RuneSet(Rune.NATURE, 4, Rune.WATER, 5, Rune.EARTH, 5)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.freeze(Ticks.fromSeconds(20), true)
        }
    },
    TELEPORT_BLOCK(85, 80.0, 30, Animation(10503), SpotAnim(1841), 1842, SpotAnim(1843), 202, 203, RuneSet(Rune.CHAOS, 1, Rune.LAW, 1, Rune.DEATH, 1)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            if (target is Player) {
                if (target.prayer.isProtectingMage) target.addEffect(Effect.TELEBLOCK, Ticks.fromMinutes(2.5).toLong()) else target.addEffect(Effect.TELEBLOCK, Ticks.fromMinutes(5).toLong())
                target.sendMessage("You have been teleport blocked!")
            }
        }

        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (target !is Player) {
                player.sendMessage("This spell is only effective against other players.")
                return false
            }
            if (target.hasEffect(Effect.TELEBLOCK)) {
                player.sendMessage("That player is already affected by this spell.")
                return false
            }
            return true
        }
    },
    SMOKE_RUSH(50, 30.0, 130, Animation(1978), null, 384, SpotAnim(385, 0, 96), 183, 185, RuneSet(Rune.DEATH, 2, Rune.CHAOS, 2, Rune.FIRE, 1, Rune.AIR, 1)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            if (Utils.random(5) == 0) target.poison.makePoisoned(20)
        }
    },
    SMOKE_BURST(62, 36.0, 170, Animation(1979), null, -1, SpotAnim(389, 0, 96), 183, 182, RuneSet(Rune.DEATH, 2, Rune.CHAOS, 4, Rune.FIRE, 2, Rune.AIR, 2)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            if (Utils.random(5) == 0) target.poison.makePoisoned(20)
        }
    },
    SMOKE_BLITZ(74, 42.0, 230, Animation(1978), null, 386, SpotAnim(387, 0, 96), 183, 181, RuneSet(Rune.BLOOD, 2, Rune.DEATH, 2, Rune.FIRE, 2, Rune.AIR, 2)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            if (Utils.random(5) == 0) target.poison.makePoisoned(40)
        }
    },
    SMOKE_BARRAGE(86, 48.0, 270, Animation(1979), null, -1, SpotAnim(391, 0, 96), 183, 180, RuneSet(Rune.BLOOD, 2, Rune.DEATH, 4, Rune.FIRE, 4, Rune.AIR, 4)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            if (Utils.random(5) == 0) target.poison.makePoisoned(40)
        }
    },
    SHADOW_RUSH(52, 31.0, 140, Animation(1978), null, 378, SpotAnim(379), 178, 179, RuneSet(Rune.SOUL, 1, Rune.DEATH, 2, Rune.CHAOS, 2, Rune.AIR, 1)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.ATTACK, 0.1, 0.9)
        }
    },
    SHADOW_BURST(64, 37.0, 180, Animation(1979), null, -1, SpotAnim(382), 178, 177, RuneSet(Rune.SOUL, 2, Rune.DEATH, 2, Rune.CHAOS, 4, Rune.AIR, 1)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.ATTACK, 0.1, 0.9)
        }
    },
    SHADOW_BLITZ(76, 43.0, 240, Animation(1978), null, 380, SpotAnim(381), 178, 176, RuneSet(Rune.SOUL, 2, Rune.BLOOD, 2, Rune.DEATH, 2, Rune.AIR, 2)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.ATTACK, 0.15, 0.85)
        }
    },
    SHADOW_BARRAGE(88, 48.0, 280, Animation(1979), null, -1, SpotAnim(383), 178, 175, RuneSet(Rune.SOUL, 3, Rune.BLOOD, 2, Rune.DEATH, 4, Rune.AIR, 4)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.lowerStat(Constants.ATTACK, 0.15, 0.85)
        }
    },
    BLOOD_RUSH(56, 33.0, 150, Animation(1978), null, 374, SpotAnim(373), 106, 110, RuneSet(Rune.BLOOD, 1, Rune.DEATH, 2, Rune.CHAOS, 2)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            caster.heal((hit.damage * 0.25).toInt())
        }
    },
    BLOOD_BURST(68, 39.0, 210, Animation(1979), null, -1, SpotAnim(376), 106, 105, RuneSet(Rune.BLOOD, 2, Rune.DEATH, 2, Rune.CHAOS, 4)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            caster.heal((hit.damage * 0.25).toInt())
        }
    },
    BLOOD_BLITZ(80, 45.0, 250, Animation(1978), null, 374, SpotAnim(375), 106, 104, RuneSet(Rune.BLOOD, 4, Rune.DEATH, 2)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            caster.heal((hit.damage * 0.25).toInt())
        }
    },
    BLOOD_BARRAGE(92, 51.0, 290, Animation(1979), null, -1, SpotAnim(377), 106, 102, RuneSet(Rune.SOUL, 1, Rune.BLOOD, 4, Rune.DEATH, 4)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            caster.heal((hit.damage * 0.25).toInt())
        }
    },
    ICE_RUSH(58, 34.0, 160, Animation(1978), null, 360, SpotAnim(361), 171, 173, RuneSet(Rune.DEATH, 2, Rune.CHAOS, 2, Rune.WATER, 2)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.freeze(Ticks.fromSeconds(5), true)
        }
    },
    ICE_BURST(70, 40.0, 220, Animation(1979), null, -1, SpotAnim(363), 171, 170, RuneSet(Rune.DEATH, 2, Rune.CHAOS, 4, Rune.WATER, 4)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.freeze(Ticks.fromSeconds(10), true)
        }
    },
    ICE_BLITZ(82, 46.0, 260, Animation(1978), SpotAnim(366, 0, 96), 362, SpotAnim(367), 171, 169, RuneSet(Rune.BLOOD, 2, Rune.DEATH, 2, Rune.WATER, 3)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.freeze(Ticks.fromSeconds(15), true)
        }
    },
    ICE_BARRAGE(94, 52.0, 300, Animation(1979), SpotAnim(368, 0, 96), -1, SpotAnim(369), 171, 168, RuneSet(Rune.BLOOD, 2, Rune.DEATH, 4, Rune.WATER, 6)) {
        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            target.freeze(Ticks.fromSeconds(20), true)
        }
    },
    MIASMIC_RUSH(61, 36.0, 180, Animation(10513), SpotAnim(1845), 1846, SpotAnim(1847), 5368, 5365, RuneSet(Rune.SOUL, 1, Rune.CHAOS, 2, Rune.EARTH, 1)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (player.equipment.weaponId != 13867 && player.equipment.weaponId != 13869 && player.equipment.weaponId != 13941 && player.equipment.weaponId != 13943) {
                player.sendMessage("You need a Zuriel's staff to cast this spell.")
                return false
            }
            return true
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            if (target is Player) target.refreshMiasmicTimer(Ticks.fromSeconds(12))
        }
    },
    MIASMIC_BURST(73, 42.0, 240, Animation(10516), SpotAnim(1848), -1, SpotAnim(1849), 5366, 5372, RuneSet(Rune.SOUL, 2, Rune.CHAOS, 4, Rune.EARTH, 2)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (player.equipment.weaponId != 13867 && player.equipment.weaponId != 13869 && player.equipment.weaponId != 13941 && player.equipment.weaponId != 13943) {
                player.sendMessage("You need a Zuriel's staff to cast this spell.")
                return false
            }
            return true
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            if (target is Player) target.refreshMiasmicTimer(Ticks.fromSeconds(24))
        }
    },
    MIASMIC_BLITZ(85, 48.0, 280, Animation(10524), SpotAnim(1850), 1852, SpotAnim(1851), 5370, 5367, RuneSet(Rune.SOUL, 3, Rune.BLOOD, 2, Rune.EARTH, 1)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (player.equipment.weaponId != 13867 && player.equipment.weaponId != 13869 && player.equipment.weaponId != 13941 && player.equipment.weaponId != 13943) {
                player.sendMessage("You need a Zuriel's staff to cast this spell.")
                return false
            }
            return true
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            if (target is Player) target.refreshMiasmicTimer(Ticks.fromSeconds(36))
        }
    },
    MIASMIC_BARRAGE(97, 54.0, 320, Animation(10518), SpotAnim(1853), -1, SpotAnim(1854), 5371, 5369, RuneSet(Rune.SOUL, 4, Rune.BLOOD, 4, Rune.EARTH, 4)) {
        override fun extraReqs(player: Player, target: Entity): Boolean {
            if (player.equipment.weaponId != 13867 && player.equipment.weaponId != 13869 && player.equipment.weaponId != 13941 && player.equipment.weaponId != 13943) {
                player.sendMessage("You need a Zuriel's staff to cast this spell.")
                return false
            }
            return true
        }

        override fun onHit(caster: Entity, target: Entity, hit: Hit) {
            if (target is Player) target.refreshMiasmicTimer(Ticks.fromSeconds(48))
        }
    };
    constructor(
        req: Int,
        splashXp: Double,
        baseDamage: Int,
        castAnim: Animation,
        castSpotAnim: SpotAnim?,
        projAnim: Int,
        hitSpotAnim: SpotAnim,
        castSound: Int,
        landSound: Int,
        runes: RuneSet
    ) : this(req, splashXp, baseDamage, castAnim, castSpotAnim, projAnim, hitSpotAnim, castSound, landSound, -1, runes)

    private fun getCastAnim(caster: Entity): Animation {
        return when (this) {
            EARTH_BLAST,
            EARTH_BOLT,
            EARTH_STRIKE,
            EARTH_SURGE,
            EARTH_WAVE -> if (caster is Player && caster.equipment.weaponName.lowercase(Locale.getDefault()).contains("staff")) Animation(14222) else castAnim
            WIND_BLAST,
            WIND_BOLT,
            WIND_RUSH,
            WIND_STRIKE,
            WIND_SURGE,
            WIND_WAVE -> if (caster is Player && caster.equipment.weaponName.lowercase(Locale.getDefault()).contains("staff")) Animation(14221) else castAnim
            else -> castAnim
        }
    }

    val isFireSpell: Boolean
        get() =
            when (this) {
                FIRE_STRIKE,
                FIRE_BOLT,
                FIRE_BLAST,
                FIRE_WAVE,
                FIRE_SURGE -> true
                else -> false
            }

    val isWaterSpell: Boolean
        get() =
            when (this) {
                WATER_STRIKE,
                WATER_BOLT,
                WATER_BLAST,
                WATER_WAVE,
                WATER_SURGE -> true
                else -> false
            }

    val isEarthSpell: Boolean
        get() =
            when (this) {
                EARTH_STRIKE,
                EARTH_BOLT,
                EARTH_BLAST,
                EARTH_WAVE,
                EARTH_SURGE -> true
                else -> false
            }

    val isAirSpell: Boolean
        get() =
            when (this) {
                WIND_STRIKE,
                WIND_BOLT,
                WIND_BLAST,
                WIND_WAVE,
                WIND_SURGE -> true
                else -> false
            }

    private fun getCastSpotAnim(caster: Entity): SpotAnim? {
        return when (this) {
            WATER_BLAST,
            WATER_BOLT,
            WATER_STRIKE,
            WATER_SURGE,
            WATER_WAVE -> (if (caster is Player && caster.equipment.weaponName.lowercase(Locale.getDefault()).contains("staff")) SpotAnim(2702) else castSpotAnim)
            else -> castSpotAnim
        }
    }

    open fun getBaseDamage(caster: Entity): Int {
        return baseDamage
    }

    fun cast(caster: Entity, target: Entity): Int {
        val castAnim = getCastAnim(caster)
        val castSpotAnim = getCastSpotAnim(caster)
        caster.anim(castAnim)
        if (castSpotAnim != null)
            caster.spotAnim(castSpotAnim)
        if (castSound != -1) caster.soundEffect(target, castSound, true)
        onCast(caster, target)
        return World.sendProjectile(caster, target, projAnim, if ((castSpotAnim?.height ?: 0) > 50) 20 else 0, 50, 1.0).taskDelay
    }

    open fun onCast(caster: Entity, target: Entity) {}

    open fun onHit(caster: Entity, target: Entity, hit: Hit) {}

    open fun getCombatDelay(caster: Entity): Int {
        if (caster is Player)
            if (caster.controllerManager.isIn(DungeonController::class.java) && caster.dungManager.activePerk == KinshipPerk.BLITZER) {
                val chance: Int = 10 * caster.dungManager.getKinshipTier(KinshipPerk.BLITZER)
                if (Utils.random(100) < chance) return 3
            }
        return 4
    }

    val isAOE: Boolean
        get() =
            when (this) {
                BLOOD_BARRAGE,
                BLOOD_BURST,
                ICE_BARRAGE,
                ICE_BURST,
                MIASMIC_BARRAGE,
                MIASMIC_BURST,
                SHADOW_BARRAGE,
                SHADOW_BURST,
                SMOKE_BARRAGE,
                SMOKE_BURST -> true
                else -> false
            }

    open fun extraReqs(player: Player, target: Entity): Boolean {
        return true
    }

    fun canCast(player: Player, target: Entity): Boolean {
        if (player.skills.getLevel(Constants.MAGIC) < req) {
            player.sendMessage("You need a magic level of $req to cast this spell.")
            return false
        }
        if (!extraReqs(player, target)) return false
        return runeSet.meetsRequirements(player)
    }

    companion object {
        private val SPELL_MAP: Map<Int, Map<Int, CombatSpell>> = mapOf(
            //Modern spellbook
            192 to mapOf(
                98 to WIND_RUSH,
                25 to WIND_STRIKE,
                26 to CONFUSE,
                28 to WATER_STRIKE,
                30 to EARTH_STRIKE,
                31 to WEAKEN,
                32 to FIRE_STRIKE,
                34 to WIND_BOLT,
                35 to CURSE,
                36 to BIND,
                39 to WATER_BOLT,
                42 to EARTH_BOLT,
                45 to FIRE_BOLT,
                47 to CRUMBLE_UNDEAD,
                49 to WIND_BLAST,
                52 to WATER_BLAST,
                54 to IBAN_BLAST,
                55 to SNARE,
                56 to MAGIC_DART,
                58 to EARTH_BLAST,
                63 to FIRE_BLAST,
                66 to SARADOMIN_STRIKE,
                67 to CLAWS_OF_GUTHIX,
                68 to FLAMES_OF_ZAMORAK,
                70 to WIND_WAVE,
                73 to WATER_WAVE,
                75 to VULNERABILITY,
                77 to EARTH_WAVE,
                78 to ENFEEBLE,
                80 to FIRE_WAVE,
                99 to STORM_OF_ARMADYL,
                81 to ENTANGLE,
                82 to STUN,
                84 to WIND_SURGE,
                86 to TELEPORT_BLOCK,
                87 to WATER_SURGE,
                89 to EARTH_SURGE,
                91 to FIRE_SURGE,
            ),

            //Ancient spellbook
            193 to mapOf(
                28 to SMOKE_RUSH,
                32 to SHADOW_RUSH,
                24 to BLOOD_RUSH,
                20 to ICE_RUSH,
                36 to MIASMIC_RUSH,
                30 to SMOKE_BURST,
                34 to SHADOW_BURST,
                26 to BLOOD_BURST,
                22 to ICE_BURST,
                38 to MIASMIC_BURST,
                29 to SMOKE_BLITZ,
                33 to SHADOW_BLITZ,
                25 to BLOOD_BLITZ,
                21 to ICE_BLITZ,
                37 to MIASMIC_BLITZ,
                31 to SMOKE_BARRAGE,
                35 to SHADOW_BARRAGE,
                27 to BLOOD_BARRAGE,
                23 to ICE_BARRAGE,
                39 to MIASMIC_BARRAGE,
            ),

            //Lunar spellbook
            430 to mapOf(),

            //Dungeoneering spellbook
            950 to mapOf(
                25 to WIND_STRIKE,
                26 to CONFUSE,
                27 to WATER_STRIKE,
                28 to EARTH_STRIKE,
                29 to WEAKEN,
                30 to FIRE_STRIKE,
                32 to WIND_BOLT,
                33 to CURSE,
                34 to BIND,
                36 to WATER_BOLT,
                37 to EARTH_BOLT,
                41 to FIRE_BOLT,
                42 to WIND_BLAST,
                43 to WATER_BLAST,
                44 to SNARE,
                45 to EARTH_BLAST,
                47 to FIRE_BLAST,
                48 to WIND_WAVE,
                49 to WATER_WAVE,
                50 to VULNERABILITY,
                54 to EARTH_WAVE,
                56 to ENFEEBLE,
                58 to FIRE_WAVE,
                59 to ENTANGLE,
                60 to STUN,
                61 to WIND_SURGE,
                62 to WATER_SURGE,
                63 to EARTH_SURGE,
                67 to FIRE_SURGE,
            )
        )

        @JvmStatic
        fun forId(spellBook: Int, spellId: Int): CombatSpell? {
            return SPELL_MAP[spellBook]?.get(spellId)
        }
    }
}
