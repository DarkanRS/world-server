package com.rs.game.player.quests.handlers.shieldofarrav;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestHandler;
import com.rs.game.player.quests.QuestOutline;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.net.decoders.handlers.ObjectHandler;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemAddedToInventoryEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemAddedToInventoryHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;


@QuestHandler(Quest.SHIELD_OF_ARRAV)
@PluginEventHandler
public class ShieldOfArrav extends QuestOutline {
    //---Stages---
    final static int NOT_STARTED = 0;

    //Starting quest
    final static int FIND_BOOK = 1;
    final static int BOOK_IS_READ = 2;
    final static int TALK_TO_BARAEK = 3;
    final static int AFTER_BRIBE_BARAEK = 4;

    //Inititiation Phoenix gang
    final static int PROVING_LOYALTY_PHOENIX = 5;

    //Initiation Black arm gang
    final static int AFTER_BRIBE_CHARLIE = 20;
    final static int PROVING_LOYALTY_BLACK_ARM = 21;

    //Gang activity
    final public static int JOINED_PHOENIX = 22;

    //Gang activity
    final public static int JOINED_BLACK_ARM = 23;

    //King Roald
    final public static int HAS_SHIELD = 24;
    final public static int SPOKE_TO_KING = 25;
    final public static int HAS_CERTIFICATE = 26;
    final static int QUEST_COMPLETE = 27;
    //------

    final static String STAGE_MAP_ID = "ShieldOfArravStages";

    //Items
    final static int BOOK = 757;
    final public static int WEAPONS_KEY = 759;
    final static int SHIELD_LEFT_HALF = 763;
    final static int SHIELD_RIGHT_HALF = 765;
    final public static int CERTIFICATE_LEFT = 11173;
    final public static int CERTIFICATE_RIGHT = 11174;
    final static int CERTIFICATE_FULL = 769;
    final static int FULL_SHIELD = 11164;
    final static int PHOENIX_CROSSBOW = 767;

	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<String>();
		switch(stage) {
            case NOT_STARTED:
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
            case FIND_BOOK:
                lines.add("Look for a book in the bookshelves around Reldo");
                lines.add("You also must read the book then talk to Reldo");
                lines.add("Turn to page 2");
                lines.add("");
			    break;
            case BOOK_IS_READ:
                lines.add("Talk to Reldo");
                lines.add("");
                break;
            case TALK_TO_BARAEK:
                lines.add("Reldo said I should talk to Baraek about the");
                lines.add("Pheonix gang at central square in Varrock");
                lines.add("");
                break;
            case AFTER_BRIBE_BARAEK://good here
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
            case AFTER_BRIBE_CHARLIE:
                lines.add("I have bribed Charlie the Tramp and discovered");
                lines.add("the location of the black arm gang. If I wish to");
                lines.add("join them I must speak to Katrine and prove my");
                lines.add("loyalty.");
                lines.add("");
                break;
            case PROVING_LOYALTY_PHOENIX:
                lines.add("I have spoken with Straven, a Phoenix gang");
                lines.add("member. To prove my loyalty I must kill");
                lines.add("a Black Arm gang informant, Johnny the Beard ");
                lines.add("at the blue moon inn and retrieve his intelligence");
                lines.add("report then give it to Straven");
                lines.add("");
                if(isStageInPlayerSave(player, AFTER_BRIBE_CHARLIE)) {
                    lines.add("If I am feeling doubt Charlie says I can speak");
                    lines.add("to Katrine to join the Black Arm Gang");
                } else {
                    lines.add("If I am feeling doubt I can speak to Charlie the");
                    lines.add("Tramp to join the Black Arm Gang.");
                }
                lines.add("");
                break;
            case PROVING_LOYALTY_BLACK_ARM:
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
            case JOINED_PHOENIX:
                if(ShieldOfArrav.isStageInPlayerSave(player, ShieldOfArrav.JOINED_PHOENIX)) {
                    lines.add("You have joined the Phoenix Gang");
                    lines.add("I should find the Shield Of Arrav");
                    lines.add("at their hideout");
                    lines.add("");
                }
                break;
            case JOINED_BLACK_ARM:
                if(ShieldOfArrav.isStageInPlayerSave(player, ShieldOfArrav.JOINED_BLACK_ARM)) {
                    lines.add("You have joined the Black Arm Gang");
                    lines.add("I should find the Shield Of Arrav");
                    lines.add("at their hideout");
                    lines.add("");
                }
                break;
            case HAS_SHIELD:
                lines.add("You should bring the shield half to King Roald");
                lines.add("");
                break;
            case SPOKE_TO_KING:
                lines.add("I should get the shield authenticated by");
                lines.add("the museum curator.");
                lines.add("");
                break;
            case HAS_CERTIFICATE:
                lines.add("Somehow I must get the other certificate half");
                if(!ShieldOfArrav.hasGang(player)) {
                    lines.add("You dont' have a gang but the quest is complete");
                    lines.add("report this or try to join a gang.");
                }
                if(ShieldOfArrav.isPhoenixGang(player)) {
                    lines.add("I am part of the Phoenix Gang. Maybe a Black Arm");
                    lines.add("member can help?");
                    lines.add("");
                    lines.add("I can trade players by using quest items on them.");
                    lines.add("");
                }
                if(ShieldOfArrav.isBlackArmGang(player)) {
                    lines.add("I am part of the Black Arm Gang, Maybe a Phoenix");
                    lines.add("member can help?");
                    lines.add("");
                    lines.add("I can trade players by using quest items on them.");
                    lines.add("");
                }
                lines.add("");
                break;
            case QUEST_COMPLETE:
                lines.add("");
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
        if(p.get(STAGE_MAP_ID) instanceof ArrayList) {
            ArrayList<Integer> questStages = (ArrayList<Integer>) p.get(STAGE_MAP_ID);
            questStages.add(questStage);
            p.save(STAGE_MAP_ID, questStages);
        } else {
            ArrayList<Integer> questStages = new ArrayList<>();
            questStages.add(questStage);
            p.delete(STAGE_MAP_ID);
            p.save(STAGE_MAP_ID, questStages);
        }
    }

