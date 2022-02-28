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
package com.rs.game.player.content.skills.hunter;

import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.hunter.puropuro.PuroPuroImpling;
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
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

<<<<<<< HEAD
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class FlyingEntityHunter {

    public static final Animation CAPTURE_ANIMATION = new Animation(6606);
    public static final Item[] CHARMS = {new Item(12158, 1), new Item(12159, 1), new Item(12160, 1), new Item(12163, 1)};

    public enum FlyingEntities {

        BABY_IMPLING(1028, 11238, 25, 17),
        YOUNG_IMPLING(1029, 11240, 65, 22),
        GOURMET_IMPLING(1030, 11242, 113, 28),
        EARTH_IMPLING(1031, 11244, 177, 36),
        ESSENCE_IMPLING(1032, 11246, 225, 42),
        ECLECTIC_IMPLING(1033, 11248, 289, 50),
        SPIRIT_IMPLING(7866, 15513, 321, 54) {
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
        NATURE_IMPLING(1034, 11250, 250, 58),
        MAGPIE_IMPLING(1035, 11252, 289, 65),
        NINJA_IMPLING(6053, 11254, 339, 74),
        PIRATE_IMPLING(7845, 13337, 350, 76),
        DRAGON_IMPLING(6054, 11256, 390, 83),
        ZOMBIE_IMPLING(7902, 15515, 412, 87),
        KINGLY_IMPLING(7903, 15517, 434, 91),

        BABY_IMPLING_PP(6055, 11238, 25, 17),
        YOUNG_IMPLING_PP(6056, 11240, 65, 22),
        GOURMET_IMPLING_PP(6057, 11242, 113, 28),
        EARTH_IMPLING_PP(6058, 11244, 177, 36),
        ESSENCE_IMPLING_PP(6059, 11246, 225, 42),
        ECLECTIC_IMPLING_PP(6060, 11248, 289, 50),
        SPIRIT_IMPLING_PP(7904, 15513, 321, 54) {
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
        NATURE_IMPLING_PP(6061, 11250, 353, 58),
        MAGPIE_IMPLING_PP(6062, 11252, 409, 65),
        NINJA_IMPLING_PP(6063, 11254, 481, 74),
        PIRATE_IMPLING_PP(7846, 13337, 497, 76),
        DRAGON_IMPLING_PP(6064, 11256, 553, 83),
        ZOMBIE_IMPLING_PP(7905, 15515, 585, 87),
        KINGLY_IMPLING_PP(7906, 15517, 617, 91),

        //		BUTTERFLYTEST(1, 1, 434, 617, 91, null, null, null, null) {
        //
        //			@Override
        //			public void effect(Player player) {
        //				// stat boost
        //			}
        //		}
        ;

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
        private SpotAnim graphics;

        FlyingEntities(int npcId, int reward, double experience, int level, SpotAnim graphics) {
            this.npcId = npcId;
            this.reward = reward;
            this.experience = experience;
            this.level = level;
            this.graphics = graphics;
        }

        FlyingEntities(int npcId, int reward, double experience, int level) {
            this.npcId = npcId;
            this.reward = reward;
            this.experience = experience;
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

        public double getExperience() {
            return experience;
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
    }

    public static NPCInteractionDistanceHandler flyingEntitiesDistance = new NPCInteractionDistanceHandler(FlyingEntities.flyingEntitiesByNPC.keySet().toArray()) {
        @Override
        public int getDistance(Player player, NPC npc) {
            return 1;
        }
    };

    public static NPCClickHandler captureFlyingEntity = new NPCClickHandler(FlyingEntities.flyingEntitiesByNPC.keySet().toArray()) {

        @Override
        public void handle(NPCClickEvent e) {
            if (!e.getOption().equals("Catch"))
                return;

            final boolean isPuroPuro = e.getNPC() instanceof PuroPuroImpling;
            final boolean hasButterflyNet = (e.getPlayer().getEquipment().getWeaponId() == 11259 && e.getPlayer().getEquipment().getWeaponId() != 10010);
            final boolean isImpling = e.getNPC().getName().toLowerCase().contains("impling");
            final FlyingEntities entity = FlyingEntities.forNPC(e.getNPC().getId());

            if (isPuroPuro && !hasButterflyNet) {
                e.getPlayer().sendMessage("You need to have a butterfly net equipped in order to capture an impling.");
                return;
            }

            if (e.getPlayer().getSkills().getLevel(Constants.HUNTER) < entity.getLevel() && hasButterflyNet) {
                e.getPlayer().sendMessage("You need a hunter level of " + entity.getLevel() + " to capture a " + e.getNPC().getName().toLowerCase() + ".");
                return;
            }

            if (e.getPlayer().getSkills().getLevel(Constants.HUNTER) < entity.getLevel() + 10 && !hasButterflyNet) {
                e.getPlayer().sendMessage("You need a hunter level of " + entity.getLevel() + 10 + " in order to capture a " + e.getNPC().getName().toLowerCase() + " barehanded.");
                return;
            }

            if (hasButterflyNet && !e.getPlayer().getInventory().containsItem(isImpling ? 11260 : 10012, 1)) {
                e.getPlayer().sendMessage("You don't have an empty " + (isImpling ? "impling jar" : "butterfly jar") + " in which to keep " + (isImpling ? "an impling" : "a butterfly") + ".");
                return;
            }

            e.getPlayer().lock(2);
            e.getPlayer().sendMessage("You swing your net...");
            e.getPlayer().setNextAnimation(CAPTURE_ANIMATION);
            WorldTasks.schedule(new WorldTask() {
                @Override
                public void run() {
                    if (isSuccessful(e.getPlayer(), entity.getLevel())) {
                        e.getNPC().setNextAnimation(new Animation(6615));
                        e.getPlayer().incrementCount(e.getNPC().getName() + " trapped");
                        e.getPlayer().getInventory().deleteItem(11260, 1);
                        e.getPlayer().getInventory().addItem(entity.getReward(), 1);
                        e.getPlayer().getSkills().addXp(Constants.HUNTER, entity.getExperience());
                        e.getPlayer().sendMessage("You manage to catch the " + e.getNPC().getName().toLowerCase() + " and squeeze it into a jar.");
                        if (isPuroPuro) {
                            if (PuroPuroController.isRareImpling(e.getNPCId())) {
                                e.getNPC().transformIntoNPC(PuroPuroImpling.getRandomRareImplingId());
                                e.getNPC().setRespawnTask(200); //Rare imps respawn every 2 minutes
                            } else {
                                e.getNPC().setRespawnTask(50); //Common imps respawn every 30 seconds
                            }
                        } else
                            e.getNPC().setRespawnTask(); //Not puro puro? Worry about spawn mechanics later.
                        return;
                    }
                    if (isImpling) {
                        e.getNPC().setNextForceTalk(new ForceTalk("Tehee, you missed me!"));
                        WorldTasks.schedule(new WorldTask() {
                            @Override
                            public void run() {
                                WorldTile teleTile = e.getNPC();
                                for (int trycount = 0; trycount < 10; trycount++) {
                                    teleTile = new WorldTile(e.getNPC(), 3);
                                    if (World.floorAndWallsFree(teleTile, e.getPlayer().getSize()))
                                        break;
                                    teleTile = e.getNPC();
                                }
                                e.getNPC().setNextWorldTile(teleTile);
                            }
                        }, 2);
                    }
                    e.getPlayer().sendMessage("...you stumble and miss the " + e.getNPC().getName().toLowerCase());
                }
            });
        }
    };

    public static ItemClickHandler openJar = new ItemClickHandler(Arrays.toString(FlyingEntities.flyingEntitiesByReward.keySet().toArray()), new String[]{"Open", "Empty", "Release"}) {
        @Override
        public void handle(ItemClickEvent e) {
            FlyingEntities entity = FlyingEntities.forId(e.getItem().getId());
            boolean isImpling = entity.toString().toLowerCase().contains("impling");

            if (e.getOption().equals("Empty") || e.getOption().equals("Release")) {
                e.getItem().setId(isImpling ? 11260 : 10012);
                return;
            }

            if (e.getOption() == "Open") {
                if (isImpling) {
                    Item[] loot = DropTable.calculateDrops(e.getPlayer(), DropSets.getDropSet(entity.getNpcId()));
                    e.getItem().setId(isImpling ? 11260 : 10012);
                    if (loot.length > 0)
                        for (Item item : loot)
                            if (item != null)
                                e.getPlayer().getInventory().addItemDrop(item.getId(), item.getAmount());
                }
                entity.effect(e.getPlayer());
                if (Utils.random(10) == 0) {
                    e.getPlayer().getInventory().deleteItem(new Item(isImpling ? 11260 : 10012));
                    e.getPlayer().sendMessage("You press too hard on the jar and the glass shatters in your hands.");
                    e.getPlayer().applyHit(new Hit(e.getPlayer(), 10, HitLook.TRUE_DAMAGE));
                }
            }
        }
    };

    public static boolean isSuccessful(Player player, int dataLevel) {

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

        if (player.getEquipment().getGlovesId() == 10075) // Falconry glove
            return Utils.random(3) != 0;
        if (player.getEquipment().getItem(3).getId() == 11259) // magic net
            return Utils.random(3) != 0;
        if (player.getEquipment().getItem(3).getId() == 10010) // regular net
            return Utils.random(4) != 0;
        return Utils.random(5) != 0; //barehanded
    }
=======
import java.util.HashMap;
import java.util.Map;

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
					if (player1.getEquipment().getItem(3).getId() == 10010)
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
							WorldTile teleTile = npc.getTile();
							for (int trycount = 0; trycount < 10; trycount++) {
								teleTile = new WorldTile(npc.getTile(), 3);
								if (World.floorAndWallsFree(teleTile, player.getSize()))
									break;
								teleTile = npc.getTile();
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
>>>>>>> f7115a811aff9687b03181b10ba18a3c95dfa904
}