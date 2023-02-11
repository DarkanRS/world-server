package com.rs.game.content.combat.special_attacks;

import com.rs.game.World;
import com.rs.game.content.bosses.pestqueen.attack.Attack;
import com.rs.game.content.combat.*;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.interactions.PlayerCombatInteraction;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.game.content.combat.special_attacks.SpecialAttack.Type;
import com.rs.utils.Ticks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.rs.game.content.combat.PlayerCombat.delayNormalHit;
import static com.rs.game.content.combat.PlayerCombat.delayMagicHit;
import static com.rs.game.content.combat.PlayerCombat.delayHit;
import static com.rs.game.content.combat.PlayerCombat.dropAmmo;
import static com.rs.game.content.combat.PlayerCombat.getRandomMaxHit;
import static com.rs.game.content.combat.PlayerCombat.getMaxHit;
import static com.rs.game.content.combat.PlayerCombat.getMultiAttackTargets;
import static com.rs.game.content.combat.PlayerCombat.addXp;
import static com.rs.game.content.combat.PlayerCombat.attackTarget;

@PluginEventHandler
public class SpecialAttacks {
    private static Map<Integer, SpecialAttack> SPECIAL_ATTACKS = new HashMap<>();

    @ServerStartupEvent
    public static void loadSpecs() {
        /**
         * Instant Specials
         */
        //Granite maul/Granite mace
        addSpec(new int[] { 4153, 14679 }, new SpecialAttack(true, 50, (player, target) -> {
            target = (player.getInteractionManager().getInteraction() instanceof PlayerCombatInteraction combat) ? combat.getAction().getTarget() : player.getTempAttribs().getO("last_target");
            if (target != null) {
                if (!(target instanceof NPC n && n.isForceMultiAttacked()))
                    if (!target.isAtMultiArea() || !player.isAtMultiArea())
                        if ((player.getAttackedBy() != target && player.inCombat()) || (target.getAttackedBy() != player && target.inCombat()))
                            return 0;
                if (target.isDead() || target.hasFinished())
                    return 0;
                if (!(player.getInteractionManager().getInteraction() instanceof PlayerCombatInteraction combat) || combat.getAction().getTarget() != target) {
                    player.resetWalkSteps();
                    player.getInteractionManager().setInteraction(new PlayerCombatInteraction(player, target));
                }
                PlayerCombat pcb = null;
                if (player.getInteractionManager().getInteraction() != null && player.getInteractionManager().getInteraction() instanceof PlayerCombatInteraction pci)
                    pcb = pci.getAction();
                if (pcb == null || !player.inMeleeRange(target))
                    return 0;
                player.getCombatDefinitions().drainSpec(50);
                player.setNextAnimation(new Animation(player.getEquipment().getWeaponId() == 4153 ? 1667 : 10505));
                if (player.getEquipment().getWeaponId() == 4153)
                    player.setNextSpotAnim(new SpotAnim(340, 0, 96 << 16));
                delayNormalHit(target, getRandomMaxHit(player, target, false, true, 1.0, 1.0));
                return 0;
            }
            return 0;
        }));

        //Dragon battleaxe
        addSpec(new int[] { 1377, 13472 }, new SpecialAttack(true, 100, (player, target) -> {
            player.setNextAnimation(new Animation(1056));
            player.setNextSpotAnim(new SpotAnim(246));
            player.setNextForceTalk(new ForceTalk("Raarrrrrgggggghhhhhhh!"));
            int defence = (int) (player.getSkills().getLevelForXp(Constants.DEFENSE) * 0.90D);
            int attack = (int) (player.getSkills().getLevelForXp(Constants.ATTACK) * 0.90D);
            int range = (int) (player.getSkills().getLevelForXp(Constants.RANGE) * 0.90D);
            int magic = (int) (player.getSkills().getLevelForXp(Constants.MAGIC) * 0.90D);
            int strength = (int) (player.getSkills().getLevelForXp(Constants.STRENGTH) * 1.2D);
            player.getSkills().set(Constants.DEFENSE, defence);
            player.getSkills().set(Constants.ATTACK, attack);
            player.getSkills().set(Constants.RANGE, range);
            player.getSkills().set(Constants.MAGIC, magic);
            player.getSkills().set(Constants.STRENGTH, strength);
            player.getCombatDefinitions().drainSpec(100);
            return 0;
        }));

        //Excalibur
        addSpec(new int[] { 35, 8280, 14632 }, new SpecialAttack(true, 100, (player, target) -> {
            player.setNextAnimation(new Animation(1168));
            player.setNextSpotAnim(new SpotAnim(247));
            player.setNextForceTalk(new ForceTalk("For Camelot!"));
            final boolean enhanced = player.getEquipment().getWeaponId() == 14632;
            player.getSkills().set(Constants.DEFENSE, enhanced ? (int) (player.getSkills().getLevelForXp(Constants.DEFENSE) * 1.15D) : (player.getSkills().getLevel(Constants.DEFENSE) + 8));
            WorldTasks.schedule(new WorldTask() {
                int count = 5;

                @Override
                public void run() {
                    if (player.isDead() || player.hasFinished() || player.getHitpoints() >= player.getMaxHitpoints()) {
                        stop();
                        return;
                    }
                    player.heal(enhanced ? 80 : 40);
                    if (count-- == 0) {
                        stop();
                        return;
                    }
                }
            }, 4, 2);
            player.getCombatDefinitions().drainSpec(100);
            return 0;
        }));

        //Staff of light
        addSpec(new int[] { 15486, 22207, 22209, 22211, 22213 }, new SpecialAttack(true, 100, (player, target) -> {
            player.setNextAnimation(new Animation(12804));
            player.setNextSpotAnim(new SpotAnim(2319));// 2320
            player.setNextSpotAnim(new SpotAnim(2321));
            player.getTempAttribs().setL("SOL_SPEC", System.currentTimeMillis() + 60000);
            player.getCombatDefinitions().drainSpec(100);
            return 0;
        }));

        /**
         * MAGIC WEAPONS
         */
        //Mindspike
        addSpec(new int[] { 23044, 23045, 23046, 23047 }, new SpecialAttack(Type.MAGIC, 75, (player, target) -> {
            delayMagicHit(target, CombatSpell.WIND_RUSH.cast(player, target), Hit.magic(player, 50).setMaxHit(50), () -> target.setNextSpotAnim(CombatSpell.WIND_RUSH.getHitSpotAnim()), null, null);
            return 3;
        }));

        /**
         * RANGED WEAPONS
         */
        addSpec(RangedWeapon.QUICK_BOW.getIds(), new SpecialAttack(Type.RANGE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(426));
            player.setNextSpotAnim(new SpotAnim(97));
            WorldProjectile p = World.sendProjectile(player, target, 1099, 20, 50, 1, proj -> target.setNextSpotAnim(new SpotAnim(1100, 0, 100)));
            WorldProjectile p2 = World.sendProjectile(player, target, 1099, 30, 50, 1.5, proj -> target.setNextSpotAnim(new SpotAnim(1100, 0, 100)));
            delayHit(target, p.getTaskDelay(), Hit.range(player, 25));
            delayHit(target, p2.getTaskDelay(), Hit.range(player, 25));
            return PlayerCombat.getRangeCombatDelay(player);
        }));

        addSpec(RangedWeapon.ZAMORAK_BOW.getIds(), new SpecialAttack(Type.RANGE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(426));
            player.setNextSpotAnim(new SpotAnim(97));
            WorldProjectile p = World.sendProjectile(player, target, 100, 20, 50, 1.5);
            delayHit(target, p.getTaskDelay(), getRandomMaxHit(player, target, true));
            dropAmmo(player, target, Equipment.AMMO, 1);
            return PlayerCombat.getRangeCombatDelay(player);
        }));

        addSpec(RangedWeapon.GUTHIX_BOW.getIds(), new SpecialAttack(Type.RANGE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(426));
            player.setNextSpotAnim(new SpotAnim(95));
            WorldProjectile p = World.sendProjectile(player, target, 98, 20, 50, 1.5);
            delayHit(target, p.getTaskDelay(), getRandomMaxHit(player, target, true));
            dropAmmo(player, target, Equipment.AMMO, 1);
            return PlayerCombat.getRangeCombatDelay(player);
        }));

        addSpec(RangedWeapon.SARADOMIN_BOW.getIds(), new SpecialAttack(Type.RANGE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(426));
            player.setNextSpotAnim(new SpotAnim(96));
            WorldProjectile p = World.sendProjectile(player, target, 99, 20, 50, 1.5);
            delayHit(target, p.getTaskDelay(), getRandomMaxHit(player, target, true));
            dropAmmo(player, target, Equipment.AMMO, 1);
            return PlayerCombat.getRangeCombatDelay(player);
        }));

        //TODO check and make sure hits aren't cancer calculated using new weapons after swap
        addSpec(RangedWeapon.RUNE_THROWNAXE.getIds(), new SpecialAttack(Type.RANGE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(9055));
            WorldProjectile p1 = World.sendProjectile(player, target, 258, 20, 50, 1);
            delayHit(target, p1.getTaskDelay(), getRandomMaxHit(player, target, true));
            WorldTasks.schedule(p1.getTaskDelay(), () -> {
                for (Entity next : getMultiAttackTargets(player, target, 5, 1, false)) {
                    WorldProjectile p2 = World.sendProjectile(target, next, 258, 20, 50, 1);
                    WorldTasks.schedule(p2.getTaskDelay(), () -> {
                        next.applyHit(getRandomMaxHit(player, next, true));
                        for (Entity next2 : getMultiAttackTargets(player, next, 5, 1, false)) {
                            WorldProjectile p3 = World.sendProjectile(next, next2, 258, 20, 50, 1);
                            WorldTasks.schedule(p3.getTaskDelay(), () -> {
                                next2.applyHit(getRandomMaxHit(player, next2, true));
                                for (Entity next3 : getMultiAttackTargets(player, next2, 5, 1, false)) {
                                    WorldProjectile p4 = World.sendProjectile(next2, next3, 258, 20, 50, 1);
                                    WorldTasks.schedule(p4.getTaskDelay(), () -> {
                                        next3.applyHit(getRandomMaxHit(player, next3, true));
                                        for (Entity next4 : getMultiAttackTargets(player, next3, 5, 1, false)) {
                                            WorldProjectile p5 = World.sendProjectile(next3, next4, 258, 20, 50, 1);
                                            WorldTasks.schedule(p5.getTaskDelay(), () -> {
                                                next4.applyHit(getRandomMaxHit(player, next4, true));
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
            return PlayerCombat.getRangeCombatDelay(player);
        }));

        addSpec(Stream.of(RangedWeapon.MAGIC_BOW.getIds(), RangedWeapon.MAGIC_LONGBOW.getIds(), RangedWeapon.MAGIC_COMP_BOW.getIds()).flatMapToInt(Arrays::stream).toArray(), new SpecialAttack(Type.RANGE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(1074));
            player.setNextSpotAnim(new SpotAnim(250, 10, 100));
            WorldProjectile p = World.sendProjectile(player, target, 249, 20, 20, 2);
            WorldProjectile p2 = World.sendProjectile(player, target, 249, 15, 50, 1.6);
            delayHit(target, p.getTaskDelay(), getRandomMaxHit(player, target, true));
            delayHit(target, p2.getTaskDelay(), getRandomMaxHit(player, target, true));
            dropAmmo(player, target, Equipment.AMMO, 2);
            return PlayerCombat.getRangeCombatDelay(player);
        }));

        addSpec(RangedWeapon.HAND_CANNON.getIds(), new SpecialAttack(Type.RANGE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(12175));
            player.setNextSpotAnim(new SpotAnim(2138));
            WorldProjectile p = World.sendProjectile(player, target, 2143, 0, 50, 1.5);
            delayHit(target, p.getTaskDelay(), getRandomMaxHit(player, target, true));
            return 1;
        }));

        addSpec(RangedWeapon.DORGESHUUN_CBOW.getIds(), new SpecialAttack(Type.RANGE, 000000000, (player, target) -> {
            player.setNextAnimation(RangedWeapon.DORGESHUUN_CBOW.getAttackAnimation());
            SpotAnim attackSpotAnim = RangedWeapon.DORGESHUUN_CBOW.getAttackSpotAnim(player, AmmoType.forId(player.getEquipment().getAmmoId()));
            if (attackSpotAnim != null)
                player.setNextSpotAnim(attackSpotAnim);
            Hit hit = getRandomMaxHit(player, target, true, true, 1.0, 1.3);
            if (hit.getDamage() > 0)
                target.lowerStat(Skills.DEFENSE, hit.getDamage()/10, 0.0);
            WorldProjectile p = World.sendProjectile(player, target, 698, 20, 50, 1);
            delayHit(target, p.getTaskDelay(), hit);
            dropAmmo(player, target, Equipment.AMMO, 1);
            return PlayerCombat.getRangeCombatDelay(player);
        }));

        addSpec(RangedWeapon.DARK_BOW.getIds(), new SpecialAttack(Type.RANGE, 000000000, (player, target) -> {
            int ammoId = player.getEquipment().getAmmoId();
            player.setNextAnimation(RangedWeapon.DARK_BOW.getAttackAnimation());
            SpotAnim attackSpotAnim = RangedWeapon.DARK_BOW.getAttackSpotAnim(player, AmmoType.forId(player.getEquipment().getAmmoId()));
            if (attackSpotAnim != null)
                player.setNextSpotAnim(attackSpotAnim);
            if (ammoId == 11212) {
                Hit hit1 = getRandomMaxHit(player, target, true, true, 1.0, 1.5);
                if (hit1.getDamage() < 80)
                    hit1.setDamage(80);
                Hit hit2 = getRandomMaxHit(player, target, true, true, 1.0, 1.5);
                if (hit2.getDamage() < 80)
                    hit2.setDamage(80);
                WorldProjectile p = World.sendProjectile(player, target, 1099, 20, 50, 1, proj -> target.setNextSpotAnim(new SpotAnim(1100, 0, 100)));
                WorldProjectile p2 = World.sendProjectile(player, target, 1099, 30, 50, 1.5, proj -> target.setNextSpotAnim(new SpotAnim(1100, 0, 100)));
                delayHit(target, p.getTaskDelay(), hit1);
                delayHit(target, p2.getTaskDelay(), hit2);
            } else {
                Hit hit1 = getRandomMaxHit(player, target, true, true, 1.0, 1.3);
                if (hit1.getDamage() < 50)
                    hit1.setDamage(50);
                Hit hit2 = getRandomMaxHit(player, target, true, true, 1.0, 1.3);
                if (hit2.getDamage() < 50)
                    hit2.setDamage(50);
                WorldProjectile p = World.sendProjectile(player, target, 1101, 20, 50, 1);
                WorldProjectile p2 = World.sendProjectile(player, target, 1101, 30, 50, 1.5);
                delayHit(target, p.getTaskDelay(), hit1);
                delayHit(target, p2.getTaskDelay(), hit2);
            }
            dropAmmo(player, target, Equipment.AMMO, 2);
            return PlayerCombat.getRangeCombatDelay(player);
        }));

        addSpec(RangedWeapon.ZANIKS_CROSSBOW.getIds(), new SpecialAttack(Type.RANGE, 000000000, (player, target) -> {
            player.setNextAnimation(RangedWeapon.ZANIKS_CROSSBOW.getAttackAnimation());
            player.setNextSpotAnim(new SpotAnim(1714));
            WorldProjectile p = World.sendProjectile(player, target, 2001, 20, 50, 1.5);
            Hit hit = getRandomMaxHit(player, target, true, true, 1.0, 1.0);
            delayHit(target, p.getTaskDelay(), Hit.range(player, hit.getDamage() + 30 + Utils.getRandomInclusive(120)));
            dropAmmo(player, target);
            return PlayerCombat.getRangeCombatDelay(player);
        }));

        addSpec(RangedWeapon.MORRIGANS_JAVELIN.getIds(), new SpecialAttack(Type.RANGE, 000000000, (player, target) -> {
            player.setNextSpotAnim(new SpotAnim(1836));
            player.setNextAnimation(new Animation(10501));
            WorldProjectile p = World.sendProjectile(player, target, 1837, 20, 50, 1.5);
            final Hit hit = getRandomMaxHit(player, target, true, true, 1.0, 1.0);
            delayHit(target, p.getTaskDelay(), hit);
            if (hit.getDamage() > 0) {
                final Entity finalTarget = target;
                WorldTasks.schedule(new WorldTask() {
                    int damage = hit.getDamage();

                    @Override
                    public void run() {
                        if (finalTarget.isDead() || finalTarget.hasFinished()) {
                            stop();
                            return;
                        }
                        if (damage > 50) {
                            damage -= 50;
                            finalTarget.applyHit(Hit.flat(player, 50));
                        } else {
                            finalTarget.applyHit(Hit.flat(player, damage));
                            stop();
                        }
                    }
                }, 4, 2);
            }
            dropAmmo(player, target, Equipment.WEAPON, 1);
            return PlayerCombat.getRangeCombatDelay(player);
        }));

        addSpec(RangedWeapon.MORRIGANS_THROWING_AXE.getIds(), new SpecialAttack(Type.RANGE, 000000000, (player, target) -> {
            player.setNextSpotAnim(new SpotAnim(1838));
            player.setNextAnimation(new Animation(10504));
            WorldProjectile p = World.sendProjectile(player, target, 1839, 20, 50, 1.5);
            delayHit(target, p.getTaskDelay(), getRandomMaxHit(player, target, true, true, 1.0, 1.0));
            dropAmmo(player, target, Equipment.WEAPON, 1);
            return PlayerCombat.getRangeCombatDelay(player);
        }));

        /**
         * MELEE WEAPONS
         */
        //Vine whip
        addSpec(new int[] { 21371, 21372, 21373, 21374, 21375 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            final AttackStyle attackStyle = player.getCombatDefinitions().getAttackStyle();
            final WorldTile tile = WorldTile.of(target.getX(), target.getY(), target.getPlane());
            player.setNextAnimation(new Animation(11971));
            player.setNextSpotAnim(new SpotAnim(476));
            WorldTasks.scheduleTimer(tick -> {
                if (player == null || player.hasFinished())
                    return false;
                if (tick % 5 == 0) {
                    World.sendSpotAnim(player, new SpotAnim(478), tile);
                    for (Entity entity : getMultiAttackTargets(player, WorldTile.of(target.getTile()), 1, 9)) {
                        Hit hit = getRandomMaxHit(player, entity, 0, getMaxHit(player, target, 21371, attackStyle, false, 0.33), 21371, attackStyle, false, true, 1.25);
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
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Abyssal whip
        addSpec(new int[] { 4151, 15441, 15442, 15443, 15444, 23691 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(11971));
            target.setNextSpotAnim(new SpotAnim(2108, 0, 100));
            if (target instanceof Player p2)
                p2.setRunEnergy(p2.getRunEnergy() > 25 ? p2.getRunEnergy() - 25 : 0);
            delayNormalHit(target, getRandomMaxHit(player, target, false, true, 1.25, 1.0));
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Saradomin sword
        addSpec(new int[] { 11730 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(11993));
            target.setNextSpotAnim(new SpotAnim(1194));
            delayNormalHit(target, new Hit(player, 50 + Utils.getRandomInclusive(100), HitLook.MELEE_DAMAGE).setMaxHit(150));
            delayNormalHit(target, getRandomMaxHit(player, target, false, true, 2.0, 1.1));
            player.soundEffect(3853);
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Dragon spear
        addSpec(new int[] { 1249, 1263, 3176, 5716, 5730, 13770, 13772, 13774, 13776 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
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
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Keenblade
        addSpec(new int[] { 23042 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(12019));
            player.setNextSpotAnim(new SpotAnim(2109));
            delayNormalHit(target, new Hit(player, 50, HitLook.MELEE_DAMAGE).setMaxHit(50));
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Saradomin godsword
        addSpec(new int[] { 11698, 23681 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(12019));
            player.setNextSpotAnim(new SpotAnim(2109));
            Hit hit = getRandomMaxHit(player, target, false, true, 2.0, 1.1);
            player.heal(hit.getDamage() / 2);
            player.getPrayer().restorePrayer((hit.getDamage() / 4) * 10);
            delayNormalHit(target, hit);
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Bandos godsword
        addSpec(new int[] { 11696, 23680 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(11991));
            player.setNextSpotAnim(new SpotAnim(2114));
            Hit hit2 = getRandomMaxHit(player, target, false, true, 2.0, 1.1);
            delayNormalHit(target, hit2);

            if (target instanceof Player other) {
                int amountLeft;
                if ((amountLeft = other.getSkills().drainLevel(Constants.DEFENSE, hit2.getDamage() / 10)) > 0)
                    if ((amountLeft = other.getSkills().drainLevel(Constants.STRENGTH, amountLeft)) > 0)
                        if ((amountLeft = other.getSkills().drainLevel(Constants.PRAYER, amountLeft)) > 0)
                            if ((amountLeft = other.getSkills().drainLevel(Constants.ATTACK, amountLeft)) > 0)
                                if ((amountLeft = other.getSkills().drainLevel(Constants.MAGIC, amountLeft)) > 0)
                                    if (other.getSkills().drainLevel(Constants.RANGE, amountLeft) > 0)
                                        return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
            } else if (target instanceof NPC n)
                if (hit2.getDamage() != 0)
                    n.lowerDefense(hit2.getDamage() / 10, 0.0);
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Ancient mace
        addSpec(new int[] { 11061 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(6147));
            player.setNextSpotAnim(new SpotAnim(1052));
            Hit hit3 = getRandomMaxHit(player, target, false, true, 1.0, 1.0);
            delayNormalHit(target, hit3);
            if (target instanceof Player other)
                other.getPrayer().drainPrayer(hit3.getDamage());
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Armadyl godsword
        addSpec(new int[] { 11694, 23679 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(11989));
            player.setNextSpotAnim(new SpotAnim(2113));
            delayNormalHit(target, getRandomMaxHit(player, target, false, true, 2.0, 1.25));
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Vesta's longsword
        addSpec(new int[] { 13899, 13901 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(10502));
            delayNormalHit(target, getRandomMaxHit(player, target, false, true, 2.0, 1.20));
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Statius' warhammer
        addSpec(new int[] { 13902, 13904 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            Hit hit1 = getRandomMaxHit(player, target, false, true, 1.0, 1.25);
            player.setNextAnimation(new Animation(10505));
            player.setNextSpotAnim(new SpotAnim(1840));
            delayNormalHit(target, hit1);

            if (hit1.getDamage() != 0)
                if (target instanceof NPC n)
                    n.lowerDefense(0.30, 0.0);
                else if (target instanceof Player p)
                    p.getSkills().adjustStat(0, -0.30, Constants.DEFENSE);
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Vesta's spear
        addSpec(new int[] { 13905, 13907 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(10499));
            player.setNextSpotAnim(new SpotAnim(1835));
            delayNormalHit(target, getRandomMaxHit(player, target, false, true, 1.0, 1.1));
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Dragon 2h sword
        addSpec(new int[] { 7158 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            final AttackStyle attackStyle = player.getCombatDefinitions().getAttackStyle();
            player.setNextAnimation(new Animation(7078));
            player.setNextSpotAnim(new SpotAnim(1225));
            attackTarget(target, getMultiAttackTargets(player, target, 1, 20), () -> {
                delayHit(target, 1, 7158, attackStyle, getRandomMaxHit(player, target, 7158, attackStyle, true, true, 1.0, 1.2));
                return true;
            });
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Korasi's sword
        addSpec(new int[] { 18786, 19784 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(14788));
            player.setNextSpotAnim(new SpotAnim(1729));
            int damage = getMaxHit(player, target, false, 1.0);
            double multiplier = 0.5 + Math.random();
            int maxHit = (int) (damage * 1.5);
            damage *= multiplier;
            delayNormalHit(target, new Hit(player, damage, HitLook.MAGIC_DAMAGE).setMaxHit(maxHit));
            WorldTasks.schedule(0, () -> target.setNextSpotAnim(new SpotAnim(1730)));
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Zamorak godsword
        addSpec(new int[] { 11700, 13453, 23682 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            Hit hit4 = getRandomMaxHit(player, target, false, true, 2.0, 1.1);
            player.setNextAnimation(new Animation(7070));
            player.setNextSpotAnim(new SpotAnim(1221));
            if (hit4.getDamage() != 0 && target.getSize() <= 1) {
                target.setNextSpotAnim(new SpotAnim(2104));
                target.freeze(Ticks.fromSeconds(18), false);
            }
            delayNormalHit(target, hit4);
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Rune claws
        addSpec(new int[] { 3101 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(10961));
            //player.setNextSpotAnim(new SpotAnim(1950));
            Hit[] hits = { new Hit(player, 0, HitLook.MELEE_DAMAGE), new Hit(player, 0, HitLook.MELEE_DAMAGE) };
            int maxHit1 = getMaxHit(player, target, false, 1.0);
            Hit base = getRandomMaxHit(player, target, maxHit1 / 2, maxHit1, false, true, 1.0);
            if (base.getDamage() > 0)
                hits = new Hit[] { base, new Hit(player, base.getDamage() / 2, HitLook.MELEE_DAMAGE), new Hit(player, (base.getDamage() / 2) / 2, HitLook.MELEE_DAMAGE), new Hit(player, (base.getDamage() / 2) - ((base.getDamage() / 2) / 2), HitLook.MELEE_DAMAGE) };
            else {
                base = getRandomMaxHit(player, target, false, true, 1.0, 1.0);
                if (base.getDamage() > 0)
                    hits = new Hit[] { Hit.miss(player), base, new Hit(player, base.getDamage() / 2, HitLook.MELEE_DAMAGE), new Hit(player, base.getDamage() - (base.getDamage() / 2), HitLook.MELEE_DAMAGE) };
                else {
                    base = getRandomMaxHit(player, target, false, true, 1.0, 1.0);
                    if (base.getDamage() > 0)
                        hits = new Hit[] { Hit.miss(player), Hit.miss(player), new Hit(player, base.getDamage() / 2, HitLook.MELEE_DAMAGE), new Hit(player, (base.getDamage() / 2) + 10, HitLook.MELEE_DAMAGE) };
                    else {
                        base = getRandomMaxHit(player, target, false, true, 1.0, 1.5);
                        if (base.getDamage() > 0)
                            hits = new Hit[] { Hit.miss(player), Hit.miss(player), Hit.miss(player), base };
                        else
                            hits = new Hit[] { Hit.miss(player), Hit.miss(player), Hit.miss(player), new Hit(player, Utils.getRandomInclusive(7), HitLook.MELEE_DAMAGE) };
                    }
                }
            }
            for (int i = 0; i < hits.length; i++)
                if (i > 1)
                    delayHit(target, 1, hits[i]);
                else
                    delayNormalHit(target, hits[i]);
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Dragon claws
        addSpec(new int[] { 14484, 23695 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(10961));
            player.setNextSpotAnim(new SpotAnim(1950));
            Hit[] hits = { new Hit(player, 0, HitLook.MELEE_DAMAGE), new Hit(player, 0, HitLook.MELEE_DAMAGE) };
            int maxHit1 = getMaxHit(player, target, false, 1.0);
            Hit base = getRandomMaxHit(player, target, maxHit1 / 2, maxHit1, false, true, 1.0);
            if (base.getDamage() > 0)
                hits = new Hit[] { base, new Hit(player, base.getDamage() / 2, HitLook.MELEE_DAMAGE), new Hit(player, (base.getDamage() / 2) / 2, HitLook.MELEE_DAMAGE), new Hit(player, (base.getDamage() / 2) - ((base.getDamage() / 2) / 2), HitLook.MELEE_DAMAGE) };
            else {
                base = getRandomMaxHit(player, target, false, true, 1.0, 1.0);
                if (base.getDamage() > 0)
                    hits = new Hit[] { Hit.miss(player), base, new Hit(player, base.getDamage() / 2, HitLook.MELEE_DAMAGE), new Hit(player, base.getDamage() - (base.getDamage() / 2), HitLook.MELEE_DAMAGE) };
                else {
                    base = getRandomMaxHit(player, target, false, true, 1.0, 1.0);
                    if (base.getDamage() > 0)
                        hits = new Hit[] { Hit.miss(player), Hit.miss(player), new Hit(player, base.getDamage() / 2, HitLook.MELEE_DAMAGE), new Hit(player, (base.getDamage() / 2) + 10, HitLook.MELEE_DAMAGE) };
                    else {
                        base = getRandomMaxHit(player, target, false, true, 1.0, 1.5);
                        if (base.getDamage() > 0)
                            hits = new Hit[] { Hit.miss(player), Hit.miss(player), Hit.miss(player), base };
                        else
                            hits = new Hit[] { Hit.miss(player), Hit.miss(player), Hit.miss(player), new Hit(player, Utils.getRandomInclusive(7), HitLook.MELEE_DAMAGE) };
                    }
                }
            }
            for (int i = 0; i < hits.length; i++)
                if (i > 1)
                    delayHit(target, 1, hits[i]);
                else
                    delayNormalHit(target, hits[i]);
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Barrelchest anchor
        addSpec(new int[] { 10887 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(5870));
            player.setNextSpotAnim(new SpotAnim(1027));
            Hit hitt = getRandomMaxHit(player, target, false, true, 2.0, 1.1);
            delayNormalHit(target, hitt);
            if (target instanceof Player other)
                other.getSkills().drainLevel(Constants.DEFENSE, hitt.getDamage() / 10);
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Dragon longsword
        addSpec(new int[] { 1305 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(12033));
            player.setNextSpotAnim(new SpotAnim(2117));
            delayNormalHit(target, getRandomMaxHit(player, target, false, true, 1.0, 1.25));
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Dragon halberd
        addSpec(new int[] { 3204 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(1665));
            player.setNextSpotAnim(new SpotAnim(282));
            if (target.getSize() > 3) {
                target.setNextSpotAnim(new SpotAnim(254, 0, 100));
                target.setNextSpotAnim(new SpotAnim(80));
            }
            delayNormalHit(target, getRandomMaxHit(player, target, false, true, 1.0, 1.1));
            if (target.getSize() > 1)
                delayHit(target, 1, getRandomMaxHit(player, target, false, true, 1.0, 1.1));
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Dragon scimitar
        addSpec(new int[] { 4587 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(12031));
            player.setNextSpotAnim(new SpotAnim(2118));
            Hit hit5 = getRandomMaxHit(player, target, false, true, 1.25, 1.0);
            if (target instanceof Player p2)
                if (hit5.getDamage() > 0)
                    p2.setProtectionPrayBlock(10);
            delayNormalHit(target, hit5);
            player.soundEffect(2540);
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Dragon daggers
        addSpec(new int[] { 1215, 1231, 5680, 5698 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(1062));
            player.setNextSpotAnim(new SpotAnim(252, 0, 100));
            delayNormalHit(target, getRandomMaxHit(player, target, false, true, 1.15, 1.15));
            delayNormalHit(target, getRandomMaxHit(player, target, false, true, 1.15, 1.15));
            player.soundEffect(2537);
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));

        //Dragon mace
        addSpec(new int[] { 1434 }, new SpecialAttack(Type.MELEE, 000000000, (player, target) -> {
            player.setNextAnimation(new Animation(1060));
            player.setNextSpotAnim(new SpotAnim(251));
            delayNormalHit(target, getRandomMaxHit(player, target, false, true, 1.25, 1.5));
            player.soundEffect(2541);
            return PlayerCombat.getMeleeCombatDelay(player, player.getEquipment().getWeaponId());
        }));
    }

    public static void addSpec(int[] itemIds, SpecialAttack spec) {
        for (int itemId : itemIds)
            SPECIAL_ATTACKS.put(itemId, spec);
    }

    public static void addSpec(int itemId, SpecialAttack spec) {
        SPECIAL_ATTACKS.put(itemId, spec);
    }

    public static SpecialAttack getSpec(int itemId) {
        return SPECIAL_ATTACKS.get(itemId);
    }

    public static void handleClick(Player player) {
        SpecialAttack spec = getSpec(player.getEquipment().getWeaponId());
        if (spec == null) {
            player.sendMessage("This weapon has no special attack implemented yet.");
            return;
        }
        if (spec.isInstant()) {
            int specAmt = spec.getEnergyCost();
            if (player.getCombatDefinitions().hasRingOfVigour())
                specAmt *= 0.9;
            if (player.getCombatDefinitions().getSpecialAttackPercentage() < specAmt) {
                player.sendMessage("You don't have enough power left.");
                player.getCombatDefinitions().drainSpec(0);
                return;
            }
            spec.getExecute().apply(player, null);
            return;
        }
        player.getCombatDefinitions().switchUsingSpecialAttack();
    }

    public static int execute(Type type, Player player, Entity target) {
        SpecialAttack spec = getSpec(player.getEquipment().getWeaponId());
        int cost = spec.getEnergyCost();
        if (spec == null || spec.getType() != type) {
            player.getCombatDefinitions().drainSpec(0);
            return 3;
        }
        if (player.getCombatDefinitions().hasRingOfVigour())
            cost *= 0.9;
        if (player.getCombatDefinitions().getSpecialAttackPercentage() < cost) {
            player.sendMessage("You don't have enough power left.");
            player.getCombatDefinitions().drainSpec(0);
            return 3;
        }
        player.getCombatDefinitions().drainSpec(cost);
        return spec.getExecute().apply(player, target);
    }
}
