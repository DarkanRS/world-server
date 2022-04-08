package com.rs.game.content.skills.summoning;

import java.util.HashSet;
import java.util.Set;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.controllers.StealingCreationController;
import com.rs.game.content.minigames.creations.Score;
import com.rs.game.content.skills.summoning.Summoning.ScrollTarget;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public enum Scroll {
	HOWL(12425, ScrollTarget.COMBAT, "Scares non-player opponents, causing them to retreat. However, this lasts for only a few seconds.", 0.1, 3) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	DREADFOWL_STRIKE(12445, ScrollTarget.COMBAT, "Fires a long-ranged, magic-based attack which can damage for up to 31.", 0.1, 3) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			familiar.setNextAnimation(new Animation(7810));
			familiar.setNextSpotAnim(new SpotAnim(1318));
			CombatScript.delayHit(familiar, World.sendProjectile(familiar, target, 1376, 34, 16, 30, 35, 16, 0).getTaskDelay(), target, CombatScript.getMagicHit(familiar, CombatScript.getMaxHit(familiar, 40, AttackStyle.MAGE, target)));
			return Familiar.DEFAULT_ATTACK_SPEED;
		}
	},
	FETCH_CASKET(19621, ScrollTarget.CLICK, "Digs for a coordinate, compass, or scan clue any bypasses any mages/double agents.", 0.1, 12) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	EGG_SPAWN(12428, ScrollTarget.CLICK, "Creates up to 5 red spider eggs on the ground next to the player.", 0.2, 6) {
		@Override
		public boolean use(Player player, Familiar familiar) {
			familiar.setNextAnimation(new Animation(8267));
			player.setNextAnimation(new Animation(7660));
			player.setNextSpotAnim(new SpotAnim(1316));
			int num = Utils.random(5);
			Set<WorldTile> spawned = new HashSet<>();
			for (int trycount = 0; trycount < Utils.getRandomInclusive(50); trycount++) {
				if (spawned.size() >= num)
					break;
				WorldTile tile = World.findRandomAdjacentTile(familiar.getTile(), 1);
				if (spawned.contains(tile))
					continue;
				spawned.add(tile);
				num++;
				World.sendSpotAnim(player, new SpotAnim(1342), tile);
				World.addGroundItem(new Item(223, 1), tile, player, true, 120);
			}
			//TODO test
			return true;
		}
	},
	SLIME_SPRAY(12459, ScrollTarget.COMBAT, "Fires a ranged-based attack that damages for up to 42.", 0.2, 3) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	STONY_SHELL(12533, ScrollTarget.CLICK, "Temporarily boosts the player's defence level by +4.", 0.2, 12) {
		@Override
		public boolean use(Player player, Familiar familiar) {
			int newLevel = player.getSkills().getLevel(Constants.DEFENSE) + 4;
			if (newLevel > player.getSkills().getLevelForXp(Constants.DEFENSE) + 4)
				newLevel = player.getSkills().getLevelForXp(Constants.DEFENSE) + 4;
			familiar.setNextSpotAnim(new SpotAnim(8108));
			familiar.setNextAnimation(new Animation(1326));
			player.getSkills().set(Constants.DEFENSE, newLevel);
			return true;
		}
	},
	PESTER(12838, ScrollTarget.COMBAT, "Immediately moves the mosquito to melee attack the target dealing up to 90 damage.", 0.5, 3) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	ELECTRIC_LASH(12460, ScrollTarget.COMBAT, "Fires a small, magic lightning bolt at the opponent dealing up to 40 damage and stunning them.", 0.4, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	VENOM_SHOT(12432, ScrollTarget.COMBAT, "Shoots a ranged-based drop of venom at the target, poisoning them for 50 damage.", 0.9, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	}, //anim 8124
	FIREBALL(12839, ScrollTarget.COMBAT, "Hits up to 5 nearby targets with a fiery magic attack.", 1.1, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	CHEESE_FEAST(12430, ScrollTarget.CLICK, "Fills the rat's inventory with 4 pieces of cheese.", 2.3, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	SANDSTORM(12446, ScrollTarget.COMBAT, "Hits up to 5 nearby targets with a magical sandstorm.", 2.5, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	COMPOST_GENERATE(12440, ScrollTarget.OBJECT, "Fills the targeted compost bin with compost. With a 10% chance of yielding super compost.", 0.6, 12) {
		@Override
		public boolean object(Player owner, Familiar familiar, GameObject object) {
			//TODO
			return false;
		}
	},
	EXPLODE(12834, ScrollTarget.COMBAT, "Allahu Akbar's the target with an AOE range of damage.", 2.9, 3) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	VAMPYRE_TOUCH(12477, ScrollTarget.COMBAT, "Attacks the target and heals the player for 50% of the damage dealt.", 1.5, 4) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	INSANE_FEROCITY(12433, ScrollTarget.CLICK, "Boosts the players attack and strength at the cost of magic, ranged, and defense levels.", 1.6, 12) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	MULTICHOP(12429, ScrollTarget.OBJECT, "Chops 0-3 logs from the target tree. ", 0.7, 3) {
		@Override
		public boolean object(Player owner, Familiar familiar, GameObject object) {
			//TODO
			return false;
		}
	},
	CALL_TO_ARMS(12443, ScrollTarget.CLICK, "Teleports the player to the landers at Pest Control.", 0.7, 3) {
		@Override
		public boolean use(Player player, Familiar familiar) {
			player.setNextSpotAnim(new SpotAnim(1316));
			player.setNextAnimation(new Animation(7660));
			//TODO
			return false;
		}
	},
	BRONZE_BULL(12461, ScrollTarget.COMBAT, "Fires a magic based attack at the opponent hitting for up to 80 damage.", 3.6, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	UNBURDEN(12431, ScrollTarget.CLICK, "Restores the player's run energy by half of their agility level.", 0.6, 12) {
		@Override
		public boolean use(Player player, Familiar familiar) {
			if (player.getRunEnergy() == 100) {
				player.sendMessage("This wouldn't affect you at all.");
				return false;
			}
			int agilityLevel = player.getSkills().getLevel(Constants.AGILITY);
			int runEnergy = (int) (player.getRunEnergy() + (Math.round(agilityLevel / 2)));
			player.setRunEnergy(runEnergy > 100 ? 100 : runEnergy);
			//TODO gfx
			return true;
		}
	},
	HERBCALL(12422, ScrollTarget.CLICK, "Sends the macaw to find a random herb to bring back to the player.", 0.8, 12) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	EVIL_FLAMES(12448, ScrollTarget.COMBAT, "Fires a magic fireball at the target and lowering their magic level.", 2.1, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	PETRIFYING_GAZE(12458, ScrollTarget.COMBAT, "Deals up to 10 damage and lowers the target's levels depending on which cockatrice variant is casting.", 0.9, 3) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	IRON_BULL(12462, ScrollTarget.COMBAT, "Fires a magic based attack at the opponent hitting for up to 90 damage.", 4.6, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	IMMENSE_HEAT(12829, ScrollTarget.ITEM, "Allows the player to craft a single peice of jewelry without a furnace.", 2.3, 6) {
		@Override
		public boolean item(Player owner, Familiar familiar, Item item) {
			//TODO
			return false;
		}
	},
	THIEVING_FINGERS(12426, ScrollTarget.CLICK, "Temporarily raises the player's thieving level by 2.", 0.9, 12) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	BLOOD_DRAIN(12444, ScrollTarget.CLICK, "Restores the player's stats by 5% and cures poison. Damages the player for 25 damage, though.", 2.4, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	TIRELESS_RUN(12441, ScrollTarget.CLICK, "Restores the player's run energy by half of their agility level. Boosts agility level by 2.", 0.8, 8) {
		@Override
		public boolean use(Player player, Familiar familiar) {
			if (player.getRunEnergy() == 100) {
				player.sendMessage("This wouldn't effect you at all.");
				return false;
			}
			int newLevel = player.getSkills().getLevel(Constants.AGILITY) + 2;
			int runEnergy = (int) (player.getRunEnergy() + (Math.round(newLevel / 2)));
			if (newLevel > player.getSkills().getLevelForXp(Constants.AGILITY) + 2)
				newLevel = player.getSkills().getLevelForXp(Constants.AGILITY) + 2;
			familiar.setNextAnimation(new Animation(8229));
			player.setNextSpotAnim(new SpotAnim(1300));
			player.setNextAnimation(new Animation(7660));
			player.getSkills().set(Constants.AGILITY, newLevel);
			player.setRunEnergy(runEnergy > 100 ? 100 : runEnergy);
			return true;
		}
	},
	ABYSSAL_DRAIN(12454, ScrollTarget.COMBAT, "Fires a magic based attack that lowers the opponent's magic level and prayer points, restoring them to the player.", 1.1, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	DISSOLVE(12453, ScrollTarget.COMBAT, "Fires a magical attack that dissolves the opponent and lowers their attack stat.", 5.5, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	FISH_RAIN(12424, ScrollTarget.CLICK, "Produces a random, low-level fish next to the player.", 1.1, 12) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	STEEL_BULL(12463, ScrollTarget.COMBAT, "Fires a magic based attack at the opponent hitting for up to 110 damage.", 5.6, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	AMBUSH(12836, ScrollTarget.COMBAT, "Teleports to the target and attacks the target, dealing up to 224 damage.", 5.7, 3) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	RENDING(12840, ScrollTarget.COMBAT, "Performs a magic based attack that lowers the opponent's strength.", 5.7, 3) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	GOAD(12835, ScrollTarget.COMBAT, "Gores the opponent with a powerful melee attack. Hits twice for up to 180 damage.", 5.7, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	DOOMSPHERE(12455, ScrollTarget.COMBAT, "Attacks the opponent with a strong water spell that hits up to 76 damage and drains the target's magic.", 5.8, 3) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	DUST_CLOUD(12468, ScrollTarget.COMBAT, "Hits up to 6 nearby targets for up to 79 damage with a strong magical dust cloud.", 3, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	ABYSSAL_STEALTH(12427, ScrollTarget.CLICK, "Temporarily boosts the player's agility and thieving levels by 4 each.", 1.9, 20) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	OPHIDIAN_INCUBATION(12436, ScrollTarget.ITEM, "Transforms a single egg in the player's inventory into a cockatrice variant.", 3.1, 3) {
		@Override
		public boolean item(Player owner, Familiar familiar, Item item) {
			//TODO
			return false;
		}
	},
	POISONOUS_BLAST(12467, ScrollTarget.COMBAT, "Attacks the target with a magic attack that deals up to 103 damage.", 3.2, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	MITHRIL_BULL(12464, ScrollTarget.COMBAT, "Fires a magic based attack at the opponent hitting for up to 160 damage.", 6.6, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	TOAD_BARK(12452, ScrollTarget.COMBAT, "Performs the same attack as if it were loaded with a cannonball.", 1, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	TESTUDO(12439, ScrollTarget.CLICK, "Temporarily boosts the player's defense level by 8 points.", 0.7, 20) {
		@Override
		public boolean use(Player player, Familiar familiar) {
			int newLevel = player.getSkills().getLevel(Constants.DEFENSE) + 9;
			if (newLevel > player.getSkills().getLevelForXp(Constants.DEFENSE) + 9)
				newLevel = player.getSkills().getLevelForXp(Constants.DEFENSE) + 9;
			player.setNextSpotAnim(new SpotAnim(1300));
			player.setNextAnimation(new Animation(7660));
			player.getSkills().set(Constants.DEFENSE, newLevel);
			return true;
		}
	},
	SWALLOW_WHOLE(12438, ScrollTarget.ITEM, "Allows a player to eat a raw fish (assuming the player has the proper cooking level for it). Also does not trigger the eat timer.", 1.4, 3) {
		@Override
		public boolean item(Player owner, Familiar familiar, Item item) {
			//TODO
			return false;
		}
	},
	FRUITFALL(12423, ScrollTarget.CLICK, "Drops from 0-5 random fruit on the ground around the player.", 1.4, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	FAMINE(12830, ScrollTarget.COMBAT, "Consumes a piece of the target's food.", 1.5, 12) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	ARCTIC_BLAST(12451, ScrollTarget.COMBAT, "Fires a large magic attack at the opponent, hitting up to 130 damage with a chance of stunning them.", 1.1, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	RISE_FROM_THE_ASHES(14622, ScrollTarget.COMBAT, "This special is a pain in the ass to code.", 8, 5) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	VOLCANIC_STRENGTH(12826, ScrollTarget.CLICK, "Boosts the player's strength level by 9 points.", 7.3, 12) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	MANTIS_STRIKE(12450, ScrollTarget.COMBAT, "Fires a magic based attack at the opponent dealing up to 100 damage, binding them for 3 seconds, and draining their prayer.", 3.7, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	CRUSHING_CLAW(12449, ScrollTarget.COMBAT, "Launches a magic based attack at the target dealing up to 96 damage and draining their defense.", 3.7, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	INFERNO(12841, ScrollTarget.COMBAT, "Fires a magic based attack that disarms the opponent and deals up to 85 damage.", 1.5, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	ADAMANT_BULL(12465, ScrollTarget.COMBAT, "Fires a magic based attack at the opponent hitting for up to 200 damage.", 7.6, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	DEADLY_CLAW(12831, ScrollTarget.COMBAT, "Causes the talon beast to attack with magic instead of melee. Dealing up to 300 damage.", 11.4, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	ACORN_MISSILE(12457, ScrollTarget.COMBAT, "Hits anyone around the opponent with a rain of acorns dealing up to 100 damage each.", 1.6, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	TITANS_CONSTITUTION(12824, ScrollTarget.CLICK, "Boosts the player's defense by 12.5% and restores 10% of their max health.", 7.9, 20) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	REGROWTH(12442, ScrollTarget.OBJECT, "Immediately regrows a tree that has been felled by farming.", 1.6, 6) {
		@Override
		public boolean object(Player owner, Familiar familiar, GameObject object) {
			//TODO
			return false;
		}
	},
	SPIKE_SHOT(12456, ScrollTarget.COMBAT, "Fires a magic attack that deals up to 108 damage.", 4.1, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	EBON_THUNDER(12837, ScrollTarget.COMBAT, "Fires a magic attack that lowers the opponent's special attack energy by 10%.", 8.3, 4) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	SWAMP_PLAGUE(12832, ScrollTarget.COMBAT, "Fires a magic attack that hits for up to 110 damage and poisons the target for 78 damage.", 4.1, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	RUNE_BULL(12466, ScrollTarget.COMBAT, "Fires a magic based attack at the opponent hitting for up to 240 damage.", 8.6, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	HEALING_AURA(12434, ScrollTarget.CLICK, "Heals the player for 15% of their maximum lifepoints.", 1.8, 20) {
		@Override
		public boolean use(Player player, Familiar familiar) {
			if (player.getHitpoints() == player.getMaxHitpoints()) {
				player.sendMessage("You're already at full hitpoints!");
				return false;
			}
			player.setNextAnimation(new Animation(7660));
			player.setNextSpotAnim(new SpotAnim(1300));
			int percentHealed = player.getMaxHitpoints() / 15;
			player.heal(percentHealed);
			return true;
		}
	},
	BOIL(12833, ScrollTarget.COMBAT, "Boils the target in their armor causing magic damage based on the opponents defense bonuses.", 8.9, 6) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	MAGIC_FOCUS(12437, ScrollTarget.CLICK, "Boosts the player's attack level by 7.", 4.6, 20) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			int newLevel = owner.getSkills().getLevel(Constants.MAGIC) + 7;
			if (newLevel > owner.getSkills().getLevelForXp(Constants.MAGIC) + 7)
				newLevel = owner.getSkills().getLevelForXp(Constants.MAGIC) + 7;
			owner.setNextSpotAnim(new SpotAnim(1300));
			owner.setNextAnimation(new Animation(7660));
			owner.getSkills().set(Constants.MAGIC, newLevel);
			return true;
		}
	},
	ESSENCE_SHIPMENT(12827, ScrollTarget.CLICK, "Sends all carried essence (both in player inventory and familiar inventory) to the bank.", 1.9, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	IRON_WITHIN(12828, ScrollTarget.COMBAT, "Hits with melee instead of magic for a turn hitting up to 3 times.", 4.7, 12) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	WINTER_STORAGE(12435, ScrollTarget.ITEM, "Banks the targeted item for the player.", 4.8, 12) {
		@Override
		public boolean item(Player owner, Familiar familiar, Item item) {
			if (!owner.getBank().hasBankSpace()) {
				owner.sendMessage("Your bank is full!");
				return false;
			}
			owner.incrementCount("Items banked with yak");
			owner.getBank().depositItem(item.getSlot(), 1, true);
			owner.sendMessage("Your pack yak has sent an item to your bank.", true);
			//TODO gfx and setting item slot before passing
			return true;
		}
	},
	STEEL_OF_LEGENDS(12825, ScrollTarget.COMBAT, "Attacks the target with four ranged or melee attacks depending on distance dealing very high damage.", 4.9, 12) {
		@Override
		public int attack(Player owner, Familiar familiar, Entity target) {
			//TODO
			return Familiar.CANCEL_SPECIAL;
		}
	},
	GHASTLY_ATTACK(21453, ScrollTarget.CLICK, "Restores 100 prayer points to the player.", 0.9, 20) {
		@Override
		public boolean use(Player owner, Familiar familiar) {
			//TODO
			return false;
		}
	},
	
	SUNDERING_STRIKE_1(18027, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that reduces the target's defense by 1 for every 20 points of damage dealt.", 0.0, 6),
	SUNDERING_STRIKE_2(18028, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that reduces the target's defense by 1 for every 20 points of damage dealt.", 0.0, 6),
	SUNDERING_STRIKE_3(18029, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that reduces the target's defense by 1 for every 20 points of damage dealt.", 0.0, 6),
	SUNDERING_STRIKE_4(18030, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that reduces the target's defense by 1 for every 20 points of damage dealt.", 0.0, 6),
	SUNDERING_STRIKE_5(18031, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that reduces the target's defense by 1 for every 20 points of damage dealt.", 0.0, 6),
	SUNDERING_STRIKE_6(18032, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that reduces the target's defense by 1 for every 20 points of damage dealt.", 0.0, 6),
	SUNDERING_STRIKE_7(18033, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that reduces the target's defense by 1 for every 20 points of damage dealt.", 0.0, 6),
	SUNDERING_STRIKE_8(18034, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that reduces the target's defense by 1 for every 20 points of damage dealt.", 0.0, 6),
	SUNDERING_STRIKE_9(18035, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that reduces the target's defense by 1 for every 20 points of damage dealt.", 0.0, 6),
	SUNDERING_STRIKE_10(18036, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that reduces the target's defense by 1 for every 20 points of damage dealt.", 0.0, 6),
	
	POISONOUS_SHOT_1(18037, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that poisons the target for 18 damage.", 0.0, 6),
	POISONOUS_SHOT_2(18038, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that poisons the target for 28 damage.", 0.0, 6),
	POISONOUS_SHOT_3(18039, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that poisons the target for 38 damage.", 0.0, 6),
	POISONOUS_SHOT_4(18040, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that poisons the target for 48 damage.", 0.0, 6),
	POISONOUS_SHOT_5(18041, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that poisons the target for 58 damage.", 0.0, 6),
	POISONOUS_SHOT_6(18042, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that poisons the target for 68 damage.", 0.0, 6),
	POISONOUS_SHOT_7(18043, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that poisons the target for 78 damage.", 0.0, 6),
	POISONOUS_SHOT_8(18044, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that poisons the target for 88 damage.", 0.0, 6),
	POISONOUS_SHOT_9(18045, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that poisons the target for 98 damage.", 0.0, 6),
	POISONOUS_SHOT_10(18046, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that poisons the target for 108 damage.", 0.0, 6),
	
	SNARING_WAVE_1(18047, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that snares the opponent.", 0.0, 6),
	SNARING_WAVE_2(18048, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that snares the opponent.", 0.0, 6),
	SNARING_WAVE_3(18049, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that snares the opponent.", 0.0, 6),
	SNARING_WAVE_4(18050, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that snares the opponent.", 0.0, 6),
	SNARING_WAVE_5(18051, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that snares the opponent.", 0.0, 6),
	SNARING_WAVE_6(18052, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that snares the opponent.", 0.0, 6),
	SNARING_WAVE_7(18053, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that snares the opponent.", 0.0, 6),
	SNARING_WAVE_8(18054, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that snares the opponent.", 0.0, 6),
	SNARING_WAVE_9(18055, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that snares the opponent.", 0.0, 6),
	SNARING_WAVE_10(18056, ScrollTarget.COMBAT, "Fires a 50% more accurate attack that snares the opponent.", 0.0, 6),
	
	APTITUDE_1(18057, ScrollTarget.CLICK, "Invisibly boosts all skills by 1.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return aptitude(owner, familiar, 1); }
	},
	APTITUDE_2(18058, ScrollTarget.CLICK, "Invisibly boosts all skills by 2.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return aptitude(owner, familiar, 2); }
	},
	APTITUDE_3(18059, ScrollTarget.CLICK, "Invisibly boosts all skills by 3.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return aptitude(owner, familiar, 3); }
	},
	APTITUDE_4(18060, ScrollTarget.CLICK, "Invisibly boosts all skills by 4.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return aptitude(owner, familiar, 4); }
	},
	APTITUDE_5(18061, ScrollTarget.CLICK, "Invisibly boosts all skills by 5.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return aptitude(owner, familiar, 5); }
	},
	APTITUDE_6(18062, ScrollTarget.CLICK, "Invisibly boosts all skills by 6.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return aptitude(owner, familiar, 6); }
	},
	APTITUDE_7(18063, ScrollTarget.CLICK, "Invisibly boosts all skills by 7.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return aptitude(owner, familiar, 7); }
	},
	APTITUDE_8(18064, ScrollTarget.CLICK, "Invisibly boosts all skills by 8.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return aptitude(owner, familiar, 8); }
	},
	APTITUDE_9(18065, ScrollTarget.CLICK, "Invisibly boosts all skills by 9.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return aptitude(owner, familiar, 9); }
	},
	APTITUDE_10(18066, ScrollTarget.CLICK, "Invisibly boosts all skills by 10.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return aptitude(owner, familiar, 10); }
	},
	
	SECOND_WIND_1(18067, ScrollTarget.CLICK, "Restores 20% of the player's run energy.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return secondWind(owner, familiar, 20); }
	},
	SECOND_WIND_2(18068, ScrollTarget.CLICK, "Restores 22% of the player's run energy.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return secondWind(owner, familiar, 22); }
	},
	SECOND_WIND_3(18069, ScrollTarget.CLICK, "Restores 24% of the player's run energy.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return secondWind(owner, familiar, 24); }
	},
	SECOND_WIND_4(18070, ScrollTarget.CLICK, "Restores 26% of the player's run energy.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return secondWind(owner, familiar, 26); }
	},
	SECOND_WIND_5(18071, ScrollTarget.CLICK, "Restores 28% of the player's run energy.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return secondWind(owner, familiar, 28); }
	},
	SECOND_WIND_6(18072, ScrollTarget.CLICK, "Restores 30% of the player's run energy.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return secondWind(owner, familiar, 30); }
	},
	SECOND_WIND_7(18073, ScrollTarget.CLICK, "Restores 32% of the player's run energy.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return secondWind(owner, familiar, 32); }
	},
	SECOND_WIND_8(18074, ScrollTarget.CLICK, "Restores 34% of the player's run energy.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return secondWind(owner, familiar, 34); }
	},
	SECOND_WIND_9(18075, ScrollTarget.CLICK, "Restores 36% of the player's run energy.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return secondWind(owner, familiar, 36); }
	},
	SECOND_WIND_10(18076, ScrollTarget.CLICK, "Restores 38% of the player's run energy.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return secondWind(owner, familiar, 38); }
	},
	
	GLIMMER_OF_LIGHT_1(18077, ScrollTarget.CLICK, "Heals the player for 20 hitpoints.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return glimmer(owner, familiar, 20); }
	},
	GLIMMER_OF_LIGHT_2(18078, ScrollTarget.CLICK, "Heals the player for 40 hitpoints.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return glimmer(owner, familiar, 40); }
	},
	GLIMMER_OF_LIGHT_3(18079, ScrollTarget.CLICK, "Heals the player for 60 hitpoints.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return glimmer(owner, familiar, 60); }
	},
	GLIMMER_OF_LIGHT_4(18080, ScrollTarget.CLICK, "Heals the player for 80 hitpoints.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return glimmer(owner, familiar, 80); }
	},
	GLIMMER_OF_LIGHT_5(18081, ScrollTarget.CLICK, "Heals the player for 100 hitpoints.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return glimmer(owner, familiar, 100); }
	},
	GLIMMER_OF_LIGHT_6(18082, ScrollTarget.CLICK, "Heals the player for 120 hitpoints.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return glimmer(owner, familiar, 120); }
	},
	GLIMMER_OF_LIGHT_7(18083, ScrollTarget.CLICK, "Heals the player for 140 hitpoints.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return glimmer(owner, familiar, 140); }
	},
	GLIMMER_OF_LIGHT_8(18084, ScrollTarget.CLICK, "Heals the player for 160 hitpoints.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return glimmer(owner, familiar, 160); }
	},
	GLIMMER_OF_LIGHT_9(18085, ScrollTarget.CLICK, "Heals the player for 180 hitpoints.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return glimmer(owner, familiar, 180); }
	},
	GLIMMER_OF_LIGHT_10(18086, ScrollTarget.CLICK, "Heals the player for 200 hitpoints.", 0.0, 6) {
		@Override
		public boolean use(Player owner, Familiar familiar) { return glimmer(owner, familiar, 200); }
	},
	
	CLAY_DEPOSIT(14421, ScrollTarget.CLICK, "Sends all held clay back to the base.", 0.0, 12) {
		@Override
		public boolean use(Player owner, Familiar familiar) { 
			if (owner.getControllerManager().getController() == null || !(owner.getControllerManager().getController() instanceof StealingCreationController)) {
				familiar.dismiss(false);
				return false;
			}
			StealingCreationController sc = (StealingCreationController) owner.getControllerManager().getController();
			Score score = sc.getGame().getScore(owner);
			if (score == null)
				return false;
			for (Item item : familiar.getInventory().array()) {
				if (item == null)
					continue;
				sc.getGame().sendItemToBase(owner, item, sc.getTeam(), true, false);
			}
			return true;
		}
	};
	
	private ScrollTarget target;
	private String name;
	private String description;
	private int id;
	private double xp;
	private int pointCost;

	private Scroll(int scrollId, ScrollTarget target, String description, double xp, int pointCost) {
		this.name = Utils.formatPlayerNameForDisplay(ItemDefinitions.getDefs(scrollId).name.replace(" scroll", ""));
		this.description = description;
		this.target = target;
		this.id = scrollId;
		this.xp = xp;
		this.pointCost = pointCost;
	}
	
	public static boolean aptitude(Player player, Familiar familiar, int boost) {
		//TODO
		return false;
	}
	
	public static boolean secondWind(Player player, Familiar familiar, int amount) {
		//TODO
		return false;
	}
	
	public static boolean glimmer(Player player, Familiar familiar, int amount) {
		//TODO
		return false;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

	public ScrollTarget getTarget() {
		return target;
	}
	
	public int getScrollId() {
		return getId();
	}

	public double getExperience() {
		return getXp();
	}
	
	public int getPointCost() {
		return pointCost;
	}

	public boolean use(Player owner, Familiar familiar) {
		owner.sendMessage("Scroll is not implemented for click targets.");
		return false;
	}
	
	public int attack(Player owner, Familiar familiar, Entity target) {
		return -1;
	}

	public boolean onCombatActivation(Player owner, Familiar familiar, Entity target) {
		return true;
	}

	public boolean item(Player owner, Familiar familiar, Item item) {
		owner.sendMessage("Scroll is not implemented for item targets.");
		return false;
	}

	public boolean object(Player owner, Familiar familiar, GameObject object) {
		owner.sendMessage("Scroll is not implemented for object targets.");
		return false;
	}

	public int getId() {
		return id;
	}

	public double getXp() {
		return xp;
	}
}
