package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Zaff extends Conversation {
    private static final int ID = 546;
    public static NPCClickHandler handleOps = new NPCClickHandler(new Object[] { ID }, e -> {
        switch(e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Zaff(e.getPlayer()));
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "zaffs_superior_staves");
        }
    });
    public Zaff(Player player) {
        super(player);
        addNPC(HeadE.CHEERFUL, "Would you like to buy or sell some staves or is there something else you need?");
        addOptions(ops -> {
            ops.add("Yes, please.", () -> ShopsHandler.openShop(player, "zaffs_superior_staves"));
            ops.add("No, thank you.");

            ops.add("Do you have any battlestaves?")
                    .addPlayer(HeadE.CONFUSED, "Do you have any battlestaves?")
                    .addNPC(ID, HeadE.CALM_TALK, "I'm not sure. My assistant Naff is in charge of those now.");

            if (player.getQuestStage(Quest.WHAT_LIES_BELOW) == 6)
                ops.add("Rat Burgiss sent me.")
                        .addPlayer(HeadE.CHEERFUL, "Rat Burgiss sent me!")
                        .addNPC(ID, HeadE.CHEERFUL, "Ah, yes; You must be " + player.getDisplayName() + "! Rat sent word that you would be coming. Everything is prepared. I have created a spell that will remove the mind control from the king.")
                        .addPlayer(HeadE.CONFUSED, "Okay, so what's the plan?")
                        .addNPC(ID, HeadE.CHEERFUL, "Listen carefully. For the spell to succeed, the king must be made very weak. If his mind controlled, you will need to fight him until he is all but dead.")
                        .addNPC(ID, HeadE.CHEERFUL, "Then and ONLY then, use your ring to summon me. I will teleport to you and cast the spell that will cure the king.")
                        .addPlayer(HeadE.CONFUSED, "Why must I summon you? Can't you come with me?")
                        .addNPC(ID, HeadE.SAD_MILD, "I cannot. I must look after my shop here and I have lots to do. Rest assured, I will come when you summon me.")
                        .addPlayer(HeadE.CHEERFUL, "Okay, so what do I do now?")
                        .addNPC(ID, HeadE.CHEERFUL, "Take this beacon ring and some instructions.")
                        .addNPC(ID, HeadE.CHEERFUL, "Once you have read the instructions, it will be time for you to arrest Surok.")
                        .addPlayer(HeadE.AMAZED, "Won't he be disinclined to acquiesce to that request?")
                        .addNPC(ID, HeadE.CONFUSED, "Won't he what?")
                        .addPlayer(HeadE.CONFUSED, "Won't he refuse?")
                        .addNPC(ID, HeadE.CALM_TALK, "I very much expect so. It may turn nasty, so be on your guard. I hope we can stop him before he can cast his spell! Make sure you have that ring I gave you.")
                        .addPlayer(HeadE.CHEERFUL, "Okay, thanks, Zaff!")
                        .addNPC(ID, HeadE.CHEERFUL, "Rat has told me that you are to be made an honorary member of the VPSG so that you can arrest Surok. If you have any questions about this, ask Rat.")
                        .addNPC(ID, HeadE.CHEERFUL, "One last thing: you must remember that as a part of the VPSG, we must remain secretive at all times. For this reason, I cannot discuss matters such as this unless absolutely necessary.")
                        .addPlayer(HeadE.CHEERFUL, "Of course! Thanks again!", () -> {
                            player.setQuestStage(Quest.WHAT_LIES_BELOW, 7);
                            player.getInventory().addItemDrop(11014, 1);
                            player.getInventory().addItemDrop(11011, 1);
                        });

            if (player.getQuestStage(Quest.WHAT_LIES_BELOW) == 7) {
                ops.add("I need to ask you something else.")
                        .addPlayer(HeadE.CALM_TALK, "I need to ask you something else.")
                        .addNPC(ID, HeadE.CONFUSED, "Go ahead!")
                        .addOptions(questions -> {
                           questions.add("What am I doing again?")
                           ;

                           questions.add("Can I have another ring?")
                           ;

                           questions.add("Can I have the instructions again?")
                           ;
                        });
            }
        });
        create();
    }
}
