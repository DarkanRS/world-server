package com.rs.game.player.quests.handlers.heroesquest;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

import static com.rs.game.player.content.world.doors.Doors.handleDoor;

@PluginEventHandler
public class BrimhavenHeroesQuest {
    public static ObjectClickHandler handleMansionDoor = new ObjectClickHandler(new Object[]{2627}) {
        int NPC = 788;//Garv
        Dialogue garvD = new Dialogue().addNPC(NPC, HeadE.FRUSTRATED, "Oi! Where do you think you're going pal?");
        @Override
        public void handle(ObjectClickEvent e) {
            Player p = e.getPlayer();
            //handleDoor(p, e.getObject());
            if(p.getQuestManager().getStage(Quest.HEROES_QUEST) == HeroesQuest.GET_ITEMS) {
                if (p.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("black_arm_trick"))
                    p.startConversation(garvD);
                else if(p.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("phoenix_trick"))
                    p.startConversation(garvD);
                else
                    p.startConversation(garvD.addNPC(NPC, HeadE.FRUSTRATED, "And no funny business!"));
            } else
                p.startConversation(garvD);
        }
    };

    public static ObjectClickHandler handleBlackArmHideoutDoor = new ObjectClickHandler(new Object[]{2626}) {
        int NPC = 789;//Grubor
        Dialogue wrongOption = new Dialogue().addNPC(NPC, HeadE.CALM_TALK, "No idea what you are talking about, go away!");
        @Override
        public void handle(ObjectClickEvent e) {
            Player p = e.getPlayer();
            if(p.getQuestManager().isComplete(Quest.HEROES_QUEST) || p.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("black_arm_hideout_open")) {
                handleDoor(p, e.getObject());
                return;
            }
            if(p.getQuestManager().getStage(Quest.HEROES_QUEST) == HeroesQuest.GET_ITEMS) {
                p.startConversation(new Dialogue().addNPC(NPC, HeadE.SECRETIVE, "Yes, what do you want?")
                        .addOptions("Choose an option:", new Options() {
                            @Override
                            public void create() {
                                option("Rabbit's Foot.", new Dialogue().addNext(wrongOption));
                                option("Four leaved Clover.", new Dialogue()
                                        .addPlayer(HeadE.CALM_TALK, "Four leaved clover")
                                        .addNPC(NPC, HeadE.HAPPY_TALKING, "Oh, you're one of the gang are you? Ok, hold up a second, I'll just let you in" +
                                                "through here.")
                                        .addSimple("You hear the door being unbarred from inside.", ()->{
                                            p.getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("black_arm_hideout_open", true);
                                        }));
                                option("Lucky horseshoe", new Dialogue().addNext(wrongOption));
                                option("Black cat", new Dialogue().addNext(wrongOption));
                            }
                        }));
            } else
                p.sendMessage("The door won't open");
        }
    };

//    public static ObjectClickHandler handleMansionDoor = new ObjectClickHandler(new Object[]{2627}) {
//        int NPC = 788;//Garv
//        Dialogue garvD = new Dialogue().addNPC(NPC, HeadE.FRUSTRATED, "Oi! Where do you think you're going pal?");
//        @Override
//        public void handle(ObjectClickEvent e) {
//            Player p = e.getPlayer();
//            //handleDoor(p, e.getObject());
//            if(p.getQuestManager().getStage(Quest.HEROES_QUEST) == HeroesQuest.GET_ITEMS) {
//                if (p.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("black_arm_trick"))
//                    p.startConversation(garvD);
//                else if(p.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("phoenix_trick"))
//                    p.startConversation(garvD);
//                else
//                    p.startConversation(garvD.addNPC(NPC, HeadE.FRUSTRATED, "And no funny business!"));
//            } else
//                p.startConversation(garvD);
//        }
//    };
}
