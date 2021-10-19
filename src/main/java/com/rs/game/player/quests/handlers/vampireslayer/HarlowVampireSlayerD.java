package com.rs.game.player.quests.handlers.vampireslayer;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class HarlowVampireSlayerD extends Conversation {
    Player p;
    final static int HARLOW = 756;
    static final int STAKE_HAMMER = 15417;
    static final int STAKE = 1549;
    static final int BEER = 1917;

    public HarlowVampireSlayerD(Player p) {
        super(p);
        this.p = p;

        switch (p.getQuestManager().getStage(Quest.VAMPYRE_SLAYER)) {
            case VampireSlayer.NOT_STARTED:
            case VampireSlayer.QUEST_COMPLETE:
                addNPC(HARLOW, HeadE.DRUNK, "Buy me a drrink pleassh...");
                addPlayer(HeadE.FRUSTRATED, "I think you've had enough.");
                break;
            case VampireSlayer.STARTED:
                addNPC(HARLOW, HeadE.DRUNK, "Buy me a drrink pleassh...");
                addOptions("Choose an option:", new Options() {
                @Override
                public void create() {
                    option("No, you've had enough.", new Dialogue()
                        .addPlayer(HeadE.FRUSTRATED, "No, you've had enough"));
                    option("Morgan needs your help!", new Dialogue()
                        .addPlayer(HeadE.TALKING_ALOT, "Morgan needs your help!")
                        .addNPC(HARLOW, HeadE.DRUNK, "Morgan you shhay..?")
                        .addPlayer(HeadE.AMAZED_MILD, "His village is being terrorised by a vampyre! He told me to ask you about how I can stop it.")
                        .addNPC(HARLOW, HeadE.DRUNK, "Buy me a beer... then I'll teash you what you need to know...")
                        .addPlayer(HeadE.FRUSTRATED, "But this is your friend Morgan we're talking about!")
                        .addNPC(HARLOW, HeadE.DRUNK, "Buy ush a drink anyway...", () -> {
                            p.getQuestManager().setStage(Quest.VAMPYRE_SLAYER, VampireSlayer.HARLOW_NEED_DRINK);
                        }));
                }
                });
                break;
            case VampireSlayer.HARLOW_NEED_DRINK:
                if(p.getInventory().containsItem(STAKE, 1)) {
                    addNPC(HARLOW, HeadE.CALM_TALK, "Don't forget to take your stake with you, otherwise he'll just regenerate. Yes, you must have a " +
                            "stake to finish it off... I'd give you a stake but you've already got one in your inventory.");
                    addNPC(HARLOW, HeadE.CALM_TALK, " You'll need a hammer as well, to drive it in properly, your everyday general store hammer will do. ");
                    addNPC(HARLOW, HeadE.CALM_TALK, "One last thing... It's wise to carry garlic with you, vampyres are somewhat weakened if they can smell " +
                            "garlic.");
                    addNPC(HARLOW, HeadE.CALM_TALK, "Morgan always liked garlic, you should try his house. But remember, a vampyre is still a dangerous foe!");
                }
                else if(p.getInventory().containsItem(BEER, 1)) {
                    addNPC(HARLOW, HeadE.DRUNK, "Buy me a drrink pleassh...");
                    addPlayer(HeadE.CALM_TALK, "Here you go");
                    addSimple("You give a beer to Dr Harlow.", () -> {p.getInventory().deleteItem(BEER, 1);});
                    addNPC(HARLOW, HeadE.DRUNK, "Cheersh matey...");
                    addPlayer(HeadE.HAPPY_TALKING, "So tell me how to kill vampyres then.");
                    addNPC(HARLOW, HeadE.DRUNK, "Yesh Yesh vampyres, I was very good at killing em once...");
                    addSimple("Dr Harlow appears to sober up slightly.");
                    addNPC(HARLOW, HeadE.CALM_TALK, "Well, you're going to need a stake, otherwise he'll just regenerate. Yes, you must have a stake to " +
                            "finish it off... I just happen to have one with me.");
                    addSimple("Dr Harlow hands you a stake and hammer.", ()->{
                        p.getInventory().addItem(STAKE, 1);
                        p.getInventory().addItem(STAKE_HAMMER, 1);
                        p.getQuestManager().setStage(Quest.VAMPYRE_SLAYER, VampireSlayer.STAKE_RECIEVED);
                    });
                    addNPC(HARLOW, HeadE.CALM_TALK, "You'll need this hammer as well, to drive it in properly. One last thing...");
                    addNPC(HARLOW, HeadE.CALM_TALK, "It's wise to carry garlic with you, vampyres are somewhat weakened if they can smell garlic. Morgan " +
                            "always liked garlic, you should try his house. But remember, a vampyre is still a dangerous foe!");
                    addPlayer(HeadE.HAPPY_TALKING, "Thank you very much!");
                } else {
                    addNPC(HARLOW, HeadE.DRUNK, "Buy me a drrink pleassh...");
                    addPlayer(HeadE.HAPPY_TALKING, "I'll just go and buy one.");
                }
                break;
            case VampireSlayer.STAKE_RECIEVED:
                if(p.getInventory().containsItem(STAKE, 1)) {
                    addNPC(HARLOW, HeadE.CALM_TALK, "Don't forget to take your stake with you, otherwise he'll just regenerate. Yes, you must have a " +
                            "stake to finish it off... I'd give you a stake but you've already got one in your inventory.");
                    addNPC(HARLOW, HeadE.CALM_TALK, " You'll need a hammer as well, to drive it in properly, your everyday general store hammer will do. ");
                    addNPC(HARLOW, HeadE.CALM_TALK, "One last thing... It's wise to carry garlic with you, vampyres are somewhat weakened if they can smell " +
                            "garlic.");
                    addNPC(HARLOW, HeadE.CALM_TALK, "Morgan always liked garlic, you should try his house. But remember, a vampyre is still a dangerous foe!");
                } else if((!p.getInventory().containsItem(STAKE, 1) || !p.getInventory().containsItem(STAKE_HAMMER, 1)) && !p.getInventory().containsItem(BEER, 1)) {
                    addPlayer(HeadE.SAD, "I lost the stake and hammer you gave me.");
                    addNPC(HARLOW, HeadE.DRUNK, "Bring me another beer and i'll give you another stake.");
                    addPlayer(HeadE.FRUSTRATED, "Fine!");
                } else if(p.getInventory().containsItem(BEER, 1)) {
                    addNPC(HARLOW, HeadE.DRUNK, "Buy me a drrink pleassh...");
                    addPlayer(HeadE.CALM_TALK, "Here you go");
                    addSimple("You give a beer to Dr Harlow.", () -> {p.getInventory().deleteItem(BEER, 1);});
                    addNPC(HARLOW, HeadE.DRUNK, "Cheersh matey...");
                    addPlayer(HeadE.HAPPY_TALKING, "So tell me how to kill vampyres then.");
                    addNPC(HARLOW, HeadE.DRUNK, "Yesh Yesh vampyres, I was very good at killing em once...");
                    addSimple("Dr Harlow appears to sober up slightly.");
                    addNPC(HARLOW, HeadE.CALM_TALK, "Well, you're going to need a stake, otherwise he'll just regenerate. Yes, you must have a stake to " +
                            "finish it off... I just happen to have one with me.");
                    addSimple("Dr Harlow hands you a stake and hammer.", ()->{
                        if(!p.getInventory().containsItem(STAKE))
                            p.getInventory().addItem(STAKE, 1);
                        if(!p.getInventory().containsItem(STAKE_HAMMER))
                            p.getInventory().addItem(STAKE_HAMMER, 1);
                    });
                    addNPC(HARLOW, HeadE.CALM_TALK, "You'll need a hammer as well, to drive it in properly, your everyday general store hammer will do." +
                            " One last thing...");
                    addNPC(HARLOW, HeadE.CALM_TALK, "It's wise to carry garlic with you, vampyres are somewhat weakened if they can smell garlic. Morgan " +
                            "always liked garlic, you should try his house. But remember, a vampyre is still a dangerous foe!");
                }
                break;
        }
    }

    public static NPCClickHandler handleHarlow = new NPCClickHandler(HARLOW) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getPlayer().startConversation(new HarlowVampireSlayerD(e.getPlayer()).getStart());
        }
    };
}
