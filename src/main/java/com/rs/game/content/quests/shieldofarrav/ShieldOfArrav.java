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
package com.rs.game.content.quests.shieldofarrav;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.World;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.GenericAttribMap;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.*;
import com.rs.utils.Ticks;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(
        quest = Quest.SHIELD_OF_ARRAV,
        startText = "Talk to Reldo in the Varrock palace library.",
        itemsText = "20 coins.",
        combatText = "You will need to defeat a level 1 enemy.",
        rewardsText = "1,200 coins",
        completedStage = 27
)
@PluginEventHandler
public class ShieldOfArrav extends QuestOutline {
    //---Stages---
    final static int NOT_STARTED_STAGE = 0;

    //Starting quest
    final static int FIND_BOOK_STAGE = 1;
    final static int BOOK_IS_READ_STAGE = 2;
    final static int TALK_TO_BARAEK_STAGE = 3;
    final static int AFTER_BRIBE_BARAEK_STAGE = 4;

    //Inititiation Phoenix gang
    final static int PROVING_LOYALTY_PHOENIX_STAGE = 5;

    //Initiation Black arm gang
    final static int AFTER_BRIBE_CHARLIE_STAGE = 20;
    final static int PROVING_LOYALTY_BLACK_ARM_STAGE = 21;

    //Gang activity
    final public static int JOINED_PHOENIX_STAGE = 22;

    //Gang activity
    final public static int JOINED_BLACK_ARM_STAGE = 23;

    //King Roald
    final public static int HAS_SHIELD_STAGE = 24;
    final public static int SPOKE_TO_KING_STAGE = 25;
    final public static int HAS_CERTIFICATE_STAGE = 26;
    final static int QUEST_COMPLETE_STAGE = 27;

