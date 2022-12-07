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
package com.rs.game.content.skills.fishing;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Fishing extends PlayerAction {

    private static Map<Integer, FishingSpot[]> FISHING_SPOTS = new HashMap<>();

    static {
        FISHING_SPOTS.put(312, new FishingSpot[]{FishingSpot.LOBSTER, FishingSpot.TUNA_SWORDFISH});
        FISHING_SPOTS.put(1332, new FishingSpot[]{FishingSpot.LOBSTER, FishingSpot.TUNA_SWORDFISH});
        FISHING_SPOTS.put(5470, new FishingSpot[]{FishingSpot.LOBSTER, FishingSpot.TUNA_SWORDFISH});
        FISHING_SPOTS.put(7046, new FishingSpot[]{FishingSpot.LOBSTER, FishingSpot.TUNA_SWORDFISH});
        FISHING_SPOTS.put(313, new FishingSpot[]{FishingSpot.BIG_NET, FishingSpot.SHARK});
        FISHING_SPOTS.put(1333, new FishingSpot[]{FishingSpot.BIG_NET, FishingSpot.SHARK});
        FISHING_SPOTS.put(5471, new FishingSpot[]{FishingSpot.BIG_NET, FishingSpot.SHARK});
        FISHING_SPOTS.put(3574, new FishingSpot[]{FishingSpot.BIG_NET, FishingSpot.SHARK});
        FISHING_SPOTS.put(3575, new FishingSpot[]{FishingSpot.BIG_NET, FishingSpot.SHARK});
        FISHING_SPOTS.put(7044, new FishingSpot[]{FishingSpot.BIG_NET, FishingSpot.SHARK});
        FISHING_SPOTS.put(317, new FishingSpot[]{FishingSpot.FLY_FISHING, FishingSpot.PIKE});
        FISHING_SPOTS.put(315, new FishingSpot[]{FishingSpot.FLY_FISHING, FishingSpot.PIKE});
        FISHING_SPOTS.put(309, new FishingSpot[]{FishingSpot.FLY_FISHING, FishingSpot.PIKE});
        FISHING_SPOTS.put(326, new FishingSpot[]{FishingSpot.SHRIMP, FishingSpot.SEA_BAIT});
        FISHING_SPOTS.put(323, new FishingSpot[]{FishingSpot.SHRIMP, FishingSpot.SEA_BAIT});
        FISHING_SPOTS.put(7045, new FishingSpot[]{FishingSpot.SHRIMP, FishingSpot.SEA_BAIT});
        FISHING_SPOTS.put(324, new FishingSpot[]{FishingSpot.LOBSTER, FishingSpot.TUNA_SWORDFISH});
        FISHING_SPOTS.put(3804, new FishingSpot[]{FishingSpot.LOBSTER, FishingSpot.TUNA_SWORDFISH});
        FISHING_SPOTS.put(325, new FishingSpot[]{FishingSpot.SHRIMP, FishingSpot.SEA_BAIT});
        FISHING_SPOTS.put(2724, new FishingSpot[]{FishingSpot.SHRIMP, FishingSpot.SEA_BAIT});
        FISHING_SPOTS.put(1331, new FishingSpot[]{FishingSpot.SHRIMP, FishingSpot.SEA_BAIT});
        FISHING_SPOTS.put(327, new FishingSpot[]{FishingSpot.SHRIMP, FishingSpot.SEA_BAIT});
        FISHING_SPOTS.put(328, new FishingSpot[]{FishingSpot.FLY_FISHING, FishingSpot.PIKE});
        FISHING_SPOTS.put(329, new FishingSpot[]{FishingSpot.FLY_FISHING, FishingSpot.PIKE});
        FISHING_SPOTS.put(330, new FishingSpot[]{FishingSpot.SHRIMP, FishingSpot.SEA_BAIT});
        FISHING_SPOTS.put(334, new FishingSpot[]{FishingSpot.MONKFISH, FishingSpot.TUNA_SWORDFISH});
        FISHING_SPOTS.put(952, new FishingSpot[]{FishingSpot.SHRIMP});
        FISHING_SPOTS.put(2068, new FishingSpot[]{FishingSpot.FROGSPAWN});
        FISHING_SPOTS.put(2722, new FishingSpot[]{FishingSpot.BARBARIAN_FLY_FISHING});
        FISHING_SPOTS.put(3848, new FishingSpot[]{FishingSpot.TUNA_SWORDFISH, FishingSpot.MONKFISH});
        FISHING_SPOTS.put(6267, new FishingSpot[]{FishingSpot.CRAYFISH});
        FISHING_SPOTS.put(8841, new FishingSpot[]{FishingSpot.CAVEFISH});
        FISHING_SPOTS.put(8842, new FishingSpot[]{FishingSpot.ROCKTAIL});
        FISHING_SPOTS.put(14907, new FishingSpot[]{FishingSpot.CRAYFISH});
        FISHING_SPOTS.put(7862, new FishingSpot[]{FishingSpot.CRAYFISH});
        FISHING_SPOTS.put(15020, new FishingSpot[]{FishingSpot.LAVA_EEL});
    }

    public static NPCClickHandler handleFishingSpots = new NPCClickHandler(FISHING_SPOTS.keySet().toArray()) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getNPC().resetDirection();
            int op = e.getOpNum() == 1 ? 0 : e.getOpNum() - 2;
            if (op >= 0 && op < FISHING_SPOTS.get(e.getNPC().getId()).length)
                e.getPlayer().getActionManager().setAction(new Fishing(FISHING_SPOTS.get(e.getNPC().getId())[op], e.getNPC()));
        }
    };

    public static ObjectClickHandler handleBarbarianBed = new ObjectClickHandler(new Object[]{25268}) {
        @Override
        public void handle(ObjectClickEvent e) {
            e.getPlayer().getInventory().addItem(11323, 1);
            e.getPlayer().sendMessage("You find a barbarian fishing rod under the bed.");
        }
    };

    public static ItemOnItemHandler handleKnifeOnBarbFish = new ItemOnItemHandler(946, new int[]{11328, 11330, 11332}) {
        @Override
        public void handle(ItemOnItemEvent e) {
            Item fish = e.getUsedWith(946);
            if (fish == null)
                return;
            if (!e.getPlayer().getInventory().containsItem(946, 1)) {
                e.getPlayer().sendMessage("You need a knife to gut fish.");
                return;
            }
            int chance1 = fish.getId() == 11328 ? 2 : 4;
            int chance99 = fish.getId() == 11328 ? 169 : 317;

            e.getPlayer().setNextAnimation(new Animation(6702));
            e.getPlayer().getInventory().deleteItem(fish);
            if (Utils.skillSuccess(e.getPlayer().getSkills().getLevel(Skills.COOKING), chance1, chance99)) {
                double offcutChance = switch(fish.getId()) {
                  default -> 0.5;
                  case 11330 -> 0.75;
                  case 11332 -> 0.83333;
                };
                e.getPlayer().getInventory().addItemDrop(fish.getId() == 11332 ? 11326 : 11324, 1);
                e.getPlayer().getSkills().addXp(Constants.COOKING, fish.getId() == 11332 ? 15 : 10);
                if (Math.random() < offcutChance)
                    e.getPlayer().getInventory().addItemDrop(11334, 1);
            }
        }
    };

    private FishingSpot spot;
    private NPC npc;
    private WorldTile tile;

    public Fishing(FishingSpot spot, NPC npc) {
        this.spot = spot;
        this.npc = npc;
        tile = WorldTile.of(npc.getTile());
    }

    @Override
    public boolean start(Player player) {
        if (!checkAll(player))
            return false;
        player.sendMessage("You attempt to capture a fish...", true);
        setActionDelay(player, 4);
        return true;
    }

    @Override
    public boolean process(Player player) {
        player.setNextAnimation(spot.getAnimation());
        return checkAll(player);
    }

    @Override
    public int processWithDelay(Player player) {
        int level = player.getSkills().getLevel(Constants.FISHING) + player.getInvisibleSkillBoost(Skills.FISHING);
        for (Fish f : spot.getFish())
            if (f.checkRequirements(player) && f.rollSuccess(player, level)) {
                f.giveFish(player, spot);
                return 4;
            }
        return 4;
    }

    private boolean checkAll(Player player) {
        if (spot == null) {
            player.sendMessage("Nothing interesting happens.");
            return false;
        }
        if (player.getSkills().getLevel(Constants.FISHING) < spot.getLevel()) {
            player.simpleDialogue("You need a fishing level of " + spot.getLevel() + " to fish here.");
            return false;
        }
        boolean hasTool = false;
        for (int tool : spot.getTool())
            if (player.getInventory().containsOneItem(tool) || player.getEquipment().getWeaponId() == tool)
                hasTool = true;
        if (!hasTool) {
            player.sendMessage("You need a " + new Item(spot.getTool()[0]).getDefinitions().getName().toLowerCase() + " to fish here.");
            return false;
        }
        if (spot.getBait() != null && !player.getInventory().containsOneItem(spot.getBait())) {
            player.sendMessage("You don't have bait to fish here.");
            return false;
        }
        if (!player.getInventory().hasFreeSlots()) {
            player.setNextAnimation(new Animation(-1));
            player.simpleDialogue("You don't have enough inventory space.");
            return false;
        }
        if (tile.getX() != npc.getX() || tile.getY() != npc.getY())
            return false;
        return true;
    }

    @Override
    public void stop(final Player player) {
        setActionDelay(player, 4);
    }

    public static boolean hasFishingSuit(Player player) {
        if (player.getEquipment().getHatId() == 24427 && player.getEquipment().getChestId() == 24428 && player.getEquipment().getLegsId() == 24429 && player.getEquipment().getBootsId() == 24430)
            return true;
        return false;
    }

    public static String getMessage(FishingSpot spot, Fish fish) {
        if (fish == Fish.ANCHOVIES || fish == Fish.SHRIMP)
            return "You manage to catch some " + ItemDefinitions.getDefs(fish.getId()).getName().toLowerCase() + ".";
        return "You manage to catch a " + ItemDefinitions.getDefs(fish.getId()).getName().toLowerCase() + ".";
    }
}
