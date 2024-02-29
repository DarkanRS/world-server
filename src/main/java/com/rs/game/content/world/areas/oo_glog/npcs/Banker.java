package com.rs.game.content.world.areas.oo_glog.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.ge.GE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Banker {

    private static final int Balnea = 7047;
    private static final int Banker = 7050;

    public static NPCInteractionDistanceHandler bankerDistance = new NPCInteractionDistanceHandler(new Object[] { 7050, 7049 }, (p, n) -> 1);


    public static NPCClickHandler OgressBanker = new NPCClickHandler(new Object[]{ 7050, 7049 }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> {
                if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
                    afterAsAFirstResort(e.getPlayer());
                else
                    beforeAsAFirstResort(e.getPlayer());
            }
            case "Collect" -> {
                if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
                    GE.openCollection(e.getPlayer());
                else
                    beforeAsAFirstResort(e.getPlayer());
            }
            case "Bank" -> {
                if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
                    e.getPlayer().getBank().open();
                else
                    beforeAsAFirstResort(e.getPlayer());
            }
        }
    });

    public static ObjectClickHandler booth = new ObjectClickHandler(new Object[] { 29085 }, e -> {
        switch (e.getOption()) {
            case "Collect" -> {
                if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
                    GE.openCollection(e.getPlayer());
                else
                    beforeAsAFirstResort(e.getPlayer());
            }
            case "Bank" -> {
                if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
                    e.getPlayer().getBank().open();
                else
                    beforeAsAFirstResort(e.getPlayer());
            }
        }
    });

    private static void beforeAsAFirstResort(Player player) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.CALM_TALK, "Excuse me, can I get some service here, please?")
                .addNPC(Banker, HeadE.CHILD_ANGRY_HEADSHAKE, "GRAAAAAH! You go away, human! Me too busy with training to talk to puny thing like you.")
                .addNPC(Balnea, HeadE.SHAKING_HEAD, "I do apologise, sir. We're temporarily unable to meet your banking needs.")
                .addNPC(Balnea, HeadE.CALM_TALK, "We'll be open just as soon as we realise our customer experience goals and can guarantee the high standards of service that you expect from all branches of the Bank of Gielinor.")
                .addNPC(Banker, HeadE.CHILD_ANGRY_HEADSHAKE, "What did you just say to me?")
                .addNPC(Balnea, HeadE.ANGRY, "We're closed until I can teach these wretched creatures some manners.")
                .addPlayer(HeadE.SKEPTICAL, "Ah, right. Good luck with that.")
        );
    }

    private static void afterAsAFirstResort(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(Banker, HeadE.CHILD_CALM_TALK, "Hi, human! You want to take or give your things?")
                .addPlayer(HeadE.HAPPY_TALKING, "What is this place?")
                .addNPC(Banker, HeadE.CHILD_CALM_TALK, "Dis Oo'glog bank place! We friends with Bank of RuneScape.")
                .addNPC(Banker, HeadE.CHILD_CALM_TALK, "We no thumpy-thumpy customers for their stuffses.")
                .addPlayer(HeadE.HAPPY_TALKING, "That's...good to hear.")
        );
    }
}