    @Override
    public List<String> getJournalLines(Player player, int stage) {
        ArrayList<String> lines = new ArrayList<>();
        switch (stage) {
            case NOT_STARTED_STAGE:
                lines.add("Varrockian literature tells of a valuable shield.");
                lines.add("It was stolen long ago from the Museum of Varrock,");
                lines.add("by a gang of professional thieves. See if you can");
                lines.add("track down this shield and return it to the Museum.");
                lines.add("You will need a friend to help you complete this quest.");
                lines.add("");
                lines.add("I can start this quest by speaking to Reldo");
                lines.add("in the Varrock palace library.");
                lines.add("");
                break;
            case FIND_BOOK_STAGE:
                lines.add("Look for a book in the bookshelves around Reldo");
                lines.add("You also must read the book then talk to Reldo");
                lines.add("Turn to page 2");
                lines.add("");
                break;
            case BOOK_IS_READ_STAGE:
                lines.add("Talk to Reldo");
                lines.add("");
                break;
            case TALK_TO_BARAEK_STAGE:
                lines.add("Reldo said I should talk to Baraek about the");
                lines.add("Pheonix gang at central square in Varrock");
                lines.add("");
                break;
            case AFTER_BRIBE_BARAEK_STAGE://good here
                lines.add("I have bribed Baraek.");
                lines.add("");
                lines.add("If I wish to join the Phoenix Gang I must");
                lines.add("find their hideout south of the rune shop");
                lines.add("and prove my loyalty to Straven");
                lines.add("");
                lines.add("If I wish to join the Black Arm Gang I can");
                lines.add("speak to Charlie The Tramp to get directions.");
                lines.add("");
                break;
            case AFTER_BRIBE_CHARLIE_STAGE:
                lines.add("I have bribed Charlie the Tramp and discovered");
                lines.add("the location of the black arm gang. If I wish to");
                lines.add("join them I must speak to Katrine and prove my");
                lines.add("loyalty.");
                lines.add("");
                break;
            case PROVING_LOYALTY_PHOENIX_STAGE:
                lines.add("I have spoken with Straven, a Phoenix gang");
                lines.add("member. To prove my loyalty I must kill");
                lines.add("a Black Arm gang informant, Johnny the Beard ");
                lines.add("at the blue moon inn and retrieve his intelligence");
                lines.add("report then give it to Straven");
                lines.add("");
                if (isStageInPlayerSave(player, AFTER_BRIBE_CHARLIE_STAGE)) {
                    lines.add("If I am feeling doubt Charlie says I can speak");
                    lines.add("to Katrine to join the Black Arm Gang");
                } else {
                    lines.add("If I am feeling doubt I can speak to Charlie the");
                    lines.add("Tramp to join the Black Arm Gang.");
                }
                lines.add("");
                break;
            case PROVING_LOYALTY_BLACK_ARM_STAGE:
                lines.add("I have spoken with Katrine, a Black Arm Gang");
                lines.add("member. To join the Black Arm Gang I must find");
                lines.add("two Phoenix Gang crossbows and bring them to her.");
                lines.add("");
                lines.add("I need to get into the weapons store room south");
                lines.add("of the rune store. I will need to get a weapon key");
                lines.add("from a player who is a Phoenix gang member.");
                lines.add("");
                lines.add("I can trade players by using quest items on them.");
                lines.add("");
                lines.add("If I am feeling doubt about this gang I can speak");
                lines.add("to Straven to join the Phoenix Gang.");
                lines.add("");
                break;
            case JOINED_PHOENIX_STAGE:
                if (ShieldOfArrav.isStageInPlayerSave(player, ShieldOfArrav.JOINED_PHOENIX_STAGE)) {
                    lines.add("You have joined the Phoenix Gang");
                    lines.add("I should find the Shield Of Arrav");
                    lines.add("at their hideout");
                    lines.add("");
                }
                break;
            case JOINED_BLACK_ARM_STAGE:
                if (ShieldOfArrav.isStageInPlayerSave(player, ShieldOfArrav.JOINED_BLACK_ARM_STAGE)) {
                    lines.add("You have joined the Black Arm Gang");
                    lines.add("I should find the Shield Of Arrav");
                    lines.add("at their hideout");
                    lines.add("");
                }
                break;
            case HAS_SHIELD_STAGE:
                lines.add("You should bring the shield half to King Roald");
                lines.add("");
                break;
            case SPOKE_TO_KING_STAGE:
                lines.add("I should get the shield authenticated by");
                lines.add("the museum curator.");
                lines.add("");
                break;
            case HAS_CERTIFICATE_STAGE:
                lines.add("Somehow I must get the other certificate half");
                if (!ShieldOfArrav.hasGang(player)) {
                    lines.add("You dont' have a gang but the quest is complete");
                    lines.add("report this or try to join a gang.");
                }
                if (ShieldOfArrav.isPhoenixGang(player)) {
                    lines.add("I am part of the Phoenix Gang. Maybe a Black Arm");
                    lines.add("member can help?");
                    lines.add("");
                    lines.add("I can trade players by using quest items on them.");
                    lines.add("");
                }
                if (ShieldOfArrav.isBlackArmGang(player)) {
                    lines.add("I am part of the Black Arm Gang, Maybe a Phoenix");
                    lines.add("member can help?");
                    lines.add("");
                    lines.add("I can trade players by using quest items on them.");
                    lines.add("");
                }
                lines.add("");
                break;
            case QUEST_COMPLETE_STAGE:
                if (!hasGang(player))
                    lines.add("Contact an admin. You appear to not have a gang...");
                if (isPhoenixGang(player))
                    lines.add("You have joined the Phoenix gang...");
                if (isBlackArmGang(player))
                    lines.add("You have joined the Black Arm gang...");
                lines.add("");
                lines.add("QUEST COMPLETE!");
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
                break;
            default:
                lines.add("Invalid quest stage. Report this to an administrator.");
                break;
        }
        return lines;
    }

    public static void saveStageToPlayerSave(Player p, int questStage) {
        GenericAttribMap questAttr = p.getQuestManager().getAttribs(Quest.SHIELD_OF_ARRAV);
        switch (questStage) {
            case NOT_STARTED_STAGE -> questAttr.setB("NOT_STARTED", true);
            case FIND_BOOK_STAGE -> {
                p.delete("claimedArravLamp");
                questAttr.setB("FIND_BOOK", true);
            }
            case BOOK_IS_READ_STAGE -> questAttr.setB("BOOK_IS_READ", true);
            case TALK_TO_BARAEK_STAGE -> questAttr.setB("TALK_TO_BARAEK", true);
            case AFTER_BRIBE_BARAEK_STAGE -> questAttr.setB("AFTER_BRIBE_BARAEK", true);
            case PROVING_LOYALTY_PHOENIX_STAGE -> questAttr.setB("PROVING_LOYALTY_PHOENIX", true);
            case AFTER_BRIBE_CHARLIE_STAGE -> questAttr.setB("AFTER_BRIBE_CHARLIE", true);
            case PROVING_LOYALTY_BLACK_ARM_STAGE -> questAttr.setB("PROVING_LOYALTY_BLACK_ARM", true);
            case JOINED_PHOENIX_STAGE -> questAttr.setB("JOINED_PHOENIX", true);
            case JOINED_BLACK_ARM_STAGE -> questAttr.setB("JOINED_BLACK_ARM", true);
            case HAS_SHIELD_STAGE -> questAttr.setB("HAS_SHIELD", true);
            case SPOKE_TO_KING_STAGE -> questAttr.setB("SPOKE_TO_KING", true);
            case HAS_CERTIFICATE_STAGE -> questAttr.setB("HAS_CERTIFICATE", true);
        }
    }

