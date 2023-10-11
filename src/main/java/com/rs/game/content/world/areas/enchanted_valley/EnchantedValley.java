package com.rs.game.content.world.areas.enchanted_valley;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.fishing.Fishing;
import com.rs.game.content.skills.fishing.FishingSpot;
import com.rs.game.content.skills.mining.Mining;
import com.rs.game.content.skills.mining.RockType;
import com.rs.game.content.skills.woodcutting.TreeType;
import com.rs.game.content.skills.woodcutting.Woodcutting;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class EnchantedValley {
    public static NPCClickHandler centaurs = new NPCClickHandler(new Object[] { 4438, 4439 }, e -> {
        e.getPlayer().startConversation(new Dialogue().addNPC(e.getNPCId(), e.getNPCId() == 4438 ? HeadE.CALM_TALK : HeadE.CHUCKLE, e.getNPCId() == 4438 ? "Hello, human, welcome to our valley." : "What a funny creature you are! Why, you only have 2 legs!"));
    });

    public static NPCClickHandler woodDryad = new NPCClickHandler(new Object[] { 4441 }, e -> {
        e.getPlayer().startConversation(new Dialogue()
                .addPlayer(HeadE.CONFUSED, "Hi, why do you have twigs growing out of you?")
                .addNPC(e.getNPCId(), HeadE.CHUCKLE, "Heehee, what a strange question; that's because I'm a dryad."));
    });

    public static ObjectClickHandler treeCut = new ObjectClickHandler(new Object[] { 16265 }, e -> {
        if (e.getPlayer().inCombat()) {
            e.getPlayer().sendMessage("You can't chop down a tree while you are under attack.");
            return;
        }
        e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.NORMAL) {
            @Override
            public void giveLog(Entity entity) { }

            @Override
            public void fellTree() {
                NPC treeSpirit = new OwnedNPC(e.getPlayer(), 4470, e.getPlayer().getNearestTeleTile(1), false);
                treeSpirit.spotAnim(179, 0, 96);
                treeSpirit.forceTalk("Leave these woods and never return!");
                treeSpirit.setTarget(e.getPlayer());
            }
        });
    });

    public static ObjectClickHandler rockMine = new ObjectClickHandler(new Object[] { 42366 }, e -> {
        if (e.getPlayer().inCombat()) {
            e.getPlayer().sendMessage("You can't mine a rock while you are under attack.");
            return;
        }
        e.getPlayer().getActionManager().setAction(new Mining(RockType.CLAY, e.getObject()) {
            @Override
            public boolean depleteOre() {
                NPC rockGolem = new OwnedNPC(e.getPlayer(), 8648, e.getPlayer().getNearestTeleTile(1), false);
                rockGolem.forceTalk("Raarrrgghh! Flee human!");
                rockGolem.setTarget(e.getPlayer());
                return true;
            }
        });
    });

    @ServerStartupEvent
    public static void addFishingSpotLOSOverride() {
        Entity.addLOSOverride(8647);
    }

    public static NPCClickHandler fishFish = new NPCClickHandler(new Object[] { 8647 }, e -> {
        if (e.getPlayer().inCombat()) {
            e.getPlayer().sendMessage("You can't fish while you are under attack.");
            return;
        }
        e.getPlayer().repeatAction(4, num -> {
            if (Utils.skillSuccess(e.getPlayer().getSkills().getLevel(Skills.FISHING), 32, 192)) {
                e.getPlayer().anim(-1);
                NPC rockGolem = new OwnedNPC(e.getPlayer(), 8646, e.getPlayer().getNearestTeleTile(1), false);
                rockGolem.forceTalk("Fishies be mine, leave dem fishies!");
                rockGolem.setTarget(e.getPlayer());
                return false;
            }
            e.getPlayer().anim(622);
            return true;
        });
    });
}