    public static boolean isStageInPlayerSave(Player p, int questStage) {
        if(p.get(STAGE_MAP_ID) instanceof ArrayList) {
            return ((ArrayList<Integer>) p.get(STAGE_MAP_ID)).contains((double)questStage) ||
                    ((ArrayList<Integer>) p.get(STAGE_MAP_ID)).contains(questStage);
        } else {
            return false;
        }
    }

    public static void reset(Player p) {
        p.getQuestManager().setStage(Quest.forId(63), 0, true);
        p.delete(STAGE_MAP_ID);
        p.delete("claimedArravLamp");
    }

    public static boolean hasGang(Player p) {
	    if(isStageInPlayerSave(p, JOINED_BLACK_ARM) || isStageInPlayerSave(p, JOINED_PHOENIX))
	        return true;
	    return false;
    }

    public static boolean isPhoenixGang(Player p) {
        return isStageInPlayerSave(p, JOINED_PHOENIX);
    }

    public static boolean isBlackArmGang(Player p) {
        return isStageInPlayerSave(p, JOINED_BLACK_ARM);
    }

    public static void setStage(Player p, int questStage) {
        p.getQuestManager().setStage(Quest.SHIELD_OF_ARRAV, questStage);
        saveStageToPlayerSave(p, questStage);
    }

    public static void setStage(Player p, int questStage, boolean updateJournal) {
        p.getQuestManager().setStage(Quest.SHIELD_OF_ARRAV, questStage, updateJournal);
        saveStageToPlayerSave(p, questStage);
    }
	
	@Override
	public void complete(Player player) {
		player.getInventory().addItem(995, 1200, true);
		getQuest().sendQuestCompleteInterface(player, FULL_SHIELD, "Speak to Historian Minas",  "at the Varrock Museum for a lamp", "1200gp");
	}

