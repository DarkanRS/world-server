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
package com.rs.game.content.skills.hunter;

import com.rs.game.content.Effect;
import com.rs.engine.dialogue.Dialogue;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.pathing.DumbRouteFinder;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class FlyingEntityHunter {

	public static final Animation CAPTURE_ANIMATION = new Animation(6606);
	public static final Animation CAUGHT_ANIMATION = new Animation(6615);
	public static final int BUTTERFLY_NET = 10010;
	public static final int MAGIC_BUTTERFLY_NET = 11259;
	public static final int IMPLING_JAR = 11260;
	public static final int BUTTERFLY_JAR = 10012;
	public static final int[] CHARMS = new int[] { 12158, 12159, 12160, 12163 };

	public enum FlyingEntities {
		BABY_IMPLING(1028, 11238, 25, 17, 79, 400),
		YOUNG_IMPLING(1029, 11240, 65, 22, 69, 349),
		GOURMET_IMPLING(1030, 11242, 113, 28, 61, 324),
		EARTH_IMPLING(1031, 11244, 177, 36, 51, 301),
		ESSENCE_IMPLING(1032, 11246, 225, 42, 41, 275),
		ECLECTIC_IMPLING(1033, 11248, 289, 50, 31, 250),
		SPIRIT_IMPLING(7866, 15513, 321, 54, 24, 225) {
			@Override
			public void effect(Player player) {
				if (Utils.random(2) == 0) {
					Item charm = new Item(CHARMS[Utils.random(CHARMS.length)], 1);
					player.startConversation(new Dialogue().addItem(charm.getId(), "The impling was carrying a " + charm.getName().toLowerCase() + "."));
					player.getInventory().addItemDrop(charm);
				}
			}
		},
		NATURE_IMPLING(1034, 11250, 250, 58, 20, 199),
		MAGPIE_IMPLING(1035, 11252, 289, 65, 15, 176),
		NINJA_IMPLING(6053, 11254, 339, 74, 10, 150),
		PIRATE_IMPLING(7845, 13337, 350, 76, 8, 132),
		DRAGON_IMPLING(6054, 11256, 390, 83, 5, 125),
		ZOMBIE_IMPLING(7902, 15515, 412, 87, 8, 140),
		KINGLY_IMPLING(7903, 15517, 434, 91, 3, 99),

		BABY_IMPLING_PP(6055, 11238, 25, 17, 79, 400),
		YOUNG_IMPLING_PP(6056, 11240, 65, 22, 69, 349),
		GOURMET_IMPLING_PP(6057, 11242, 113, 28, 61, 324),
		EARTH_IMPLING_PP(6058, 11244, 177, 36, 51, 301),
		ESSENCE_IMPLING_PP(6059, 11246, 225, 42, 41, 275),
		ECLECTIC_IMPLING_PP(6060, 11248, 289, 50, 31, 250),
		SPIRIT_IMPLING_PP(7904, 15513, 321, 54, 24, 225) {
			@Override
			public void effect(Player player) {
				if (Utils.random(2) == 0) {
					Item charm = new Item(CHARMS[Utils.random(CHARMS.length)], 1);
					player.startConversation(new Dialogue().addItem(charm.getId(), "The impling was carrying a " + charm.getName().toLowerCase() + "."));
					player.getInventory().addItemDrop(charm);
				}
			}
		},
		NATURE_IMPLING_PP(6061, 11250, 353, 58, 20, 199),
		MAGPIE_IMPLING_PP(6062, 11252, 409, 65, 15, 176),
		NINJA_IMPLING_PP(6063, 11254, 481, 74, 10, 150),
		PIRATE_IMPLING_PP(7846, 13337, 497, 76, 8, 132),
		DRAGON_IMPLING_PP(6064, 11256, 553, 83, 5, 125),
		ZOMBIE_IMPLING_PP(7905, 15515, 585, 87, 8, 140),
		KINGLY_IMPLING_PP(7906, 15517, 617, 91, 3, 99);

		public static final Map<Integer, FlyingEntities> flyingEntitiesByNPC = new HashMap<>();
		public static final Map<Integer, FlyingEntities> flyingEntitiesByReward = new HashMap<>();

		static {
			for (FlyingEntities impling : FlyingEntities.values()) {
				flyingEntitiesByNPC.put(impling.npcId, impling);
				flyingEntitiesByReward.put(impling.reward, impling);
			}
		}

		public static FlyingEntities forNPC(int npcId) {
			return flyingEntitiesByNPC.get(npcId);
		}

		public static FlyingEntities forItem(int reward) {
			return flyingEntitiesByReward.get(reward);
		}

		private final int npcId;
		private final int level;
		private final int reward;
		private final double experience;
		private final int rate1;
		private final int rate99;

		FlyingEntities(int npcId, int reward, double experience, int level, int rate1, int rate99) {
			this.npcId = npcId;
			this.reward = reward;
			this.experience = experience;
			this.level = level;
			this.rate1 = rate1;
			this.rate99 = rate99;
		}

		public int getNpcId() {
			return npcId;
		}

		public int getLevel() {
			return level;
		}

		public int getReward() {
			return reward;
		}

		public double getExperience() {
			return experience;
		}

		public int getRate1() {
			return rate1;
		}

		public int getRate99() {
			return rate99;
		}

		public void effect(Player player) {

		}

		public static FlyingEntities forId(int itemId) {
			for (FlyingEntities entity : FlyingEntities.values())
				if (itemId == entity.getReward())
					return entity;
			return null;
		}
	}

	public static boolean hasButterflyNetEquipped(Player p) {
		return switch (p.getEquipment().getWeaponId()) {
			case BUTTERFLY_NET, MAGIC_BUTTERFLY_NET -> true;
			default -> false;
		};
	}

	public static NPCInteractionDistanceHandler flyingEntitiesDistance = new NPCInteractionDistanceHandler(FlyingEntities.flyingEntitiesByNPC.keySet().toArray(), (player, npc) -> {
		return 0;
	});

	public static NPCClickHandler captureFlyingEntity = new NPCClickHandler(true, FlyingEntities.flyingEntitiesByNPC.keySet().toArray(), e -> {
		if (!e.getOption().equals("Catch") || !e.isAtNPC())
			return;

		final FlyingEntities entity = FlyingEntities.forNPC(e.getNPC().getId());

		if (e.getNPC() instanceof Impling && !hasButterflyNetEquipped(e.getPlayer()) && ((Impling) e.getNPC()).getPuroPuro()) {
			e.getPlayer().sendMessage("You need to have a butterfly net equipped in order to capture an impling.");
			return;
		}
		if (hasButterflyNetEquipped(e.getPlayer())) {
			if (e.getPlayer().getSkills().getLevel(Constants.HUNTER) < entity.getLevel()) {
				e.getPlayer().sendMessage("You need a hunter level of " + entity.getLevel() + " to capture a " + e.getNPC().getName().toLowerCase() + ".");
				return;
			}
			if (!e.getPlayer().getInventory().containsItem(e.getNPC() instanceof Impling ? IMPLING_JAR : BUTTERFLY_JAR, 1)) {
				e.getPlayer().sendMessage("You don't have an empty " + (e.getNPC() instanceof Impling ? "impling jar" : "butterfly jar") + " in which to keep " + (e.getNPC() instanceof Impling ? "an impling" : "a butterfly") + ".");
				return;
			}
		} else if (e.getPlayer().getSkills().getLevel(Constants.HUNTER) < entity.getLevel()+10) {
			e.getPlayer().sendMessage("You need a hunter level of " + (entity.getLevel() + 10) + " in order to capture a " + e.getNPC().getName().toLowerCase() + " barehanded.");
			return;
		}
		e.getPlayer().lock(2);
		e.getPlayer().sendMessage("You swing your net...");
		e.getPlayer().setNextAnimation(CAPTURE_ANIMATION);
		if (rollSuccess(e.getPlayer(), entity, e.getPlayer().getEquipment().getWeaponId() == BUTTERFLY_NET)) {
			e.getNPC().setNextAnimation(CAUGHT_ANIMATION);
			e.getPlayer().incrementCount(e.getNPC().getName() + " trapped");
			e.getPlayer().getInventory().deleteItem(e.getNPC() instanceof Impling ? IMPLING_JAR : BUTTERFLY_JAR, 1);
			e.getPlayer().getInventory().addItem(entity.getReward(), 1);
			e.getPlayer().getSkills().addXp(Constants.HUNTER, entity.getExperience());
			e.getPlayer().sendMessage("You manage to catch the " + e.getNPC().getName().toLowerCase() + " and squeeze it into a jar.");
			e.getNPC().sendDeath(e.getPlayer());
			return;
		}
		e.getPlayer().sendMessage("...you stumble and miss the " + e.getNPC().getName().toLowerCase());
		if (!e.getNPC().hasEffect(Effect.FREEZE)) {
			e.getNPC().resetWalkSteps();
			Tile fleeTo = e.getNPC().getRespawnTile();
			fleeTo.transform(Utils.random(-10, 10), Utils.random(-10, 10));
			DumbRouteFinder.addDumbPathfinderSteps(e.getNPC(), fleeTo, 10, e.getNPC().getClipType());
		}
		if (e.getNPC() instanceof Impling && Utils.random(10) == 0)
			e.getNPC().setNextForceTalk(new ForceTalk("Tee hee, you missed me!"));

	});

	public static ItemClickHandler openJar = new ItemClickHandler(FlyingEntities.flyingEntitiesByReward.keySet().toArray(), new String[]{"Open", "Loot", "Empty", "Release"}, e -> {
		FlyingEntities entity = FlyingEntities.forId(e.getItem().getId());

		if (e.getOption().equals("Empty") || e.getOption().equals("Release")) {
			e.getItem().setId(entity.toString().toLowerCase().contains("impling") ? IMPLING_JAR : BUTTERFLY_JAR);
			return;
		}

		if (e.getOption().equals("Open") || e.getOption().equals("Loot")) {
			if (entity.toString().toLowerCase().contains("impling")) {
				Item[] loot = DropTable.calculateDrops(e.getPlayer(), DropSets.getDropSet(entity.getNpcId()));
				e.getItem().setId(entity.toString().toLowerCase().contains("impling") ? IMPLING_JAR : BUTTERFLY_JAR);
				if (loot.length > 0)
					for (Item item : loot)
						if (item != null)
							e.getPlayer().getInventory().addItemDrop(item.getId(), item.getAmount());
			}
			entity.effect(e.getPlayer());
			if (Utils.random(10) == 0) {
				e.getPlayer().getInventory().deleteItem(new Item(entity.toString().toLowerCase().contains("impling") ? IMPLING_JAR : BUTTERFLY_JAR));
				e.getPlayer().sendMessage("You press too hard on the jar and the glass shatters in your hands.");
				e.getPlayer().applyHit(new Hit(e.getPlayer(), 10, HitLook.TRUE_DAMAGE));
			}
			e.getPlayer().getInventory().refresh(e.getSlotId());
		}
	});

	public static boolean rollSuccess(Player player, FlyingEntities entity, boolean standardNet) {
		return Utils.skillSuccess(player.getSkills().getLevel(Skills.HUNTER), ((standardNet ? 0 : 0.1) + player.getAuraManager().getHuntingMul()), entity.getRate1(), entity.getRate99());
	}

//    1118: Teleport to Impetuous Implings Minigame - Wheat Field beam
//    1119: Impling teleporting to a different location GFX - White smoke puff
//    1124: Impling jar breaking w/ impling inside
//    1125: Impling jar breaking w/ impling inside
//    1126: Impling jar breaking w/ impling inside
//    1127: Impling jar breaking w/ impling inside
//    1128: Impling jar breaking w/ impling inside
//    1129: Impling jar breaking w/ impling inside
//    1130: Impling jar breaking w/ impling inside
//    1131: Impling jar breaking w/ impling inside
//    1132: Impling jar breaking w/ impling inside
//    1133: Impling jar breaking w/ impling inside
//    1134: Exchanging an impling jar w/ impling inside
//    1135: Exchanging an impling jar w/ impling inside
//    1136: Exchanging an impling jar w/ impling inside
//    1137: Exchanging an impling jar w/ impling inside
//    1138: Exchanging an impling jar w/ impling inside
//    1139: Exchanging an impling jar w/ impling inside
//    1140: Exchanging an impling jar w/ impling inside
//    1141: Exchanging an impling jar w/ impling inside
//    1142: Exchanging an impling jar w/ impling inside
//    1143: Exchanging an impling jar w/ impling inside
//    1144: Spinning impling jar w/ impling inside
//    1145: Spinning impling jar w/ impling inside
//    1146: Spinning impling jar w/ impling inside
//    1147: Spinning impling jar w/ impling inside
//    1148: Spinning impling jar w/ impling inside
//    1149: Spinning impling jar w/ impling inside
//    1150: Spinning impling jar w/ impling inside
//    1151: Spinning impling jar w/ impling inside
//    1152: Spinning impling jar w/ impling inside
//    1153: Spinning impling jar w/ impling inside
//    2327-2330: imp jar breaks
//    2331: imp jar flys away
//    2332: imp jar breaks
//    2333-2335: impling jar flys away
//    2336: impling jar projectile
//    2337: imp in jar flys away?
//    2338: imp in jar flys away?
//    1605: Imp relocation

}