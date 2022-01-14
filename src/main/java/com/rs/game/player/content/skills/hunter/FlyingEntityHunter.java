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
package com.rs.game.player.content.skills.hunter;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.PuroPuroController;
import com.rs.game.player.dialogues.ItemMessage;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

public class FlyingEntityHunter {

	public static final Animation CAPTURE_ANIMATION = new Animation(6606);
	public static final Item[] CHARMS = { new Item(12158, 1), new Item(12159, 1), new Item(12160, 1), new Item(12163, 1) };

	public enum FlyingEntities {

		BABY_IMPLING(1028, 11238, 20, 25, 17),
		YOUNG_IMPLING(1029, 11240, 48, 65, 22),
		GOURMET_IMPLING(1030, 11242, 82, 113, 28),
		EARTH_IMPLING(1031, 11244, 126, 177, 36),
		ESSENCE_IMPLING(1032, 11246, 160, 225, 42),
		ECLECTIC_IMPLING(1033, 11248, 205, 289, 50),
		SPIRIT_IMPLING(7866, 15513, 227, 321, 54) {
			@Override
			public void effect(Player player) {
				if (Utils.random(2) == 0) {
					Item charm = CHARMS[Utils.random(CHARMS.length)];
					int charmAmount = Utils.random(charm.getAmount());
					player.getDialogueManager().execute(new ItemMessage(), "The impling was carrying a" + charm.getName().toLowerCase() + ".", charm.getId());
					player.getInventory().addItem(charm.getId(), charmAmount, true);
				}
			}
		},
		NATURE_IMPLING(1034, 11250, 250, 353, 58),
		MAGPIE_IMPLING(1035, 11252, 289, 409, 65),
		NINJA_IMPLING(6053, 11254, 339, 481, 74),
		PIRATE_IMPLING(7845, 13337, 350, 497, 76),
		DRAGON_IMPLING(6054, 11256, 390, 553, 83),
		ZOMBIE_IMPLING(7902, 15515, 412, 585, 87),
		KINGLY_IMPLING(7903, 15517, 434, 617, 91),

		//		BUTTERFLYTEST(1, 1, 434, 617, 91, null, null, null, null) {
		//
		//			@Override
		//			public void effect(Player player) {
		//				// stat boost
		//			}
		//		}

		;

		static final Map<Short, FlyingEntities> flyingEntities = new HashMap<>();

		static {
			for (FlyingEntities impling : FlyingEntities.values())
				flyingEntities.put((short) impling.reward, impling);
		}

		public static FlyingEntities forItem(short reward) {
			return flyingEntities.get(reward);
		}

		private int npcId, level, reward;
		private double puroExperience, rsExperience;
		private SpotAnim graphics;

		private FlyingEntities(int npcId, int reward, double puroExperience, double rsExperience, int level, SpotAnim graphics) {
			this.npcId = npcId;
			this.reward = reward;
			this.puroExperience = puroExperience;
			this.rsExperience = rsExperience;
			this.level = level;
			this.graphics = graphics;
		}

		private FlyingEntities(int npcId, int reward, double puroExperience, double rsExperience, int level) {
			this.npcId = npcId;
			this.reward = reward;
			this.puroExperience = puroExperience;
			this.rsExperience = rsExperience;
			this.level = level;
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

		public double getPuroExperience() {
			return puroExperience;
		}

		public double getRsExperience() {
			return rsExperience;
		}

		public SpotAnim getGraphics() {
			return graphics;
		}

		public void effect(Player player) {

		}

		public static FlyingEntities forId(int itemId) {
			for (FlyingEntities entity : FlyingEntities.values())
				if (itemId == entity.getReward())
					return entity;
			return null;
		}

		public static FlyingEntities forNPCId(int npcId) {
			for (FlyingEntities entity : FlyingEntities.values())
				if (npcId == entity.getNpcId())
					return entity;
			return null;
		}
	}

	public interface DynamicFormula {

		public int getExtraProbablity(Player player);

	}