    public static ObjectClickHandler handleBookShelfClick = new ObjectClickHandler(new Object[] { 2402, 6916, 15542, 15543, 15544, 23091, 23092, 23102, 24281, 24282, 31207, 35763 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            Player p = e.getPlayer();
            if(e.getObject().getId()==2402)
                if(!p.getInventory().containsItem(757)) {
                    p.getInventory().addItem(757, 1);
                    p.getPackets().sendGameMessage("You found the book, \"Shield Of Arrav\".");
                    if(p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) == ShieldOfArrav.FIND_BOOK)
                        p.getDialogueManager().execute(new Dialogue() {
                        @Override
                        public void start() {
                            sendPlayerDialogue(p, HeadE.HAPPY_TALKING.getEmoteId(), "Aha! 'The Shield Of Arrav'! Exactly what I was looking for.");
                        }

                        @Override
                        public void run(int interfaceId, int componentId) {
                        }

                        @Override
                        public void finish() {

                        }
                    });
                } else
                    p.getPackets().sendGameMessage("You already found the book, \"Shield Of Arrav\".");
            else {
                p.getPackets().sendGameMessage("You search the books...");
                p.getPackets().sendGameMessage("You find nothing of interest to you.");
            }


        }
    };

    public static ItemClickHandler handleClickOnArravBook = new ItemClickHandler(BOOK) {
        @Override
        public void handle(ItemClickEvent e) {
            if(e.getOption().equalsIgnoreCase("read"))
                BookShieldOfArrav.openBook(e.getPlayer());
            if(e.getOption().equalsIgnoreCase("drop")) {
                e.getPlayer().getInventory().deleteItem(e.getSlotId(), e.getItem());
                World.addGroundItem(e.getItem(), new WorldTile(e.getPlayer()), e.getPlayer());
                e.getPlayer().getPackets().sendSound(2739, 0, 1);
            }
        }
    };

    public static ItemClickHandler handleClickOnIntelReport = new ItemClickHandler(761) {
        @Override
        public void handle(ItemClickEvent e) {
            if(e.getOption().equalsIgnoreCase("read"))
                e.getPlayer().sendMessage("It seems to have intel on the Phoenix gang");
        }
    };

    public static ObjectClickHandler handlePhoenixGangDoor = new ObjectClickHandler(new Object[] { 2397 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            if (e.getObject().matches(new WorldTile(3247, 9779, 0))) {
                if (e.getOption().equalsIgnoreCase("open")) {
                    if (!ShieldOfArrav.isStageInPlayerSave(e.getPlayer(), ShieldOfArrav.JOINED_PHOENIX) && e.getPlayer().getY() > e.getObject().getY()) {
                        e.getPlayer().startConversation(new com.rs.game.player.content.dialogue.Dialogue().addNPC(644, HeadE.FRUSTRATED, "Hey! You can't go in there. Only authorised personnel of" +
                                " the VTAM Corporation are allowed beyond this point."));
                        return;
                    }
                    Doors.handleDoor(e.getPlayer(), e.getObject());
                }
            return;
            }
            Doors.handleDoor(e.getPlayer(), e.getObject());
        }
    };

    public static ObjectClickHandler handleBlackArmGangDoor = new ObjectClickHandler(new Object[] { 2399 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            if (e.getObject().matches(new WorldTile(3185, 3388, 0))) {
                if (e.getOption().equalsIgnoreCase("open")) {
                    if (!ShieldOfArrav.isStageInPlayerSave(e.getPlayer(), ShieldOfArrav.JOINED_BLACK_ARM) && e.getPlayer().getY() < e.getObject().getY()) {
                        e.getPlayer().sendMessage("The door seems to be locked from the inside.");
                        return;
                    }
                    Doors.handleDoor(e.getPlayer(), e.getObject());
                }
                return;
            }
            Doors.handleDoor(e.getPlayer(), e.getObject());
        }
    };

    public static ObjectClickHandler handleShieldChest = new ObjectClickHandler(new Object[] { 2403, 2404 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            Player p = e.getPlayer();
            GameObject obj = e.getObject();
            if(!obj.matches(new WorldTile(3235, 9761, 0)))
                return;
            if(e.getOption().equalsIgnoreCase("open")) {
                p.setNextAnimation(new Animation(536));
                p.lock(2);
                GameObject openedChest = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
                p.faceObject(openedChest);
                World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
            }
            if(e.getOption().equalsIgnoreCase("search")) {
                if(p.getInventory().containsItem(SHIELD_RIGHT_HALF))
                    p.sendMessage("The chest is empty");
                else if(p.getBank().containsItem(SHIELD_RIGHT_HALF, 1)) {
                    p.getDialogueManager().execute(new Dialogue() {
                        @Override
                        public void start() {
                            sendPlayerDialogue(p, HeadE.HAPPY_TALKING.getEmoteId(), "Oh that's right, the right shield half is in my bank.");
                        }

                        @Override
                        public void run(int interfaceId, int componentId) {
                        }

                        @Override
                        public void finish() {

                        }
                    });
                    p.sendMessage("The chest is empty");
                } else {
                    p.sendMessage("You get the right half of the shield of Arrav");
                    p.getInventory().addItem(SHIELD_RIGHT_HALF, 1);
                    if(p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) < HAS_SHIELD) {
                        setStage(p, HAS_SHIELD);
                        p.getDialogueManager().execute(new Dialogue() {
                            @Override
                            public void start() {
                                sendPlayerDialogue(p, HeadE.HAPPY_TALKING.getEmoteId(), "I should take this to King Roald");
                            }

                            @Override
                            public void run(int interfaceId, int componentId) {
                            }

                            @Override
                            public void finish() {

                            }
                        });
                    }
                }
            }

        }
    };

    public static ObjectClickHandler handleBlackArmCupboard = new ObjectClickHandler(new Object[] { 2400, 2401 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            Player p = e.getPlayer();
            GameObject obj = e.getObject();
            if(!obj.matches(new WorldTile(3189, 3385, 1)))
                return;
            if(e.getOption().equalsIgnoreCase("open")) {
                p.setNextAnimation(new Animation(536));
                p.lock(2);
                GameObject openedChest = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
                p.faceObject(openedChest);
                World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
            }
            if(e.getOption().equalsIgnoreCase("shut")) {
                p.setNextAnimation(new Animation(536));
                p.lock(2);
                GameObject openedChest = new GameObject(obj.getId() - 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
                p.faceObject(openedChest);
                World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
            }
            if(e.getOption().equalsIgnoreCase("search")) {
                if(p.getInventory().containsItem(SHIELD_LEFT_HALF))
                    p.sendMessage("The cupboard is empty");
                else if(p.getBank().containsItem(SHIELD_LEFT_HALF, 1)) {
                    p.getDialogueManager().execute(new Dialogue() {
                        @Override
                        public void start() {
                            sendPlayerDialogue(p, HeadE.HAPPY_TALKING.getEmoteId(), "Oh that's right, the right shield half is in my bank.");
                        }

                        @Override
                        public void run(int interfaceId, int componentId) {
                        }

                        @Override
                        public void finish() {

                        }
                    });
                    p.sendMessage("The cupboard is empty");
                } else {
                    p.sendMessage("You get the left half of the shield of Arrav");
                    p.getInventory().addItem(SHIELD_LEFT_HALF, 1);
                    if(p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) < HAS_SHIELD) {
                        setStage(p, HAS_SHIELD);
                        p.getDialogueManager().execute(new Dialogue() {
                            @Override
                            public void start() {
                                sendPlayerDialogue(p, HeadE.HAPPY_TALKING.getEmoteId(), "I should take this to King Roald");
                            }

                            @Override
                            public void run(int interfaceId, int componentId) {
                            }

                            @Override
                            public void finish() {

                            }
                        });
                    }
                }
            }

        }
    };

    public static ObjectClickHandler handleWeaponsStoreDoor = new ObjectClickHandler(new Object[] { 2398 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            GameObject obj = e.getObject();
            if(!obj.matches(new WorldTile(3251, 3386, 0)))
                return;
            if(e.getPlayer().getInventory().containsItem(WEAPONS_KEY, 1) || e.getPlayer().getY() < obj.getY())
                Doors.handleDoor(e.getPlayer(), e.getObject());
            else {
                e.getPlayer().sendMessage("The door appears to need a key");
            }
        }
    };

    public static ObjectClickHandler handleBlackArmStaircases = new ObjectClickHandler(new Object[] { 24356 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            Player p = e.getPlayer();
            GameObject obj = e.getObject();
            if(obj.matches(new WorldTile(3188, 3389, 0))) {
                p.useStairs(-1, new WorldTile(p.getX(), obj.getY()+3, p.getPlane() + 1), 0, 1);
                return;
            }

            ObjectHandler.handleStaircases(p, obj, 1);
        }
    };

    public static ItemOnItemHandler handleCertificates = new ItemOnItemHandler(CERTIFICATE_RIGHT, new int[] {CERTIFICATE_LEFT}) {
        @Override
        public void handle(ItemOnItemEvent e) {
            if(e.getPlayer().getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) >= ShieldOfArrav.HAS_CERTIFICATE) {
                e.getPlayer().getInventory().deleteItem(e.getItem1().getId(), 1);
                e.getPlayer().getInventory().deleteItem(e.getItem2().getId(), 1);
                e.getPlayer().getInventory().addItem(CERTIFICATE_FULL, 1);
            }
            else {
                e.getPlayer().sendMessage("You don't know what these papers are for...");
            }
        }
    };

    public static ItemClickHandler handleClickOnCertificate = new ItemClickHandler(11173, 11174, 769) {
        @Override
        public void handle(ItemClickEvent e) {
            if(e.getOption().equalsIgnoreCase("read"))
                e.getPlayer().sendMessage("This authenticates the Shield Of Arrav");
        }
    };

    public static ItemAddedToInventoryHandler handlePhoenixBowsPickup = new ItemAddedToInventoryHandler(PHOENIX_CROSSBOW) {
        @Override
        public void handle(ItemAddedToInventoryEvent e) {
            Player p = e.getPlayer();
            if(!p.matches(new WorldTile(3245, 3385, 1)))
                return;

            List<NPC> npcs = World.getNPCsInRegion(p.getRegionId());
            for(NPC npc : npcs)
                if(npc.getId() == 643) {
                    switch(Utils.random(1, 4)) {
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
                    npc.faceEntity(p);
                    p.getInventory().deleteItem(e.getItem());
                    World.addGroundItem(e.getItem(), new WorldTile(e.getPlayer()), e.getPlayer());
                }
        }
    };

    public static NPCClickHandler handleWeaponsMaster = new NPCClickHandler(643) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
                {
                    addNPC(e.getNPCId(), HeadE.FRUSTRATED, "I would die before I let anyone take a weapon from here...");
                    addPlayer(HeadE.NERVOUS, "Is that so?");
                    addNPC(e.getNPCId(), HeadE.FRUSTRATED, "Yes it is.");
                    addPlayer(HeadE.NERVOUS, "...");
                    addNPC(e.getNPCId(), HeadE.NERVOUS, " . ");
                    addPlayer(HeadE.NERVOUS, " . ");
                    create();
                }
            });
        }
    };

    /**
     * When the player logs in, the Shield Of Arrav display case is updated based on quest completion.
     */
    public static LoginHandler onLogin = new LoginHandler() {
        @Override
        public void handle(LoginEvent e) {
            if(e.getPlayer().getQuestManager().isComplete(Quest.SHIELD_OF_ARRAV))
                e.getPlayer().getVars().setVarBit(5394, 1);
            else
                e.getPlayer().getVars().setVarBit(5394, 0);
        }
    };

}