    public static boolean isStageInPlayerSave(Player p, int questStage) {
        GenericAttribMap questAttr = p.getQuestManager().getAttribs(Quest.SHIELD_OF_ARRAV);
        switch (questStage) {
            case NOT_STARTED_STAGE -> {
                return questAttr.getB("NOT_STARTED");
            }
            case FIND_BOOK_STAGE -> {
                return questAttr.getB("FIND_BOOK");
            }
            case BOOK_IS_READ_STAGE -> {
                return questAttr.getB("BOOK_IS_READ");
            }
            case TALK_TO_BARAEK_STAGE -> {
                return questAttr.getB("TALK_TO_BARAEK");
            }
            case AFTER_BRIBE_BARAEK_STAGE -> {
                return questAttr.getB("AFTER_BRIBE_BARAEK");
            }
            case PROVING_LOYALTY_PHOENIX_STAGE -> {
                return questAttr.getB("PROVING_LOYALTY_PHOENIX");
            }
            case AFTER_BRIBE_CHARLIE_STAGE -> {
                return questAttr.getB("AFTER_BRIBE_CHARLIE");
            }
            case PROVING_LOYALTY_BLACK_ARM_STAGE -> {
                return questAttr.getB("PROVING_LOYALTY_BLACK_ARM");
            }
            case JOINED_PHOENIX_STAGE -> {
                return questAttr.getB("JOINED_PHOENIX");
            }
            case JOINED_BLACK_ARM_STAGE -> {
                return questAttr.getB("JOINED_BLACK_ARM");
            }
            case HAS_SHIELD_STAGE -> {
                return questAttr.getB("HAS_SHIELD");
            }
            case SPOKE_TO_KING_STAGE -> {
                return questAttr.getB("SPOKE_TO_KING");
            }
            case HAS_CERTIFICATE_STAGE -> {
                return questAttr.getB("HAS_CERTIFICATE");
            }
            default -> {
                return false;
            }
        }
    }

    public static boolean hasGang(Player p) {
		if(isPhoenixGang(p))
        	return true;
		if(isBlackArmGang(p))
			return true;
		return false;
    }

    public static boolean isPhoenixGang(Player p) {
        return p.getO("ThievingGang") != null && ((String)p.getO("ThievingGang")).equalsIgnoreCase("Phoenix");
    }

    public static boolean isBlackArmGang(Player p) {
        return p.getO("ThievingGang") != null && ((String)p.getO("ThievingGang")).equalsIgnoreCase("Black");
    }

    public static void setStage(Player p, int questStage) {
        p.getQuestManager().setStage(Quest.SHIELD_OF_ARRAV, questStage);
        saveStageToPlayerSave(p, questStage);
    }

    public static void setGang(Player p, String gang) {//"Phoenix", "Black", "None"
		p.save("ThievingGang", gang);
    }

    @Override
    public void complete(Player player) {
        player.getInventory().addCoins(1200);
        sendQuestCompleteInterface(player, 11164);
    }