	public static void captureFlyingEntity(final Player player, final NPC npc) {
		final String name = npc.getDefinitions().getName().toUpperCase();
		final FlyingEntities instance = FlyingEntities.forNPCId(npc.getId());
		if (instance == null)
			return;
		final boolean isImpling = name.toLowerCase().contains("impling");
		if (!player.getInventory().containsItem(isImpling ? 11260 : 10012, 1)) {
			player.sendMessage("You don't have an empty " + (isImpling ? "impling jar" : "butterfly jar") + " in which to keep " + (isImpling ? "an impling" : "a butterfly") + ".");
			return;
		}
		int weaponId = player.getEquipment().getWeaponId();
		if (weaponId != 11259 && weaponId != 10010 && isImpling) {
			player.sendMessage("You need to have a butterfly net equipped in order to capture an impling.");
			return;
		}
		if (player.getSkills().getLevel(Constants.HUNTER) < instance.getLevel()) {
			player.sendMessage("You need a hunter level of " + instance.getLevel() + " to capture a " + name.toLowerCase() + ".");
			return;
		}
		player.lock(2);
		player.sendMessage("You swing your net...");
		player.setNextAnimation(CAPTURE_ANIMATION);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				if (isSuccessful(player, instance.getLevel(), player1 -> {
					if (player1.getEquipment().getItem(3).getId() == 11259)
						return 3;// magic net
					else if (player1.getEquipment().getItem(3).getId() == 10010)
						return 2;// regular net
					return 1;// hands
				})) {
					player.incrementCount(npc.getDefinitions().getName() + " trapped");
					player.getInventory().deleteItem(new Item(11260, 1));
					player.getInventory().addItem(new Item(instance.getReward(), 1));
					player.getSkills().addXp(Constants.HUNTER, instance.getPuroExperience());
					npc.setRespawnTask(); // sets loc and finishes auto
					player.sendMessage("You manage to catch the " + name.toLowerCase() + " and squeeze it into a jar.");
					if (isImpling)
						npc.transformIntoNPC(PuroPuroController.getRandomImplingId());
					return;
				}
				if (isImpling) {
					npc.setNextForceTalk(new ForceTalk("Tehee, you missed me!"));
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							WorldTile teleTile = npc;
							for (int trycount = 0; trycount < 10; trycount++) {
								teleTile = new WorldTile(npc, 3);
								if (World.floorAndWallsFree(teleTile, player.getSize()))
									break;
								teleTile = npc;
							}
							npc.setNextWorldTile(teleTile);
						}
					}, 2);
				}
				player.sendMessage("...you stumble and miss the " + name.toLowerCase());
			}
		});
	}

	public static void openJar(Player player, FlyingEntities instance, int slot) {
		boolean isImpling = instance.toString().toLowerCase().contains("impling");
		if (isImpling) {
			Item[] loot = DropTable.calculateDrops(player, DropSets.getDropSet(instance.getNpcId()));
			player.getInventory().getItem(slot).setId(isImpling ? 11260 : 11);
			player.getInventory().refresh(slot);
			if (loot.length > 0)
				for (Item item : loot)
					if (item != null)
						player.getInventory().addItem(item.getId(), item.getAmount(), true);
		}
		if (instance != null)
			instance.effect(player);
		if (Utils.random(4) == 0) {
			player.getInventory().deleteItem(new Item(isImpling ? 11260 : 11));
			player.sendMessage("You press too hard on the jar and the glass shatters in your hands.");
			player.applyHit(new Hit(player, 10, HitLook.TRUE_DAMAGE));
		}
	}

	public static boolean isSuccessful(Player player, int dataLevel, DynamicFormula formula) {
		/*
		 * int hunterlevel = player.getSkills().getLevel(Constants.HUNTER); int
		 * increasedProbability = formula == null ? 1 :
		 * formula.getExtraProbablity(player); int level =
		 * Utils.random(hunterlevel + increasedProbability) + 1;
		 *
		 * int chance = level * 100 / (dataLevel * 2);
		 *
		 * if (Utils.random(100) > chance) return false;
		 */

		return Utils.random(4) != 0;
	}
}