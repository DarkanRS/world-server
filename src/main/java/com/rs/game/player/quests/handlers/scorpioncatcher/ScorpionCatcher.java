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
package com.rs.game.player.quests.handlers.scorpioncatcher;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestHandler;
import com.rs.game.player.quests.QuestOutline;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.GenericAttribMap;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * How this is written:
 * Each seer prophecy/premonition allows you to spawn a scorp in a chunk
 * If you lose the cage you keep the seer prophecy.
 * You can talk to any Seer in Seer's village to get a prophecy.
 */
@QuestHandler(Quest.SCORPION_CATCHER)
@PluginEventHandler
public class ScorpionCatcher extends QuestOutline {
	public final static int NOT_STARTED = 0;
    public final static int LOOK_FOR_SCORPIONS = 1;
    public final static int QUEST_COMPLETE = 2;

    //Attributes
    public final static String HAS_SEER1_PROHPECY_ATTR = "SEER1";
    public final static String SCORP_COUNT_ATTR = "SCORP_COUNT";
    public final static String HAS_SCORP2_ATTR = "SCORP_2";
    public final static String HAS_SCORP3_ATTR = "SCORP_3";
    public final static String HAS_SEER2_PROHPECY_ATTR = "SEER2";

    //items
//    public final static int
    public final static int EMPTY_CAGE = 456;
    public final static int ONE_SCORP_CAGE = 457;
    public final static int TWO_SCORP_CAGE = 458;
    public final static int THREE_SCORP_CAGE = 459;

	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<String>();
		switch(stage) {
            case NOT_STARTED->{
                lines.add("Thormac the Sorcerer has a hobby involving scorpions.");
                lines.add("Unfortunately, three of them have escaped and managed to run");
                lines.add("far away. If you manage to find them all, he may just be able");
                lines.add("to perform an important service for you.");
                lines.add("");
                lines.add("~~~Requirements~~~");
                lines.add("31 prayer");
                lines.add("");
            }
            case LOOK_FOR_SCORPIONS->{
                if(player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB(HAS_SEER1_PROHPECY_ATTR)) {
                    lines.add("The scorpion is in Taverly Dungeon in a room north of");
                    lines.add("the black demons to the west.");
                    lines.add("");
                    if (player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getI(SCORP_COUNT_ATTR) == 1) {
                        lines.add("I have gotten the first scorpion and need ");
                        lines.add("to ask a seer about the 2nd scorption");
                        lines.add("");
                    }
                    if(player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB(HAS_SEER2_PROHPECY_ATTR)) {
                        lines.add("The second scorpion is somewhere at the Barbarian");
                        lines.add("agility course.... And the third scorption is");
                        lines.add("somewhere in the Edgville Monastary");
                        lines.add("");
                        if(player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getI(SCORP_COUNT_ATTR) == 3)
                            lines.add("Now I just need to return to Thormac to finish.");
                    }
                } else {
                    lines.add("I need to ask a seer about the 1st scorption");
                    lines.add("");
                }
            }
            case QUEST_COMPLETE -> {
                lines.add("");
                lines.add("");
                lines.add("QUEST COMPLETE!");
            }
		    default->{
                lines.add("Invalid quest stage. Report this to an administrator.");
            }
		}
		return lines;
	}

    public static EnterChunkHandler handleScorp1 = new EnterChunkHandler() {
        int CHUNK = 5891648;
        int SCORP_ID= 385;
        WorldTile scorpSpawn = new WorldTile(2878, 9796, 0);
        @Override
        public void handle(EnterChunkEvent e) {
            if(e.getChunkId() != CHUNK)
                return;
            Player p = e.getPlayer();
            for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
                if(npc.getId() == SCORP_ID)
                    return;
            GenericAttribMap attr = p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER);
            if(p.getQuestManager().getStage(Quest.SCORPION_CATCHER) == LOOK_FOR_SCORPIONS
                    && attr.getB(HAS_SEER1_PROHPECY_ATTR)) {
                if(attr.getI(SCORP_COUNT_ATTR) == 0) {
                    NPC scorpion = World.spawnNPC(SCORP_ID, scorpSpawn, -1, false, true);
                    scorpion.lingerForPlayer(p);
                }
            }
        }
    };
    public static EnterChunkHandler handleScorp2 = new EnterChunkHandler() {
        int CHUNK = 5230064;
        int SCORP_ID= 386;
        WorldTile scorpSpawn = new WorldTile(2555, 3570, 0);
        @Override
        public void handle(EnterChunkEvent e) {
            if(e.getChunkId() != CHUNK)
                return;
            Player p = e.getPlayer();
            for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
                if(npc.getId() == SCORP_ID)
                    return;
            GenericAttribMap attr = p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER);
            if(p.getQuestManager().getStage(Quest.SCORPION_CATCHER) == LOOK_FOR_SCORPIONS
                    && attr.getB(HAS_SEER2_PROHPECY_ATTR)) {
                if(!attr.getB(HAS_SCORP2_ATTR)) {
                    NPC scorpion = World.spawnNPC(SCORP_ID, scorpSpawn, -1, false, true);
                    scorpion.lingerForPlayer(p);
                }
            }
        }
    };
    public static EnterChunkHandler handleScorp3 = new EnterChunkHandler() {
        Set<Integer> CHUNKS = new HashSet<>(Arrays.asList(6262176, 6262168));
        int SCORP_ID= 387;
        WorldTile scorpSpawn = new WorldTile(3059, 3486, 1);
        @Override
        public void handle(EnterChunkEvent e) {
            if(!CHUNKS.contains(e.getChunkId()))
                return;
            Player p = e.getPlayer();
            for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
                if(npc.getId() == SCORP_ID)
                    return;
            GenericAttribMap attr = p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER);
            if(p.getQuestManager().getStage(Quest.SCORPION_CATCHER) == LOOK_FOR_SCORPIONS
                    && attr.getB(HAS_SEER2_PROHPECY_ATTR)) {
                if(!attr.getB(HAS_SCORP3_ATTR)) {
                    NPC scorpion = World.spawnNPC(SCORP_ID, scorpSpawn, -1, false, true);
                    scorpion.lingerForPlayer(p);
                }
            }
        }
    };

    public static NPCClickHandler handleScorp1Click = new NPCClickHandler(385) {
        @Override
        public void handle(NPCClickEvent e) {
            Player p = e.getPlayer();
            if(hasLostCage(p))
                return;
            if(p.getQuestManager().getStage(Quest.SCORPION_CATCHER) != LOOK_FOR_SCORPIONS)
                return;
            if(p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB(HAS_SEER1_PROHPECY_ATTR))
                if(p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getI(SCORP_COUNT_ATTR) == 0) {
                    for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
                        if(npc.getId() == 385)
                            npc.finish();
                    removeCages(p);
                    p.getInventory().addItem(ONE_SCORP_CAGE, 1);
                    p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).setI(SCORP_COUNT_ATTR, 1);
                    p.sendMessage("You add the scorpion to the cage");
                }
        }
    };

    public static NPCClickHandler handleScorp2Click = new NPCClickHandler(386) {
        @Override
        public void handle(NPCClickEvent e) {
            Player p = e.getPlayer();
            if(hasLostCage(p))
                return;
            if(p.getQuestManager().getStage(Quest.SCORPION_CATCHER) != LOOK_FOR_SCORPIONS)
                return;
            if(p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB(HAS_SEER2_PROHPECY_ATTR))
                if(!p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB(HAS_SCORP2_ATTR)) {
                    for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
                        if(npc.getId() == 386)
                            npc.finish();
                    removeCages(p);
                    p.getInventory().addItem(TWO_SCORP_CAGE, 1);
                    p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).setB(HAS_SCORP2_ATTR, true);
                    p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).setI(SCORP_COUNT_ATTR,
                            p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getI(SCORP_COUNT_ATTR) + 1);
                    p.sendMessage("You add the scorpion to the cage");
                }
        }
    };

    public static NPCClickHandler handleScorp3Click = new NPCClickHandler(387) {
        @Override
        public void handle(NPCClickEvent e) {
            Player p = e.getPlayer();
            if(hasLostCage(p))
                return;
            if(p.getQuestManager().getStage(Quest.SCORPION_CATCHER) != LOOK_FOR_SCORPIONS)
                return;
            if(p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB(HAS_SEER2_PROHPECY_ATTR))
                if(!p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB(HAS_SCORP3_ATTR)) {
                    for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
                        if(npc.getId() == 387)
                            npc.finish();
                    removeCages(p);
                    p.getInventory().addItem(THREE_SCORP_CAGE, 1);
                    p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).setB(HAS_SCORP3_ATTR, true);
                    p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).setI(SCORP_COUNT_ATTR,
                            p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getI(SCORP_COUNT_ATTR) + 1);
                    p.sendMessage("You add the scorpion to the cage");
                }
        }
    };

    public static void removeCages(Player p) {
        Inventory inv = p.getInventory();
        if(inv.containsItem(EMPTY_CAGE, 1))
            inv.removeItems(new Item(EMPTY_CAGE, 1));
        if(inv.containsItem(ONE_SCORP_CAGE, 1))
            inv.removeItems(new Item(ONE_SCORP_CAGE, 1));
        if(inv.containsItem(TWO_SCORP_CAGE, 1))
            inv.removeItems(new Item(TWO_SCORP_CAGE, 1));
        if(inv.containsItem(THREE_SCORP_CAGE, 1))
            inv.removeItems(new Item(THREE_SCORP_CAGE, 1));
    }

    public static boolean hasLostCage(Player p) {
        Inventory inv = p.getInventory();
        if(inv.containsItem(EMPTY_CAGE, 1) || inv.containsItem(ONE_SCORP_CAGE, 1)
                || inv.containsItem(TWO_SCORP_CAGE, 1) || inv.containsItem(THREE_SCORP_CAGE, 1))
            return false;
        return true;
    }
	
	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.STRENGTH, 6625);
		getQuest().sendQuestCompleteInterface(player, 456, "6,625 Strength XP");
	}
}