    public static ObjectClickHandler handleBookShelfClick = new ObjectClickHandler(new Object[]{2402, 6916, 15542, 15543, 15544, 23091, 23092, 23102, 24281, 24282, 31207, 35763}, e -> {
    	 Player p = e.getPlayer();
         if (e.getObject().getId() == 2402)
             if (!p.getInventory().containsItem(757)) {
                 p.getInventory().addItem(757, 1);
                 p.getPackets().sendGameMessage("You found the book, \"Shield Of Arrav\".");
                 if (p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) == ShieldOfArrav.FIND_BOOK_STAGE)
                     p.startConversation(new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "Aha, 'The Shield Of Arrav'! Exactly what I was looking for."));
             } else
                 p.getPackets().sendGameMessage("You already found the book, \"Shield Of Arrav\".");
         else {
             p.getPackets().sendGameMessage("You search the books...");
             p.getPackets().sendGameMessage("You find nothing of interest to you.");
         }
    });

	public static ItemClickHandler handleClickOnArravBook = new ItemClickHandler(new Object[] { 757 }, new String[] { "Read" }, e -> BookShieldOfArrav.openBook(e.getPlayer()));

	public static ItemClickHandler handleClickOnIntelReport = new ItemClickHandler(new Object[] { 761 }, new String[] { "Read" }, e -> e.getPlayer().sendMessage("It seems to have intel on the Phoenix gang"));

    public static ObjectClickHandler handlePhoenixGangDoor = new ObjectClickHandler(new Object[]{2397}, e -> {
    	if (e.getObject().getTile().matches(Tile.of(3247, 9779, 0))) {
            if (e.getOption().equalsIgnoreCase("open")) {
                if (!ShieldOfArrav.isStageInPlayerSave(e.getPlayer(), ShieldOfArrav.JOINED_PHOENIX_STAGE) && e.getPlayer().getY() > e.getObject().getY()) {
                    e.getPlayer().startConversation(new Dialogue().addNPC(644, HeadE.FRUSTRATED, "Hey! You can't go in there. Only authorised personnel of" +
                            " the VTAM Corporation are allowed beyond this point."));
                    return;
                }
                Doors.handleDoor(e.getPlayer(), e.getObject());
            }
            return;
        }
        Doors.handleDoor(e.getPlayer(), e.getObject());
    });

    public static ObjectClickHandler handleBlackArmGangDoor = new ObjectClickHandler(new Object[]{2399}, e -> {
    	 if (e.getObject().getTile().matches(Tile.of(3185, 3388, 0))) {
             if (e.getOption().equalsIgnoreCase("open")) {
                 if (!ShieldOfArrav.isStageInPlayerSave(e.getPlayer(), ShieldOfArrav.JOINED_BLACK_ARM_STAGE) && e.getPlayer().getY() < e.getObject().getY()) {
                     e.getPlayer().sendMessage("The door seems to be locked from the inside.");
                     return;
                 }
                 Doors.handleDoor(e.getPlayer(), e.getObject());
             }
             return;
         }
         Doors.handleDoor(e.getPlayer(), e.getObject());
    });

    public static ObjectClickHandler handleShieldChest = new ObjectClickHandler(new Object[]{2403, 2404}, e -> {
    	Player p = e.getPlayer();
        GameObject obj = e.getObject();
        if (!obj.getTile().matches(Tile.of(3235, 9761, 0)))
            return;
        if (e.getOption().equalsIgnoreCase("open")) {
            p.setNextAnimation(new Animation(536));
            p.lock(2);
            GameObject openedChest = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
            p.faceObject(openedChest);
            World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
        }
        if (e.getOption().equalsIgnoreCase("search"))
            if (p.getInventory().containsItem(765))
                p.sendMessage("The chest is empty");
            else if (p.getBank().containsItem(765, 1)) {
                p.startConversation(new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "Oh that's right, the right shield half is in my bank."));
                p.sendMessage("The chest is empty");
            } else {
                p.sendMessage("You get the right half of the shield of Arrav");
                p.getInventory().addItem(765, 1);
                if (p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) < HAS_SHIELD_STAGE) {
                    setStage(p, HAS_SHIELD_STAGE);
                    p.startConversation(new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "I should take this to King Roald"));
                }
            }
    });

    public static ObjectClickHandler handleBlackArmCupboard = new ObjectClickHandler(new Object[]{2400, 2401}, e -> {
    	 Player p = e.getPlayer();
         GameObject obj = e.getObject();
         if (!obj.getTile().matches(Tile.of(3189, 3385, 1)))
             return;
         if (e.getOption().equalsIgnoreCase("open")) {
             p.setNextAnimation(new Animation(536));
             p.lock(2);
             GameObject openedChest = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
             p.faceObject(openedChest);
             World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
         }
         if (e.getOption().equalsIgnoreCase("shut")) {
             p.setNextAnimation(new Animation(536));
             p.lock(2);
             GameObject openedChest = new GameObject(obj.getId() - 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
             p.faceObject(openedChest);
             World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
         }
         if (e.getOption().equalsIgnoreCase("search"))
             if (p.getInventory().containsItem(763))
                 p.sendMessage("The cupboard is empty");
             else if (p.getBank().containsItem(763, 1)) {
                 p.startConversation(new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "Oh that's right, the right shield half is in my bank."));
                 p.sendMessage("The cupboard is empty");
             } else {
                 p.sendMessage("You get the left half of the shield of Arrav");
                 p.getInventory().addItem(763, 1);
                 if (p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) < HAS_SHIELD_STAGE) {
                     setStage(p, HAS_SHIELD_STAGE);
                     p.startConversation(new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "I should take this to King Roald"));
                 }
             }
    });

    public static ObjectClickHandler handleWeaponsStoreDoor = new ObjectClickHandler(new Object[]{2398}, e -> {
    	GameObject obj = e.getObject();
        if (!obj.getTile().matches(Tile.of(3251, 3386, 0)))
            return;
        if (e.getPlayer().getInventory().containsItem(759, 1) || e.getPlayer().getY() < obj.getY())
            Doors.handleDoor(e.getPlayer(), e.getObject());
        else
            e.getPlayer().sendMessage("The door appears to need a key");
    });


    public static ItemOnItemHandler handleCertificates = new ItemOnItemHandler(11174, new int[]{11173}, e -> {
    	if (e.getPlayer().getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) >= ShieldOfArrav.HAS_CERTIFICATE_STAGE) {
            e.getPlayer().getInventory().deleteItem(e.getItem1().getId(), 1);
            e.getPlayer().getInventory().deleteItem(e.getItem2().getId(), 1);
            e.getPlayer().getInventory().addItem(769, 1);
        } else
            e.getPlayer().sendMessage("You don't know what these papers are for...");
    });

    public static ItemClickHandler handleClickOnCertificate = new ItemClickHandler(new Object[] { 11173, 11174, 769 }, e -> {
    	 if (e.getOption().equalsIgnoreCase("read"))
             e.getPlayer().sendMessage("This authenticates the Shield Of Arrav");
    });

	public static PickupItemHandler handlePhoenixBowsPickup = new PickupItemHandler(new Object[] { 767 }, Tile.of(3245, 3385, 1), e -> {
		List<NPC> npcs = World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 1);
		for (NPC npc : npcs)
			if (npc.getId() == 643) {
				switch (Utils.random(1, 4)) {
				case 1:
					npc.forceTalk("Get your hands off!");
					break;
				case 2:
					npc.forceTalk("Don't touch that!");
					break;
				case 3:
					npc.forceTalk("Hey, that's Phoenix Gang property!");
					break;
				}
				npc.faceEntityTile(e.getPlayer());
				e.cancelPickup();
			}
	});

    public static NPCClickHandler handleWeaponsMaster = new NPCClickHandler(new Object[] { 643 }, new String[] { "Talk-to" }, e -> e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
{
addNPC(e.getNPCId(), HeadE.FRUSTRATED, "I would die before I let anyone take a weapon from here...");
addPlayer(HeadE.NERVOUS, "Is that so?");
addNPC(e.getNPCId(), HeadE.FRUSTRATED, "Yes it is.");
addPlayer(HeadE.NERVOUS, "...");
addNPC(e.getNPCId(), HeadE.NERVOUS, " . ");
addPlayer(HeadE.NERVOUS, " . ");
create();
}
}));
    
    public static ItemOnPlayerHandler giveCerts = new ItemOnPlayerHandler(new Object[] { 759, 11173, 11174 }, e -> {
		if (e.getItem().getAmount() >= 1) {
			if (e.getTarget().getInventory().getFreeSlots() >= 1) {
				e.getPlayer().lock();
				e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1);
				WorldTasks.delay(0, () -> {
					e.getPlayer().setNextAnimation(new Animation(881));
					e.getTarget().getInventory().addItem(e.getItem().getId(), 1);
					if (e.getTarget().isIronMan())
						e.getPlayer().sendMessage("They stand alone, but not this once!");
					e.getPlayer().unlock();
				});
			} else {
				e.getTarget().sendMessage("You need to make space in your inventory");
				e.getPlayer().sendMessage(e.getTarget().getUsername() + " does not have enough space.");
			}
		} else
			e.getPlayer().sendMessage("You need at least 1 of this item to give!");
    });

    /**
     * When the player logs in, the Shield Of Arrav display case is updated based on quest completion.
	 * Also if the player does not have a gang upon completion they must be assigned one
	 * --Do this with Minas--
     */
//    public static LoginHandler onLogin = new LoginHandler() {
//        @Override
//        public void handle(LoginEvent e) {
//            if (e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
//				e.getPlayer().getVars().setVarBit(5394, 1);
//            else
//                e.getPlayer().getVars().setVarBit(5394, 0);
//        }
//    };

}
